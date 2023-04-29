package de.legoshi.parkourcalculator.simulation.environment.blockmanager;

import de.legoshi.parkourcalculator.simulation.environment.block.*;
import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import de.legoshi.parkourcalculator.simulation.environment.block_1_8.*;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.AxisVecTuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class BlockManager_1_8 extends BlockManager {

    public BlockManager_1_8() {
        registeredBlocks.add(new StandardBlock());
        registeredBlocks.add(new Ice());
        registeredBlocks.add(new Slime());
        registeredBlocks.add(new Soulsand());
        registeredBlocks.add(new EndPortalFrame());
        registeredBlocks.add(new Anvil());
        registeredBlocks.add(new DragonEgg());
        registeredBlocks.add(new Enderchest());
        registeredBlocks.add(new Cactus());

        registeredBlocks.add(new Stair());
        registeredBlocks.add(new Bed_1_8());
        registeredBlocks.add(new Cake());

        registeredBlocks.add(new Snow());
        registeredBlocks.add(new Carpet());
        registeredBlocks.add(new Lilypad_1_8());

        registeredBlocks.add(new Cobblewall());
        registeredBlocks.add(new Fence());
        registeredBlocks.add(new Pane_1_8());

        registeredBlocks.add(new Head());
        registeredBlocks.add(new CocoaBean());
        registeredBlocks.add(new Flowerpot());

        registeredBlocks.add(new Vine());
        registeredBlocks.add(new Ladder_1_8());
        registeredBlocks.add(new Trapdoor());

        registeredBlocks.add(new PistonHead_1_8());
        registeredBlocks.add(new PistonBase());
        registeredBlocks.add(new BrewingStand());
        registeredBlocks.add(new Cauldron());
        registeredBlocks.add(new Hopper());

        registeredBlocks.add(new Water());
        registeredBlocks.add(new Lava());
        registeredBlocks.add(new Cobweb());
    }

    public List<AxisAlignedBB> getAllBBs() {
        List<AxisAlignedBB> boundingBoxes = new ArrayList<>();
        for (ABlock aBlock : aBlocks) {
            for (AxisVecTuple axisVecTuple : aBlock.axisVecTuples) {
                boundingBoxes.add(axisVecTuple.getBb());
            }
        }
        return boundingBoxes;
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

}
