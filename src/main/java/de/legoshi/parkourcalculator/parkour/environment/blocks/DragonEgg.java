package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DragonEgg extends ABlock {
    public DragonEgg(Vec3 vec3) {
        super(vec3);
    }

    @Override
    void updateBoundingBox() {
        Vec3 lowerEdge = new Vec3(0.0625, 0, 0.0625);
        Vec3 upperEdge = new Vec3(0.9375, 1, 0.9375);
        Vec3 shift = new Vec3(0.0625, 0, 0.0625);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/dragon_egg.webp");
    }
}
