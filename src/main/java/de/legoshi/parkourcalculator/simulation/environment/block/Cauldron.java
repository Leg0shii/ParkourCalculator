package de.legoshi.parkourcalculator.simulation.environment.block;

import de.legoshi.parkourcalculator.util.BlockColors;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Cauldron extends ABlock {

    public Cauldron(Vec3 vec3) {
        super(vec3);
    }

    @Override
    public void updateColor() {
        setMaterialColor(BlockColors.DARK_IRON.get());
        setSpecularColor(BlockColors.IRON_SPEC.get());
    }

    @Override
    void updateBoundingBox() {
        Vec3 lowerEdge1 = new Vec3(0, 0, 0);
        Vec3 upperEdge1 = new Vec3(1, 1, 0.125);
        Vec3 shift1 = new Vec3(0, 0, 0.4375);
        this.axisVecTuples.add(constructBlock(lowerEdge1, upperEdge1, shift1));

        Vec3 lowerEdge2 = new Vec3(0.875, 0, 0);
        Vec3 upperEdge2 = new Vec3(1, 1, 1);
        Vec3 shift2 = new Vec3(0.4375, 0, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge2, upperEdge2, shift2));

        Vec3 lowerEdge3 = new Vec3(0, 0, 0.875);
        Vec3 upperEdge3 = new Vec3(1, 1, 1);
        Vec3 shift3 = new Vec3(0, 0, 0.4375);
        this.axisVecTuples.add(constructBlock(lowerEdge3, upperEdge3, shift3));

        Vec3 lowerEdge4 = new Vec3(0, 0, 0);
        Vec3 upperEdge4 = new Vec3(0.125, 1, 1);
        Vec3 shift4 = new Vec3(0.4375, 0, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge4, upperEdge4, shift4));

        Vec3 lowerEdgeBase = new Vec3(0, 0, 0);
        Vec3 upperEdgeBase = new Vec3(1, 0.3125, 1);
        Vec3 shiftBase = new Vec3(0, 0.5-0.3125/2, 0);
        this.axisVecTuples.add(constructBlock(lowerEdgeBase, upperEdgeBase, shiftBase));
    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/cauldron.png");
    }

}
