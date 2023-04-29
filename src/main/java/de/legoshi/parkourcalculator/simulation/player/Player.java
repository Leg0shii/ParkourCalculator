package de.legoshi.parkourcalculator.simulation.player;

import de.legoshi.parkourcalculator.simulation.tick.InputTick;
import de.legoshi.parkourcalculator.simulation.tick.PlayerTickInformation;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.Movement;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.Getter;
import lombok.Setter;

public abstract class Player {

    @Getter @Setter public Vec3 startPos;
    @Getter @Setter public Vec3 position;
    @Getter @Setter public Vec3 startVel;
    @Getter @Setter public Vec3 velocity;
    @Getter @Setter public Vec3 realVel;

    @Getter @Setter public float YAW;
    @Getter @Setter public float startYAW;

    public Player(Vec3 position, Vec3 velocity, float startYAW) {
        this.startPos = position.copy();
        this.position = position.copy();

        this.startVel = velocity.copy();
        this.velocity = velocity.copy();

        this.startYAW = startYAW;
    }

    public abstract void updateTick(InputTick inputTick);
    public abstract void applyInput(InputTick inputTick);
    public abstract void resetPlayer();
    public abstract PlayerTickInformation getPlayerTickInformation();
    public abstract AxisAlignedBB getStartBB();
    public abstract void setWEB(boolean b);
    public abstract boolean isSNEAK();

}
