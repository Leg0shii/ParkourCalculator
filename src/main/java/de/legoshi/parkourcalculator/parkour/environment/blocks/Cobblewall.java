package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.gui.debug.menu.BlockSettings;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
public class Cobblewall extends FacingBlock {

    public Cobblewall(Vec3 vec3) {
        super(vec3);
    }

    @Override
    protected void updateBoundingBox() {
        this.axisVecTuples = new ArrayList<>();

        calcBase();

        if (BlockSettings.isNorth()) calcNorth();
        if (BlockSettings.isEast()) calcEast();
        if (BlockSettings.isSouth()) calcSouth();
        if (BlockSettings.isWest()) calcWest();

        if (BlockSettings.isNorth() && BlockSettings.isSouth() && !BlockSettings.isEast() && !BlockSettings.isWest()) {
            this.axisVecTuples = new ArrayList<>();
            calcThinNorth();
            return;
        } else if (BlockSettings.isEast() && BlockSettings.isWest() && !BlockSettings.isNorth() && !BlockSettings.isSouth()) {
            this.axisVecTuples = new ArrayList<>();
            calcThinWest();
            return;
        }

        if (BlockSettings.isNorth() && BlockSettings.isEast()) calcNorthEastCorner();
        if (BlockSettings.isNorth() && BlockSettings.isWest()) calcNorthWestCorner();
        if (BlockSettings.isSouth() && BlockSettings.isEast()) calcSouthEastCorner();
        if (BlockSettings.isSouth() && BlockSettings.isWest()) calcSouthWestCorner();

    }

    protected void calcBase() {
        Vec3 lowerEdge = new Vec3(0.25, 0, 0.25);
        Vec3 upperEdge = new Vec3(0.75, 1.5, 0.75);
        Vec3 shift = new Vec3(0.25, -0.25, 0.25);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcNorth() {
        Vec3 lowerEdge = new Vec3(0.25, 0, 0);
        Vec3 upperEdge = new Vec3(0.75, 1.5, 0.5);
        Vec3 shift = new Vec3(0.25, -0.25, 0.25);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcEast() {
        Vec3 lowerEdge = new Vec3(0.5, 0, 0.25);
        Vec3 upperEdge = new Vec3(1, 1.5, 0.75);
        Vec3 shift = new Vec3(0.25, -0.25, 0.25);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcSouth() {
        Vec3 lowerEdge = new Vec3(0.25, 0, 0.5);
        Vec3 upperEdge = new Vec3(0.75, 1.5, 1);
        Vec3 shift = new Vec3(0.25, -0.25, 0.25);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcWest() {
        Vec3 lowerEdge = new Vec3(0, 0, 0.25);
        Vec3 upperEdge = new Vec3(0.5, 1.5, 0.75);
        Vec3 shift = new Vec3(0.25, -0.25, 0.25);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    protected void calcNorthEastCorner() {
        Vec3 lowerEdge = new Vec3(0.25, 0, 0.0);
        Vec3 upperEdge = new Vec3(1, 1.5, 0.75);
        Vec3 shift = new Vec3(0.25 / 2, -0.25, 0.25 / 2);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    protected void calcNorthWestCorner() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(0.75, 1.5, 0.75);
        Vec3 shift = new Vec3(0.25 / 2, -0.25, 0.25 / 2);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    protected void calcSouthEastCorner() {
        Vec3 lowerEdge = new Vec3(0.25, 0, 0.25);
        Vec3 upperEdge = new Vec3(1, 1.5, 1);
        Vec3 shift = new Vec3(0.25 / 2, -0.25, 0.25 / 2);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    protected void calcSouthWestCorner() {
        Vec3 lowerEdge = new Vec3(0, 0, 0.25);
        Vec3 upperEdge = new Vec3(0.75, 1.5, 1);
        Vec3 shift = new Vec3(0.25 / 2, -0.25, 0.25 / 2);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    protected void calcThinNorth() {
        Vec3 lowerEdge = new Vec3(0.3125, 0, 0);
        Vec3 upperEdge = new Vec3(0.6875, 1.5, 1);
        Vec3 shift = new Vec3(0.3125, -0.25, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    protected void calcThinWest() {
        Vec3 lowerEdge = new Vec3(0, 0, 0.3125);
        Vec3 upperEdge = new Vec3(1, 1.5, 0.6875);
        Vec3 shift = new Vec3(0, -0.25, 0.3125);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcBaseFlip() {

    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/cobblewall.webp");
    }

}
