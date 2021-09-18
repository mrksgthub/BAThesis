import org.apache.commons.lang3.tuple.MutablePair;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;


public class SPQGenTest {

    @Test
    public void graphGen() {


    }

    @Test
    public void GraphBuilder() {


    }


    @Test
    public void teilGraphTest() throws Exception {

        for (int i = 0; i < 100000; i++) {
            teilGraphTestRunner();

            System.out.println(i + ". Iteration");
        }


    }


    public void teilGraphTestRunner() throws Exception {

        SPQTree tree;
        SPQNode root;


        SPQGenerator spqGenerator = new SPQGenerator(300, 30);
        spqGenerator.run();


        tree = spqGenerator.getTree();
        root = spqGenerator.getRoot();

        SPQExporter spqExporter = new SPQExporter(tree);
        spqExporter.run(root);
        spqExporter.run(root, "C:/a.txt");

        SPQImporter spqImporter = new SPQImporter("C:/a.txt");
        // SPQImporter spqImporter = new SPQImporter("C:\\Graphs\\38321N2774F.txt");
        //
        // SPQImporter spqImporter = new SPQImporter("C:\\Graphs\\127kN19kF.txt");
        spqImporter.run();

        tree = spqImporter.tree;
        root = spqImporter.tree.getRoot();

        for (TreeVertex vertex : tree.constructedGraph.vertexSet()
        ) {
            int i = tree.constructedGraph.degreeOf(vertex);
            if (i > 4) {
                throw new Exception("Illegal Graph: maxDegree of nodes = " + i);
            }
        }

        Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding = new Hashtable<>();
        Embedder embedder = new Embedder(embedding, root);
        embedder.run(root);
        FaceGenerator<TreeVertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<>(tree.constructedGraph, root.getStartVertex(), root.getSinkVertex(), embedding);
        treeVertexFaceGenerator.generateFaces2();
        TamassiaRepresentation tamassiaRepresentation = new TamassiaRepresentation(tree, root, treeVertexFaceGenerator);
        tamassiaRepresentation.run();


    }


    @Test
    public void teilerGraphgen() throws IOException {


        SPQNode root = new SPQNode();
        SPQTree tree = new SPQTree(root);

        Boolean check = false;
        Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding = new Hashtable<>();


        int counter = 0;
        while (!check) {
            counter++;
            check = true;

            GraphgenSplitGraph graphgenSplitGraph = new GraphgenSplitGraph(500, 30);
            graphgenSplitGraph.generateGraph();


            root = graphgenSplitGraph.getRoot();
            root.compactTree();
            System.out.println("SPQ-Trees");
            //   DefaultDirectedGraph<SPQNode, DefaultEdge> graph2 = GraphHelper.treeToDOT(root, 2);
            //   GraphHelper.printTODOTSPQNode(graph2);


            tree = new SPQTree(root);
            //   tree.fillNodeToEdgesTable(tree.getRoot());
            tree.setStartAndSinkNodesOrBuildConstructedGraph(tree.getRoot(), tree.getVisited());

            // normale repräsentation
            root.generateQstarNodes();

            root.computeAdjecentVertices();

            //   graph2 = GraphHelper.treeToDOT(root, 2);
            //   GraphHelper.printTODOTSPQNode(graph2);
            //   GraphHelper.printToDOTTreeVertex(tree.constructedGraph);

            embedding = erstelleHashtablefuerFacegenerator(tree);


            check = root.computeRepresentability(check);
            if (check) {
                check = (tree.computeNofRoot()) ? check : false;
                if (!check) {
                    System.out.println("Didimo rejected at source Node");
                }
            }

            if (!check) {
                continue;
            }

            boolean tamassiaValid = true;
            try {

                //    Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding = erstelleHashtablefuerFacegenerator(tree);
                FaceGenerator<TreeVertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<TreeVertex, DefaultEdge>(tree.constructedGraph, root.getStartVertex(), root.getSinkVertex(), embedding);
                treeVertexFaceGenerator.generateFaces2(); // counterclockwise = inner, clockwise = outerFacette

                DefaultDirectedWeightedGraph<TreeVertex, DefaultWeightedEdge> treeVertexDefaultEdgeDefaultDirectedWeightedGraph = treeVertexFaceGenerator.generateFlowNetworkLayout2();
                treeVertexFaceGenerator.generateCapacities();

            } catch (Exception e) {
                tamassiaValid = false;
                System.out.println("----------------------------------------Invalid Graph-----------------------------------------------------------");
            }


            assert (tamassiaValid == check);


        }


        // TODO computeNofRoot berchnet spirality und root condition
        boolean isValidDidimo = false;
        if (isValidDidimo = tree.computeNofRoot()) {
            root.getMergedChildren().get(0).computeSpirality();
            System.out.println("------------------------------------------------Valid Graph-------------------------------------------------------");
            //  GraphHelper.printToDOTTreeVertex(tree.constructedGraph);
        }

        //  Angulator angulator = new Angulator(tree, embedding, treeVertexFaceGenerator);
        //   angulator.run();

        //  Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding = erstelleHashtablefuerFacegenerator(tree);
        FaceGenerator<TreeVertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<TreeVertex, DefaultEdge>(tree.constructedGraph, root.getStartVertex(), root.getSinkVertex(), embedding);
        treeVertexFaceGenerator.generateFaces2(); // counterclockwise = inner, clockwise = outerFacette

        DefaultDirectedWeightedGraph<TreeVertex, DefaultWeightedEdge> treeVertexDefaultEdgeDefaultDirectedWeightedGraph = treeVertexFaceGenerator.generateFlowNetworkLayout2();
        treeVertexFaceGenerator.generateCapacities();


        HashMap<MutablePair<TreeVertex, TreeVertex>, Integer> pairIntegerMap = new HashMap<>();

        for (MutablePair<TreeVertex, TreeVertex> pair :
                treeVertexFaceGenerator.adjFaces2.keySet()) {
            pairIntegerMap.put(pair, 0);
        }


        if (isValidDidimo) {
            winkelHinzufügen(root, pairIntegerMap);
        }


        List<PlanarGraphFace<TreeVertex, DefaultEdge>> test = new ArrayList<>();
        for (PlanarGraphFace<TreeVertex, DefaultEdge> face : treeVertexFaceGenerator.planarGraphFaces
        ) {
            int edgeCount = 0;
            for (MutablePair<TreeVertex, TreeVertex> pair :
                    face.getOrthogonalRep().keySet()) {
                face.getOrthogonalRep().put(pair, pairIntegerMap.get(pair));
                edgeCount += pairIntegerMap.get(pair);
            }

            if (Math.abs(edgeCount) != 4) {
                //    assert(Math.abs(edgeCount) == 4);
                test.add(face);
                if (Math.abs(edgeCount) == 4) {
                    System.out.println("Fehler");

                }
            }

        }


        // GraphHelper.printToDOTTreeVertexWeighted(treeVertexDefaultEdgeDefaultDirectedWeightedGraph);


        //GraphHelper.writeFaceGeneatorToFile(treeVertexFaceGenerator, "C:\\graph.ser");
        // FaceGenerator<TreeVertex, DefaultEdge> treeVertexFaceGenerator2 = GraphHelper.ReadFaceGeneratorFromFile("C:\\graph.ser");


        Rectangulator<DefaultEdge> rectangulator = new Rectangulator<>(treeVertexFaceGenerator.planarGraphFaces);
        rectangulator.setOriginaledgeToFaceMap(treeVertexFaceGenerator.getAdjFaces2());
        rectangulator.initialize();
        int cou = rectangulator.rectangularFaceMap.size();
        System.out.println(cou);
        //assert (cou == 29);

        int counter2 = 0;
        List<MutablePair<TreeVertex, TreeVertex>> testList = new ArrayList<>();
        for (MutablePair<TreeVertex, TreeVertex> edge : rectangulator.originaledgeToFaceMap.keySet()
        ) {

            if (rectangulator.originaledgeToFaceMap.get(new TupleEdge<TreeVertex, TreeVertex>(edge.getRight(), edge.getLeft())) == null) {
                counter2++;
                testList.add(edge);
            }


        }
        HashSet<PlanarGraphFace<TreeVertex, DefaultEdge>> set = new HashSet(rectangulator.originaledgeToFaceMap.values());

        List<PlanarGraphFace<TreeVertex, DefaultEdge>> frontList = new ArrayList<>(set);
        System.out.println(counter2);


        rectangulator.outerFace.setOrientations();


        for (PlanarGraphFace<TreeVertex, DefaultEdge> face : rectangulator.rectangularFaceMap.keySet()) {
            int count = 0;
            int count2 = 0;
            for (MutablePair<TreeVertex, TreeVertex> edge : face.edgeList) {
                if (face.getOrthogonalRep().get(edge) != 0) {
                    count++;
                    count2 += face.getOrthogonalRep().get(edge);

                }

            }
            assert (count == 4);
            assert (count2 == 4);
            count = 0;
            count2 = 0;
            for (MutablePair<TreeVertex, TreeVertex> edge : rectangulator.outerFace.edgeList) {
                if (rectangulator.outerFace.getOrthogonalRep().get(edge) != 0) {
                    count++;
                    count2 += rectangulator.outerFace.getOrthogonalRep().get(edge);

                }

            }
            assert (count == 4);
            assert (count2 == -4);


        }


        Orientator<DefaultEdge> orientator = new Orientator(rectangulator.getRectangularFaceMap(), rectangulator.outerFace);
        orientator.run();


        VerticalEdgeFlow verticalFlow = new VerticalEdgeFlow(orientator.originalFaceList, rectangulator.outerFace);
        DirectedWeightedMultigraph<TreeVertex, DefaultWeightedEdge> testgraph = verticalFlow.generateFlowNetworkLayout2();
        // GraphHelper.printToDOTTreeVertexWeighted(testgraph);

        verticalFlow.generateCapacities();


        HorizontalEdgeFlow horizontalFlow = new HorizontalEdgeFlow(orientator.originalFaceList, rectangulator.outerFace);
        DirectedWeightedMultigraph<TreeVertex, DefaultWeightedEdge> testgraphHor = horizontalFlow.generateFlowNetworkLayout2();
        //  GraphHelper.printToDOTTreeVertexWeighted(testgraphHor);

        horizontalFlow.generateCapacities();

        Coordinator coordinator = new Coordinator(rectangulator.outerFace, rectangulator.getRectangularFaceMap(), verticalFlow.edgeToArcMap, horizontalFlow.edgeToArcMap, verticalFlow.getMinimumCostFlow(), horizontalFlow.getMinimumCostFlow());
        coordinator.run();

        //     MaximumFlowAlgorithm<TreeVertex, DefaultWeightedEdge> test33 = new EdmondsKarpMFImpl<>(treeVertexDefaultEdgeDefaultDirectedWeightedGraph);


        //    test33.getMaximumFlow(treeVertexFaceGenerator.source, treeVertexFaceGenerator.sink);
        //    test33.getFlowMap();


        //    MinimumCostFlowProblem<TreeVertex, DefaultWeightedEdge> minimumCostFlowProblem = new MinimumCostFlowProblem.MinimumCostFlowProblemImpl<TreeVertex, DefaultWeightedEdge>(treeVertexDefaultEdgeDefaultDirectedWeightedGraph);

        GraphHelper.writeObjectToFile(coordinator.getEdgeToCoordMap(), "C:\\hashMap.ser");
        GraphHelper.writeObjectToFile(embedding, "C:\\adjecency.ser");

        GraphFrame graphFrame = new GraphFrame(coordinator.getEdgeToCoordMap(), embedding);
        //  graphFrame.main(null);


        System.out.println("Test");


    }

    public Hashtable<TreeVertex, ArrayList<TreeVertex>> erstelleHashtablefuerFacegenerator(SPQTree tree) {
        Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding = new Hashtable<>();

        for (TreeVertex vertex :
                tree.constructedGraph.vertexSet()) {

            ArrayList<TreeVertex> arrList = new ArrayList<>();

            Set<DefaultEdge> tempIn = tree.constructedGraph.incomingEdgesOf(vertex);
            Set<DefaultEdge> tempOut = tree.constructedGraph.outgoingEdgesOf(vertex);


            ArrayList<DefaultEdge> inEdgesList = new ArrayList<>(tempIn);
            ListIterator<DefaultEdge> tempInListIterator = inEdgesList.listIterator(inEdgesList.size());
            ListIterator<DefaultEdge> tempOutListIterator = new ArrayList<>(tempOut).listIterator();

            while (tempInListIterator.hasPrevious()) {
                arrList.add(tree.constructedGraph.getEdgeSource(tempInListIterator.previous()));
            }
            while (tempOutListIterator.hasNext()) {
                arrList.add(tree.constructedGraph.getEdgeTarget(tempOutListIterator.next()));
            }


            embedding.put(vertex, arrList);

        }
        return embedding;
    }


    @Test
    public void teilergraphMassTest() {

        for (int i = 0; i < 10000; i++) {
            try {
                teilerGraphgen();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


    private void checkSpiralitiesWithinBounds(SPQTree tree) {
        for (SPQNode node :
                tree.visited) {
            if (node.mergedChildren.size() > 0 && !node.getName().equals("Proot")) {
                assert (node.getRepIntervalLowerBound() <= node.getSpirality() && node.getSpirality() <= node.getRepIntervalUpperBound());
            }
        }
    }


    public void winkelHinzufügen(SPQNode root, HashMap<MutablePair<TreeVertex, TreeVertex>, Integer> hashmap) {

        for (SPQNode node :
                root.getMergedChildren()) {
            winkelHinzufügen(node, hashmap);
        }
        if (root.getMergedChildren().size() > 1 && !root.isIsroot()) {
            root.computeOrthogonalRepresentation(hashmap);
        }

    }


    public SPQPNode addPNode(SPQNode parentNode, int counter) {
        SPQPNode node = new SPQPNode("P" + counter);
        node.setParent(parentNode);
        parentNode.getChildren().add(node);
        return node;
    }

    public SPQSNode addSNode(SPQNode parentNode, int counter) {
        SPQSNode node = new SPQSNode("S" + counter);
        node.setParent(parentNode);
        parentNode.getChildren().add(node);
        return node;
    }


    public SPQQNode addQNode(SPQNode parentNode, int counter, TreeVertex start, TreeVertex sink) {
        SPQQNode node = new SPQQNode("Q" + counter++);
        node.setParent(parentNode);
        parentNode.getChildren().add(node);
        node.setStartVertex(start);
        node.setSinkVertex(sink);
        return node;
    }

    public ArrayList<TreeVertex> generateVertices(int counter) {
        ArrayList<TreeVertex> list = new ArrayList<>();

        for (int i = 0; i < counter; i++) {
            TreeVertex vertex = new TreeVertex("v" + i);
            list.add(vertex);
        }

        return list;
    }


    public int addQStar(SPQNode parentNode, int counter, int[] nodes, ArrayList<TreeVertex> vertexList) {
        SPQSNode node = new SPQSNode("S" + counter);

        for (int i = 0; i < nodes.length - 1; i++) {
            addQNode(node, counter++, vertexList.get(nodes[i]), vertexList.get(nodes[i + 1]));
        }
        node.setParent(parentNode);
        parentNode.getChildren().add(node);
        return counter;
    }


    public HashMap<SPQNode, ArrayList<Double>> getIntervals(SPQNode root, HashMap<SPQNode, ArrayList<Double>> intervalsMap) {

        for (SPQNode node :
                root.getMergedChildren()) {
            getIntervals(node, intervalsMap);
        }
        if (root.getMergedChildren().size() > 1 && root.getNodeType() != NodeTypesEnum.NODETYPE.Q) {
            ArrayList<Double> array = new ArrayList<>();
            array.add(root.getRepIntervalLowerBound());
            array.add(root.getRepIntervalUpperBound());
            intervalsMap.put(root, array);
        }

        return intervalsMap;
    }


    @Test
    public void graphStreamTest() {
        System.setProperty("org.graphstream.ui", "swing");

        Graph graph = new SingleGraph("Tutorial 1");

        graph.addNode("A");
        Node node = graph.getNode("A");
        node.setAttribute("xy", 0, 0);
        graph.addNode("B");
        node = graph.getNode("B");
        node.setAttribute("xy", 0, 10);

        graph.addNode("C");
        node = graph.getNode("C");
        node.setAttribute("xy", 10, 10);

        graph.addNode("D");
        node = graph.getNode("D");
        node.setAttribute("xy", 5, 5);



/*
        graph.addEdge("AB", "A", "B");
        graph.addEdge("BC", "B", "C");
        graph.addEdge("CA", "C", "A");
*/
        graph.display(false);
    }


}