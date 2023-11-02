package de.legoshi.parkourcalculator.ai;

import de.legoshi.parkourcalculator.simulation.tick.InputTick;

import java.util.Random;

public class InputGenerator {
    
    private Random random;
    
    private double wProb = 1.0;
    private double aProb = 0.1;
    private double sProb = 0.0;
    private double dProb = 0.1;
    private double jumpProb = 0.1;
    private double sprintProb = 1.0;
    private double sneakProb = 0.0;
    private double fChangeProb = 0.02;
    
    public InputGenerator() {
        this.random = new Random();
    }
    
    public InputGenerator(long seed) {
        this.random = new Random(seed);
    }
    
    public void apply(double wProb, double aProb, double sProb, double dProb, double jumpProb, double sprintProb,
                      double sneakProb, double fChangeProb) {
        this.wProb = wProb;
        this.aProb = aProb;
        this.sProb = sProb;
        this.dProb = dProb;
        this.jumpProb = jumpProb;
        this.sprintProb = sprintProb;
        this.sneakProb = sneakProb;
        this.fChangeProb = fChangeProb;
    }
    
    public InputTick getNextTick() {
        float facing = generateFacing();
        return new InputTick(
            random.nextDouble() < wProb,
            random.nextDouble() < aProb,
            random.nextDouble() < sProb,
            random.nextDouble() < dProb,
            random.nextDouble() < jumpProb,
            random.nextDouble() < sprintProb,
            random.nextDouble() < sneakProb,
            facing
        );
    }
    
    private float generateFacing() {
        float facing = 0;
        if (random.nextDouble() < fChangeProb) {
            facing = new Random().nextFloat(361) - 180;
        }
        return facing;
    }
    
}
