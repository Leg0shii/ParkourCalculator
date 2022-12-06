package de.legoshi.parkourcalculator.parkour;

import de.legoshi.parkourcalculator.gui.DebugScreen;
import de.legoshi.parkourcalculator.parkour.simulator.Parkour;
import de.legoshi.parkourcalculator.parkour.tick.InputTick;
import de.legoshi.parkourcalculator.parkour.tick.InputTickManager;
import de.legoshi.parkourcalculator.util.Vec3;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class PositionVisualizer implements Observer {

    private final Parkour parkour;
    private final InputTickManager inputTickManager;
    private final Group group;

    public ArrayList<Sphere> spheres = new ArrayList<>();
    public ArrayList<Cylinder> lines = new ArrayList<>();

    private double lastX;
    private double lastY;
    private double lastZ;

    public PositionVisualizer(Group group, Parkour parkour, InputTickManager inputTickManager) {
        this.inputTickManager = inputTickManager;
        this.parkour = parkour;
        this.group = group;
    }

    public void generatePlayerPath() {
        ArrayList<Vec3> playerPos = getUpdatedPlayerPos();
        group.getChildren().clear();

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
        group.setOnMouseClicked(this::onMouseReleaseClick);
        group.setOnMouseDragged(this::onMouseDrag);
    }

    private ArrayList<Vec3> getUpdatedPlayerPos() {
        ArrayList<InputTick> playerInputs = inputTickManager.getInputTicks();
        System.out.println(inputTickManager.getInputTicks().size());
        return parkour.updatePath(playerInputs);
    }

    private void onMouseClick(MouseEvent event, int tickPos) {
        System.out.println("CLICK");
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

    // move around the player path
    private void onMouseDrag(MouseEvent event) {
        if (lastX == 0 && lastZ == 0) {
            this.lastX = event.getSceneX();
            this.lastZ = event.getSceneY();
        }

        Vec3 updatedStartPos = parkour.player.getPosition().copy();
        updatedStartPos.x = updatedStartPos.x + (event.getSceneX() - lastX);
        updatedStartPos.z = updatedStartPos.z - (event.getSceneY() - lastZ);
        parkour.player.setPosition(updatedStartPos);

        this.lastX = event.getSceneX();
        this.lastZ = event.getSceneY();

        generatePlayerPath();
    }

    private void onMouseReleaseClick(MouseEvent event) {
        this.lastX = 0;
        this.lastZ = 0;
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
