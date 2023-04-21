package de.legoshi.parkourcalculator.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private Properties properties;

    public ConfigReader() {
        properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public boolean getBooleanProperty(String key) {
        String value = getProperty(key);
        return Boolean.parseBoolean(value);
    }

    public double getDoubleProperty(String key) {
        String value = getProperty(key);
        return value == null ? 0 : Double.parseDouble(value);
    }

    public int getIntProperty(String key) {
        String value = getProperty(key);
        return value == null ? 0 : Integer.parseInt(value);
    }
}