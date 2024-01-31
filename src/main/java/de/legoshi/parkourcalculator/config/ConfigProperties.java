package de.legoshi.parkourcalculator.config;

import de.legoshi.parkourcalculator.simulation.ParkourVersion;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigProperties extends Properties {

    protected static final String CONFIG_FILE = "config.properties";

    public void resetToDefault() {
        setProperty("parkourVersion", "V_1_8");
        setProperty("forward", "W");
        setProperty("backward", "S");
        setProperty("left", "A");
        setProperty("right", "D");
        setProperty("up", "SPACE");
        setProperty("down", "SHIFT");
        setProperty("sprint", "CONTROL");
        setProperty("placeBlock", "PRIMARY");
        setProperty("destroyBlock", "SECONDARY");
        setProperty("cameraSpeed", "0.02");
        setProperty("mouseSpeed", "0.02");
        setProperty("maxSpeedMultiplier", "2.5");
        setProperty("maxMouseMultiplier", "2.5");
        setProperty("coordinatePrecision", "8");
        setProperty("previewBlock", "true");
        setProperty("pathCollision", "true");
        setProperty("realVelocity", "true");
    }

    public ParkourVersion getVersion() {
        return ParkourVersion.valueOf(getProperty("parkourVersion"));
    }

    public void setVersion(ParkourVersion version) {
        setProperty("parkourVersion", version.toString());
    }

    // keycode
    public String getForward() {
        return getProperty("forward");
    }

    public void setForward(String forward) {
        setProperty("forward", forward);
    }

    public String getBackward() {
        return getProperty("backward");
    }

    public void setBackward(String backward) {
        setProperty("backward", backward);
    }

    public String getLeft() {
        return getProperty("left");
    }

    public void setLeft(String left) {
        setProperty("left", left);
    }

    public String getRight() {
        return getProperty("right");
    }

    public void setRight(String right) {
        setProperty("right", right);
    }

    public String getUp() {
        return getProperty("up");
    }

    public void setUp(String up) {
        setProperty("up", up);
    }

    public String getDown() {
        return getProperty("down");
    }

    public void setDown(String down) {
        setProperty("down", down);
    }

    public String getSprint() {
        return getProperty("sprint");
    }

    public void setSprint(String sprint) {
        setProperty("sprint", sprint);
    }

    // mousecode
    public String getPlaceBlock() {
        return getProperty("placeBlock");
    }

    public void setPlaceBlock(String placeBlock) {
        setProperty("placeBlock", placeBlock);
    }

    public String getDestroyBlock() {
        return getProperty("destroyBlock");
    }

    public void setDestroyBlock(String destroyBlock) {
        setProperty("destroyBlock", destroyBlock);
    }

    // double
    public double getCameraSpeed() {
        return Double.parseDouble(getProperty("cameraSpeed"));
    }

    public void setCameraSpeed(double cameraSpeed) {
        setProperty("cameraSpeed", Double.toString(cameraSpeed));
    }

    public double getMaxSpeedMultiplier() {
        return Double.parseDouble(getProperty("maxSpeedMultiplier"));
    }

    public void setMaxSpeedMultiplier(double maxSpeedMultiplier) {
        setProperty("maxSpeedMultiplier", Double.toString(maxSpeedMultiplier));
    }

    public double getMaxMouseMultiplier() {
        return Double.parseDouble(getProperty("maxMouseMultiplier"));
    }

    public void setMaxMouseMultiplier(double maxMouseMultiplier) {
        setProperty("maxMouseMultiplier", Double.toString(maxMouseMultiplier));
    }

    public double getMouseSpeed() {
        return Double.parseDouble(getProperty("mouseSpeed"));
    }

    public void setMouseSpeed(double mouseSpeed) {
        setProperty("mouseSpeed", Double.toString(mouseSpeed));
    }

    public int getCoordinatePrecision() {
        return Integer.parseInt(getProperty("coordinatePrecision"));
    }

    public void setCoordinatePrecision(int coordinatePrecision) {
        setProperty("coordinatePrecision", Integer.toString(coordinatePrecision));
    }

    // boolean
    public boolean isPreviewBlock() {
        return Boolean.parseBoolean(getProperty("previewBlock"));
    }

    public void setPreviewBlock(boolean previewBlock) {
        setProperty("previewBlock", String.valueOf(previewBlock));
    }

    public boolean isPathCollision() {
        return Boolean.parseBoolean(getProperty("pathCollision"));
    }

    public void setPathCollision(boolean pathCollision) {
        setProperty("pathCollision", String.valueOf(pathCollision));
    }

    public boolean isRealVelocity() {
        return Boolean.parseBoolean(getProperty("realVelocity"));
    }

    public void setRealVelocity(boolean realVelocity) {
        setProperty("realVelocity", String.valueOf(realVelocity));
    }

}