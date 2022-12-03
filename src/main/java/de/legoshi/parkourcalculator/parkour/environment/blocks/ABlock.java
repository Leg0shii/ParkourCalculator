package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.Movement;
import de.legoshi.parkourcalculator.util.Vec3;
import javafx.scene.image.Image;
import javafx.scene.shape.Box;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public abstract class ABlock {

    public Image image;
    public ArrayList<AxisAlignedBB> axisAlignedBBS;
    public Movement.Slipperiness slipperiness;

    public ArrayList<Box> boxesArrayList;
    private Vec3 vec3;

    public ABlock(Vec3 vec3) {
        this.vec3 = vec3;
        updateBoundingBox();
        updateBoxes();
        updateImage();
        updateSlipperiness();
    }

    abstract void updateBoundingBox();
    abstract void updateSlipperiness();
    abstract void updateImage();

    private void updateBoxes() {
        this.boxesArrayList = new ArrayList<>();
        for (AxisAlignedBB axisAlignedBB : this.axisAlignedBBS) {
            this.boxesArrayList.add(axisAlignedBB.getBox());
        }
    }

}
