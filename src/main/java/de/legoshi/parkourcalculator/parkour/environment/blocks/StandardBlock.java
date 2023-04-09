package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.util.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
public class StandardBlock extends ABlock {

    public StandardBlock(Vec3 vec3) {
        super(vec3);
    }

    @Override
    void updateBoundingBox() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(1, 1, 1);
        Vec3 shift = new Vec3(0, 0, 0);
        AxisVecTuple axisVecTuple = constructBlock(lowerEdge, upperEdge, shift);
        this.axisVecTuples.add(axisVecTuple);
    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/stone.webp");
    }

}
