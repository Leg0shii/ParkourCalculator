package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.gui.debug.menu.BlockSettings;
import de.legoshi.parkourcalculator.util.BlockColors;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
public class PistonHead extends FacingBlock {

    public PistonHead(Vec3 vec3) {
        super(vec3);
    }

    @Override
    public void updateColor() {
        setMaterialColor(BlockColors.WOOD.get());
        setSpecularColor(BlockColors.WOOD_SPEC.get());
    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/piston_head.png");
    }

    @Override
    protected void updateBoundingBox() {
        this.axisVecTuples = new ArrayList<>();

        if (BlockSettings.isNorth()) calcNorth();
        else if (BlockSettings.isEast()) calcEast();
        else if (BlockSettings.isSouth()) calcSouth();
        else if (BlockSettings.isWest()) calcWest();
        else if (BlockSettings.isFlip()) calcBaseFlip();
        else if (BlockSettings.isFloor()) calcBase();
    }

    // 0.5x0.25x1
    @Override
    protected void calcBase() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(1, 0.25, 1);
        Vec3 shift = new Vec3(0, 0.375, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));

        Vec3 lowerEdgeRod = new Vec3(0.375, 0, 0.375);
        Vec3 upperEdgeRod = new Vec3(0.625, 1, 0.625);
        Vec3 shiftRod = new Vec3(0.375, 0, 0.375);
        this.axisVecTuples.add(constructBlock(lowerEdgeRod, upperEdgeRod, shiftRod));
    }

    @Override
    protected void calcBaseFlip() {
        Vec3 lowerEdge = new Vec3(0, 0.75, 0);
        Vec3 upperEdge = new Vec3(1, 1, 1);
        Vec3 shift = new Vec3(0, 0.375, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));

        Vec3 lowerEdgeRod = new Vec3(0.375, 0, 0.375);
        Vec3 upperEdgeRod = new Vec3(0.625, 1, 0.625);
        Vec3 shiftRod = new Vec3(0.375, 0, 0.375);
        this.axisVecTuples.add(constructBlock(lowerEdgeRod, upperEdgeRod, shiftRod));
    }

    @Override
    protected void calcNorth() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(1, 1, 0.25);
        Vec3 shift = new Vec3(0, 0, 0.375);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));

        Vec3 lowerEdgeRod = new Vec3(0.25, 0.375, 0);
        Vec3 upperEdgeRod = new Vec3(0.75, 0.625, 1);
        Vec3 shiftRod = new Vec3(0.25, 0.375, 0);
        this.axisVecTuples.add(constructBlock(lowerEdgeRod, upperEdgeRod, shiftRod));
    }

    @Override
    protected void calcWest() {
        Vec3 lowerEdge = new Vec3(0.75, 0, 0);
        Vec3 upperEdge = new Vec3(1, 1, 1);
        Vec3 shift = new Vec3(0.375, 0, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));

        Vec3 lowerEdgeRod = new Vec3(0.375, 0.375, 0.25);
        Vec3 upperEdgeRod = new Vec3(1, 0.625, 1);
        Vec3 shiftRod = new Vec3(0.375/2, 0.375, 0.25/2);
        this.axisVecTuples.add(constructBlock(lowerEdgeRod, upperEdgeRod, shiftRod));
    }

    @Override
    protected void calcSouth() {
        Vec3 lowerEdge = new Vec3(0, 0, 0.75);
        Vec3 upperEdge = new Vec3(1, 1, 1);
        Vec3 shift = new Vec3(0, 0, 0.375);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));

        Vec3 lowerEdgeRod = new Vec3(0.25, 0.375, 0);
        Vec3 upperEdgeRod = new Vec3(0.75, 0.625, 1);
        Vec3 shiftRod = new Vec3(0.25, 0.375, 0);
        this.axisVecTuples.add(constructBlock(lowerEdgeRod, upperEdgeRod, shiftRod));
    }

    @Override
    protected void calcEast() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(0.25, 1, 1);
        Vec3 shift = new Vec3(0.375, 0, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));

        Vec3 lowerEdgeRod = new Vec3(0, 0.375, 0.25);
        Vec3 upperEdgeRod = new Vec3(1, 0.625, 0.75);
        Vec3 shiftRod = new Vec3(0, 0.375, 0.25);
        this.axisVecTuples.add(constructBlock(lowerEdgeRod, upperEdgeRod, shiftRod));
    }
}
