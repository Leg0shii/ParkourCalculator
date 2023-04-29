package de.legoshi.parkourcalculator.simulation.environment.block_1_12;

import de.legoshi.parkourcalculator.gui.debug.menu.BlockSettings;
import de.legoshi.parkourcalculator.simulation.environment.block.FacingBlock;
import de.legoshi.parkourcalculator.util.BlockColors;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
public class ChorusPlant_1_12 extends FacingBlock {

    public ChorusPlant_1_12(Vec3 vec3) {
        super(vec3);
    }

    @Override
    public void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/chorus.png");
    }

    @Override
    public void updateColor() {
        setMaterialColor(BlockColors.CHORUS_PLANT.get());
        setSpecularColor(BlockColors.IRON_SPEC.get());
    }

    @Override
    public void updateBoundingBox() {
        this.axisVecTuples = new ArrayList<>();

        calcBase();

        if (BlockSettings.isNorth()) calcNorth();
        if (BlockSettings.isEast()) calcEast();
        if (BlockSettings.isSouth()) calcSouth();
        if (BlockSettings.isWest()) calcWest();

        if (BlockSettings.isNorth() && BlockSettings.isEast()) calcNorthEastCorner();
        if (BlockSettings.isNorth() && BlockSettings.isWest()) calcNorthWestCorner();
        if (BlockSettings.isSouth() && BlockSettings.isEast()) calcSouthEastCorner();
        if (BlockSettings.isSouth() && BlockSettings.isWest()) calcSouthWestCorner();
    }

    protected void calcBase() {
        Vec3 lowerEdge = new Vec3(0.1875, 0, 0.1875);
        Vec3 upperEdge = new Vec3(0.8125, 0.8125, 0.8125);
        Vec3 shift = new Vec3(0.1875, 0.1875 / 2, 0.1875);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcBaseFlip() {

    }

    @Override
    protected void calcNorth() {
        Vec3 lowerEdge = new Vec3(0.1875, 0, 0);
        Vec3 upperEdge = new Vec3(0.8125, 0.8125, 0.5);
        Vec3 shift = new Vec3(0.1875, 0.1875 / 2, 0.25);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcWest() {
        Vec3 lowerEdge = new Vec3(0.5, 0, 0.1875);
        Vec3 upperEdge = new Vec3(1, 0.8125, 0.8125);
        Vec3 shift = new Vec3(0.25, 0.1875 / 2, 0.1875);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcSouth() {
        Vec3 lowerEdge = new Vec3(0.1875, 0, 0.5);
        Vec3 upperEdge = new Vec3(0.8125, 0.8125, 1);
        Vec3 shift = new Vec3(0.1875, 0.1875 / 2, 0.25);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcEast() {
        Vec3 lowerEdge = new Vec3(0, 0, 0.1875);
        Vec3 upperEdge = new Vec3(0.5, 0.8125, 0.8125);
        Vec3 shift = new Vec3(0.25, 0.1875 / 2, 0.1875);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    protected void calcNorthWestCorner() {
        Vec3 lowerEdge = new Vec3(0.1875, 0, 0.0);
        Vec3 upperEdge = new Vec3(1, 0.8125, 0.8125);
        Vec3 shift = new Vec3(0.1875 / 2, 0.1875 / 2, 0.1875 / 2);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    protected void calcNorthEastCorner() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(0.8125, 0.8125, 0.8125);
        Vec3 shift = new Vec3(0.1875 / 2, 0.1875 / 2, 0.1875 / 2);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    protected void calcSouthWestCorner() {
        Vec3 lowerEdge = new Vec3(0.1875, 0, 0.1875);
        Vec3 upperEdge = new Vec3(1, 0.8125, 1);
        Vec3 shift = new Vec3(0.1875 / 2, 0.1875 / 2, 0.1875 / 2);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    protected void calcSouthEastCorner() {
        Vec3 lowerEdge = new Vec3(0, 0, 0.1875);
        Vec3 upperEdge = new Vec3(0.8125, 0.8125, 1);
        Vec3 shift = new Vec3(0.1875 / 2, 0.1875 / 2, 0.1875 / 2);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

}
