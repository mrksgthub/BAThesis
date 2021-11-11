package Visualizing;

import Algorithms.Coordinator;
import Algorithms.DidimoTestAndAngles;
import Datatypes.PlanarGraphFace;
import Datatypes.Vertex;
import Datatypes.TupleEdge;
import org.antlr.v4.runtime.misc.Pair;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.*;

public class GraphDrawer implements Runnable {

    private final List<PlanarGraphFace<Vertex, DefaultEdge>> planarGraphFaces;
    private final Hashtable<Vertex, ArrayList<Vertex>> embedding;
    double time = Integer.MAX_VALUE;
    private DidimoTestAndAngles angles;
    private HashMap<TupleEdge<Vertex, Vertex>, PlanarGraphFace<Vertex, DefaultEdge>> adjFaces2;


    public GraphDrawer(DidimoTestAndAngles angles) {
        this.angles = angles;
        planarGraphFaces = angles.getTreeVertexFaceGenerator().getPlanarGraphFaces();
        embedding = angles.getEmbedding();
        adjFaces2 = angles.getTreeVertexFaceGenerator().getAdjFaces2();
    }

    public GraphDrawer(List<PlanarGraphFace<Vertex, DefaultEdge>> planarGraphFaces, Hashtable<Vertex, ArrayList<Vertex>> embedding, HashMap<TupleEdge<Vertex, Vertex>, PlanarGraphFace<Vertex, DefaultEdge>> adjFaces2) {
        this.planarGraphFaces = planarGraphFaces;
        this.embedding = embedding;
        this.adjFaces2 = adjFaces2;
    }

    @Override
    public void run() {


        ExecutorService executorService =
                new ThreadPoolExecutor(3, 3, 0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>());
        List<Callable<Object>> callableList = new ArrayList<>();


        System.out.println("Anzahl Faces:" + planarGraphFaces.size());

        Rectangulator<DefaultEdge> rectangulator = new Rectangulator<>(planarGraphFaces);

        rectangulator.setOriginaledgeToFaceMap(adjFaces2);
        rectangulator.initialize();
        rectangulator.outerFace.setOrientations();


        Orientator<DefaultEdge> orientator = new Orientator<>(rectangulator.getRectangularFaceMap(), rectangulator.outerFace);
        orientator.run();

        System.out.println("Nach Visualizing.Orientator");


        VerticalEdgeFlow verticalFlow = new VerticalEdgeFlow(orientator.originalFaceList, rectangulator.outerFace);
        DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> testgraph = verticalFlow.generateFlowNetworkLayout2();
        // Helperclasses.GraphHelper.printToDOTTreeVertexWeighted(testgraph);
        // verticalFlow.generateCapacities();


        HorizontalEdgeFlow horizontalFlow = new HorizontalEdgeFlow(orientator.originalFaceList, rectangulator.outerFace);
        DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> testgraphHor = horizontalFlow.generateFlowNetworkLayout2();
        //  Helperclasses.GraphHelper.printToDOTTreeVertexWeighted(testgraphHor);
        //  horizontalFlow.generateCapacities();


        callableList.add(Executors.callable(verticalFlow));
        callableList.add(Executors.callable(horizontalFlow));

        try {
            executorService.invokeAll(callableList);
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        }


        System.out.println("Nach den FlowNetworks");

        Coordinator coordinator = new Coordinator(rectangulator.outerFace, rectangulator.getRectangularFaceMap(), verticalFlow.edgeToArcMap, horizontalFlow.edgeToArcMap, verticalFlow.getMinimumCostFlow(), horizontalFlow.getMinimumCostFlow());
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


        for (Vertex treeVertex : embedding.keySet()) {

            ArrayList<Vertex> list = embedding.get(treeVertex);

            for (Vertex vertex1 : list) {

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
        // https://stackoverflow.com/questions/37530756/dont-close-swing-main-app-when-closing-graphstream
        Viewer viewer = graph.display(false);
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
    }


}