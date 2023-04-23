package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.util.BlockColors;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Flowerpot extends ABlock {

    public Flowerpot(Vec3 vec3) {
        super(vec3);
    }

    @Override
    public void updateColor() {
        setMaterialColor(BlockColors.FLOWER_POT.get());
        setSpecularColor(BlockColors.WOOD_SPEC.get());
    }

    @Override
    void updateBoundingBox() {
        Vec3 lowerEdge = new Vec3(0.3215, 0, 0.3215);
        Vec3 upperEdge = new Vec3(0.6875, 0.375, 0.6875);
        Vec3 shift = new Vec3(0.3215, 0.625/2, 0.3215);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/flowerpot.png");
    }
}
