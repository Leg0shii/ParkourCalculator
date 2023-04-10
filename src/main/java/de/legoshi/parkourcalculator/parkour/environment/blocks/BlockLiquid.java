package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BlockLiquid extends ABlock {

    public int waterLevel = 8;

    public BlockLiquid(Vec3 vec3) {
        super(vec3);
    }

    @Override
    void updateBoundingBox() {

    }

    @Override
    void updateImage() {

    }

    public static float getLiquidHeightPercent(int meta) {
        if (meta >= 8) {
            meta = 0;
        }
        return (float) (meta + 1) / 9.0F;
    }

    public AxisAlignedBB getBB() {
        return this.axisVecTuples.get(0).getBb();
    }

}
