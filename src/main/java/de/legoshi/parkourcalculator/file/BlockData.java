package de.legoshi.parkourcalculator.file;

import de.legoshi.parkourcalculator.util.Vec3;
import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class BlockData {

    public String blockType;
    public Vec3 pos;
    public int tier;
    public Color color;
    public boolean TOP, BOTTOM, NORTH, EAST, SOUTH, WEST;

}
