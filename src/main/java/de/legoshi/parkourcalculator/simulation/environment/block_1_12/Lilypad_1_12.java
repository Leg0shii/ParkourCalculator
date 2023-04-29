package de.legoshi.parkourcalculator.simulation.environment.block_1_12;

import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import de.legoshi.parkourcalculator.util.BlockColors;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Lilypad_1_12 extends ABlock {

    public Lilypad_1_12(Vec3 vec3) {
        super(vec3);
    }

    @Override
    public void updateColor() {
        setMaterialColor(BlockColors.PLANT.get());
        setSpecularColor(BlockColors.PLANT_SPEC.get());
    }

    @Override
    public void updateBoundingBox() {
        Vec3 lowerEdge = new Vec3(0.0625, 0, 0.0625);
        Vec3 upperEdge = new Vec3(0.9375, 0.09375, 0.9375);
        Vec3 shift = new Vec3(0, 0.5-0.09375/2, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    public void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/lilypad.png");
    }

}
