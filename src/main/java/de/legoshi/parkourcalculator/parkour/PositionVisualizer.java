package de.legoshi.parkourcalculator.parkour;

import de.legoshi.parkourcalculator.gui.MinecraftScreen;
import de.legoshi.parkourcalculator.parkour.simulator.Parkour;
import de.legoshi.parkourcalculator.parkour.tick.InputTick;
import de.legoshi.parkourcalculator.parkour.tick.InputTickManager;
import de.legoshi.parkourcalculator.util.Vec3;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.util.ArrayList;

public class PositionVisualizer {

    private final Parkour parkour;
    private final InputTickManager inputTickManager;
    private Group group;

    public ArrayList<Sphere> spheres = new ArrayList<>();
    public ArrayList<Cylinder> lines = new ArrayList<>();

    private double lastX;
    private double lastY;
    private double lastZ;

    private static final int XPOSOFFSET = -50;
    private static final int YPOSOFFSET = 50;
    private static final int ZPOSOFFSET = -50;

    public PositionVisualizer(Parkour parkour, InputTickManager inputTickManager) {
        this.inputTickManager = inputTickManager;
        this.parkour = parkour;
        this.group = new Group();
    }

    public Group generatePlayerPath() {
        ArrayList<Vec3> playerPos = getUpdatedPlayerPos();
        group.getChildren().clear();

        spheres = new ArrayList<>();
        lines = new ArrayList<>();

        for (Vec3 pos : playerPos) {
            Sphere sphere = new Sphere(3);
            sphere.setTranslateX(pos.x * 100 + XPOSOFFSET);
            sphere.setTranslateY(pos.y * -100 + YPOSOFFSET);
            sphere.setTranslateZ(pos.z * 100 + ZPOSOFFSET);
            spheres.add(sphere);
            group.getChildren().add(sphere);
        }

        for (int i = 0; i < playerPos.size() - 1; i++) {
            Point3D startPoint = new Point3D(playerPos.get(i).x * 100 + XPOSOFFSET, playerPos.get(i).y * -100 + YPOSOFFSET, playerPos.get(i).z * 100 + ZPOSOFFSET);
            Point3D endPoint = new Point3D(playerPos.get(i + 1).x * 100 + XPOSOFFSET, playerPos.get(i + 1).y * -100 + YPOSOFFSET, playerPos.get(i + 1).z * 100 + ZPOSOFFSET);
            Cylinder cylinder = createCylinder(startPoint, endPoint);
            lines.add(cylinder);
            group.getChildren().add(cylinder);
        }
        group.setOnMouseClicked(this::onMouseReleaseClick);
        group.setOnMouseDragged(this::onMouseDrag);
        return group;
    }

    private ArrayList<Vec3> getUpdatedPlayerPos() {
        ArrayList<InputTick> playerInputs = inputTickManager.getInputTicks();
        return parkour.updatePath(playerInputs);
    }

    private void onMouseDrag(MouseEvent event) {
        if (lastX == 0 && lastZ == 0) {
            this.lastX = event.getSceneX();
            this.lastZ = event.getSceneY();
        }

        Vec3 updatedStartPos = parkour.getStartPosition().copy();
        updatedStartPos.x = updatedStartPos.x + (event.getSceneX() - lastX)/100;
        updatedStartPos.z = updatedStartPos.z - (event.getSceneY() - lastZ)/100;
        parkour.setStartPosition(updatedStartPos);

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

        Cylinder line = new Cylinder(1, height);

        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);

        return line;
    }

}
