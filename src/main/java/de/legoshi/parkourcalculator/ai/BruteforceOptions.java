package de.legoshi.parkourcalculator.ai;

import lombok.Getter;

@Getter
public class BruteforceOptions {

    private int numberOfTrials;
    private int ticksPerTrial;
    private int repetitions;
    private double dimension;
    private boolean stopOnFind;
    private int intervallOfLastShown;
    private int recTicks;
    private int bruteforceInstances;
    private int syncInterval;

    public void apply(int numberOfTrials, int ticksPerTrial, int repetitions, double dimension, boolean stopOnFind,
                      int intervallOfLastShown, int recTicks, int bruteforceInstances, int syncInterval) {
        this.numberOfTrials = numberOfTrials;
        this.ticksPerTrial = ticksPerTrial;
        this.repetitions = repetitions;
        this.dimension = dimension;
        this.stopOnFind = stopOnFind;
        this.intervallOfLastShown = intervallOfLastShown;
        this.recTicks = recTicks;
        this.bruteforceInstances = bruteforceInstances;
        this.syncInterval = syncInterval;
    }

}
