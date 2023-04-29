package de.legoshi.parkourcalculator.gui.menu;

import de.legoshi.parkourcalculator.simulation.ParkourVersion;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigProperties {

    private static final String CONFIG_FILE = "config.properties";
    private Properties properties;

    public ConfigProperties() {
        properties = new Properties();
        loadConfig();
    }

    private void loadConfig() {
        System.out.println();
        String workingDirectory = System.getProperty("user.dir");
        Path configFilePath = Paths.get(workingDirectory, "config.properties");

        if (Files.exists(configFilePath)) {
            try (InputStream inputStream = Files.newInputStream(configFilePath)) {
                properties.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
                properties.load(inputStream);
                saveConfig(); // Save the configuration file to the working directory
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveConfig() {
        try {
            String workingDirectory = System.getProperty("user.dir");
            Path configFilePath = Paths.get(workingDirectory, "config.properties");
            try (OutputStream outputStream = Files.newOutputStream(configFilePath)) {
                properties.store(outputStream, "Configuration settings");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resetToDefault() {
        properties.setProperty("parkourVersion", "V_1_8");
        properties.setProperty("forward", "W");
        properties.setProperty("backward", "S");
        properties.setProperty("left", "A");
        properties.setProperty("right", "D");
        properties.setProperty("up", "SPACE");
        properties.setProperty("down", "SHIFT");
        properties.setProperty("sprint", "CONTROL");
        properties.setProperty("placeBlock", "PRIMARY");
        properties.setProperty("destroyBlock", "SECONDARY");
        properties.setProperty("cameraSpeed", "0.02");
        properties.setProperty("mouseSpeed", "0.02");
        properties.setProperty("maxSpeedMultiplier", "2.5");
        properties.setProperty("maxMouseMultiplier", "2.5");
        properties.setProperty("coordinatePrecision", "8");
        properties.setProperty("previewBlock", "true");
        properties.setProperty("pathCollision", "true");
        properties.setProperty("realVelocity", "true");
    }

    public ParkourVersion getVersion() {
        return ParkourVersion.valueOf(properties.getProperty("parkourVersion"));
    }

    public void setVersion(ParkourVersion version) {
        properties.setProperty("parkourVersion", version.toString());
    }

    // keycode
    public String getForward() {
        return properties.getProperty("forward");
    }

    public void setForward(String forward) {
        properties.setProperty("forward", forward);
    }

    public String getBackward() {
        return properties.getProperty("backward");
    }

    public void setBackward(String backward) {
        properties.setProperty("backward", backward);
    }

    public String getLeft() {
        return properties.getProperty("left");
    }

    public void setLeft(String left) {
        properties.setProperty("left", left);
    }

    public String getRight() {
        return properties.getProperty("right");
    }

    public void setRight(String right) {
        properties.setProperty("right", right);
    }

    public String getUp() {
        return properties.getProperty("up");
    }

    public void setUp(String up) {
        properties.setProperty("up", up);
    }

    public String getDown() {
        return properties.getProperty("down");
    }

    public void setDown(String down) {
        properties.setProperty("down", down);
    }

    public String getSprint() {
        return properties.getProperty("sprint");
    }

    public void setSprint(String sprint) {
        properties.setProperty("sprint", sprint);
    }

    // mousecode
    public String getPlaceBlock() {
        return properties.getProperty("placeBlock");
    }

    public void setPlaceBlock(String placeBlock) {
        properties.setProperty("placeBlock", placeBlock);
    }

    public String getDestroyBlock() {
        return properties.getProperty("destroyBlock");
    }

    public void setDestroyBlock(String destroyBlock) {
        properties.setProperty("destroyBlock", destroyBlock);
    }

    // double
    public double getCameraSpeed() {
        return Double.parseDouble(properties.getProperty("cameraSpeed"));
    }

    public void setCameraSpeed(double cameraSpeed) {
        properties.setProperty("cameraSpeed", Double.toString(cameraSpeed));
    }

    public double getMaxSpeedMultiplier() {
        return Double.parseDouble(properties.getProperty("maxSpeedMultiplier"));
    }

    public void setMaxSpeedMultiplier(double maxSpeedMultiplier) {
        properties.setProperty("maxSpeedMultiplier", Double.toString(maxSpeedMultiplier));
    }

    public double getMaxMouseMultiplier() {
        return Double.parseDouble(properties.getProperty("maxMouseMultiplier"));
    }

    public void setMaxMouseMultiplier(double maxMouseMultiplier) {
        properties.setProperty("maxMouseMultiplier", Double.toString(maxMouseMultiplier));
    }

    public double getMouseSpeed() {
        return Double.parseDouble(properties.getProperty("mouseSpeed"));
    }

    public void setMouseSpeed(double mouseSpeed) {
        properties.setProperty("mouseSpeed", Double.toString(mouseSpeed));
    }

    public int getCoordinatePrecision() {
        return Integer.parseInt(properties.getProperty("coordinatePrecision"));
    }

    public void setCoordinatePrecision(int coordinatePrecision) {
        properties.setProperty("coordinatePrecision", Integer.toString(coordinatePrecision));
    }

    // boolean
    public boolean isPreviewBlock() {
        return Boolean.parseBoolean(properties.getProperty("previewBlock"));
    }

    public void setPreviewBlock(boolean previewBlock) {
        properties.setProperty("previewBlock", String.valueOf(previewBlock));
    }

    public boolean isPathCollision() {
        return Boolean.parseBoolean(properties.getProperty("pathCollision"));
    }

    public void setPathCollision(boolean pathCollision) {
        properties.setProperty("pathCollision", String.valueOf(pathCollision));
    }

    public boolean isRealVelocity() {
        return Boolean.parseBoolean(properties.getProperty("realVelocity"));
    }

    public void setRealVelocity(boolean realVelocity) {
        properties.setProperty("realVelocity", String.valueOf(realVelocity));
    }

}