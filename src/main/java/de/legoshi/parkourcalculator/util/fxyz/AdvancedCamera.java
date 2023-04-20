package de.legoshi.parkourcalculator.util.fxyz;

import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class AdvancedCamera extends PerspectiveCamera {

    // Wrapper for "World" movement and lighting
    private final Group wrapper = new Group();
    private final PointLight headLight = new PointLight();
    private final AmbientLight ambientLight = new AmbientLight();
    private final PointLight pointLight1 = new PointLight();
    private final PointLight pointLight2 = new PointLight();

    private CameraController controller;

    public AdvancedCamera() {
        super(true);
        setNearClip(0.1);
        setFarClip(10000);
        setFieldOfView(42);

        // Configure ambient light
        ambientLight.setLightOn(true);
        ambientLight.setColor(Color.rgb(128, 128, 128, 0.3));

        // Configure headlight
        headLight.setLightOn(true);
        headLight.setColor(Color.rgb(255, 255, 255, 0.7));
        headLight.getTransforms().add(new Translate(0, 0, -10));

        // Configure additional point lights
        pointLight1.setLightOn(true);
        pointLight1.setColor(Color.rgb(255, 255, 255, 0.7));
        pointLight1.getTransforms().addAll(
                new Translate(100, 50, 100),
                new Rotate(-20, Rotate.X_AXIS)
        );

        pointLight2.setLightOn(true);
        pointLight2.setColor(Color.rgb(255, 255, 255, 0.7));
        pointLight2.getTransforms().addAll(
                new Translate(-100, 50, 100),
                new Rotate(20, Rotate.X_AXIS)
        );

        // Add lights to the wrapper
        wrapper.getChildren().addAll(AdvancedCamera.this, headLight, ambientLight, pointLight1, pointLight2);
    }

    public Group getWrapper() {
        return wrapper;
    }

    public CameraController getController() {
        return controller;
    }

    public void setController(CameraController controller) {
        controller.setCamera(this);
        this.controller = controller;
    }

    public PointLight getHeadLight() {
        return headLight;
    }

    public AmbientLight getAmbientLight() {
        return ambientLight;
    }

}
