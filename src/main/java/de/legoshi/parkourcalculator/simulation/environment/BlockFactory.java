package de.legoshi.parkourcalculator.simulation.environment;

import de.legoshi.parkourcalculator.file.BlockData;
import de.legoshi.parkourcalculator.gui.debug.menu.BlockSettings;
import de.legoshi.parkourcalculator.simulation.environment.block.*;
import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import de.legoshi.parkourcalculator.util.Vec3;

public class BlockFactory {

    public static ABlock createBlock(Vec3 vec3, String type) {
        return switch (type) {
            // flipable
            case "Stair" -> new Stair(vec3);
            case "PistonHead" -> new PistonHead(vec3);
            case "PistonBase" -> new PistonBase(vec3);

            // tiers
            case "CocoaBean" -> new CocoaBean(vec3);
            case "Snow" -> new Snow(vec3);
            case "Cake" -> new Cake(vec3);

            // other
            case "EndPortalFrame" -> new EndPortalFrame(vec3);
            case "Head" -> new Head(vec3);
            case "Water" -> new Water(vec3);
            case "Lava" -> new Lava(vec3);
            case "Cobweb" -> new Cobweb(vec3);
            case "Slime" -> new Slime(vec3);

            case "Enderchest" -> new Enderchest(vec3);
            case "Anvil" -> new Anvil(vec3);
            case "Bed" -> new Bed(vec3);
            case "BrewingStand" -> new BrewingStand(vec3);
            case "Cactus" -> new Cactus(vec3);
            case "Carpet" -> new Carpet(vec3);
            case "Cauldron" -> new Cauldron(vec3);
            case "Cobblewall" -> new Cobblewall(vec3);
            case "DragonEgg" -> new DragonEgg(vec3);
            case "Fence" -> new Fence(vec3);
            case "Flowerpot" -> new Flowerpot(vec3);
            case "Hopper" -> new Hopper(vec3);
            case "Ice" -> new Ice(vec3);
            case "Lilypad" -> new Lilypad(vec3);
            case "Soulsand" -> new Soulsand(vec3);
            case "Trapdoor" -> new Trapdoor(vec3);
            case "Pane" -> new Pane(vec3);
            case "Ladder" -> new Ladder(vec3);
            case "Vine" -> new Vine(vec3);
            default -> new StandardBlock(vec3);
        };
    }

    public static void applyValues(BlockData blockData) {
        BlockSettings.setFlip(blockData.TOP);
        BlockSettings.setFloor(blockData.BOTTOM);
        BlockSettings.setNorth(blockData.NORTH);
        BlockSettings.setEast(blockData.EAST);
        BlockSettings.setWest(blockData.WEST);
        BlockSettings.setSouth(blockData.SOUTH);
        BlockSettings.setTier(blockData.tier);
        BlockSettings.setColor(blockData.color);
    }

}
