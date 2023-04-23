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

    private final TextField facing;

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
        xPosField = new TextField("" + -startPos.x);
        xPosField.setPromptText("X Position");

        yPosField = new TextField("" + startPos.y);
        yPosField.setPromptText("Y Position");

        zPosField = new TextField("" + startPos.z);
        zPosField.setPromptText("Z Position");

        xVelField = new TextField("" + -startVel.x);
        xVelField.setPromptText("X Velocity");

        yVelField = new TextField("" + startVel.y);
        yVelField.setPromptText("Y Velocity");

        zVelField = new TextField("" + startVel.z);
        zVelField.setPromptText("Z Velocity");

        facing = new TextField("" + movementEngine.player.getYAW());
        facing.setPromptText("Facing");

        // Create a GridPane to hold the text fields
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 0, 10));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(new Label("Facing:"), 0, 0);
        gridPane.add(facing, 0, 1);

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
        getButton.setOnAction(event -> {
            // Get values from somewhere (e.g. a game engine) and set them in the text fields
            PlayerTickInformation ptiC = movementEngine.playerTickInformations.get(0);
            this.xPosField.setText(-ptiC.getPosition().x + "");
            this.yPosField.setText(ptiC.getPosition().y + "");
            this.zPosField.setText(ptiC.getPosition().z + "");
            this.facing.setText(ptiC.getFacing() + "");

            Player player = movementEngine.player;
            this.xVelField.setText(-player.getStartVel().x + "");
            this.yVelField.setText(player.getStartVel().y + "");
            this.zVelField.setText(player.getStartVel().z + "");
        });

        Button copyButton = new Button("Copy to Clipboard");
        copyButton.setOnAction(event -> {
            double x = tryParseDouble(xPosField.getText())*(-1);
            double y = tryParseDouble(yPosField.getText());
            double z = tryParseDouble(zPosField.getText());
            double facing = movementEngine.playerTickInformations.get(0).getFacing();

            String tpCommand = "/tp " + x + " " + y + " " + z + " " + facing + " 90";
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

    private void registerTextFieldChanges() {
        this.xPosField.setOnKeyTyped(keyEvent -> {
            Vec3 oldPos = movementEngine.player.getStartPos().copy();
            double value = getDouble(oldPos.x, xPosField.getText())*(-1);
            Vec3 newPos = new Vec3(value, oldPos.y, oldPos.z);
            movementEngine.player.setStartPos(newPos);
            syncPathAndScreen();
        });
        this.yPosField.setOnKeyTyped(keyEvent -> {
            Vec3 oldPos = movementEngine.player.getStartPos().copy();
            double value = getDouble(oldPos.y, yPosField.getText());
            Vec3 newPos = new Vec3(oldPos.x, value, oldPos.z);
            movementEngine.player.setStartPos(newPos);
            syncPathAndScreen();
        });
        this.zPosField.setOnKeyTyped(keyEvent -> {
            Vec3 oldPos = movementEngine.player.getStartPos().copy();
            double value = getDouble(oldPos.z, zPosField.getText());
            Vec3 newPos = new Vec3(oldPos.x, oldPos.y, value);
            movementEngine.player.setStartPos(newPos);
            syncPathAndScreen();
        });
        this.facing.setOnKeyTyped(keyEvent -> {
            float oldFacing = movementEngine.player.getStartYAW();
            float newFacing = getFloat(oldFacing, facing.getText());
            movementEngine.player.setStartYAW(newFacing);
            syncPathAndScreen();
        });
        this.xVelField.setOnKeyTyped(keyEvent -> {
            Vec3 oldVel = movementEngine.player.getStartVel().copy();
            double value = getDouble(oldVel.x, xVelField.getText())*(-1);
            Vec3 newVel = new Vec3(value, oldVel.y, oldVel.z);
            movementEngine.player.setStartVel(newVel);
            syncPathAndScreen();
        });
        this.yVelField.setOnKeyTyped(keyEvent -> {
            Vec3 oldVel = movementEngine.player.getStartVel().copy();
            double value = getDouble(oldVel.y, yVelField.getText());
            Vec3 newVel = new Vec3(oldVel.x, value, oldVel.z);
            movementEngine.player.setStartVel(newVel);
            syncPathAndScreen();
        });
        this.zVelField.setOnKeyTyped(keyEvent -> {
            Vec3 oldVel = movementEngine.player.getStartVel().copy();
            double value = getDouble(oldVel.z, zVelField.getText());
            Vec3 newVel = new Vec3(oldVel.x, oldVel.y, value);
            movementEngine.player.setStartVel(newVel);
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
        System.out.println(d);
        return d == null ? oldVal : d;
    }

}
