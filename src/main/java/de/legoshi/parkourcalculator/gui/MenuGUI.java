package de.legoshi.parkourcalculator.gui;

import de.legoshi.parkourcalculator.Application;
import de.legoshi.parkourcalculator.file.BlockData;
import de.legoshi.parkourcalculator.file.FileHandler;
import de.legoshi.parkourcalculator.file.InputData;
import de.legoshi.parkourcalculator.gui.debug.menu.BlockSettings;
import de.legoshi.parkourcalculator.parkour.PositionVisualizer;
import de.legoshi.parkourcalculator.parkour.environment.BlockFactory;
import de.legoshi.parkourcalculator.parkour.environment.Environment;
import de.legoshi.parkourcalculator.parkour.environment.blocks.ABlock;
import de.legoshi.parkourcalculator.parkour.simulator.MovementEngine;
import de.legoshi.parkourcalculator.parkour.tick.InputTick;
import de.legoshi.parkourcalculator.util.Vec3;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Window;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class MenuGUI extends MenuBar {

    private final Window window;
    private final Application application;
    private final InputTickGUI inputTickGUI;
    private final MovementEngine movementEngine;
    private final PositionVisualizer positionVisualizer;
    @Setter private MinecraftGUI minecraftGUI;

    public MenuGUI(Window window, Application application) {
        this.window = window;
        this.application = application;
        this.inputTickGUI = application.inputTickGUI;
        this.positionVisualizer = application.positionVisualizer;
        this.movementEngine = positionVisualizer.getMovementEngine();

        Menu fileMenu = new Menu("File");

        MenuItem openInputMenuItem = new MenuItem("Open Inputs");
        MenuItem saveInputMenuItem = new Menu("Save Inputs");
        MenuItem openBlockMenuItem = new MenuItem("Open Blocks");
        MenuItem saveBlockMenuItem = new Menu("Save Blocks");

        registerOpenInputMenu(openInputMenuItem);
        registerSaveInputMenu(saveInputMenuItem);
        registerOpenBlockMenu(openBlockMenuItem);
        registerSaveBlockMenu(saveBlockMenuItem);

        fileMenu.getItems().add(openInputMenuItem);
        fileMenu.getItems().add(saveInputMenuItem);
        fileMenu.getItems().add(openBlockMenuItem);
        fileMenu.getItems().add(saveBlockMenuItem);

        getMenus().add(fileMenu);
    }

    private void registerOpenInputMenu(MenuItem menuItem) {
        menuItem.setOnAction(event -> {
            List<InputData> inputDatas = FileHandler.loadInputs(window);
            if (inputDatas == null || inputDatas.size() == 0) return;

            // clear all current ticks
            inputTickGUI.getHBoxes().clear();

            // add all ticks to the side
            List<InputTick> inputTicks = new ArrayList<>();
            for (InputData inputData : inputDatas) inputTicks.add(inputData.getInputTick());
            inputTickGUI.addAllTicks(inputTicks);

            // update player path
            positionVisualizer.generatePlayerPath();
        });
    }

    private void registerSaveInputMenu(MenuItem menuItem) {
        menuItem.setOnAction(event -> {
            List<InputData> inputDatas = new ArrayList<>();

            Vec3 playerStart = movementEngine.player.getStartPos();
            Vec3 playerStartVel = movementEngine.player.getStartVel();

            int i = 0;
            for (InputTick inputTick : inputTickGUI.getInputTicks().getInputTicks()) {
                InputData inputData = new InputData();
                inputData.setInputTick(inputTick);
                if (i == 0) {
                    inputData.setPosition(playerStart);
                    inputData.setVelocity(playerStartVel);
                } else {
                    inputData.setPosition(new Vec3(0, 0, 0));
                    inputData.setVelocity(new Vec3(0, 0, 0));
                }
                inputDatas.add(inputData);
                i++;
            }

            FileHandler.saveInputs(inputDatas, window);
        });
    }

    private void registerOpenBlockMenu(MenuItem menuItem) {
        menuItem.setOnAction(event -> {
            /*List<ABlock> aBlocks = Environment.aBlocks;
            for (ABlock aBlock : aBlocks) minecraftGUI.removeBlock(aBlock);*/

            List<BlockData> blockDataList = FileHandler.loadBlocks(window);
            if (blockDataList == null || blockDataList.size() == 0) return;

            for (BlockData blockData : blockDataList) {

                // update BlockSettings
                BlockSettings.setFlip(blockData.TOP);
                BlockSettings.setFloor(blockData.BOTTOM);
                BlockSettings.setNorth(blockData.NORTH);
                BlockSettings.setEast(blockData.EAST);
                BlockSettings.setWest(blockData.WEST);
                BlockSettings.setSouth(blockData.SOUTH);
                BlockSettings.setTier(blockData.tier);
                BlockSettings.setColor(blockData.color);

                ABlock aBlock = BlockFactory.createBlock(blockData.pos, blockData.blockType);
                minecraftGUI.addBlock(aBlock);
            }
        });
    }

    private void registerSaveBlockMenu(MenuItem menuItem) {
        menuItem.setOnAction(event -> {
            List<ABlock> aBlocks = Environment.aBlocks;
            List<BlockData> blockDataList = new ArrayList<>();
            for (ABlock aBlock : aBlocks) {
                blockDataList.add(aBlock.toBlockData());
            }
            FileHandler.saveBlocks(blockDataList, window);
        });
    }

}
