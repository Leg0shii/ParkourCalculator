package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
public class Enderchest extends ABlock {

    public Enderchest(Vec3 vec3) {
        super(vec3);
    }

    @Override
    void updateBoundingBox() {

        Vec3 lowerEdge = getVec3();
        lowerEdge.subtract(0.0625, 0, 0.0625);
        Vec3 upperEdge = getVec3().copy();
        upperEdge.addVector(0.875, 0.9375, 0.875);

        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(lowerEdge, upperEdge);
        this.axisAlignedBBS = new ArrayList<>();
        this.axisAlignedBBS.add(axisAlignedBB);
    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/enderchest.jpeg");
    }
}
