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

    public static Block[][][] field = new Block[100][100][100];

    public Environment() {
        addFirstBlock();
    }

    public void addFirstBlock() {
        field[0][0][0] = new FullBlock(0, 0, 0);
    }

    public static void addBlock(Box placedBox, Box firstBox) {
        int x = (int) Math.round((placedBox.getTranslateX() - firstBox.getTranslateX())/100);
        int y = (int) Math.round((firstBox.getTranslateY() - placedBox.getTranslateY())/100);
        int z = (int) Math.round((placedBox.getTranslateZ() - firstBox.getTranslateZ())/100);
        Block block = new FullBlock(x, y, z);
        field[block.gridY][block.gridZ][block.gridX] = block;
    }

    public static void removeBlock(Box placedBox, Box firstBox) {
        int x = (int) Math.round((placedBox.getTranslateX() - firstBox.getTranslateX())/100);
        int y = (int) Math.round((firstBox.getTranslateY() - placedBox.getTranslateY())/100);
        int z = (int) Math.round((placedBox.getTranslateZ() - firstBox.getTranslateZ())/100);
        field[y][z][x] = null;
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
        return field[y][z][x].slipperiness;
    }

    private Block getBlockFromDrawable(Block.DrawableBlock block, int x, int y, int z) {
        if (block instanceof FullBlock.DrawableFullBlock) return new FullBlock(x, y, z);
        if (block instanceof Fence.DrawableFence) return new Fence(x, y, z);
        if (block instanceof CobbleWall.DrawableCobbleWall) return new CobbleWall(x, y, z);
        return null;
    }

}
