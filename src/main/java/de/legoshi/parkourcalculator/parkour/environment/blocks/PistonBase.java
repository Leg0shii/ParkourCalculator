package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.gui.ConnectionGUI;
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
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/piston_base.webp");
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

        if (ConnectionGUI.isNorth()) this.axisVecTuples.add(north);
        else if (ConnectionGUI.isEast()) this.axisVecTuples.add(east);
        else if (ConnectionGUI.isSouth()) this.axisVecTuples.add(south);
        else if (ConnectionGUI.isWest()) this.axisVecTuples.add(west);
        else if (ConnectionGUI.isFlip() && baseFlip != null) this.axisVecTuples.add(baseFlip);
        else if (!ConnectionGUI.isFlip() && base != null) this.axisVecTuples.add(base);
    }

    @Override
    protected void calcBase() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(1, 0.75, 1);
        Vec3 shift = new Vec3(0, 0.125, 0);
        this.base = constructBlock(lowerEdge, upperEdge, shift);
    }

    @Override
    protected void calcBaseFlip() {
        Vec3 lowerEdge = new Vec3(0, 0.25, 0);
        Vec3 upperEdge = new Vec3(1, 1, 1);
        Vec3 shift = new Vec3(0, 0.125, 0);
        this.baseFlip = constructBlock(lowerEdge, upperEdge, shift);
    }

    @Override
    protected void calcNorth() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(1, 1, 0.75);
        Vec3 shift = new Vec3(0, 0, 0.125);
        this.north = constructBlock(lowerEdge, upperEdge, shift);
    }

    @Override
    protected void calcEast() {
        Vec3 lowerEdge = new Vec3(0.25, 0, 0);
        Vec3 upperEdge = new Vec3(1, 1, 1);
        Vec3 shift = new Vec3(0.125, 0, 0);
        this.east = constructBlock(lowerEdge, upperEdge, shift);
    }

    @Override
    protected void calcSouth() {
        Vec3 lowerEdge = new Vec3(0, 0, 0.25);
        Vec3 upperEdge = new Vec3(1, 1, 1);
        Vec3 shift = new Vec3(0, 0, 0.125);
        this.south = constructBlock(lowerEdge, upperEdge, shift);
    }

    @Override
    protected void calcWest() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(0.75, 1, 1);
        Vec3 shift = new Vec3(0.125, 0, 0);
        this.west = constructBlock(lowerEdge, upperEdge, shift);
    }
}
