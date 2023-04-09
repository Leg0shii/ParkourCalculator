package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.util.AxisVecTuple;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Fence extends FacingBlock {

    public Fence(Vec3 vec3) {
        super(vec3);
    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/fence.webp");
    }

    protected void calcBase() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(0.25, 1.5, 0.25);
        Vec3 shift = new Vec3(0, -0.25, 0);

        AxisVecTuple axisVecTuple = constructBlock(lowerEdge, upperEdge, shift);
        this.axisVecTuples.add(axisVecTuple);
    }

    @Override
    protected void calcBaseFlip() {

    }

    @Override
    protected void calcNorth() {
        Vec3 lowerEdge = new Vec3(0.375, 0, 0);
        Vec3 upperEdge = new Vec3(0.625, 1.5, 0.5);
        Vec3 shift = new Vec3(0.375, -0.25, 0.25);
        this.north = constructBlock(lowerEdge, upperEdge, shift);
    }

    @Override
    protected void calcEast() {
        Vec3 lowerEdge = new Vec3(0.5, 0, 0.375);
        Vec3 upperEdge = new Vec3(1.0, 1.5, 0.625);
        Vec3 shift = new Vec3(0.25, -0.25, 0.375);
        this.east = constructBlock(lowerEdge, upperEdge, shift);
    }

    @Override
    protected void calcSouth() {
        Vec3 lowerEdge = new Vec3(0.375, 0, 0.5);
        Vec3 upperEdge = new Vec3(0.625, 1.5, 1.0);
        Vec3 shift = new Vec3(0.375, -0.25, 0.25);
        this.south = constructBlock(lowerEdge, upperEdge, shift);
    }

    @Override
    protected void calcWest() {
        Vec3 lowerEdge = new Vec3(0.0, 0, 0.375);
        Vec3 upperEdge = new Vec3(0.5000, 1.5, 0.625);
        Vec3 shift = new Vec3(0.25, -0.25, 0.375);
        this.west = constructBlock(lowerEdge, upperEdge, shift);
    }

}
