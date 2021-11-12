package Datatypes;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.*;

public class SPQTree {

    SPQNode root;
    Set<SPQNode> visited = new LinkedHashSet<>();
    DirectedMultigraph<Vertex, DefaultEdge> constructedGraph = new DirectedMultigraph<>(DefaultEdge.class);

    public DirectedMultigraph<Vertex, DefaultEdge> getConstructedGraph() {
        return constructedGraph;
    }

    public SPQTree(SPQNode root) {
        this.root = root;


    }

    public Set<SPQNode> getVisited() {
        return visited;
    }

    public SPQNode getRoot() {
        return root;
    }

    public void setRoot(SPQNode root) {
        this.root = root;
    }

    public void setStartAndSinkNodesOrBuildConstructedGraph(SPQNode root, Set<SPQNode> visited) {
        visited.add(root);


        for (SPQNode node : root.getMergedChildren()
        ) {
            setStartAndSinkNodesOrBuildConstructedGraph(node, visited);
        }
        if (root.getNodeType() != NodeTypesEnum.NODETYPE.Q || root.getMergedChildren().size() > 0) {
            root.setStartVertex(root.getMergedChildren().get(0).getStartVertex());
            root.setSinkVertex(root.getMergedChildren().get(root.getMergedChildren().size() - 1).getSinkVertex());

        } else { // ist eine Q-Node
            constructedGraph.addVertex(root.getStartVertex());
            constructedGraph.addVertex(root.getSinkVertex());
            constructedGraph.addEdge(root.getStartVertex(), root.getSinkVertex());
        }

    }

    public boolean computeNofRoot() { // Änderungen in neuer v4 aus Paper eingefügt

        int spirality = 99999;

        if (root.getMergedChildren().get(0).startNodes.size() == 1 && root.getMergedChildren().get(0).sinkNodes.size() == 1) {
            
            if (root.getMergedChildren().get(0).repIntervalLowerBound <= 6 && 2 <= root.getMergedChildren().get(0).repIntervalUpperBound) {

                spirality = (int) Math.ceil(Math.max(2.0,root.getMergedChildren().get(0).repIntervalLowerBound));
              //  spirality = 2;
            }

        } else if (root.getMergedChildren().get(0).startNodes.size() >= 2 && root.getMergedChildren().get(0).sinkNodes.size() >= 2) {
            if (root.getMergedChildren().get(0).repIntervalLowerBound <= 4 && 4 <= root.getMergedChildren().get(0).repIntervalUpperBound) {
                spirality = 4;
            }



        } else {
            if (root.getMergedChildren().get(0).repIntervalLowerBound <= 5 && 3 <= root.getMergedChildren().get(0).repIntervalUpperBound) {
                spirality = (int) Math.ceil(Math.max(3.0,root.getMergedChildren().get(0).repIntervalLowerBound));
              //  spirality = 3;
            }

        }


        if (root.getMergedChildren().get(0).getRepIntervalLowerBound() <= spirality && spirality <= root.getMergedChildren().get(0).getRepIntervalUpperBound()) {
            root.getMergedChildren().get(0).setSpirality(spirality);
            return true;
        } else {
            System.out.println("Fehler?");
            return false;
        }
    }


}
























