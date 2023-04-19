package de.legoshi.parkourcalculator.gui;

import de.legoshi.parkourcalculator.parkour.tick.InputTick;
import de.legoshi.parkourcalculator.parkour.tick.InputTickManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import lombok.Getter;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class InputTickGUI extends ScrollPane {

    public static double PREF_HEIGHT = 500.0;
    public static double PREF_WIDTH = 350.0;

    private AnchorPane anchorPane;
    private VBox vBox;

    @Getter private final ArrayList<HBox> hBoxes = new ArrayList<>();
    @Getter private final InputTickManager inputTicks;
    private final Button button = new Button("+");

    public InputTickGUI(InputTickManager inputTickManager) {
        this.inputTicks = inputTickManager;

        setPrefHeight(PREF_HEIGHT);
        setPrefWidth(PREF_WIDTH);
        BorderPane.setAlignment(this, Pos.CENTER);

        vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10.0);
        vBox.setPadding(new Insets(0, 0, 0, 5));

        anchorPane = new AnchorPane(vBox);

        addTextLabels();
        duplicateRow(new InputTick());
        addButton();

        setContent(vBox);
    }

    public void importTicks(List<InputTick> inputTicks) {
        clearAllTicks();
        addTextLabels();
        for (InputTick inputTick : inputTicks) {
            duplicateRow(inputTick);
        }
        addButton();
    }

    private void clearAllTicks() {
        vBox.getChildren().clear();
        this.inputTicks.getInputTicks().clear();
    }

    private void addButton() {
        button.setOnAction((actionEvent -> {
            duplicateRow(new InputTick());
            vBox.getChildren().remove(button);
            vBox.getChildren().add(button);
        }));
        vBox.getChildren().add(button);
    }

    private void addTextLabels() {
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10, 10, 0, 10));
        hBox.setSpacing(29);

        Label wLabel = new Label("W");
        Label aLabel = new Label("A");
        Label sLabel = new Label("S");
        Label dLabel = new Label("D");
        Label jLabel = new Label("J");
        Label pLabel = new Label("P");
        Label nLabel = new Label("N");
        Label facingLabel = new Label("F");

        hBox.getChildren().addAll(wLabel, aLabel, sLabel, dLabel, jLabel, pLabel, nLabel, facingLabel);
        vBox.getChildren().add(hBox);
    }

    private void duplicateRow(InputTick inputTick) {
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(5, 5, 5, 5));
        hBox.setSpacing(16);

        CheckBox w = registerAction("w", new CheckBox(), inputTick);
        CheckBox a = registerAction("a", new CheckBox(), inputTick);
        CheckBox s = registerAction("s", new CheckBox(), inputTick);
        CheckBox d = registerAction("d", new CheckBox(), inputTick);
        CheckBox j = registerAction("j", new CheckBox(), inputTick);
        CheckBox p = registerAction("p", new CheckBox(), inputTick);
        CheckBox n = registerAction("n", new CheckBox(), inputTick);
        TextField facing = registerTextField(inputTick);

        w.setSelected(inputTick.W);
        a.setSelected(inputTick.A);
        s.setSelected(inputTick.S);
        d.setSelected(inputTick.D);
        j.setSelected(inputTick.JUMP);
        p.setSelected(inputTick.SPRINT);
        n.setSelected(inputTick.SNEAK);
        facing.setText(inputTick.YAW + "");

        hBox.getChildren().addAll(w, a, s, d, j, p, n, facing);

        inputTicks.getInputTicks().add(inputTick);
        hBoxes.add(hBox);
        vBox.getChildren().add(hBox);
    }

    private TextField registerTextField(InputTick inputTick) {
        TextField tF = new TextField("0");
        tF.setOnAction(actionEvent -> {
            inputTick.YAW = Float.parseFloat(tF.getText());
            inputTicks.notifyObservers();
        });
        tF.setMaxWidth(60);
        return tF;
    }

    private CheckBox registerAction(String s, CheckBox cB, InputTick inputTick) {
        cB.setOnAction(actionEvent -> {
            switch (s) {
                case "w" -> inputTick.W = cB.isSelected();
                case "a" -> inputTick.A = cB.isSelected();
                case "s" -> inputTick.S = cB.isSelected();
                case "d" -> inputTick.D = cB.isSelected();
                case "j" -> inputTick.JUMP = cB.isSelected();
                case "p" -> inputTick.SPRINT = cB.isSelected();
                case "n" -> inputTick.SNEAK = cB.isSelected();
            }
            inputTicks.notifyObservers();
        });
        return cB;
    }

}
