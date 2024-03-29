package Datastructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Abstrakte Basisklasse für die verschiedenen Knoten des SPQ- bzw. SPQ*-Baums.
 *
 * Implementiert:
 *
 * Den Baum
 * Methoden zur Transformation von SPQ- zu SPQ*-Baum
 * Methoden, um die rektilineare Plnarität zu testen und die Winkel festzulegen (nach Didimo. et. al. 2020)
 *
 *
 */
public abstract class SPQNode {


    static int id = 0;
    List<SPQNode> spqChildren = new ArrayList<>();
    List<Vertex> sourceNodes = new ArrayList<>(); // innere Knoten an der Quelle
    List<Vertex> sinkNodes = new ArrayList<>();   // innere Knoten an der Senke
    double spirality = 999999;
    int counter = 0;
    NodeTypesEnum.NODETYPE nodeType;
    Vertex sourceVertex;
    Vertex sinkVertex;
    double repIntervalLowerBound = 999;
    double repIntervalUpperBound = -990;
    boolean isRoot = false;
    boolean isQ =false;

    public boolean isNotQNode() {
        return isQ;
    }

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

    public Vertex getSourceVertex() {
        return sourceVertex;
    }

    public void setSourceVertex(Vertex sourceVertex) {
        this.sourceVertex = sourceVertex;
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

    /**
     * Überprüft die Kindsknoten und fügt diese, falls Q-Knoten vorkommen, zu korrekten Q*-Knoten zusammen.
     *
     *
     */
    public void generateQstarChildren() {

    }

    /**
     * Bestimmt die Quelle und Senke des Knotens, basierend auf seinen Kindsknoten
     *
     * @param nodes - Ein Kindsknoten des Baums
     */
    public void addToSourceAndSinkLists(SPQNode nodes) {

        if (this.getSourceVertex() == nodes.getSourceVertex()) {
            sourceNodes.addAll(nodes.sourceNodes);
        }
        if (this.getSinkVertex() == nodes.getSinkVertex()) {
            sinkNodes.addAll(nodes.sinkNodes);
        }
    }

    /**
     * Wird nur in den Blättern des Baums durchgeführt. Fügt die Senke (Quelle) in die Adjazenzliste der Quelle (Senke)
     * ein.
     */
    public void addToAdjacencyListsSinkAndSource() {
        if (this.getNodeType() == NodeTypesEnum.NODETYPE.Q && spqChildren.size() == 0) {
            sourceVertex.adjacentVertices.add(sinkVertex);
            sinkVertex.adjacentVertices.add(0, sourceVertex);
        }
    }

    /**
     * @param node   Kindsknoten
     * @param parent Der Elternknoten, mit dem node verschmelzen soll.
     */
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

    /**
     * Legt die Spiralität der Kinder nach Didimo et al. 2020 fest.
     */
    public void setSpiralityOfChildren() {
    }

    public List<Vertex> getSourceNodes() {
        return sourceNodes;
    }

    public List<Vertex> getSinkNodes() {
        return sinkNodes;
    }

    public static class NodeTypesEnum {

        public enum NODETYPE {P, Q, S}

    }
}
