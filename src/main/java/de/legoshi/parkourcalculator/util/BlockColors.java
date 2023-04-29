package de.legoshi.parkourcalculator.util;

import javafx.scene.paint.Color;

public enum BlockColors {

    PLANT_SPEC("0x004000ff"),
    STONE_SPEC("0x404040ff"),
    WOOD_SPEC("0x45220Aff"),
    IRON_SPEC("0xffffffff"),

    DARK_IRON("0x272727ff"),
    WOOD("0xc8961eff"),
    STONE("0x999999ff"),
    PLANT("0x6fd77eff"),

    BED("0xff6a6aff"),
    END_ROD("0xffffffff"),
    BREWING_STAND("0xfff39aff"),
    CACTUS("0x008323ff"),
    CAKE("0xffc4c4ff"),
    CARPET("0x9b9b9bff"),
    COBWEB("0xb6b6b655"),
    DRAGON_EGG("0x7557ffff"),
    CHORUS_PLANT("0x5555ffff"),
    SHULKER("0xffb6c1ff"),
    END_PORTAL("0xd8d92dff"),
    ICE("0x97fff2ff"),
    LAVA("0xd9343455"),
    SLIME("0x009915aa"),
    WATER("0x00a46a55"),
    PANE("0xc3c3c388"),
    SNOW("0xffffffff"),
    ENDER_CHEST("0x5239c5ff"),
    SOUL_SAND("0x523600ff"),
    HEAD("0xb9b9b9ff"),
    COCOA_BEAN("0x7b5100ff"),
    FLOWER_POT("0xd24a00ff");

    private final Color color;

    BlockColors(String hex) {
        this.color = parse(hex);
    }

    public Color get() {
        return this.color;
    }

    public static Color parse(String hex) {
        if (hex.startsWith("0x") && hex.length() == 10) {
            hex = hex.substring(2);
        } else {
            throw new IllegalArgumentException("Invalid color specification");
        }

        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        int a = Integer.parseInt(hex.substring(6, 8), 16);

        return Color.color(r / 255.0, g / 255.0, b / 255.0, a / 255.0);
    }

}
