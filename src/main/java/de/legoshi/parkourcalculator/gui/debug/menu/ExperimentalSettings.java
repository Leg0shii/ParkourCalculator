package de.legoshi.parkourcalculator.gui.debug.menu;

import de.legoshi.parkourcalculator.util.Vec3;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.*;

public class ExperimentalSettings extends TitledPane {

    private final TextField startTF;
    private final TextField endTF;
    private final Button findBTN;

    public ExperimentalSettings() {
        Text titleText = new Text("Experimental Settings");
        titleText.setFill(Color.WHITE);
        setGraphic(titleText);

        this.startTF = new TextField();
        this.endTF = new TextField();
        this.findBTN = new Button("Find");

        this.findBTN.setOnAction(event -> find());

        VBox vBox = new VBox(startTF, endTF, findBTN);
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.setAlignment(Pos.BASELINE_CENTER);
        setContent(vBox);

        disable();
    }

    private void disable() {
        this.startTF.setDisable(true);
        this.endTF.setDisable(true);
        this.findBTN.setDisable(true);
    }

    private void find() {
        List<Double> sD = Arrays.stream(startTF.getText().split(";")).map((Double::parseDouble)).toList();
        List<Double> eD = Arrays.stream(endTF.getText().split(";")).map((Double::parseDouble)).toList();
        Vec3 start = new Vec3(sD.get(0), sD.get(1), sD.get(2));
        Vec3 end = new Vec3(eD.get(0), eD.get(1), eD.get(2));
        List<Vec3> path = findShortestPath(start, end);
    }

    private List<Vec3> findShortestPath(Vec3 start, Vec3 end) {
        Map<Vec3, Node> nodes = new HashMap<>();
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(node -> node.fCost));

        Node startNode = new Node(start, null, 0, start.distanceTo(end));
        nodes.put(start, startNode);
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node currentNode = openSet.poll();

            if (currentNode.position.equals(end)) {
                return reconstructPath(currentNode);
            }

            for (Vec3 neighborPosition : getNeighbors(currentNode.position)) {
                if (!nodes.containsKey(neighborPosition)) {
                    double tentativeGCost = currentNode.gCost + currentNode.position.distanceTo(neighborPosition);
                    double hCost = neighborPosition.distanceTo(end);
                    Node neighborNode = new Node(neighborPosition, currentNode, tentativeGCost, hCost);
                    nodes.put(neighborPosition, neighborNode);
                    openSet.add(neighborNode);
                } else {
                    Node neighborNode = nodes.get(neighborPosition);
                    double tentativeGCost = currentNode.gCost + currentNode.position.distanceTo(neighborPosition);

                    if (tentativeGCost < neighborNode.gCost) {
                        neighborNode.parent = currentNode;
                        neighborNode.gCost = tentativeGCost;
                        neighborNode.fCost = tentativeGCost + neighborNode.hCost;
                        openSet.remove(neighborNode);
                        openSet.add(neighborNode);
                    }
                }
            }
        }

        return null; // No path found
    }

    private List<Vec3> reconstructPath(Node currentNode) {
        List<Vec3> path = new ArrayList<>();

        while (currentNode != null) {
            path.add(currentNode.position);
            currentNode = currentNode.parent;
        }

        Collections.reverse(path);
        return path;
    }

    private List<Vec3> getNeighbors(Vec3 position) {
        // can the player stand here? -> neighbor
        return null;
    }

    class Node {
        Vec3 position;
        Node parent;
        double gCost;
        double hCost;
        double fCost;

        public Node(Vec3 position, Node parent, double gCost, double hCost) {
            this.position = position;
            this.parent = parent;
            this.gCost = gCost;
            this.hCost = hCost;
            this.fCost = gCost + hCost;
        }
    }

}
