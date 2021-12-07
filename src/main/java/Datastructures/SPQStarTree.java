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



    private void setStartAndSinkNodesAndBuildConstructedGraph(SPQNode root) {

        Deque<SPQNode> stack = DFSIterator.buildPostOrderStack(root);
        while (!stack.isEmpty()) {
            SPQNode node = stack.pop();
            if (node.getNodeType() != SPQNode.NodeTypesEnum.NODETYPE.Q || node.getSpqChildren().size() > 0) {
                node.setSourceVertex(node.getSpqChildren().get(0).getSourceVertex());
                node.setSinkVertex(node.getSpqChildren().get(node.getSpqChildren().size() - 1).getSinkVertex());

            } else {
                constructedGraph.addVertex(node.getSourceVertex());
                constructedGraph.addVertex(node.getSinkVertex());
                constructedGraph.addEdge(node.getSourceVertex(), node.getSinkVertex());
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
        setStartAndSinkNodesAndBuildConstructedGraph(root);
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


}
























