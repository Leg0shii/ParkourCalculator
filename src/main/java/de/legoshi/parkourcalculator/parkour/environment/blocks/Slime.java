package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.parkour.simulator.Player;
import de.legoshi.parkourcalculator.util.BlockColors;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Movement;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Slime extends ABlock {

    public Slime(Vec3 vec3) {
        super(vec3);
    }

    @Override
    public void updateColor() {
        setMaterialColor(BlockColors.SLIME.get());
        setSpecularColor(BlockColors.IRON_SPEC.get());
    }

    @Override
    void updateBoundingBox() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(1, 1, 1);
        Vec3 shift = new Vec3(0, 0, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/slime.webp");
    }

    @Override
    public void onLanded(Player player) {
        if (player.isSNEAK()) {
            super.onLanded(player);
        } else if (player.getVelocity().y < 0.0D) {
            player.getVelocity().y = -player.getVelocity().y;
        }
    }

    @Override
    public void updateSlipperiness() {
        this.slipperiness = Movement.Slipperiness.SLIME;
    }

    @Override
    public void onEntityCollidedWithBlock(Player player) {
        if (Math.abs(player.getVelocity().y) < 0.1D && !player.isSNEAK()) {
            double d0 = 0.4D + Math.abs(player.getVelocity().y) * 0.2D;
            player.getVelocity().x *= d0;
            player.getVelocity().z *= d0;
        }
        super.onEntityCollidedWithBlock(player);
    }

}
