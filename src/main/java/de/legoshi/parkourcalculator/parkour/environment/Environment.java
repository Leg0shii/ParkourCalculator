package de.legoshi.parkourcalculator.parkour.environment;

import de.legoshi.parkourcalculator.parkour.environment.blocks.Block;
import de.legoshi.parkourcalculator.parkour.environment.blocks.CobbleWall;
import de.legoshi.parkourcalculator.parkour.environment.blocks.Fence;
import de.legoshi.parkourcalculator.parkour.environment.blocks.FullBlock;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.Movement;
import javafx.scene.shape.Box;

import java.util.ArrayList;
import java.util.List;

public class Environment {

    public static Block[][][] field = new Block[200][200][200];
    private static final int ARRAY_OFFSET = 50;

    public Environment() {
        addFirstBlock();
    }

    public void addFirstBlock() {
        field[ARRAY_OFFSET][ARRAY_OFFSET][ARRAY_OFFSET] = new FullBlock(0, 0, 0);
    }

    public static void addBlock(Box placedBox, Box firstBox) {
        int x = (int) Math.round((placedBox.getTranslateX() - firstBox.getTranslateX())/100);
        int y = (int) Math.round((firstBox.getTranslateY() - placedBox.getTranslateY())/100);
        int z = (int) Math.round((placedBox.getTranslateZ() - firstBox.getTranslateZ())/100);
        Block block = new FullBlock(x, y, z);
        field[block.gridY+ARRAY_OFFSET][block.gridZ+ARRAY_OFFSET][block.gridX+ARRAY_OFFSET] = block;
    }

    public static void removeBlock(Box placedBox, Box firstBox) {
        int x = (int) Math.round((placedBox.getTranslateX() - firstBox.getTranslateX())/100);
        int y = (int) Math.round((firstBox.getTranslateY() - placedBox.getTranslateY())/100);
        int z = (int) Math.round((placedBox.getTranslateZ() - firstBox.getTranslateZ())/100);
        field[y+ARRAY_OFFSET][z+ARRAY_OFFSET][x+ARRAY_OFFSET] = null;
    }

    public static List<AxisAlignedBB> getAllBBs() {
        List<AxisAlignedBB> list = new ArrayList<>();
        for (Block[][] blockss : field) {
            for (Block[] blocks : blockss) {
                for (Block block : blocks) {
                    if (block != null) list.addAll(block.axisAlignedBBS);
                }
            }
        }
        return list;
    }

    public static Movement.Slipperiness getSlipperinessFromWorld(int x, int y, int z) {
        return field[y+ARRAY_OFFSET][z+ARRAY_OFFSET][x+ARRAY_OFFSET].slipperiness;
    }

    private Block getBlockFromDrawable(Block.DrawableBlock block, int x, int y, int z) {
        if (block instanceof FullBlock.DrawableFullBlock) return new FullBlock(x, y, z);
        if (block instanceof Fence.DrawableFence) return new Fence(x, y, z);
        if (block instanceof CobbleWall.DrawableCobbleWall) return new CobbleWall(x, y, z);
        return null;
    }

}
