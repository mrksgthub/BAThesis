import org.antlr.v4.runtime.misc.Pair;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.*;

public class basicMainClass {


    public static void main(String[] args) throws Exception {
        System.setProperty("org.graphstream.ui", "swing");

        ExecutorService executorService =
                new ThreadPoolExecutor(3, 3, 0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>());
        List<Callable<Object>> callableList = new ArrayList<>();
        assert (false);

        SPQTree tree;
        SPQNode root;

        SPQGenerator spqGenerator = new SPQGenerator(15, 70);
        spqGenerator.run();


        tree = spqGenerator.getTree();
        root = spqGenerator.getRoot();

        SPQExporter spqExporter = new SPQExporter(tree);
        //      spqExporter.run(root);
        //spqExporter.run(root, "C:/a.txt");


        //  SPQImporter spqImporter = new SPQImporter("C:\\Graphs\\55664N2389F.txt");
        // SPQImporter spqImporter = new SPQImporter("C:\\Graphs\\163386N20963F.txt");
        //SPQImporter spqImporter = new SPQImporter("C:\\GraphInvalid\\38321N2774F.txt");
        SPQImporter spqImporter = new SPQImporter("C:/a.txt");
        // SPQImporter spqImporter = new SPQImporter("C:/bug - Kopie.txt");
      //   SPQImporter spqImporter = new SPQImporter("C:/b.txt");
      //  SPQImporter spqImporter = new SPQImporter("C:/testGraph.txt");
        spqImporter.run();


        tree = spqImporter.tree;
        root = tree.getRoot();


        Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding = new Hashtable<>();
        Embedder embedder = new Embedder(embedding, root);
        embedder.run(root);

        FaceGenerator<TreeVertex, DefaultEdge> treeVertexFaceGenerator = new FaceGenerator<>(tree.constructedGraph, root.getStartVertex(), root.getSinkVertex(), embedding);
        treeVertexFaceGenerator.generateFaces2();


        GraphValidifier graphValidifier = new GraphValidifier(tree.constructedGraph, treeVertexFaceGenerator.planarGraphFaces);
        graphValidifier.run();

        GraphHelper.writeTODOTTreeVertex(tree.constructedGraph, "C:/a-constructedGraph.txt");


        HashMap parentsList = new HashMap<>();
        root.determineParents(root, parentsList);


        ConnectivityInspector inspector = new ConnectivityInspector<>(tree.constructedGraph);
        inspector.isConnected();

        // Zeit:
        long startTime = System.currentTimeMillis();


        DidimoRepresentability didimoRepresentability = new DidimoRepresentability(tree, root);
        didimoRepresentability.run();


        root.getMergedChildren().get(0).computeSpirality();


        Angulator angulator = new Angulator(tree, embedding, treeVertexFaceGenerator);
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        long startTime3 = System.currentTimeMillis();
             angulator.run();
        long stopTime3 = System.currentTimeMillis();
        long elapsedTime3 = stopTime3 - startTime3;

        System.out.println("Angulator  :" + elapsedTime3);
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Didimo Zeit: " + elapsedTime);


        startTime = System.currentTimeMillis();

        //  TamassiaRepresentation tamassiaRepresentation = new TamassiaRepresentation(tree, root, treeVertexFaceGenerator);
        //   tamassiaRepresentation.run();
        long startTime2 = System.currentTimeMillis();
        MaxFlow test = new MaxFlow(tree, root, treeVertexFaceGenerator);
        long stopTime2 = System.currentTimeMillis();
        long elapsedTime2 = stopTime2 - startTime2;
        System.out.println("MaxFlow Init " + elapsedTime2);
     //   test.run3();


        stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;
        System.out.println("Tamassia Zeit: " + elapsedTime);
////////////////////////////////////////////


/////////////////////////////////////////////////////////////////////////////////////

// orthogonal rep muss gesetted werden

        System.out.println("Anzahl Faces:" + treeVertexFaceGenerator.planarGraphFaces.size());

        Rectangulator<DefaultEdge> rectangulator = new Rectangulator<>(treeVertexFaceGenerator.planarGraphFaces);
        rectangulator.setOriginaledgeToFaceMap(treeVertexFaceGenerator.getAdjFaces2());
        rectangulator.initialize();
        rectangulator.outerFace.setOrientations();


        Orientator<DefaultEdge> orientator = new Orientator<>(rectangulator.getRectangularFaceMap(), rectangulator.outerFace);
        orientator.run();

        System.out.println("Nach Orientator");


        VerticalEdgeFlow verticalFlow = new VerticalEdgeFlow(orientator.originalFaceList, rectangulator.outerFace);
        DirectedWeightedMultigraph<TreeVertex, DefaultWeightedEdge> testgraph = verticalFlow.generateFlowNetworkLayout2();
        // GraphHelper.printToDOTTreeVertexWeighted(testgraph);
        // verticalFlow.generateCapacities();


        HorizontalEdgeFlow horizontalFlow = new HorizontalEdgeFlow(orientator.originalFaceList, rectangulator.outerFace);
        DirectedWeightedMultigraph<TreeVertex, DefaultWeightedEdge> testgraphHor = horizontalFlow.generateFlowNetworkLayout2();
        //  GraphHelper.printToDOTTreeVertexWeighted(testgraphHor);
        //  horizontalFlow.generateCapacities();


        callableList.add(Executors.callable(verticalFlow));
        callableList.add(Executors.callable(horizontalFlow));

        try {
            executorService.invokeAll(callableList);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        System.out.println("Nach den FlowNetworks");

        Coordinator coordinator = new Coordinator(rectangulator.outerFace, rectangulator.getRectangularFaceMap(), verticalFlow.edgeToArcMap, horizontalFlow.edgeToArcMap, verticalFlow.getMinimumCostFlow(), horizontalFlow.getMinimumCostFlow());
        coordinator.run();


        Graph graph = new SingleGraph("Tutorial 1");


        for (TreeVertex vertex : coordinator.getEdgeToCoordMap().keySet()) {

            if (!vertex.dummy) {
                graph.addNode(vertex.getName());
                Node node = graph.getNode(vertex.getName());
                Pair<Integer, Integer> coords = coordinator.getEdgeToCoordMap().get(vertex);
                node.setAttribute("xy", coords.a, coords.b);
            }
        }


        for (TreeVertex treeVertex : embedding.keySet()) {

            ArrayList<TreeVertex> list = embedding.get(treeVertex);

            for (TreeVertex vertex1 : list) {

                if (graph.getEdge(vertex1.getName() + " " + treeVertex.getName()) == null)
                    graph.addEdge(treeVertex.getName() + " " + vertex1.getName(), treeVertex.getName(), vertex1.getName());

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
