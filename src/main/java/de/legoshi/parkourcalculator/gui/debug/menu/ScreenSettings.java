package de.legoshi.parkourcalculator.gui.debug.menu;

import de.legoshi.parkourcalculator.gui.debug.CoordinateScreen;
import de.legoshi.parkourcalculator.util.ConfigReader;
import de.legoshi.parkourcalculator.util.NumberHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class ScreenSettings extends TitledPane {

    private static final CheckBox previewBlockCB = new CheckBox();
    private static final CheckBox pathCollisionCB = new CheckBox();
    private static final CheckBox realVelCB = new CheckBox();
    private static final TextField coordinatePrecTF = new TextField();

    private static int precision;

    public ScreenSettings(ConfigReader configReader, PlayerSettings playerSettings, CoordinateScreen coordinateScreen) {
        Text titleText = new Text("Screen Settings");
        titleText.setFill(Color.WHITE);
        setGraphic(titleText);

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(new Label("Preview Block"), 0, 0);
        gridPane.add(previewBlockCB, 1, 0);

        gridPane.add(new Label("Collision on path move"), 0, 1);
        gridPane.add(pathCollisionCB, 1, 1);

        gridPane.add(new Label("Real Velocity"), 0, 2);
        gridPane.add(realVelCB, 1, 2);

        gridPane.add(new Label("Coordinate Precision"), 0, 3);
        gridPane.add(coordinatePrecTF, 1, 3);

        boolean previewBlock = configReader.getBooleanProperty("previewBlock");
        boolean pathCollision = configReader.getBooleanProperty("pathCollision");
        boolean tickVel = configReader.getBooleanProperty("realVelocity");
        precision = configReader.getIntProperty("coordinatePrecision");

        previewBlockCB.setSelected(previewBlock);
        pathCollisionCB.setSelected(pathCollision);
        realVelCB.setSelected(tickVel);

        coordinatePrecTF.setText("" + precision);
        coordinatePrecTF.setOnKeyTyped(keyEvent -> coordinateScreen.updatePrecision());
        realVelCB.setOnAction(keyEvent -> coordinateScreen.update());

        coordinateScreen.updatePrecision();
        playerSettings.updatePlayerSettings();
        setContent(gridPane);
    }

    public static boolean isPathCollision() {
        return pathCollisionCB.isSelected();
    }

    public static boolean isPreviewMode() {
        return previewBlockCB.isSelected();
    }

    public static boolean isRealVelocity() {
        return realVelCB.isSelected();
    }

    public static int getCoordinatePrecision() {
        Double d = NumberHelper.parseDouble(coordinatePrecTF.getText());
        int val = (int) (d == null ? precision : d);
        precision = val;
        return val;
    }

}
