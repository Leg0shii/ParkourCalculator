package de.legoshi.parkourcalculator.simulation.player;

import de.legoshi.parkourcalculator.simulation.tick.InputTick;
import de.legoshi.parkourcalculator.simulation.tick.PlayerTickInformation;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.Vec3;

public abstract class Player {

    public abstract void resetPlayer();
    public abstract void updateTick(InputTick inputTick);
    public abstract void applyInput(InputTick inputTick);
    public abstract PlayerTickInformation getPlayerTickInformation();
    public abstract Vec3 getStartPos();
    public abstract void setStartPos(Vec3 vec3);

    public abstract Vec3 getStartVel();
    public abstract void setStartVel(Vec3 vec3);

    public abstract float getYAW();
    public abstract void setYAW(float f);

    public abstract float getStartYAW();
    public abstract void setStartYAW(float f);

    public abstract AxisAlignedBB getStartBB();

    public abstract Vec3 getVelocity();

    public abstract void setWEB(boolean b);

    public abstract boolean isSNEAK();
}
