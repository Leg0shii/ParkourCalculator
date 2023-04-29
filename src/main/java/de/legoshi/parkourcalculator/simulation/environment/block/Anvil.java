package de.legoshi.parkourcalculator.simulation.environment.block;

import de.legoshi.parkourcalculator.gui.debug.menu.BlockSettings;
import de.legoshi.parkourcalculator.util.BlockColors;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
public class Anvil extends FacingBlock {

    public Anvil(Vec3 vec3) {
        super(vec3);
    }

    @Override
    protected void calcBase() {

    }

    @Override
    protected void calcBaseFlip() {

    }

    @Override
    public void updateBoundingBox() {
        this.axisVecTuples = new ArrayList<>();

        if (BlockSettings.isNorth() || BlockSettings.isSouth()) calcNorth();
        else if (BlockSettings.isEast() || BlockSettings.isWest()) calcEast();
        else calcNorth();
    }

    @Override
    public void updateColor() {
        setMaterialColor(BlockColors.DARK_IRON.get());
        setSpecularColor(BlockColors.IRON_SPEC.get());
    }

    @Override
    protected void calcNorth() {
        Vec3 lowerEdge = new Vec3(0.125, 0, 0);
        Vec3 upperEdge = new Vec3(0.875, 1, 1);
        Vec3 shift = new Vec3(0.125, 0, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcEast() {
        Vec3 lowerEdge = new Vec3(0, 0, 0.125);
        Vec3 upperEdge = new Vec3(1, 1, 0.875);
        Vec3 shift = new Vec3(0, 0, 0.125);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcSouth() {

    }

    @Override
    protected void calcWest() {

    }

    @Override
    public void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/anvil.png");
    }

}
