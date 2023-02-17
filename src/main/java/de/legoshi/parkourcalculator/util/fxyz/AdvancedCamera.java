package de.legoshi.parkourcalculator.util.fxyz;

import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;

public class AdvancedCamera extends PerspectiveCamera {

    // Wrapper for "World" movement and lighting
    private final Group wrapper = new Group();
    private final PointLight headLight = new PointLight();
    private final AmbientLight ambientLight = new AmbientLight();

    private CameraController controller;

    public AdvancedCamera() {
        super(true);
        setNearClip(0.1);
        setFarClip(10000);
        setFieldOfView(42);
        //setVerticalFieldOfView(true);

        ambientLight.setLightOn(false);
        wrapper.getChildren().addAll(AdvancedCamera.this, headLight, ambientLight);
    }

    public CameraController getController() {
        return controller;
    }

    public void setController(CameraController controller) {
        controller.setCamera(this);
        this.controller = controller;
    }

    public Group getWrapper() {
        return wrapper;
    }

    public PointLight getHeadLight() {
        return headLight;
    }

    public AmbientLight getAmbientLight() {
        return ambientLight;
    }

}
