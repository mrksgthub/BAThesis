package Datatypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SPQNode {


    List<SPQNode> children = new ArrayList<>();
    List<SPQNode> mergedChildren = new ArrayList<>();

    List<Vertex> startNodes = new ArrayList<>();
    List<Vertex> sinkNodes = new ArrayList<>();

    double spirality = 999999;
    int alphaul;
    int alphavl;
    int alphaur;
    int alphavr;
    int nodes;
    SPQNode parent;
    boolean isroot = false;
    String name;
    int counter = 0;
    NodeTypesEnum.NODETYPE nodeType;
    Vertex startVertex;
    Vertex sinkVertex;
    double repIntervalLowerBound = 999;
    double repIntervalUpperBound = -990;
    private boolean isRoot = false;
    static int id = 0;

    public SPQNode() {

    }

    public SPQNode(int nodes) {
        this.nodes = nodes;
    }

    public SPQNode(int nodes, String name) {
        this.nodes = nodes;
        this.name = name;
    }

    public SPQNode(String name) {
        this.name = name;
    }

    public List<SPQNode> getMergedChildren() {
        return mergedChildren;
    }

    public void setMergedChildren(List<SPQNode> mergedChildren) {
        this.mergedChildren = mergedChildren;
    }

    public NodeTypesEnum.NODETYPE getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeTypesEnum.NODETYPE nodeType) {
        this.nodeType = nodeType;
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

    public void setName(String name) {
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

 /*   public void compactTree() {

        this.mergedChildren.addAll(children);

        for (SPQNode spQNode : children
        ) {
            spQNode.compactTree();
        }
        if (this.getParent() != null && this.getNodeType() == this.getParent().getNodeType() && !this.getParent().isRoot()) {
            mergeNodeWithParent(this, this.getParent());
        }


    }

    public void generateQstarNodes() {


        for (SPQNode spQNode : mergedChildren
        ) {
            spQNode.generateQstarNodes();
        }

        if ((this.getNodeType() == NodeTypesEnum.NODETYPE.S)) {
            mergeQNodes();
        }
        if ((this.getNodeType() == NodeTypesEnum.NODETYPE.P) || this.getNodeType() == NodeTypesEnum.NODETYPE.PROOT) {
            fixQNode();
        }

    }*/


    public void generateQstarChildren() {

    }









/*    public void computeAdjecentVertices() {

        addToAdjecencyListsSinkAndSource();


        for (SPQNode spQNode : mergedChildren
        ) {
            spQNode.computeAdjecentVertices();
        }

        if (mergedChildren.size() > 0) {
            for (SPQNode nodes :
                    mergedChildren) {

                addToSourceAndSinkLists(nodes);
            }
        }

    }*/

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


    private void mergeQNodes() {

        List<SPQNode> qNodes = new ArrayList<>();
        List<SPQNode> replacementmergedChildren = new ArrayList<>();


        List<SPQNode> mergedChildren = this.getMergedChildren();

        for (int i = 0; i < mergedChildren.size(); i++) {

            if ((mergedChildren.get(i).getNodeType() == NodeTypesEnum.NODETYPE.Q)) {
                qNodes.add(mergedChildren.get(i));
            }
            if (mergedChildren.get(i).getNodeType() != NodeTypesEnum.NODETYPE.Q) {
                if (qNodes.size() > 0) {
                    SPQQNode newQ = new SPQQNode("Qstar" + counter++ + this.getName());
                    newQ.setMergedChildren(qNodes);
                    newQ.setSinkVertex(newQ.getMergedChildren().get(newQ.getMergedChildren().size() - 1).getSinkVertex());
                    newQ.setStartVertex(newQ.getMergedChildren().get(0).getStartVertex());
                    replacementmergedChildren.add(newQ);

                    qNodes = new ArrayList<>();
                }
                replacementmergedChildren.add(mergedChildren.get(i));
            }
        }
        if (qNodes.size() > 0) {
            SPQQNode newQ = new SPQQNode("Qstar" + counter++ + this.getName());
            newQ.setMergedChildren(qNodes);
            newQ.setSinkVertex(newQ.getMergedChildren().get(newQ.getMergedChildren().size() - 1).getSinkVertex());
            newQ.setStartVertex(newQ.getMergedChildren().get(0).getStartVertex());
            replacementmergedChildren.add(newQ);

            this.setMergedChildren(replacementmergedChildren);
            if (getMergedChildren().size() == 1) {
                this.getParent().getMergedChildren().set(this.getParent().getMergedChildren().indexOf(this), newQ);
            }
        } else if (replacementmergedChildren.size() > 1) {
            this.setMergedChildren(replacementmergedChildren);

        }
    }

    public void fixQNode() {

        for (SPQNode node :
                mergedChildren) {

            if (node.getNodeType() == NodeTypesEnum.NODETYPE.Q && node.mergedChildren.size() == 0) {
                SPQNode newQ = new SPQQNode("Qstar" + node.getName());
                newQ.setParent(this);
                newQ.setStartVertex(node.getStartVertex());
                newQ.setSinkVertex(node.getSinkVertex());
                newQ.mergedChildren.add(node);
                this.mergedChildren.set(this.mergedChildren.indexOf(node), newQ);
            }
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

/*    public boolean computeRepresentability(Boolean check) {

        boolean temp;
        for (SPQNode root : getMergedChildren()
        ) {
            temp = root.computeRepresentability(check);
            if (!temp) {
                check = temp;
            }
        }

        if (this.mergedChildren.size() != 0 && !this.isRoot) {
            if (!calculateRepresentabilityInterval()) {
                check = false;
            }
        }
        return check;
    }*/


    public void computeOrthogonalRepresentation(HashMap<TupleEdge<Vertex, Vertex>, Integer> hashMap) {
        // System.out.println("Test");
    }


/*    public void determineParents(SPQNode node, HashMap<String, ArrayList<SPQNode>> map) {


        for (SPQNode root : node.mergedChildren
        ) {
            map.put(root.getName(), new ArrayList<>());
            map.get(root.getName()).add(node);
            determineParents(root, map);
        }
    }*/


    public void setSpiralityOfChildren() {
    }
}
