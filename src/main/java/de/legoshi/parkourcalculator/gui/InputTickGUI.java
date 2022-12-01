package de.legoshi.parkourcalculator.gui;

import de.legoshi.parkourcalculator.parkour.tick.InputTick;
import de.legoshi.parkourcalculator.parkour.tick.InputTickManager;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import java.util.ArrayList;

public class InputTickGUI {

    private ArrayList<HBox> hBoxes = new ArrayList<>();
    private InputTickManager inputTicks;
    private Button button;

    public void addObserver(InputTickManager inputTicks, Button button) {
        this.inputTicks = inputTicks;
        this.button = button;
    }

    public void setButtonAction(VBox vBox) {
        button.setOnAction((actionEvent -> {
            vBox.getChildren().remove(button);
            vBox.getChildren().add(duplicateRow());
            vBox.getChildren().add(button);
        }));
    }

    public ArrayList<HBox> getHBoxes() {
        return hBoxes;
    }

    private HBox duplicateRow() {
        InputTick inputTick = new InputTick();

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

        hBox.getChildren().addAll(w, a, s, d, j, p, n, facing);

        inputTicks.getInputTicks().add(inputTick);
        hBoxes.add(hBox);
        return hBox;
    }

    private TextField registerTextField(InputTick inputTick) {
        TextField tF = new TextField();
        tF.setOnAction(actionEvent -> {
            inputTick.YAW = Float.parseFloat(tF.getText());
            inputTicks.update(inputTicks, null);
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
            inputTicks.update(inputTicks, null);
        });
        return cB;
    }

}
