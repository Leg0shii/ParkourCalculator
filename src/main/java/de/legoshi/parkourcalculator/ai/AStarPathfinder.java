package de.legoshi.parkourcalculator.ai;

import de.legoshi.parkourcalculator.gui.MinecraftGUI;
import de.legoshi.parkourcalculator.simulation.Parkour;
import de.legoshi.parkourcalculator.simulation.environment.block.*;
import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager;
import de.legoshi.parkourcalculator.simulation.movement.Movement;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.AxisVecTuple;
import de.legoshi.parkourcalculator.util.Vec3;
import javafx.scene.paint.Color;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class AStarPathfinder {

    private static final Logger logger = LogManager.getLogger(AStarPathfinder.class.getName());

    @Setter
    private MinecraftGUI minecraftGUI;
    private final BlockManager blockManager;
    private final Movement movement;
    private final Parkour parkour;

    @Setter
    private boolean colorize = false;
    private static final double DISTANCE_THRESHOLD = 2;
    private static final double PENALTY_THRESHOLD = 4.85;

    private static final int MIN_X = -50;
    private static final int MAX_X = 50;

    private static final int MIN_Y = -20;
    private static final int MAX_Y = 50;

    private static final int MIN_Z = -50;
    private static final int MAX_Z = 50;

    public AStarPathfinder(Parkour parkour, MinecraftGUI minecraftGUI) {
        this.minecraftGUI = minecraftGUI;
        this.parkour = parkour;
        this.blockManager = parkour.getBlockManager();
        this.movement = parkour.getMovement();
        setColorize(true);
    }

    public List<ABlock> findShortestPath(Vec3 start, Vec3 end) {
        Map<Vec3, BlockNode> nodes = new HashMap<>();
        PriorityQueue<BlockNode> openSet = new PriorityQueue<>(Comparator.comparingDouble(BlockNode::getFCost));

        logger.info("A* Started!");
        BlockNode startNode = new BlockNode(start, start.distanceTo(end));
        nodes.put(start, startNode);
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            BlockNode currentNode = openSet.poll();

            if (currentNode.getPosition().equals(end)) {
                return reconstructPath(currentNode);
            }

            for (Vec3 neighborPosition : getNeighbors(currentNode)) {
                boolean isJump = blockManager.getBlock(neighborPosition) instanceof Air;

                Vec3 climbPos = new Vec3(neighborPosition.x, neighborPosition.y + 1, neighborPosition.z);
                boolean isClimb = isClimbable(blockManager.getBlock(climbPos));

                boolean isSwim = isSwimable(blockManager.getBlock(neighborPosition));

                if (!nodes.containsKey(neighborPosition)) {
                    double tentativeGCost = currentNode.getGCost() + currentNode.getPosition().distanceTo(neighborPosition);
                    double hCost = neighborPosition.distanceTo(end);

                    BlockNode neighborNode = new BlockNode(currentNode, neighborPosition, tentativeGCost, hCost, isJump, isClimb, isSwim);
                    nodes.put(neighborPosition, neighborNode);
                    openSet.add(neighborNode);
                } else {
                    BlockNode neighborNode = nodes.get(neighborPosition);
                    double tentativeGCost = currentNode.getGCost() + currentNode.getPosition().distanceTo(neighborPosition);

                    if (tentativeGCost < neighborNode.getGCost()) {
                        neighborNode.setParent(currentNode);
                        neighborNode.setGCost(tentativeGCost);
                        neighborNode.setFCost(tentativeGCost + neighborNode.getHCost());
                        neighborNode.updateLastGroundPos();

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

    private List<Vec3> getNeighbors(BlockNode currentNode) {
        if (currentNode.isClimb()) {
            return getClimbNeighbors(currentNode);
        } else {
            return getBlockNeighbors(currentNode);
        }
    }

    private List<Vec3> getBlockNeighbors(BlockNode currentNode) {
        List<Vec3> neighbors = new ArrayList<>();
        Vec3 position = currentNode.getPosition().copy();

        for (int x = (int) position.x - 1; x <= position.x + 1; x++) {
            if (x < MIN_X || x > MAX_X) continue;
            for (int y = (int) position.y - 1; y <= position.y + 1; y++) {
                if (y < MIN_Y || y > MAX_Y) continue;
                for (int z = (int) position.z - 1; z <= position.z + 1; z++) {
                    if (z < MIN_Z || z > MAX_Z) continue;
                    if (x == position.x && y == position.y && z == position.z) continue;

                    if (canNotStand(y, x, y + 1, z)) continue;
                    if (canNotStand(y, x, y + 2, z)) continue;
                    if (y == position.y - 1) {
                        if (canNotStand(y, x, y + 3, z)) continue;
                    }

                    Vec3 endPos = new Vec3(x, y, z);
                    if (!allowJumpDistance(currentNode, endPos.copy())) continue;
                    neighbors.add(endPos.copy());
                }
            }
        }
        return neighbors;
    }

    private List<Vec3> getClimbNeighbors(BlockNode currentNode) {
        List<Vec3> neighbors = new ArrayList<>();
        Vec3 position = currentNode.getPosition().copy();
        Vec3 below = new Vec3(position.x, position.y - 1, position.z);
        Vec3 above = new Vec3(position.x, position.y + 1, position.z);

        ABlock blockBelow = blockManager.getBlock(below);
        ABlock blockAbove = blockManager.getBlock(above);

        if (isClimbable(blockBelow) || blockBelow instanceof Air) neighbors.add(below);
        if (isClimbable(blockAbove) || blockAbove instanceof Air) neighbors.add(above);

        return neighbors;
    }

    public boolean isClimbable(ABlock block) {
        return block instanceof Ladder || block instanceof Vine;
    }

    public boolean isSwimable(ABlock block) {
        return block instanceof Water || block instanceof Lava;
    }

    public boolean isTraversable(ABlock block) {
        return isClimbable(block) && block instanceof Air;
    }

    public boolean canNotStand(int yOriginal, int xPos, int yAbove, int zPos) {
        ABlock aboveBlock = blockManager.getBlock(xPos, yAbove, zPos);
        if (aboveBlock instanceof Air) return false;
        if (aboveBlock instanceof Ladder) return false;

        List<AxisAlignedBB> aboveBB = aboveBlock.getAxisVecTuples().stream().map(AxisVecTuple::getBb).toList();

        ABlock checkBlock = blockManager.getBlock(xPos, yOriginal, zPos);
        List<AxisAlignedBB> checkBB = Collections.singletonList(new AxisAlignedBB(0, 0, 0, 0, 0, 0));
        if (!(checkBlock instanceof Air)) {
            checkBB = checkBlock.getAxisVecTuples().stream().map(AxisVecTuple::getBb).toList();
        }

        final double playerHitBoxSize = 0.6;
        for (AxisAlignedBB check : checkBB) {
            for (double x = check.minX; x < check.maxX; x += 0.0625) {
                for (double z = check.minZ; z < check.maxZ; z += 0.0625) {
                    AxisAlignedBB playerStandBox = new AxisAlignedBB(x, yOriginal, z, x + playerHitBoxSize, yAbove + 0.8, z + playerHitBoxSize);
                    if (aboveBB.stream().noneMatch(playerStandBox::intersectsWith)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean allowJumpDistance(BlockNode node, Vec3 end) {
        Vec3 startPos = node.getLastGroundPos().copy();
        Vec3 endPos = end.copy();

        ABlock startBlock = blockManager.getBlock(startPos);
        ABlock endBlock = blockManager.getBlock(endPos);
        double verticalDistance = endBlock.getHighestY() - startBlock.getHighestY();

        if (movement.evalDistance(verticalDistance)) {
            return false;
        }

        double deltaX = Math.abs(startPos.x - endPos.x);
        double deltaZ = Math.abs(startPos.z - endPos.z);
        double horizontalDistance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaZ, 2)) - 1;

        // Safety net
        int tier = movement.tierCalc(verticalDistance);
        double maxAllowedDistance = movement.approxHorizontalDist(tier);
        if (horizontalDistance >= maxAllowedDistance) return false;

        /*if (horizontalDistance > DISTANCE_THRESHOLD && !(endBlock instanceof Air)) {
            List<BlockNode> nodes = new ArrayList<>();
            BlockNode nodeToCheck = node;
            String path = nodeToCheck.getPosition() + "";
            while (!nodeToCheck.getLastGroundPos().equals(nodeToCheck.getPosition())) {
                nodes.add(nodeToCheck);
                nodeToCheck = nodeToCheck.getParent();
                path = path + " " + nodeToCheck.getPosition() + " CLIMB: " + nodeToCheck.isClimb();
            }
            double penalty = 1; // calculatePathCost(nodes, 1);
            logger.info("Path: {}, Penalty: {}", path, penalty);
            return !(penalty > PENALTY_THRESHOLD);
        }*/
        return true;
    }

    public double calculatePathCost(List<BlockNode> nodes, double beta) {
        if (nodes == null || nodes.size() < 2) {
            return 0.0;
        }

        double totalCost = 1;

        Vec3 previousDirection = null;

        for (int i = 1; i < nodes.size(); i++) {
            BlockNode currentNode = nodes.get(i);
            BlockNode previousNode = nodes.get(i - 1);

            Vec3 movementVector = currentNode.getPosition().copy();
            movementVector.subtract(previousNode.getPosition());

            double distance = movementVector.length();

            Vec3 direction = movementVector.normalize();
            double movementCost = distance;

            if (previousDirection != null) {
                double dotProduct = direction.dot(previousDirection);
                dotProduct = Math.max(-1.0, Math.min(1.0, dotProduct));
                double penalty = beta * (1 - dotProduct);
                movementCost *= (1 + penalty);
            }

            totalCost += movementCost;
            previousDirection = direction;
        }

        return totalCost;
    }

    private List<ABlock> reconstructPath(BlockNode currentNode) {
        List<ABlock> path = new ArrayList<>();

        List<BlockNode> nodes = new ArrayList<>();
        nodes.add(currentNode);
        while (currentNode != null) {
            Vec3 currentPos = currentNode.getPosition();
            ABlock pathBlock = blockManager.getBlock(currentPos);
            if (colorize) {
                if (pathBlock instanceof Air) {
                    Vec3 flippedPos = currentPos.multiply(new Vec3(-1, 1, 1));
                    ABlock airBlock = new Air(flippedPos);
                    minecraftGUI.addBlock(airBlock);
                    path.add(airBlock);
                } else {
                    path.add(pathBlock);
                    pathBlock.applyMaterialColor(Color.PURPLE);
                }
            }
            currentNode = currentNode.getParent();
            nodes.add(currentNode);
        }

        Collections.reverse(path);
        StringBuilder stringPath = new StringBuilder();
        /*for (BlockNode n : nodes) {
            if (n != null)
                stringPath.append("Found: ").append(n.getPosition())
                        .append(" JUMP: ").append(n.isJump())
                        .append(" CLIMB: ").append(n.isClimb())
                        .append(" SWIM: ").append(n.isSwim())
                        .append("\n");
        }

        logger.info(stringPath.toString());*/
        logger.info("Path Found!");
        return path;
    }

}
