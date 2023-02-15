package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.gui.ConnectionGUI;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.AxisVecTuple;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
public class Pane extends ABlock {

    // north, east, south, west
    private AxisVecTuple northPane;
    private AxisVecTuple eastPane;
    private AxisVecTuple southPane;
    private AxisVecTuple westPane;

    public Pane(Vec3 vec3) {
        super(vec3);
    }

    @Override
    void updateBoundingBox() {

        calcNorthPane();
        calcEastPane();
        calcSouthPane();
        calcWestPane();

        this.axisVecTuples = new ArrayList<>();
        if (ConnectionGUI.isNorth()) this.axisVecTuples.add(northPane);
        if (ConnectionGUI.isEast()) this.axisVecTuples.add(eastPane);
        if (ConnectionGUI.isSouth()) this.axisVecTuples.add(southPane);
        if (ConnectionGUI.isWest()) this.axisVecTuples.add(westPane);
    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/glass.png");
    }

    private void calcNorthPane() {
        Vec3 lowerEdge = getVec3().copy();
        Vec3 upperEdge = getVec3().copy();
        lowerEdge.addVector(0.4375, 0, 0);
        upperEdge.addVector(0.5625, 1.0, 0.5000);
        Vec3 shift = new Vec3(0.4375, 0, 0.5000/2);
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(lowerEdge, upperEdge);
        this.northPane = new AxisVecTuple(axisAlignedBB, shift);
    }

    private void calcEastPane() {
        Vec3 lowerEdge = getVec3().copy();
        Vec3 upperEdge = getVec3().copy();
        lowerEdge.addVector(0.5000, 0, 0.4375);
        upperEdge.addVector(1.0, 1.0, 0.5625);
        Vec3 shift = new Vec3(0.5000/2, 0, 0.4375);
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(lowerEdge, upperEdge);
        this.eastPane = new AxisVecTuple(axisAlignedBB, shift);
    }

    private void calcSouthPane() {
        Vec3 lowerEdge = getVec3().copy();
        Vec3 upperEdge = getVec3().copy();
        lowerEdge.addVector(0.4375, 0, 0.5000);
        upperEdge.addVector(0.5625, 1.0, 1.0);
        Vec3 shift = new Vec3(0.4375, 0, 0.5000/2);
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(lowerEdge, upperEdge);
        this.southPane = new AxisVecTuple(axisAlignedBB, shift);
    }

    private void calcWestPane() {
        Vec3 lowerEdge = getVec3().copy();
        Vec3 upperEdge = getVec3().copy();
        lowerEdge.addVector(0.0, 0, 0.4375);
        upperEdge.addVector(0.5000, 1.0, 0.5625);
        Vec3 shift = new Vec3(0.5000/2, 0, 0.4375);
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(lowerEdge, upperEdge);
        this.westPane = new AxisVecTuple(axisAlignedBB, shift);
    }

}
