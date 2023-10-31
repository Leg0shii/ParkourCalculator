package de.legoshi.parkourcalculator.gui.debug.menu;

import de.legoshi.parkourcalculator.Application;
import de.legoshi.parkourcalculator.ai.Bruteforcer;
import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import lombok.Getter;

public class BruteforceSettings extends TitledPane {
    
    private final Bruteforcer bruteforcer;
    private final Application application;
    
    private final Button setEndBlockButton;
    private final Button removeEndBlockButton;
    
    private final Button bruteforceButton;
    private final Button cancelButton;
    
    private TextField numberOfTrialsField;
    private TextField ticksPerTrialField;
    private TextField repetitionsField;
    private TextField dimensionField;
    private CheckBox stopOnFindField;
    private TextField intervallOfLastShownField;
    
    private TextField wField;
    private TextField aField;
    private TextField sField;
    private TextField dField;
    private TextField jumpField;
    private TextField sneakField;
    private TextField sprintField;
    private TextField facingField;
    
    @Getter
    public ABlock endBlock;
    
    public BruteforceSettings(Application application) {
        this.application = application;
        this.bruteforcer = new Bruteforcer(application);
        Text titleText = new Text("Bruteforce Settings");
        titleText.setFill(Color.WHITE);
        setGraphic(titleText);
        
        setEndBlockButton = new Button("Set LB");
        removeEndBlockButton = new Button("Remove LB");
        
        bruteforceButton = new Button("Bruteforce");
        cancelButton = new Button("Cancel");
        
        // Add components to the GridPane
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        
        gridPane.add(setEndBlockButton, 0, 12, 2, 1);
        gridPane.add(removeEndBlockButton, 2, 12, 2, 1);
        
        gridPane.add(bruteforceButton, 0, 13, 2, 1);
        gridPane.add(cancelButton, 2, 13, 2, 1);
        
        Label numberOfTrialsLabel = new Label("Number Of Trials:");
        numberOfTrialsField = new TextField("100");
        gridPane.add(numberOfTrialsLabel, 0, 2);
        gridPane.add(numberOfTrialsField, 1, 2);
        
        Label ticksPerTrialLabel = new Label("Ticks Per Trial:");
        ticksPerTrialField = new TextField("15");
        gridPane.add(ticksPerTrialLabel, 0, 3);
        gridPane.add(ticksPerTrialField, 1, 3);
        
        Label repetitionsLabel = new Label("Repetitions:");
        repetitionsField = new TextField("10000");
        gridPane.add(repetitionsLabel, 0, 4);
        gridPane.add(repetitionsField, 1, 4);
        
        Label dimensionLabel = new Label("Dimension:");
        dimensionField = new TextField("0.5");
        gridPane.add(dimensionLabel, 0, 5);
        gridPane.add(dimensionField, 1, 5);
        
        // Adding TextField and Label for stopOnFind
        Label stopOnFindLabel = new Label("Stop On Find:");
        stopOnFindField = new CheckBox();
        gridPane.add(stopOnFindLabel, 0, 6);
        gridPane.add(stopOnFindField, 1, 6);
        
        // Adding TextField and Label for intervallOfLastShown
        Label intervallOfLastShownLabel = new Label("Interval Of Last Shown:");
        intervallOfLastShownField = new TextField("100");
        gridPane.add(intervallOfLastShownLabel, 0, 7);
        gridPane.add(intervallOfLastShownField, 1, 7);
        
        Label wProbLabel = new Label("W-%:");
        wField = new TextField("100");
        gridPane.add(wProbLabel, 0, 8);
        gridPane.add(wField, 1, 8);
    
        Label aProbLabel = new Label("A-%:");
        aField = new TextField("10");
        gridPane.add(aProbLabel, 0, 9);
        gridPane.add(aField, 1, 9);
    
        Label sProbLabel = new Label("S-%:");
        sField = new TextField("0");
        gridPane.add(sProbLabel, 0, 10);
        gridPane.add(sField, 1, 10);
    
        Label dProbLabel = new Label("D-%:");
        dField = new TextField("10");
        gridPane.add(dProbLabel, 0, 11);
        gridPane.add(dField, 1, 11);
    
        Label spaceProbLabel = new Label("Jump-%:");
        jumpField = new TextField("10");
        gridPane.add(spaceProbLabel, 2, 8);
        gridPane.add(jumpField, 3, 8);
    
        Label sneakProbLabel = new Label("Sneak-%:");
        sneakField = new TextField("0");
        gridPane.add(sneakProbLabel, 2, 9);
        gridPane.add(sneakField, 3, 9);
    
        Label sprintProbLabel = new Label("Sprint-%:");
        sprintField = new TextField("100");
        gridPane.add(sprintProbLabel, 2, 10);
        gridPane.add(sprintField, 3, 10);
    
        Label facingProbLabel = new Label("Facing-%:");
        facingField = new TextField("2");
        gridPane.add(facingProbLabel, 2, 11);
        gridPane.add(facingField, 3, 11);
        
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
        setEndBlockButton.setOnAction(event -> onEndBlockClick());
        removeEndBlockButton.setOnAction(event -> reset());
        bruteforceButton.setOnAction(event -> bruteForceAction());
        cancelButton.setOnAction(event -> cancelBruteforce());
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
            
            double w = getProbFromText(wField);
            double a = getProbFromText(aField);
            double s = getProbFromText(sField);
            double d = getProbFromText(dField);
            double jump = getProbFromText(jumpField);
            double sprint = getProbFromText(sprintField);
            double sneak = getProbFromText(sneakField);
            double fChange = getProbFromText(facingField);
            
            bruteforcer.applyConfigValues(numberOfTrials, ticksPerTrial, repetitions, dimension, stopOnFind, intervallOfLastShown);
            bruteforcer.applyWASDConfig(w, a, s, d, jump, sprint, sneak, fChange);
            bruteforcer.bruteforce(this.endBlock);
        } catch (NumberFormatException e) {
            System.err.println("Error: Input values are not correctly formatted: " + e.getMessage());
        }
    }
    
    private void onEndBlockClick() {
        application.minecraftGUI.setEndBlock();
    }
    
    private void cancelBruteforce() {
        bruteforcer.cancelBruteforce();
    }
    
    private double getProbFromText(TextField field) {
        return field.getText().isEmpty() ? 0.0 : Double.parseDouble(field.getText())/100;
    }
    
}
