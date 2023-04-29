package de.legoshi.parkourcalculator.simulation.environment.block;

import de.legoshi.parkourcalculator.simulation.player.Player;
import de.legoshi.parkourcalculator.simulation.player.Player_1_8;
import de.legoshi.parkourcalculator.util.BlockColors;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Cobweb extends ABlock {

    public Cobweb(Vec3 vec3) {
        super(vec3);
    }

    @Override
    public void updateColor() {
        setMaterialColor(BlockColors.COBWEB.get());
        setSpecularColor(BlockColors.PLANT_SPEC.get());
    }

    @Override
    public void updateBoundingBox() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(1, 1, 1);
        Vec3 shift = new Vec3(0, 0, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    public void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/cobweb.png");
    }

    @Override
    public void onEntityCollidedWithBlock(Player player) {
        player.setWEB(true);
    }
}
