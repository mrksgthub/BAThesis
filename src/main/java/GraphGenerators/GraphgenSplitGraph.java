package GraphGenerators;

import Datastructures.*;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.util.SupplierUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

class GraphgenSplitGraph {


    private final DirectedMultigraph<Vertex, DefaultEdge> multigraph = new DirectedMultigraph<>(Vertex.getvSupplier, SupplierUtil.createDefaultEdgeSupplier(), false);
    private final int numberOfNodes;
    private final List<DefaultEdge> edges = new ArrayList<>();
    private final HashMap<DefaultEdge, SPQNode> edgeSPQNodeHashMap = new HashMap<>();
    private SPQPNode root;
    private int counter = 0;
    private double chanceOfP = 50;
    private int maxDeg = 4;
    private int einfachheit = 1;
    private int mode;


    private GraphgenSplitGraph(int numberOfNodes) {
        // Erzeugen des "Basisgraphen" an sich und auch den BasisSPQ-Baum
        this.numberOfNodes = numberOfNodes;
        root = new SPQPNode("Proot");
        root.setToRoot();

        Vertex vertex = new Vertex("vsource");
        Vertex vertex2 = new Vertex("vsink");
        multigraph.addVertex(vertex);
        multigraph.addVertex(vertex2);
        multigraph.addEdge(vertex, vertex2);
        multigraph.addEdge(vertex, vertex2);


        SPQQNode qLeft = new SPQQNode("Q" + ++counter, vertex, vertex2);
        root.getSpqChildren().add(qLeft);
        qLeft.setParent(root);
        qLeft.setStartVertex(vertex);
        qLeft.setSinkVertex(vertex2);

        SPQNode qRight = new SPQQNode("Q" + ++counter, vertex, vertex2);
        qRight.setStartVertex(vertex);
        qRight.setSinkVertex(vertex2);
        root.getSpqChildren().add(qRight);
        qRight.setParent(root);

        edges.add(multigraph.getEdge(vertex, vertex2));
        // First two entries in the Hashmap, which will be used for the embedding
        edgeSPQNodeHashMap.put(multigraph.getEdge(vertex, vertex2), qRight);
        edgeSPQNodeHashMap.put(multigraph.getEdge(vertex, vertex2), qLeft);


    }

    public GraphgenSplitGraph(int numberOfNodes, int chanceOfP, int maxDeg, int einfachheit, int mode) {
        this(numberOfNodes);
        this.chanceOfP = chanceOfP;
        this.maxDeg = maxDeg;
        this.einfachheit = einfachheit;
        this.mode = mode;
    }

    private int getRandomNumberUsingNextInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    public SPQPNode getRoot() {
        return root;
    }


    public void generateGraph() {
        if (mode == 0) {
            generateRandomGraph();
        } else {
            generateSimpleGraph(mode);
        }


    }


    public void generateSimpleGraph(int mode) {


        DefaultEdge edge = edges.get(getRandomNumberUsingNextInt(0, edges.size()));

        if (getRandomNumberUsingNextInt(0, 100000) < chanceOfP) {
            //newInitialPNode(edge);
            newInitialPNodeGuaranteed(edge);
        } else {
            randomnewSNode(edge);
        }

        // Helperclasses.GraphHelper.printToDOT(Helperclasses.GraphHelper.treeToDOT(root));

        for (int i = 0; i < numberOfNodes; i++) {
            edge = edges.get(getRandomNumberUsingNextInt(0, edges.size()));

         /*   int degreeOfedgeSource = multigraph.outDegreeOf(multigraph.getEdgeSource(edge)) + multigraph.inDegreeOf(multigraph.getEdgeSource(edge));
            int degreeOfedgeSink = multigraph.outDegreeOf(multigraph.getEdgeTarget(edge)) + multigraph.inDegreeOf(multigraph.getEdgeTarget(edge));*/
            if (getRandomNumberUsingNextInt(0, 100000) < chanceOfP) {
                if (multigraph.degreeOf(multigraph.getEdgeSource(edge)) < 3 && multigraph.degreeOf(multigraph.getEdgeTarget(edge)) < 3) {
                    if (mode == 1) {
                        if (getRandomNumberUsingNextInt(0, 100) < 50) {
                            DefaultEdge[] arr1 = randomnewSNode(edge);
                            arr1 = randomnewSNode(arr1[1]);
                            arr1 = randomnewSNode(arr1[1]);
                            newDeg3PNode(arr1[0]);
                            i = i + 5;
                        } else {
                            //     int a = multigraph.vertexSet().size();
                            newMaxDegreePNode(edge);
                            //    int b = multigraph.vertexSet().size()-a;
                            i = i + 7;
                        }
                    } else if (mode == 2) {
                        DefaultEdge[] arr1 = randomnewSNode(edge);
                        arr1 = randomnewSNode(arr1[1]);
                        arr1 = randomnewSNode(arr1[1]);
                        newDeg3PNode(arr1[0]);
                        i = i + 5;
                    } else if (mode == 3) {
                        newMaxDegreePNode(edge);
                        i = i + 7;
                    }
                    i += einfachheit + 1;
                } else {
                    i--;
                }

            } else {
                randomnewSNode(edge);
            }
            i = multigraph.vertexSet().size();
        }


        //  System.out.println("test");
        //    Helperclasses.GraphHelper.printTODOTSPQNode(Helperclasses.GraphHelper.treeToDOT(root, 1));

        counter = counter + 1 - 1;
    }

    public void generateRandomGraph() {


        DefaultEdge edge = edges.get(getRandomNumberUsingNextInt(0, edges.size()));

        if (getRandomNumberUsingNextInt(0, 100000) < chanceOfP) {
            //   newInitialPNode(edge);
            newInitialPNodeGuaranteed(edge);
        } else {
            randomnewSNode(edge);
        }

        // Helperclasses.GraphHelper.printToDOT(Helperclasses.GraphHelper.treeToDOT(root));

        for (int i = 0; i < numberOfNodes; i++) {
            edge = edges.get(getRandomNumberUsingNextInt(0, edges.size()));

            int degreeOfedgeSource = multigraph.outDegreeOf(multigraph.getEdgeSource(edge)) + multigraph.inDegreeOf(multigraph.getEdgeSource(edge));
            int degreeOfedgeSink = multigraph.outDegreeOf(multigraph.getEdgeTarget(edge)) + multigraph.inDegreeOf(multigraph.getEdgeTarget(edge));

            if (getRandomNumberUsingNextInt(0, 100000) < chanceOfP) {
                if (degreeOfedgeSource < maxDeg && degreeOfedgeSink < maxDeg) {
                    randomnewPNode(edge, einfachheit);
                    i += einfachheit + 2;
                } else {
                    i--;
                }
            } else {
                randomnewSNode(edge);
            }
            i = multigraph.vertexSet().size();
        }


        //  System.out.println("test");
        //    Helperclasses.GraphHelper.printTODOTSPQNode(Helperclasses.GraphHelper.treeToDOT(root, 1));

        counter = counter + 1 - 1;
    }


    private void newMaxDegreePNode(DefaultEdge edge) {
        DefaultEdge[] arr1 = randomnewSNode(edge);
        arr1 = randomnewSNode(arr1[1]);
        arr1 = randomnewSNode(arr1[1]);
        randomnewMaxDegPNode(arr1[0]);
    }


    private void newInitialPNode(DefaultEdge edge) {

        DefaultEdge edge1 = multigraph.addEdge(multigraph.getEdgeSource(edge), multigraph.getEdgeTarget(edge));
        edges.add(edge1);

        SPQNode oldQNode = edgeSPQNodeHashMap.get(edge);
        SPQNode newPnode = new SPQPNode("P" + ++counter);

        SPQNode newQnode1 = new SPQQNode("Q" + ++counter, multigraph.getEdgeSource(edge1), multigraph.getEdgeTarget(edge1));
        edgeSPQNodeHashMap.put(edge1, newQnode1);

        nodeUmhaengen(oldQNode, newPnode);
        addNodeAsRightChild(newQnode1, newPnode);


        randomnewSNode(edge);

        DefaultEdge[] arr1 = randomnewSNode(edge1);
        randomnewSNode(arr1[0]);

    }


    private void newInitialPNodeGuaranteed(DefaultEdge edge) {

        DefaultEdge edge1 = multigraph.addEdge(multigraph.getEdgeSource(edge), multigraph.getEdgeTarget(edge));
        edges.add(edge1);

        SPQNode oldQNode = edgeSPQNodeHashMap.get(edge);
        SPQNode newPnode = new SPQPNode("P" + ++counter);

        SPQNode newQnode1 = new SPQQNode("Q" + ++counter, multigraph.getEdgeSource(edge1), multigraph.getEdgeTarget(edge1));
        edgeSPQNodeHashMap.put(edge1, newQnode1);

        nodeUmhaengen(oldQNode, newPnode);
        addNodeAsRightChild(newQnode1, newPnode);


        edge = randomnewSNode(edge)[0];
        edge = randomnewSNode(edge)[0];
        edge = randomnewSNode(edge)[0];
        edge = randomnewSNode(edge)[0];

        edge1 = randomnewSNode(edge1)[0];
        edge1 = randomnewSNode(edge1)[0];
        edge1 = randomnewSNode(edge1)[0];
        edge1 = randomnewSNode(edge1)[0];

    }


    private void randomnewPNode(DefaultEdge edge, int einfachheit) {

        //TODO reihenfolge Randomizen? dh zufällig welches rechts, oder links eingefügt wird

        DefaultEdge edge1 = multigraph.addEdge(multigraph.getEdgeSource(edge), multigraph.getEdgeTarget(edge));
        edges.add(edge1);

        SPQNode oldQNode = edgeSPQNodeHashMap.get(edge);
        SPQNode newPnode = new SPQPNode("P" + ++counter);

        SPQNode newQnode1 = new SPQQNode("Q" + ++counter, multigraph.getEdgeSource(edge1), multigraph.getEdgeTarget(edge1));
        edgeSPQNodeHashMap.put(edge1, newQnode1);
        nodeUmhaengen(oldQNode, newPnode);
        // Links oder rechts
        if (getRandomNumberUsingNextInt(0, 1) == 0) {
            addNodeAsRightChild(newQnode1, newPnode);
        } else {
            addNodeAsLeftChild(newQnode1, newPnode);
        }

        DefaultEdge[] arr1;

        // TODO Sinnvoll?
        // mind 1 wetere Kante, um zu sicherzustellen, dass wir keine Mehrfachkante erhalten und um die Wahrscheinlichkeit
        // eines rektilinearen Graphn zu erhöhen
        if (getRandomNumberUsingNextInt(0, 99) < 50) {

            arr1 = randomnewSNode(edge);
            edge = arr1[0];
        } else {
            arr1 = randomnewSNode(edge1);
            edge1 = arr1[0];
        }


        for (int i = 0; i < einfachheit; i++) {

            if (getRandomNumberUsingNextInt(0, 99) < 50) {
                arr1 = randomnewSNode(edge);
                edge = arr1[0];
            } else {
                arr1 = randomnewSNode(edge1);
                edge1 = arr1[0];
            }
        }
    }

    /**
     * Q-Knoten wird durch einen P-Knoten mit Q-P-Q* ersetzt
     * Q* ist 3 Kanten lang und das rechte Kind.
     *
     * @param edge
     */
    private void newDeg3PNode(DefaultEdge edge) {

        //TODO reihenfolge Randomizen? dh zufällig welches rechts, oder links eingefügt wird

        DefaultEdge edge1 = multigraph.addEdge(multigraph.getEdgeSource(edge), multigraph.getEdgeTarget(edge));
        edges.add(edge1);
        SPQNode oldQNode = edgeSPQNodeHashMap.get(edge);
        boolean wasOldQLeft = oldQNode.getParent().getSpqChildren().get(0) == oldQNode;
        SPQNode newPnode = new SPQPNode("P" + ++counter);
        SPQNode newQnode1 = new SPQQNode("Q" + ++counter, multigraph.getEdgeSource(edge1), multigraph.getEdgeTarget(edge1));
        edgeSPQNodeHashMap.put(edge1, newQnode1);
        nodeUmhaengen(oldQNode, newPnode);

        // Links oder rechts
        if (wasOldQLeft) {
            addNodeAsLeftChild(newQnode1, newPnode);
        } else {
            addNodeAsRightChild(newQnode1, newPnode);
        }

        DefaultEdge[] arr1;
        if (getRandomNumberUsingNextInt(0, 99) < 0) {

            arr1 = randomnewSNode(edge);
            edge = arr1[0];
        } else {
            arr1 = randomnewSNode(edge1);
            edge1 = arr1[0];
        }

        for (int i = 0; i < einfachheit; i++) {

            if (getRandomNumberUsingNextInt(0, 99) < 0) {
                arr1 = randomnewSNode(edge);
                edge = arr1[0];
            } else {
                arr1 = randomnewSNode(edge1);
                edge1 = arr1[0];
            }
        }
    }

    private void randomnewMaxDegPNode(DefaultEdge edge) {

        DefaultEdge edge1 = multigraph.addEdge(multigraph.getEdgeSource(edge), multigraph.getEdgeTarget(edge));
        edges.add(edge1);

        SPQNode oldQNode = edgeSPQNodeHashMap.get(edge);
        SPQNode newPnode = new SPQPNode("P" + ++counter);
        SPQNode newQnode1 = new SPQQNode("Q" + ++counter, multigraph.getEdgeSource(edge1), multigraph.getEdgeTarget(edge1));
        edgeSPQNodeHashMap.put(edge1, newQnode1);

        nodeUmhaengen(oldQNode, newPnode);
        addNodeAsRightChild(newQnode1, newPnode);


        DefaultEdge edge2 = multigraph.addEdge(multigraph.getEdgeSource(edge), multigraph.getEdgeTarget(edge));
        SPQNode oldQNode2 = edgeSPQNodeHashMap.get(edge);
        SPQNode newPnode2 = new SPQPNode("P" + ++counter);
        SPQNode newQnode12 = new SPQQNode("Q" + ++counter, multigraph.getEdgeSource(edge2), multigraph.getEdgeTarget(edge2));
        edgeSPQNodeHashMap.put(edge2, newQnode12);

        nodeUmhaengen(oldQNode2, newPnode2);
        addNodeAsLeftChild(newQnode12, newPnode2);









        DefaultEdge[] arr1;

        edge1 = randomnewSNode(edge1)[0];
        // TODO hier letzte Veräanderung
        for (int i = 0; i < einfachheit; i++) {

            if (getRandomNumberUsingNextInt(0, 99) < 0) {
                arr1 = randomnewSNode(edge);
                edge = arr1[0];
            } else {
                arr1 = randomnewSNode(edge1);
                edge1 = arr1[0];
            }
        }

        edge2 = randomnewSNode(edge2)[0];
        // TODO hier letzte Veräanderung
        for (int i = 0; i < einfachheit; i++) {

            if (getRandomNumberUsingNextInt(0, 99) < 0) {
                arr1 = randomnewSNode(edge);
                edge = arr1[0];
            } else {
                arr1 = randomnewSNode(edge2);
                edge2 = arr1[0];
            }
        }

    }


    private DefaultEdge[] randomnewSNode(DefaultEdge edge) {

        Vertex vertex = multigraph.addVertex();
        DefaultEdge edge1 = multigraph.addEdge(vertex, multigraph.getEdgeTarget(edge));
        DefaultEdge edge2 = multigraph.addEdge(multigraph.getEdgeSource(edge), vertex);

        multigraph.removeEdge(edge);
        edges.remove(edge);
        edges.add(edge1);
        edges.add(edge2);
        SPQNode oldQNode = edgeSPQNodeHashMap.get(edge);
        oldQNode.setSinkVertex(vertex); // Der alte Q-Knoten bleibt drinn, bekommt aber eine neue Senke (vertex)
        SPQNode newSnode = new SPQSNode("S" + ++counter);
        SPQNode newQnode = new SPQQNode("Q" + ++counter, multigraph.getEdgeSource(edge1), multigraph.getEdgeTarget(edge1));
        edgeSPQNodeHashMap.remove(edge);
        edgeSPQNodeHashMap.put(edge2, oldQNode);
        edgeSPQNodeHashMap.put(edge1, newQnode);
        nodeUmhaengen(oldQNode, newSnode);
        addNodeAsRightChild(newQnode, newSnode);

        DefaultEdge[] arr = {edge1, edge2};
        return arr;
    }


    private <T extends SPQNode> void addNodeAsRightChild(T node, T parent) {
        node.setParent(parent);
        parent.getSpqChildren().add(node);

    }

    public <T extends SPQNode> void addNodeAsLeftChild(T node, T parent) {
        node.setParent(parent);
        parent.getSpqChildren().add(0, node);
    }


    private <T extends SPQNode> void nodeUmhaengen(T node, T newnode) {
        //Abhängen
        node.getParent().getSpqChildren().set(node.getParent().getSpqChildren().indexOf(node), newnode);
        //neuer Knoten als Parent festlegen

        newnode.setParent(node.getParent());
        addNodeAsRightChild(node, newnode);

    }


    public DirectedMultigraph<Vertex, DefaultEdge> getMultigraph() {
        return multigraph;
    }


    private void randomnewPNode2(DefaultEdge edge, int einfachheit) {

        //TODO reihenfolge Randomizen? dh zufällig welches rechts, oder links eingefügt wird

        DefaultEdge edge1 = multigraph.addEdge(multigraph.getEdgeSource(edge), multigraph.getEdgeTarget(edge));
        edges.add(edge1);

        SPQNode oldQNode = edgeSPQNodeHashMap.get(edge);
        SPQNode newPnode = new SPQPNode("P" + ++counter);

        SPQNode newQnode1 = new SPQQNode("Q" + ++counter, multigraph.getEdgeSource(edge1), multigraph.getEdgeTarget(edge1));
        edgeSPQNodeHashMap.put(edge1, newQnode1);

        nodeUmhaengen(oldQNode, newPnode);
        addNodeAsRightChild(newQnode1, newPnode);

        DefaultEdge[] arr1 = new DefaultEdge[2];

        // TODO Sinnvoll?

        if (getRandomNumberUsingNextInt(0, 99) < 50) {

            arr1 = randomnewSNode(edge);
        } else {
            arr1 = randomnewSNode(edge1);
        }


        for (int i = 0; i < einfachheit; i++) {


            if (getRandomNumberUsingNextInt(0, 99) > 50) {

                DefaultEdge tempEdge1 = arr1[getRandomNumberUsingNextInt(0, 1)];
                arr1 = randomnewSNode(tempEdge1);

            } else {
                DefaultEdge tempEdge2 = arr1[getRandomNumberUsingNextInt(0, 1)];
                arr1 = randomnewSNode(tempEdge2);
            }
        }


    }


}
