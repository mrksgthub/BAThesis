package Datastructures;

import Helperclasses.DFSIterator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.*;

public class SPQStarTree {

    private SPQNode root;
    private Set<SPQNode> visited = new LinkedHashSet<>();
    private DirectedMultigraph<Vertex, DefaultEdge> constructedGraph = new DirectedMultigraph<>(DefaultEdge.class);
    private Hashtable<Vertex, ArrayList<Vertex>> vertexToAdjecencyListMap = new Hashtable<>();

    public SPQStarTree(SPQNode root) {
        this.root = root;


    }

    public DirectedMultigraph<Vertex, DefaultEdge> getConstructedGraph() {
        return constructedGraph;
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



    private void setStartAndSinkNodesOrBuildConstructedGraph(SPQNode root) {

        Deque<SPQNode> stack = DFSIterator.buildPostOrderStack(root);
        while (!stack.isEmpty()) {
            SPQNode node = stack.pop();
            if (node.getNodeType() != SPQNode.NodeTypesEnum.NODETYPE.Q || node.getSpqChildren().size() > 0) {
                node.setStartVertex(node.getSpqChildren().get(0).getStartVertex());
                node.setSinkVertex(node.getSpqChildren().get(node.getSpqChildren().size() - 1).getSinkVertex());

            } else {
                constructedGraph.addVertex(node.getStartVertex());
                constructedGraph.addVertex(node.getSinkVertex());
                constructedGraph.addEdge(node.getStartVertex(), node.getSinkVertex());
            }
        }

    }




    private void compactTree(SPQNode root) {

        Deque<SPQNode> stack = DFSIterator.buildPostOrderStack(root);
        while (!stack.isEmpty()) {
            SPQNode node = stack.pop();
            if (node.getParent() != null && node.getNodeType() == node.getParent().getNodeType() && !node.getParent().isRoot()) {
                node.mergeNodeWithParent(node, node.getParent());
            }
        }
    }




    private void generateQStarNodes(SPQNode root) {

        Deque<SPQNode> stack = DFSIterator.buildPostOrderStack(root);
        while (!stack.isEmpty()) {
            SPQNode node = stack.pop();
            node.generateQstarChildren();
        }

    }

    public void addValidSPQStarTreeRepresentation(SPQNode root) {
        compactTree(root);
        generateQStarNodes(root);
    }

    public void initializeSPQNodes(SPQNode root) {
        setStartAndSinkNodesOrBuildConstructedGraph(root);
        calculateAdjaecencyListsOfSinkAndSource(root);
        determineInnerOuterAdjecentsOfSinkAndSource(root);
    }


    private void determineInnerOuterNodesAndAdjVertices(SPQNode root) {
        calculateAdjaecencyListsOfSinkAndSource(root);
        determineInnerOuterAdjecentsOfSinkAndSource(root);
    }


    private void calculateAdjaecencyListsOfSinkAndSource(SPQNode root) {

        Deque<SPQNode> stack = DFSIterator.buildPreOrderStack(root);
        while (!stack.isEmpty()) {
            SPQNode node = stack.pop();
            node.addToAdjacencyListsSinkAndSource();
        }
    }

    private void determineInnerOuterAdjecentsOfSinkAndSource(SPQNode root) {

        Deque<SPQNode> stack = DFSIterator.buildPostOrderStack(root);
        while (!stack.isEmpty()) {
            SPQNode node = stack.pop();
            if (node.getSpqChildren().size() > 0) {
                for (SPQNode nodes :
                        node.getSpqChildren()) {
                    node.addToSourceAndSinkLists(nodes); //innere adjazente Knoten
                }
            }
        }
    }








    public void determineInnerOuterNodesAndAdjVertices2(SPQNode root) {

        root.addToAdjacencyListsSinkAndSource(); // AdjLists der Knoten des SP-Graphen
        for (SPQNode node : root.getSpqChildren()
        ) {
            determineInnerOuterNodesAndAdjVertices(node);
        }
        if (root.getSpqChildren().size() > 0) {
            for (SPQNode nodes :
                    root.getSpqChildren()) {
                root.addToSourceAndSinkLists(nodes); //innere adjazente Knoten
            }
        }
    }

    private void compactTree2(SPQNode root) {

        root.getSpqChildren().addAll(root.getSpqChildren()); // mergedChildren sind die Kinder im SPQ*Baum

        for (SPQNode node : root.getSpqChildren()
        ) {
            compactTree(node);
        }

        if (root.getParent() != null && root.getNodeType() == root.getParent().getNodeType() && !root.getParent().isRoot()) {
            root.mergeNodeWithParent(root, root.getParent());
        }
    }

    private void generateQStarNodes2(SPQNode root) {

        for (SPQNode node : root.getSpqChildren()
        ) {
            generateQStarNodes(node);
        }
        root.generateQstarChildren();
    }

    public void setStartAndSinkNodesOrBuildConstructedGraph2(SPQNode root) {


        for (SPQNode node : root.getSpqChildren()
        ) {
            setStartAndSinkNodesOrBuildConstructedGraph(node);
        }
        if (root.getNodeType() != SPQNode.NodeTypesEnum.NODETYPE.Q || root.getSpqChildren().size() > 0) {
            root.setStartVertex(root.getSpqChildren().get(0).getStartVertex());
            root.setSinkVertex(root.getSpqChildren().get(root.getSpqChildren().size() - 1).getSinkVertex());

        } else {
            constructedGraph.addVertex(root.getStartVertex());
            constructedGraph.addVertex(root.getSinkVertex());
            constructedGraph.addEdge(root.getStartVertex(), root.getSinkVertex());
        }

    }

}
























