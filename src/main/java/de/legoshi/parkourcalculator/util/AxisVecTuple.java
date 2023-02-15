package de.legoshi.parkourcalculator.util;

import de.legoshi.parkourcalculator.gui.MinecraftScreen;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AxisVecTuple {

    private AxisAlignedBB bb;
    private Vec3 shift;

    public Box getBox() {
        Box box = new Box(bb.maxX-bb.minX, bb.maxY-bb.minY, bb.maxZ-bb.minZ);
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);
        box.setMaterial(redMaterial);
        box.setTranslateX(bb.minX + MinecraftScreen.BLOCK_OFFSET_X - shift.x);
        box.setTranslateY(-bb.minY - MinecraftScreen.BLOCK_OFFSET_Y + shift.y);
        box.setTranslateZ(bb.minZ + MinecraftScreen.BLOCK_OFFSET_Z - shift.z);
        return box;
    }

}
