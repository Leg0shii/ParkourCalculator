package de.legoshi.parkourcalculator.parkour.environment;

import de.legoshi.parkourcalculator.parkour.environment.blocks.*;
import de.legoshi.parkourcalculator.parkour.environment.blocks.ABlock;
import de.legoshi.parkourcalculator.util.Vec3;

public class BlockFactory {

    public static ABlock createBlock(Vec3 vec3, String type) {
        return switch (type) {
            case "Enderchest" -> new Enderchest(vec3);
            case "Pane" -> new Pane(vec3);
            case "Cake" -> new Cake(vec3);
            case "Stair" -> new Stair(vec3);
            case "Ladder" -> new Ladder(vec3);
            case "Vine" -> new Vine(vec3);
            default -> new StandardBlock(vec3);
        };
    }

}
