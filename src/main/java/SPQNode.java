import org.antlr.v4.runtime.misc.Pair;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.*;

public class SPQNode {


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

    List<SPQNode> children = new ArrayList<>();
    List<SPQNode> mergedChildren = new ArrayList<>();
    HashSet<TreeVertex> nodesInCompnent = new LinkedHashSet<>();
    HashSet<TreeVertex> inDegreeStarVertexSet = new LinkedHashSet<>();
    HashSet<TreeVertex> inDegreeSinkVertexSet = new LinkedHashSet<>();
    HashSet<TreeVertex> outDegreeStartVertexSet = new LinkedHashSet<>();
    HashSet<TreeVertex> outDegreeSinkVertexSet = new LinkedHashSet<>();
    List<TreeVertex> startNodes = new ArrayList<>();
    List<TreeVertex> sinkNodes = new ArrayList<>();
    int inDegreeStartVertex = 9999;
    int inDegreeSinkVertex = 9999;
    int outDegreeStartVertex = 9999;
    int outDegreeSinkVertex = 9999;
    double spirality = 999999;
    ArrayList<Integer> anglesStart = new ArrayList<>();
    ArrayList<Integer> anglesSink = new ArrayList<>();
    int alphaul;
    int alphavl;
    int alphaur;
    int alphavr;



    public double getSpirality() {
        return spirality;
    }

    public void setSpirality(double spirality) {
        this.spirality = spirality;
    }

    public HashSet<TreeVertex> getInDegreeStarVertexSet() {
        return inDegreeStarVertexSet;
    }

    public void setInDegreeStarVertexSet(HashSet<TreeVertex> inDegreeStarVertexSet) {
        this.inDegreeStarVertexSet = inDegreeStarVertexSet;
    }

    public HashSet<TreeVertex> getInDegreeSinkVertexSet() {
        return inDegreeSinkVertexSet;
    }

    public void setInDegreeSinkVertexSet(HashSet<TreeVertex> inDegreeSinkVertexSet) {
        this.inDegreeSinkVertexSet = inDegreeSinkVertexSet;
    }

    public HashSet<TreeVertex> getOutDegreeStartVertexSet() {
        return outDegreeStartVertexSet;
    }

    public void setOutDegreeStartVertexSet(HashSet<TreeVertex> outDegreeStartVertexSet) {
        this.outDegreeStartVertexSet = outDegreeStartVertexSet;
    }

    public HashSet<TreeVertex> getOutDegreeSinkVertexSet() {
        return outDegreeSinkVertexSet;
    }

    public void setOutDegreeSinkVertexSet(HashSet<TreeVertex> outDegreeSinkVertexSet) {
        this.outDegreeSinkVertexSet = outDegreeSinkVertexSet;
    }

    public int getInDegreeStartVertex() {
        return inDegreeStartVertex;
    }

    public void setInDegreeStartVertex(int inDegreeStartVertex) {
        this.inDegreeStartVertex = inDegreeStartVertex;
    }

    public int getInDegreeSinkVertex() {
        return inDegreeSinkVertex;
    }

    public void setInDegreeSinkVertex(int inDegreeSinkVertex) {
        this.inDegreeSinkVertex = inDegreeSinkVertex;
    }

    public int getOutDegreeStartVertex() {
        return outDegreeStartVertex;
    }

    public void setOutDegreeStartVertex(int outDegreeStartVertex) {
        this.outDegreeStartVertex = outDegreeStartVertex;
    }

    public int getOutDegreeSinkVertex() {
        return outDegreeSinkVertex;
    }

    public void setOutDegreeSinkVertex(int outDegreeSinkVertex) {
        this.outDegreeSinkVertex = outDegreeSinkVertex;
    }

    public HashSet<TreeVertex> getNodesInCompnent() {
        return nodesInCompnent;
    }

    public void setNodesInCompnent(HashSet<TreeVertex> nodesInCompnent) {
        this.nodesInCompnent = nodesInCompnent;
    }

    int nodes;
    SPQNode parent;
    boolean isroot = false;
    boolean visited;
    String name;
    int counter = 0;
    NodeTypesEnum.NODETYPE nodeType;
    TreeVertex startVertex;
    TreeVertex sinkVertex;
    List<TreeVertex> aPathFromSourceToSink = new ArrayList<>();
    double repIntervalLowerBound = 0;
    double repIntervalUpperBound = 0;

    public double getRepIntervalLowerBound() {
        return repIntervalLowerBound;
    }

    public void setRepIntervalLowerBound(int repIntervalLowerBound) {
        this.repIntervalLowerBound = repIntervalLowerBound;
    }

    public double getRepIntervalUpperBound() {
        return repIntervalUpperBound;
    }

    public void setRepIntervalUpperBound(int repIntervalUpperBound) {
        this.repIntervalUpperBound = repIntervalUpperBound;
    }

    public void calculateRepresentabilityInterval(DirectedMultigraph<TreeVertex, DefaultEdge> graph) {

    }

    public List<TreeVertex> getaPathFromSourceToSink() {
        return aPathFromSourceToSink;
    }

    public void setaPathFromSourceToSink(List<TreeVertex> aPathFromSourceToSink) {
        this.aPathFromSourceToSink = aPathFromSourceToSink;
    }

    public TreeVertex getStartVertex() {
        return startVertex;
    }

    public void setStartVertex(TreeVertex startVertex) {
        this.startVertex = startVertex;
    }

    public TreeVertex getSinkVertex() {
        return sinkVertex;
    }

    public void setSinkVertex(TreeVertex sinkVertex) {
        this.sinkVertex = sinkVertex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
        this.nodes = nodes;
        this.name = name;
    }


    public List<SPQNode> getChildren() {
        return children;
    }

    public void setChildren(List<SPQNode> children) {
        this.children = children;
    }

    public int getNodes() {
        return nodes;
    }

    public void setNodes(int nodes) {
        this.nodes = nodes;
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

    public void setIsroot(boolean isroot) {
        this.isroot = isroot;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }


    public SPQNode generateTree(int size) {
        isroot = true;
        visited = true;
        nodes = size;

        children.add(new SPQNode(nodes / 2));
        children.add(new SPQNode(nodes / 2));

        children.get(0).generateNode(nodes / 2, this);
        children.get(1).generateNode(nodes / 2, this);

        return this;

    }

    public SPQNode generateTree2(int size) {
        isroot = true;
        visited = true;
        nodes = size;

        children.add(new SPQNode(nodes - 1));
        children.add(new SPQNode(1));

        children.get(0).generateNode2(nodes - 1, this);

        return this;

    }


    public void generateNode(int size, SPQNode node) {
        visited = true;
        parent = node;

        if (size > 2) {

            Random random = new Random();
            int size2 = random.nextInt(size / 2) + 2;
            int size3 = size / size2;


            for (int i = 0; i < size2; i++) {

                children.add(new SPQNode(size3));

            }

            for (int i = 0; i < children.size(); i++) {
                children.get(i).generateNode(size3, this);
            }

        }

    }


    public void generateNode2(int size, SPQNode node) {
        visited = true;
        parent = node;

        if (size > 1) {


            int size2 = GraphHelper.getRandomNumberUsingNextInt(1, size);
            int size3 = size - size2;


            children.add(new SPQNode(size2));
            children.add(new SPQNode(size3));


            children.get(0).generateNode2(size2, this);
            children.get(1).generateNode2(size3, this);


        } else {

        }
    }


    public <T extends SPQNode> void addNodeAsChild(T node, T parent) {
        node.setParent(parent);
        parent.getChildren().add(node);

    }


    public <T extends SPQNode> void nodeUmhaengen(T node, T newnode) {
        //Abhängen
        node.getParent().getChildren().set(node.getParent().getChildren().indexOf(node), newnode);
        //neuer Knoten als Parent festlegen
        newnode.setParent(node.getParent());

        addNodeAsChild(node, newnode);

    }


    public void compactTree() {

        this.mergedChildren.addAll(children);

        for (SPQNode spQNode : children
        ) {
            spQNode.compactTree();
        }
        if (this.getParent() != null && this.getNodeType() == this.getParent().getNodeType()) {
            nodeMerge(this, this.getParent());
        }


    }

    public void compactTree2() {


        for (SPQNode spQNode : mergedChildren
        ) {
            spQNode.compactTree2();
        }

        if ((this.getNodeType() == NodeTypesEnum.NODETYPE.S)) {
            mergeQNodes();
        }
        if ((this.getNodeType() == NodeTypesEnum.NODETYPE.P) || this.getNodeType() == NodeTypesEnum.NODETYPE.PROOT) {
            fixQNode();
        }


    }


    public void computeNodesInComponent() {

        if (this.getNodeType() == NodeTypesEnum.NODETYPE.Q && mergedChildren.size() == 0) {
            startVertex.adjecentVertices.add(sinkVertex);
            sinkVertex.adjecentVertices.add(0, startVertex);
        }


        for (SPQNode spQNode : mergedChildren
        ) {
            spQNode.computeNodesInComponent();
        }

        if ((this.getNodeType() == NodeTypesEnum.NODETYPE.Q)) {
            nodesInCompnent.add(startVertex);
            nodesInCompnent.add(sinkVertex);
            nodesInCompnent.add(startVertex);
            nodesInCompnent.add(sinkVertex);
        }


        if (mergedChildren.size() > 0) {
            for (SPQNode nodes :
                    mergedChildren) {
                nodesInCompnent.addAll(nodes.getNodesInCompnent());

                if ((nodes.getNodeType() == NodeTypesEnum.NODETYPE.Q) && nodes.mergedChildren.size() == 0) {
                    if (this.getStartVertex() == nodes.getStartVertex()) {
                        startNodes.add(nodes.getSinkVertex());
                    }
                    if (this.getSinkVertex() == nodes.getSinkVertex()) {
                        sinkNodes.add(nodes.getStartVertex());
                    }
                } else {
                    if (this.getStartVertex() == nodes.getStartVertex()) {
                        startNodes.addAll(nodes.startNodes);
                    }
                    if (this.getSinkVertex() == nodes.getSinkVertex()) {
                        sinkNodes.addAll(nodes.sinkNodes);
                    }

                }
            }
        }






















    }


    private void mergeQNodes() {

        List<SPQNode> qNodes = new ArrayList<SPQNode>();
        List<SPQNode> replacementmergedChildren = new ArrayList<SPQNode>();


        List<SPQNode> mergedChildren = this.getMergedChildren();

        for (int i = 0; i < mergedChildren.size(); i++) {

            if ((mergedChildren.get(i).getNodeType() == NodeTypesEnum.NODETYPE.Q)) {
                qNodes.add(mergedChildren.get(i));
            }
            if (mergedChildren.get(i).getNodeType() != NodeTypesEnum.NODETYPE.Q) {
                if (qNodes.size() > 0) {
                    SPQQNode newQ = new SPQQNode(this.getName() + "Qstar" + counter++);
                    newQ.setMergedChildren(qNodes);
                    newQ.setSinkVertex(newQ.getMergedChildren().get(newQ.getMergedChildren().size() - 1).getSinkVertex());
                    newQ.setStartVertex(newQ.getMergedChildren().get(0).getStartVertex());
                    replacementmergedChildren.add(newQ);

                    qNodes = new ArrayList<SPQNode>();
                }
                replacementmergedChildren.add(mergedChildren.get(i));
            }
        }
        if (qNodes.size() > 0) {
            SPQQNode newQ = new SPQQNode(this.getName() + "Qstar" + counter++);
            newQ.setMergedChildren(qNodes);
            newQ.setSinkVertex(newQ.getMergedChildren().get(newQ.getMergedChildren().size() - 1).getSinkVertex());
            newQ.setStartVertex(newQ.getMergedChildren().get(0).getStartVertex());
            replacementmergedChildren.add(newQ);

            qNodes = new ArrayList<SPQNode>();

            this.setMergedChildren(replacementmergedChildren);
            if (getMergedChildren().size() == 1) {
                this.getParent().getMergedChildren().set(this.getParent().getMergedChildren().indexOf(this), newQ);
            }
        } else if (replacementmergedChildren.size() > 1) {
            this.setMergedChildren(replacementmergedChildren);

        }
    }

    public void fixQNode() {

        for (SPQNode nodes :
                mergedChildren) {

            if (nodes.getNodeType() == NodeTypesEnum.NODETYPE.Q && nodes.mergedChildren.size() == 0) {
                SPQNode newQ = new SPQQNode("Qstar" + nodes.getName());
                newQ.setParent(this);
                newQ.setStartVertex(nodes.getStartVertex());
                newQ.setSinkVertex(nodes.getSinkVertex());
                newQ.mergedChildren.add(nodes);
                this.mergedChildren.set(this.mergedChildren.indexOf(nodes), newQ);


            }
        }
    }


    private void nodeMerge(SPQNode node, SPQNode parent) {

        int pos = parent.mergedChildren.indexOf(node);
        // node.mergedChildren.clear();
        // node.mergedChildren.addAll(node.getChildren());
        parent.mergedChildren.remove(node);

        for (SPQNode spQNode : node.mergedChildren
        ) {
            parent.mergedChildren.add(pos++, spQNode);
            spQNode.setParent(parent);
        }
    }

    public void computeRepresentability(DirectedMultigraph<TreeVertex, DefaultEdge> graph) {

        for (SPQNode root : getMergedChildren()
        ) {
            root.computeRepresentability(graph);
        }
        if (this.mergedChildren.size() != 0) {
            calculateRepresentabilityInterval(graph);
        }

    }

    public int computeHowManyCommonNodesThisAndSet(HashSet<TreeVertex> tempHashSet) {
        int counter = 0;
        for (TreeVertex node : tempHashSet
        ) {
            if (nodesInCompnent.contains(node)) {
                counter++;
            }
        }
        return counter;
    }


    public void computeSpirality() {


        if (this.getNodeType() == NodeTypesEnum.NODETYPE.S) {

            double delta = 0;
            for (SPQNode node :
                    mergedChildren) {
                node.setSpirality(node.getRepIntervalUpperBound());
                delta += (node.getRepIntervalUpperBound());
            }
            delta -= this.spirality;

            if (delta != 0) {
                for (SPQNode node :
                        mergedChildren) {
                    double temp = Math.min(delta, node.getRepIntervalUpperBound() - node.getRepIntervalLowerBound());
                    node.setSpirality(node.getSpirality() - temp);
                    delta -= temp;
                    if (delta == 0) {
                        break;
                    }
                }
            }


        } else if (this.getNodeType() == NodeTypesEnum.NODETYPE.P && this.getMergedChildren().size() == 3) {
            this.getMergedChildren().get(0).setSpirality(this.spirality + 2);
            this.getMergedChildren().get(1).setSpirality(this.spirality);
            this.getMergedChildren().get(2).setSpirality(this.spirality - 2);
            anglesStart.add(1);
            anglesStart.add(1);
            anglesStart.add(1);
            anglesStart.add(1);
            anglesSink.add(1);
            anglesSink.add(1);
            anglesSink.add(1);
            anglesSink.add(1);


        } else if (this.getNodeType() == NodeTypesEnum.NODETYPE.P && this.getMergedChildren().size() == 2) {
            int alphaul = 9999;
            int alphaur = 9999;

            int alphavl = 9999;
            int alphavr = 9999;

            double kul = (this.getOutDegreeStartVertex() == 1 && this.getMergedChildren().get(0).getInDegreeStartVertex() == 1) ? 1 : 0.5;
            double kur = (this.getOutDegreeStartVertex() == 1 && this.getMergedChildren().get(1).getInDegreeStartVertex() == 1) ? 1 : 0.5;


            double kvl = (this.getOutDegreeSinkVertex() == 1 && this.getMergedChildren().get(0).getInDegreeSinkVertex() == 1) ? 1 : 0.5;
            double kvr = (this.getOutDegreeSinkVertex() == 1 && this.getMergedChildren().get(1).getInDegreeSinkVertex() == 1) ? 1 : 0.5;

            int[] arrU;
            if ((this.getInDegreeStartVertex() + this.getOutDegreeStartVertex()) == 4) {
                alphaul = 1;
                alphaur = 1;
                arrU = new int[]{1};


            } else {
                arrU = new int[]{1, 0};
            }
            int[] arrV;
            if ((this.getInDegreeSinkVertex() + this.getOutDegreeSinkVertex()) == 4) {
                alphavl = 1;
                alphavr = 1;
                arrV = new int[]{1};


            } else {
                arrV = new int[]{1, 0};
            }

            //Spirality des Linken Kindes Festlegen
            outerloop:
            for (int i = 0; i < arrU.length; i++) {
                for (int j = 0; j < arrV.length; j++) {
                    alphaul = arrU[i];
                    alphavl = arrV[j];
                    double temp = this.spirality + kul * arrU[i] + kvl * arrV[j];
                    if (this.getMergedChildren().get(0).getRepIntervalLowerBound() <= temp && temp <= this.getMergedChildren().get(0).getRepIntervalUpperBound()) {
                        this.getMergedChildren().get(0).setSpirality(this.spirality + kul * alphaul + kvl * alphavl);
                        break outerloop;
                    }
                }
            }

            //Spirality des rechten Kindes Festlegen
            outerloop2:
            for (int i = 0; i < arrU.length; i++) {
                for (int j = 0; j < arrV.length; j++) {
                    alphaur = arrU[i];
                    alphavr = arrV[j];
                    double temp = this.spirality - kur * arrU[i] - kvr * arrV[j];
                    if (this.getMergedChildren().get(1).getRepIntervalLowerBound() <= temp && temp <= this.getMergedChildren().get(1).getRepIntervalUpperBound()) {
                        this.getMergedChildren().get(1).setSpirality(this.spirality - kur * alphaur - kvr * alphavr);
                        break outerloop2;
                    }
                }
            }

            int tempstart = alphaul + alphavl;
            if ((this.getInDegreeStartVertex() + this.getOutDegreeStartVertex()) == 4) {
                anglesStart.add(1);
                tempstart++;
            } else {
                anglesStart.add(999999); // dh nur eine Kante außerhalb
            }
            anglesStart.add(alphaul);
            anglesStart.add(alphaur);
            anglesStart.add((tempstart == 2) ? 0 : 1);

            int tempsink = alphavl + alphavr;
            if ((this.getInDegreeSinkVertex() + this.getOutDegreeSinkVertex()) == 4) {
                anglesSink.add(1);
                tempsink++;
            } else {
            //    anglesSink.add(9999); // dh nur eine Kante außerhalb
            }
            anglesSink.add(alphavl);
            anglesSink.add(alphavr);
            anglesSink.add((tempsink == 2) ? 0 : 1);
            System.out.println("Test");

            this.alphavl = alphavl;
            this.alphavr = alphavr;
            this.alphaul = alphaul;
            this.alphaur = alphaur;



          //  this.mergedChildren.get(0).startNodes.get(0).setRightAngle(alphaul);
           // this.mergedChildren.get(0).startNodes.get(1).setRightAngle(alphaur);



        }


        for (SPQNode node :
                mergedChildren) {
            node.computeSpirality();
        }


    }
    public void computeOrthogonalRepresentation(HashMap<Pair<TreeVertex, TreeVertex>, Integer> hashMap) {





        System.out.println("Test");
        }


}
