import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.*;

public class SPQTree {

    SPQNode root;
    Hashtable<SPQNode, List<SPQNode>> nodeTOedgesTable = new Hashtable<>();
    Set<SPQNode> visited = new LinkedHashSet<SPQNode>();
    DirectedMultigraph<TreeVertex, DefaultEdge> constructedGraph = new DirectedMultigraph<TreeVertex, DefaultEdge>(DefaultEdge.class);
    Hashtable<SPQNode, HashSet<SPQNode>> vertexAdjMap = new Hashtable<>();


    public SPQTree(SPQNode root) {
        this.root = root;


    }

    public Set<SPQNode> getVisited() {
        return visited;
    }

    public void setVisited(Set<SPQNode> visited) {
        this.visited = visited;
    }

    public SPQNode getRoot() {
        return root;
    }

    public void setRoot(SPQNode root) {
        this.root = root;
    }

    public Hashtable<SPQNode, List<SPQNode>> getNodeTOedgesTable() {
        return nodeTOedgesTable;
    }

    public void setNodeTOedgesTable(Hashtable<SPQNode, List<SPQNode>> nodeTOedgesTable) {
        this.nodeTOedgesTable = nodeTOedgesTable;
    }

    public void fillNodeToEdgesTable(SPQNode root) {

        nodeTOedgesTable.putIfAbsent(root, root.mergedChildren);

        for (SPQNode node : root.mergedChildren
        ) {
            fillNodeToEdgesTable(node);
        }
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

        } else {
            constructedGraph.addVertex(root.getStartVertex());
            constructedGraph.addVertex(root.getSinkVertex());
            constructedGraph.addEdge(root.getStartVertex(), root.getSinkVertex());
        }

    }

    public void determineAdjecents() {

        for (TreeVertex vertex : constructedGraph.vertexSet()
        ) {
            Set<TreeVertex> adjSetStart = Graphs.neighborSetOf(constructedGraph, vertex);

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
























