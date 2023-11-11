package de.legoshi.parkourcalculator.ai;

import de.legoshi.parkourcalculator.simulation.tick.InputTick;

import java.util.Random;

public class InputGenerator {
    
    private Random random;
    
    private double wProb;
    private double aProb;
    private double sProb;
    private double dProb;
    private double jumpProb;
    private double sprintProb;
    private double sneakProb;
    private double fChangeProb;
    
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
