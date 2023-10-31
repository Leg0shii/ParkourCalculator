package de.legoshi.parkourcalculator.ai;

import de.legoshi.parkourcalculator.simulation.Parkour;
import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import de.legoshi.parkourcalculator.simulation.environment.block.Air;
import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager;
import de.legoshi.parkourcalculator.simulation.movement.Movement;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
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
                // if (neighborPosition.y < 0) continue;
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
                        if (hasBlock(x, y + 1, z)) continue;
                        if (hasBlock(x, y + 2, z)) continue;
                        if (y == position.y - 1) {
                            if (hasBlock(x, y + 3, z)) continue;
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
    
    private boolean hasBlock(int x, int y, int z) {
        ABlock checkBlock = blockManager.getBlock(x, y, z);
        return !(checkBlock instanceof Air);
    }

    private boolean allowDistance(Vec3 start, Vec3 end) {
        ABlock endBlock = blockManager.getBlock(end);

        double verticalDistance = end.y - start.y;
        if (verticalDistance > 1.6) {
            return false;
        }

        AxisAlignedBB startBlockBB = blockManager.getBlock(start).getAxisVecTuples().get(0).getBb();
        AxisAlignedBB endBlockBB = new AxisAlignedBB(end.copy(), new Vec3(end.x + 1, end.y + 1, end.z + 1));
        if (!(endBlock instanceof Air)) {
            endBlockBB = endBlock.getAxisVecTuples().get(0).getBb();
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
