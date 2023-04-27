package de.legoshi.parkourcalculator.simulation.environment.block;

import de.legoshi.parkourcalculator.gui.debug.menu.BlockSettings;
import de.legoshi.parkourcalculator.util.BlockColors;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
//base: 1x1x0.75,
public class PistonBase extends FacingBlock {

    public PistonBase(Vec3 vec3) {
        super(vec3);
    }

    @Override
    public void updateColor() {
        setMaterialColor(BlockColors.STONE.get());
        setSpecularColor(BlockColors.STONE_SPEC.get());
    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/piston_base.png");
    }

    @Override
    protected void updateBoundingBox() {
        this.axisVecTuples = new ArrayList<>();

        if (BlockSettings.isNorth()) calcNorth();
        else if (BlockSettings.isEast()) calcEast();
        else if (BlockSettings.isSouth()) calcSouth();
        else if (BlockSettings.isWest()) calcWest();
        else if (BlockSettings.isFlip()) calcBaseFlip();
        else if (BlockSettings.isFloor()) calcBase();

        if (this.axisVecTuples.isEmpty()) calcSouth();
    }

    @Override
    protected void calcBase() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(1, 0.75, 1);
        Vec3 shift = new Vec3(0, 0.125, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcBaseFlip() {
        Vec3 lowerEdge = new Vec3(0, 0.25, 0);
        Vec3 upperEdge = new Vec3(1, 1, 1);
        Vec3 shift = new Vec3(0, 0.125, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcSouth() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(1, 1, 0.75);
        Vec3 shift = new Vec3(0, 0, 0.125);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcEast() {
        Vec3 lowerEdge = new Vec3(0.25, 0, 0);
        Vec3 upperEdge = new Vec3(1, 1, 1);
        Vec3 shift = new Vec3(0.125, 0, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcNorth() {
        Vec3 lowerEdge = new Vec3(0, 0, 0.25);
        Vec3 upperEdge = new Vec3(1, 1, 1);
        Vec3 shift = new Vec3(0, 0, 0.125);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcWest() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(0.75, 1, 1);
        Vec3 shift = new Vec3(0.125, 0, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }
}
