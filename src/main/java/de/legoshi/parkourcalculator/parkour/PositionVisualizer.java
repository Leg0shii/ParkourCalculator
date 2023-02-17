package de.legoshi.parkourcalculator.parkour;

import de.legoshi.parkourcalculator.gui.DebugScreen;
import de.legoshi.parkourcalculator.parkour.simulator.MovementEngine;
import de.legoshi.parkourcalculator.parkour.simulator.PlayerTickInformation;
import de.legoshi.parkourcalculator.parkour.tick.InputTick;
import de.legoshi.parkourcalculator.parkour.tick.InputTickManager;
import de.legoshi.parkourcalculator.util.Vec3;
import de.legoshi.parkourcalculator.util.fxyz.AdvancedCamera;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class PositionVisualizer implements Observer {

    private final MovementEngine movementEngine;
    private final InputTickManager inputTickManager;
    private final Group group;
    private final Box box;

    public ArrayList<Sphere> spheres = new ArrayList<>();
    public ArrayList<Cylinder> lines = new ArrayList<>();

    private double lastX;
    private double lastY;
    private double lastZ;

    public PositionVisualizer(Group group, MovementEngine movementEngine, InputTickManager inputTickManager) {
        this.inputTickManager = inputTickManager;
        this.movementEngine = movementEngine;
        this.group = group;

        this.box = new Box(400, 1, 400);
        box.setTranslateY(-0.5);
        box.setOpacity(0);
        group.getChildren().add(box);
    }

    public void generatePlayerPath() {
        ArrayList<PlayerTickInformation> playerTI = getUpdatedPlayerPos();
        ArrayList<Vec3> playerPos = new ArrayList<>();
        playerTI.forEach(pti -> playerPos.add(pti.getPosition()));

        group.getChildren().clear();
        group.getChildren().add(box);

        spheres = new ArrayList<>();
        lines = new ArrayList<>();

        int posCounter = 0;
        for (Vec3 pos : playerPos) {
            Sphere sphere = new Sphere(0.03);
            sphere.setTranslateX(pos.x);
            sphere.setTranslateY(pos.y*-1);
            sphere.setTranslateZ(pos.z);
            spheres.add(sphere);
            int finalPosCounter = posCounter;
            sphere.setOnMouseClicked((event) -> onMouseClick(event, finalPosCounter));
            sphere.setOnMouseDragged((event) -> onMouseClick(event, finalPosCounter));
            group.getChildren().add(sphere);
            posCounter++;
        }

        for (int i = 0; i < playerPos.size() - 1; i++) {
            Point3D startPoint = new Point3D(playerPos.get(i).x, playerPos.get(i).y*-1, playerPos.get(i).z);
            Point3D endPoint = new Point3D(playerPos.get(i+1).x, playerPos.get(i+1).y*-1, playerPos.get(i+1).z);
            Cylinder cylinder = createCylinder(startPoint, endPoint);
            lines.add(cylinder);
            group.getChildren().add(cylinder);
        }
        group.setOnMouseDragged(this::onMouseDrag);
        group.setOnMouseReleased(this::onMouseDragReleased);
    }

    public PlayerTickInformation calcLastTick() {
        ArrayList<InputTick> playerInputs = inputTickManager.getInputTicks();
        return movementEngine.getLastTick(playerInputs);
    }

    public ArrayList<PlayerTickInformation> getUpdatedPlayerPos() {
        ArrayList<InputTick> playerInputs = inputTickManager.getInputTicks();
        return movementEngine.updatePath(playerInputs);
    }

    private void onMouseDragReleased(MouseEvent event) {
        event.consume();
        for (Node node : group.getChildren()) {
            node.setMouseTransparent(false);
        }
        box.setMouseTransparent(true);
    }

    // move around the player path
    private void onMouseDrag(MouseEvent event) {
        event.consume();
        box.setMouseTransparent(false);

        for (Node node : group.getChildren()) {
            if (node instanceof Sphere || node instanceof Cylinder) {
                node.setMouseTransparent(true);
            }
        }

        Point3D coords = event.getPickResult().getIntersectedPoint();
        Vec3 pOffset = movementEngine.player.getStartPos().copy();

        if (coords.getZ() == 0) return;
        if (coords.getY() != -0.5) return;

        Node node = event.getPickResult().getIntersectedNode();
        if (!node.equals(this.box)) {
            coords = coords.add(node.getTranslateX(), 0, node.getTranslateZ());
        }

        movementEngine.player.setStartPos(new Vec3(coords.getX(), pOffset.y, coords.getZ()));
        generatePlayerPath();
    }

    private void onMouseClick(MouseEvent event, int tickPos) {
        if (!(event.getTarget() instanceof Sphere sphere)) return;
        PhongMaterial white = new PhongMaterial();
        white.setDiffuseColor(Color.WHITE);
        for (Sphere s : spheres) s.setMaterial(white);
        sphere.setMaterial(new PhongMaterial(Color.RED));
        for (Observer observer : inputTickManager.getObservers()) {
            if (observer instanceof DebugScreen) {
                ((DebugScreen) observer).updateTickClick(tickPos);
            }
        }
    }

    private Cylinder createCylinder(Point3D startP, Point3D endP) {
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = endP.subtract(startP);
        double height = diff.magnitude();

        Point3D mid = endP.midpoint(startP);
        Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());

        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

        Cylinder line = new Cylinder(0.01, height);
        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);
        return line;
    }

    @Override
    public void update(Observable o, Object arg) {
        generatePlayerPath();
    }

}
