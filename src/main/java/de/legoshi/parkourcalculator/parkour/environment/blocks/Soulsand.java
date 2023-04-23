package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.parkour.simulator.Player;
import de.legoshi.parkourcalculator.util.BlockColors;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Soulsand extends ABlock {

    public Soulsand(Vec3 vec3) {
        super(vec3);
    }

    @Override
    public void updateColor() {
        setMaterialColor(BlockColors.SOUL_SAND.get());
        setSpecularColor(BlockColors.PLANT_SPEC.get());
    }

    @Override
    void updateBoundingBox() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(1, 0.875, 1);
        Vec3 shift = new Vec3(0, 0.125/2, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/soulsand.png");
    }

    @Override
    public void onEntityCollidedWithBlock(Player player) {
        player.getVelocity().x *= 0.4;
        player.getVelocity().z *= 0.4;
    }

}
