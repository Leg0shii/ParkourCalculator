package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.file.BlockData;
import de.legoshi.parkourcalculator.gui.debug.menu.BlockSettings;
import de.legoshi.parkourcalculator.util.BlockColors;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CocoaBean extends FacingBlock implements TierBlock {

    private int tier;
    private double addedSize = 0;

    public CocoaBean(Vec3 vec3) {
        super(vec3);
    }

    @Override
    public void updateColor() {
        setMaterialColor(BlockColors.COCOA_BEAN.get());
        setSpecularColor(BlockColors.PLANT_SPEC.get());
    }

    @Override
    protected void updateBoundingBox() {
        this.tier = BlockSettings.getTier();
        prepareBlock(this.tier);

        if (BlockSettings.isNorth()) calcNorth();
        else if (BlockSettings.isEast()) calcEast();
        else if (BlockSettings.isSouth()) calcSouth();
        else calcWest();
    }

    @Override
    protected void calcSouth() {
        Vec3 lowerEdge = new Vec3(0.375 - addedSize, 0.4325 - addedSize * 2, 0.6875 - addedSize * 2);
        Vec3 upperEdge = new Vec3(0.625 + addedSize, 0.75, 0.9375);
        Vec3 shift = new Vec3(0.375 - addedSize, 0.3425 - addedSize, 0.375 - addedSize);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcWest() {
        Vec3 lowerEdge = new Vec3(0.6875 - addedSize * 2, 0.4325 - addedSize * 2, 0.375 - addedSize);
        Vec3 upperEdge = new Vec3(0.9375, 0.75, 0.625 + addedSize);
        Vec3 shift = new Vec3(0.375 - addedSize, 0.3425 - addedSize, 0.375 - addedSize);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcNorth() {
        Vec3 lowerEdge = new Vec3(0.375 - addedSize, 0.4325 - addedSize * 2, 0.0625);
        Vec3 upperEdge = new Vec3(0.625 + addedSize, 0.75, 0.3125 + addedSize * 2);
        Vec3 shift = new Vec3(0.375 - addedSize, 0.3425 - addedSize, 0.375 - addedSize);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcEast() {
        Vec3 lowerEdge = new Vec3(0.0625, 0.4325 - addedSize * 2, 0.375 - addedSize);
        Vec3 upperEdge = new Vec3(0.3125 + addedSize * 2, 0.75, 0.625 + addedSize);
        Vec3 shift = new Vec3(0.375 - addedSize, 0.3425 - addedSize, 0.375 - addedSize);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcBase() {

    }

    @Override
    protected void calcBaseFlip() {

    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/cocoa_bean.png");
    }

    @Override
    public void prepareBlock(int tier) {
        tier--;
        if (tier > 2) tier = 2;
        this.addedSize = (tier) * 0.0625;
    }

    @Override
    public BlockData toBlockData() {
        BlockData blockData = super.toBlockData();
        blockData.tier = this.tier;
        return blockData;
    }

}
