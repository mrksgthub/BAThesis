package Visualizer;

import Datastructures.PlanarGraphFace;
import Datastructures.TupleEdge;
import Datastructures.Vertex;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.*;

public class GraphDrawer implements Runnable {

    private final List<PlanarGraphFace<Vertex>> planarGraphFaces;
    private final Hashtable<Vertex, ArrayList<Vertex>> vertexToAdjListMap;
    double time = Integer.MAX_VALUE;
    private final HashMap<TupleEdge<Vertex, Vertex>, PlanarGraphFace<Vertex>> adjFaces2;


    public GraphDrawer(List<PlanarGraphFace<Vertex>> planarGraphFaces, Hashtable<Vertex, ArrayList<Vertex>> embedding, HashMap<TupleEdge<Vertex, Vertex>, PlanarGraphFace<Vertex>> adjFaces2) {
        this.planarGraphFaces = planarGraphFaces;
        this.vertexToAdjListMap = embedding;
        this.adjFaces2 = adjFaces2;
    }

    @Override
    public void run() {

        ExecutorService executorService =
                new ThreadPoolExecutor(3, 3, 0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>());
        List<Callable<Object>> callableList = new ArrayList<>();


        System.out.println("Anzahl Faces:" + planarGraphFaces.size());

        Rectangulator rectangulator = new Rectangulator(planarGraphFaces);

        rectangulator.setOriginalEdgeToFaceMap(adjFaces2);
        try {
            rectangulator.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // rectangulator.outerFace.setOrientationsOuterFacette();


        Orientator orientator = new Orientator(rectangulator.getRectangularInnerFaces(), rectangulator.getOuterFace());
        orientator.run(rectangulator.getOuterFace(), rectangulator.getRectangularInnerFaces());

        System.out.println("Nach Visualizing.Orientator");


        VerticalEdgeFlow verticalFlow = new VerticalEdgeFlow(rectangulator.getRectangularInnerFaces(), rectangulator.getOuterFace());
        DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> testgraphVer = verticalFlow.generateFlowNetworkLayout2();
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
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        }


        System.out.println("Nach den FlowNetworks");

        // Lege Koordinaten fest
        Coordinator coordinator = new Coordinator(rectangulator.outerFace, rectangulator.getRectangularInnerFaces(), verticalFlow.edgeToArcMap, horizontalFlow.edgeToArcMap, verticalFlow.getMinimumCostFlow(), horizontalFlow.getMinimumCostFlow());
        coordinator.run();

        GraphStreamOutput output = new GraphStreamOutput(vertexToAdjListMap, coordinator.getEdgeToCoordMap());
        output.run();
    }



}
