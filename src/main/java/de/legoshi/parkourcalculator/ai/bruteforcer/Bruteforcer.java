package de.legoshi.parkourcalculator.ai.bruteforcer;

import de.legoshi.parkourcalculator.ai.BruteforceOptions;
import de.legoshi.parkourcalculator.ai.InputGenerator;
import de.legoshi.parkourcalculator.simulation.Parkour;
import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import de.legoshi.parkourcalculator.simulation.environment.block.Air;
import de.legoshi.parkourcalculator.simulation.tick.InputTick;
import de.legoshi.parkourcalculator.simulation.tick.PlayerTickInformation;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.AxisVecTuple;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ToString
public class Bruteforcer implements Runnable {

    private static final Logger logger = LogManager.getLogger(Bruteforcer.class.getName());
    @Getter private final UUID uuid = UUID.randomUUID();

    @Getter private HashMap<Vec3, List<InputTick>> ticksMap = new HashMap<>();
    @Getter private List<InputTick> currentFastestSolution = new ArrayList<>();
    @Setter private List<Vec3> boundaries;

    @Setter private BruteforceOptions bruteforceOptions;
    @Setter private InputGenerator inputGenerator;
    @Getter private boolean isActive;
    private long startTime;

    @Getter private int tickGeneration;
    @Getter private int longestElement;
    @Getter @Setter private int iterationCount;
    @Getter @Setter private int lowestBound;
    @Getter @Setter private int highestBound;

    private final MultiThreadBruteforcer instance;
    private Parkour parkour;
    private final ABlock endBlock;

    public Bruteforcer(Parkour parkour, MultiThreadBruteforcer multiThreadBruteforcer, ABlock endBlock) {
        this.instance = multiThreadBruteforcer;
        this.parkour = parkour;
        this.endBlock = endBlock;
    }

    @Override
    public void run() {
        logger.debug("Started bruteforcer instance. {}", this);
        this.parkour = parkour.clone();
        this.clearBruteforce();
        this.findPath();
    }

    protected synchronized void syncMap(ConcurrentHashMap<Vec3, List<InputTick>> map) {
        this.ticksMap = new HashMap<>(map);
    }

    protected synchronized void syncCFS(List<InputTick> solution) {
        setCurrentFastestSolution(solution);
    }

    protected void cancelBruteforce() {
        if (isActive) {
            isActive = false;
        }
    }

    protected void setCurrentFastestSolution(List<InputTick> solution) {
        this.currentFastestSolution = new ArrayList<>(solution);
    }

    private void findPath() {
        this.isActive = true;
        try {
            outer:
            for (int k = 0; k < bruteforceOptions.getRepetitions(); k++) {
                List<InputTick> nthEntry = getNthElement();
                for (int i = 0; i < bruteforceOptions.getNumberOfTrials(); i++) {
                    if (!isActive) {
                        break outer;
                    }

                    List<InputTick> inputTicks = new ArrayList<>();
                    int startIndex = 0;
                    if (nthEntry != null) {
                        inputTicks = new ArrayList<>(nthEntry);
                        startIndex = inputTicks.size();
                    }
                    this.tickGeneration = startIndex;

                    for (int j = 0; j < bruteforceOptions.getTicksPerTrial(); j++) {
                        inputTicks.add(inputGenerator.getNextTick());
                    }

                    saveInputs(boundaries, inputTicks, startIndex);
                    if (bruteforceOptions.isStopOnFind() && !currentFastestSolution.isEmpty()) {
                        break outer;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("An error occurred in findPath: {}", e.getMessage(), e);
        } finally {
            this.isActive = false;
        }
    }

    private void clearBruteforce() {
        this.longestElement = 0;
        this.iterationCount = 0;
        this.lowestBound = 0;
        this.highestBound = bruteforceOptions.getGenerateInterval();
        this.startTime = System.currentTimeMillis();
        this.ticksMap.clear();
        this.currentFastestSolution.clear();
    }

    private void saveInputs(List<Vec3> boundaries, List<InputTick> inputTicks, int startIndex) {
        List<PlayerTickInformation> playerInfo = parkour.getMovement().updatePath(inputTicks);
        int count = startIndex == 0 ? 0 : startIndex - 1;

        for (; count < playerInfo.size(); count++) {
            PlayerTickInformation information = playerInfo.get(count);
            Vec3 unRoundedVec = information.getPosition().copy();
            Vec3 roundedVec = getRoundedVec(unRoundedVec);

            if (!boundaries.isEmpty()) {
                if (!isInsideBoundaries(boundaries, unRoundedVec)) {
                    continue;
                }
            }

            if (!ticksMap.containsKey(roundedVec)) {
                List<InputTick> temp = new ArrayList<>(inputTicks);
                ticksMap.put(roundedVec, temp.subList(0, count == 0 ? 0 : count - 1));
                if (count > longestElement) {
                    longestElement = count;
                }
            } else {
                List<InputTick> savedInputs = ticksMap.get(roundedVec);
                if (savedInputs.size() > count) {
                    ticksMap.put(roundedVec, inputTicks.subList(0, count == 0 ? 0 : count - 1));
                } else {
                    count++;
                    continue;
                }
            }

            List<Vec3> endBlockBounds = new ArrayList<>();
            endBlockBounds.add(endBlock.getVec3());

            if (isInsideBoundaries(endBlockBounds, unRoundedVec)) {
                List<InputTick> shrinkList = new ArrayList<>(inputTicks.subList(0, count));
                if ((currentFastestSolution.isEmpty() || shrinkList.size() < currentFastestSolution.size())) {
                    // instance.mergeFastestSolution(shrinkList);
                    this.currentFastestSolution = new ArrayList<>(shrinkList);
                    logger.debug("New Solution with {} ticks. Found in: {}ms", currentFastestSolution.size(), System.currentTimeMillis() - startTime);
                }
            }

            count++;
        }
    }

    private Vec3 getRoundedVec(Vec3 unRoundedVec) {
        double DIMENSION = bruteforceOptions.getDimension();
        double roundedX = Math.floor(unRoundedVec.x / DIMENSION) * DIMENSION;
        double roundedY = Math.floor(unRoundedVec.y / DIMENSION) * DIMENSION;
        double roundedZ = Math.floor(unRoundedVec.z / DIMENSION) * DIMENSION;

        return new Vec3(roundedX, roundedY, roundedZ);
    }

    private boolean isInsideBoundaries(List<Vec3> boundaries, Vec3 vec) {
        for (Vec3 boundary : boundaries) {
            ABlock landBlock = parkour.getBlockManager().getBlock(boundary);

            for (AxisVecTuple tuple : landBlock.getAxisVecTuples()) {
                AxisAlignedBB bbBlock = tuple.getBb();
                if (vec.x >= bbBlock.minX - 0.3 && vec.x < bbBlock.maxX + 0.3 &&
                        vec.y >= bbBlock.minY && vec.y < bbBlock.maxY + 1.5 &&
                        vec.z >= bbBlock.minZ - 0.3 && vec.z < bbBlock.maxZ + 0.3) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<InputTick> getNthElement() {
        List<InputTick> selectedValue = null;
        Random random = new Random();
        boolean isWindowed = bruteforceOptions.isWindowed();

        List<List<InputTick>> eligibleValues = new ArrayList<>();
        if (isWindowed) {
            for (List<InputTick> value : ticksMap.values()) {
                if (isWithinBounds(value.size(), lowestBound, highestBound)) {
                    eligibleValues.add(value);
                }
            }
        } else {
            int recursiveTicks = bruteforceOptions.getRecTicks();
            for (List<InputTick> value : ticksMap.values()) {
                if (!currentFastestSolution.isEmpty() && recursiveTicks >= currentFastestSolution.size() - value.size()) {
                    eligibleValues.add(value);
                } else if (recursiveTicks >= longestElement - value.size()) {
                    eligibleValues.add(value);
                }
            }
        }

        if (!eligibleValues.isEmpty()) {
            int randomIndex = random.nextInt(eligibleValues.size());
            selectedValue = eligibleValues.get(randomIndex);
        }

        return selectedValue;
    }

    private boolean isWithinBounds(int size, int lowestBound, int highestBound) {
        return size >= lowestBound && size <= highestBound;
    }

}
