package de.legoshi.parkourcalculator.simulation.environment.block;

import de.legoshi.parkourcalculator.util.BlockColors;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Enderchest extends ABlock {

    public Enderchest(Vec3 vec3) {
        super(vec3);
    }

    @Override
    public void updateColor() {
        setMaterialColor(BlockColors.ENDER_CHEST.get());
        setSpecularColor(BlockColors.STONE_SPEC.get());
    }

    @Override
    public void updateBoundingBox() {
        Vec3 lowerEdge = new Vec3(0.0625, 0, 0.0625);
        Vec3 upperEdge = new Vec3(0.9375, 0.875, 0.9375);
        Vec3 shift = new Vec3(0.0625, 0.0625, 0.0625);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    public void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/enderchest.png");
    }
}
