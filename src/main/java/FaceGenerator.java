import org.antlr.v4.runtime.misc.Pair;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.flow.mincost.CapacityScalingMinimumCostFlow;
import org.jgrapht.alg.flow.mincost.MinimumCostFlowProblem;
import org.jgrapht.alg.interfaces.MinimumCostFlowAlgorithm;
import org.jgrapht.alg.planar.BoyerMyrvoldPlanarityInspector;
import org.jgrapht.graph.*;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

public class FaceGenerator<V extends TreeVertex, E> {

    Map<TreeVertex, Integer> supplyMap = new HashMap<>();
    Map<DefaultWeightedEdge, Integer> lowerMap = new HashMap<>();
    Map<DefaultWeightedEdge, Integer> upperMap = new HashMap<>();


    List<List<E>> listOfFaces = new ArrayList<>();
    List<List<V>> listOfFaces2 = new ArrayList<>();

    Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding;
    BoyerMyrvoldPlanarityInspector.Embedding embedding2;


    Map<E, Integer> visitsMap = new HashMap<>();
    Map<Pair<V, V>, Integer> visitsMap2 = new HashMap<>();
    Map<Pair<V, V>, Integer> pairIntegerMap = new HashMap<>();

    Set<PlanarGraphFace<V, E>> planarGraphFaces = new HashSet<>();

    HashMap<PlanarGraphFace<V, E>, ArrayList<V>> adjVertices = new HashMap<>();

    HashMap<E, ArrayList<PlanarGraphFace<V, E>>> adjFaces = new HashMap<>();
    HashMap<Pair<V, V>, PlanarGraphFace<V, E>> adjFaces2 = new HashMap<Pair<V, V>, PlanarGraphFace<V, E>>();


    DirectedMultigraph<V, E> graph;

    private DirectedMultigraph<TreeVertex, DefaultEdge> flowNetworkLayout;
    V startvertex;
    V sinkVertex;
    AsUndirectedGraph<V, E> embeddingGraphAsUndirectred;
    public TreeVertex sink;
    public TreeVertex source;
    private DefaultDirectedWeightedGraph<TreeVertex, DefaultWeightedEdge> networkGraph;


    public FaceGenerator(DirectedMultigraph<V, E> graph, V startvertex, V sinkVertex, Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding) {

        this.embedding = embedding;
        this.startvertex = startvertex;
        this.sinkVertex = sinkVertex;
        for (E edge : graph.edgeSet()
        ) {
            visitsMap.put(edge, 0);
            pairIntegerMap.put(new Pair<V, V>(graph.getEdgeSource(edge), graph.getEdgeTarget(edge)), 0);
            pairIntegerMap.put(new Pair<V, V>(graph.getEdgeTarget(edge), graph.getEdgeSource(edge)), 0);
            adjFaces.put(edge, new ArrayList<PlanarGraphFace<V, E>>());
        }

        this.graph = graph;

    }


    public DirectedMultigraph<TreeVertex, DefaultEdge> getFlowNetworkLayout() {
        return flowNetworkLayout;
    }

    public void setFlowNetworkLayout(DirectedMultigraph<TreeVertex, DefaultEdge> flowNetworkLayout) {
        this.flowNetworkLayout = flowNetworkLayout;
    }

    public void generateFaces() {


        List<Pair<V, V>> pairList = new ArrayList<>();
        E edge;


        List<Pair<V, V>> pairList1 = new ArrayList<>(pairIntegerMap.keySet());

        Pair<V, V> startingEdge = new Pair<V, V>(startvertex, sinkVertex);
        int x = pairList1.lastIndexOf(startingEdge);
        Collections.swap(pairList1, 0, x);


        Iterator<Pair<V, V>> pairIterator = pairList1.iterator();
        int i = 0;


        while (pairIterator.hasNext()
        ) {

            Pair<V, V> edgePair = pairIterator.next();
            pairList.add(edgePair);
            List<E> face = new ArrayList<>();

            PlanarGraphFace<V, E> faceObj = new PlanarGraphFace<V, E>(Integer.toString(i++));
            adjVertices.put(faceObj, new ArrayList<>());
            planarGraphFaces.add(faceObj);


            V startVertex = edgePair.a;
            List<E> tArrayList = (ArrayList<E>) embedding2.getEdgesAround(startVertex);
            //     E edge = tArrayList.get(0);
            V vertex = startVertex;
            V nextVertex = edgePair.b;
            pairList1.remove(edgePair);

            edge = embeddingGraphAsUndirectred.getEdge(edgePair.a, edgePair.b);
            face.add(edge);

            faceObj.getvSet().add(vertex);
            adjVertices.get(faceObj).add(vertex);
            adjFaces.get(edge).add(faceObj);


            while (nextVertex != startVertex) {

                vertex = nextVertex;
                faceObj.getvSet().add(vertex);
                adjVertices.get(faceObj).add(vertex);

                tArrayList = embedding2.getEdgesAround(nextVertex);
                edge = tArrayList.get((tArrayList.indexOf(edge) + 1) % tArrayList.size());
                adjFaces.get(edge).add(faceObj);
                nextVertex = Graphs.getOppositeVertex(embeddingGraphAsUndirectred, edge, vertex);
                edgePair = new Pair<V, V>(vertex, nextVertex);
                pairList1.remove(edgePair);
                pairList.add(edgePair);

                face.add(edge);
                visitsMap.merge(edge, 1, Integer::sum);

            }
            listOfFaces.add(face);
            pairIterator = pairList1.iterator();

        }


        flowNetworkLayout = (DirectedMultigraph<TreeVertex, DefaultEdge>) generateFlowNetworkLayout();
        System.out.println("Test");


    }


    public void generateFaces2() { // läuft im Moment "rückwärts" von daher hat das äußere Face sink -> source als Ausgangsvertex


        E edge;


        List<Pair<V, V>> pairList = new ArrayList<>(pairIntegerMap.keySet());

        Pair<V, V> startingEdge = new Pair<V, V>(startvertex, sinkVertex);
        int x = pairList.lastIndexOf(startingEdge);
        Collections.swap(pairList, 0, x);

        LinkedHashMap<Pair<V, V>, Boolean> pairBooleanHashtable = new LinkedHashMap<>();
        for (Pair<V, V> pair :
                pairList) {
            pairBooleanHashtable.put(pair, false);
        }
        int i = 0;

        for (Pair<V, V> pair :
                pairBooleanHashtable.keySet()) {
            if (pairBooleanHashtable.get(pair) == false) {


                List<V> face = new ArrayList<>();

                PlanarGraphFace<V, E> faceObj = new PlanarGraphFace<V, E>(Integer.toString(i++));
                adjVertices.put(faceObj, new ArrayList<>());
                planarGraphFaces.add(faceObj);

                V startVertex = pair.a;
                List<V> tArrayList = (ArrayList<V>) embedding.get(startVertex);
                V vertex = startVertex;
                V nextVertex = pair.b;
                pairBooleanHashtable.put(pair, true);

                face.add(startVertex);
                face.add(nextVertex);

                faceObj.getvSet().add(vertex);
                adjVertices.get(faceObj).add(vertex);
                adjFaces2.put(pair, faceObj); // Hier zum checken einfach um die beiden Faces zu finden einfach adjFaces2 nach <a,b> und <b,a> untersuchen
                faceObj.getOrthogonalRep().put(pair, 999);


                while (nextVertex != startVertex) {


                    faceObj.getvSet().add(nextVertex);
                    adjVertices.get(faceObj).add(nextVertex);

                    tArrayList = (List<V>) embedding.get(nextVertex);
                    V temp = nextVertex;
                    nextVertex = tArrayList.get(Math.floorMod((tArrayList.indexOf(vertex) - 1), tArrayList.size()));
                    Pair<V, V> vvPair = new Pair<>(temp, nextVertex);
                    vertex = temp;
                    adjFaces2.put(vvPair, faceObj);
                    faceObj.getOrthogonalRep().put(vvPair, 999);


                    pairBooleanHashtable.put(vvPair, true);
                    //.add(edgePair);

                    face.add(nextVertex);
                    visitsMap2.merge(vvPair, 1, Integer::sum);

                }
                listOfFaces2.add(face);
            }

        }


    }


    private Graph<TreeVertex, DefaultEdge> generateFlowNetworkLayout() {


        DirectedMultigraph<TreeVertex, DefaultEdge> graph = new DirectedMultigraph<>(DefaultEdge.class);


        for (TreeVertex nodes : graph.vertexSet()
        ) {
            graph.addVertex(nodes);

        }
        for (PlanarGraphFace<V, E> vePlanarGraphFace : planarGraphFaces
        ) {
            graph.addVertex(vePlanarGraphFace);
        }


        for (E edge : adjFaces.keySet()
        ) {
            graph.addEdge(adjFaces.get(edge).get(0), adjFaces.get(edge).get(1));
            graph.addEdge(adjFaces.get(edge).get(1), adjFaces.get(edge).get(0));
        }
        for (PlanarGraphFace<V, E> vePlanarGraphFace : adjVertices.keySet()
        ) {
            for (TreeVertex vertex : adjVertices.get(vePlanarGraphFace)
            ) {
                graph.addEdge(vertex, vePlanarGraphFace);
            }
        }


        return graph;
    }


    public DefaultDirectedWeightedGraph<TreeVertex, DefaultWeightedEdge> generateFlowNetworkLayout2() {
        networkGraph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        //  DirectedMultigraph<TreeVertex, DefaultEdge> graph2 = new DirectedMultigraph<>(DefaultEdge.class);


        //   sink = new TreeVertex("Mainsink");
        //   source = new TreeVertex("Mainsource");
        //   graph.addVertex(sink);
        //     graph.addVertex(source);


        List<V> vertexList = listOfFaces2.get(0);
        TreeVertex outerFace = new TreeVertex("0");
        networkGraph.addVertex(outerFace);
        // Test
        //     graph2.addVertex(outerFace);

        supplyMap.put(outerFace, -1 * (2 * (vertexList.size() - 1) + 4));

        //     graph.setEdgeWeight(graph.addEdge(outerFace, sink), 1);
        for (int j = 0; j < vertexList.size() - 1; j++) {
            TreeVertex temp = vertexList.get(j);
            networkGraph.addVertex(temp);
            supplyMap.put(temp, 4);
            DefaultWeightedEdge e = networkGraph.addEdge(temp, outerFace);
            networkGraph.setEdgeWeight(e, 1);
            upperMap.put(e, 4);
            lowerMap.put(e, 1);


            //     graph.setEdgeWeight(graph.addEdge(source, temp), 1);
        }


        for (int i = 1; i < listOfFaces2.size(); i++) {

            vertexList = listOfFaces2.get(i);
            TreeVertex innerFace = new TreeVertex(Integer.toString(i));
            networkGraph.addVertex(innerFace);
            supplyMap.put(innerFace, -1 * (2 * (vertexList.size() - 1) - 4));

            //        graph.setEdgeWeight(graph.addEdge(innerFace, sink), 2*(vertexList.size()-1)-4);


            for (int j = 0; j < vertexList.size() - 1; j++) {
                TreeVertex temp = vertexList.get(j);

                networkGraph.addVertex(temp);
                supplyMap.put(temp, 4);

                DefaultWeightedEdge e = networkGraph.addEdge(temp, innerFace);
                networkGraph.setEdgeWeight(e, 1);
                upperMap.put(e, 4);
                lowerMap.put(e, 1);


                //    DefaultWeightedEdge e = graph.addEdge(source, temp);
                //        if (e != null) {
                //          graph.setEdgeWeight(e, 4);
                //      }

            }


        }


        return networkGraph;
    }

    public void generateCapacities() {


        List<V> vertexList = listOfFaces2.get(0);

        int capacity = 2 * (vertexList.size() - 1) - 4;

        for (int i = 1; i < listOfFaces2.size() - 1; i++) {

            vertexList = listOfFaces2.get(i);


            capacity = 2 * (vertexList.size() - 1) - 4;


        }


        MinimumCostFlowProblem<TreeVertex,
                DefaultWeightedEdge> problem = new MinimumCostFlowProblem.MinimumCostFlowProblemImpl<>(
                networkGraph, v -> supplyMap.getOrDefault(v, 0), upperMap::get,
                e -> lowerMap.getOrDefault(e, 1));

        CapacityScalingMinimumCostFlow<TreeVertex, DefaultWeightedEdge> minimumCostFlowAlgorithm =
                new CapacityScalingMinimumCostFlow<>();



        HashMap<DefaultWeightedEdge, Double> costMap = new HashMap();
        for ( DefaultWeightedEdge edge:
        networkGraph.edgeSet()     ) {


        };

        MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> minimumCostFlow =
                minimumCostFlowAlgorithm.getMinimumCostFlow(problem);



    }


}
