package de.legoshi.parkourcalculator.util;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.io.InputStream;

public class ImageHelper {

    private static final int IMAGE_HEIGHT = 50;
    private static final int IMAGE_WIDTH = 50;
    private static final boolean PRESERVE_RATIO = true;
    private static final boolean SMOOTH = true;

    public Image getImageFromURL(String path) {
        return getImageView(path);
    }

    private static Image getImageView(String s) {
        try (InputStream is = ImageHelper.class.getResourceAsStream(s)) {
            BufferedImage webpImage = ImageIO.read(is);
            BufferedImage processedImage = changeWhiteToDarkGray(webpImage, IMAGE_HEIGHT, IMAGE_WIDTH, PRESERVE_RATIO, SMOOTH);
            return SwingFXUtils.toFXImage(processedImage, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static BufferedImage changeWhiteToDarkGray(BufferedImage inputImage, int targetWidth, int targetHeight, boolean preserveRatio, boolean smooth) {
        // Calculate the new width and height while preserving the aspect ratio
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        if (preserveRatio) {
            double widthRatio = (double) targetWidth / width;
            double heightRatio = (double) targetHeight / height;
            double minRatio = Math.min(widthRatio, heightRatio);
            width = (int) (width * minRatio);
            height = (int) (height * minRatio);
        } else {
            width = targetWidth;
            height = targetHeight;
        }

        // Resize the image
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        if (smooth) {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        }
        g.drawImage(inputImage, 0, 0, width, height, null);

        // Change white background to dark gray
        for (int x = 0; x < resizedImage.getWidth(); x++) {
            for (int y = 0; y < resizedImage.getHeight(); y++) {
                int rgba = resizedImage.getRGB(x, y);
                Color color = new Color(rgba, true);
                if (color.equals(Color.WHITE)) {
                    resizedImage.setRGB(x, y, new Color(44, 44, 44, color.getAlpha()).getRGB());
                }
            }
        }
        g.dispose();
        return resizedImage;
    }

}
