package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.util.AxisVecTuple;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Snow extends ABlock implements TierBlock {

    private double height = 0.125;

    public Snow(Vec3 vec3) {
        super(vec3);
        this.updateBoundingBox();
    }

    @Override
    void updateBoundingBox() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(1, 0.125, 1);
        Vec3 shift = new Vec3(0, 0.5-0.125/2, 0);
        AxisVecTuple axisVecTuple = constructBlock(lowerEdge, upperEdge, shift);
        this.axisVecTuples.add(axisVecTuple);
    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/snow.webp");
    }

    @Override
    public void prepareBlock(int tier) {
        switch (tier) {
            case 0 -> this.height = 0.125;
            case 1 -> this.height = 0.25;
            case 2 -> this.height = 0.375;
            case 3 -> this.height = 0.5;
            case 4 -> this.height = 0.625;
            case 5 -> this.height = 0.75;
            case 6 -> this.height = 0.875;
            case 7 -> this.height = 1.0;
        }
    }

}
