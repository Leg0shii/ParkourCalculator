package de.legoshi.parkourcalculator.util.fxyz;

import javafx.animation.*;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;

public abstract class CameraController implements Transformable {

    public AdvancedCamera camera;
    private Scene scene;
    private SubScene subScene;
    private double previousX, previousY, speed = 1.0;
    private AnimationTimer timer;
    private Timeline timeline;
    private Transition transition;
    private boolean enable;
    private AnimationPreference animPref;

    public CameraController(boolean enableTransforms, AnimationPreference movementType) {
        enable = enableTransforms;
        animPref = movementType;
        switch (animPref) {
            case TIMELINE:
                timeline = new Timeline();
                timeline.setCycleCount(Animation.INDEFINITE);
                break;
            case TIMER:
                timer = new AnimationTimer() {
                    @Override
                    public void handle(long l) {
                        if (enable) {
                            initialize();
                            enable = false;
                        }
                        update();
                    }
                };
                break;
            case TRANSITION:
                transition = new Transition() {
                    {setCycleDuration(Duration.seconds(1));}
                    @Override
                    protected void interpolate(double frac) {
                        updateTransition(frac);
                    }
                };
                transition.setCycleCount(Animation.INDEFINITE);
                break;
            case ANIMATION:

                break;
        }

    }

    //Abstract Methods
    protected abstract void update(); // called each frame handle movement/ button clicks here

    protected abstract void updateTransition(double now);

    // Following methods should update values for use in update method etc...

    protected abstract void handleKeyEvent(KeyEvent event, boolean handle);

    protected abstract void handlePrimaryMouseDrag(MouseEvent event, Point2D dragDelta, double modifier);

    protected abstract void handleMiddleMouseDrag(MouseEvent event, Point2D dragDelta, double modifier);

    protected abstract void handleSecondaryMouseDrag(MouseEvent event, Point2D dragDelta, double modifier);

    protected abstract void handlePrimaryMouseClick(MouseEvent e);

    protected abstract void handleSecondaryMouseClick(MouseEvent e);

    protected abstract void handleMiddleMouseClick(MouseEvent e);

    protected abstract void handlePrimaryMousePress(MouseEvent e);

    protected abstract void handleSecondaryMousePress(MouseEvent e);

    protected abstract void handleMiddleMousePress(MouseEvent e);

    protected abstract void handleMouseMoved(MouseEvent event, Point2D moveDelta, double modifier);

    protected abstract void handleScrollEvent(ScrollEvent event);

    protected abstract double getSpeedModifier(KeyEvent event);

    //Self contained Methods
    private void handleKeyEvent(KeyEvent t) {
        if (t.getEventType() == KeyEvent.KEY_PRESSED) {
            handleKeyEvent(t, true);
        } else if (t.getEventType() == KeyEvent.KEY_RELEASED) {
            handleKeyEvent(t, true);
        }
        speed = getSpeedModifier(t);
    }

    private void handleMouseEvent(MouseEvent t) {
        if (t.getEventType() == MouseEvent.MOUSE_PRESSED) {
            switch (t.getButton()) {
                case PRIMARY -> handlePrimaryMousePress(t);
                case MIDDLE -> handleMiddleMousePress(t);
                case SECONDARY -> handleSecondaryMousePress(t);
                default -> throw new AssertionError();
            }
            handleMousePress(t);
        } else if (t.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            Point2D d = getMouseDelta(t);
            switch (t.getButton()) {
                case PRIMARY -> handlePrimaryMouseDrag(t, d, speed);
                case MIDDLE -> handleMiddleMouseDrag(t, d, speed);
                case SECONDARY -> handleSecondaryMouseDrag(t, d, speed);
                default -> throw new AssertionError();
            }
        } else if (t.getEventType() == MouseEvent.MOUSE_MOVED) {
            handleMouseMoved(t, getMouseDelta(t), speed);
        } else if (t.getEventType() == MouseEvent.MOUSE_CLICKED) {
            switch (t.getButton()) {
                case PRIMARY -> handlePrimaryMouseClick(t);
                case MIDDLE -> handleMiddleMouseClick(t);
                case SECONDARY -> handleSecondaryMouseClick(t);
                default -> throw new AssertionError();
            }
        }
    }

    private void setEventHandlers(Scene scene) {
        scene.addEventHandler(KeyEvent.ANY, this::handleKeyEvent);
        scene.addEventHandler(MouseEvent.ANY, this::handleMouseEvent);
        scene.addEventHandler(ScrollEvent.ANY, this::handleScrollEvent);
    }

    private void setEventHandlers(SubScene scene) {
        scene.addEventHandler(KeyEvent.ANY, this::handleKeyEvent);
        scene.addEventHandler(MouseEvent.ANY, this::handleMouseEvent);
        scene.addEventHandler(ScrollEvent.ANY, this::handleScrollEvent);
    }

    private void handleMousePress(MouseEvent event) {
        previousX = event.getSceneX();
        previousY = event.getSceneY();
        event.consume();
    }

    private Point2D getMouseDelta(MouseEvent event) {
        Point2D res = new Point2D(event.getSceneX() - previousX, event.getSceneY() - previousY);
        previousX = event.getSceneX();
        previousY = event.getSceneY();

        return res;
    }

    public AdvancedCamera getCamera() {
        return camera;
    }

    public void setCamera(AdvancedCamera camera) {
        this.camera = camera;
        switch (animPref) {
            case TIMELINE -> {
                timeline.getKeyFrames().addAll(
                        new KeyFrame(Duration.millis(15), e -> {
                            new Timeline(new KeyFrame(Duration.ONE, ev -> update())).play();
                        })
                );
                timeline.play();
            }
            case TIMER -> timer.start();
            case TRANSITION -> transition.play();
            case ANIMATION -> { }
        }
    }

    public void setScene(Scene scene) {
        this.scene = scene;
        setEventHandlers(scene);
    }

    public void setSubScene(SubScene subScene) {
        this.subScene = subScene;
        setEventHandlers(subScene);
    }

    protected Scene getScene() {
        return scene;
    }

    protected SubScene getSubScene() {
        return subScene;
    }

    @Override
    public RotateOrder getRotateOrder() {
        return RotateOrder.USE_AFFINE;
    }

}
