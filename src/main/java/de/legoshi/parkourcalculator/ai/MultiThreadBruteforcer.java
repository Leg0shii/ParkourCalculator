package de.legoshi.parkourcalculator.ai;

import de.legoshi.parkourcalculator.Application;
import de.legoshi.parkourcalculator.gui.InputTickGUI;
import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import de.legoshi.parkourcalculator.simulation.tick.InputTick;
import de.legoshi.parkourcalculator.simulation.tick.InputTickManager;
import de.legoshi.parkourcalculator.util.Vec3;
import javafx.application.Platform;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

public class MultiThreadBruteforcer {

    private long SHOW_INTERVAL;
    private int SYNC_INTERVAL;
    private int BF_INSTANCES;
    private boolean STOP_ON_FIND = false;

    private ScheduledExecutorService scheduler;
    private ExecutorService mainService;
    private ExecutorService bfService = Executors.newCachedThreadPool();

    private final Application application;
    private final InputTickGUI inputTickGUI;
    private final InputTickManager inputTickManager;

    private final List<Bruteforcer> bruteforcers;
    private final List<BruteforceOptions> bruteforceOptions;
    private final List<InputGenerator> inputGenerators;

    private final AStarPathfinder aStarPathfinder;

    @Setter private ABlock endBlock;

    private List<Vec3> boundaries;

    private List<InputTick> currentFastestSolution = new ArrayList<>();
    private final ConcurrentHashMap<Vec3, List<InputTick>> ticksMap = new ConcurrentHashMap<>();

    public MultiThreadBruteforcer(Application application, List<Vec3> boundaries) {
        this.application = application;
        this.inputTickGUI = application.inputTickGUI;
        this.inputTickManager = application.inputTickManager;

        this.bruteforcers = new ArrayList<>();
        this.bruteforceOptions = new ArrayList<>();
        this.inputGenerators = new ArrayList<>();
        this.boundaries = boundaries;

        this.aStarPathfinder = new AStarPathfinder(application.currentParkour);
        this.aStarPathfinder.setColorize(true);
    }

    public void addBruteforceOptions(BruteforceOptions bruteforceOptions) {
        this.bruteforceOptions.clear();
        this.bruteforceOptions.add(bruteforceOptions);
        this.SHOW_INTERVAL = bruteforceOptions.getIntervallOfLastShown();
        this.BF_INSTANCES = bruteforceOptions.getBruteforceInstances();
        this.SYNC_INTERVAL = bruteforceOptions.getSyncInterval();
        this.STOP_ON_FIND = bruteforceOptions.isStopOnFind();
    }

    public void addInputGenerator(InputGenerator inputGenerator) {
        this.inputGenerators.clear();
        this.inputGenerators.add(inputGenerator);
    }

    public void buildBruteforcer() {
        for (int i = 0; i < BF_INSTANCES; i++) {
            Bruteforcer bruteforcer = new Bruteforcer(application, this, endBlock, i);
            bruteforcer.addBruteforceSettings(bruteforceOptions.get(0));
            bruteforcer.addInputGenerator(inputGenerators.get(0));
            bruteforcers.add(bruteforcer);
        }
    }

    public void calculateBoundaries(Vec3 start, Vec3 end) {
        this.boundaries.addAll(aStarPathfinder.calculateBoundaries(start, end));
    }

    public void start() {
        clearAll();

        mainService = Executors.newSingleThreadExecutor();
        scheduler = Executors.newSingleThreadScheduledExecutor();
        bfService = Executors.newCachedThreadPool();

        mainService.submit(() -> {
            buildBruteforcer();

            for (Bruteforcer bruteforcer : bruteforcers) {
                bruteforcer.setBoundaries(boundaries);
                bfService.submit(bruteforcer);
            }

            scheduler.scheduleAtFixedRate(() -> {
                if ((STOP_ON_FIND && !currentFastestSolution.isEmpty()) || isAllDone()) {
                    Platform.runLater(this::endRun);
                    return;
                }

                syncAllToMain();
            }, SYNC_INTERVAL, SYNC_INTERVAL, TimeUnit.SECONDS);

            scheduler.scheduleAtFixedRate(this::showUpdate, SHOW_INTERVAL, SHOW_INTERVAL, TimeUnit.MILLISECONDS);
        });
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

        this.bruteforcers.clear();
        this.ticksMap.clear();
        this.currentFastestSolution.clear();
        Platform.runLater(this.inputTickGUI::resetTicks);
    }

    private void showUpdate() {
        if (mainService.isShutdown()) return;
        if (scheduler.isShutdown()) return;

        Random random = new Random();
        List<Map.Entry<Vec3, List<InputTick>>> entries = new ArrayList<>(ticksMap.entrySet());
        if (entries.isEmpty()) entries = new ArrayList<>(bruteforcers.get((int) (Math.random()*bruteforcers.size())).getTicksMap().entrySet());
        if (!entries.isEmpty()) {
            Map.Entry<Vec3, List<InputTick>> randomEntry = entries.get(random.nextInt(entries.size()));
            List<InputTick> randomValue = randomEntry.getValue();
            Platform.runLater(() -> {
                inputTickGUI.resetTicks();
                inputTickManager.setInputTicks(randomValue);
            });
        }
    }

    private void showResult() {
        Platform.runLater(() -> {
            inputTickGUI.resetTicks();
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

    public void cancelBruteforce() {
        for (Bruteforcer bruteforcer : bruteforcers) {
            bruteforcer.cancelBruteforce();
        }
        endRun();
    }

    private void endRun() {
        syncAllToMain();

        bfService.shutdown();
        scheduler.shutdownNow();
        mainService.shutdownNow();

        System.out.println("Setting current fastest solution: " + currentFastestSolution.size());
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

    public void mergeFastestSolution(List<InputTick> inputTicks) {
        currentFastestSolution.clear();
        currentFastestSolution.addAll(inputTicks);
        bruteforcers.forEach(bf -> bf.setCurrentFastestSolution(inputTicks));
    }
}
