package de.legoshi.parkourcalculator.ai;

import de.legoshi.parkourcalculator.simulation.Parkour;
import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import de.legoshi.parkourcalculator.simulation.movement.Movement;
import de.legoshi.parkourcalculator.simulation.tick.InputTick;
import de.legoshi.parkourcalculator.simulation.tick.InputTickManager;
import de.legoshi.parkourcalculator.simulation.tick.PlayerTickInformation;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.PositionVisualizer;
import de.legoshi.parkourcalculator.util.Vec3;

import java.util.*;

public class Bruteforcer {

    private final PositionVisualizer positionVisualizer;
    private final InputTickManager inputTickManager;
    
    private final int NUM_TRIALS = 200;
    private final int TICKS_PER_TRIAL = 25;
    private final int REPETITIONS = 2000000;
    private final double DIMENSION = 0.5;
    private final boolean stopOnFind = true;
    
    private final HashMap<Vec3, List<InputTick>> ticksMap = new HashMap<>();

    private final Parkour parkour;
    private ABlock endBlock;

    public Bruteforcer(PositionVisualizer positionVisualizer, InputTickManager inputTickManager, Parkour parkour) {
        this.positionVisualizer = positionVisualizer;
        this.inputTickManager = inputTickManager;
        this.parkour = parkour;
    }

    public void applyAndBruteforce(ABlock e) {
        this.endBlock = e;
        this.ticksMap.clear();
        this.findPath();
    }
    
    private void findPath() {
        Random random = new Random();
        long startTime = System.currentTimeMillis();
        
        outer: for (int k = 0; k < REPETITIONS; k++) {
            Map.Entry<Vec3, List<InputTick>> nthEntry = getNthElement(ticksMap, (int) (Math.random() * ticksMap.size()));
            for (int i = 0; i < NUM_TRIALS; i++) {
                List<InputTick> inputTicks = new ArrayList<>();
                if (nthEntry != null) {
                    inputTicks = new ArrayList<>(nthEntry.getValue());
                }
                
                for (int j = 0; j < TICKS_PER_TRIAL; j++) {
                    boolean facingChange = random.nextDouble() > 0.9;
                    float facing = (float) ((random.nextDouble() * 360) % 90);
                    if (inputTicks.size() > 0 && facingChange) {
                        facing = inputTicks.get(inputTicks.size() - 1).YAW;
                    }
                    
                    InputTick tick = new InputTick(
                        random.nextDouble() >= 0.2,
                        random.nextDouble() >= 0.5,
                        false,
                        random.nextDouble() >= 0.5,
                        random.nextDouble() > 0.5,
                        random.nextDouble() > 0.2,
                        false,
                        facing
                    );
                    inputTicks.add(tick);
                }
                saveInputs(inputTicks);
                
                if (stopOnFind) {
                    List<InputTick> solution = findFastestLanding();
                    if (!solution.isEmpty()) {
                        break outer;
                    }
                }
                
            }
        }
    
        List<InputTick> foundInputs = findFastestLanding();
        if (foundInputs.isEmpty()) {
            System.out.println("NO PATH FOUND.");
            return;
        }
    
        System.out.println(System.currentTimeMillis() - startTime);
        inputTickManager.setInputTicks(foundInputs);
        positionVisualizer.generatePlayerPath();
    }
    
    private void saveInputs(List<InputTick> inputTicks) {
        List<PlayerTickInformation> playerInfo = parkour.getMovement().updatePath(inputTicks);
        int count = 0;
        
        for (PlayerTickInformation information : playerInfo) {
            Vec3 unRoundedVec = information.getPosition().copy();
            Vec3 roundedVec = getRoundedVec(unRoundedVec);
            if (unRoundedVec.y <= 0) continue;
            
            if (!ticksMap.containsKey(roundedVec)) {
                List<InputTick> temp = new ArrayList<>(inputTicks);
                ticksMap.put(roundedVec, temp.subList(0, count));
            } else {
                List<InputTick> savedInputs = ticksMap.get(roundedVec);
                if (savedInputs.size() >= count) {
                    ticksMap.put(roundedVec, inputTicks.subList(0, count));
                }
            }
            count++;
        }
    }
    
    private List<InputTick> findFastestLanding() {
        AxisAlignedBB endBlockAABB = endBlock.getAxisVecTuples().get(0).getBb()
            .expand(1, 0, 1)
            .offset(0, 1, 0);
        
        List<List<InputTick>> lists = getListsInRange(endBlockAABB);
        List<InputTick> shortest = new ArrayList<>();
        
        int listSize = Integer.MAX_VALUE;
        System.out.println("NEU: ");
        for (List<InputTick> list : lists) {
            System.out.println(list.size());
            parkour.getMovement().updatePath(list);
            PlayerTickInformation pti = parkour.getMovement().getLandTick(endBlock);
            if (pti == null) continue;
    
            System.out.println(pti.getPosition());
            
            if (listSize >= list.size()) {
                listSize = list.size();
                shortest = new ArrayList<>(list);
            }
        }
        
        return shortest;
    }
    
    public Vec3 getRoundedVec(Vec3 unRoundedVec) {
        double roundedX = Math.floor(unRoundedVec.x / DIMENSION) * DIMENSION;
        double roundedY = Math.floor(unRoundedVec.y / DIMENSION) * DIMENSION;
        double roundedZ = Math.floor(unRoundedVec.z / DIMENSION) * DIMENSION;
        
        return new Vec3(roundedX, roundedY, roundedZ);
    }
    
    public List<List<InputTick>> getListsInRange(AxisAlignedBB boundingBox) {
        List<List<InputTick>> result = new ArrayList<>();
        
        for (Map.Entry<Vec3, List<InputTick>> entry : ticksMap.entrySet()) {
            if (boundingBox.isVecInside(entry.getKey())) {
                result.add(entry.getValue());
            }
        }
        
        return result;
    }
    
    public static <K, V> Map.Entry<K, V> getNthElement(HashMap<K, V> map, int n) {
        int i = 0;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (i == n) {
                return entry;
            }
            i++;
        }
        return null;
    }

}
