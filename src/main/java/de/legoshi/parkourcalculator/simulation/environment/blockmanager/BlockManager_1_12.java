package de.legoshi.parkourcalculator.simulation.environment.blockmanager;

import de.legoshi.parkourcalculator.simulation.environment.BlockPosition;
import de.legoshi.parkourcalculator.simulation.environment.block.*;
import de.legoshi.parkourcalculator.simulation.environment.block_1_12.*;
import javafx.scene.shape.Box;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockManager_1_12 extends BlockManager {

    public BlockManager_1_12() {
        registeredBlocks.add(new StandardBlock());
        registeredBlocks.add(new Ice());
        registeredBlocks.add(new Slime());
        registeredBlocks.add(new Shulker_1_12());
        registeredBlocks.add(new Soulsand());
        registeredBlocks.add(new EndPortalFrame());
        registeredBlocks.add(new Anvil());
        registeredBlocks.add(new DragonEgg());
        registeredBlocks.add(new Enderchest());
        registeredBlocks.add(new Cactus());

        registeredBlocks.add(new Stair());
        registeredBlocks.add(new Bed_1_12());
        registeredBlocks.add(new Cake());

        registeredBlocks.add(new Snow());
        registeredBlocks.add(new Carpet());
        registeredBlocks.add(new Lilypad_1_12());

        registeredBlocks.add(new ChorusPlant_1_12());
        registeredBlocks.add(new Cobblewall());
        registeredBlocks.add(new Fence());
        registeredBlocks.add(new Pane_1_12());

        registeredBlocks.add(new EndRod_1_12());
        registeredBlocks.add(new Head());
        registeredBlocks.add(new CocoaBean());
        registeredBlocks.add(new Flowerpot());

        registeredBlocks.add(new Vine());
        registeredBlocks.add(new Ladder_1_12());
        registeredBlocks.add(new Trapdoor());

        registeredBlocks.add(new PistonHead_1_12());
        registeredBlocks.add(new PistonBase());
        registeredBlocks.add(new BrewingStand());
        registeredBlocks.add(new Cauldron());
        registeredBlocks.add(new Hopper());

        registeredBlocks.add(new Water());
        registeredBlocks.add(new Lava());
        registeredBlocks.add(new Cobweb());
    }
    
    @Override
    public BlockManager clone() {
        ABlock[][][] aBlocks_clone = blocks.clone();
        HashMap<Box, ABlock> boxBlocks_clone = new HashMap<>(boxBlocks);
        List<ABlock> allBlocks_clone = new ArrayList<>(allBlocks);
        
        BlockManager_1_12 blockManager_1_12 = (BlockManager_1_12) super.clone();
        blockManager_1_12.blocks = aBlocks_clone;
        blockManager_1_12.boxBlocks = boxBlocks_clone;
        blockManager_1_12.allBlocks = allBlocks_clone;
        return blockManager_1_12;
    }

}
