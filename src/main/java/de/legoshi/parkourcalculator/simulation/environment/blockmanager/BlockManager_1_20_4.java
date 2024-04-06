package de.legoshi.parkourcalculator.simulation.environment.blockmanager;

import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import de.legoshi.parkourcalculator.simulation.environment.block.StandardBlock;
import javafx.scene.shape.Box;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockManager_1_20_4 extends BlockManager {

    public BlockManager_1_20_4() {
        registeredBlocks.add(new StandardBlock());
    }

    @Override
    public BlockManager clone() {
        ABlock[][][] aBlocks_clone = blocks.clone();
        HashMap<Box, ABlock> boxBlocks_clone = new HashMap<>(boxBlocks);
        List<ABlock> allBlocks_clone = new ArrayList<>(allBlocks);

        BlockManager_1_20_4 blockManager_1_20_4 = (BlockManager_1_20_4) super.clone();
        blockManager_1_20_4.blocks = aBlocks_clone;
        blockManager_1_20_4.boxBlocks = boxBlocks_clone;
        blockManager_1_20_4.allBlocks = allBlocks_clone;
        return blockManager_1_20_4;
    }

}
