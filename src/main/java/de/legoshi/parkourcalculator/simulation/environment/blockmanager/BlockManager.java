package de.legoshi.parkourcalculator.simulation.environment.blockmanager;

import de.legoshi.parkourcalculator.simulation.environment.block.*;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.AxisVecTuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public abstract class BlockManager implements Observer {

    public ABlock currentBlock = new StandardBlock();
    public List<ABlock> registeredBlocks = new ArrayList<>();
    public List<ABlock> aBlocks = new ArrayList<>();

    public ABlock getBlock(double x, double y, double z) {
        for (ABlock aBlock : aBlocks) {
            if (aBlock.getVec3().x == x && aBlock.getVec3().y == y && aBlock.getVec3().z == z) {
                return aBlock;
            }
        }
        return new Air();
    }

    public List<AxisAlignedBB> getAllBlockHitboxes() {
        List<AxisAlignedBB> boundingBoxes = new ArrayList<>();
        for (ABlock aBlock : aBlocks) {
            for (AxisVecTuple axisVecTuple : aBlock.axisVecTuples) {
                if (!(aBlock instanceof BlockLiquid || aBlock instanceof Vine || aBlock instanceof Cobweb))
                    boundingBoxes.add(axisVecTuple.getBb());
            }
        }
        return boundingBoxes;
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
        aBlocks.add(block);
    }

    public void removeBlock(ABlock block) {
        aBlocks.remove(block);
    }

}
