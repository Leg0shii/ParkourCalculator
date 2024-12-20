package de.legoshi.parkourcalculator.gui.debug.menu;

import de.legoshi.parkourcalculator.gui.debug.CoordinateScreen;
import de.legoshi.parkourcalculator.simulation.Parkour;
import de.legoshi.parkourcalculator.simulation.player.Player;
import de.legoshi.parkourcalculator.simulation.potion.Potion;
import de.legoshi.parkourcalculator.util.PositionVisualizer;
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
import lombok.Getter;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class PlayerSettings extends TitledPane {

    private final Parkour parkour;
    private final PositionVisualizer positionVisualizer;
    private final CoordinateScreen coordinateScreen;

    private final TextField xPosField;
    private final TextField yPosField;
    private final TextField zPosField;
    private final TextField xVelField;
    private final TextField yVelField;
    private final TextField zVelField;

    @Getter private final TextField facingYaw;
    private final TextField facingPitch;

    private final TextField speedField;
    private final TextField slownessField;
    private final TextField jumpField;

    public PlayerSettings(CoordinateScreen coordinateScreen, Parkour parkour, PositionVisualizer positionVisualizer) {
        this.parkour = parkour;
        this.positionVisualizer = positionVisualizer;
        this.coordinateScreen = coordinateScreen;

        Text titleText = new Text("Player Settings");
        titleText.setFill(Color.WHITE);
        setGraphic(titleText);

        Vec3 startPos = parkour.getPlayer().getStartPos();
        Vec3 startVel = parkour.getPlayer().getStartVel();

        // Create the text fields
        xPosField = new TextField("" + NumberHelper.replaceNegZero(-startPos.x));
        xPosField.setPromptText("X Position");

        yPosField = new TextField("" + startPos.y);
        yPosField.setPromptText("Y Position");

        zPosField = new TextField("" + startPos.z);
        zPosField.setPromptText("Z Position");

        xVelField = new TextField(NumberHelper.replaceNegZero((ScreenSettings.isRealVelocity() ? 0 : startVel.x*(-1))) + "");
        xVelField.setPromptText("X Velocity");

        yVelField = new TextField((ScreenSettings.isRealVelocity() ? 0 : startVel.y) + "");
        yVelField.setPromptText("Y Velocity");

        zVelField = new TextField((ScreenSettings.isRealVelocity() ? 0 : startVel.z) + "");
        zVelField.setPromptText("Z Velocity");

        facingYaw = new TextField("" + parkour.getPlayer().getYAW());
        facingYaw.setPromptText("Yaw");

        facingPitch = new TextField("60.0");
        facingPitch.setPromptText("Pitch");

        speedField = new TextField("" + parkour.getPlayer().potionEffects.get(Potion.moveSpeed).getAmplifier());
        speedField.setPromptText("Speed");

        slownessField = new TextField("" + parkour.getPlayer().potionEffects.get(Potion.moveSlowdown).getAmplifier());
        slownessField.setPromptText("Slowness");

        jumpField = new TextField("" + parkour.getPlayer().potionEffects.get(Potion.jump).getAmplifier());
        jumpField.setPromptText("Jumpboost");

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
        gridPane.add(new Label("Speed:"), 2, 0);
        gridPane.add(speedField, 2, 1);

        gridPane.add(new Label("Position:"), 0, 2);
        gridPane.add(xPosField, 0, 3);
        gridPane.add(new Label("Slowness:"), 2, 2);
        gridPane.add(slownessField, 2, 3);

        gridPane.add(yPosField, 0, 4);
        gridPane.add(zPosField, 0, 5);
        gridPane.add(new Label("Jumpboost:"), 2, 4);
        gridPane.add(jumpField, 2, 5);

        gridPane.add(new Label("Velocity:"), 1, 2);
        gridPane.add(xVelField, 1, 3);
        gridPane.add(yVelField, 1, 4);
        gridPane.add(zVelField, 1, 5);

        // Create the button to apply values
        Button getButton = new Button("Get Pos. & Vel.");
        getButton.setOnAction(event -> updatePlayerSettings());

        Button copyButton = new Button("Copy to Clipboard");
        copyButton.setOnAction(event -> {
            double x = NumberHelper.parseDoubleOrZero(xPosField.getText());
            double y = NumberHelper.parseDoubleOrZero(yPosField.getText());
            double z = NumberHelper.parseDoubleOrZero(zPosField.getText());
            float yaw = NumberHelper.parseFloatOrZero(facingYaw.getText());
            float pitch = NumberHelper.parseFloatOrZero(facingPitch.getText());

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
        Vec3 startPos = parkour.getPlayer().getStartPos();
        Vec3 startVel = parkour.getPlayer().getStartVel();

        this.xPosField.setText(NumberHelper.replaceNegZero(startPos.x*(-1))+ "");
        this.yPosField.setText(startPos.y + "");
        this.zPosField.setText(startPos.z + "");
        this.facingYaw.setText(NumberHelper.replaceNegZero(parkour.getPlayer().getStartYAW()) + "");

        this.xVelField.setText(NumberHelper.replaceNegZero(startVel.x*(-1)) + "");
        this.yVelField.setText(startVel.y + "");
        this.zVelField.setText(startVel.z + "");

        parkour.getPlayer().setStartVel(startVel.copy());
        syncPathAndScreen();
    }

    private void registerTextFieldChanges() {
        Player player = parkour.getPlayer();
        this.xPosField.setOnKeyTyped(keyEvent -> {
            Vec3 oldPos = player.getStartPos().copy();
            double value = NumberHelper.getDoubleOrOld(oldPos.x, xPosField.getText())*(-1);
            player.setStartPos(new Vec3(value, oldPos.y, oldPos.z));
            syncPathAndScreen();
        });
        this.yPosField.setOnKeyTyped(keyEvent -> {
            Vec3 oldPos = player.getStartPos().copy();
            double value = NumberHelper.getDoubleOrOld(oldPos.y, yPosField.getText());
            player.setStartPos(new Vec3(oldPos.x, value, oldPos.z));
            syncPathAndScreen();
        });
        this.zPosField.setOnKeyTyped(keyEvent -> {
            Vec3 oldPos = player.getStartPos().copy();
            double value = NumberHelper.getDoubleOrOld(oldPos.z, zPosField.getText());
            player.setStartPos(new Vec3(oldPos.x, oldPos.y, value));
            syncPathAndScreen();
        });
        this.facingYaw.setOnKeyTyped(keyEvent -> {
            float oldFacing = player.getStartYAW();
            float newFacing = NumberHelper.getFloatOrOld(oldFacing, facingYaw.getText());
            player.setStartYAW(newFacing);
            syncPathAndScreen();
        });
        this.xVelField.setOnKeyTyped(keyEvent -> {
            Vec3 oldVel = player.getStartVel().copy();
            double value = NumberHelper.getDoubleOrOld(oldVel.x, xVelField.getText())*(-1);
            player.setStartVel(new Vec3(value, oldVel.y, oldVel.z));
            syncPathAndScreen();
        });
        this.yVelField.setOnKeyTyped(keyEvent -> {
            Vec3 oldVel = player.getStartVel().copy();
            double value = NumberHelper.getDoubleOrOld(oldVel.y, yVelField.getText());
            player.setStartVel(new Vec3(oldVel.x, value, oldVel.z));
            syncPathAndScreen();
        });
        this.zVelField.setOnKeyTyped(keyEvent -> {
            Vec3 oldVel = player.getStartVel().copy();
            double value = NumberHelper.getDoubleOrOld(oldVel.z, zVelField.getText());
            player.setStartVel(new Vec3(oldVel.x, oldVel.y, value));
            syncPathAndScreen();
        });
        this.speedField.setOnKeyTyped(keyEvent -> {
            int value = NumberHelper.getIntOrOld(player.getPotionEffects().get(Potion.moveSpeed).getAmplifier(), speedField.getText());
            player.getPotionEffects().get(Potion.moveSpeed).setAmplifier(value);
            syncPathAndScreen();
        });
        this.slownessField.setOnKeyTyped(keyEvent -> {
            int value = NumberHelper.getIntOrOld(player.getPotionEffects().get(Potion.moveSlowdown).getAmplifier(), slownessField.getText());
            player.getPotionEffects().get(Potion.moveSlowdown).setAmplifier(value);
            syncPathAndScreen();
        });
        this.jumpField.setOnKeyTyped(keyEvent -> {
            int value = NumberHelper.getIntOrOld(player.getPotionEffects().get(Potion.jump).getAmplifier(), jumpField.getText());
            player.getPotionEffects().get(Potion.jump).setAmplifier(value);
            syncPathAndScreen();
        });

        this.speedField.setDisable(true);
        this.slownessField.setDisable(true);
        this.jumpField.setDisable(true);
    }

    private void syncPathAndScreen() {
        positionVisualizer.update(null, null);
        coordinateScreen.update(null, null);
    }

}
