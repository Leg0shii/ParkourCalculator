package de.legoshi.parkourcalculator.gui.menu;

import de.legoshi.parkourcalculator.Application;
import de.legoshi.parkourcalculator.file.BlockData;
import de.legoshi.parkourcalculator.file.FileHandler;
import de.legoshi.parkourcalculator.file.InputData;
import de.legoshi.parkourcalculator.gui.BlockGUI;
import de.legoshi.parkourcalculator.gui.InputTickGUI;
import de.legoshi.parkourcalculator.gui.MinecraftGUI;
import de.legoshi.parkourcalculator.gui.VersionDependent;
import de.legoshi.parkourcalculator.gui.debug.menu.BlockSettings;
import de.legoshi.parkourcalculator.simulation.Parkour;
import de.legoshi.parkourcalculator.simulation.Parkour_1_8;
import de.legoshi.parkourcalculator.simulation.movement.Movement;
import de.legoshi.parkourcalculator.util.PositionVisualizer;
import de.legoshi.parkourcalculator.simulation.environment.BlockFactory;
import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager_1_8;
import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import de.legoshi.parkourcalculator.simulation.movement.Movement_1_8;
import de.legoshi.parkourcalculator.simulation.tick.InputTick;
import de.legoshi.parkourcalculator.util.Vec3;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Window;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class MenuGUI extends MenuBar implements VersionDependent {

    private static final Logger logger = LogManager.getLogger(MenuGUI.class.getName());
    private final Window window;
    private final Application application;
    private final InputTickGUI inputTickGUI;
    private final PositionVisualizer positionVisualizer;

    @Setter private MinecraftGUI minecraftGUI;
    private Parkour parkour;
    private Movement movement;

    public MenuGUI(Window window, Application application) {
        this.window = window;
        this.application = application;
        this.inputTickGUI = application.inputTickGUI;
        this.positionVisualizer = application.positionVisualizer;
        apply(application.currentParkour);

        Menu fileMenu = new Menu("File");
        Menu helpMenu = new Menu("Help");
        Menu configMenu = new Menu("Config");

        MenuItem openInputMenuItem = new MenuItem("Open Inputs");
        MenuItem openBlockMenuItem = new MenuItem("Open Blocks");
        MenuItem saveInputMenuItem = new MenuItem("Save Inputs");
        MenuItem saveBlockMenuItem = new MenuItem("Save Blocks");
        MenuItem openConfigItem = new MenuItem("Open Config");

        openInputMenuItem.setOnAction(event -> openInputMenu());
        openBlockMenuItem.setOnAction(event -> openBlockMenu());
        saveInputMenuItem.setOnAction(event -> saveInputMenu());
        saveBlockMenuItem.setOnAction(event -> saveBlockMenu());
        openConfigItem.setOnAction(event -> openConfigMenu());

        fileMenu.getItems().addAll(openInputMenuItem, openBlockMenuItem, saveInputMenuItem, saveBlockMenuItem);

        MenuItem helpBlocksResetMenuItem = new MenuItem("Reset Blocks");
        MenuItem helpTicksResetMenuItem = new MenuItem("Reset Ticks");
        MenuItem helpPlayerResetMenuItem = new MenuItem("Reset Player");

        helpPlayerResetMenuItem.setOnAction(event -> resetPlayer());
        helpTicksResetMenuItem.setOnAction(event -> resetTicks());
        helpBlocksResetMenuItem.setOnAction(event -> resetBlocks());

        helpMenu.getItems().addAll(helpBlocksResetMenuItem, helpTicksResetMenuItem, helpPlayerResetMenuItem);
        configMenu.getItems().addAll(openConfigItem);
        getMenus().addAll(fileMenu, helpMenu, configMenu);
    }

    private void openConfigMenu() {
        application.configGUI.showConfigScreen();
    }

    @Override
    public void apply(Parkour parkour) {
        this.parkour = parkour;
        this.movement = parkour.getMovement();
        logger.info("MenuGUI applied");
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
            // startVel.y = Parkour_1_8.DEFAULT_VELOCITY.y;
            startVel.y = -0.0784000015258789D;
        }

        movement.resetPlayer();
        parkour.getPlayer().setStartPos(startPos.copy());
        parkour.getPlayer().setStartVel(startVel.copy());

        application.menuScreen.playerSettings.getFacingYaw().setText("0.0");

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

        Vec3 playerStart = parkour.getPlayer().getStartPos();
        Vec3 playerStartVel = parkour.getPlayer().getStartVel();

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
        List<ABlock> aBlocks = parkour.getBlockManager().getAllBlocks();
        List<BlockData> blockDataList = new ArrayList<>();
        for (ABlock aBlock : aBlocks) {
            blockDataList.add(aBlock.toBlockData());
        }
        FileHandler.saveBlocks(blockDataList, window);
    }

}
