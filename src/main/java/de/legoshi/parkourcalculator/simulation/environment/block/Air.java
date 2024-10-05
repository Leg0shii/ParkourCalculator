package de.legoshi.parkourcalculator.simulation.environment.block;

import de.legoshi.parkourcalculator.util.BlockColors;
import de.legoshi.parkourcalculator.util.Vec3;

public class Air extends ABlock {

    private static final Air instance = new Air(new Vec3(0, 0, 0));

    public Air(Vec3 vec3) {
        super(vec3);
    }

    public static ABlock getInstance() {
        return instance;
    }

    @Override
    public void updateColor() {
        setMaterialColor(BlockColors.A_STAR_PATH.get());
    }

    @Override
    public void updateBoundingBox() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(1, 1, 1);
        Vec3 shift = new Vec3(0, 0, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    public void updateImage() {

    }

}
