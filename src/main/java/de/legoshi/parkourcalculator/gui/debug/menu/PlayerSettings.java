package de.legoshi.parkourcalculator.gui.debug.menu;

import de.legoshi.parkourcalculator.gui.debug.CoordinateScreen;
import de.legoshi.parkourcalculator.parkour.PositionVisualizer;
import de.legoshi.parkourcalculator.parkour.simulator.MovementEngine;
import de.legoshi.parkourcalculator.parkour.simulator.Player;
import de.legoshi.parkourcalculator.parkour.simulator.PlayerTickInformation;
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

    public PlayerSettings(CoordinateScreen coordinateScreen, MovementEngine movementEngine, PositionVisualizer positionVisualizer) {
        this.movementEngine = movementEngine;
        this.positionVisualizer = positionVisualizer;
        this.coordinateScreen = coordinateScreen;

        Text titleText = new Text("Player Settings");
        titleText.setFill(Color.WHITE);
        setGraphic(titleText);

        // Create the text fields
        xPosField = new TextField();
        xPosField.setPromptText("X Position");

        yPosField = new TextField();
        yPosField.setPromptText("Y Position");

        zPosField = new TextField();
        zPosField.setPromptText("Z Position");

        xVelField = new TextField();
        xVelField.setPromptText("X Velocity");

        yVelField = new TextField();
        yVelField.setPromptText("Y Velocity");

        zVelField = new TextField();
        zVelField.setPromptText("Z Velocity");

        // Create a GridPane to hold the text fields
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 0, 10));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(new Label("Position:"), 0, 0);
        gridPane.add(xPosField, 0, 1);
        gridPane.add(yPosField, 0, 2);
        gridPane.add(zPosField, 0, 3);

        gridPane.add(new Label("Velocity:"), 1, 0);
        gridPane.add(xVelField, 1, 1);
        gridPane.add(yVelField, 1, 2);
        gridPane.add(zVelField, 1, 3);

        // Create the button to apply values
        Button getButton = new Button("Get values");
        getButton.setOnAction(event -> {
            // Get values from somewhere (e.g. a game engine) and set them in the text fields
            PlayerTickInformation ptiC = movementEngine.playerTickInformations.get(0);
            this.xPosField.setText(-ptiC.getPosition().x + "");
            this.yPosField.setText(ptiC.getPosition().y + "");
            this.zPosField.setText(ptiC.getPosition().z + "");

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
            double value = Double.parseDouble(xPosField.getText())*(-1);
            Vec3 oldPos = movementEngine.player.getStartPos().copy();
            Vec3 newPos = new Vec3(value, oldPos.y, oldPos.z);
            movementEngine.player.setStartPos(newPos);
            syncPathAndScreen();
        });
        this.yPosField.setOnKeyTyped(keyEvent -> {
            double value = Double.parseDouble(yPosField.getText());
            Vec3 oldPos = movementEngine.player.getStartPos().copy();
            Vec3 newPos = new Vec3(oldPos.x, value, oldPos.z);
            movementEngine.player.setStartPos(newPos);
            syncPathAndScreen();
        });
        this.zPosField.setOnKeyTyped(keyEvent -> {
            double value = Double.parseDouble(zPosField.getText());
            Vec3 oldPos = movementEngine.player.getStartPos().copy();
            Vec3 newPos = new Vec3(oldPos.x, oldPos.y, value);
            movementEngine.player.setStartPos(newPos);
            syncPathAndScreen();
        });
        this.xVelField.setOnKeyTyped(keyEvent -> {
            double value = Double.parseDouble(xVelField.getText())*(-1);
            Vec3 oldPos = movementEngine.player.getStartVel().copy();
            Vec3 newPos = new Vec3(value, oldPos.y, oldPos.z);
            movementEngine.player.setStartVel(newPos);
            syncPathAndScreen();
        });
        this.yVelField.setOnKeyTyped(keyEvent -> {
            double value = Double.parseDouble(yVelField.getText());
            Vec3 oldPos = movementEngine.player.getStartVel().copy();
            Vec3 newPos = new Vec3(oldPos.x, value, oldPos.z);
            movementEngine.player.setStartVel(newPos);
            syncPathAndScreen();
        });
        this.zVelField.setOnKeyTyped(keyEvent -> {
            double value = Double.parseDouble(zVelField.getText());
            Vec3 oldPos = movementEngine.player.getStartVel().copy();
            Vec3 newPos = new Vec3(oldPos.x, oldPos.y, value);
            movementEngine.player.setStartVel(newPos);
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

}
