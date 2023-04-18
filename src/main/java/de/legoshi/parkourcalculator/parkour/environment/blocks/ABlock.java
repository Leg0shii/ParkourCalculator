package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.file.BlockData;
import de.legoshi.parkourcalculator.gui.debug.menu.BlockSettings;
import de.legoshi.parkourcalculator.parkour.simulator.Player;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.AxisVecTuple;
import de.legoshi.parkourcalculator.util.Movement;
import de.legoshi.parkourcalculator.util.Vec3;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public abstract class ABlock {

    public Image image;
    public ArrayList<AxisVecTuple> axisVecTuples;
    public Movement.Slipperiness slipperiness;

    public ArrayList<Box> boxesArrayList;
    private Vec3 vec3;
    private Color color;

    public ABlock() {
        updateImage();
        updateSlipperiness();
    }

    public ABlock(Vec3 vec3) {
        this.vec3 = vec3;
        this.axisVecTuples = new ArrayList<>();
        this.boxesArrayList = new ArrayList<>();
        this.color = BlockSettings.getColor();
        updateBoundingBox();
        updateBoxes();
        updateSlipperiness();
    }

    abstract void updateBoundingBox();

    public void updateSlipperiness() {
        this.slipperiness = Movement.Slipperiness.BLOCK;
    }
    abstract void updateImage();

    private void updateBoxes() {
        for (AxisVecTuple axisVecTuple : this.axisVecTuples) {
            this.boxesArrayList.add(axisVecTuple.getBox());
        }
    }

    public AxisVecTuple constructBlock(Vec3 lE, Vec3 uE, Vec3 shift) {
        Vec3 lEdge = getVec3().copy();
        Vec3 uEdge = getVec3().copy();
        lEdge.addVector(lE.x, lE.y, lE.z);
        uEdge.addVector(uE.x, uE.y, uE.z);

        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(lEdge, uEdge);
        return new AxisVecTuple(axisAlignedBB, shift);
    }

    public BlockData toBlockData() {
        BlockData blockData = new BlockData();
        blockData.blockType = this.getClass().getSimpleName();
        blockData.pos = this.vec3;
        blockData.color = this.color;
        return blockData;
    }

    public void onLanded(Player player) {
        player.getVelocity().y = 0.0D;
    }

}
