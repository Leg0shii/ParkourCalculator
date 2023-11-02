package de.legoshi.parkourcalculator.ai;

import de.legoshi.parkourcalculator.Application;
import de.legoshi.parkourcalculator.gui.InputTickGUI;
import de.legoshi.parkourcalculator.simulation.Parkour;
import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import de.legoshi.parkourcalculator.simulation.environment.block.Air;
import de.legoshi.parkourcalculator.simulation.tick.InputTick;
import de.legoshi.parkourcalculator.simulation.tick.InputTickManager;
import de.legoshi.parkourcalculator.simulation.tick.PlayerTickInformation;
import de.legoshi.parkourcalculator.util.PositionVisualizer;
import de.legoshi.parkourcalculator.util.Vec3;
import javafx.application.Platform;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Bruteforcer {
    
    private final PositionVisualizer positionVisualizer;
    private final InputTickManager inputTickManager;
    private final InputTickGUI inputTickGUI;
    private final AStarPathfinder aStarPathfinder;
    
    private int NUM_TRIALS = 100;
    private int TICKS_PER_TRIAL = 15;
    private int REPETITIONS = 20000;
    private double DIMENSION = 0.5;
    private boolean STOP_ON_FIND = true;
    private long SHOW_INTERVAL = 100;
    
    private final HashMap<Vec3, List<InputTick>> ticksMap = new HashMap<>();
    private List<InputTick> currentFastestSolution = new ArrayList<>();
    
    private InputGenerator inputGenerator;
    private long lastShown = 0;
    private boolean isActive;
    
    private final Parkour parkour;
    private Parkour parkourClone;
    
    private ABlock startBlock;
    private ABlock endBlock;
    
    public Bruteforcer(Application application) {
        this.positionVisualizer = application.positionVisualizer;
        this.inputTickManager = application.inputTickManager;
        this.inputTickGUI = application.inputTickGUI;
        this.parkour = application.currentParkour;
        
        this.aStarPathfinder = new AStarPathfinder(application.currentParkour);
        this.aStarPathfinder.setColorize(true);
        
        this.inputGenerator = new InputGenerator();
    }
    
    public void applyConfigValues(int numberOfTrials, int ticksPerTrial, int repetitions, double dimension, boolean stopOnFind, int intervallOfLastShown) {
        this.NUM_TRIALS = numberOfTrials;
        this.TICKS_PER_TRIAL = ticksPerTrial;
        this.REPETITIONS = repetitions;
        this.DIMENSION = dimension;
        this.STOP_ON_FIND = stopOnFind;
        this.SHOW_INTERVAL = intervallOfLastShown;
    }
    
    public void applyWASDConfig(double wProb, double aProb, double sProb, double dProb, double jumpProb, double sprintProb, double sneakProb, double fChangeProb) {
        this.inputGenerator.apply(wProb, aProb, sProb, dProb, jumpProb, sprintProb, sneakProb, fChangeProb);
    }
    
    public void bruteforce(ABlock e) {
        this.parkourClone = parkour.clone();
        this.clearBruteforce();
        
        Vec3 playerStart = parkourClone.getPlayer().startPos.copy();
        playerStart.subtract(0, 1, 0);
        
        this.startBlock = parkourClone.getBlockManager().getBlock(playerStart);
        this.endBlock = e;
        
        Vec3 startPos = this.startBlock.getVec3();
        Vec3 endPos = this.endBlock.getVec3();
        
        List<ABlock> boundaryBlock = aStarPathfinder.findShortestPath(startPos, endPos);
        List<Vec3> boundaries = getBoundaries(boundaryBlock);
        this.findPath(boundaries);
    }
    
    private void clearBruteforce() {
        this.ticksMap.clear();
        this.currentFastestSolution.clear();
        this.inputTickGUI.resetTicks();
        this.parkour.getBlockManager().allBlocks.forEach(ABlock::resetAndApplyMaterialColor);
    }
    
    private void findPath(List<Vec3> boundaries) {
        long startTime = System.currentTimeMillis();
        isActive = true;
        
        CompletableFuture.supplyAsync(() -> {
            for (int k = 0; k < REPETITIONS; k++) {
                List<InputTick> nthEntry = getNthElement(ticksMap, (int) (Math.random() * ticksMap.size()));
                for (int i = 0; i < NUM_TRIALS; i++) {
                    if (!isActive) {
                        return null;
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
                    showResults(inputTicks);
                    
                    if (STOP_ON_FIND && !currentFastestSolution.isEmpty()) {
                        return null;
                    }
                }
            }
            return null;
        }).thenRun(() -> {
            Platform.runLater(() -> {
                this.inputTickGUI.resetTicks();
                isActive = false;
                
                if (currentFastestSolution.isEmpty()) {
                    System.out.println("NO PATH FOUND.");
                    return;
                }
                
                System.out.println(System.currentTimeMillis() - startTime);
                inputTickManager.setInputTicks(currentFastestSolution);
                positionVisualizer.generatePlayerPath();
                inputTickGUI.importTicks(currentFastestSolution);
            });
        });
    }
    
    private void saveInputs(List<Vec3> boundaries, List<InputTick> inputTicks, int startIndex) {
        List<PlayerTickInformation> playerInfo = parkourClone.getMovement().updatePath(inputTicks);
        int count = startIndex == 0 ? 0 : startIndex-1;
        
        for (;count < playerInfo.size(); count++) {
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
            ABlock possibleLB = parkourClone.getBlockManager().getBlock(possibleLBPos);
            
            if (!(possibleLB instanceof Air) &&
                possibleLB.getVec3().equals(endBlock.getVec3()) &&
                (currentFastestSolution.isEmpty() || playerInfo.size() < currentFastestSolution.size())
            ) {
                currentFastestSolution = new ArrayList<>(inputTicks);
            }
    
            count++;
        }
    }
    
    private void showResults(List<InputTick> inputTicks) {
        if ((System.currentTimeMillis() - lastShown) > SHOW_INTERVAL) {
            lastShown = System.currentTimeMillis();
            Platform.runLater(() -> {
                this.inputTickGUI.resetTicks();
                inputTickManager.setInputTicks(new ArrayList<>(inputTicks)); // handles path generation
            });
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
    
    public List<Vec3> getBoundaries(List<ABlock> boundaries) {
        List<Vec3> result = new ArrayList<>();
        for (ABlock block : boundaries) {
            if (!(block instanceof Air)) {
                result.add(block.getVec3());
            }
        }
        return result;
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
    
        int threshold = highestLength - Math.min(highestLength, 250);
    
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
