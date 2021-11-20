package Datatypes;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.Set;

public class SPQStarTree {

    private SPQNode root;
    private Set<SPQNode> visited = new LinkedHashSet<>();
    private DirectedMultigraph<Vertex, DefaultEdge> constructedGraph = new DirectedMultigraph<>(DefaultEdge.class);
    private  Hashtable<Vertex, ArrayList<Vertex>> vertexToAdjecencyListMap = new Hashtable<>();

    public SPQStarTree(SPQNode root) {
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


        for (SPQNode node : root.getSpqStarChildren()
        ) {
            setStartAndSinkNodesOrBuildConstructedGraph(node, visited);
        }
        if (root.getNodeType() != NodeTypesEnum.NODETYPE.Q || root.getSpqStarChildren().size() > 0) {
            root.setStartVertex(root.getSpqStarChildren().get(0).getStartVertex());
            root.setSinkVertex(root.getSpqStarChildren().get(root.getSpqStarChildren().size() - 1).getSinkVertex());

        } else {
            constructedGraph.addVertex(root.getStartVertex());
            constructedGraph.addVertex(root.getSinkVertex());
            constructedGraph.addEdge(root.getStartVertex(), root.getSinkVertex());
        }

    }


    private void compactTree(SPQNode root) {

        root.getSpqStarChildren().addAll(root.getSpqChildren()); // mergedChildren sind die Kinder im SPQ*Baum

        for (SPQNode node : root.getSpqChildren()
        ) {
            compactTree(node);
        }
        if (root.getParent() != null && root.getNodeType() == root.getParent().getNodeType() && !root.getParent().isRoot()) {
            root.mergeNodeWithParent(root, root.getParent());
        }

    }

    private void generateQStarNodes(SPQNode root) {

        for (SPQNode node : root.getSpqStarChildren()
        ) {
            generateQStarNodes(node);
        }
        root.generateQstarChildren();
    }

    public void determineInnerOuterNodesAndAdjVertices(SPQNode root) {

        root.addToAdjecencyListsSinkAndSource(); // AdjLists
        for (SPQNode node : root.getSpqStarChildren()
        ) {
            determineInnerOuterNodesAndAdjVertices(node);
        }
        if (root.getSpqStarChildren().size() > 0) {
            for (SPQNode nodes :
                    root.getSpqStarChildren()) {
                root.addToSourceAndSinkLists(nodes); //innere adjazente Knoten
            }
        }
    }


    public void addValidSPQStarTreeRepresentation() {

        this.compactTree(this.getRoot());
        this.generateQStarNodes(this.getRoot());

        initializeSPQNodes(this.getRoot());
        //  this.generateAdjecencyListMaP(this.getRoot());

    }

    public void initializeSPQNodes(SPQNode root) {
        this.setStartAndSinkNodesOrBuildConstructedGraph(root, this.getVisited());
        this.determineInnerOuterNodesAndAdjVertices(root);
    }
}
























