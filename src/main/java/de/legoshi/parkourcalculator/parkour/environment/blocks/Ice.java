package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Ice extends ABlock {
    public Ice(Vec3 vec3) {
        super(vec3);
    }

    @Override
    void updateBoundingBox() {

    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/ice.webp");
    }
}
