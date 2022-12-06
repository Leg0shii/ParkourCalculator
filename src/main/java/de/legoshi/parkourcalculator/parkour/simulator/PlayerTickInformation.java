package de.legoshi.parkourcalculator.parkour.simulator;

import de.legoshi.parkourcalculator.util.Vec3;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class PlayerTickInformation {

    private float facing;
    private Vec3 position;
    private Vec3 velocity;

    private boolean collided;
    private boolean ground;
    private boolean jump;

}
