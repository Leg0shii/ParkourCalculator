package de.legoshi.parkourcalculator.simulation.environment.block_1_12;

import de.legoshi.parkourcalculator.gui.debug.menu.BlockSettings;
import de.legoshi.parkourcalculator.simulation.environment.block.FacingBlock;
import de.legoshi.parkourcalculator.util.BlockColors;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
public class EndRod_1_12 extends FacingBlock {

    public EndRod_1_12(Vec3 vec3) {
        super(vec3);
    }

    @Override
    public void updateBoundingBox() {
        this.axisVecTuples = new ArrayList<>();

        if (BlockSettings.isNorth() || BlockSettings.isSouth()) calcNorth();
        else if (BlockSettings.isEast() || BlockSettings.isWest()) calcEast();
        else calcBase();
    }

    @Override
    public void updateColor() {
        setMaterialColor(BlockColors.END_ROD.get());
        setSpecularColor(BlockColors.IRON_SPEC.get());
    }

    @Override
    protected void calcNorth() {
        Vec3 lowerEdge = new Vec3(0.375, 0.375, 0);
        Vec3 upperEdge = new Vec3(0.625, 0.625, 1);
        Vec3 shift = new Vec3(0.375, 0.375, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcEast() {
        Vec3 lowerEdge = new Vec3(0, 0.375, 0.375);
        Vec3 upperEdge = new Vec3(1, 0.625, 0.625);
        Vec3 shift = new Vec3(0, 0.375, 0.375);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    public void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/endrod.png");
    }

    @Override
    protected void calcBase() {
        Vec3 lowerEdge = new Vec3(0.375, 0, 0.375);
        Vec3 upperEdge = new Vec3(0.625, 1, 0.625);
        Vec3 shift = new Vec3(0.375, 0, 0.375);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcBaseFlip() {

    }

    @Override
    protected void calcSouth() {

    }

    @Override
    protected void calcWest() {

    }

}
