package de.legoshi.parkourcalculator.util.fxyz;

import de.legoshi.parkourcalculator.config.ConfigProperties;
import de.legoshi.parkourcalculator.config.Configurable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.util.Locale;

public class FPSController extends CameraController implements Configurable {

    private static boolean fwd, strafeL, strafeR, back, up, down, shift, mouseLookEnabled;
    private static double speed, maxSpeed, minSpeed;
    private static double mouseSpeed, maxMouseSpeed, minMouseSpeed;

    private static KeyCode forwardKey, backwardsKey, leftKey, rightKey, upKey, downKey, sprintKey;

    public FPSController() {
        super(true, AnimationPreference.TIMER);
    }

    public void applyConfigValues(ConfigProperties configProperties) {
        double speedMulti = configProperties.getMaxSpeedMultiplier();
        speed = configProperties.getCameraSpeed();
        minSpeed = speed;
        maxSpeed = speed * speedMulti;

        double mouseSpeedMulti = configProperties.getMaxMouseMultiplier();
        mouseSpeed = configProperties.getMouseSpeed();
        minMouseSpeed = mouseSpeed;
        maxMouseSpeed = mouseSpeed * mouseSpeedMulti;

        forwardKey = KeyCode.valueOf(configProperties.getForward().toUpperCase(Locale.ROOT));
        backwardsKey = KeyCode.valueOf(configProperties.getBackward().toUpperCase(Locale.ROOT));
        leftKey = KeyCode.valueOf(configProperties.getLeft().toUpperCase(Locale.ROOT));
        rightKey = KeyCode.valueOf(configProperties.getRight().toUpperCase(Locale.ROOT));
        upKey = KeyCode.valueOf(configProperties.getUp().toUpperCase(Locale.ROOT));
        downKey = KeyCode.valueOf(configProperties.getDown().toUpperCase(Locale.ROOT));
        sprintKey = KeyCode.valueOf(configProperties.getSprint().toUpperCase(Locale.ROOT));
    }

    @Override
    public void update() {
        if (fwd && !back) moveForward();
        if (strafeL) strafeLeft();
        if (strafeR) strafeRight();
        if (back && !fwd) moveBack();
        if (up && !down) moveUp();
        if (down && !up) moveDown();
    }

    @Override
    public void handleKeyEvent(KeyEvent event, boolean handle) {
        if (event.getEventType() == KeyEvent.KEY_PRESSED) {
            if (event.getCode().equals(forwardKey)) fwd = true;
            else if (event.getCode().equals(backwardsKey)) back = true;
            else if (event.getCode().equals(leftKey)) strafeL = true;
            else if (event.getCode().equals(rightKey)) strafeR = true;
            else if (event.getCode().equals(upKey)) up = true;
            else if (event.getCode().equals(downKey)) down = true;
            else if (event.getCode().equals(sprintKey)) {
                shift = true;
                speed = maxSpeed;
                mouseSpeed = maxMouseSpeed;
            }
        } else if (event.getEventType() == KeyEvent.KEY_RELEASED) {
            if (event.getCode().equals(forwardKey)) fwd = false;
            else if (event.getCode().equals(backwardsKey)) back = false;
            else if (event.getCode().equals(leftKey)) strafeL = false;
            else if (event.getCode().equals(rightKey)) strafeR = false;
            else if (event.getCode().equals(upKey)) up = false;
            else if (event.getCode().equals(downKey)) down = false;
            else if (event.getCode().equals(sprintKey)) {
                shift = false;
                speed = minSpeed;
                mouseSpeed = minMouseSpeed;
            }
        }
    }

    @Override
    protected void handlePrimaryMouseDrag(MouseEvent event, Point2D dragDelta, double modifier) {
        if (!mouseLookEnabled) {
            t.setX(getPosition().getX());
            t.setY(getPosition().getY());
            t.setZ(getPosition().getZ());
            
            affine.setToIdentity();
            
            rotateY.setAngle(MathUtils.clamp(((rotateY.getAngle() + dragDelta.getX() * (mouseSpeed/3 * 15)) % 360 + 540) % 360 - 180, -360, 360)); // horizontal
            rotateX.setAngle(MathUtils.clamp(((rotateX.getAngle() - dragDelta.getY() * (mouseSpeed/3 * 15)) % 360 + 540) % 360 - 180, -90, 90)); // vertical
            
            affine.prepend(t.createConcatenation(rotateY.createConcatenation(rotateX)));
        }     
    }

    @Override
    protected void handleMiddleMouseDrag(MouseEvent event, Point2D dragDelta, double modifier) {
        // do nothing for now
    }

    @Override
    protected void handleSecondaryMouseDrag(MouseEvent event, Point2D dragDelta, double modifier) {
        // do nothing for now
    }

    @Override
    protected void handleMouseMoved(MouseEvent event, Point2D moveDelta, double speed) {
        if (mouseLookEnabled) {
            t.setX(getPosition().getX());
            t.setY(getPosition().getY());
            t.setZ(getPosition().getZ());

            affine.setToIdentity();

            rotateY.setAngle(MathUtils.clamp(((rotateY.getAngle() + moveDelta.getX() * (speed * 0.05)) % 360 + 540) % 360 - 180, -360, 360)); // horizontal
            rotateX.setAngle(MathUtils.clamp(((rotateX.getAngle() - moveDelta.getY() * (speed * 0.05)) % 360 + 540) % 360 - 180, -90, 90)); // vertical

            affine.prepend(t.createConcatenation(rotateY.createConcatenation(rotateX)));

        }
    }

    @Override
    protected void handleScrollEvent(ScrollEvent event) {
        //do nothing for now, use for Zoom?
    }

    @Override
    protected double getSpeedModifier(KeyEvent event) {
        return speed;
    }

    @Override
    public Node getTransformableNode() {
        if (getCamera() != null) {
            return getCamera();
        } else {
            throw new UnsupportedOperationException("Must have a Camera");
        }
    }

    private void moveForward() {      
        affine.setTx(getPosition().getX() + speed * getForwardMatrixRow().x);
        affine.setTy(getPosition().getY() + speed * getForwardMatrixRow().y);
        affine.setTz(getPosition().getZ() + speed * getForwardMatrixRow().z);
    }

    private void strafeLeft() {
        affine.setTx(getPosition().getX() + speed * -getRightMatrixRow().x);
        affine.setTy(getPosition().getY() + speed * -getRightMatrixRow().y);
        affine.setTz(getPosition().getZ() + speed * -getRightMatrixRow().z);
    }

    private void strafeRight() {
        affine.setTx(getPosition().getX() + speed * getRightMatrixRow().x);
        affine.setTy(getPosition().getY() + speed * getRightMatrixRow().y);
        affine.setTz(getPosition().getZ() + speed * getRightMatrixRow().z);
    }

    private void moveBack() {
        affine.setTx(getPosition().getX() + speed * -getForwardMatrixRow().x);
        affine.setTy(getPosition().getY() + speed * -getForwardMatrixRow().y);
        affine.setTz(getPosition().getZ() + speed * -getForwardMatrixRow().z);
    }

    private void moveUp() {
        affine.setTx(getPosition().getX() + speed * -getUpMatrixRow().x);
        affine.setTy(getPosition().getY() + speed * -getUpMatrixRow().y);
        affine.setTz(getPosition().getZ() + speed * -getUpMatrixRow().z);
    }

    private void moveDown() {
        affine.setTx(getPosition().getX() + speed * getUpMatrixRow().x);
        affine.setTy(getPosition().getY() + speed * getUpMatrixRow().y);
        affine.setTz(getPosition().getZ() + speed * getUpMatrixRow().z);
    }

    public void setMouseLookEnabled(boolean b) {
        mouseLookEnabled = b;
    }

    @Override
    protected void handlePrimaryMouseClick(MouseEvent t) {
        //System.out.println("Primary Button Clicked!");
    }

    @Override
    protected void handleMiddleMouseClick(MouseEvent t) {
        //System.out.println("Middle Button Clicked!");
    }

    @Override
    protected void handleSecondaryMouseClick(MouseEvent t) {
        //System.out.println("Secondary Button Clicked!");
    }

    @Override
    protected void handlePrimaryMousePress(MouseEvent e) {

    }

    @Override
    protected void handleSecondaryMousePress(MouseEvent e) {

    }

    @Override
    protected void handleMiddleMousePress(MouseEvent e) {

    }

    @Override
    protected void updateTransition(double now) {

    }

}