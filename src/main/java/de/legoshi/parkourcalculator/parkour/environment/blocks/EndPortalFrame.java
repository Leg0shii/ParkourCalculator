package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.gui.debug.menu.BlockSettings;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EndPortalFrame extends ABlock implements TierBlock {

    private int tier;

    public EndPortalFrame(Vec3 vec3) {
        super(vec3);
    }

    @Override
    void updateBoundingBox() {
        this.tier = BlockSettings.getTier();
        prepareBlock(tier);

        Vec3 lowerEdgeBase = new Vec3(0, 0, 0);
        Vec3 upperEdgeBase = new Vec3(1, 0.875, 1);
        Vec3 shiftBase = new Vec3(0, 0.0625, 0);
        this.axisVecTuples.add(constructBlock(lowerEdgeBase, upperEdgeBase, shiftBase));

        if (tier > 1) {
            Vec3 lowerEdgeEye = new Vec3(0.3215, 0, 0.3215);
            Vec3 upperEdgeEye = new Vec3(0.6875, 1, 0.6875);
            Vec3 shiftEye = new Vec3(0.3215, 0, 0.3215);
            this.axisVecTuples.add(constructBlock(lowerEdgeEye, upperEdgeEye, shiftEye));
        }
    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/end_portal_frame.webp");
    }

    @Override
    public void prepareBlock(int tier) {
        this.tier = tier;
    }
}
