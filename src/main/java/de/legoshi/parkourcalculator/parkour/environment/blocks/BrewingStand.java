package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.util.BlockColors;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BrewingStand extends ABlock {

    public BrewingStand(Vec3 vec3) {
        super(vec3);
    }

    @Override
    public void updateColor() {
        setMaterialColor(BlockColors.BREWING_STAND.get());
        setSpecularColor(BlockColors.WOOD.get());
    }

    @Override
    void updateBoundingBox() {
        Vec3 lowerEdgeBase = new Vec3(0, 0, 0);
        Vec3 upperEdgeBase = new Vec3(1, 0.125, 1);
        Vec3 shiftBase = new Vec3(0, 0.5-0.125/2, 0);
        this.axisVecTuples.add(constructBlock(lowerEdgeBase, upperEdgeBase, shiftBase));

        Vec3 lowerEdgeRod = new Vec3(0.4375, 0, 0.4375);
        Vec3 upperEdgeRod = new Vec3(0.5625, 0.875, 0.5625);
        Vec3 shiftRod = new Vec3(0.4375, 0.125/2, 0.4375);
        this.axisVecTuples.add(constructBlock(lowerEdgeRod, upperEdgeRod, shiftRod));
    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/brewing_stand.webp");
    }
}
