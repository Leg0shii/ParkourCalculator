package de.legoshi.parkourcalculator.simulation.environment;

import java.util.Objects;

public class BlockPosition {
    private final int x;
    private final int y;
    private final int z;

    private Integer cachedHashCode = null;

    public BlockPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockPosition that = (BlockPosition) o;
        return x == that.x && y == that.y && z == that.z;
    }

    @Override
    public int hashCode() {
        if (cachedHashCode == null) {
            cachedHashCode = 31 * (31 * x + y) + z;
        }
        return cachedHashCode;
    }

}