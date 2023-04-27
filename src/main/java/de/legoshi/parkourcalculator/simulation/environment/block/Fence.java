package de.legoshi.parkourcalculator.simulation.environment.block;

import de.legoshi.parkourcalculator.gui.debug.menu.BlockSettings;
import de.legoshi.parkourcalculator.util.BlockColors;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
public class Fence extends FacingBlock {

    public Fence(Vec3 vec3) {
        super(vec3);
    }

    @Override
    public void updateColor() {
        setMaterialColor(BlockColors.WOOD.get());
        setSpecularColor(BlockColors.WOOD_SPEC.get());
    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/fence.png");
    }

    @Override
    protected void updateBoundingBox() {
        this.axisVecTuples = new ArrayList<>();
        calcBase();
        if (BlockSettings.isNorth()) calcNorth();
        if (BlockSettings.isEast()) calcEast();
        if (BlockSettings.isSouth()) calcSouth();
        if (BlockSettings.isWest()) calcWest();
    }

    protected void calcBase() {
        Vec3 lowerEdge = new Vec3(0.375, 0, 0.375);
        Vec3 upperEdge = new Vec3(0.625, 1.5, 0.625);
        Vec3 shift = new Vec3(0.375, -0.25, 0.375);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcBaseFlip() {

    }

    @Override
    protected void calcNorth() {
        Vec3 lowerEdge = new Vec3(0.375, 0, 0);
        Vec3 upperEdge = new Vec3(0.625, 1.5, 0.5);
        Vec3 shift = new Vec3(0.375, -0.25, 0.25);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcWest() {
        Vec3 lowerEdge = new Vec3(0.5, 0, 0.375);
        Vec3 upperEdge = new Vec3(1, 1.5, 0.625);
        Vec3 shift = new Vec3(0.25, -0.25, 0.375);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcSouth() {
        Vec3 lowerEdge = new Vec3(0.375, 0, 0.5);
        Vec3 upperEdge = new Vec3(0.625, 1.5, 1);
        Vec3 shift = new Vec3(0.375, -0.25, 0.25);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcEast() {
        Vec3 lowerEdge = new Vec3(0, 0, 0.375);
        Vec3 upperEdge = new Vec3(0.5, 1.5, 0.625);
        Vec3 shift = new Vec3(0.25, -0.25, 0.375);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

}
