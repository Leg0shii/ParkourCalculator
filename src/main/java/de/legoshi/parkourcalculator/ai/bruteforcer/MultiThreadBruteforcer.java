package de.legoshi.parkourcalculator.ai.bruteforcer;

import de.legoshi.parkourcalculator.Application;
import de.legoshi.parkourcalculator.gui.debug.CoordinateScreen;
import de.legoshi.parkourcalculator.simulation.Parkour;
import de.legoshi.parkourcalculator.ai.AStarPathfinder;
import de.legoshi.parkourcalculator.ai.BruteforceOptions;
import de.legoshi.parkourcalculator.ai.InputGenerator;
import de.legoshi.parkourcalculator.gui.InputTickGUI;
import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import de.legoshi.parkourcalculator.simulation.tick.InputTick;
import de.legoshi.parkourcalculator.simulation.tick.InputTickManager;
import de.legoshi.parkourcalculator.util.Vec3;
import javafx.application.Platform;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;

public class MultiThreadBruteforcer {

    private static final Logger logger = LogManager.getLogger(MultiThreadBruteforcer.class.getName());
    private long SHOW_INTERVAL;
    private int SYNC_INTERVAL;
    private long TICK_INTERVAL;
    private int BF_INSTANCES;
    private boolean STOP_ON_FIND = false;

    private ScheduledExecutorService scheduler;
    private ExecutorService mainService;
    private ExecutorService bfService = Executors.newCachedThreadPool();

    private final Parkour parkour;
    private final InputTickGUI inputTickGUI;
    private final InputTickManager inputTickManager;

    private final List<Bruteforcer> bruteforcers;
    private final List<BruteforceOptions> bruteforceOptions;
    private final List<InputGenerator> inputGenerators;

    @Getter private final AStarPathfinder aStarPathfinder;

    @Setter private ABlock endBlock;

    private final List<Vec3> boundaries;
    private int lowestBound;
    private int highestBound;
    private int iterationCount;

    private List<InputTick> currentFastestSolution = new ArrayList<>();
    private final ConcurrentHashMap<Vec3, List<InputTick>> ticksMap = new ConcurrentHashMap<>();

    public MultiThreadBruteforcer(Application application, List<Vec3> boundaries) {
        this.parkour = application.getParkour();
        this.inputTickGUI = application.inputTickGUI;
        this.inputTickManager = application.inputTickManager;

        this.bruteforcers = new ArrayList<>();
        this.bruteforceOptions = new ArrayList<>();
        this.inputGenerators = new ArrayList<>();
        this.boundaries = boundaries;

        this.aStarPathfinder = new AStarPathfinder(application.getParkour(), application.getMinecraftGUI());
        this.aStarPathfinder.setColorize(true);
    }

    public void addBruteforceOptions(BruteforceOptions bruteforceOptions) {
        this.bruteforceOptions.clear();
        this.bruteforceOptions.add(bruteforceOptions);
        this.SHOW_INTERVAL = bruteforceOptions.getIntervallOfLastShown();
        this.BF_INSTANCES = bruteforceOptions.getBruteforceInstances();
        this.SYNC_INTERVAL = bruteforceOptions.getSyncInterval();
        this.STOP_ON_FIND = bruteforceOptions.isStopOnFind();
        this.TICK_INTERVAL = bruteforceOptions.getIntervalDuration();
    }

    public void addInputGenerator(InputGenerator inputGenerator) {
        this.inputGenerators.clear();
        this.inputGenerators.add(inputGenerator);
    }

    public void calculateBoundaries(Vec3 start, Vec3 end) {
        this.boundaries.addAll(aStarPathfinder.calculateBoundaries(start, end));
    }

    public void cancelBruteforce() {
        bruteforcers.forEach(Bruteforcer::cancelBruteforce);
        endRun();
    }

    public void clearAll() {
        if (mainService != null && !mainService.isShutdown()) {
            mainService.shutdownNow();
        }
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
        if (bfService != null && !bfService.isShutdown()) {
            bfService.shutdownNow();
        }

        this.lowestBound = 0;
        this.highestBound = 0;
        this.iterationCount = 0;
        this.bruteforcers.clear();
        this.ticksMap.clear();
        this.currentFastestSolution.clear();
        Platform.runLater(this.inputTickGUI::resetTicks);
    }

    public void start() {
        clearAll();

        mainService = Executors.newSingleThreadExecutor();
        scheduler = Executors.newSingleThreadScheduledExecutor();
        bfService = Executors.newCachedThreadPool();

        mainService.submit(() -> {
            try {
                buildBruteforcer();
            } catch (Exception e) {
                logger.error("An error occurred while building the bruteforcers: " + e.getMessage(), e);
                e.printStackTrace();
            }

            for (Bruteforcer bruteforcer : bruteforcers) {
                bruteforcer.setBoundaries(boundaries);
                bfService.submit(bruteforcer);
            }

            scheduler.scheduleAtFixedRate(() -> {
                if ((STOP_ON_FIND && !currentFastestSolution.isEmpty()) || isAllDone()) {
                    Platform.runLater(this::endRun);
                }
            }, 1, 1, TimeUnit.SECONDS);

            scheduler.scheduleAtFixedRate(() -> {
                this.syncAllToMain();
                if (setIterationCount()) {
                    bruteforcers.forEach(bf -> {
                        bf.setIterationCount(iterationCount);
                        bf.setLowestBound(lowestBound);
                        bf.setHighestBound(highestBound);
                    });
                }
            }, TICK_INTERVAL, TICK_INTERVAL, TimeUnit.SECONDS);

            scheduler.scheduleAtFixedRate(this::syncAllToMain, SYNC_INTERVAL, SYNC_INTERVAL, TimeUnit.SECONDS);
            scheduler.scheduleAtFixedRate(this::showUpdate, SHOW_INTERVAL, SHOW_INTERVAL, TimeUnit.MILLISECONDS);
        });
    }

    protected void mergeFastestSolution(List<InputTick> inputTicks) {
        currentFastestSolution.clear();
        currentFastestSolution.addAll(inputTicks);
        bruteforcers.forEach(bf -> bf.setCurrentFastestSolution(inputTicks));
    }

    private void buildBruteforcer() {
        if (bruteforceOptions.isEmpty() || inputGenerators.isEmpty()) {
            return;
        }

        for (int i = 0; i < BF_INSTANCES; i++) {
            Bruteforcer bruteforcer = new Bruteforcer(parkour, this, endBlock);
            bruteforcer.setBruteforceOptions(bruteforceOptions.get(0));
            bruteforcer.setInputGenerator(inputGenerators.get(0));
            bruteforcers.add(bruteforcer);
        }
    }

    private void showUpdate() {
        if (mainService.isShutdown()) return;
        if (scheduler.isShutdown()) return;

        Random random = new Random();
        List<Map.Entry<Vec3, List<InputTick>>> entries = new ArrayList<>(ticksMap.entrySet());
        if (entries.isEmpty()) {
            int randIndex = (int) (Math.random() * bruteforcers.size());
            Set<Map.Entry<Vec3, List<InputTick>>> randomValue = bruteforcers.get(randIndex).getTicksMap().entrySet();
            entries = new ArrayList<>(randomValue);
        }
        if (!entries.isEmpty()) {
            Map.Entry<Vec3, List<InputTick>> randomEntry = entries.get(random.nextInt(entries.size()));
            List<InputTick> randomValue = randomEntry.getValue();
            Platform.runLater(() -> {
                inputTickGUI.clearAllTicks();
                inputTickManager.setInputTicks(randomValue);
            });
        }
    }

    private void showResult() {
        logger.debug("Setting current fastest solution: " + currentFastestSolution.size());
        Platform.runLater(() -> {
            inputTickGUI.clearAllTicks();
            inputTickManager.setInputTicks(currentFastestSolution);
            inputTickGUI.importTicks(currentFastestSolution);
        });
    }

    private void syncAllToMain() {
        for (Bruteforcer bruteforcer : bruteforcers) {
            for (Vec3 vec3 : bruteforcer.getTicksMap().keySet()) {
                if (!ticksMap.containsKey(vec3) || (ticksMap.get(vec3).size() > bruteforcer.getTicksMap().get(vec3).size())) {
                    ticksMap.put(vec3, bruteforcer.getTicksMap().get(vec3));
                }
            }

            if (bruteforcer.getCurrentFastestSolution().isEmpty()) continue;
            if (currentFastestSolution.isEmpty()) {
                currentFastestSolution = new ArrayList<>(bruteforcer.getCurrentFastestSolution());
            } else if (bruteforcer.getCurrentFastestSolution().size() < currentFastestSolution.size()) {
                currentFastestSolution = new ArrayList<>(bruteforcer.getCurrentFastestSolution());
            }
        }
        bruteforcers.forEach(bf -> bf.syncMap(ticksMap));
    }

    private boolean setIterationCount() {
        int prevValue = iterationCount;
        iterationCount++;

        int highestValue = 0;
        for (List<InputTick> value : ticksMap.values()) {
            if (value.size() > highestValue) {
                highestValue = value.size();
            }
        }
        if (highestValue <= lowestBound && iterationCount > 0) {
            iterationCount--;
        }

        calculateTickBounds();
        return iterationCount != prevValue;
    }

    private void calculateTickBounds() {
        BruteforceOptions bruteforceOptions = this.bruteforceOptions.get(0);
        if (bruteforceOptions.isWindowed()) {
            lowestBound = iterationCount * bruteforceOptions.getGenerateInterval();
            highestBound = lowestBound + bruteforceOptions.getGenerateInterval();
            if (iterationCount > 0) lowestBound = lowestBound - bruteforceOptions.getOverlap();

            if (!currentFastestSolution.isEmpty()) {
                lowestBound = Math.max(0, currentFastestSolution.size() - bruteforceOptions.getGenerateInterval());
                highestBound = currentFastestSolution.size();
            }
        } else {
            highestBound = 0;
            for (List<InputTick> value : ticksMap.values()) {
                if (value.size() > highestBound) {
                    highestBound = value.size();
                }
            }
            lowestBound = highestBound - Math.min(highestBound, bruteforceOptions.getRecTicks());
        }
    }

    private void endRun() {
        syncAllToMain();

        bfService.shutdown();
        scheduler.shutdownNow();
        mainService.shutdownNow();

        showResult();
    }

    private boolean isAllDone() {
        for (Bruteforcer bruteforcer : bruteforcers) {
            if (bruteforcer.isActive()) {
                return false;
            }
        }
        return true;
    }
}
