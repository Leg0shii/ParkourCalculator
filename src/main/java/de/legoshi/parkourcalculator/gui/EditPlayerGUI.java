package de.legoshi.parkourcalculator.gui;

import de.legoshi.parkourcalculator.parkour.PositionVisualizer;
import de.legoshi.parkourcalculator.parkour.environment.Environment;
import de.legoshi.parkourcalculator.parkour.environment.blocks.ABlock;
import de.legoshi.parkourcalculator.parkour.simulator.MovementEngine;
import de.legoshi.parkourcalculator.parkour.simulator.Player;
import de.legoshi.parkourcalculator.parkour.simulator.PlayerTickInformation;
import de.legoshi.parkourcalculator.util.AxisVecTuple;
import de.legoshi.parkourcalculator.util.Vec3;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class EditPlayerGUI extends GridPane {

    private Stage stage;
    private MovementEngine movementEngine;
    private PositionVisualizer positionVisualizer;
    private InputTickGUI inputTickGUI;

    private Button xButton = new Button("Opt. X Jump");
    private Button yButton = new Button("Opt. Y Jump");
    private Button zButton = new Button("Opt. Z Jump");

    private Separator separator = new Separator();

    private Label xCBLabel = new Label("towards X");
    private CheckBox xCheckBox = new CheckBox();

    private Label zCBLabel = new Label("towards Z");
    private CheckBox zCheckBox = new CheckBox();

    private Label stopOnLandingLabel = new Label("Stop landing");
    private CheckBox stopOnLanding = new CheckBox();

    private Label facingLabel = new Label("F-Deviation");
    private TextField facingField = new TextField("1.0");

    private Label facingRangeLabel = new Label("F-Intervall");
    private TextField facingRangeField = new TextField("10"); // 1/4096
    private Label facingRangeLabelInfo = new Label("(x/4096)"); // 1/4096

    private Label startXLabel = new Label("M-X-Deviation");
    private TextField startXAreaField = new TextField("0.05");
    private Label startZLabel = new Label("M-Z-Deviation");
    private TextField startZAreaField = new TextField("0.05");

    private Label positionXLabel = new Label("M-X-Intervall");
    private TextField positionXPrecision = new TextField("0.003");
    private Label positionZLabel = new Label("M-Z-Intervall");
    private TextField positionZPrecision = new TextField("0.003");

    private Label blockLabel = new Label("Landing block");
    private TextField blockTextField = new TextField("1,1,1");

    private Button bruteForceButton = new Button("Bruteforce");


    public EditPlayerGUI(MovementEngine movementEngine, PositionVisualizer positionVisualizer, InputTickGUI inputTickGUI) {

        this.xButton.setDisable(true);
        this.yButton.setDisable(true);
        this.zButton.setDisable(true);

        this.separator.setPadding(new Insets(20, 20, 20, 20));
        this.add(separator, 0, 7);

        this.add(xCBLabel, 0, 8);
        this.add(xCheckBox, 1, 8);
        this.xCheckBox.setDisable(true);

        this.add(zCBLabel, 0, 9);
        this.add(zCheckBox, 1, 9);
        this.zCheckBox.setDisable(true);

        this.add(stopOnLandingLabel, 0, 10);
        this.add(stopOnLanding, 1, 10);
        this.stopOnLanding.setDisable(true);

        this.add(facingLabel, 0, 11);
        this.add(facingField, 1, 11);

        this.add(facingRangeLabel, 0, 12);
        this.add(facingRangeField, 1, 12);
        this.add(facingRangeLabelInfo, 2, 12);

        this.add(startXLabel, 0, 13);
        this.add(startXAreaField, 1, 13);
        this.add(startZLabel, 2, 13);
        this.add(startZAreaField, 3, 13);

        this.add(positionXLabel, 0, 14);
        this.add(positionXPrecision, 1, 14);
        this.add(positionZLabel, 2, 14);
        this.add(positionZPrecision, 3, 14);

        this.add(blockLabel, 0, 15);
        this.add(blockTextField, 1, 15);

        this.add(bruteForceButton, 0, 16);

        registerBruteForceButton();
    }

    private void registerBruteForceButton() {
        this.bruteForceButton.setOnMouseClicked(mouseEvent -> {
            // get landing block
            String []pos = blockTextField.getText().split(",");
            int xLoc = Integer.parseInt(pos[0]);
            int yLoc = Integer.parseInt(pos[1]);
            int zLoc = Integer.parseInt(pos[2]);
            ABlock aBlock = null;
            for (ABlock block : Environment.aBlocks) {
                for (AxisVecTuple axisVecTuple : block.getAxisVecTuples()) {
                    if (xLoc <= axisVecTuple.getBb().maxX && xLoc - 1 >= axisVecTuple.getBb().minX &&
                        yLoc <= axisVecTuple.getBb().maxY && yLoc - 1 >= axisVecTuple.getBb().minY &&
                        zLoc <= axisVecTuple.getBb().maxZ && zLoc - 1 >= axisVecTuple.getBb().minZ) {
                        aBlock = block;
                    }
                }
            }

            if (aBlock == null) return;

            // save current position
            Player player = movementEngine.player;
            double areaStartX = player.getStartPos().x;
            double areaStartZ = player.getStartPos().z;
            double areaXIterator = Double.parseDouble(positionXPrecision.getText());
            areaXIterator = areaXIterator == 0 ? 1 : areaXIterator;
            double areaXGoal = Double.parseDouble(startXAreaField.getText());
            double areaZIterator = Double.parseDouble(positionZPrecision.getText());
            areaZIterator = areaZIterator == 0 ? 1 : areaZIterator;
            double areaZGoal = Double.parseDouble(startZAreaField.getText());

            TextField textField = (TextField) inputTickGUI.getHBoxes().get(0).getChildren().get(7);
            double value = Double.parseDouble(textField.getText());

            double facingStart = value;
            double facingIterator = Double.parseDouble(facingRangeField.getText()) / 4096;
            facingIterator = facingIterator == 0 ? 1 : facingIterator;
            double facingGoal = Double.parseDouble(facingField.getText());

            textField.setText(facingStart - facingGoal + "");

            // iterate through facing
            long timeStart = System.currentTimeMillis();
            double facingResult = value;
            double xResult = areaStartX;
            double zResult = areaStartZ;
            boolean found = false;

            boolean fIteration = false;
            boolean xIteration = false;
            boolean zIteration = false;
            outer: for (double facing = facingStart - facingGoal; (facing <= facingStart + facingGoal) || !fIteration; facing += facingIterator) {
                textField.setText(value + facing + "");
                for (double xStart = areaStartX - areaXGoal; (xStart <= areaStartX + areaXGoal) || !xIteration; xStart += areaXIterator) {
                    for (double zStart = areaStartZ - areaZGoal; (zStart <= areaStartZ + areaZGoal) || !zIteration; zStart += areaZIterator) {
                        player.setStartPos(new Vec3(xStart, player.getStartPos().y, zStart));
                        facingResult = facing;
                        xResult = xStart;
                        zResult = zStart;
                        PlayerTickInformation pti = positionVisualizer.calcLastTick();
                        for (AxisVecTuple axisVecTuple : aBlock.getAxisVecTuples()) {
                            if(axisVecTuple.getBb().maxY == pti.getPosition().y) {
                                found = true;
                                break outer;
                            }
                        }
                        zIteration = true;
                    }
                    xIteration = true;
                }
                fIteration = true;
            }

            if (found) {
                System.out.println("FOUND POSITION!!!");
                System.out.println("Facing: " + facingResult + " X: " + xResult + " Z: " + zResult);
                System.out.println((System.currentTimeMillis() - timeStart) + " ms");
            } else {
                System.out.println("Restored position");
                player.setStartPos(new Vec3(areaStartX, player.getStartPos().y, areaStartZ));
                textField.setText(value + "");
            }

            positionVisualizer.generatePlayerPath();
        });
        this.yButton.setDisable(true);
    }

}
