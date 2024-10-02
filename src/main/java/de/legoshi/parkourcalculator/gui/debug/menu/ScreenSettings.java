package de.legoshi.parkourcalculator.gui.debug.menu;

import de.legoshi.parkourcalculator.config.Configurable;
import de.legoshi.parkourcalculator.gui.debug.CoordinateScreen;
import de.legoshi.parkourcalculator.config.ConfigProperties;
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
import lombok.Getter;

public class ScreenSettings extends TitledPane implements Configurable {

    @Getter private static final CheckBox previewBlockCB = new CheckBox();
    @Getter private static final CheckBox pathCollisionCB = new CheckBox();
    @Getter private static final CheckBox realVelCB = new CheckBox();
    @Getter private static final TextField coordinatePrecTF = new TextField();

    public int precision;

    public ScreenSettings(PlayerSettings playerSettings, CoordinateScreen coordinateScreen) {
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

        coordinatePrecTF.setOnKeyTyped(keyEvent -> updateCoordinatePrecision(coordinateScreen));
        realVelCB.setOnAction(keyEvent -> coordinateScreen.update());

        updateCoordinatePrecision(coordinateScreen);
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

    private void updateCoordinatePrecision(CoordinateScreen coordinateScreen) {
        precision = getCoordinatePrecision();
        coordinateScreen.updatePrecision(precision);
    }

    private int getCoordinatePrecision() {
        Double d = NumberHelper.parseDouble(coordinatePrecTF.getText());
        return (int) (d == null ? precision : d);
    }

    @Override
    public void applyConfigValues(ConfigProperties configProperties) {
        precision = configProperties.getCoordinatePrecision();
        previewBlockCB.setSelected(configProperties.isPreviewBlock());
        pathCollisionCB.setSelected(configProperties.isPathCollision());
        realVelCB.setSelected(configProperties.isRealVelocity());
        coordinatePrecTF.setText(String.valueOf(precision));
    }

}
