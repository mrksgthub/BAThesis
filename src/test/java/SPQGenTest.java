import org.antlr.v4.runtime.misc.Pair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.*;

import org.jgrapht.traverse.DepthFirstIterator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;


public class SPQGenTest {

    @Test
    public void graphGen() {

        SPQGen2 spqGen2 = new SPQGen2(10000);
        spqGen2.generate();


        DefaultDirectedGraph<SPQNode, DefaultEdge> graph = GraphHelper.treeToDOT(spqGen2.root, 1);
        GraphHelper.printToDOT(graph);


        System.out.println("Test");

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

            GraphgenSplitGraph graphgenSplitGraph = new GraphgenSplitGraph(50, 30);
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

            if (rectangulator.originaledgeToFaceMap.get(new Tuple<TreeVertex, TreeVertex>(edge.getRight(), edge.getLeft())) == null) {
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


    @Test
    public void didimoTest() {
        System.setProperty("org.graphstream.ui", "swing");
        int counter = 0;
        int vertexcounter = 0;
        SPQPNode root = new SPQPNode("Proot");
        root.setRoot();
        TreeVertex source = new TreeVertex("source");
        TreeVertex sink = new TreeVertex("sink");

        ArrayList<TreeVertex> vertexList = new ArrayList<>();
        vertexList = generateVertices(34);


        SPQSNode sN = addSNode(root, counter++);

        int[] array = {1, 33};
        counter = addQStar(root, counter++, array, vertexList);
        int[] array2 = {1, 2, 3};
        counter = addQStar(sN, counter++, array2, vertexList);
        SPQPNode pV1 = addPNode(sN, counter++);

        SPQSNode sV2 = addSNode(pV1, counter++);

        int[] array3 = {3, 4};
        counter = addQStar(sV2, counter++, array3, vertexList);
        SPQPNode pV4 = addPNode(sV2, counter++);
        int[] array4 = {13, 14, 27, 28, 29, 33};
        counter = addQStar(sV2, counter++, array4, vertexList);

        SPQSNode sV8 = addSNode(pV4, counter++);
        SPQSNode sV9 = addSNode(pV4, counter++);
        int[] array5 = {4, 5, 6};
        counter = addQStar(sV8, counter++, array5, vertexList);
        SPQPNode pV10 = addPNode(sV8, counter++);

        int[] array6 = {6, 8, 13};
        counter = addQStar(pV10, counter++, array6, vertexList);
        int[] array7 = {6, 7, 13};
        counter = addQStar(pV10, counter++, array7, vertexList);


        SPQPNode pV11 = addPNode(sV9, counter++);
        int[] array8 = {11, 12, 13};
        counter = addQStar(sV9, counter++, array8, vertexList);

        int[] array9 = {4, 9, 11};
        counter = addQStar(pV11, counter++, array9, vertexList);
        int[] array10 = {4, 10, 11};
        counter = addQStar(pV11, counter++, array10, vertexList);


        SPQSNode sV3 = addSNode(pV1, counter++);
        int[] array11 = {3, 15};
        counter = addQStar(sV3, counter++, array11, vertexList);
        SPQPNode pV5 = addPNode(sV3, counter++);

        int[] array12 = {15, 16, 17, 22};
        counter = addQStar(pV5, counter++, array12, vertexList);
        int[] array13 = {15, 18, 22};
        counter = addQStar(pV5, counter++, array13, vertexList);
        int[] array14 = {15, 19, 20, 21, 22};
        counter = addQStar(pV5, counter++, array14, vertexList);


        int[] array15 = {22, 23};
        counter = addQStar(sV3, counter++, array15, vertexList);
        SPQPNode pV6 = addPNode(sV3, counter++);
        int[] array16 = {23, 24, 30};
        counter = addQStar(pV6, counter++, array16, vertexList);
        int[] array17 = {23, 25, 26, 30};
        counter = addQStar(pV6, counter++, array17, vertexList);


        SPQPNode pV7 = addPNode(sV3, counter++);
        int[] array18 = {30, 31, 33};
        counter = addQStar(pV7, counter++, array18, vertexList);
        int[] array19 = {30, 32, 33};
        counter = addQStar(pV7, counter++, array19, vertexList);


        SPQTree tree = new SPQTree(root);
        root.compactTree();
        tree.setStartAndSinkNodesOrBuildConstructedGraph(tree.getRoot(), tree.getVisited());
        root.generateQstarNodes();


        root.computeAdjecentVertices();
        boolean check = true;
        root.computeRepresentability(check);


        DefaultDirectedGraph<SPQNode, DefaultEdge> graph2 = GraphHelper.treeToDOT(root, 2);
        GraphHelper.printTODOTSPQNode(graph2);
        GraphHelper.printToDOTTreeVertex(tree.constructedGraph);


        HashMap<SPQNode, ArrayList<Double>> list = new HashMap<>();
        getIntervals(root, list);

        HashMap<TreeVertex, HashSet<TreeVertex>> adjMap = new HashMap<>();
        getAdjecentsMap(root, adjMap, tree.constructedGraph);


        tree.computeNofRoot();
        root.getMergedChildren().get(0).setSpirality(3);
        root.getMergedChildren().get(0).computeSpirality();

        // TODO visited map ist outdated
        // checkSpiralitiesWithinBounds(tree);


        Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding = erstelleHashtablefuerFacegenerator(tree);


        DepthFirstIterator<TreeVertex, DefaultEdge> depthFirstIterator = new DepthFirstIterator<>(tree.constructedGraph);
        while (depthFirstIterator.hasNext()) {
            depthFirstIterator.next();

        }

        FaceGenerator<TreeVertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<TreeVertex, DefaultEdge>(tree.constructedGraph, root.getStartVertex(), root.getSinkVertex(), embedding);
        treeVertexFaceGenerator.generateFaces2(); // counterclockwise = inner, clockwise = outerFacette
        HashMap<MutablePair<TreeVertex, TreeVertex>, Integer> pairIntegerMap = new HashMap<>();
        for (MutablePair<TreeVertex, TreeVertex> pair :
                treeVertexFaceGenerator.adjFaces2.keySet()) {
            pairIntegerMap.put(pair, 0);
        }

        winkelHinzufügen(root, pairIntegerMap);


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
            }


        }


        Rectangulator<DefaultEdge> rectangulator = new Rectangulator<>(treeVertexFaceGenerator.planarGraphFaces);
        rectangulator.setOriginaledgeToFaceMap(treeVertexFaceGenerator.getAdjFaces2());
        rectangulator.initialize();
        int cou = rectangulator.rectangularFaceMap.size();
        System.out.println(cou);
        assert (cou == 29);

        int counter2 = 0;
        List<MutablePair<TreeVertex, TreeVertex>> testList = new ArrayList<>();
        for (MutablePair<TreeVertex, TreeVertex> edge : rectangulator.originaledgeToFaceMap.keySet()
        ) {

            if (rectangulator.originaledgeToFaceMap.get(new Tuple<TreeVertex, TreeVertex>(edge.getRight(), edge.getLeft())) == null) {
                counter2++;
                testList.add(edge);
            }


        }
        System.out.println(counter2);
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
        }


        rectangulator.outerFace.setOrientations();
        Orientator<DefaultEdge> orientator = new Orientator(rectangulator.getRectangularFaceMap(), rectangulator.outerFace);
        orientator.run();


        VerticalEdgeFlow verticalFlow = new VerticalEdgeFlow(orientator.originalFaceList, rectangulator.outerFace);
        DirectedWeightedMultigraph<TreeVertex, DefaultWeightedEdge> testgraphVer = verticalFlow.generateFlowNetworkLayout2();
        GraphHelper.printToDOTTreeVertexWeighted(testgraphVer);

        verticalFlow.generateCapacities();

        HorizontalEdgeFlow horizontalFlow = new HorizontalEdgeFlow(orientator.originalFaceList, rectangulator.outerFace);
        DirectedWeightedMultigraph<TreeVertex, DefaultWeightedEdge> testgraphHor = horizontalFlow.generateFlowNetworkLayout2();
        GraphHelper.printToDOTTreeVertexWeighted(testgraphHor);

        horizontalFlow.generateCapacities();

        Coordinator coordinator = new Coordinator(rectangulator.outerFace, rectangulator.getRectangularFaceMap(), verticalFlow.edgeToArcMap, horizontalFlow.edgeToArcMap, verticalFlow.getMinimumCostFlow(), horizontalFlow.getMinimumCostFlow());
        coordinator.run();


        GraphHelper.writeObjectToFile(coordinator.getEdgeToCoordMap(), "C:\\hashMap.ser");
        GraphHelper.writeObjectToFile(embedding, "C:\\adjecency.ser");

        Map<TreeVertex, Pair<Integer, Integer>> coord = (Map<TreeVertex, Pair<Integer, Integer>>) GraphHelper.readObjectFromFile("C:\\hashMap.ser");
        Hashtable<TreeVertex, ArrayList<TreeVertex>> embed = (Hashtable<TreeVertex, ArrayList<TreeVertex>>) GraphHelper.readObjectFromFile("C:\\adjecency.ser");

        GraphFrame graphFrame = new GraphFrame(coordinator.getEdgeToCoordMap(), embedding);
        graphFrame.displayGraph();


        System.out.println("Test");
    }


    @Test
    public void didimoRepeatedTest() {

        for (int i = 0; i < 1000; i++) {
            didimoTest();

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


    /**
     * Ist counterclockwise
     *
     * @param root
     * @param adjecentsMap
     * @param constructedGraph
     * @return
     */
    public HashMap<TreeVertex, HashSet<TreeVertex>> getAdjecentsMap(SPQNode root, HashMap<TreeVertex, HashSet<TreeVertex>> adjecentsMap, DirectedMultigraph<TreeVertex, DefaultEdge> constructedGraph) {

        for (SPQNode node :
                root.getMergedChildren()) {
            getAdjecentsMap(node, adjecentsMap, constructedGraph);
        }
        if (root.getMergedChildren().size() > 0) {


            Set<TreeVertex> adjSetStart = Graphs.neighborSetOf(constructedGraph, root.getStartVertex());
            Set<TreeVertex> adjSetSink = Graphs.neighborSetOf(constructedGraph, root.getSinkVertex());
            HashSet<TreeVertex> tempSetStart = new LinkedHashSet<>(adjSetStart);
            tempSetStart.retainAll(root.getNodesInCompnent());
            HashSet<TreeVertex> tempSetSink = new LinkedHashSet<>(adjSetSink);
            tempSetSink.retainAll(root.getNodesInCompnent());

            HashSet<TreeVertex> outDegreeAdjSink = new LinkedHashSet<>();
            HashSet<TreeVertex> outDegreeAdjStart = new LinkedHashSet<>();

            for (TreeVertex vertex :
                    adjSetSink) {
                if (!tempSetSink.contains(vertex)) {
                    outDegreeAdjSink.add(vertex);
                }
            }
            for (TreeVertex vertex :
                    adjSetStart) {
                if (!tempSetStart.contains(vertex)) {
                    outDegreeAdjStart.add(vertex);
                }
            }


            root.setOutDegreeSinkVertex(outDegreeAdjSink.size());
            root.setOutDegreeSinkVertexSet(outDegreeAdjSink);

            root.setInDegreeSinkVertex(tempSetSink.size());
            root.setInDegreeSinkVertexSet(tempSetSink);

            root.setOutDegreeStartVertex(outDegreeAdjStart.size());
            root.setOutDegreeStartVertexSet(outDegreeAdjStart);

            root.setInDegreeStartVertex(tempSetStart.size());
            root.setInDegreeStarVertexSet(tempSetStart);


            adjecentsMap.put(root.getStartVertex(), (LinkedHashSet<TreeVertex>) adjSetSink);
            adjecentsMap.put(root.getSinkVertex(), (LinkedHashSet<TreeVertex>) adjSetSink);
        }

        return adjecentsMap;
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