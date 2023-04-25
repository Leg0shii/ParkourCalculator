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
    @Setter
    private MinecraftGUI minecraftGUI;

    public MenuGUI(Window window, Application application) {
        this.window = window;
        this.application = application;
        this.inputTickGUI = application.inputTickGUI;
        this.positionVisualizer = application.positionVisualizer;
        this.movementEngine = positionVisualizer.getMovementEngine();

        Menu fileMenu = new Menu("File");
        Menu helpMenu = new Menu("Help");

        MenuItem openInputMenuItem = new MenuItem("Open Inputs");
        MenuItem openBlockMenuItem = new MenuItem("Open Blocks");
        MenuItem saveInputMenuItem = new MenuItem("Save Inputs");
        MenuItem saveBlockMenuItem = new MenuItem("Save Blocks");

        openInputMenuItem.setOnAction(event -> openInputMenu());
        openBlockMenuItem.setOnAction(event -> openBlockMenu());
        saveInputMenuItem.setOnAction(event -> saveInputMenu());
        saveBlockMenuItem.setOnAction(event -> saveBlockMenu());

        fileMenu.getItems().addAll(openInputMenuItem, openBlockMenuItem, saveInputMenuItem, saveBlockMenuItem);

        MenuItem helpBlocksResetMenuItem = new MenuItem("Reset Blocks");
        MenuItem helpTicksResetMenuItem = new MenuItem("Reset Ticks");
        MenuItem helpPlayerResetMenuItem = new MenuItem("Reset Player");

        helpPlayerResetMenuItem.setOnAction(event -> resetPlayer());
        helpTicksResetMenuItem.setOnAction(event -> resetTicks());
        helpBlocksResetMenuItem.setOnAction(event -> resetBlocks());

        helpMenu.getItems().addAll(helpBlocksResetMenuItem, helpTicksResetMenuItem, helpPlayerResetMenuItem);
        getMenus().addAll(fileMenu, helpMenu);
    }

    private void resetPlayer() {
        positionVisualizer.resetPlayer();
    }

    private void resetTicks() {
        inputTickGUI.resetTicks();
    }

    private void resetBlocks() {
        minecraftGUI.resetScreen();
    }

    private void openInputMenu() {
        List<InputData> inputDatas = FileHandler.loadInputs(window);
        if (inputDatas == null || inputDatas.size() == 0) return;

        // add all ticks to the side
        List<InputTick> inputTicks = new ArrayList<>();
        Vec3 startPos = inputDatas.get(0).getPosition();
        Vec3 startVel = inputDatas.get(0).getVelocity();

        // necessary because mpk doesn't set a y-velocity.
        // Without y-velocity the player is floating above the ground on first tick
        if (startVel.y == 0.0) {
            System.out.println("REPLACED startVel.y == 0.0 with default");
            startVel.y = MovementEngine.DEFAULT_VELOCITY.y;
        }

        movementEngine.player.setStartPos(startPos.copy());
        movementEngine.player.setStartVel(startVel.copy());

        for (InputData inputData : inputDatas) inputTicks.add(inputData.getInputTick());
        inputTickGUI.importTicks(inputTicks);

        // update player path
        positionVisualizer.update(null, null);
    }

    private void openBlockMenu() {
        List<List<BlockData>> blockDataList = FileHandler.loadBlocks(window);
        if (blockDataList == null || blockDataList.size() == 0) return;

        minecraftGUI.clearScreen();

        // load solid blocks first
        BlockSettings.enableCustomColors();
        for (List<BlockData> blockDatas : blockDataList) {
            for (BlockData blockData : blockDatas) {
                BlockFactory.applyValues(blockData);
                ABlock aBlock = BlockFactory.createBlock(blockData.pos, blockData.blockType);
                minecraftGUI.addBlock(aBlock);
            }
        }
        BlockSettings.disableCustomColors();
    }

    private void saveInputMenu() {
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
    }

    private void saveBlockMenu() {
        List<ABlock> aBlocks = Environment.aBlocks;
        List<BlockData> blockDataList = new ArrayList<>();
        for (ABlock aBlock : aBlocks) {
            blockDataList.add(aBlock.toBlockData());
        }
        FileHandler.saveBlocks(blockDataList, window);
    }

}
