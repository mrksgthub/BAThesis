package Helperclasses;

import PlanarityAndAngles.*;
import PlanarityAndAngles.Didimo.Angulator;
import PlanarityAndAngles.Didimo.DidimoRepresentability;
import Datastructures.SPQNode;
import Datastructures.SPQStarTree;
import Datastructures.Vertex;
import GraphGenerators.SPQGenerator;
import PlanarityAndAngles.Flow.MaxFlow;
import Visualizer.*;
import org.antlr.v4.runtime.misc.Pair;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.*;

class basicMainClass {


    public static void main(String[] args) throws Exception {
        System.setProperty("org.graphstream.ui", "swing");

        ExecutorService executorService =
                new ThreadPoolExecutor(3, 3, 0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>());
        List<Callable<Object>> callableList = new ArrayList<>();
        assert (false);

        SPQStarTree tree;
        SPQNode root;

        SPQGenerator spqGenerator = new SPQGenerator(1000, 20);
        spqGenerator.run();


        tree = spqGenerator.getTree();
        root = spqGenerator.getRoot();

        SPQExporter spqExporter = new SPQExporter();
        //      spqExporter.run(root);
        spqExporter.run(root, "C:/a.dot");


       // Helperclasses.SPQImporter spqImporter = new Helperclasses.SPQImporter("C:\\Graphs\\10002N9F.txt");
        // Helperclasses.SPQImporter spqImporter = new Helperclasses.SPQImporter("C:\\Graphs\\163386N20963F.txt");
      //  Helperclasses.SPQImporter spqImporter = new Helperclasses.SPQImporter("C:\\GraphInvalid\\10002N9F.txt");
        SPQImporter spqImporter = new SPQImporter();
        // Helperclasses.SPQImporter spqImporter = new Helperclasses.SPQImporter("C:/bug - Kopie.txt");
      //   Helperclasses.SPQImporter spqImporter = new Helperclasses.SPQImporter("C:/b.txt");
      //  Helperclasses.SPQImporter spqImporter = new Helperclasses.SPQImporter("C:/testGraph.txt");
        spqImporter.runFromFile("C:/a.dot");


        tree = spqImporter.getTree();
        root = tree.getRoot();

        Deque<SPQNode> s = DFSIterator.buildPostOrderStack(root);
        int notQnodeCount = 0;
        while (!s.isEmpty()) {
            notQnodeCount += (s.pop().getSpqChildren().size() != 0) ? 1 : 0;
        }



        FaceGenerator<Vertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<>(tree.getConstructedGraph(), root.getStartVertex(), root.getSinkVertex());
        treeVertexFaceGenerator.generateFaces();


        GraphValidifier graphValidifier = new GraphValidifier(tree.getConstructedGraph(), treeVertexFaceGenerator.getPlanarGraphFaces());
        graphValidifier.run();

        GraphHelper.writeTODOTTreeVertex(tree.getConstructedGraph(), "C:/a-constructedGraph.dot");





        ConnectivityInspector inspector = new ConnectivityInspector<>(tree.getConstructedGraph());
        inspector.isConnected();

        // Zeit:
        long startTime = System.currentTimeMillis();


        DidimoRepresentability didimoRepresentability = new DidimoRepresentability();
        didimoRepresentability.run(tree.getRoot());


       // root.getMergedChildren().get(0).computeSpirality();

       // tree.computeSpirality(root.getSpqStarChildren().get(0));


        Angulator angulator = new Angulator();
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        long startTime3 = System.currentTimeMillis();
        angulator.run(tree.getRoot(), treeVertexFaceGenerator.getPlanarGraphFaces());
        long stopTime3 = System.currentTimeMillis();
        long elapsedTime3 = stopTime3 - startTime3;

        System.out.println("Algorithms.Didimo.Angulator  :" + elapsedTime3);
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Didimo Zeit: " + elapsedTime);


        startTime = System.currentTimeMillis();

        //  PlanarityAndAngles.Flow.TamassiaRepresentation tamassiaRepresentation = new PlanarityAndAngles.Flow.TamassiaRepresentation(tree, root, treeVertexFaceGenerator);
        //  tamassiaRepresentation.run(treeVertexFaceGenerator.getPlanarGraphFaces());


        long startTime2 = System.currentTimeMillis();
        MaxFlow test = new MaxFlow(tree, treeVertexFaceGenerator.getPlanarGraphFaces());
        long stopTime2 = System.currentTimeMillis();
        long elapsedTime2 = stopTime2 - startTime2;
        System.out.println("Algorithms.Flow.MaxFlow Init " + elapsedTime2);
      //  test.runPushRelabel(treeVertexFaceGenerator.getPlanarGraphFaces(), tree.getConstructedGraph());
        test.runJGraptHImplementation();


        stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;
        System.out.println("Tamassia Zeit: " + elapsedTime);
////////////////////////////////////////////


/////////////////////////////////////////////////////////////////////////////////////

// orthogonal rep muss gesetted werden

        System.out.println("Anzahl Faces:" + treeVertexFaceGenerator.getPlanarGraphFaces().size());

        Rectangulator rectangulator = new Rectangulator(treeVertexFaceGenerator.getPlanarGraphFaces());
        rectangulator.setOriginalEdgeToFaceMap(treeVertexFaceGenerator.getAdjFaces2());
        rectangulator.run();
       // rectangulator.getOuterFace().setOrientationsOuterFacette();


        Orientator orientator = new Orientator(rectangulator.getRectangularInnerFaces(), rectangulator.getOuterFace());
        orientator.run(rectangulator.getOuterFace(), rectangulator.getRectangularInnerFaces());

        System.out.println("Nach Visualizing.Orientator");


        VerticalEdgeFlow verticalFlow = new VerticalEdgeFlow(rectangulator.getRectangularInnerFaces(), rectangulator.getOuterFace());
         DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> testgraph = verticalFlow.generateFlowNetworkLayout2();
        // Helperclasses.GraphHelper.printToDOTTreeVertexWeighted(testgraph);
        // verticalFlow.generateCapacities();


        HorizontalEdgeFlow horizontalFlow = new HorizontalEdgeFlow(rectangulator.getRectangularInnerFaces(), rectangulator.getOuterFace());
        DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> testgraphHor = horizontalFlow.generateFlowNetworkLayout2();
        //  Helperclasses.GraphHelper.printToDOTTreeVertexWeighted(testgraphHor);
        //  horizontalFlow.generateCapacities();


        callableList.add(Executors.callable(verticalFlow));
        callableList.add(Executors.callable(horizontalFlow));

        try {
            executorService.invokeAll(callableList);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        System.out.println("Nach den FlowNetworks");

        Coordinator coordinator = new Coordinator(rectangulator.getOuterFace(), rectangulator.getRectangularInnerFaces(), verticalFlow.getEdgeToArcMap(), horizontalFlow.getEdgeToArcMap(), verticalFlow.getMinimumCostFlow(), horizontalFlow.getMinimumCostFlow());
        coordinator.run();


        Graph graph = new SingleGraph("Tutorial 1");


        for (Vertex vertex : coordinator.getEdgeToCoordMap().keySet()) {

            if (!vertex.isDummy()) {
                graph.addNode(vertex.getName());
                Node node = graph.getNode(vertex.getName());
                Pair<Integer, Integer> coords = coordinator.getEdgeToCoordMap().get(vertex);
                node.setAttribute("xy", coords.a, coords.b);
            }
        }


   /*     for (Vertex treeVertex : embedding.keySet()) {*/
            for (Vertex vertex : coordinator.getEdgeToCoordMap().keySet()) {

                if (!vertex.isDummy()) {
                    ArrayList<Vertex> list =vertex.getAdjacentVertices();

                    for (Vertex vertex1 : list) {

                        if (graph.getEdge(vertex1.getName() + " " + vertex.getName()) == null)
                            graph.addEdge(vertex.getName() + " " + vertex1.getName(), vertex.getName(), vertex1.getName());

                    }
                }


            }

        for (Node node : graph) {
            node.setAttribute("ui.label", node.getId());
        }
         String styleSheet =
                "node { text-alignment: at-right; text-color: #222; } node#B { text-alignment: at-left; } node#C { text-alignment: under; }";

        graph.setAttribute("ui.stylesheet", styleSheet);

        GraphHelper.writeGraphStreamToFIle(graph, "C:/a-graphstreamDOT.dot");

        graph.display(false);


    }


}
