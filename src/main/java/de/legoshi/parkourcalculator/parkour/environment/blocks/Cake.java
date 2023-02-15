package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.AxisVecTuple;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
public class Cake extends ABlock {

    public Cake(Vec3 vec3) {
        super(vec3);
    }

    @Override
    void updateBoundingBox() {
        Vec3 lowerEdge = getVec3();
        Vec3 upperEdge = getVec3().copy();
        lowerEdge.addVector(0.0625, 0, 0.0625);
        upperEdge.addVector(0.9375, 0.5, 0.9375);

        Vec3 shift = new Vec3(0.0625, 0.25, 0.0625);
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(lowerEdge, upperEdge);
        AxisVecTuple axisVecTuple = new AxisVecTuple(axisAlignedBB, shift);
        this.axisVecTuples = new ArrayList<>();
        this.axisVecTuples.add(axisVecTuple);
    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/cake.png");
    }
}
