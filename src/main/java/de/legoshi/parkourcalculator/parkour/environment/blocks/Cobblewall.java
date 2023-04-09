package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.AxisVecTuple;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Cobblewall extends FacingBlock {

    public Cobblewall(Vec3 vec3) {
        super(vec3);
    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/cobblewall.webp");
    }

    protected void calcBase() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(0.5, 1.5, 0.5);
        Vec3 shift = new Vec3(0, -0.25, 0);
        this.base = constructBlock(lowerEdge, upperEdge, shift);
    }

    @Override
    protected void calcBaseFlip() {

    }

    @Override
    protected void calcNorth() {
        Vec3 lowerEdge = new Vec3(0.25, 0, 0);
        Vec3 upperEdge = new Vec3(0.75, 1.5, 0.5);
        Vec3 shift = new Vec3(0.25, -0.25, 0.25);
        this.north = constructBlock(lowerEdge, upperEdge, shift);
    }

    @Override
    protected void calcEast() {
        Vec3 lowerEdge = new Vec3(0.5, 0, 0.25);
        Vec3 upperEdge = new Vec3(1, 1.5, 0.75);
        Vec3 shift = new Vec3(0.25, -0.25, 0.25);
        this.east = constructBlock(lowerEdge, upperEdge, shift);
    }

    @Override
    protected void calcSouth() {
        Vec3 lowerEdge = new Vec3(0.25, 0, 0.5);
        Vec3 upperEdge = new Vec3(0.75, 1.5, 1);
        Vec3 shift = new Vec3(0.25, -0.25, 0.25);
        this.south = constructBlock(lowerEdge, upperEdge, shift);
    }

    @Override
    protected void calcWest() {
        Vec3 lowerEdge = new Vec3(0, 0, 0.25);
        Vec3 upperEdge = new Vec3(0.5, 1.5, 0.75);
        Vec3 shift = new Vec3(0.25, -0.25, 0.25);
        this.west = constructBlock(lowerEdge, upperEdge, shift);
    }

}
