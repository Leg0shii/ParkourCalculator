package de.legoshi.parkourcalculator.ai;

import de.legoshi.parkourcalculator.gui.MinecraftGUI;
import de.legoshi.parkourcalculator.simulation.Parkour;
import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import de.legoshi.parkourcalculator.simulation.environment.block.Air;
import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager;
import de.legoshi.parkourcalculator.simulation.movement.Movement;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.AxisVecTuple;
import de.legoshi.parkourcalculator.util.Vec3;
import javafx.scene.paint.Color;
import lombok.Setter;

import java.util.*;

public class AStarPathfinder {

    @Setter
    private MinecraftGUI minecraftGUI;
    private final BlockManager blockManager;
    private final Movement movement;

    @Setter
    private boolean colorize = false;
    private static final double MAX_AIR_COUNT = 3.4;

    public AStarPathfinder(Parkour parkour, MinecraftGUI minecraftGUI) {
        this.minecraftGUI = minecraftGUI;
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
        boundaryBlocks.forEach(block -> boundaries.add(block.getVec3()));
        return boundaries;
    }

    private List<ABlock> reconstructPath(Node currentNode) {
        List<ABlock> path = new ArrayList<>();

        while (currentNode != null) {
            ABlock pathBlock = blockManager.getBlock(currentNode.position);
            if (colorize) {
                if (pathBlock instanceof Air) {
                    Vec3 flippedPos = currentNode.position.multiply(new Vec3(-1, 1, 1));
                    ABlock airBlock = new Air(flippedPos);
                    minecraftGUI.addBlock(airBlock);
                    path.add(airBlock);
                } else {
                    path.add(pathBlock);
                    pathBlock.applyMaterialColor(Color.PURPLE);
                }
            }

            currentNode = currentNode.parent;
        }

        Collections.reverse(path);
        return path;
    }

    private List<Vec3> getNeighbors(Node node) {
        List<Vec3> neighbors = new ArrayList<>();
        Vec3 position = node.position.copy();

        for (int x = (int) position.x - 1; x <= position.x + 1; x++) {
            for (int y = (int) position.y - 1; y <= position.y + 1 && y >= -26; y++) {
                for (int z = (int) position.z - 1; z <= position.z + 1; z++) {
                    if (x != position.x || y != position.y || z != position.z) {
                        ABlock floor = blockManager.getBlock(x, y, z);
                        if (canNotStand(y, x, y + 1, z)) continue;
                        if (canNotStand(y, x, y + 2, z)) continue;
                        if (y == position.y - 1) {
                            if (canNotStand(y, x, y + 3, z)) continue;
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

    public boolean canNotStand(int yOriginal, int xAbove, int yAbove, int zAbove) {
        ABlock aboveBlock = blockManager.getBlock(xAbove, yAbove, zAbove);
        if (aboveBlock instanceof Air) return false;
        List<AxisAlignedBB> aboveBB = aboveBlock.getAxisVecTuples().stream().map(AxisVecTuple::getBb).toList();

        ABlock checkBlock = blockManager.getBlock(xAbove, yOriginal, zAbove);
        List<AxisAlignedBB> checkBB = Collections.singletonList(new AxisAlignedBB(0, 0, 0, 0, 0, 0));
        if (!(checkBlock instanceof Air)) {
            checkBB = checkBlock.getAxisVecTuples().stream().map(AxisVecTuple::getBb).toList();
        }

        if (checkBB.get(0).minX == aboveBB.get(0).minX && checkBB.get(0).maxX == aboveBB.get(0).maxX &&
                checkBB.get(0).minZ == aboveBB.get(0).minZ && checkBB.get(0).maxZ == aboveBB.get(0).maxZ) {
            return true;
        }

        final double playerHitBoxSize = 0.6;
        for (AxisAlignedBB check : checkBB) {
            for (double x = check.minX; x < check.maxX; x += 0.0625) {
                for (double z = check.minZ; z < check.maxZ; z += 0.0625) {
                    AxisAlignedBB playerStandBox = new AxisAlignedBB(x, yOriginal, z, x + playerHitBoxSize, yAbove + 1, z + playerHitBoxSize);
                    if (aboveBB.stream().noneMatch(playerStandBox::intersectsWith)) {
                        return false;
                    }
                }
            }
        }

        return true;
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
