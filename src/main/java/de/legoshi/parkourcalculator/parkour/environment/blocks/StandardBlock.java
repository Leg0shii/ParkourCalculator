package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.gui.MinecraftScreen;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Movement;
import de.legoshi.parkourcalculator.util.Vec3;
import java.util.ArrayList;

public class StandardBlock extends ABlock {

    public StandardBlock(Vec3 vec3) {
        super(vec3);
    }

    @Override
    void updateBoundingBox() {
        Vec3 lowerEdge = getVec3();
        Vec3 upperEdge = getVec3().copy();
        upperEdge.addVector(1.0, 1.0, 1.0);

        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(lowerEdge, upperEdge);
        this.axisAlignedBBS = new ArrayList<>();
        this.axisAlignedBBS.add(axisAlignedBB);
    }

    @Override
    void updateSlipperiness() {
        this.slipperiness = Movement.Slipperiness.BLOCK;
    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/grass_block.png");
    }

}
