package de.legoshi.parkourcalculator.simulation.environment.blockmanager;

import de.legoshi.parkourcalculator.simulation.environment.block.*;
import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.AxisVecTuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class BlockManager_1_8 extends BlockManager {

    public static ABlock currentBlock = new StandardBlock();
    public static List<ABlock> registeredBlocks = new ArrayList<>();
    public static List<ABlock> aBlocks = new ArrayList<>();

    public BlockManager_1_8() {
        registeredBlocks.add(new Water());
        registeredBlocks.add(new Cobweb());
        registeredBlocks.add(new Slime());
        registeredBlocks.add(new Lava());
        registeredBlocks.add(new PistonHead());
        registeredBlocks.add(new PistonBase());
        registeredBlocks.add(new StandardBlock());
        registeredBlocks.add(new Enderchest());
        registeredBlocks.add(new Pane());
        registeredBlocks.add(new Cake());
        registeredBlocks.add(new Stair());
        registeredBlocks.add(new Ladder());
        registeredBlocks.add(new Vine());
        registeredBlocks.add(new Anvil());
        registeredBlocks.add(new Bed());
        registeredBlocks.add(new BrewingStand());
        registeredBlocks.add(new Cactus());
        registeredBlocks.add(new Carpet());
        registeredBlocks.add(new Cauldron());
        registeredBlocks.add(new Cobblewall());
        registeredBlocks.add(new CocoaBean());
        registeredBlocks.add(new DragonEgg());
        registeredBlocks.add(new EndPortalFrame());
        registeredBlocks.add(new Fence());
        registeredBlocks.add(new Flowerpot());
        registeredBlocks.add(new Head());
        registeredBlocks.add(new Hopper());
        registeredBlocks.add(new Ice());
        registeredBlocks.add(new Lilypad());
        registeredBlocks.add(new Snow());
        registeredBlocks.add(new Soulsand());
        registeredBlocks.add(new Trapdoor());
    }

    public static void updateCurrentBlock(ABlock aBlock) {
        currentBlock = aBlock;
    }

    public void addBlock(ABlock block) {
        aBlocks.add(block);
    }

    public void removeBlock(ABlock block) {
        aBlocks.remove(block);
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

    public static ABlock getBlock(double x, double y, double z) {
        for (ABlock aBlock : aBlocks) {
            if (aBlock.getVec3().x == x && aBlock.getVec3().y == y && aBlock.getVec3().z == z) {
                return aBlock;
            }
        }
        return new Air();
    }

    @Override
    public void update(Observable o, Object arg) {
        List<Object> objects = (List<Object>) arg;
        if (objects.get(0).equals("add")) addBlock((ABlock) objects.get(1));
        else removeBlock((ABlock) objects.get(1));
    }
}
