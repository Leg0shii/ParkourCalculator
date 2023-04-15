package de.legoshi.parkourcalculator.gui.menu;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class BlockSettings extends TitledPane {

    // update screen when block selected
    private ComboBox<String> facingSelector;
    private ComboBox<Integer> tiersSelector;
    private Slider slipperinessSelector;
    private ColorPicker colorSelector;

    public BlockSettings() {
        Text titleText = new Text("Block Settings");
        titleText.setFill(Color.WHITE);
        setGraphic(titleText);

        // Create the selectors
        facingSelector = new ComboBox<>();
        facingSelector.getItems().addAll("North", "West", "South", "East");

        tiersSelector = new ComboBox<>();
        tiersSelector.getItems().addAll(0, 1, 2, 3, 4, 5, 6, 7);

        slipperinessSelector = new Slider(0, 1, 0.5);
        slipperinessSelector.setShowTickLabels(true);
        slipperinessSelector.setShowTickMarks(true);
        slipperinessSelector.setMajorTickUnit(0.25);
        slipperinessSelector.setBlockIncrement(0.1);

        colorSelector = new ColorPicker(Color.BLACK);

        // Create a GridPane to hold the selectors
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(new Label("Facing:"), 0, 0);
        gridPane.add(facingSelector, 1, 0);

        gridPane.add(new Label("Tiers:"), 0, 1);
        gridPane.add(tiersSelector, 1, 1);

        gridPane.add(new Label("Slipperiness:"), 0, 2);
        gridPane.add(slipperinessSelector, 1, 2);

        gridPane.add(new Label("Color:"), 0, 3);
        gridPane.add(colorSelector, 1, 3);

        // Add the GridPane to a VBox
        VBox vBox = new VBox(gridPane);
        vBox.setAlignment(Pos.CENTER);

        // Set the content of the titledPane to the VBox
        setContent(vBox);
    }

    // Method to exchange facing selector values
    public void setFacingSelectorValues(String[] values) {
        facingSelector.getItems().setAll(values);
    }

    // Method to exchange tiers selector values based on an int value
    public void setTiersSelectorValues(int numTiers) {
        tiersSelector.getItems().setAll(createTiersSelectorValues(numTiers));
    }

    // Private helper method to create the tiers selector values based on an int value
    private Integer[] createTiersSelectorValues(int numTiers) {
        Integer[] values = new Integer[numTiers + 1];
        for (int i = 0; i <= numTiers; i++) {
            values[i] = i;
        }
        return values;
    }

    // Getters for the selectors
    public String getFacing() {
        return facingSelector.getValue();
    }

    public int getTiers() {
        return tiersSelector.getValue();
    }

    public double getSlipperiness() {
        return slipperinessSelector.getValue();
    }

    public Color getColor() {
        return colorSelector.getValue();
    }

}
