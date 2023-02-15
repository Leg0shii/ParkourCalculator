package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.gui.ConnectionGUI;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.AxisVecTuple;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
public class Stair extends ABlock {

    private AxisVecTuple baseStair;

    private AxisVecTuple northStair;
    private AxisVecTuple eastStair;
    private AxisVecTuple southStair;
    private AxisVecTuple westStair;

    public Stair(Vec3 vec3) {
        super(vec3);
    }

    @Override
    void updateBoundingBox() {
        calcBaseStair();
        calcNorthStair();
        calcEastStair();
        calcSouthStair();
        calcWestStair();

        this.axisVecTuples = new ArrayList<>();
        this.axisVecTuples.add(baseStair);

        if (ConnectionGUI.isNorth()) this.axisVecTuples.add(northStair);
        if (ConnectionGUI.isEast()) this.axisVecTuples.add(eastStair);
        if (ConnectionGUI.isSouth()) this.axisVecTuples.add(southStair);
        if (ConnectionGUI.isWest()) this.axisVecTuples.add(westStair);
    }

    private void calcBaseStair() {
        Vec3 lowerEdge = getVec3().copy();
        Vec3 upperEdge = getVec3().copy();
        lowerEdge.addVector(0, 0, 0);
        upperEdge.addVector(1, 0.5, 1);

        Vec3 shift = new Vec3(0, 0.25, 0);
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(lowerEdge, upperEdge);
        this.baseStair = new AxisVecTuple(axisAlignedBB, shift);
    }

    private void calcNorthStair() {
        Vec3 lowerEdge = getVec3().copy();
        Vec3 upperEdge = getVec3().copy();
        lowerEdge.addVector(0, 0, 0);
        upperEdge.addVector(1, 1.0, 0.5);
        Vec3 shift = new Vec3(0, 0, 0.25);
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(lowerEdge, upperEdge);
        this.northStair = new AxisVecTuple(axisAlignedBB, shift);
    }

    private void calcEastStair() {
        Vec3 lowerEdge = getVec3().copy();
        Vec3 upperEdge = getVec3().copy();
        lowerEdge.addVector(0.5, 0, 0);
        upperEdge.addVector(1, 1.0, 1);
        Vec3 shift = new Vec3(0.25, 0, 0);
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(lowerEdge, upperEdge);
        this.eastStair = new AxisVecTuple(axisAlignedBB, shift);
    }

    private void calcSouthStair() {
        Vec3 lowerEdge = getVec3().copy();
        Vec3 upperEdge = getVec3().copy();
        lowerEdge.addVector(0, 0, 0.5);
        upperEdge.addVector(1, 1.0, 1);
        Vec3 shift = new Vec3(0, 0, 0.25);
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(lowerEdge, upperEdge);
        this.southStair = new AxisVecTuple(axisAlignedBB, shift);
    }

    private void calcWestStair() {
        Vec3 lowerEdge = getVec3().copy();
        Vec3 upperEdge = getVec3().copy();
        lowerEdge.addVector(0, 0, 0);
        upperEdge.addVector(0.5, 1.0, 1);
        Vec3 shift = new Vec3(0.25, 0, 0);
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(lowerEdge, upperEdge);
        this.westStair = new AxisVecTuple(axisAlignedBB, shift);
    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/stair.png");
    }
}
