package de.legoshi.parkourcalculator.simulation.environment.blockmanager;

import de.legoshi.parkourcalculator.simulation.environment.block.*;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.AxisVecTuple;
import de.legoshi.parkourcalculator.util.Vec3;
import javafx.scene.shape.Box;

import java.util.*;

public abstract class BlockManager implements Observer {

    public ABlock currentBlock = new StandardBlock();
    public List<ABlock> registeredBlocks = new ArrayList<>();
    
    // x, y, z
    public HashMap<Integer, HashMap<Integer, HashMap<Integer, ABlock>>> aBlocks = new HashMap<>();
    public HashMap<Box, ABlock> boxBlocks = new HashMap<>();
    public List<ABlock> allBlocks = new ArrayList<>();
    
    public abstract BlockManager clone();
    
    public ABlock getBlock(Vec3 vec3) {
        int x = (int) Math.floor(vec3.x);
        int y = (int) Math.floor(vec3.y);
        int z = (int) Math.floor(vec3.z);
        return getBlock(x, y, z);
    }
    
    public ABlock getBlock(int x, int y, int z) {
        if (aBlocks.containsKey(x)) {
            HashMap<Integer, HashMap<Integer, ABlock>> yMap = aBlocks.get(x);
            if (yMap.containsKey(y)) {
                HashMap<Integer, ABlock> zMap = yMap.get(y);
                if (zMap.containsKey(z)) {
                    return zMap.get(z);
                }
            }
        }
        return new Air();
    }
    
    public List<ABlock> getAllBlocks() {
        return allBlocks;
    }
    
    public ABlock getBlockFromBox(Box box) {
        return boxBlocks.get(box);
    }

    @Override
    public void update(Observable o, Object arg) {
        List<Object> objects = (List<Object>) arg;
        if (objects.get(0).equals("add")) addBlock((ABlock) objects.get(1));
        else removeBlock((ABlock) objects.get(1));
    }

    public void updateCurrentBlock(ABlock aBlock) {
        currentBlock = aBlock;
    }

    public void addBlock(ABlock block) {
        int x = (int) block.getVec3().x;
        int y = (int) block.getVec3().y;
        int z = (int) block.getVec3().z;
        
        aBlocks
            .computeIfAbsent(x, k -> new HashMap<>())
            .computeIfAbsent(y, k -> new HashMap<>())
            .put(z, block);
        
        block.getBoxesArrayList().forEach(box -> boxBlocks.put(box, block));
        allBlocks.add(block);
    }
    
    public void removeBlock(ABlock block) {
        int x = (int) block.getVec3().x;
        int y = (int) block.getVec3().y;
        int z = (int) block.getVec3().z;
        
        if (aBlocks.containsKey(x) && aBlocks.get(x).containsKey(y)) {
            aBlocks.get(x).get(y).remove(z);
            if (aBlocks.get(x).get(y).isEmpty()) {
                aBlocks.get(x).remove(y);
                if (aBlocks.get(x).isEmpty()) {
                    aBlocks.remove(x);
                }
            }
        }
    
        block.getBoxesArrayList().forEach(box -> boxBlocks.remove(box));
        allBlocks.remove(block);
    }

}
