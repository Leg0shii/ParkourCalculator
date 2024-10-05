package de.legoshi.parkourcalculator.gui.debug.menu;

import de.legoshi.parkourcalculator.Application;
import de.legoshi.parkourcalculator.ai.BruteforceOptions;
import de.legoshi.parkourcalculator.ai.InputGenerator;
import de.legoshi.parkourcalculator.ai.bruteforcer.MultiThreadBruteforcer;
import de.legoshi.parkourcalculator.gui.VersionDependent;
import de.legoshi.parkourcalculator.simulation.Parkour;
import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager;
import de.legoshi.parkourcalculator.simulation.player.Player;
import de.legoshi.parkourcalculator.util.Vec3;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class BruteforceSettings extends TitledPane implements VersionDependent {

    private static final Logger logger = LogManager.getLogger(BruteforceSettings.class.getName());
    private final Application application;

    @Getter private MultiThreadBruteforcer multiThreadBruteforcer;
    private GridPane gridPane;

    public List<Vec3> boundaries = new ArrayList<>();
    public BlockManager blockManager;
    public Player player;

    @Getter public ABlock startBlock;
    @Getter public ABlock endBlock;

    private Button setStartBlockButton, setEndBlockButton, removeBlockButton;
    private Button preComputePathButton, addPathButton ,removePathButton;
    private Button bruteforceButton, cancelButton, resetAllButton;

    private TextField numberOfTrialsField, recTicksField, ticksPerTrialField;
    private TextField syncField, repetitionsField, dimensionField, instancesField;
    private TextField intervallOfLastShownField, intervalDurationField, intervalSizeField, overlapField;
    private TextField wField, aField, sField, dField, jumpField, sneakField, sprintField, facingField;

    private CheckBox stopOnFindField, windowedCB;

    public BruteforceSettings(Application application) {
        this.application = application;
        this.blockManager = application.getParkour().getBlockManager();
        this.player = application.getParkour().getPlayer();
        this.multiThreadBruteforcer = new MultiThreadBruteforcer(application, boundaries);

        createUIComponents();
        setupEventHandlers();
    }

    private void createUIComponents() {
        Text titleText = new Text("Bruteforce Settings");
        titleText.setFill(Color.WHITE);
        setGraphic(titleText);

        this.gridPane = new GridPane();
        this.gridPane.setPadding(new Insets(10, 10, 10, 10));
        this.gridPane.setHgap(10);
        this.gridPane.setVgap(10);

        createTextFieldComponents();
        createButtonComponents();

        setContent(gridPane);
    }

    private void setupEventHandlers() {
        setStartBlockButton.setOnAction(event -> onStartBlockClick());
        setEndBlockButton.setOnAction(event -> onEndBlockClick());
        preComputePathButton.setOnAction(event -> preCompute());
        addPathButton.setOnAction(event -> addPathBlock());
        removePathButton.setOnAction(event -> removePathBlocks());
        windowedCB.setOnAction(event -> switchWindowedState());

        removeBlockButton.setOnAction(event -> reset());
        bruteforceButton.setOnAction(event -> bruteForceAction());
        cancelButton.setOnAction(event -> cancelBruteforce());
        resetAllButton.setOnAction(event -> resetAll());
    }

    private void createTextFieldComponents() {
        this.numberOfTrialsField = addUIPair("Number Of Trials", new TextField("100"), 0, 1, 2);
        this.ticksPerTrialField = addUIPair("Ticks Per Trial:", new TextField("20"), 0, 1, 3);
        this.repetitionsField = addUIPair("Repetitions:", new TextField("1000000"), 0, 1, 4);
        this.dimensionField = addUIPair("Dimension:", new TextField("0.5"), 0, 1, 5);
        this.stopOnFindField = addUIPair("Stop On Find:", new CheckBox(), 0, 1, 6);
        this.windowedCB = addUIPair("Windowed:", new CheckBox(), 0, 1, 7);
        this.windowedCB.setSelected(true);
        this.intervalDurationField = addUIPair("Interval Duration:", new TextField("15"), 0, 1, 8);
        this.intervalSizeField = addUIPair("Interval Size:", new TextField("30"), 0, 1, 9);
        this.intervallOfLastShownField = addUIPair("Show Path:", new TextField("100"), 0, 1, 10);

        this.recTicksField = addUIPair("Recursive:", new TextField("100"), 2, 3, 2);
        this.syncField = addUIPair("Sync (s):", new TextField("5"), 2, 3, 3);
        this.instancesField = addUIPair("Instance:", new TextField("5"), 2, 3, 4);
        this.overlapField = addUIPair("Overlap (t):", new TextField("20"), 2, 3, 9);

        this.wField = addUIPair("W-%:", new TextField("100"), 0, 1, 11);
        this.aField = addUIPair("A-%:", new TextField("0"), 0, 1, 12);
        this.sField = addUIPair("S-%:", new TextField("0"), 0, 1, 13);
        this.dField = addUIPair("D-%:", new TextField("0"), 0, 1, 14);

        this.jumpField = addUIPair("Jump-%:", new TextField("50"), 2, 3, 11);
        this.sneakField = addUIPair("Sneak-%:", new TextField("0"), 2, 3, 12);
        this.sprintField = addUIPair("Sprint-%:", new TextField("100"), 2, 3, 13);
        this.facingField = addUIPair("Yaw-%:", new TextField("25"), 2, 3, 14);
    }

    private void createButtonComponents() {
        this.setStartBlockButton = addButton(new Button("Set SB"), 0, 15, 1);
        this.setEndBlockButton = addButton(new Button("Set LB"), 1, 15, 1);
        this.removeBlockButton = addButton(new Button("Remove SB/LB"), 2, 15, 2);

        this.preComputePathButton = addButton(new Button("Compute Path"), 0, 16, 1);
        this.addPathButton = addButton(new Button("+ Path"), 1, 16, 1);
        this.removePathButton = addButton(new Button("Remove Path"), 2, 16, 2);

        this.bruteforceButton = addButton(new Button("Bruteforce"), 0, 17, 1);
        this.cancelButton = addButton(new Button("Cancel"), 1, 17, 1);
        this.resetAllButton = addButton(new Button("Reset ALL"), 2, 17, 2);
    }

    @Override
    public void apply(Parkour parkour) {
        this.multiThreadBruteforcer = new MultiThreadBruteforcer(application, boundaries);
        this.blockManager = parkour.getBlockManager();
        this.player = parkour.getPlayer();
        logger.info("Bruteforce Settings applied");
    }

    public void setEndBlock(ABlock block) {
        if (this.endBlock != null) this.endBlock.resetAndApplyMaterialColor();
        this.endBlock = block;
        this.endBlock.applyMaterialColor(Color.RED);
    }

    public void setStartBlock(ABlock block) {
        if (this.startBlock != null) this.startBlock.resetAndApplyMaterialColor();
        this.startBlock = block;
        this.startBlock.applyMaterialColor(Color.GREEN);
    }

    public void setPathBlock(ABlock block) {
        block.applyMaterialColor(Color.PURPLE);
        this.boundaries.add(block.getVec3());
    }

    private void removePathBlocks() {
        this.boundaries.forEach(block -> {
            ABlock aBlock = blockManager.getBlock(block);
            aBlock.resetAndApplyMaterialColor();
            aBlock.resetColoredAirBlocks();
        });
        this.boundaries.clear();
    }

    public void reset() {
        if (startBlock != null) {
            this.startBlock.resetAndApplyMaterialColor();
            this.startBlock = null;
        }
        if (endBlock != null) {
            this.endBlock.resetAndApplyMaterialColor();
            this.endBlock = null;
        }
        removePathBlocks();
    }

    private void resetAll() {
        reset();
        removePathBlocks();
        multiThreadBruteforcer.clearAll();
    }

    private void switchWindowedState() {
        if (windowedCB.isSelected()) {
            intervalDurationField.setDisable(false);
            intervalSizeField.setDisable(false);
            overlapField.setDisable(false);
            recTicksField.setDisable(true);
        } else {
            intervalDurationField.setDisable(true);
            intervalSizeField.setDisable(true);
            overlapField.setDisable(true);
            recTicksField.setDisable(false);
        }
    }

    private void bruteForceAction() {
        if (this.endBlock == null) {
            return;
        }
        try {
            int numberOfTrials = Integer.parseInt(numberOfTrialsField.getText());
            int ticksPerTrial = Integer.parseInt(ticksPerTrialField.getText());
            int repetitions = Integer.parseInt(repetitionsField.getText());
            double dimension = Double.parseDouble(dimensionField.getText());
            boolean stopOnFind = stopOnFindField.isSelected();
            int intervallOfLastShown = Integer.parseInt(intervallOfLastShownField.getText());
            int recTicks = Integer.parseInt(recTicksField.getText());
            int sync = Integer.parseInt(syncField.getText());
            int instances = Integer.parseInt(instancesField.getText());
            boolean windowed = windowedCB.isSelected();
            int intervallDuration = Integer.parseInt(intervalDurationField.getText());
            int generateIntervall = Integer.parseInt(intervalSizeField.getText());
            int overlap = Integer.parseInt(overlapField.getText());

            double w = getProbFromText(wField);
            double a = getProbFromText(aField);
            double s = getProbFromText(sField);
            double d = getProbFromText(dField);
            double jump = getProbFromText(jumpField);
            double sprint = getProbFromText(sprintField);
            double sneak = getProbFromText(sneakField);
            double fChange = getProbFromText(facingField);

            BruteforceOptions bruteforceOptions = new BruteforceOptions();
            bruteforceOptions.apply(numberOfTrials, ticksPerTrial, repetitions, dimension, stopOnFind, intervallOfLastShown,
                    recTicks, instances, sync, windowed, intervallDuration, generateIntervall, overlap);

            InputGenerator inputGenerator = new InputGenerator();
            inputGenerator.apply(w, a, s, d, jump, sprint, sneak, fChange);

            multiThreadBruteforcer.setEndBlock(endBlock);
            multiThreadBruteforcer.addBruteforceOptions(bruteforceOptions);
            multiThreadBruteforcer.addInputGenerator(inputGenerator);

            multiThreadBruteforcer.start();
        } catch (NumberFormatException e) {
            System.err.println("Error: Input values are not correctly formatted: " + e.getMessage());
        }
    }

    private void preCompute() {
        if (this.endBlock == null) return;
        if (this.startBlock == null) {
            this.startBlock = blockManager.getBlock(player.startPos.copy());
        }

        multiThreadBruteforcer.calculateBoundaries(startBlock.getVec3(), endBlock.getVec3());
    }

    private void addPathBlock() {
        application.minecraftGUI.setPathBlock();
    }

    private void onEndBlockClick() {
        application.minecraftGUI.setEndBlock();
    }

    private void onStartBlockClick() {
        application.minecraftGUI.setStartBlock();
    }

    private void cancelBruteforce() {
        multiThreadBruteforcer.cancelBruteforce();
    }

    private <T extends Node> T addUIPair(String labelName, T node, int column1, int column2, int row) {
        Label label = new Label(labelName);
        gridPane.add(label, column1, row);
        gridPane.add(node, column2, row);
        return node;
    }

    private Button addButton(Button button, int column, int row, int width) {
        gridPane.add(button, column, row, width, 1);
        return button;
    }

    private double getProbFromText(TextField field) {
        return field.getText().isEmpty() ? 0.0 : Double.parseDouble(field.getText()) / 100;
    }

}
