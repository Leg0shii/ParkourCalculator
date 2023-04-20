package de.legoshi.parkourcalculator.gui.debug.menu;

import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class ScreenSettings extends TitledPane {

    private static Label previewBlockLabel = new Label("Preview Block");
    private static CheckBox previewBlockCB = new CheckBox();

    public ScreenSettings() {
        Text titleText = new Text("Screen Settings");
        titleText.setFill(Color.WHITE);
        setGraphic(titleText);

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.BASELINE_LEFT);
        hBox.getChildren().addAll(previewBlockLabel, previewBlockCB);

        previewBlockCB.setSelected(true);

        setContent(hBox);
    }

    public static boolean isPreviewMode() {
        return previewBlockCB.isSelected();
    }

}
