package de.legoshi.parkourcalculator.gui.debug;

import de.legoshi.parkourcalculator.gui.debug.menu.ScreenSettings;
import de.legoshi.parkourcalculator.parkour.simulator.MovementEngine;
import de.legoshi.parkourcalculator.parkour.simulator.Player;
import de.legoshi.parkourcalculator.parkour.simulator.PlayerTickInformation;
import de.legoshi.parkourcalculator.util.ConfigReader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CoordinateScreen extends VBox implements Observer {

    private final MovementEngine movementEngine;
    private int precision;

    private final Label startLabelInfo = new Label("First Tick Coordinates");
    private List<Label> start = new ArrayList<>();

    private final Label tickLabelInfo = new Label("nth Tick Coordinate");
    private List<Label> tick = new ArrayList<>();

    private final Label lastLabelInfo = new Label("Last Tick Coordinates");
    private List<Label> last = new ArrayList<>();

    @Getter private int tickClicked = -1;

    public CoordinateScreen(MovementEngine movementEngine) {
        this.movementEngine = movementEngine;
        this.setPadding(new Insets(10, 10, 10, 10));
        this.setMinWidth(200);
        this.getStyleClass().add("coordinate-field");
        this.setSpacing(15);

        initLabels();
        update(null, null);
        indentLabels();

        updatePrecision();
    }

    public void updateSpecificTick(Label title, List<Label> labels, String name, int tickPos) {
        if (tickPos == -1 || tickPos >= movementEngine.playerTickInformations.size()) {
            labels.get(0).setText("F-" + name + ": -");
            labels.get(1).setText("X-" + name + ": -");
            labels.get(2).setText("Y-" + name + ": -");
            labels.get(3).setText("Z-" + name + ": -");
            labels.get(4).setText("X-" + name + "-Vel: -");
            labels.get(5).setText("Y-" + name + "-Vel: -");
            labels.get(6).setText("Z-" + name + "-Vel: -");
            return;
        }
        PlayerTickInformation ptiC = movementEngine.playerTickInformations.get(tickPos);
        title.setText(tickPos + ". Tick Information");
        labels.get(0).setText("F-" + name + ": " + setDecimals(-ptiC.getFacing())); // flips facing on x-axis
        labels.get(1).setText("X-" + name + ": " + setDecimals(-ptiC.getPosition().x)); // flips pos on x-axis
        labels.get(2).setText("Y-" + name + ": " + setDecimals(ptiC.getPosition().y));
        labels.get(3).setText("Z-" + name + ": " + setDecimals(ptiC.getPosition().z));
        labels.get(4).setText("X-" + name + "-Vel: " + setDecimals(-ptiC.getVelocity().x)); // flips vel on x-axis
        labels.get(5).setText("Y-" + name + "-Vel: " + setDecimals(ptiC.getVelocity().y));
        labels.get(6).setText("Z-" + name + "-Vel: " + setDecimals(ptiC.getVelocity().z));
    }

    public void setClickedTick(int tick) {
        this.tickClicked = tick;
    }

    @Override
    public void update(Observable o, Object arg) {
        updateSpecificTick(startLabelInfo, start, "Start", 0);
        updateSpecificTick(tickLabelInfo, tick, "Tick", tickClicked);
        updateSpecificTick(lastLabelInfo, last, "Last", movementEngine.getPlayerTickInformations().size()-1);
    }

    public void updatePrecision() {
        this.precision = Math.min(Math.max(ScreenSettings.getCoordinatePrecision(), 1), 16);
        this.update(null, null);
    }

    private void indentLabels() {
        Insets insets = new Insets(0, 0, 0, 5);
        for (Node node : getChildren()) {
            if (node instanceof Label label) {
                label.setPadding(insets);
            }
        }
    }

    private void addTextClass(VBox... vBoxes) {
        for (VBox vBox : vBoxes) {
            for (Node n : vBox.getChildren()) n.getStyleClass().add("coords-text");
        }
    }

    private String setDecimals(double value) {
        String parsedValue = String.format("%," + precision + "f", value);
        if (value == 0) parsedValue = ("" + parsedValue).replace("-", "");
        parsedValue = parsedValue.replace(",", ".");
        return parsedValue;
    }

    private Label getSpacer() {
        return new Label(" ");
    }

    private void initLabels() {
        this.start = Stream.of("", "", "", "", "", "", "").map(Label::new).collect(Collectors.toList());
        this.tick = Stream.of("", "", "", "", "", "", "").map(Label::new).collect(Collectors.toList());
        this.last = Stream.of("", "", "", "", "", "", "").map(Label::new).collect(Collectors.toList());
        initLabel(start, startLabelInfo);
        initLabel(tick, tickLabelInfo);
        initLabel(last, lastLabelInfo);
    }

    private void initLabel(List<Label> labels, Label title) {
        VBox posLabel = new VBox(labels.get(0), labels.get(1), labels.get(2), labels.get(3));
        VBox velLabel = new VBox(labels.get(4), labels.get(5), labels.get(6));
        HBox posContainer = new HBox(posLabel, velLabel);
        posContainer.setSpacing(25);
        posContainer.setPadding(new Insets(5, 5, 5, 5));
        addTextClass(posLabel, velLabel);
        title.getStyleClass().add("coords-title");
        this.getChildren().addAll(new VBox(title, posContainer));
    }

}
