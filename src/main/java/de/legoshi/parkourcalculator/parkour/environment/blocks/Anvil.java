package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.gui.ConnectionGUI;
import de.legoshi.parkourcalculator.util.AxisVecTuple;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
public class Anvil extends FacingBlock {

    public Anvil(Vec3 vec3) {
        super(vec3);
    }

    @Override
    protected void calcBase() {

    }

    @Override
    protected void calcBaseFlip() {

    }

    @Override
    protected void updateBoundingBox() {
        this.axisVecTuples = new ArrayList<>();

        calcNorth();
        calcEast();

        if (ConnectionGUI.isNorth() || ConnectionGUI.isSouth()) this.axisVecTuples.add(north);
        else if (ConnectionGUI.isEast() || ConnectionGUI.isWest()) this.axisVecTuples.add(east);
        else this.axisVecTuples.add(north);
    }

    @Override
    protected void calcNorth() {
        Vec3 lowerEdge = new Vec3(0.125, 0, 0);
        Vec3 upperEdge = new Vec3(0.875, 1, 1);
        Vec3 shift = new Vec3(0.125, 0, 0);
        this.north = constructBlock(lowerEdge, upperEdge, shift);
    }

    @Override
    protected void calcEast() {
        Vec3 lowerEdge = new Vec3(0, 0, 0.125);
        Vec3 upperEdge = new Vec3(1, 1, 0.875);
        Vec3 shift = new Vec3(0, 0, 0.125);
        this.east = constructBlock(lowerEdge, upperEdge, shift);
    }

    @Override
    protected void calcSouth() {

    }

    @Override
    protected void calcWest() {

    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/anvil.webp");
    }

}
