package de.legoshi.parkourcalculator.gui.debug.menu;

import de.legoshi.parkourcalculator.gui.debug.CoordinateScreen;
import de.legoshi.parkourcalculator.util.ConfigReader;
import de.legoshi.parkourcalculator.util.NumberHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.Objects;

public class ScreenSettings extends TitledPane {

    private static final CheckBox previewBlockCB = new CheckBox();
    private static final CheckBox pathCollisionCB = new CheckBox();
    private static final TextField coordinatePrecTF = new TextField();
    private static int precision;

    private final CoordinateScreen coordinateScreen;

    public ScreenSettings(ConfigReader configReader, CoordinateScreen coordinateScreen) {
        this.coordinateScreen = coordinateScreen;

        Text titleText = new Text("Screen Settings");
        titleText.setFill(Color.WHITE);
        setGraphic(titleText);

        HBox previewHBox = addTuple(new Label("Preview Block"), previewBlockCB);
        HBox pathHBox = addTuple(new Label("Collision on path move"), pathCollisionCB);
        HBox precHBox = addTuple(new Label("Coordinate Precision"), coordinatePrecTF);
        VBox vBox = new VBox(previewHBox, pathHBox, precHBox);
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.setAlignment(Pos.BASELINE_CENTER);

        boolean previewBlock = configReader.getBooleanProperty("previewBlock");
        boolean pathCollision = configReader.getBooleanProperty("pathCollision");
        precision = configReader.getIntProperty("coordinatePrecision");

        previewBlockCB.setSelected(previewBlock);
        pathCollisionCB.setSelected(pathCollision);

        coordinatePrecTF.setText("" + precision);
        coordinatePrecTF.setOnKeyTyped(keyEvent -> coordinateScreen.updatePrecision());

        coordinateScreen.updatePrecision();
        setContent(vBox);
    }

    public static boolean isPathCollision() {
        return pathCollisionCB.isSelected();
    }

    public static boolean isPreviewMode() {
        return previewBlockCB.isSelected();
    }

    public static int getCoordinatePrecision() {
        Double d = NumberHelper.parseDouble(coordinatePrecTF.getText());
        int val = (int) (d == null ? precision : d);
        precision = val;
        return val;
    }

    private HBox addTuple(Node label, Node checkBox) {
        HBox pathHBox = new HBox();
        pathHBox.setSpacing(10);
        pathHBox.setAlignment(Pos.BASELINE_LEFT);
        pathHBox.getChildren().addAll(label, checkBox);
        return pathHBox;
    }

}
