package de.legoshi.parkourcalculator.ai;

import de.legoshi.parkourcalculator.util.Vec3;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockNode {

    private BlockNode parent;
    private Vec3 position;
    private Vec3 lastGroundPos;

    private boolean isJump;
    private boolean isClimb;
    private boolean isSwim;
    private boolean isSwimJump;

    private double gCost;
    private double hCost;
    private double fCost;

    public BlockNode(Vec3 position, double distance) {
        this(null, position, 0, distance, false, false, false);
    }

    public BlockNode(BlockNode parent, Vec3 position, double gCost, double hCost, boolean isJump, boolean isClimb, boolean isSwim) {
        this.position = position.copy();
        this.parent = parent;
        this.gCost = gCost;
        this.hCost = hCost;
        this.fCost = gCost + hCost;
        this.isJump = isJump;
        this.isClimb = isClimb;
        this.isSwim = isSwim;

        updateLastGroundPos();
    }

    public void updateLastGroundPos() {
        if (isClimb || isSwim) {
            isJump = false;
        }

        if (isJump) this.lastGroundPos = parent.lastGroundPos.copy();
        else this.lastGroundPos = position.copy();

        isSwimJump = isJump && (parent.isSwim || parent.isSwimJump);
    }
}
