package de.legoshi.parkourcalculator.gui;

import de.legoshi.parkourcalculator.simulation.tick.InputTick;
import de.legoshi.parkourcalculator.simulation.tick.InputTickManager;
import de.legoshi.parkourcalculator.util.NumberHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class InputTickGUI extends ScrollPane {

    public static double PREF_HEIGHT = 500.0;
    public static double PREF_WIDTH = 370.0;

    private AnchorPane anchorPane;
    private VBox vBox;

    @Getter private final ArrayList<HBox> hBoxes = new ArrayList<>();
    @Getter private final InputTickManager inputTicks;

    private final VBox buttonVBox = new VBox();
    private final HBox duplicateButtonHBox = new HBox();

    private final HBox buttonHBox = new HBox();

    private final Button addButton = new Button("+");
    private final Button duplicateButton = new Button("Duplicate");
    private final TextField countTF = new TextField("1");
    private final Button removeButton = new Button("-");

    private CheckBox lastRightClickedCheckBox = null;
    private String lastRightClickedAction = null;

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

        buttonHBox.getChildren().addAll(addButton, countTF, removeButton);
        buttonHBox.setPadding(new Insets(10, 10, 0, 10));
        buttonHBox.setSpacing(10);
        buttonHBox.setAlignment(Pos.CENTER);

        duplicateButtonHBox.getChildren().add(duplicateButton);
        duplicateButtonHBox.setAlignment(Pos.CENTER);

        buttonVBox.getChildren().addAll(buttonHBox, duplicateButtonHBox);
        buttonVBox.setSpacing(10);
        addButtons();

        setContent(vBox);
    }

    public void importTicks(List<InputTick> inputTicks) {
        clearAllTicks();
        addTextLabels();
        for (InputTick inputTick : inputTicks) {
            duplicateRow(inputTick);
        }
        addButtons();
    }

    private void clearAllTicks() {
        this.getHBoxes().clear();
        this.vBox.getChildren().clear();
        this.inputTicks.getInputTicks().clear();
    }

    public void resetTicks() {
        clearAllTicks();
        addTextLabels();
        duplicateRow(new InputTick());
        addButtons();
        inputTicks.notifyObservers();
    }

    private void addButtons() {
        countTF.setMaxWidth(40);
        duplicateButton.setMinWidth(138);

        addButton.setOnAction(actionEvent -> {
            Double iteration = NumberHelper.parseDouble(countTF.getText());
            int i = 0;
            while (iteration != null && i < iteration.intValue()) {
                duplicateRow(new InputTick());
                i++;
            }
            runButtonClick();
        });

        removeButton.setOnAction(actionEvent -> {
            Double iteration = NumberHelper.parseDouble(countTF.getText());
            int i = 0;
            while (iteration != null && i < iteration.intValue()) {
                removeLastRow();
                i++;
            }
            runButtonClick();
        });

        duplicateButton.setOnAction(actionEvent -> {
            Double iteration = NumberHelper.parseDouble(countTF.getText());
            int i = 0;
            while (iteration != null && i < iteration.intValue()) {
                if (!inputTicks.getInputTicks().isEmpty()) {
                    InputTick inputTick = inputTicks.getInputTicks().get(inputTicks.getInputTicks().size()-1).copy();
                    duplicateRow(inputTick);
                }
                i++;
            }
            runButtonClick();
        });

        vBox.getChildren().add(buttonVBox);
    }

    private void runButtonClick() {
        vBox.getChildren().remove(buttonVBox);
        vBox.getChildren().add(buttonVBox);
        inputTicks.notifyObservers();
    }

    private void addTextLabels() {
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10, 0, 0, 7));
        hBox.setSpacing(18);

        Label tagLabel = addLabel("#", 0);
        Label wLabel = addLabel("W", 1);
        Label aLabel = addLabel("A", 8);
        Label sLabel = addLabel("S", 10);
        Label dLabel = addLabel("D", 10);
        Label jLabel = addLabel("J", 11);
        Label pLabel = addLabel("P", 10);
        Label nLabel = addLabel("N", 9);
        Label facingLabel = addLabel("F", 5);

        hBox.getChildren().addAll(tagLabel, wLabel, aLabel, sLabel, dLabel, jLabel, pLabel, nLabel, facingLabel);
        vBox.getChildren().add(hBox);
    }

    private Label addLabel(String name, int offset) {
        Label label = new Label(name);
        label.setPadding(new Insets(0, 0, 0, offset));
        return label;
    }

    public void duplicateRow(InputTick inputTick) {
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(5, 5, 5, 5));
        hBox.setSpacing(15);

        Label label = new Label((inputTicks.getInputTicks().size()+1) + "." );
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

        hBox.getChildren().addAll(label, w, a, s, d, j, p, n, facing);

        inputTicks.getInputTicks().add(inputTick);
        hBoxes.add(hBox);
        vBox.getChildren().add(hBox);
    }

    private void removeLastRow() {
        if (inputTicks.getInputTicks().isEmpty()) return;
        inputTicks.getInputTicks().remove(inputTicks.getInputTicks().size()-1);
        HBox hBox = hBoxes.get(hBoxes.size()-1);
        vBox.getChildren().remove(hBox);
        hBoxes.remove(hBox);
    }

    private TextField registerTextField(InputTick inputTick) {
        TextField tF = new TextField("0");
        tF.setOnKeyTyped(actionEvent -> {
            Float f = NumberHelper.parseFloat(tF.getText());
            inputTick.YAW = f == null ? 0 : f;
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

        cB.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                handleRightClick(s, cB, inputTick);
            }
        });
        return cB;
    }

    private void handleRightClick(String s, CheckBox cB, InputTick inputTick) {
        if (lastRightClickedCheckBox == null || !lastRightClickedAction.equals(s)) {
            if (lastRightClickedCheckBox != null) lastRightClickedCheckBox.getStyleClass().remove("darkmode-selection");
            lastRightClickedCheckBox = cB;
            lastRightClickedAction = s;
            lastRightClickedCheckBox.setSelected(!lastRightClickedCheckBox.isSelected());
            lastRightClickedCheckBox.getStyleClass().add("darkmode-selection");
            updateInputTick(s, inputTick, lastRightClickedCheckBox.isSelected());
        } else {
            boolean select = lastRightClickedCheckBox.isSelected();
            int startIndex = hBoxes.indexOf(lastRightClickedCheckBox.getParent());
            int endIndex = hBoxes.indexOf(cB.getParent());

            if (startIndex > endIndex) {
                int temp = startIndex;
                startIndex = endIndex;
                endIndex = temp;
            }

            for (int i = startIndex; i <= endIndex; i++) {
                HBox row = hBoxes.get(i);
                CheckBox checkBox = (CheckBox) row.getChildren().get(columnIndexForAction(s));
                checkBox.setSelected(select);
                updateInputTick(s, inputTicks.getInputTicks().get(i), select);
            }
            lastRightClickedCheckBox.getStyleClass().remove("darkmode-selection");
            lastRightClickedCheckBox = null;
            lastRightClickedAction = null;
        }
    }

    private int columnIndexForAction(String s) {
        return switch (s) {
            case "w" -> 1;
            case "a" -> 2;
            case "s" -> 3;
            case "d" -> 4;
            case "j" -> 5;
            case "p" -> 6;
            case "n" -> 7;
            default -> -1;
        };
    }

    private void updateInputTick(String s, InputTick inputTick, boolean selected) {
        switch (s) {
            case "w" -> inputTick.W = selected;
            case "a" -> inputTick.A = selected;
            case "s" -> inputTick.S = selected;
            case "d" -> inputTick.D = selected;
            case "j" -> inputTick.JUMP = selected;
            case "p" -> inputTick.SPRINT = selected;
            case "n" -> inputTick.SNEAK = selected;
        }
        inputTicks.notifyObservers();
    }

}
