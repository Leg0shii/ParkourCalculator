package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.gui.debug.menu.BlockSettings;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Cake extends ABlock implements TierBlock {

    private double widthReduce = 0;

    public Cake(Vec3 vec3) {
        super(vec3);
    }

    @Override
    void updateBoundingBox() {
        prepareBlock(BlockSettings.getTier());

        Vec3 lowerEdge = new Vec3(0.0625+this.widthReduce, 0, 0.0625);
        Vec3 upperEdge = new Vec3(0.9375, 0.5, 0.9375);
        Vec3 shift = new Vec3(0.0625+this.widthReduce/2, 0.25, 0.0625);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/cake.webp");
    }

    @Override
    public void prepareBlock(int tier) {
        switch (tier) {
            case 0 -> this.widthReduce = 0;
            case 1 -> this.widthReduce = 0.125;
            case 2 -> this.widthReduce = 0.25;
            case 3 -> this.widthReduce = 0.375;
            case 4 -> this.widthReduce = 0.5;
            case 5 -> this.widthReduce = 0.625;
            case 6 -> this.widthReduce = 0.75;
        }
    }
}
