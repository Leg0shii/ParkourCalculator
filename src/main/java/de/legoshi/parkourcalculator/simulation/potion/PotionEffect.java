package de.legoshi.parkourcalculator.simulation.potion;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PotionEffect {

    private Potion potion;
    private double speedChange;
    private int duration;
    private int amplifier;

    public PotionEffect(Potion potion, double speedChange) {
        this(potion, 0, speedChange);
    }

    public PotionEffect(Potion potion, int amplifier, double speedChange) {
        this.potion = potion;
        this.amplifier = amplifier;
        this.speedChange = speedChange;
    }

}
