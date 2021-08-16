import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.util.SupplierUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GraphgenSplitGraph {


    SPQPNode root;

    DirectedMultigraph<TreeVertex, DefaultEdge> multigraph = new DirectedMultigraph<>(TreeVertex.getvSupplier, SupplierUtil.createDefaultEdgeSupplier(), false);
    int operations;
    List<DefaultEdge> edges = new ArrayList<>();
    HashMap<DefaultEdge, SPQNode> edgeSPQNodeHashMap = new HashMap<>();
    int counter = 0;
    private double chanceOfP = 50;

    public GraphgenSplitGraph(int operations, double chanceOfP) {

        this(operations);
        this.chanceOfP = chanceOfP;
    }


    public GraphgenSplitGraph(int operations) {
        this.operations = operations;
        root = new SPQPNode("Proot");
        root.setRoot();
        root.setNodeType(NodeTypesEnum.NODETYPE.P);
        TreeVertex vertex = new TreeVertex("vsource");
        TreeVertex vertex2 = new TreeVertex("vsink");
        multigraph.addVertex(vertex);
        multigraph.addVertex(vertex2);
        multigraph.addEdge(vertex, vertex2);
        multigraph.addEdge(vertex, vertex2);


        SPQQNode qLeft = new SPQQNode("Q" + ++counter);
        root.getChildren().add(qLeft);
        qLeft.setParent(root);
        qLeft.setStartVertex(vertex);
        qLeft.setSinkVertex(vertex2);

        SPQNode qRight = new SPQQNode("Q" + ++counter);
        qRight.setStartVertex(vertex);
        qRight.setSinkVertex(vertex2);
        root.getChildren().add(qRight);
        qRight.setParent(root);

        edges.add(multigraph.getEdge(vertex, vertex2));
        // First two entries in the Hashmap, which will be used for the embedding
        edgeSPQNodeHashMap.put(multigraph.getEdge(vertex, vertex2), qRight);
        edgeSPQNodeHashMap.put(multigraph.getEdge(vertex, vertex2), qLeft);


    }

    public SPQPNode getRoot() {
        return root;
    }

    public void setRoot(SPQPNode root) {
        this.root = root;
    }

    public void generateGraph() {


        DefaultEdge edge = edges.get(GraphHelper.getRandomNumberUsingNextInt(0, edges.size()));

        if (GraphHelper.getRandomNumberUsingNextInt(0, 99) < chanceOfP) {
            newInitialPNode(edge);
        } else {
            randomnewSNode(edge);
        }


        // GraphHelper.printToDOT(GraphHelper.treeToDOT(root));

        for (int i = 0; i < 1*operations; i++) {
            edge = edges.get(GraphHelper.getRandomNumberUsingNextInt(0, edges.size()));

            int degreeOfedgeSource = multigraph.outDegreeOf(multigraph.getEdgeSource(edge)) + multigraph.inDegreeOf(multigraph.getEdgeSource(edge));
            int degreeOfedgeSink = multigraph.outDegreeOf(multigraph.getEdgeTarget(edge)) + multigraph.inDegreeOf(multigraph.getEdgeTarget(edge));
            if (GraphHelper.getRandomNumberUsingNextInt(0, 99) < chanceOfP && degreeOfedgeSource < 4 && degreeOfedgeSink < 4) {
                randomnewPNode(edge);
            } else {
                randomnewSNode(edge);
            }
        }

        int counter = 0;
        for (int i = 0; i < 0.0*operations; i++) {

            edge = edges.get(GraphHelper.getRandomNumberUsingNextInt(0, edges.size()));

            int degreeOfedgeSource = multigraph.outDegreeOf(multigraph.getEdgeSource(edge)) + multigraph.inDegreeOf(multigraph.getEdgeSource(edge));
            int degreeOfedgeSink = multigraph.outDegreeOf(multigraph.getEdgeTarget(edge)) + multigraph.inDegreeOf(multigraph.getEdgeTarget(edge));
            if (degreeOfedgeSource < 4 && degreeOfedgeSink < 4  && (degreeOfedgeSource > 2 || degreeOfedgeSink > 2) ) {
                randomnewSNode(edge);
            } else {
                i++;
                counter++;
            }

        }







        // Start- und Endknoten in die Q-Nodes einfügen
        for (DefaultEdge edge1 :
                edgeSPQNodeHashMap.keySet()) {
            //      edgeSPQNodeHashMap.get((edge1)).setName(edgeSPQNodeHashMap.get(edge1).getName() + edge1.toString().replaceAll("\\s", "").replaceAll(":", "_").replaceAll("\\(", " ").replaceAll("\\)", "").trim());
            //    edgeSPQNodeHashMap.get((edge1)).setName(edgeSPQNodeHashMap.get(edge1).getName()+edge1.toString());

            edgeSPQNodeHashMap.get(edge1).setStartVertex(multigraph.getEdgeSource(edge1));
            edgeSPQNodeHashMap.get(edge1).setSinkVertex(multigraph.getEdgeTarget(edge1));
        }


      //  System.out.println("test");
        //    GraphHelper.printTODOTSPQNode(GraphHelper.treeToDOT(root, 1));

        counter = counter + 1 -1;
    }



    public void generateGraph2() {


        DefaultEdge edge = edges.get(GraphHelper.getRandomNumberUsingNextInt(0, edges.size()));

        if (GraphHelper.getRandomNumberUsingNextInt(0, 99) < chanceOfP) {
            newInitialPNode(edge);
        } else {
            randomnewSNode(edge);
        }


        // GraphHelper.printToDOT(GraphHelper.treeToDOT(root));

        for (int i = 0; i < operations; i++) {
            edge = edges.get(GraphHelper.getRandomNumberUsingNextInt(0, edges.size()));

            int degreeOfedgeSource = multigraph.outDegreeOf(multigraph.getEdgeSource(edge)) + multigraph.inDegreeOf(multigraph.getEdgeSource(edge));
            int degreeOfedgeSink = multigraph.outDegreeOf(multigraph.getEdgeTarget(edge)) + multigraph.inDegreeOf(multigraph.getEdgeTarget(edge));
            if (GraphHelper.getRandomNumberUsingNextInt(0, 99) < chanceOfP && degreeOfedgeSource < 4 && degreeOfedgeSink < 4) {
                randomnewPNode(edge);
            } else {
                randomnewSNode(edge);
            }
        }



        // Start- und Endknoten in die Q-Nodes einfügen
        for (DefaultEdge edge1 :
                edgeSPQNodeHashMap.keySet()) {
            //      edgeSPQNodeHashMap.get((edge1)).setName(edgeSPQNodeHashMap.get(edge1).getName() + edge1.toString().replaceAll("\\s", "").replaceAll(":", "_").replaceAll("\\(", " ").replaceAll("\\)", "").trim());
            //    edgeSPQNodeHashMap.get((edge1)).setName(edgeSPQNodeHashMap.get(edge1).getName()+edge1.toString());

            edgeSPQNodeHashMap.get(edge1).setStartVertex(multigraph.getEdgeSource(edge1));
            edgeSPQNodeHashMap.get(edge1).setSinkVertex(multigraph.getEdgeTarget(edge1));
        }


        //  System.out.println("test");
        //    GraphHelper.printTODOTSPQNode(GraphHelper.treeToDOT(root, 1));
    }


    public void generateGraph3() {


        DefaultEdge edge = edges.get(GraphHelper.getRandomNumberUsingNextInt(0, edges.size()));

        if (GraphHelper.getRandomNumberUsingNextInt(0, 99) < chanceOfP) {
            newInitialPNode(edge);
        } else {
            randomnewSNode(edge);
        }


        // GraphHelper.printToDOT(GraphHelper.treeToDOT(root));

        for (int i = 0; i < 1*operations; i++) {
            edge = edges.get(GraphHelper.getRandomNumberUsingNextInt(0, edges.size()));

            int degreeOfedgeSource = multigraph.outDegreeOf(multigraph.getEdgeSource(edge)) + multigraph.inDegreeOf(multigraph.getEdgeSource(edge));
            int degreeOfedgeSink = multigraph.outDegreeOf(multigraph.getEdgeTarget(edge)) + multigraph.inDegreeOf(multigraph.getEdgeTarget(edge));
            if (GraphHelper.getRandomNumberUsingNextInt(0, 99) < chanceOfP && degreeOfedgeSource < 3 && degreeOfedgeSink < 3) {
                randomnewPNode(edge);
            } else {
                randomnewSNode(edge);
            }
        }

        for (int i = 0; i < 2*operations; i++) {
            edge = edges.get(GraphHelper.getRandomNumberUsingNextInt(0, edges.size()));

            int degreeOfedgeSource = multigraph.outDegreeOf(multigraph.getEdgeSource(edge)) + multigraph.inDegreeOf(multigraph.getEdgeSource(edge));
            int degreeOfedgeSink = multigraph.outDegreeOf(multigraph.getEdgeTarget(edge)) + multigraph.inDegreeOf(multigraph.getEdgeTarget(edge));
            if (GraphHelper.getRandomNumberUsingNextInt(0, 99) < 0 && degreeOfedgeSource < 4 && degreeOfedgeSink < 4) {
                randomnewPNode(edge);
            } else {
                randomnewSNode(edge);
            }
        }







        // Start- und Endknoten in die Q-Nodes einfügen
        for (DefaultEdge edge1 :
                edgeSPQNodeHashMap.keySet()) {
            //      edgeSPQNodeHashMap.get((edge1)).setName(edgeSPQNodeHashMap.get(edge1).getName() + edge1.toString().replaceAll("\\s", "").replaceAll(":", "_").replaceAll("\\(", " ").replaceAll("\\)", "").trim());
            //    edgeSPQNodeHashMap.get((edge1)).setName(edgeSPQNodeHashMap.get(edge1).getName()+edge1.toString());

            edgeSPQNodeHashMap.get(edge1).setStartVertex(multigraph.getEdgeSource(edge1));
            edgeSPQNodeHashMap.get(edge1).setSinkVertex(multigraph.getEdgeTarget(edge1));
        }


        //  System.out.println("test");
        //    GraphHelper.printTODOTSPQNode(GraphHelper.treeToDOT(root, 1));
    }



























    private void newInitialPNode(DefaultEdge edge) {

        DefaultEdge edge1 = multigraph.addEdge(multigraph.getEdgeSource(edge), multigraph.getEdgeTarget(edge));
        edges.add(edge1);

        SPQNode oldQNode = edgeSPQNodeHashMap.get(edge);
        SPQNode newPnode = new SPQPNode("P" + ++counter);

        SPQNode newQnode1 = new SPQQNode("Q" + ++counter);
        edgeSPQNodeHashMap.put(edge1, newQnode1);

        nodeUmhaengen(oldQNode, newPnode);
        addNodeAsRightChild(newQnode1, newPnode);


        randomnewSNode(edge);

        randomnewSNode(edge1);


    }

    private DefaultEdge[] randomnewSNode(DefaultEdge edge) {

        TreeVertex vertex = multigraph.addVertex();
        DefaultEdge edge1 = multigraph.addEdge(vertex, multigraph.getEdgeTarget(edge));
        DefaultEdge edge2 = multigraph.addEdge(multigraph.getEdgeSource(edge), vertex);
        multigraph.removeEdge(edge);
        edges.remove(edge);
        edges.add(edge1);
        edges.add(edge2);
        SPQNode oldQNode = edgeSPQNodeHashMap.get(edge);
        SPQNode newSnode = new SPQSNode("S" + ++counter);
        SPQNode newQnode = new SPQQNode("Q" + ++counter);
        edgeSPQNodeHashMap.remove(edge);
        edgeSPQNodeHashMap.put(edge2, oldQNode);
        edgeSPQNodeHashMap.put(edge1, newQnode);
        nodeUmhaengen(oldQNode, newSnode);
        addNodeAsRightChild(newQnode, newSnode);

        DefaultEdge[] arr = {edge1, edge2};
        return arr;
        }

    private void randomnewPNode2(DefaultEdge edge) {

        //TODO reihenfolge Randomizen? dh zufällig welches rechts, oder links eingefügt wird

        TreeVertex vertex = multigraph.addVertex();
        DefaultEdge edge1 = multigraph.addEdge(multigraph.getEdgeSource(edge), vertex);
        DefaultEdge edge2 = multigraph.addEdge(vertex, multigraph.getEdgeTarget(edge));
        edges.add(edge1);
        edges.add(edge2);

        SPQNode oldQNode = edgeSPQNodeHashMap.get(edge);
        SPQNode newPnode = new SPQPNode("P" + ++counter);


        SPQNode newQnode1 = new SPQQNode("Q" + ++counter);
        edgeSPQNodeHashMap.put(edge1, newQnode1);
        SPQNode newQnode2 = new SPQQNode("Q" + ++counter);
        edgeSPQNodeHashMap.put(edge2, newQnode2);

        SPQNode newSnode = new SPQSNode(("S" + ++counter));
        newQnode1.setParent(newSnode);
        newQnode2.setParent(newSnode);

        newSnode.getChildren().add(newQnode1);
        newSnode.getChildren().add(newQnode2);

        nodeUmhaengen(oldQNode, newPnode);
        /*
        if (GraphHelper.getRandomNumberUsingNextInt(0, 1) == 0) {
            addNodeAsLeftChild(newSnode, newPnode);
        } else {
            addNodeAsRightChild(newSnode, newPnode);
        }
*/
        addNodeAsRightChild(newSnode, newPnode);

        //möglicherweise addNodeAsLeftChild hinzuf+gen und dann random wäheln welche


    }

    //TODO Original randomnewPNode
    private void randomnewPNode3(DefaultEdge edge) {

        //TODO reihenfolge Randomizen? dh zufällig welches rechts, oder links eingefügt wird

        TreeVertex vertex = multigraph.addVertex();
        DefaultEdge edge1 = multigraph.addEdge(multigraph.getEdgeSource(edge), vertex);
        DefaultEdge edge2 = multigraph.addEdge(vertex, multigraph.getEdgeTarget(edge));
        edges.add(edge1);
        edges.add(edge2);

        SPQNode oldQNode = edgeSPQNodeHashMap.get(edge);
        SPQNode newPnode = new SPQPNode("P" + ++counter);


        SPQNode newQnode1 = new SPQQNode("Q" + ++counter);
        edgeSPQNodeHashMap.put(edge1, newQnode1);
        SPQNode newQnode2 = new SPQQNode("Q" + ++counter);
        edgeSPQNodeHashMap.put(edge2, newQnode2);

        SPQNode newSnode = new SPQSNode(("S" + ++counter));
        newQnode1.setParent(newSnode);
        newQnode2.setParent(newSnode);


        // TODO hier noch eine if-STruktur, um der Linken Seite einzufügen?

        newSnode.getChildren().add(newQnode1);
        newSnode.getChildren().add(newQnode2);

        nodeUmhaengen(oldQNode, newPnode);

        addNodeAsRightChild(newSnode, newPnode);

        // TODO Sinnvoll?

        if (GraphHelper.getRandomNumberUsingNextInt(0, 99) > 50) {

            randomnewSNode(edge1);
        } else {
            randomnewSNode(edge2);
        }

    }



    private void randomnewPNode(DefaultEdge edge) {

        //TODO reihenfolge Randomizen? dh zufällig welches rechts, oder links eingefügt wird

        DefaultEdge edge1 = multigraph.addEdge(multigraph.getEdgeSource(edge), multigraph.getEdgeTarget(edge));
        edges.add(edge1);

        SPQNode oldQNode = edgeSPQNodeHashMap.get(edge);
        SPQNode newPnode = new SPQPNode("P" + ++counter);

        SPQNode newQnode1 = new SPQQNode("Q" + ++counter);
        edgeSPQNodeHashMap.put(edge1, newQnode1);

        nodeUmhaengen(oldQNode, newPnode);
        addNodeAsRightChild(newQnode1, newPnode);


        DefaultEdge[] arr1 = new DefaultEdge[2];



        // TODO Sinnvoll?

        if (GraphHelper.getRandomNumberUsingNextInt(0, 99) > 0) {

            arr1 = randomnewSNode(edge);
        } else {
            arr1 = randomnewSNode(edge1);
        }


        for (int i = 0; i < 5; i++) {


        if (GraphHelper.getRandomNumberUsingNextInt(0, 99) > 50) {

            DefaultEdge tempEdge1 = arr1[GraphHelper.getRandomNumberUsingNextInt(0, 1)];
            arr1 = randomnewSNode(tempEdge1);

        } else {
            DefaultEdge tempEdge2 = arr1[GraphHelper.getRandomNumberUsingNextInt(0, 1)];
            arr1 =randomnewSNode(tempEdge2);

        }


        }







    }





























    public <T extends SPQNode> void addNodeAsRightChild(T node, T parent) {
        node.setParent(parent);
        parent.getChildren().add(node);

    }

    public <T extends SPQNode> void addNodeAsLeftChild(T node, T parent) {
        node.setParent(parent);
        parent.getChildren().add(0, node);
    }


    public <T extends SPQNode> void nodeUmhaengen(T node, T newnode) {
        //Abhängen
        node.getParent().getChildren().set(node.getParent().getChildren().indexOf(node), newnode);
        //neuer Knoten als Parent festlegen

        newnode.setParent(node.getParent());
        addNodeAsRightChild(node, newnode);

    }


    public SPQPNode getNode() {
        return root;
    }

    public void setNode(SPQPNode node) {
        this.root = node;
    }

    public DirectedMultigraph<TreeVertex, DefaultEdge> getMultigraph() {
        return multigraph;
    }

    public void setMultigraph(DirectedMultigraph<TreeVertex, DefaultEdge> multigraph) {
        this.multigraph = multigraph;
    }

    public int getOperations() {
        return operations;
    }

    public void setOperations(int operations) {
        this.operations = operations;
    }
}
