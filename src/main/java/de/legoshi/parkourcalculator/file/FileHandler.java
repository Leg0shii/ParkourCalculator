package de.legoshi.parkourcalculator.file;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.*;
import java.util.List;

public class FileHandler {

    public static void saveInputs(List<InputData> inputs, Window window) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Input");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Input Files (*.csv)", "*.csv"));
        File file = fileChooser.showSaveDialog(window);
        if (file != null) {
            CSVUtils.saveTicksToCSV(inputs, file.getAbsolutePath());
        }
    }

    public static List<InputData> loadInputs(Window window) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Inputs");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Input Files (*.csv)", "*.csv"));
        File file = fileChooser.showOpenDialog(window);

        if (file != null) {
            return CSVUtils.loadTicksFromCSV(file.getAbsolutePath());
        }

        return null;
    }

    public static void saveBlocks(List<BlockData> blocks, Window window) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Input");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Input Files (*.bcsv)", "*.bcsv"));
        File file = fileChooser.showSaveDialog(window);
        if (file != null) {
            CSVUtils.saveBlocksToCSV(blocks, file.getAbsolutePath());
        }
    }

    public static List<BlockData> loadBlocks(Window window) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Inputs");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Input Files (*.bcsv)", "*.bcsv"));
        File file = fileChooser.showOpenDialog(window);

        if (file != null) {
            return CSVUtils.loadBlocksFromCSV(file.getAbsolutePath());
        }

        return null;
    }

    // Add similar methods for saving and loading Block and Jump objects
}