package de.legoshi.parkourcalculator.util;

import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Shape3D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CacheClearer {

    private static final Logger logger = LogManager.getLogger(CacheClearer.class.getName());
    private final PositionVisualizer positionVisualizer;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public CacheClearer(PositionVisualizer positionVisualizer) {
        this.positionVisualizer = positionVisualizer;
    }

    public void startClearCacheScheduler() {
        final Runnable clearCache = this::clearTriangleMeshCacheScheduler;
        scheduler.scheduleAtFixedRate(clearCache, 0, 2, TimeUnit.SECONDS);
    }

    private void clearTriangleMeshCacheScheduler() {
        List<Cylinder> lines = positionVisualizer.lines;
        if (!(lines != null && !lines.isEmpty())) return;
        Shape3D line = lines.get(0);

        try {
            Field predefinedMeshManager = Shape3D.class.getDeclaredField("manager");
            predefinedMeshManager.setAccessible(true);

            Object manager = predefinedMeshManager.get(line);
            if (manager == null) return;

            Field cylinderCacheField = manager.getClass().getDeclaredField("cylinderCache");
            cylinderCacheField.setAccessible(true);

            Object cylinderCache = cylinderCacheField.get(manager);
            if (cylinderCache == null) return;

            Method clearMethod = cylinderCache.getClass().getDeclaredMethod("clear");
            clearMethod.setAccessible(true);
            clearMethod.invoke(cylinderCache);
        } catch (Exception e) {
            logger.error("Error while clearing triangle mesh cache: " + e.getMessage(), e);
            e.printStackTrace();
        }
    }

}
