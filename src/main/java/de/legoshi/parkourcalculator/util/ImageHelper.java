package de.legoshi.parkourcalculator.util;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Scale;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.io.InputStream;

public class ImageHelper {

    private static final int IMAGE_HEIGHT = 50;
    private static final int IMAGE_WIDTH = 50;
    private static final boolean PRESERVE_RATIO = true;
    private static final boolean SMOOTH = true;

    public Image getImageFromURL(String path) {
        return createWebPImage(path, IMAGE_HEIGHT, IMAGE_WIDTH, PRESERVE_RATIO, SMOOTH);
    }

    public static Image createWebPImage(String resourcePath, double requestedWidth, double requestedHeight, boolean preserveRatio, boolean smooth) {
        InputStream inputStream = ImageHelper.class.getResourceAsStream(resourcePath);
        try {
            // Read the WebP image
            BufferedImage webpImage = ImageIO.read(inputStream);

            // Convert the BufferedImage to a JavaFX Image
            Image image = SwingFXUtils.toFXImage(webpImage, null);

            // Resize the image
            if (requestedWidth > 0 && requestedHeight > 0) {
                double widthRatio = requestedWidth / image.getWidth();
                double heightRatio = requestedHeight / image.getHeight();

                double finalWidth = requestedWidth;
                double finalHeight = requestedHeight;

                if (preserveRatio) {
                    double minRatio = Math.min(widthRatio, heightRatio);
                    finalWidth = image.getWidth() * minRatio;
                    finalHeight = image.getHeight() * minRatio;
                }

                Canvas canvas = new Canvas(finalWidth, finalHeight);
                GraphicsContext gc = canvas.getGraphicsContext2D();

                if (smooth) {
                    gc.setImageSmoothing(true);
                }

                Scale scale = new Scale(finalWidth / image.getWidth(), finalHeight / image.getHeight());
                gc.setTransform(scale.getMxx(), scale.getMyx(), scale.getMxy(), scale.getMyy(), scale.getTx(), scale.getTy());
                gc.drawImage(image, 0, 0);

                WritableImage resizedImage = new WritableImage((int) finalWidth, (int) finalHeight);
                canvas.snapshot(null, resizedImage);
                return resizedImage;
            }

            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
