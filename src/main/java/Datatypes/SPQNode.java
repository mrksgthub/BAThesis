package Datatypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class SPQNode {


    private List<SPQNode> children = new ArrayList<>();
    List<SPQNode> mergedChildren = new ArrayList<>();

    List<Vertex> startNodes = new ArrayList<>();
    List<Vertex> sinkNodes = new ArrayList<>();
    double spirality = 999999;
    private SPQNode parent;
    boolean isroot = false;
    private String name;
    int counter = 0;
    NodeTypesEnum.NODETYPE nodeType;
    Vertex startVertex;
    Vertex sinkVertex;
    double repIntervalLowerBound = 999;
    double repIntervalUpperBound = -990;
     boolean isRoot = false;
    static int id = 0;

    public SPQNode(int nodes) {
    }

    public SPQNode(int nodes, String name) {
        this.name = name;
    }

    public SPQNode(String name) {
        this.name = name;
    }

    public List<SPQNode> getMergedChildren() {
        return mergedChildren;
    }

    void setMergedChildren(List<SPQNode> mergedChildren) {
        this.mergedChildren = mergedChildren;
    }

    public NodeTypesEnum.NODETYPE getNodeType() {
        return nodeType;
    }

    public double getSpirality() {
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

    public List<SPQNode> getChildren() {
        return children;
    }


    public SPQNode getParent() {
        return parent;
    }

    public void setParent(SPQNode parent) {
        this.parent = parent;
    }

    public boolean isIsroot() {
        return isroot;
    }


    public boolean isRoot() {
        return this.isRoot;
    }

    public void setRoot() {
        isRoot = true;

    }




    public void generateQstarChildren() {

    }




    public void addToSourceAndSinkLists(SPQNode nodes) {

            if (this.getStartVertex() == nodes.getStartVertex()) {
                startNodes.addAll(nodes.startNodes);
            }
            if (this.getSinkVertex() == nodes.getSinkVertex()) {
                sinkNodes.addAll(nodes.sinkNodes);
            }
    }

    public void addToAdjecencyListsSinkAndSource() {
        if (this.getNodeType() == NodeTypesEnum.NODETYPE.Q && mergedChildren.size() == 0) {
            startVertex.adjecentVertices.add(sinkVertex);
            sinkVertex.adjecentVertices.add(0, startVertex);
        }
    }


    void mergeNodeWithParent(SPQNode node, SPQNode parent) {

        int pos = parent.mergedChildren.indexOf(node);
        parent.mergedChildren.remove(node);

        for (SPQNode spQNode : node.mergedChildren
        ) {
            parent.mergedChildren.add(pos++, spQNode);
            spQNode.setParent(parent);
        }
    }



    public void computeOrthogonalRepresentation(HashMap<TupleEdge<Vertex, Vertex>, Integer> hashMap) {
        // System.out.println("Test");
    }




    public void setSpiralityOfChildren() {
    }
}
