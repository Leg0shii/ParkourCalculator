package de.legoshi.parkourcalculator.ai;

import de.legoshi.parkourcalculator.simulation.Parkour;
import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import de.legoshi.parkourcalculator.simulation.environment.block.Air;
import de.legoshi.parkourcalculator.simulation.environment.block.Stair;
import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager;
import de.legoshi.parkourcalculator.simulation.movement.Movement;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.AxisVecTuple;
import de.legoshi.parkourcalculator.util.Vec3;
import javafx.scene.paint.Color;
import lombok.Setter;

import java.util.*;

public class AStarPathfinder {
    
    private final BlockManager blockManager;
    private final Movement movement;

    @Setter private boolean colorize = false;
    private static final double MAX_AIR_COUNT = 3.4;
    
    public AStarPathfinder(Parkour parkour) {
        this.blockManager = parkour.getBlockManager();
        this.movement = parkour.getMovement();
        setColorize(true);
    }
    
    public List<ABlock> findShortestPath(Vec3 start, Vec3 end) {
        Map<Vec3, Node> nodes = new HashMap<>();
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(node -> node.fCost));
        
        Node startNode = new Node(start, null, 0, start.distanceTo(end), true);
        nodes.put(start, startNode);
        openSet.add(startNode);
        
        while (!openSet.isEmpty()) {
            Node currentNode = openSet.poll();
            
            if (currentNode.position.equals(end)) {
                return reconstructPath(currentNode);
            }
            
            for (Vec3 neighborPosition : getNeighbors(currentNode)) {
                if (neighborPosition.y < -21) continue; // shows importance of search boundaries for A*
                boolean isGround = !(blockManager.getBlock(neighborPosition) instanceof Air);

                if (!nodes.containsKey(neighborPosition)) {
                    double tentativeGCost = currentNode.gCost + currentNode.position.distanceTo(neighborPosition);
                    double hCost = neighborPosition.distanceTo(end);

                    Node neighborNode = new Node(neighborPosition, currentNode, tentativeGCost, hCost, isGround);
                    nodes.put(neighborPosition, neighborNode);
                    openSet.add(neighborNode);
                } else {
                    Node neighborNode = nodes.get(neighborPosition);
                    double tentativeGCost = currentNode.gCost + currentNode.position.distanceTo(neighborPosition);
                    
                    if (tentativeGCost < neighborNode.gCost) {
                        neighborNode.parent = currentNode;
                        neighborNode.gCost = tentativeGCost;
                        neighborNode.fCost = tentativeGCost + neighborNode.hCost;

                        if (isGround) {
                            neighborNode.lastGroundPos = neighborPosition.copy();
                        } else {
                            neighborNode.lastGroundPos = currentNode.lastGroundPos.copy();
                        }

                        openSet.remove(neighborNode);
                        openSet.add(neighborNode);
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    public List<Vec3> calculateBoundaries(Vec3 startPos, Vec3 endPos) {
        List<ABlock> boundaryBlocks = findShortestPath(startPos, endPos);
        List<Vec3> boundaries = new ArrayList<>();
        for (ABlock block : boundaryBlocks) {
            if (!(block instanceof Air)) {
                boundaries.add(block.getVec3());
            }
        }
        return boundaries;
    }
    
    private List<ABlock> reconstructPath(Node currentNode) {
        List<ABlock> path = new ArrayList<>();
        
        while (currentNode != null) {
            ABlock pathBlock = blockManager.getBlock(currentNode.position);
            if (colorize) {
                pathBlock.applyMaterialColor(Color.PURPLE);
            }
            path.add(pathBlock);
            
            currentNode = currentNode.parent;
        }
        
        Collections.reverse(path);
        return path;
    }
    
    private List<Vec3> getNeighbors(Node node) {
        List<Vec3> neighbors = new ArrayList<>();
        Vec3 position = node.position.copy();
        
        for (int x = (int) position.x - 1; x <= position.x + 1; x++) {
            for (int y = (int) position.y - 1; y <= position.y + 1; y++) {
                for (int z = (int) position.z - 1; z <= position.z + 1; z++) {
                    if (x != position.x || y != position.y || z != position.z) {
                        if (canNotStand(x, y + 1, z)) continue;
                        if (canNotStand(x, y + 2, z)) continue;
                        if (y == position.y - 1) {
                            if (canNotStand(x, y + 3, z)) continue;
                        }

                        Vec3 startPos = node.lastGroundPos.copy();
                        Vec3 endPos = new Vec3(x, y, z);
                        if (!allowDistance(startPos, endPos)) {
                            continue;
                        }

                        neighbors.add(new Vec3(x, y, z));
                    }
                }
            }
        }
        return neighbors;
    }

    private boolean canNotStand(int x, int y, int z) {
        ABlock checkBlock = blockManager.getBlock(x, y, z);
        if (checkBlock instanceof Air) return false;

        // Assuming each block has a 1x1x1 size, we define the bounds of the checkBlock
        double checkBlockMinX = x;
        double checkBlockMinZ = z;
        double checkBlockMaxX = x + 1.0;
        double checkBlockMaxZ = z + 1.0;

        // The ranges that need to be covered by the surrounding blocks
        double coveredRangeXMin = checkBlockMinX;
        double coveredRangeXMax = checkBlockMaxX;
        double coveredRangeZMin = checkBlockMinZ;
        double coveredRangeZMax = checkBlockMaxZ;

        // Check the surrounding blocks to expand the covered range
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                // Skip the center block where the entity is trying to stand
                if (dx == 0 && dz == 0) continue;

                ABlock otherBlock = blockManager.getBlock(x + dx, y, z + dz);
                if (!(otherBlock instanceof Air)) {

                    // Expand the covered range if this block extends beyond the current range
                    for (AxisVecTuple tuple : otherBlock.getAxisVecTuples()) {
                        AxisAlignedBB otherBlockBox = tuple.getBb();
                        coveredRangeXMin = Math.min(coveredRangeXMin, otherBlockBox.minX);
                        coveredRangeXMax = Math.max(coveredRangeXMax, otherBlockBox.maxX);
                        coveredRangeZMin = Math.min(coveredRangeZMin, otherBlockBox.minZ);
                        coveredRangeZMax = Math.max(coveredRangeZMax, otherBlockBox.maxZ);
                    }
                }
            }
        }

        // Determine if the covered ranges fully encompass the checkBlock's ranges
        boolean isCoveredX = coveredRangeXMin <= checkBlockMinX && coveredRangeXMax >= checkBlockMaxX;
        boolean isCoveredZ = coveredRangeZMin <= checkBlockMinZ && coveredRangeZMax >= checkBlockMaxZ;

        // If both the x and z ranges are fully covered, the entity cannot stand here
        return isCoveredX && isCoveredZ;
    }

    private boolean allowDistance(Vec3 start, Vec3 end) {
        ABlock endBlock = blockManager.getBlock(end);
        ABlock startBlock = blockManager.getBlock(start);

        AxisAlignedBB startBlockBB = startBlock.axisVecTuples.get(0).getBb();
        AxisAlignedBB endBlockBB = new AxisAlignedBB(end.copy(), new Vec3(end.x + 1, end.y + 1, end.z + 1));

        if (!(endBlock instanceof Air)) {
            endBlockBB = endBlock.getAxisVecTuples().get(0).getBb();
        }

        double endBlockY = endBlock.getHighestY() == Integer.MIN_VALUE ? end.y + 1 : endBlock.getHighestY();
        double verticalDistance = endBlockY - startBlock.getHighestY();

        if (movement.evalDistance(verticalDistance)) {
            return false;
        }

        double horizontalDistance = startBlockBB.distanceBetween(endBlockBB);
        int tier = movement.tierCalc(verticalDistance);

        // get allowed max distance for the given tier
        double maxAllowedDistance = movement.approxHorizontalDist(tier);
        return horizontalDistance < maxAllowedDistance;
    }
    
    private static class Node {

        private Vec3 position;
        private Node parent;
        private Vec3 lastGroundPos;

        private double gCost;
        private double hCost;
        private double fCost;
        
        public Node(Vec3 position, Node parent, double gCost, double hCost, boolean isGround) {
            this.position = position.copy();
            this.parent = parent;
            this.gCost = gCost;
            this.hCost = hCost;
            this.fCost = gCost + hCost;

            if (isGround) {
                this.lastGroundPos = position.copy();
            } else {
                this.lastGroundPos = parent.lastGroundPos.copy();
            }
        }
    }
    
}
