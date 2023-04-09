package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.gui.ConnectionGUI;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
public class Head extends FacingBlock {

    public Head(Vec3 vec3) {
        super(vec3);
    }

    @Override
    protected void updateBoundingBox() {
        this.axisVecTuples = new ArrayList<>();

        if (!ConnectionGUI.isNorth() && !ConnectionGUI.isEast() && !ConnectionGUI.isSouth() && !ConnectionGUI.isWest()) calcBase();
        else if (ConnectionGUI.isNorth()) calcNorth();
        else if (ConnectionGUI.isEast()) calcEast();
        else if (ConnectionGUI.isSouth()) calcSouth();
        else if (ConnectionGUI.isWest()) calcWest();
    }

    @Override
    protected void calcBase() {
        Vec3 lowerEdge = new Vec3(0.25, 0, 0.25);
        Vec3 upperEdge = new Vec3(0.75, 0.5, 0.75);
        Vec3 shift = new Vec3(0.25, 0.25, 0.25);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcNorth() {
        Vec3 lowerEdge = new Vec3(0.25, 0.25, 0);
        Vec3 upperEdge = new Vec3(0.75, 0.75, 0.5);
        Vec3 shift = new Vec3(0.25, 0.25, -0.25);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcEast() {
        Vec3 lowerEdge = new Vec3(0.5, 0.25, 0.25);
        Vec3 upperEdge = new Vec3(1, 0.75, 0.75);
        Vec3 shift = new Vec3(0.25, 0.25, 0.25);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcSouth() {
        Vec3 lowerEdge = new Vec3(0.25, 0.25, 0.5);
        Vec3 upperEdge = new Vec3(0.75, 0.75, 1);
        Vec3 shift = new Vec3(0.25, 0.25, 0.75);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcWest() {
        Vec3 lowerEdge = new Vec3(0, 0.25, 0.25);
        Vec3 upperEdge = new Vec3(0.5, 0.75, 0.75);
        Vec3 shift = new Vec3(0.25, 0.25, 0.25);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcBaseFlip() {

    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/head.webp");
    }

}
