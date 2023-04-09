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

    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/dragon_egg.webp");
    }
}
