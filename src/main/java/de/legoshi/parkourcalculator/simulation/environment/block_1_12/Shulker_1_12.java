package de.legoshi.parkourcalculator.simulation.environment.block_1_12;

import de.legoshi.parkourcalculator.gui.debug.menu.BlockSettings;
import de.legoshi.parkourcalculator.simulation.environment.block.FacingBlock;
import de.legoshi.parkourcalculator.util.BlockColors;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
public class Shulker_1_12 extends FacingBlock {

    public Shulker_1_12(Vec3 vec3) {
        super(vec3);
    }

    @Override
    public void updateBoundingBox() {
        this.axisVecTuples = new ArrayList<>();

        if (BlockSettings.isNorth()) calcNorth();
        else if (BlockSettings.isEast()) calcEast();
        else if (BlockSettings.isSouth()) calcSouth();
        else if (BlockSettings.isWest()) calcWest();
        else if (BlockSettings.isFlip()) calcBaseFlip();
        else if (BlockSettings.isFloor()) calcBase();

        if (this.axisVecTuples.isEmpty()) calcSouth();
    }

    @Override
    public void updateColor() {
        setMaterialColor(BlockColors.SHULKER.get());
        setSpecularColor(BlockColors.WOOD_SPEC.get());
    }

    @Override
    protected void calcBase() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(1, 1.5, 1);
        Vec3 shift = new Vec3(0, -0.25, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcBaseFlip() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(1, 1.5, 1);
        Vec3 shift = new Vec3(0, 0.25, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcNorth() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(1, 1, 1.5);
        Vec3 shift = new Vec3(0, 0, -0.25);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcWest() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(1.5, 1, 1);
        Vec3 shift = new Vec3(0.25, 0, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcSouth() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(1, 1, 1.5);
        Vec3 shift = new Vec3(0, 0, 0.25);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcEast() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(1.5, 1, 1);
        Vec3 shift = new Vec3(-0.25, 0, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    public void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/shulker.png");
    }

}
