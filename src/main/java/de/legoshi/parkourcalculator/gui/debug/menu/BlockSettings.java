package de.legoshi.parkourcalculator.gui.debug.menu;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class BlockSettings extends TitledPane {

    private final Label floorLabel = new Label("Floor: ");
    private final Label ceilingLabel = new Label("Ceiling: ");
    private final Label northLabel = new Label("North: ");
    private final Label eastLabel = new Label("East: ");
    private final Label southLabel = new Label("South: ");
    private final Label westLabel = new Label("West: ");

    private static final CheckBox checkBoxFloor = new CheckBox();
    private static final CheckBox checkBoxCeiling = new CheckBox();
    private static final CheckBox checkBoxNorth = new CheckBox();
    private static final CheckBox checkBoxEast = new CheckBox();
    private static final CheckBox checkBoxSouth = new CheckBox();
    private static final CheckBox checkBoxWest = new CheckBox();

    private static final ComboBox<Integer> tiersSelector = new ComboBox<>();
    private static final ColorPicker colorSelector = new ColorPicker(Color.LIGHTGRAY);

    public BlockSettings() {
        Text titleText = new Text("Block Settings");
        titleText.setFill(Color.WHITE);
        setGraphic(titleText);

        tiersSelector.getItems().addAll(0, 1, 2, 3, 4, 5, 6, 7);
        tiersSelector.setValue(0);

        // Create a GridPane to hold the selectors
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(new Label("Tiers:"), 0, 2, 5, 1);
        gridPane.add(tiersSelector, 1, 2, 5, 1);

        gridPane.add(new Label("Color:"), 0, 3, 5, 1);
        gridPane.add(colorSelector, 1, 3, 5, 1);

        // Add the GridPane to a VBox
        VBox vBox = new VBox(gridPane);
        vBox.setAlignment(Pos.CENTER);

        setUPLabelCheckboxes(gridPane);

        // Set the content of the titledPane to the VBox
        setContent(vBox);
    }

    private void setUPLabelCheckboxes(GridPane gridPane) {
        gridPane.add(ceilingLabel, 0, 0);
        gridPane.add(checkBoxCeiling, 1, 0);
        gridPane.add(floorLabel, 0, 1);
        gridPane.add(checkBoxFloor, 1, 1);
        gridPane.add(northLabel, 2, 0);
        gridPane.add(checkBoxNorth, 3, 0);
        gridPane.add(eastLabel, 2, 1);
        gridPane.add(checkBoxEast, 3, 1);
        gridPane.add(southLabel, 4, 0);
        gridPane.add(checkBoxSouth, 5, 0);
        gridPane.add(westLabel, 4, 1);
        gridPane.add(checkBoxWest, 5, 1);
    }

    public static boolean isNorth() {
        return checkBoxNorth.isSelected();
    }

    public static boolean isEast() {
        return checkBoxEast.isSelected();
    }

    public static boolean isSouth() {
        return checkBoxSouth.isSelected();
    }

    public static boolean isWest() {
        return checkBoxWest.isSelected();
    }

    public static boolean isFloor() {
        return checkBoxFloor.isSelected();
    }

    public static boolean isFlip() {
        return checkBoxCeiling.isSelected();
    }

    public static int getTier() {
        return tiersSelector.getValue();
    }

    public static Color getColor() {
        return colorSelector.getValue();
    }

}
