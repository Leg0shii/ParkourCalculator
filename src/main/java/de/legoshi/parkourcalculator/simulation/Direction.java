package de.legoshi.parkourcalculator.simulation;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

public enum Direction {
    DOWN(0, 1, -1, "down", Direction.AxisDirection.NEGATIVE, Direction.Axis.Y),
    UP(1, 0, -1, "up", Direction.AxisDirection.POSITIVE, Direction.Axis.Y),
    NORTH(2, 3, 2, "north", Direction.AxisDirection.NEGATIVE, Direction.Axis.Z),
    SOUTH(3, 2, 0, "south", Direction.AxisDirection.POSITIVE, Direction.Axis.Z),
    WEST(4, 5, 1, "west", Direction.AxisDirection.NEGATIVE, Direction.Axis.X),
    EAST(5, 4, 3, "east", Direction.AxisDirection.POSITIVE, Direction.Axis.X);

    private final int data3d;
    private final int oppositeIndex;
    private final int data2d;
    private final String name;
    private final Direction.Axis axis;
    private final Direction.AxisDirection axisDirection;
    private static final Direction[] VALUES = values();

    private Direction(int var3, int var4, int var5, String var6, Direction.AxisDirection var7, Direction.Axis var8) {
        this.data3d = var3;
        this.data2d = var5;
        this.oppositeIndex = var4;
        this.name = var6;
        this.axis = var8;
        this.axisDirection = var7;
    }

    public static Stream<Direction> stream() {
        return Stream.of(VALUES);
    }

    public int get3DDataValue() {
        return this.data3d;
    }

    public int get2DDataValue() {
        return this.data2d;
    }

    public Direction.AxisDirection getAxisDirection() {
        return this.axisDirection;
    }

    public Direction getClockWise(Direction.Axis var1) {
        Direction var10000;
        switch (var1) {
            case X:
                var10000 = this != WEST && this != EAST ? this.getClockWiseX() : this;
                break;
            case Z:
                var10000 = this != NORTH && this != SOUTH ? this.getClockWiseZ() : this;
                break;
            case Y:
                var10000 = this != UP && this != DOWN ? this.getClockWise() : this;
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public static Direction fromAxisAndDirection(Direction.Axis var0, Direction.AxisDirection var1) {
        Direction var10000;
        switch(var0) {
            case X:
                var10000 = var1 == Direction.AxisDirection.POSITIVE ? EAST : WEST;
                break;
            case Z:
                var10000 = var1 == Direction.AxisDirection.POSITIVE ? SOUTH : NORTH;
                break;
            case Y:
                var10000 = var1 == Direction.AxisDirection.POSITIVE ? UP : DOWN;
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public Direction getCounterClockWise(Direction.Axis var1) {
        Direction var10000;
        switch (var1) {
            case X:
                var10000 = this != WEST && this != EAST ? this.getCounterClockWiseX() : this;
                break;
            case Z:
                var10000 = this != NORTH && this != SOUTH ? this.getCounterClockWiseZ() : this;
                break;
            case Y:
                var10000 = this != UP && this != DOWN ? this.getCounterClockWise() : this;
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public Direction getClockWise() {
        Direction var10000;
        switch (this) {
            case NORTH:
                var10000 = EAST;
                break;
            case SOUTH:
                var10000 = WEST;
                break;
            case WEST:
                var10000 = NORTH;
                break;
            case EAST:
                var10000 = SOUTH;
                break;
            default:
                throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
        }

        return var10000;
    }

    private Direction getClockWiseX() {
        Direction var10000;
        switch (this) {
            case DOWN:
                var10000 = SOUTH;
                break;
            case UP:
                var10000 = NORTH;
                break;
            case NORTH:
                var10000 = DOWN;
                break;
            case SOUTH:
                var10000 = UP;
                break;
            default:
                throw new IllegalStateException("Unable to get X-rotated facing of " + this);
        }

        return var10000;
    }

    private Direction getCounterClockWiseX() {
        Direction var10000;
        switch (this) {
            case DOWN:
                var10000 = NORTH;
                break;
            case UP:
                var10000 = SOUTH;
                break;
            case NORTH:
                var10000 = UP;
                break;
            case SOUTH:
                var10000 = DOWN;
                break;
            default:
                throw new IllegalStateException("Unable to get X-rotated facing of " + this);
        }

        return var10000;
    }

    private Direction getClockWiseZ() {
        Direction var10000;
        switch (this) {
            case DOWN:
                var10000 = WEST;
                break;
            case UP:
                var10000 = EAST;
                break;
            case NORTH:
            case SOUTH:
            default:
                throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
            case WEST:
                var10000 = UP;
                break;
            case EAST:
                var10000 = DOWN;
        }

        return var10000;
    }

    private Direction getCounterClockWiseZ() {
        Direction var10000;
        switch (this) {
            case DOWN:
                var10000 = EAST;
                break;
            case UP:
                var10000 = WEST;
                break;
            case NORTH:
            case SOUTH:
            default:
                throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
            case WEST:
                var10000 = DOWN;
                break;
            case EAST:
                var10000 = UP;
        }

        return var10000;
    }

    public Direction getCounterClockWise() {
        Direction var10000;
        switch (this) {
            case NORTH:
                var10000 = WEST;
                break;
            case SOUTH:
                var10000 = EAST;
                break;
            case WEST:
                var10000 = SOUTH;
                break;
            case EAST:
                var10000 = NORTH;
                break;
            default:
                throw new IllegalStateException("Unable to get CCW facing of " + this);
        }

        return var10000;
    }

    public String getName() {
        return this.name;
    }

    public Direction.Axis getAxis() {
        return this.axis;
    }


    public float toYRot() {
        return (float) ((this.data2d & 3) * 90);
    }

    public String toString() {
        return this.name;
    }

    public String getSerializedName() {
        return this.name;
    }

    public static Direction get(Direction.AxisDirection var0, Direction.Axis var1) {
        Direction[] var2 = VALUES;
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            Direction var5 = var2[var4];
            if (var5.getAxisDirection() == var0 && var5.getAxis() == var1) {
                return var5;
            }
        }

        throw new IllegalArgumentException("No such direction: " + var0 + " " + var1);
    }

    public static enum Axis implements Predicate<Direction> {
        X("x") {
            public int choose(int var1, int var2, int var3) {
                return var1;
            }

            public double choose(double var1, double var3, double var5) {
                return var1;
            }
        },
        Y("y") {
            public int choose(int var1, int var2, int var3) {
                return var2;
            }

            public double choose(double var1, double var3, double var5) {
                return var3;
            }
        },
        Z("z") {
            public int choose(int var1, int var2, int var3) {
                return var3;
            }

            public double choose(double var1, double var3, double var5) {
                return var5;
            }
        };

        public static final Direction.Axis[] VALUES = values();
        private final String name;

        Axis(String var3) {
            this.name = var3;
        }

        public String getName() {
            return this.name;
        }

        public boolean isVertical() {
            return this == Y;
        }

        public boolean isHorizontal() {
            return this == X || this == Z;
        }

        public String toString() {
            return this.name;
        }

        public boolean test(Direction var1) {
            return var1 != null && var1.getAxis() == this;
        }

        public Direction.Plane getPlane() {
            Direction.Plane var10000;
            switch (this) {
                case X:
                case Z:
                    var10000 = Direction.Plane.HORIZONTAL;
                    break;
                case Y:
                    var10000 = Direction.Plane.VERTICAL;
                    break;
                default:
                    throw new IncompatibleClassChangeError();
            }

            return var10000;
        }

        public String getSerializedName() {
            return this.name;
        }

        public abstract int choose(int var1, int var2, int var3);

        public abstract double choose(double var1, double var3, double var5);
    }

    public static enum AxisDirection {
        POSITIVE(1, "Towards positive"),
        NEGATIVE(-1, "Towards negative");

        private final int step;
        private final String name;

        private AxisDirection(int var3, String var4) {
            this.step = var3;
            this.name = var4;
        }

        public int getStep() {
            return this.step;
        }

        public String getName() {
            return this.name;
        }

        public String toString() {
            return this.name;
        }

        public Direction.AxisDirection opposite() {
            return this == POSITIVE ? NEGATIVE : POSITIVE;
        }
    }

    public static enum Plane implements Predicate<Direction> {
        HORIZONTAL(new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}, new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z}),
        VERTICAL(new Direction[]{Direction.UP, Direction.DOWN}, new Direction.Axis[]{Direction.Axis.Y});

        private final Direction[] faces;
        private final Direction.Axis[] axis;

        private Plane(Direction[] var3, Direction.Axis[] var4) {
            this.faces = var3;
            this.axis = var4;
        }

        public boolean test(Direction var1) {
            return var1 != null && var1.getAxis().getPlane() == this;
        }

        public Stream<Direction> stream() {
            return Arrays.stream(this.faces);
        }

    }
}