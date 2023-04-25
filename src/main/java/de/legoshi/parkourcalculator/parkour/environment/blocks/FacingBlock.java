package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.file.BlockData;
import de.legoshi.parkourcalculator.gui.debug.menu.BlockSettings;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
public abstract class FacingBlock extends ABlock {

    private boolean TOP, BOTTOM, NORTH, EAST, SOUTH, WEST;

    public FacingBlock(Vec3 vec3) {
        super(vec3);
        this.BOTTOM = BlockSettings.isFloor();
        this.TOP = BlockSettings.isFlip();
        this.NORTH = BlockSettings.isNorth();
        this.EAST = BlockSettings.isEast();
        this.SOUTH = BlockSettings.isSouth();
        this.WEST = BlockSettings.isWest();
    }

    @Override
    protected void updateBoundingBox() {
        this.axisVecTuples = new ArrayList<>();

        if (BlockSettings.isNorth()) calcNorth();
        if (BlockSettings.isEast()) calcEast();
        if (BlockSettings.isSouth()) calcSouth();
        if (BlockSettings.isWest()) calcWest();

        if (BlockSettings.isFlip()) calcBaseFlip();
        if (BlockSettings.isFloor()) calcBase();

        if (this.axisVecTuples.isEmpty()) calcSouth();
    }

    @Override
    public BlockData toBlockData() {
        BlockData blockData = super.toBlockData();
        blockData.BOTTOM = this.BOTTOM;
        blockData.TOP = this.TOP;
        blockData.NORTH = this.NORTH;
        blockData.EAST = this.EAST;
        blockData.WEST = this.WEST;
        blockData.SOUTH = this.SOUTH;
        return blockData;
    }

    protected abstract void calcBase();
    protected abstract void calcBaseFlip();
    protected abstract void calcNorth();
    protected abstract void calcEast();
    protected abstract void calcSouth();
    protected abstract void  calcWest();

}
