package de.legoshi.parkourcalculator.gui.debug.menu;

import de.legoshi.parkourcalculator.gui.debug.CoordinateScreen;
import de.legoshi.parkourcalculator.parkour.PositionVisualizer;
import de.legoshi.parkourcalculator.parkour.simulator.MovementEngine;
import de.legoshi.parkourcalculator.parkour.simulator.Player;
import de.legoshi.parkourcalculator.parkour.simulator.PlayerTickInformation;
import de.legoshi.parkourcalculator.util.NumberHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class PlayerSettings extends TitledPane {

    private final MovementEngine movementEngine;
    private final PositionVisualizer positionVisualizer;
    private final CoordinateScreen coordinateScreen;

    private final TextField xPosField;
    private final TextField yPosField;
    private final TextField zPosField;
    private final TextField xVelField;
    private final TextField yVelField;
    private final TextField zVelField;

    private final TextField facingYaw;
    private final TextField facingPitch;

    public PlayerSettings(CoordinateScreen coordinateScreen, MovementEngine movementEngine, PositionVisualizer positionVisualizer) {
        this.movementEngine = movementEngine;
        this.positionVisualizer = positionVisualizer;
        this.coordinateScreen = coordinateScreen;

        Text titleText = new Text("Player Settings");
        titleText.setFill(Color.WHITE);
        setGraphic(titleText);

        Vec3 startPos = movementEngine.getPlayer().getStartPos();
        Vec3 startVel = movementEngine.getPlayer().getStartVel();

        // Create the text fields
        xPosField = new TextField("" + replaceNegZero(-startPos.x));
        xPosField.setPromptText("X Position");

        yPosField = new TextField("" + startPos.y);
        yPosField.setPromptText("Y Position");

        zPosField = new TextField("" + startPos.z);
        zPosField.setPromptText("Z Position");

        xVelField = new TextField(replaceNegZero((ScreenSettings.isRealVelocity() ? 0 : startVel.x*(-1))) + "");
        xVelField.setPromptText("X Velocity");

        yVelField = new TextField((ScreenSettings.isRealVelocity() ? 0 : startVel.y) + "");
        yVelField.setPromptText("Y Velocity");

        zVelField = new TextField((ScreenSettings.isRealVelocity() ? 0 : startVel.z) + "");
        zVelField.setPromptText("Z Velocity");

        facingYaw = new TextField("" + movementEngine.player.getYAW());
        facingYaw.setPromptText("Yaw");

        facingPitch = new TextField("60.0");
        facingPitch.setPromptText("Pitch");

        // Create a GridPane to hold the text fields
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 0, 10));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(new Label("Yaw:"), 0, 0);
        gridPane.add(facingYaw, 0, 1);
        gridPane.add(new Label("Pitch:"), 1, 0);
        gridPane.add(facingPitch, 1, 1);

        gridPane.add(new Label("Position:"), 0, 2);
        gridPane.add(xPosField, 0, 3);
        gridPane.add(yPosField, 0, 4);
        gridPane.add(zPosField, 0, 5);

        gridPane.add(new Label("Velocity:"), 1, 2);
        gridPane.add(xVelField, 1, 3);
        gridPane.add(yVelField, 1, 4);
        gridPane.add(zVelField, 1, 5);

        // Create the button to apply values
        Button getButton = new Button("Get values");
        getButton.setOnAction(event -> updatePlayerSettings());

        Button copyButton = new Button("Copy to Clipboard");
        copyButton.setOnAction(event -> {
            double x = tryParseDouble(xPosField.getText());
            double y = tryParseDouble(yPosField.getText());
            double z = tryParseDouble(zPosField.getText());
            float yaw = tryParseFloat(facingYaw.getText());
            float pitch = tryParseFloat(facingPitch.getText());

            String tpCommand = "/tp " + x + " " + y + " " + z + " " + yaw + " " + pitch;
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection stringSelection = new StringSelection(tpCommand);
            clipboard.setContents(stringSelection, null);
        });

        registerTextFieldChanges();

        // Add the GridPane and button to a VBox
        HBox hBox = new HBox(getButton, copyButton);
        hBox.setSpacing(30);
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(0, 5, 5, 0));

        VBox vBox = new VBox(gridPane, hBox);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);

        // Set the content of the titledPane to the VBox
        ScrollPane scrollPane = new ScrollPane(vBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new Insets(0, 0, 10, 0));
        setContent(scrollPane);
    }

    public void updatePlayerSettings() {
        Vec3 startPos = movementEngine.getPlayer().getStartPos();
        Vec3 startVel = movementEngine.player.getStartVel();

        this.xPosField.setText(replaceNegZero(startPos.x*(-1))+ "");
        this.yPosField.setText(startPos.y + "");
        this.zPosField.setText(startPos.z + "");
        this.facingYaw.setText(replaceNegZero(movementEngine.player.getStartYAW()*(-1)) + "");

        Player player = movementEngine.player;
        this.xVelField.setText(replaceNegZero(startVel.x*(-1)) + "");
        this.yVelField.setText(startVel.y + "");
        this.zVelField.setText(startVel.z + "");

        player.setStartVel(startVel.copy());
        syncPathAndScreen();
    }

    private void registerTextFieldChanges() {
        this.xPosField.setOnKeyTyped(keyEvent -> {
            Vec3 oldPos = movementEngine.player.getStartPos().copy();
            double value = getDouble(oldPos.x, xPosField.getText())*(-1);
            movementEngine.player.setStartPos(new Vec3(value, oldPos.y, oldPos.z));
            syncPathAndScreen();
        });
        this.yPosField.setOnKeyTyped(keyEvent -> {
            Vec3 oldPos = movementEngine.player.getStartPos().copy();
            double value = getDouble(oldPos.y, yPosField.getText());
            movementEngine.player.setStartPos(new Vec3(oldPos.x, value, oldPos.z));
            syncPathAndScreen();
        });
        this.zPosField.setOnKeyTyped(keyEvent -> {
            Vec3 oldPos = movementEngine.player.getStartPos().copy();
            double value = getDouble(oldPos.z, zPosField.getText());
            movementEngine.player.setStartPos(new Vec3(oldPos.x, oldPos.y, value));
            syncPathAndScreen();
        });
        this.facingYaw.setOnKeyTyped(keyEvent -> {
            float oldFacing = movementEngine.player.getStartYAW();
            float newFacing = getFloat(oldFacing, facingYaw.getText());
            movementEngine.player.setStartYAW(newFacing);
            syncPathAndScreen();
        });
        this.xVelField.setOnKeyTyped(keyEvent -> {
            Vec3 oldVel = movementEngine.player.getStartVel().copy();
            double value = getDouble(oldVel.x, xVelField.getText())*(-1);
            movementEngine.player.setStartVel(new Vec3(value, oldVel.y, oldVel.z));
            syncPathAndScreen();
        });
        this.yVelField.setOnKeyTyped(keyEvent -> {
            Vec3 oldVel = movementEngine.player.getStartVel().copy();
            double value = getDouble(oldVel.y, yVelField.getText());
            movementEngine.player.setStartVel(new Vec3(oldVel.x, value, oldVel.z));
            syncPathAndScreen();
        });
        this.zVelField.setOnKeyTyped(keyEvent -> {
            Vec3 oldVel = movementEngine.player.getStartVel().copy();
            double value = getDouble(oldVel.z, zVelField.getText());
            movementEngine.player.setStartVel(new Vec3(oldVel.x, oldVel.y, value));
            syncPathAndScreen();
        });
    }

    private double tryParseDouble(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException | NullPointerException e) {
            return 0.0;
        }
    }

    private float tryParseFloat(String text) {
        try {
            return Float.parseFloat(text);
        } catch (NumberFormatException | NullPointerException e) {
            return 0.0F;
        }
    }

    private void syncPathAndScreen() {
        positionVisualizer.update(null, null);
        coordinateScreen.update(null, null);
    }

    private double getDouble(double oldVal, String text) {
        Double d = NumberHelper.parseDouble(text);
        return d == null ? oldVal : d;
    }

    private float getFloat(float oldVal, String text) {
        Float d = NumberHelper.parseFloat(text);
        return d == null ? oldVal : d;
    }

    private String replaceNegZero(double val) {
        String s = val + "";
        if (val == 0.0) s = s.replace("-", "");
        return s;
    }

    private String replaceNegZero(float val) {
        String s = val + "";
        if (val == 0.0) s = s.replace("-", "");
        return s;
    }

}
