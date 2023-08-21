package de.legoshi.parkourcalculator.gui.debug.menu;

import de.legoshi.parkourcalculator.Application;
import de.legoshi.parkourcalculator.ai.Bruteforcer;
import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import lombok.Getter;

public class BruteforceSettings extends TitledPane {

    private final static String PRECISION = "0.0001";
    private final Bruteforcer bruteforcer;
    private final Application application;

    private final Button startBlockButton;
    private final Button endBlockButton;
    private final Button bruteforceButton;
    private final Button resetButton;

    private final TextField errorTextField;

    @Getter public ABlock startBlock;
    @Getter public ABlock endBlock;

    public BruteforceSettings(Application application) {
        this.application = application;
        this.bruteforcer = new Bruteforcer(application.positionVisualizer, application.inputTickManager, application.currentParkour);
        Text titleText = new Text("Bruteforce Settings");
        titleText.setFill(Color.WHITE);
        setGraphic(titleText);

        // Buttons
        startBlockButton = new Button("Select Jump Block");
        endBlockButton = new Button("Select Land Block");
        bruteforceButton = new Button("Bruteforce");
        resetButton = new Button("Reset");

        // Error TextField
        Label errorLabel = new Label("Bruteforce Accuracy:");
        errorTextField = new TextField(PRECISION);
        errorTextField.setPromptText("0.1 to 1e-15");

        // ToggleGroups for RadioButtons
        ToggleGroup startGroup = new ToggleGroup();
        ToggleGroup endGroup = new ToggleGroup();

        // Add components to the GridPane
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        gridPane.add(startBlockButton, 0, 2);
        gridPane.add(endBlockButton, 1, 2);
        gridPane.add(errorLabel, 0, 3);
        gridPane.add(errorTextField, 1, 3);

        // Start and End labels
        Label startLabel = new Label("Start");
        Label endLabel = new Label("End");
        gridPane.add(startLabel, 0, 0);
        gridPane.add(endLabel, 1, 0);

        HBox bruteForceBox = new HBox(5, bruteforceButton);
        bruteForceBox.setAlignment(Pos.BASELINE_CENTER);
        gridPane.add(bruteForceBox, 0, 4);
        gridPane.add(resetButton, 1, 4);

        registerNodes();
        setContent(gridPane);
    }

    public void setStartBlock(ABlock block) {
        if (this.startBlock != null) this.startBlock.resetAndApplyMaterialColor();
        this.startBlock = block;
        this.startBlock.applyMaterialColor(Color.GREEN);
    }

    public void setEndBlock(ABlock block) {
        if (this.endBlock != null) this.endBlock.resetAndApplyMaterialColor();
        this.endBlock = block;
        this.endBlock.applyMaterialColor(Color.RED);
    }

    public void reset() {
        errorTextField.setText(PRECISION);
        if (startBlock != null) {
            this.startBlock.resetAndApplyMaterialColor();
            this.startBlock = null;
        }
        if (endBlock != null) {
            this.endBlock.resetAndApplyMaterialColor();
            this.endBlock = null;
        }
    }

    private void registerNodes() {
        startBlockButton.setOnAction(event -> onStartBlockClick());
        endBlockButton.setOnAction(event -> onEndBlockClick());
        bruteforceButton.setOnAction(event -> bruteForceAction());
        resetButton.setOnAction(event -> reset());
    }

    private void bruteForceAction() {
        if (this.startBlock == null || this.endBlock == null) return;

        bruteforcer.applyAndBruteforce(
                this.startBlock,
                this.endBlock,
                Double.parseDouble(this.errorTextField.getText())
        );
    }

    private void onStartBlockClick() {
        application.minecraftGUI.setStartBlock();
    }

    private void onEndBlockClick() {
        application.minecraftGUI.setEndBlock();
    }

}
