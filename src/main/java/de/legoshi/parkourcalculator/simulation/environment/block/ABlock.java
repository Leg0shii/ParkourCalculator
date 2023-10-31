package de.legoshi.parkourcalculator.simulation.environment.block;

import de.legoshi.parkourcalculator.file.BlockData;
import de.legoshi.parkourcalculator.simulation.player.Player;
import de.legoshi.parkourcalculator.simulation.player.Player_1_8;
import de.legoshi.parkourcalculator.util.*;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public abstract class ABlock {

    public Image image;
    public ArrayList<AxisVecTuple> axisVecTuples;
    public Movement.Slipperiness slipperiness;

    public ArrayList<Box> boxesArrayList;
    private Vec3 vec3;

    private Color materialColor;
    private Color specularColor;
    private int specularPower;

    public ABlock() {
        updateImage();
        updateSlipperiness();
    }

    public ABlock(Vec3 vec3) {
        this.vec3 = vec3;
        this.vec3.x = -this.vec3.x; // flipping the x axis
        this.axisVecTuples = new ArrayList<>();
        this.boxesArrayList = new ArrayList<>();
        updateColor();
        updateBoundingBox();
        updateBoxes();
        updateSlipperiness();
    }

    public abstract void updateBoundingBox();
    public abstract void updateImage();

    public void updateColor() {
        setMaterialColor(BlockColors.STONE.get());
        setSpecularColor(BlockColors.STONE_SPEC.get());
        this.specularPower = 5;
    }

    public void updateSlipperiness() {
        this.slipperiness = Movement.Slipperiness.BLOCK;
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
        blockData.color = this.materialColor;
        return blockData;
    }

    public void onLanded(Player player) {
        player.getVelocity().y = 0.0D;
    }

    public void onEntityCollidedWithBlock(Player player) {

    }

    public void setMaterialColor(Color materialColor) {
        this.materialColor = materialColor;
        this.updateBoxes();
    }

    public void applyMaterialColor(Color materialColor) {
        if (boxesArrayList == null || boxesArrayList.isEmpty()) return;
        boxesArrayList.forEach(b -> b.setMaterial(new PhongMaterial(materialColor)));
    }

    public void resetAndApplyMaterialColor() {
        if (boxesArrayList == null || boxesArrayList.isEmpty()) return;
        boxesArrayList.forEach(b -> b.setMaterial(new PhongMaterial(this.materialColor)));
    }

    public void setSpecularColor(Color specularColor) {
        if (specularColor.equals(BlockColors.IRON_SPEC.get())) this.specularPower = 50;
        else if (specularColor.equals(BlockColors.STONE_SPEC.get())) this.specularPower = 20;
        else if (specularColor.equals(BlockColors.WOOD_SPEC.get())) this.specularPower = 10;
        else this.specularPower = 5;
        this.specularColor = specularColor;
        this.updateBoxes();
    }

    private void updateBoxes() {
        this.boxesArrayList.clear();
        for (AxisVecTuple axisVecTuple : this.axisVecTuples) {
            this.boxesArrayList.add(axisVecTuple.getBox(this));
        }
    }

}
