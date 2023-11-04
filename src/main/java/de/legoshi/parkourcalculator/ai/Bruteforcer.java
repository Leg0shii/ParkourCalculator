package de.legoshi.parkourcalculator.ai;

import de.legoshi.parkourcalculator.Application;
import de.legoshi.parkourcalculator.simulation.Parkour;
import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import de.legoshi.parkourcalculator.simulation.environment.block.Air;
import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager;
import de.legoshi.parkourcalculator.simulation.movement.Movement;
import de.legoshi.parkourcalculator.simulation.tick.InputTick;
import de.legoshi.parkourcalculator.simulation.tick.PlayerTickInformation;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Bruteforcer implements Runnable {

    private int NUM_TRIALS = 100;
    private int TICKS_PER_TRIAL = 15;
    private int REPETITIONS = 20000;
    private int REC_TICKS = 100;
    private double DIMENSION = 0.5;
    private boolean STOP_ON_FIND = true;

    @Getter
    private HashMap<Vec3, List<InputTick>> ticksMap = new HashMap<>();
    @Getter
    private List<InputTick> currentFastestSolution = new ArrayList<>();
    @Setter private List<Vec3> boundaries;

    private InputGenerator inputGenerator;
    @Getter private boolean isActive;

    private final BlockManager blockManager;
    private final Movement movement;
    private final ABlock endBlock;

    public Bruteforcer(Application application, ABlock endBlock) {
        this.movement = application.currentParkour.getMovement().clone();
        this.blockManager = application.currentParkour.getBlockManager();
        this.endBlock = endBlock;
    }

    public void addBruteforceSettings(BruteforceOptions bruteforceSettings) {
        this.NUM_TRIALS = bruteforceSettings.getNumberOfTrials();
        this.TICKS_PER_TRIAL = bruteforceSettings.getTicksPerTrial();
        this.REPETITIONS = bruteforceSettings.getRepetitions();
        this.DIMENSION = bruteforceSettings.getDimension();
        this.STOP_ON_FIND = bruteforceSettings.isStopOnFind();
        this.REC_TICKS = bruteforceSettings.getRecTicks();
    }

    public void addInputGenerator(InputGenerator inputGenerator) {
        this.inputGenerator = inputGenerator;
    }

    @Override
    public void run() {
        this.clearBruteforce();
        this.findPath();
    }

    public void clearBruteforce() {
        this.ticksMap.clear();
        this.currentFastestSolution.clear();
    }

    public void syncMap(ConcurrentHashMap<Vec3, List<InputTick>> map) {
        this.ticksMap = new HashMap<>(map);
    }

    private void findPath() {
        isActive = true;

        outer: for (int k = 0; k < REPETITIONS; k++) {
            List<InputTick> nthEntry = getNthElement(ticksMap, (int) (Math.random() * ticksMap.size()));
            for (int i = 0; i < NUM_TRIALS; i++) {
                if (!isActive) {
                    break outer;
                }

                List<InputTick> inputTicks = new ArrayList<>();
                int startIndex = 0;
                if (nthEntry != null) {
                    inputTicks = new ArrayList<>(nthEntry);
                    startIndex = inputTicks.size();
                }

                for (int j = 0; j < TICKS_PER_TRIAL; j++) {
                    inputTicks.add(inputGenerator.getNextTick());
                }

                saveInputs(boundaries, inputTicks, startIndex);
                if (STOP_ON_FIND && !currentFastestSolution.isEmpty()) {
                    break outer;
                }
            }
        }

        isActive = false;
    }

    private void saveInputs(List<Vec3> boundaries, List<InputTick> inputTicks, int startIndex) {
        List<PlayerTickInformation> playerInfo = movement.updatePath(inputTicks);
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
                ticksMap.put(roundedVec, temp.subList(0, count));
            } else {
                List<InputTick> savedInputs = ticksMap.get(roundedVec);
                if (savedInputs.size() > count) {
                    ticksMap.put(roundedVec, inputTicks.subList(0, count));
                } else {
                    count++;
                    continue;
                }
            }

            Vec3 possibleLBPos = roundedVec.copy();
            possibleLBPos.y--;
            ABlock possibleLB = blockManager.getBlock(possibleLBPos);

            if (!(possibleLB instanceof Air) &&
                    possibleLB.getVec3().equals(endBlock.getVec3()) &&
                    (currentFastestSolution.isEmpty() || playerInfo.size() < currentFastestSolution.size())
            ) {
                currentFastestSolution = new ArrayList<>(inputTicks);
            }

            count++;
        }
    }

    public Vec3 getRoundedVec(Vec3 unRoundedVec) {
        double roundedX = Math.floor(unRoundedVec.x / DIMENSION) * DIMENSION;
        double roundedY = Math.floor(unRoundedVec.y / DIMENSION) * DIMENSION;
        double roundedZ = Math.floor(unRoundedVec.z / DIMENSION) * DIMENSION;

        return new Vec3(roundedX, roundedY, roundedZ);
    }

    public void cancelBruteforce() {
        if (isActive) {
            isActive = false;
        }
    }

    public boolean isInsideBoundaries(List<Vec3> boundaries, Vec3 vec) {
        for (Vec3 boundary : boundaries) {
            if (vec.x >= boundary.x && vec.x < boundary.x + 1 &&
                    vec.y >= boundary.y && vec.y < boundary.y + 3 &&
                    vec.z >= boundary.z && vec.z < boundary.z + 1) {
                return true;
            }
        }
        return false;
    }

    public List<InputTick> getNthElement(HashMap<Vec3, List<InputTick>> map, int n) {
        int i = 0;
        if (!currentFastestSolution.isEmpty() && Math.random() > 0.8) {
            int subListStart = (int) (Math.random() * currentFastestSolution.size());
            return currentFastestSolution.subList(0, subListStart);
        }

        int highestLength = 0;

        // Determine the highest length of List<InputTick> in the map
        for (List<InputTick> value : map.values()) {
            if (value.size() > highestLength) {
                highestLength = value.size();
            }
        }

        int threshold = highestLength - Math.min(highestLength, REC_TICKS);

        // Filter the entries that have a List<InputTick> length within the desired range
        List<List<InputTick>> filteredValues = new ArrayList<>();
        for (List<InputTick> value : map.values()) {
            if (value.size() >= threshold && value.size() <= highestLength) {
                filteredValues.add(value);
            }
        }

        // Randomly select one of the filtered entries
        if (filteredValues.isEmpty()) {
            return null; // No suitable entry found
        }

        Random random = new Random();
        return filteredValues.get(random.nextInt(filteredValues.size()));
    }

}
