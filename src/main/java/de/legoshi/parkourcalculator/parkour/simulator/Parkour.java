package de.legoshi.parkourcalculator.parkour.simulator;

import de.legoshi.parkourcalculator.parkour.tick.InputTick;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class Parkour {

    public Player player;
    private Vec3 startPosition = new Vec3(0.5, 1, 0.5);
    private Vec3 startVelocity = new Vec3(0, -0.0784000015258789, 0);

    public ArrayList<Vec3> updatePath(ArrayList<InputTick> inputTicks) {
        ArrayList<Vec3> vec3s = new ArrayList<>();
        if (inputTicks.size() == 0) return vec3s;

        Vec3 startPosCopy = startPosition.copy();
        Vec3 startVelCopy = startVelocity.copy();

        vec3s.add(startPosCopy.copy());

        player = new Player(startPosCopy, startVelCopy);
        player.calculateTick(inputTicks.get(0));
        vec3s.add(startPosCopy.copy());

        for (int i = 1; i < inputTicks.size(); i++) {
            player.calculateTick(inputTicks.get(i));
            vec3s.add(startPosCopy.copy());
        }

        return vec3s;
    }
}
