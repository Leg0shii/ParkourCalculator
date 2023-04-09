package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.gui.ConnectionGUI;
import de.legoshi.parkourcalculator.util.AxisVecTuple;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
public class PistonHead extends FacingBlock {

    protected AxisVecTuple northRod;
    protected AxisVecTuple eastRod;
    protected AxisVecTuple southRod;
    protected AxisVecTuple westRod;

    protected AxisVecTuple baseRod;
    protected AxisVecTuple baseFlipRod;

    public PistonHead(Vec3 vec3) {
        super(vec3);
    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/piston_head.webp");
    }

    @Override
    protected void updateBoundingBox() {
        this.axisVecTuples = new ArrayList<>();

        calcBase();
        calcBaseFlip();

        calcNorth();
        calcEast();
        calcSouth();
        calcWest();

        if (ConnectionGUI.isNorth()) {
            this.axisVecTuples.add(north);
            this.axisVecTuples.add(northRod);
        } else if (ConnectionGUI.isEast()) {
            this.axisVecTuples.add(east);
            this.axisVecTuples.add(eastRod);
        } else if (ConnectionGUI.isSouth()) {
            this.axisVecTuples.add(south);
            this.axisVecTuples.add(southRod);
        } else if (ConnectionGUI.isWest()) {
            this.axisVecTuples.add(west);
            this.axisVecTuples.add(westRod);
        } else if (ConnectionGUI.isFlip() && baseFlip != null) {
            this.axisVecTuples.add(baseFlip);
            this.axisVecTuples.add(baseFlipRod);
        } else if (!ConnectionGUI.isFlip() && base != null) {
            this.axisVecTuples.add(base);
            this.axisVecTuples.add(baseRod);
        }
    }
    // 0.5x0.25x1
    @Override
    protected void calcBase() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(1, 0.25, 1);
        Vec3 shift = new Vec3(0, 0.375, 0);
        this.base = constructBlock(lowerEdge, upperEdge, shift);

        Vec3 lowerEdgeRod = new Vec3(0, 0, 0);
        Vec3 upperEdgeRod = new Vec3(0.25, 1, 0.5);
        Vec3 shiftRod = new Vec3(0, 0, 0);
        this.baseRod = constructBlock(lowerEdgeRod, upperEdgeRod, shiftRod);
    }

    @Override
    protected void calcBaseFlip() {
        Vec3 lowerEdge = new Vec3(0, 0.75, 0);
        Vec3 upperEdge = new Vec3(1, 1, 1);
        Vec3 shift = new Vec3(0, 0.375, 0);
        this.baseFlip = constructBlock(lowerEdge, upperEdge, shift);
    }

    @Override
    protected void calcNorth() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(1, 1, 0.25);
        Vec3 shift = new Vec3(0, 0, 0.375);
        this.north = constructBlock(lowerEdge, upperEdge, shift);
    }

    @Override
    protected void calcEast() {
        Vec3 lowerEdge = new Vec3(0.75, 0, 0);
        Vec3 upperEdge = new Vec3(1, 1, 1);
        Vec3 shift = new Vec3(0.375, 0, 0);
        this.east = constructBlock(lowerEdge, upperEdge, shift);
    }

    @Override
    protected void calcSouth() {
        Vec3 lowerEdge = new Vec3(0, 0, 0.75);
        Vec3 upperEdge = new Vec3(1, 1, 1);
        Vec3 shift = new Vec3(0, 0, 0.375);
        this.south = constructBlock(lowerEdge, upperEdge, shift);
    }

    @Override
    protected void calcWest() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(0.25, 1, 1);
        Vec3 shift = new Vec3(0.375, 0, 0);
        this.west = constructBlock(lowerEdge, upperEdge, shift);
    }
}
