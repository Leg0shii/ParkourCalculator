package de.legoshi.parkourcalculator.util;

import javafx.scene.image.Image;

import java.io.InputStream;

public class ImageHelper {

    private static final int IMAGE_HEIGHT = 50;
    private static final int IMAGE_WIDTH = 50;
    private static final boolean PRESERVE_RATIO = true;
    private static final boolean SMOOTH = true;

    public Image getImageFromURL(String path) {
        return new Image(ImageHelper.class.getResourceAsStream(path), IMAGE_HEIGHT, IMAGE_WIDTH, PRESERVE_RATIO, SMOOTH);
    }

}
