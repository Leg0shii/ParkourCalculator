package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.gui.ConnectionGUI;
import de.legoshi.parkourcalculator.util.AxisVecTuple;
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

        calcNorth();
        calcEast();
        calcSouth();
        calcWest();

        this.axisVecTuples.add(base);

        if (ConnectionGUI.isNorth()) this.axisVecTuples.add(north);
        if (ConnectionGUI.isEast()) this.axisVecTuples.add(east);
        if (ConnectionGUI.isSouth()) this.axisVecTuples.add(south);
        if (ConnectionGUI.isWest()) this.axisVecTuples.add(west);

        if (ConnectionGUI.isNorth() && ConnectionGUI.isSouth() && !ConnectionGUI.isEast() && !ConnectionGUI.isWest()) {
            this.axisVecTuples = new ArrayList<>();
            calcThinNorth();
            return;
        } else if (ConnectionGUI.isEast() && ConnectionGUI.isWest() && !ConnectionGUI.isNorth() && !ConnectionGUI.isSouth()) {
            this.axisVecTuples = new ArrayList<>();
            calcThinWest();
            return;
        }

        if (ConnectionGUI.isNorth() && ConnectionGUI.isEast()) calcNorthEastCorner();
        if (ConnectionGUI.isNorth() && ConnectionGUI.isWest()) calcNorthWestCorner();
        if (ConnectionGUI.isSouth() && ConnectionGUI.isEast()) calcSouthEastCorner();
        if (ConnectionGUI.isSouth() && ConnectionGUI.isWest()) calcSouthWestCorner();

    }

    protected void calcBase() {
        Vec3 lowerEdge = new Vec3(0.25, 0, 0.25);
        Vec3 upperEdge = new Vec3(0.75, 1.5, 0.75);
        Vec3 shift = new Vec3(0.25, -0.25, 0.25);
        this.base = constructBlock(lowerEdge, upperEdge, shift);
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
