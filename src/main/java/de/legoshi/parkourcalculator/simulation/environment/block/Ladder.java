package de.legoshi.parkourcalculator.simulation.environment.block;

import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class Ladder extends FacingBlock {

    public Ladder(Vec3 vec3) {
        super(vec3);
    }

}
