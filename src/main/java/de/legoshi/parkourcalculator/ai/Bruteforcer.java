package de.legoshi.parkourcalculator.ai;

import de.legoshi.parkourcalculator.Application;
import de.legoshi.parkourcalculator.gui.InputTickGUI;
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
    private final InputTickGUI inputTickGUI;
    
    private final int NUM_TRIALS = 200;
    private final int TICKS_PER_TRIAL = 25;
    private final int REPETITIONS = 2000000;
    private final double DIMENSION = 0.5;
    private final boolean stopOnFind = true;
    
    private final HashMap<Vec3, List<InputTick>> ticksMap = new HashMap<>();

    private final Parkour parkour;
    private ABlock endBlock;

    public Bruteforcer(Application application) {
        this.positionVisualizer = application.positionVisualizer;
        this.inputTickManager = application.inputTickManager;
        this.inputTickGUI = application.inputTickGUI;
        this.parkour = application.currentParkour;
    }

    public void applyAndBruteforce(ABlock e) {
        this.endBlock = e;
        this.clearBruteforce();
        this.findPath();
    }

    private void clearBruteforce() {
        this.ticksMap.clear();
        this.inputTickGUI.resetTicks();
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
                    boolean facingChange = random.nextDouble() > 0.1;
                    float facing = 90 * ((int) (Math.random() * 4));
                    if (inputTicks.size() > 0 && facingChange) {
                        facing = 0;
                    } else if (!facingChange) {
                        facing = (float) (Math.random() * 360);
                    }

                    InputTick tick = new InputTick(
                        random.nextDouble() >= 0.05,
                        random.nextDouble() >= 0.9,
                        false,
                        random.nextDouble() >= 0.9,
                        random.nextDouble() > 0.9,
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
        inputTickGUI.importTicks(foundInputs);
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
            .offset(0, 0.5, 0);
        
        List<List<InputTick>> lists = getListsInRange(endBlockAABB);
        List<InputTick> shortest = new ArrayList<>();
        
        int listSize = Integer.MAX_VALUE;
        for (List<InputTick> list : lists) {
            PlayerTickInformation pti = parkour.getMovement().getLandOnBlock(list, endBlock);
            if (pti == null) continue;
            
            if (listSize >= list.size()) {
                listSize = list.size();
                shortest = new ArrayList<>(list);
            }

            System.out.println("SIZE: " + list.size() + " On Ground: " + pti.isGround());
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
        
        for (Vec3 key : ticksMap.keySet()) {
            if (boundingBox.isVecInside(key)) {
                result.add(ticksMap.get(key));
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
