package de.legoshi.parkourcalculator.simulation.environment.block_1_12;

import de.legoshi.parkourcalculator.gui.debug.menu.BlockSettings;
import de.legoshi.parkourcalculator.simulation.environment.block.FacingBlock;
import de.legoshi.parkourcalculator.util.BlockColors;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
public class PistonHead_1_12 extends FacingBlock {

    public PistonHead_1_12(Vec3 vec3) {
        super(vec3);
    }

    @Override
    public void updateColor() {
        setMaterialColor(BlockColors.WOOD.get());
        setSpecularColor(BlockColors.WOOD_SPEC.get());
    }

    @Override
    public void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/piston_head.png");
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

    // 0.5x0.25x1
    @Override
    protected void calcBase() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(1, 0.25, 1);
        Vec3 shift = new Vec3(0, 0.375, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));

        Vec3 lowerEdgeRod = new Vec3(0.375, 0, 0.375);
        Vec3 upperEdgeRod = new Vec3(0.625, 1.25, 0.625);
        Vec3 shiftRod = new Vec3(0.375, -0.125, 0.375);
        this.axisVecTuples.add(constructBlock(lowerEdgeRod, upperEdgeRod, shiftRod));
    }

    @Override
    protected void calcBaseFlip() {
        Vec3 lowerEdge = new Vec3(0, 0.75, 0);
        Vec3 upperEdge = new Vec3(1, 1, 1);
        Vec3 shift = new Vec3(0, 0.375, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));

        Vec3 lowerEdgeRod = new Vec3(0.375, 0, 0.375);
        Vec3 upperEdgeRod = new Vec3(0.625, 1.25, 0.625);
        Vec3 shiftRod = new Vec3(0.375, 0.125, 0.375);
        this.axisVecTuples.add(constructBlock(lowerEdgeRod, upperEdgeRod, shiftRod));
    }

    @Override
    protected void calcNorth() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(1, 1, 0.25);
        Vec3 shift = new Vec3(0, 0, 0.375);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));

        Vec3 lowerEdgeRod = new Vec3(0.375, 0.375, 0);
        Vec3 upperEdgeRod = new Vec3(0.625, 0.625, 1.25);
        Vec3 shiftRod = new Vec3(0.375, 0.375, -0.125);
        this.axisVecTuples.add(constructBlock(lowerEdgeRod, upperEdgeRod, shiftRod));
    }

    @Override
    protected void calcWest() {
        Vec3 lowerEdge = new Vec3(0.75, 0, 0);
        Vec3 upperEdge = new Vec3(1, 1, 1);
        Vec3 shift = new Vec3(0.375, 0, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));

        Vec3 lowerEdgeRod = new Vec3(0, 0.375, 0.375);
        Vec3 upperEdgeRod = new Vec3(1.25, 0.625, 0.625);
        Vec3 shiftRod = new Vec3(0.125, 0.375, 0.375);
        this.axisVecTuples.add(constructBlock(lowerEdgeRod, upperEdgeRod, shiftRod));
    }

    @Override
    protected void calcSouth() {
        Vec3 lowerEdge = new Vec3(0, 0, 0.75);
        Vec3 upperEdge = new Vec3(1, 1, 1);
        Vec3 shift = new Vec3(0, 0, 0.375);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));

        Vec3 lowerEdgeRod = new Vec3(0.375, 0.375, 0);
        Vec3 upperEdgeRod = new Vec3(0.625, 0.625, 1.25);
        Vec3 shiftRod = new Vec3(0.375, 0.375, 0.125);
        this.axisVecTuples.add(constructBlock(lowerEdgeRod, upperEdgeRod, shiftRod));
    }

    @Override
    protected void calcEast() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(0.25, 1, 1);
        Vec3 shift = new Vec3(0.375, 0, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));

        Vec3 lowerEdgeRod = new Vec3(0, 0.375, 0.375);
        Vec3 upperEdgeRod = new Vec3(1.25, 0.625, 0.625);
        Vec3 shiftRod = new Vec3(-0.125, 0.375, 0.375);
        this.axisVecTuples.add(constructBlock(lowerEdgeRod, upperEdgeRod, shiftRod));
    }
}
