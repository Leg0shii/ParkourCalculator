package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CocoaBean extends FacingBlock {
    // a0: 0.25x0.3125x0.25, a1: 0.375x0.4375x0.375, a2: 0.5x0.5625x0.5
    private double tier = 0;
    private double addedSize = tier * 0.0625;

    public CocoaBean(Vec3 vec3) {
        super(vec3);
    }

    @Override
    protected void calcNorth() {
        this.tier = 2;
        this.addedSize = tier * 0.0625;
        Vec3 lowerEdge = new Vec3(0.375 - addedSize, 0.4325 - addedSize * 2, 0.6875 - addedSize * 2);
        Vec3 upperEdge = new Vec3(0.625 + addedSize, 0.75, 0.9375);
        Vec3 shift = new Vec3(0.375 - addedSize, 0.3425 - addedSize, 0.375 - addedSize);
        this.north = constructBlock(lowerEdge, upperEdge, shift);
    }

    @Override
    protected void calcEast() {
        Vec3 lowerEdge = new Vec3(0.6875 - addedSize * 2, 0.4325 - addedSize * 2, 0.375 - addedSize);
        Vec3 upperEdge = new Vec3(0.9375, 0.75, 0.625 + addedSize);
        Vec3 shift = new Vec3(0.375 - addedSize, 0.3425 - addedSize, 0.375 - addedSize);
        this.east = constructBlock(lowerEdge, upperEdge, shift);
    }

    @Override
    protected void calcSouth() {
        Vec3 lowerEdge = new Vec3(0.375 - addedSize, 0.4325 - addedSize * 2, 0.0625);
        Vec3 upperEdge = new Vec3(0.625 + addedSize, 0.75, 0.3125 + addedSize * 2);
        Vec3 shift = new Vec3(0.375 - addedSize, 0.3425 - addedSize, 0.375 - addedSize);
        this.south = constructBlock(lowerEdge, upperEdge, shift);
    }

    @Override
    protected void calcWest() {
        Vec3 lowerEdge = new Vec3(0.0625, 0.4325 - addedSize * 2, 0.375 - addedSize);
        Vec3 upperEdge = new Vec3(0.3125 + addedSize * 2, 0.75, 0.625 + addedSize);
        Vec3 shift = new Vec3(0.375 - addedSize, 0.3425 - addedSize, 0.375 - addedSize);
        this.west = constructBlock(lowerEdge, upperEdge, shift);
    }

    @Override
    protected void calcBase() {

    }

    @Override
    protected void calcBaseFlip() {

    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/cocoa_bean.webp");
    }

}
