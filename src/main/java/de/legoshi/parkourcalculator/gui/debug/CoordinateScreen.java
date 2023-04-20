package de.legoshi.parkourcalculator.gui.debug;

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

public class CoordinateScreen extends VBox implements Observer {

    private final Font boldFont = Font.font("Arial", FontWeight.BOLD, 12);

    private final MovementEngine movementEngine;
    private final Player player;

    private final Text generalLabelInfo = new Text("Start Coordinates");
    private final Label facing = new Label();
    private final Label xPos = new Label();
    private final Label yPos = new Label();
    private final Label zPos = new Label();
    private final Label xVel = new Label();
    private final Label yVel = new Label();
    private final Label zVel = new Label();

    private final Text tickLabelInfo = new Text("nth Tick Information");
    private final Label tickFacing = new Label();
    private final Label xTickPos = new Label();
    private final Label yTickPos = new Label();
    private final Label zTickPos = new Label();
    private final Label xTickVel = new Label();
    private final Label yTickVel = new Label();
    private final Label zTickVel = new Label();

    public CoordinateScreen(MovementEngine movementEngine) {
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
        this.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, new Insets(0, 0, 0, 0))));
    }

    private void addLabels() {
        VBox posLabel = new VBox(xPos, yPos, zPos, facing);
        VBox velLabel = new VBox(xVel, yVel, zVel);
        HBox posContainer = new HBox(posLabel, velLabel);
        posContainer.setSpacing(25);
        this.getChildren().addAll(generalLabelInfo, posContainer);

        VBox tickLabel = new VBox(xTickPos, yTickPos, zTickPos, tickFacing);
        VBox tickVelLabel = new VBox(xTickVel, yTickVel, zTickVel);
        HBox tickContainer = new HBox(tickLabel, tickVelLabel);
        tickContainer.setSpacing(25);
        this.getChildren().addAll(getSpacer(), tickLabelInfo, tickContainer);
    }

    private void updateLabels() {
        this.generalLabelInfo.setFont(boldFont);
        this.tickLabelInfo.setFont(boldFont);
        this.facing.setText("F: " + setDecimals(-this.player.getYAW())); // flips facing on x-axis
        this.xPos.setText("X-Pos: " + setDecimals(-this.player.getPosition().x)); // flips pos on x-axis
        this.yPos.setText("Y-Pos: " + setDecimals(this.player.getPosition().y));
        this.zPos.setText("Z-Pos: " + setDecimals(this.player.getPosition().z));
        this.xVel.setText("X-Vel: " + setDecimals(-this.player.getVelocity().x)); // flips vel on x-axis
        this.yVel.setText("Y-Vel: " + setDecimals(this.player.getVelocity().y));
        this.zVel.setText("Z-Vel: " + setDecimals(this.player.getVelocity().z));
    }

    public void updateTickClick(int tickPos) {
        if (tickPos == -1) {
            this.tickFacing.setText("F-Tick: -");
            this.xTickPos.setText("X-Tick: -");
            this.yTickPos.setText("Y-Tick: -");
            this.zTickPos.setText("Z-Tick: -");
            this.xTickVel.setText("X-Tick-Vel: -");
            this.yTickVel.setText("Y-Tick-Vel: -");
            this.zTickVel.setText("Z-Tick-Vel: -");
            return;
        }
        PlayerTickInformation ptiC = movementEngine.playerTickInformations.get(tickPos);
        this.tickLabelInfo.setText(tickPos + ". Tick Information");
        this.tickFacing.setText("F-Tick: " + setDecimals(-ptiC.getFacing())); // flips facing on x-axis
        this.xTickPos.setText("X-Tick: " + setDecimals(-ptiC.getPosition().x)); // flips pos on x-axis
        this.yTickPos.setText("Y-Tick: " + setDecimals(ptiC.getPosition().y));
        this.zTickPos.setText("Z-Tick: " + setDecimals(ptiC.getPosition().z));
        this.xTickVel.setText("X-Tick-Vel: " + setDecimals(-ptiC.getVelocity().x)); // flips vel on x-axis
        this.yTickVel.setText("Y-Tick-Vel: " + setDecimals(ptiC.getVelocity().y));
        this.zTickVel.setText("Z-Tick-Vel: " + setDecimals(ptiC.getVelocity().z));
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
