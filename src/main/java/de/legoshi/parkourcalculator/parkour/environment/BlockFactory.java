package de.legoshi.parkourcalculator.parkour.environment;

import de.legoshi.parkourcalculator.parkour.environment.blocks.*;
import de.legoshi.parkourcalculator.util.Vec3;

import java.util.ArrayList;

public class BlockFactory {

    public static ABlock createBlock(Vec3 vec3, String type) {
        ArrayList<Boolean> list = new ArrayList<>();
        list.add(true);
        list.add(false);
        list.add(true);
        list.add(false);
        return switch (type) {
            case "Enderchest" -> new Enderchest(vec3);
            case "Pane" -> new Pane(vec3);
            case "Cake" -> new Cake(vec3);
            case "Stair" -> new Stair(vec3);
            default -> new StandardBlock(vec3);
        };
    }

}
