import Datatypes.*;
import Datatypes.Vertex;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.Test;

import java.util.*;


class SPQGenTest {

    @Test
    void graphGen() {


    }

    @Test
    void GraphBuilder() {


    }


/*
    @Test
    public void teilGraphTest() throws Exception {

        for (int i = 0; i < 100000; i++) {
            teilGraphTestRunner();

            System.out.println(i + ". Iteration");
        }


    }
*/

/*

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
        // Helperclasses.SPQImporter spqImporter = new Helperclasses.SPQImporter("C:\\Graphs\\38321N2774F.txt");
        //
        // Helperclasses.SPQImporter spqImporter = new Helperclasses.SPQImporter("C:\\Graphs\\127kN19kF.txt");
        spqImporter.run();

        tree = spqImporter.getTree();
        root = spqImporter.getTree().getRoot();

        for (Experimental.Vertex vertex : tree.getConstructedGraph().vertexSet()
        ) {
            int i = tree.getConstructedGraph().degreeOf(vertex);
            if (i > 4) {
                throw new Exception("Illegal Graph: maxDegree of nodes = " + i);
            }
        }

        Hashtable<Experimental.Vertex, ArrayList<Experimental.Vertex>> embedding = new Hashtable<>();
        Embedder embedder = new Embedder(embedding);
        embedder.run(root);
        FaceGenerator<Experimental.Vertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<>(tree.getConstructedGraph(), root.getStartVertex(), root.getSinkVertex(), embedding);
        treeVertexFaceGenerator.generateFaces2();
        TamassiaRepresentation tamassiaRepresentation = new TamassiaRepresentation(tree, root, treeVertexFaceGenerator);
        tamassiaRepresentation.run();


    }
*/

/*
    @Test
    public void teilerGraphgen() throws IOException {


        SPQNode root = new SPQNode();
        SPQTree tree = new SPQTree(root);

        Boolean check = false;
        Hashtable<Experimental.Vertex, ArrayList<Experimental.Vertex>> embedding = new Hashtable<>();


        int counter = 0;
        while (!check) {
            counter++;
            check = true;

            GraphgenSplitGraph graphgenSplitGraph = new GraphgenSplitGraph(500, 30);
            graphgenSplitGraph.generateGraph();


            root = graphgenSplitGraph.getRoot();
            root.compactTree();
            System.out.println("SPQ-Trees");
            //   DefaultDirectedGraph<Datatypes.SPQNode, DefaultEdge> graph2 = Helperclasses.GraphHelper.treeToDOT(root, 2);
            //   Helperclasses.GraphHelper.printTODOTSPQNode(graph2);


            tree = new SPQTree(root);
            //   tree.fillNodeToEdgesTable(tree.getRoot());
            tree.setStartAndSinkNodesOrBuildConstructedGraph(tree.getRoot(), tree.getVisited());

            // normale repräsentation
            root.generateQstarNodes();

            root.computeAdjecentVertices();

            //   graph2 = Helperclasses.GraphHelper.treeToDOT(root, 2);
            //   Helperclasses.GraphHelper.printTODOTSPQNode(graph2);
            //   Helperclasses.GraphHelper.printToDOTTreeVertex(tree.constructedGraph);

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

                //    Hashtable<Datatypes.TreeVertex, ArrayList<Datatypes.TreeVertex>> embedding = erstelleHashtablefuerFacegenerator(tree);
                FaceGenerator<Experimental.Vertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<Experimental.Vertex, DefaultEdge>(tree.getConstructedGraph(), root.getStartVertex(), root.getSinkVertex(), embedding);
                treeVertexFaceGenerator.generateFaces2(); // counterclockwise = inner, clockwise = outerFacette

                DefaultDirectedWeightedGraph<Experimental.Vertex, DefaultWeightedEdge> treeVertexDefaultEdgeDefaultDirectedWeightedGraph = treeVertexFaceGenerator.generateFlowNetworkLayout2();
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
            //  Helperclasses.GraphHelper.printToDOTTreeVertex(tree.constructedGraph);
        }

        //  Algorithms.Didimo.Angulator angulator = new Algorithms.Didimo.Angulator(tree, embedding, treeVertexFaceGenerator);
        //   angulator.run();

        //  Hashtable<Datatypes.TreeVertex, ArrayList<Datatypes.TreeVertex>> embedding = erstelleHashtablefuerFacegenerator(tree);
        FaceGenerator<Experimental.Vertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<Experimental.Vertex, DefaultEdge>(tree.getConstructedGraph(), root.getStartVertex(), root.getSinkVertex(), embedding);
        treeVertexFaceGenerator.generateFaces2(); // counterclockwise = inner, clockwise = outerFacette

        DefaultDirectedWeightedGraph<Experimental.Vertex, DefaultWeightedEdge> treeVertexDefaultEdgeDefaultDirectedWeightedGraph = treeVertexFaceGenerator.generateFlowNetworkLayout2();
        treeVertexFaceGenerator.generateCapacities();


        HashMap<TupleEdge<Experimental.Vertex, Experimental.Vertex>, Integer> pairIntegerMap = new HashMap<>();

        for (TupleEdge<Experimental.Vertex, Experimental.Vertex> pair :
                treeVertexFaceGenerator.getAdjFaces2().keySet()) {
            pairIntegerMap.put(pair, 0);
        }


        if (isValidDidimo) {
            winkelHinzufügen(root, pairIntegerMap);
        }


        List<PlanarGraphFace<Experimental.Vertex, DefaultEdge>> test = new ArrayList<>();
        for (PlanarGraphFace<Experimental.Vertex, DefaultEdge> face : treeVertexFaceGenerator.getPlanarGraphFaces()
        ) {
            int edgeCount = 0;
            for (TupleEdge<Experimental.Vertex, Experimental.Vertex> pair :
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


        // Helperclasses.GraphHelper.printToDOTTreeVertexWeighted(treeVertexDefaultEdgeDefaultDirectedWeightedGraph);


        //Helperclasses.GraphHelper.writeFaceGeneatorToFile(treeVertexFaceGenerator, "C:\\graph.ser");
        // Algorithms.FaceGenerator<Datatypes.TreeVertex, DefaultEdge> treeVertexFaceGenerator2 = Helperclasses.GraphHelper.ReadFaceGeneratorFromFile("C:\\graph.ser");


        Rectangulator<DefaultEdge> rectangulator = new Rectangulator<>(treeVertexFaceGenerator.getPlanarGraphFaces());
        rectangulator.setOriginaledgeToFaceMap(treeVertexFaceGenerator.getAdjFaces2());
        rectangulator.initialize();
        int cou = rectangulator.getRectangularFaceMap().size();
        System.out.println(cou);
        //assert (cou == 29);

        int counter2 = 0;
        List<TupleEdge<Experimental.Vertex, Experimental.Vertex>> testList = new ArrayList<>();
        for (TupleEdge<Experimental.Vertex, Experimental.Vertex> edge : rectangulator.getOriginaledgeToFaceMap().keySet()
        ) {

            if (rectangulator.getOriginaledgeToFaceMap().get(new TupleEdge<>(edge.getRight(), edge.getLeft())) == null) {
                counter2++;
                testList.add(edge);
            }


        }
        HashSet<PlanarGraphFace<Experimental.Vertex, DefaultEdge>> set = new HashSet(rectangulator.getOriginaledgeToFaceMap().values());

        List<PlanarGraphFace<Experimental.Vertex, DefaultEdge>> frontList = new ArrayList<>(set);
        System.out.println(counter2);


        rectangulator.getOuterFace().setOrientations();


        for (PlanarGraphFace<Experimental.Vertex, DefaultEdge> face : rectangulator.getRectangularFaceMap().keySet()) {
            int count = 0;
            int count2 = 0;
            for (TupleEdge<Experimental.Vertex, Experimental.Vertex> edge : face.getEdgeList()) {
                if (face.getOrthogonalRep().get(edge) != 0) {
                    count++;
                    count2 += face.getOrthogonalRep().get(edge);

                }

            }
            assert (count == 4);
            assert (count2 == 4);
            count = 0;
            count2 = 0;
            for (TupleEdge<Experimental.Vertex, Experimental.Vertex> edge : rectangulator.getOuterFace().getEdgeList()) {
                if (rectangulator.getOuterFace().getOrthogonalRep().get(edge) != 0) {
                    count++;
                    count2 += rectangulator.getOuterFace().getOrthogonalRep().get(edge);

                }

            }
            assert (count == 4);
            assert (count2 == -4);


        }


        Orientator<DefaultEdge> orientator = new Orientator(rectangulator.getRectangularFaceMap(), rectangulator.getOuterFace());
        orientator.run();


        VerticalEdgeFlow verticalFlow = new VerticalEdgeFlow(orientator.getOriginalFaceList(), rectangulator.getOuterFace());
        DirectedWeightedMultigraph<Experimental.Vertex, DefaultWeightedEdge> testgraph = verticalFlow.generateFlowNetworkLayout2();
        // Helperclasses.GraphHelper.printToDOTTreeVertexWeighted(testgraph);

        verticalFlow.generateCapacities();


        HorizontalEdgeFlow horizontalFlow = new HorizontalEdgeFlow(orientator.getOriginalFaceList(), rectangulator.getOuterFace());
        DirectedWeightedMultigraph<Experimental.Vertex, DefaultWeightedEdge> testgraphHor = horizontalFlow.generateFlowNetworkLayout2();
        //  Helperclasses.GraphHelper.printToDOTTreeVertexWeighted(testgraphHor);

        horizontalFlow.generateCapacities();

        Coordinator coordinator = new Coordinator(rectangulator.getOuterFace(), rectangulator.getRectangularFaceMap(), verticalFlow.getEdgeToArcMap(), horizontalFlow.getEdgeToArcMap(), verticalFlow.getMinimumCostFlow(), horizontalFlow.getMinimumCostFlow());
        coordinator.run();

        //     MaximumFlowAlgorithm<Datatypes.TreeVertex, DefaultWeightedEdge> test33 = new EdmondsKarpMFImpl<>(treeVertexDefaultEdgeDefaultDirectedWeightedGraph);


        //    test33.getMaximumFlow(treeVertexFaceGenerator.source, treeVertexFaceGenerator.sink);
        //    test33.getFlowMap();


        //    MinimumCostFlowProblem<Datatypes.TreeVertex, DefaultWeightedEdge> minimumCostFlowProblem = new MinimumCostFlowProblem.MinimumCostFlowProblemImpl<Datatypes.TreeVertex, DefaultWeightedEdge>(treeVertexDefaultEdgeDefaultDirectedWeightedGraph);

        GraphHelper.writeObjectToFile(coordinator.getEdgeToCoordMap(), "C:\\hashMap.ser");
        GraphHelper.writeObjectToFile(embedding, "C:\\adjecency.ser");

        GraphFrame graphFrame = new GraphFrame(coordinator.getEdgeToCoordMap(), embedding);
        //  graphFrame.main(null);


        System.out.println("Test");


    }*/

    Hashtable<Vertex, ArrayList<Vertex>> erstelleHashtablefuerFacegenerator(SPQStarTree tree) {
        Hashtable<Vertex, ArrayList<Vertex>> embedding = new Hashtable<>();

        for (Vertex vertex :
                tree.getConstructedGraph().vertexSet()) {

            ArrayList<Vertex> arrList = new ArrayList<>();

            Set<DefaultEdge> tempIn = tree.getConstructedGraph().incomingEdgesOf(vertex);
            Set<DefaultEdge> tempOut = tree.getConstructedGraph().outgoingEdgesOf(vertex);


            ArrayList<DefaultEdge> inEdgesList = new ArrayList<>(tempIn);
            ListIterator<DefaultEdge> tempInListIterator = inEdgesList.listIterator(inEdgesList.size());
            ListIterator<DefaultEdge> tempOutListIterator = new ArrayList<>(tempOut).listIterator();

            while (tempInListIterator.hasPrevious()) {
                arrList.add(tree.getConstructedGraph().getEdgeSource(tempInListIterator.previous()));
            }
            while (tempOutListIterator.hasNext()) {
                arrList.add(tree.getConstructedGraph().getEdgeTarget(tempOutListIterator.next()));
            }


            embedding.put(vertex, arrList);

        }
        return embedding;
    }


/*
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
*/


    /*

    SPQPNode addPNode(SPQNode parentNode, int counter) {
        SPQPNode node = new SPQPNode("P" + counter, true);
        node.setParent(parentNode);
        parentNode.getChildren().add(node);
        return node;
    }
*/

    SPQSNode addSNode(SPQNode parentNode, int counter) {
        SPQSNode node = new SPQSNode("S" + counter);
        node.setParent(parentNode);
        parentNode.getSpqChildren().add(node);
        return node;
    }


    SPQQNode addQNode(SPQNode parentNode, int counter, Vertex start, Vertex sink) {
        SPQQNode node = new SPQQNode("Q" + counter++);
        node.setParent(parentNode);
        parentNode.getSpqChildren().add(node);
        node.setStartVertex(start);
        node.setSinkVertex(sink);
        return node;
    }

    ArrayList<Vertex> generateVertices(int counter) {
        ArrayList<Vertex> list = new ArrayList<>();

        for (int i = 0; i < counter; i++) {
            Vertex vertex = new Vertex("v" + i);
            list.add(vertex);
        }

        return list;
    }


    int addQStar(SPQNode parentNode, int counter, int[] nodes, ArrayList<Vertex> vertexList) {
        SPQSNode node = new SPQSNode("S" + counter);

        for (int i = 0; i < nodes.length - 1; i++) {
            addQNode(node, counter++, vertexList.get(nodes[i]), vertexList.get(nodes[i + 1]));
        }
        node.setParent(parentNode);
        parentNode.getSpqChildren().add(node);
        return counter;
    }


    HashMap<SPQNode, ArrayList<Double>> getIntervals(SPQNode root, HashMap<SPQNode, ArrayList<Double>> intervalsMap) {

        for (SPQNode node :
                root.getSpqStarChildren()) {
            getIntervals(node, intervalsMap);
        }
        if (root.getSpqStarChildren().size() > 1 && root.getNodeType() != NodeTypesEnum.NODETYPE.Q) {
            ArrayList<Double> array = new ArrayList<>();
            array.add(root.getRepIntervalLowerBound());
            array.add(root.getRepIntervalUpperBound());
            intervalsMap.put(root, array);
        }

        return intervalsMap;
    }


    @Test
    void graphStreamTest() {
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