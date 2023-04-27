package de.legoshi.parkourcalculator.util;

import de.legoshi.parkourcalculator.gui.MinecraftGUI;
import de.legoshi.parkourcalculator.gui.debug.menu.BlockSettings;
import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AxisVecTuple {

    private AxisAlignedBB bb;
    private Vec3 shift;

    public Box getBox(ABlock aBlock) {
        Box box = new Box(bb.maxX-bb.minX, bb.maxY-bb.minY, bb.maxZ-bb.minZ);
        final PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(BlockSettings.getColor(aBlock));
        material.setSpecularColor(BlockSettings.getSpecularColor(aBlock));
        material.setSpecularPower(aBlock.getSpecularPower());
        box.setMaterial(material);
        box.setTranslateX(bb.minX + MinecraftGUI.BLOCK_OFFSET_X - shift.x);
        box.setTranslateY(-bb.minY - MinecraftGUI.BLOCK_OFFSET_Y + shift.y);
        box.setTranslateZ(bb.minZ + MinecraftGUI.BLOCK_OFFSET_Z - shift.z);
        return box;
    }

}
