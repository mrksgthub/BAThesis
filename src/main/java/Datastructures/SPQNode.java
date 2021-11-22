package Datastructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class SPQNode {


    static int id = 0;
    List<SPQNode> spqChildren = new ArrayList<>();
    List<Vertex> startNodes = new ArrayList<>();
    List<Vertex> sinkNodes = new ArrayList<>();
    double spirality = 999999;
    int counter = 0;
    NodeTypesEnum.NODETYPE nodeType;
    Vertex startVertex;
    Vertex sinkVertex;
    double repIntervalLowerBound = 999;
    double repIntervalUpperBound = -990;
    boolean isRoot = false;
    private SPQNode parent;
    private String name;


    SPQNode(String name) {
        this.name = name;
    }

    public List<SPQNode> getSpqChildren() {
        return spqChildren;
    }

    void setSpqChildren(List<SPQNode> spqStarChildren) {
        this.spqChildren = spqStarChildren;
    }

    public NodeTypesEnum.NODETYPE getNodeType() {
        return nodeType;
    }

    double getSpirality() {
        return spirality;
    }

    public void setSpiralityOfChildren(double spirality) {
        this.spirality = spirality;
    }


    public double getRepIntervalLowerBound() {
        return repIntervalLowerBound;
    }

    public double getRepIntervalUpperBound() {
        return repIntervalUpperBound;
    }

    public boolean calculateRepresentabilityInterval() {

        return true;
    }

    public Vertex getStartVertex() {
        return startVertex;
    }

    public void setStartVertex(Vertex startVertex) {
        this.startVertex = startVertex;
    }

    public Vertex getSinkVertex() {
        return sinkVertex;
    }

    public void setSinkVertex(Vertex sinkVertex) {
        this.sinkVertex = sinkVertex;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }




    public SPQNode getParent() {
        return parent;
    }

    public void setParent(SPQNode parent) {
        this.parent = parent;
    }


    public boolean isRoot() {
        return this.isRoot;
    }

    public void setToRoot() {
        isRoot = true;

    }


    public void generateQstarChildren() {

    }

    /**
     * Bestimmt die Quelle und Senke des Knotens, basierend auf seinen Kindsknoten
     *
     * @param nodes
     */
    public void addToSourceAndSinkLists(SPQNode nodes) {

        if (this.getStartVertex() == nodes.getStartVertex()) {
            startNodes.addAll(nodes.startNodes);
        }
        if (this.getSinkVertex() == nodes.getSinkVertex()) {
            sinkNodes.addAll(nodes.sinkNodes);
        }
    }

    public void addToAdjecencyListsSinkAndSource() {
        if (this.getNodeType() == NodeTypesEnum.NODETYPE.Q && spqChildren.size() == 0) {
            startVertex.adjacentVertices.add(sinkVertex);
            sinkVertex.adjacentVertices.add(0, startVertex);
        }
    }


    void mergeNodeWithParent(SPQNode node, SPQNode parent) {

        int pos = parent.spqChildren.indexOf(node);
        parent.spqChildren.remove(node);

        for (SPQNode spQNode : node.spqChildren
        ) {
            parent.spqChildren.add(pos++, spQNode);
            spQNode.setParent(parent);
        }
    }




    /**
     * Berechnet die Winkel aus der Spiralität nach Didimo et al. 2020 und füght sie in hashMap ein. (Wird nur in Q* und P-Knoten gemacht)
     *
     * @param angleMap bildet Vertex -> Winkel ab.
     */
    public void computeAngles(HashMap<TupleEdge<Vertex, Vertex>, Integer> angleMap) {

    }


    public void setSpiralityOfChildren() {
    }

    public List<Vertex> getStartNodes() {
        return startNodes;
    }

    public List<Vertex> getSinkNodes() {
        return sinkNodes;
    }
}
