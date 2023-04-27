package de.legoshi.parkourcalculator.simulation.tick;

import de.legoshi.parkourcalculator.util.Vec3;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class PlayerTickInformation {

    private float facing;
    private Vec3 position;
    private Vec3 velocity;
    private Vec3 realVelocity;

    private boolean collided;
    private boolean ground;
    private boolean jump;

}
