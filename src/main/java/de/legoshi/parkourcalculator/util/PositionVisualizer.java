package de.legoshi.parkourcalculator.util;

import de.legoshi.parkourcalculator.gui.MinecraftGUI;
import de.legoshi.parkourcalculator.gui.debug.CoordinateScreen;
import de.legoshi.parkourcalculator.gui.debug.menu.ScreenSettings;
import de.legoshi.parkourcalculator.simulation.Parkour;
import de.legoshi.parkourcalculator.simulation.movement.Movement;
import de.legoshi.parkourcalculator.simulation.player.Player;
import de.legoshi.parkourcalculator.simulation.tick.PlayerTickInformation;
import de.legoshi.parkourcalculator.simulation.tick.InputTick;
import de.legoshi.parkourcalculator.simulation.tick.InputTickManager;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class PositionVisualizer extends Observable implements Observer {

    private static final double ALLOWED_ERROR = 1e-15;
    private static final int MAX_ITERATIONS = 15;

    private Parkour parkour;
    private Movement movement;

    private final InputTickManager inputTickManager;
    private final Group group;
    private final CacheClearer cacheClearer;

    private Box box;

    public List<Sphere> spheres = new ArrayList<>();
    public List<Cylinder> lines = new ArrayList<>();

    private final List<Observer> observers = new ArrayList<>();

    private int tickClicked = -1;

    public PositionVisualizer(Group group, Parkour parkour, InputTickManager inputTickManager) {
        this.inputTickManager = inputTickManager;
        this.group = group;
        apply(parkour);
        this.cacheClearer = new CacheClearer(this);
        this.cacheClearer.startClearCacheScheduler();
        this.resetPlayer();
    }

    public void apply(Parkour parkour) {
        this.parkour = parkour;
        this.movement = parkour.getMovement();
        if (box != null) group.getChildren().remove(box);

        this.box = new Box(400, 1, 400);
        this.box.setTranslateY(MinecraftGUI.BLOCK_OFFSET_Y - parkour.getPlayer().getStartPos().y);
        this.box.setOpacity(0);
        this.box.setMouseTransparent(true);
        this.group.getChildren().add(box);

        generatePlayerPath();
        System.out.println("PositionVisualizer applied");
    }

    public void resetPlayer() {
        movement.resetPlayer();
        generatePlayerPath();
    }

    public void generatePlayerPath() {
        List<PlayerTickInformation> playerTI = getUpdatedPlayerPos();
        List<Vec3> playerPos = new ArrayList<>();
        playerTI.forEach(pti -> playerPos.add(pti.getPosition()));

        group.getChildren().clear();
        group.getChildren().add(box);

        spheres = new ArrayList<>();
        lines = new ArrayList<>();

        int posCounter = 0;
        for (Vec3 pos : playerPos) {
            Sphere sphere = new Sphere(0.03);
            if (posCounter == tickClicked) sphere.setMaterial(new PhongMaterial(Color.RED));
            sphere.setTranslateX(pos.x);
            sphere.setTranslateY(pos.y * -1);
            sphere.setTranslateZ(pos.z);
            spheres.add(sphere);
            int finalPosCounter = posCounter;
            sphere.setOnMouseClicked((event) -> onMouseClick(event, finalPosCounter));
            sphere.setOnMouseDragged((event) -> onMouseClick(event, finalPosCounter));
            group.getChildren().add(sphere);
            posCounter++;
        }

        for (int i = 0; i < playerPos.size() - 1; i++) {
            Point3D startPoint = new Point3D(playerPos.get(i).x, playerPos.get(i).y * -1, playerPos.get(i).z);
            Point3D endPoint = new Point3D(playerPos.get(i + 1).x, playerPos.get(i + 1).y * -1, playerPos.get(i + 1).z);
            Cylinder cylinder = createCylinder(startPoint, endPoint);
            lines.add(cylinder);
            group.getChildren().add(cylinder);
        }
        group.setOnMouseDragged(this::onMouseDrag);
        group.setOnMouseReleased(this::onMouseDragReleased);

        notifyObservers();
    }

    public List<PlayerTickInformation> getUpdatedPlayerPos() {
        List<InputTick> playerInputs = inputTickManager.getInputTicks();
        return movement.updatePath(playerInputs);
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

        Player player = parkour.getPlayer();
        Point3D coords = event.getPickResult().getIntersectedPoint();
        Vec3 pOffset = player.getStartPos().copy();

        DecimalFormat df = new DecimalFormat("#.##########");
        double yCoordinate = Double.parseDouble(df.format(coords.getY()).replace(",", "."));
        double zCoordinate = Double.parseDouble(df.format(coords.getZ()).replace(",", "."));
        double decimalNumber = player.getStartPos().y % 1;
        double roundedDecimalNumber = Double.parseDouble(df.format(-decimalNumber / 2).replace(",", "."));

        if (zCoordinate == 0) return;
        if (yCoordinate != roundedDecimalNumber && yCoordinate != -0.5 && yCoordinate != -0.75) return;

        Node node = event.getPickResult().getIntersectedNode();
        if (!node.equals(this.box)) {
            coords = coords.add(node.getTranslateX(), 0, node.getTranslateZ());
        }

        double xOffset = coords.getX() - pOffset.x;
        double zOffset = coords.getZ() - pOffset.z;
        Vec3 updatedVec = handleBoxMovementAndCollision(pOffset, xOffset, zOffset);
        player.setStartPos(updatedVec);

        generatePlayerPath();
    }

    public Vec3 handleBoxMovementAndCollision(Vec3 pOffset, double xOffset, double zOffset) {
        Player player = parkour.getPlayer();
        Vec3 newPos = new Vec3(pOffset.x + xOffset, pOffset.y, pOffset.z + zOffset);
        if (!ScreenSettings.isPathCollision()) return newPos;

        player.setStartPos(newPos);
        if (!movement.getCollidingBoundingBoxes(player.getStartBB()).isEmpty()) {
            double newX = binarySearchAxis(player, movement, pOffset.x, xOffset, pOffset.y, pOffset.z, true);
            double newZ = binarySearchAxis(player, movement, pOffset.z, zOffset, pOffset.y, newX, false);
            newPos = new Vec3(newX, pOffset.y, newZ);
        }
        return newPos;
    }

    private double binarySearchAxis(Player player, Movement movement, double original, double offset, double y, double otherAxis, boolean isX) {
        double low = original, high = original + offset, newVal;
        int currentIteration = 0;
        while (Math.abs(high - low) > ALLOWED_ERROR && currentIteration <= MAX_ITERATIONS) {
            newVal = (low + high) / 2;
            Vec3 testPos = isX ? new Vec3(newVal, y, otherAxis) : new Vec3(otherAxis, y, newVal);
            player.setStartPos(testPos);
            if (movement.getCollidingBoundingBoxes(player.getStartBB()).isEmpty()) {
                low = newVal;
            } else {
                high = newVal;
            }
            currentIteration++;
        }
        return low;
    }

    private void onMouseClick(MouseEvent event, int tickPos) {
        if (event.getClickCount() <= 1) return;
        if (!(event.getTarget() instanceof Sphere sphere)) return;
        PhongMaterial white = new PhongMaterial();
        white.setDiffuseColor(Color.WHITE);
        for (Sphere s : spheres) s.setMaterial(white);
        sphere.setMaterial(new PhongMaterial(Color.RED));
        for (Observer observer : inputTickManager.getObservers()) {
            if (observer instanceof CoordinateScreen) {
                setTickClicked((CoordinateScreen) observer, tickPos);
                observer.update(null, null);
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

    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(null, null);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        box.setTranslateY(MinecraftGUI.BLOCK_OFFSET_Y - parkour.getPlayer().getStartPos().y);
        generatePlayerPath();
    }

    private void setTickClicked(CoordinateScreen coordinateScreen, int tick) {
        coordinateScreen.setClickedTick(tick);
        this.tickClicked = tick;
    }

}
