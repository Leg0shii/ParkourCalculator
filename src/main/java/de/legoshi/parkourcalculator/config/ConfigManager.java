package de.legoshi.parkourcalculator.config;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@Getter
@Setter
public class ConfigManager extends ArrayList<Configurable> {

    private ConfigProperties configProperties = new ConfigProperties();

    public ConfigManager() {
        loadConfig();
        applyConfig();
    }

    public void applyConfig() {
        this.forEach(configurable -> configurable.applyConfigValues(configProperties));
    }

    private void loadConfig() {
        String workingDirectory = System.getProperty("user.dir");
        Path configFilePath = Paths.get(workingDirectory, "config.properties");

        if (Files.exists(configFilePath)) {
            try (InputStream inputStream = Files.newInputStream(configFilePath)) {
                configProperties.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(ConfigProperties.CONFIG_FILE)) {
                configProperties.load(inputStream);
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
                configProperties.store(outputStream, "Configuration settings");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
