package de.legoshi.parkourcalculator.util;

import javafx.scene.image.Image;

import java.io.InputStream;

public class ImageHelper {

    public Image getImageFromURL(String path) {
        // "/images/grass_block.png"
        return new Image(ImageHelper.class.getResourceAsStream(path));
    }

}
