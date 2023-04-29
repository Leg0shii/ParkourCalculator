package de.legoshi.parkourcalculator.simulation.environment.blockmanager;

import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import de.legoshi.parkourcalculator.simulation.environment.block.Air;
import de.legoshi.parkourcalculator.simulation.environment.block.StandardBlock;

import java.util.ArrayList;
import java.util.List;
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
