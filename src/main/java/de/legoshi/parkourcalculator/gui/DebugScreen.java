package de.legoshi.parkourcalculator.gui;

import de.legoshi.parkourcalculator.parkour.simulator.MovementEngine;
import de.legoshi.parkourcalculator.parkour.simulator.Player;
import de.legoshi.parkourcalculator.parkour.simulator.PlayerTickInformation;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.Observable;
import java.util.Observer;

public class DebugScreen extends VBox implements Observer {

    private final Font boldFont = Font.font("Arial", FontWeight.BOLD, 12);

    private final MovementEngine movementEngine;
    private final Player player;

    private final Text generalLabelInfo = new Text("General Information");
    private final Label facing = new Label();
    private final Label xPos = new Label();
    private final Label yPos = new Label();
    private final Label zPos = new Label();
    private final Label xVel = new Label();
    private final Label yVel = new Label();
    private final Label zVel = new Label();

    private final Text advancedInfo = new Text("Advanced Information");
    private final Label jumpFacing = new Label();
    private final Label xJumpPos = new Label();
    private final Label yJumpPos = new Label();
    private final Label zJumpPos = new Label();
    private final Label landFacing = new Label();
    private final Label xLandPos = new Label();
    private final Label yLandPos = new Label();
    private final Label zLandPos = new Label();

    private final Text tickLabelInfo = new Text("nth Tick Information");
    private final Label tickFacing = new Label();
    private final Label xTickPos = new Label();
    private final Label yTickPos = new Label();
    private final Label zTickPos = new Label();

    public DebugScreen(MovementEngine movementEngine) {
        this.movementEngine = movementEngine;
        this.player = movementEngine.player;
        this.setPadding(new Insets(10, 10, 10, 10));
        this.setMinWidth(200);
        applyBackgroundColor();
        updateLabels();
        updateTickClick(-1); // init
        addLabels();
        indentLabels();
    }

    private void applyBackgroundColor() {
        this.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, new Insets(0, 0, 0, 0))));
    }

    private void addLabels() {
        this.getChildren().addAll(generalLabelInfo, facing, getSpacer(), xPos, yPos, zPos, getSpacer(), xVel, yVel, zVel);
        this.getChildren().addAll(getSpacer(), advancedInfo, jumpFacing, xJumpPos, yJumpPos, zJumpPos, getSpacer(), landFacing, xLandPos, yLandPos, zLandPos);
        this.getChildren().addAll(getSpacer(), tickLabelInfo, tickFacing, xTickPos, yTickPos, zTickPos);
    }

    private void updateLabels() {
        this.generalLabelInfo.setFont(boldFont);
        this.advancedInfo.setFont(boldFont);
        this.tickLabelInfo.setFont(boldFont);
        this.facing.setText("F: " + setDecimals(this.player.getYAW()));
        this.xPos.setText("X-Pos: " + setDecimals(this.player.getPosition().x));
        this.yPos.setText("Y-Pos: " + setDecimals(this.player.getPosition().y));
        this.zPos.setText("Z-Pos: " + setDecimals(this.player.getPosition().z));
        this.xVel.setText("X-Vel: " + setDecimals(this.player.getVelocity().x));
        this.yVel.setText("Y-Vel: " + setDecimals(this.player.getVelocity().y));
        this.zVel.setText("Z-Vel: " + setDecimals(this.player.getVelocity().z));

        PlayerTickInformation ptiJ = getJumpTick();
        if (ptiJ != null) {
            this.jumpFacing.setText("F-Jump: " + setDecimals(ptiJ.getFacing()));
            this.xJumpPos.setText("X-Jump: " + setDecimals(ptiJ.getPosition().x));
            this.yJumpPos.setText("Y-Jump: " + setDecimals(ptiJ.getPosition().y));
            this.zJumpPos.setText("Z-Jump: " + setDecimals(ptiJ.getPosition().z));
        } else {
            this.jumpFacing.setText("F-Jump: -");
            this.xJumpPos.setText("X-Jump: -");
            this.yJumpPos.setText("Y-Jump: -");
            this.zJumpPos.setText("Z-Jump: -");
        }

        PlayerTickInformation ptiL = getLandTick();
        if(ptiL != null) {
            if (!this.getChildren().contains(landFacing))
                this.getChildren().addAll(landFacing, xLandPos, yLandPos, zLandPos);
            this.landFacing.setText("F-Land: " + setDecimals(ptiL.getFacing()));
            this.xLandPos.setText("X-Land: " + setDecimals(ptiL.getPosition().x));
            this.yLandPos.setText("Y-Land: " + setDecimals(ptiL.getPosition().y));
            this.zLandPos.setText("Z-Land: " + setDecimals(ptiL.getPosition().z));
        } else {
            this.landFacing.setText("F-Land: -");
            this.xLandPos.setText("X-Land: -");
            this.yLandPos.setText("Y-Land: -");
            this.zLandPos.setText("Z-Land: -");
        }
    }

    public void updateTickClick(int tickPos) {
        if (tickPos == -1) {
            this.tickFacing.setText("F-Tick: -");
            this.xTickPos.setText("X-Tick: -");
            this.yTickPos.setText("Y-Tick: -");
            this.zTickPos.setText("Z-Tick: -");
            return;
        }
        PlayerTickInformation ptiC = movementEngine.playerTickInformations.get(tickPos);
        this.tickLabelInfo.setText(tickPos + ". Tick Information");
        this.tickFacing.setText("F-Tick: " + setDecimals(ptiC.getFacing()));
        this.xTickPos.setText("X-Tick: " + setDecimals(ptiC.getPosition().x));
        this.yTickPos.setText("Y-Tick: " + setDecimals(ptiC.getPosition().y));
        this.zTickPos.setText("Z-Tick: " + setDecimals(ptiC.getPosition().z));
    }

    private PlayerTickInformation getLandTick() {
        PlayerTickInformation playerTickInformation = null;
        PlayerTickInformation prevTick = null;
        for (PlayerTickInformation pti : movementEngine.getPlayerTickInformations()) {
            if (pti.isGround() && prevTick != null && !prevTick.isGround()) playerTickInformation = prevTick;
            prevTick = pti;
        }
        return playerTickInformation;
    }

    private PlayerTickInformation getJumpTick() {
        PlayerTickInformation playerTickInformation = null;
        for (PlayerTickInformation pti : movementEngine.getPlayerTickInformations()) {
            if (pti.isJump()) playerTickInformation = pti;
        }
        return playerTickInformation;
    }

    private void indentLabels() {
        Insets insets = new Insets(0, 0, 0, 5);
        for (Node node : getChildren()) {
            if (node instanceof Label label) {
                label.setPadding(insets);
            }
        }
    }

    private String setDecimals(double value) {
        return String.format("%.12f", value);
    }

    private Label getSpacer() {
        return new Label(" ");
    }

    @Override
    public void update(Observable o, Object arg) {
        updateLabels();
    }

}
