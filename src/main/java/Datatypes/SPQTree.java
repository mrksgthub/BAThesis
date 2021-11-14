package Datatypes;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.Set;

public class SPQTree {

    SPQNode root;
    Set<SPQNode> visited = new LinkedHashSet<>();
    DirectedMultigraph<Vertex, DefaultEdge> constructedGraph = new DirectedMultigraph<>(DefaultEdge.class);
    private  Hashtable<Vertex, ArrayList<Vertex>> vertexToAdjecencyListMap = new Hashtable<>();

    public SPQTree(SPQNode root) {
        this.root = root;





    }

    public DirectedMultigraph<Vertex, DefaultEdge> getConstructedGraph() {
        return constructedGraph;
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

    public Hashtable<Vertex, ArrayList<Vertex>> getVertexToAdjecencyListMap() {
        return vertexToAdjecencyListMap;
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


    public void compactTree(SPQNode root) {

        root.getMergedChildren().addAll(root.getChildren()); // mergedChildren sind die Kinder im SPQ*Baum

        for (SPQNode node : root.getChildren()
        ) {
            compactTree(node);
        }
        if (root.getParent() != null && root.getNodeType() == root.getParent().getNodeType() && !root.getParent().isRoot()) {
            root.mergeNodeWithParent(root, root.getParent());
        }

    }

    public void generateQStarNodes(SPQNode root) {

        for (SPQNode node : root.getMergedChildren()
        ) {
            generateQStarNodes(node);
        }
        root.generateQstarChildren();
    }

    public void determineInnerOuterNodesAndAdjVertices(SPQNode root) {

        root.addToAdjecencyListsSinkAndSource();
        for (SPQNode node : root.getMergedChildren()
        ) {
            determineInnerOuterNodesAndAdjVertices(node);
        }
        if (root.getMergedChildren().size() > 0) {
            for (SPQNode nodes :
                    root.getMergedChildren()) {
                root.addToSourceAndSinkLists(nodes);
            }
        }
    }


    public boolean computeNofRoot() { // Änderungen in neuer v4 aus Paper eingefügt

        int spirality = 99999;

        if (root.getMergedChildren().get(0).startNodes.size() == 1 && root.getMergedChildren().get(0).sinkNodes.size() == 1) {

            if (root.getMergedChildren().get(0).repIntervalLowerBound <= 6 && 2 <= root.getMergedChildren().get(0).repIntervalUpperBound) {

                spirality = (int) Math.ceil(Math.max(2.0, root.getMergedChildren().get(0).repIntervalLowerBound));
                //  spirality = 2;
            }

        } else if (root.getMergedChildren().get(0).startNodes.size() >= 2 && root.getMergedChildren().get(0).sinkNodes.size() >= 2) {
            if (root.getMergedChildren().get(0).repIntervalLowerBound <= 4 && 4 <= root.getMergedChildren().get(0).repIntervalUpperBound) {
                spirality = 4;
            }


        } else {
            if (root.getMergedChildren().get(0).repIntervalLowerBound <= 5 && 3 <= root.getMergedChildren().get(0).repIntervalUpperBound) {
                spirality = (int) Math.ceil(Math.max(3.0, root.getMergedChildren().get(0).repIntervalLowerBound));
                //  spirality = 3;
            }

        }


        if (root.getMergedChildren().get(0).getRepIntervalLowerBound() <= spirality && spirality <= root.getMergedChildren().get(0).getRepIntervalUpperBound()) {
            root.getMergedChildren().get(0).setSpiralityOfChildren(spirality);
            return true;
        } else {
            System.out.println("Fehler?");
            return false;
        }
    }





    public void computeSpirality(SPQNode root) {

        root.setSpiralityOfChildren();

        for (SPQNode node : root.getMergedChildren()
        ) {
            computeSpirality(node);
        }

    }

    public Boolean computeRepresentability() {
        Boolean check = true;
        check = this.computeRepresentabilityIntervals(root, check);
        if (check) {
            check = (this.computeNofRoot()) ? check : false;
            if (!check) {
                System.out.println("Didimo rejected at source Node");
            }
        }
        return check;
    }






    private Boolean computeRepresentabilityIntervals(SPQNode root, Boolean check) {

        boolean temp;
        for (SPQNode node : root.getMergedChildren()
        ) {
            temp = this.computeRepresentabilityIntervals(node, check);
            if (!temp) {
                check = temp;
            }
        }

        if (root.getMergedChildren().size() != 0 && !root.isRoot()) {
            if (!root.calculateRepresentabilityInterval()) {
                check = false;
            }
        }
        return check;
    }








    public void generateAdjecencyListMaP(SPQNode node) {

        vertexToAdjecencyListMap.putIfAbsent(node.getStartVertex(), node.getStartVertex().getAdjecentVertices());
        vertexToAdjecencyListMap.putIfAbsent(node.getSinkVertex(), node.getSinkVertex().getAdjecentVertices());

        assert (node.getStartVertex() != null);
        assert (node.getSinkVertex() != null);

        for (SPQNode child : node.getMergedChildren()
        ) {
            generateAdjecencyListMaP(child);
        }


    }











}
























