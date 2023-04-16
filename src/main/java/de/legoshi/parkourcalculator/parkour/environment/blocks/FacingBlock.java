package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.gui.debug.menu.BlockSettings;
import de.legoshi.parkourcalculator.util.AxisVecTuple;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
public abstract class FacingBlock extends ABlock {

    public FacingBlock(Vec3 vec3) {
        super(vec3);
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
    }

    protected abstract void calcBase();
    protected abstract void calcBaseFlip();
    protected abstract void calcNorth();
    protected abstract void calcEast();
    protected abstract void calcSouth();
    protected abstract void  calcWest();

}
