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

    private final Button endBlockButton;
    private final Button bruteforceButton;
    private final Button resetButton;

    @Getter public ABlock endBlock;

    public BruteforceSettings(Application application) {
        this.application = application;
        this.bruteforcer = new Bruteforcer(application.positionVisualizer, application.inputTickManager, application.currentParkour);
        Text titleText = new Text("Bruteforce Settings");
        titleText.setFill(Color.WHITE);
        setGraphic(titleText);

        // Buttons
        endBlockButton = new Button("Select Land Block");
        bruteforceButton = new Button("Bruteforce");
        resetButton = new Button("Reset");

        // Add components to the GridPane
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        gridPane.add(endBlockButton, 1, 2);

        // Start and End labels
        Label endLabel = new Label("End");
        gridPane.add(endLabel, 1, 0);

        HBox bruteForceBox = new HBox(5, bruteforceButton);
        bruteForceBox.setAlignment(Pos.BASELINE_CENTER);
        gridPane.add(bruteForceBox, 0, 4);
        gridPane.add(resetButton, 1, 4);

        registerNodes();
        setContent(gridPane);
    }

    public void setEndBlock(ABlock block) {
        if (this.endBlock != null) this.endBlock.resetAndApplyMaterialColor();
        this.endBlock = block;
        this.endBlock.applyMaterialColor(Color.RED);
    }

    public void reset() {
        if (endBlock != null) {
            this.endBlock.resetAndApplyMaterialColor();
            this.endBlock = null;
        }
    }

    private void registerNodes() {
        endBlockButton.setOnAction(event -> onEndBlockClick());
        bruteforceButton.setOnAction(event -> bruteForceAction());
        resetButton.setOnAction(event -> reset());
    }

    private void bruteForceAction() {
        if (this.endBlock == null) {
            return;
        }
        bruteforcer.applyAndBruteforce(this.endBlock);
    }

    private void onEndBlockClick() {
        application.minecraftGUI.setEndBlock();
    }

}
