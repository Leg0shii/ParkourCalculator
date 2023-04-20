package de.legoshi.parkourcalculator.gui.debug.menu;

import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class ScreenSettings extends TitledPane {

    private static Label previewBlockLabel = new Label("Preview Block");
    private static CheckBox previewBlockCB = new CheckBox();

    private static Label pathCollisionLabel = new Label("Collision on path move");
    private static CheckBox pathCollisionCB = new CheckBox();

    public ScreenSettings() {
        Text titleText = new Text("Screen Settings");
        titleText.setFill(Color.WHITE);
        setGraphic(titleText);

        HBox previewHBox = new HBox();
        previewHBox.setSpacing(10);
        previewHBox.setAlignment(Pos.BASELINE_LEFT);
        previewHBox.getChildren().addAll(previewBlockLabel, previewBlockCB);

        HBox pathHBox = new HBox();
        pathHBox.setSpacing(10);
        pathHBox.setAlignment(Pos.BASELINE_LEFT);
        pathHBox.getChildren().addAll(pathCollisionLabel, pathCollisionCB);

        VBox vBox = new VBox(previewHBox, pathHBox);

        previewBlockCB.setSelected(true);

        setContent(vBox);
    }

    public static boolean isPathCollision() {
        return pathCollisionCB.isSelected();
    }

    public static boolean isPreviewMode() {
        return previewBlockCB.isSelected();
    }

}
