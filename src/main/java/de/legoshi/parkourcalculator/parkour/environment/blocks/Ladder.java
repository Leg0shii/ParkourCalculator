package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.util.BlockColors;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Ladder extends FacingBlock {

    public Ladder(Vec3 vec3) {
        super(vec3);
    }

    @Override
    public void updateColor() {
        setMaterialColor(BlockColors.WOOD.get());
        setSpecularColor(BlockColors.WOOD_SPEC.get());
    }

    @Override
    protected void calcBase() {

    }

    @Override
    protected void calcBaseFlip() {

    }

    @Override
    protected void calcSouth() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(1, 1, 0.125);
        Vec3 shift = new Vec3(0, 0, 0.4375);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcEast() {
        Vec3 lowerEdge = new Vec3(0.875, 0, 0);
        Vec3 upperEdge = new Vec3(1, 1, 1);
        Vec3 shift = new Vec3(0.4375, 0, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcNorth() {
        Vec3 lowerEdge = new Vec3(0, 0, 0.875);
        Vec3 upperEdge = new Vec3(1, 1, 1);
        Vec3 shift = new Vec3(0, 0, 0.4375);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcWest() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(0.125, 1, 1);
        Vec3 shift = new Vec3(0.4375, 0, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/ladder.png");
    }

}
