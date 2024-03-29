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


/**
 * Implementiert den Ablauf der Umwandlung einer gültigen orthogonalen Repräsentation in eine Zeichnung.
 *
 *
 *
 */
public class GraphDrawer implements Runnable {

    private final List<PlanarGraphFace<Vertex>> planarGraphFaces;
    private final Hashtable<Vertex, ArrayList<Vertex>> vertexToAdjListMap;
    double time = Integer.MAX_VALUE;
    private final HashMap<TupleEdge<Vertex, Vertex>, PlanarGraphFace<Vertex>> tupleToFaceMap;


    public GraphDrawer(List<PlanarGraphFace<Vertex>> planarGraphFaces, Hashtable<Vertex, ArrayList<Vertex>> embedding, HashMap<TupleEdge<Vertex, Vertex>, PlanarGraphFace<Vertex>> tupleToFaceMap) {
        this.planarGraphFaces = planarGraphFaces;
        this.vertexToAdjListMap = embedding;
        this.tupleToFaceMap = tupleToFaceMap;
    }

    @Override
    public void run() {

        ExecutorService executorService =
                new ThreadPoolExecutor(3, 3, 0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>());
        List<Callable<Object>> callableList = new ArrayList<>();


        // Wandle Facetten in rechteckige Facetten um
        Rectangulator rectangulator = new Rectangulator(planarGraphFaces);

        rectangulator.setOriginalTupleToFaceMap(tupleToFaceMap);
        try {
            rectangulator.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Orientiere die TupleEdges in den Facetten
        Orientator orientator = new Orientator(rectangulator.getRectangularInnerFaces(), rectangulator.getOuterFace());
        orientator.run(rectangulator.getOuterFace(), rectangulator.getRectangularInnerFaces());

        // Lege die Länge der horizontalen Kanten fest
        VerticalEdgeFlow verticalFlow = new VerticalEdgeFlow(rectangulator.getRectangularInnerFaces(), rectangulator.getOuterFace());
        DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> testgraphVer = verticalFlow.generateFlowNetworkLayout();

        // Lege die Länge der vertikalen Kanten fest
        HorizontalEdgeFlow horizontalFlow = new HorizontalEdgeFlow(rectangulator.getRectangularInnerFaces(), rectangulator.getOuterFace());
        DirectedWeightedMultigraph<Vertex, DefaultWeightedEdge> testgraphHor = horizontalFlow.generateFlowNetworkLayout();


        // Löst das Minimalkostenproblem auf beiden Netzwerken.
        callableList.add(Executors.callable(verticalFlow));
        callableList.add(Executors.callable(horizontalFlow));

        try {
            executorService.invokeAll(callableList);
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        }


     //   System.out.println("Nach den FlowNetworks");

        // Lege Koordinaten fest
        Coordinator coordinator = new Coordinator(rectangulator.outerFace, rectangulator.getRectangularInnerFaces(), verticalFlow.edgeToArcMap, horizontalFlow.edgeToArcMap, verticalFlow.getMinimumCostFlow(), horizontalFlow.getMinimumCostFlow());
        coordinator.run();

        GraphStreamOutput output = new GraphStreamOutput(vertexToAdjListMap, coordinator.getEdgeToCoordMap());
        output.run();
    }



}
