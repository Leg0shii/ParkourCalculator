package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.AxisVecTuple;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Ladder extends FacingBlock {

    public Ladder(Vec3 vec3) {
        super(vec3);
    }

    // works
    @Override
    protected void calcNorth() {
        Vec3 lowerEdge = getVec3().copy();
        Vec3 upperEdge = getVec3().copy();
        lowerEdge.addVector(0, 0, 0);
        upperEdge.addVector(1, 1.0, 0.125);
        Vec3 shift = new Vec3(0, 0, -0.4375);
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(lowerEdge, upperEdge);
        this.north = new AxisVecTuple(axisAlignedBB, shift);
    }

    @Override
    protected void calcEast() {
        Vec3 lowerEdge = getVec3().copy();
        Vec3 upperEdge = getVec3().copy();
        lowerEdge.addVector(0.875, 0, 0);
        upperEdge.addVector(1.0, 1.0, 1);
        Vec3 shift = new Vec3(1.3125, 0, 0);
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(lowerEdge, upperEdge);
        this.east = new AxisVecTuple(axisAlignedBB, shift);
    }

    @Override
    protected void calcSouth() {
        Vec3 lowerEdge = getVec3().copy();
        Vec3 upperEdge = getVec3().copy();
        lowerEdge.addVector(0, 0, 0.875);
        upperEdge.addVector(1, 1.0, 1.0);
        Vec3 shift = new Vec3(0, 0, 1.3125);
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(lowerEdge, upperEdge);
        this.south = new AxisVecTuple(axisAlignedBB, shift);
    }

    // works
    @Override
    protected void calcWest() {
        Vec3 lowerEdge = getVec3().copy();
        Vec3 upperEdge = getVec3().copy();
        lowerEdge.addVector(0.0, 0, 0);
        upperEdge.addVector(0.125, 1.0, 1);
        Vec3 shift = new Vec3(-0.4375, 0, 0);
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(lowerEdge, upperEdge);
        this.west = new AxisVecTuple(axisAlignedBB, shift);
    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/ladder.png");
    }

}
