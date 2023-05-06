package de.legoshi.parkourcalculator.simulation.environment.blockmanager;

import de.legoshi.parkourcalculator.simulation.environment.block.*;
import de.legoshi.parkourcalculator.simulation.environment.block_1_12.*;

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

}
