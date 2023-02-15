package de.legoshi.parkourcalculator.gui;

import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.Getter;

@Getter
public class ConnectionGUI extends GridPane {

    private Stage stage;

    private Label northLabel = new Label("North: ");
    private static CheckBox checkBoxNorth = new CheckBox();

    private Label eastLabel = new Label("East: ");
    private static CheckBox checkBoxEast = new CheckBox();

    private Label southLabel = new Label("South: ");
    private static CheckBox checkBoxSouth = new CheckBox();

    private Label westLabel = new Label("West: ");
    private static CheckBox checkBoxWest = new CheckBox();

    public ConnectionGUI() {
        checkBoxNorth.setSelected(true);

        this.add(northLabel, 0, 0);
        this.add(checkBoxNorth, 1, 0);
        this.add(eastLabel, 0, 1);
        this.add(checkBoxEast, 1, 1);
        this.add(southLabel, 0, 2);
        this.add(checkBoxSouth, 1, 2);
        this.add(westLabel, 0, 3);
        this.add(checkBoxWest, 1, 3);
    }

    public void show() {
        if (stage == null) {
            this.stage = new Stage();
            stage.setTitle("Select connection facing");
            stage.setScene(new Scene(this, 450, 450));
            stage.show();
            return;
        }
        stage.show();
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

}
