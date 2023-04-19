package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.gui.debug.menu.BlockSettings;
import de.legoshi.parkourcalculator.util.BlockColors;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
public class Stair extends FacingBlock {

    public Stair(Vec3 vec3) {
        super(vec3);
    }

    @Override
    public void updateColor() {
        setColor(BlockColors.STONE.get());
    }

    @Override
    protected void updateBoundingBox() {
        this.axisVecTuples = new ArrayList<>();

        if (!BlockSettings.isFlip()) calcBase();
        else calcBaseFlip();

        if (BlockSettings.isNorth()) calcNorth();
        if (BlockSettings.isEast()) calcEast();
        if (BlockSettings.isSouth()) calcSouth();
        if (BlockSettings.isWest()) calcWest();
    }

    @Override
    protected void calcBase() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(1, 0.5, 1);
        Vec3 shift = new Vec3(0, 0.25, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcBaseFlip() {
        Vec3 lowerEdge = new Vec3(0, 0.5, 0);
        Vec3 upperEdge = new Vec3(1, 1, 1);
        Vec3 shift = new Vec3(0, 0.25, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcNorth() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(1, 1, 0.5);
        Vec3 shift = new Vec3(0, 0, 0.25);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcWest() {
        Vec3 lowerEdge = new Vec3(0.5, 0, 0);
        Vec3 upperEdge = new Vec3(1, 1, 1);
        Vec3 shift = new Vec3(0.25, 0, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcSouth() {
        Vec3 lowerEdge = new Vec3(0, 0, 0.5);
        Vec3 upperEdge = new Vec3(1, 1, 1);
        Vec3 shift = new Vec3(0, 0, 0.25);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcEast() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(0.5, 1, 1);
        Vec3 shift = new Vec3(0.25, 0, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/stair.webp");
    }
}
