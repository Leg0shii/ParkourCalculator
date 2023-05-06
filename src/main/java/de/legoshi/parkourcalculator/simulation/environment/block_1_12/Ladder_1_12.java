package de.legoshi.parkourcalculator.simulation.environment.block_1_12;

import de.legoshi.parkourcalculator.simulation.environment.block.Ladder;
import de.legoshi.parkourcalculator.util.BlockColors;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Ladder_1_12 extends Ladder {

    public Ladder_1_12(Vec3 vec3) {
        super(vec3);
    }

    @Override
    public void updateColor() {
        setMaterialColor(BlockColors.WOOD.get());
        setSpecularColor(BlockColors.WOOD_SPEC.get());
    }

    @Override
    public void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/ladder.png");
    }

    @Override
    protected void calcBase() {

    }

    @Override
    protected void calcBaseFlip() {

    }

    @Override
    protected void calcNorth() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(1, 1, 0.1875);
        Vec3 shift = new Vec3(0, 0, 0.40625);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcEast() {
        Vec3 lowerEdge = new Vec3(0.8125, 0, 0);
        Vec3 upperEdge = new Vec3(1, 1, 1);
        Vec3 shift = new Vec3(0.40625, 0, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcSouth() {
        Vec3 lowerEdge = new Vec3(0, 0, 0.8125);
        Vec3 upperEdge = new Vec3(1, 1, 1);
        Vec3 shift = new Vec3(0, 0, 0.40625);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcWest() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(0.1875, 1, 1);
        Vec3 shift = new Vec3(0.40625, 0, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

}
