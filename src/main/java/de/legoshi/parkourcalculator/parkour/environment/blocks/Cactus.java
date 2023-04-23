package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.util.BlockColors;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Cactus extends ABlock {

    public Cactus(Vec3 vec3) {
        super(vec3);
    }

    @Override
    public void updateColor() {
        setMaterialColor(BlockColors.CACTUS.get());
        setSpecularColor(BlockColors.PLANT_SPEC.get());
    }

    @Override
    void updateBoundingBox() {
        Vec3 lowerEdge = new Vec3(0.0625, 0, 0.0625);
        Vec3 upperEdge = new Vec3(0.9375, 0.9375, 0.9375);
        Vec3 shift = new Vec3(0.0625, 0.0625/2, 0.0625);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/cactus.png");
    }
}
