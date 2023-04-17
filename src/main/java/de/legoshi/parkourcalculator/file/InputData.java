package de.legoshi.parkourcalculator.file;

import de.legoshi.parkourcalculator.parkour.tick.InputTick;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InputData {

    private InputTick inputTick;
    private Vec3 position;
    private Vec3 velocity;

}
