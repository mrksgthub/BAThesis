package Algorithms;

import Datatypes.PlanarGraphFace;
import Datatypes.TupleEdge;
import Datatypes.Vertex;
import org.jgrapht.alg.flow.mincost.CapacityScalingMinimumCostFlow;
import org.jgrapht.alg.flow.mincost.MinimumCostFlowProblem;
import org.jgrapht.alg.interfaces.MinimumCostFlowAlgorithm;
import org.jgrapht.graph.*;

import java.io.Serializable;
import java.util.*;

public class FaceGenerator<V extends Vertex, E> implements Serializable {

    private List<TupleEdge<V, V>> pairList;
    private Map<Vertex, Integer> supplyMap = new HashMap<>();
    private Map<DefaultWeightedEdge, Integer> lowerMap = new HashMap<>();
    private Map<DefaultWeightedEdge, Integer> upperMap = new HashMap<>();
    private List<List<V>> listOfFaces2 = new ArrayList<>();
    private Hashtable<Vertex, ArrayList<Vertex>> embedding;
    private Map<E, Integer> visitsMap = new HashMap<>();
    private Map<TupleEdge<V, V>, Integer> visitsMap2 = new HashMap<>();
    private List<PlanarGraphFace<V, E>> planarGraphFaces = new ArrayList<>();
    private HashMap<PlanarGraphFace<V, E>, ArrayList<V>> adjVertices = new HashMap<>();
    private HashMap<E, ArrayList<PlanarGraphFace<V, E>>> adjFaces = new HashMap<>();
    private HashMap<TupleEdge<V, V>, PlanarGraphFace<V, E>> adjFaces2 = new HashMap<>();
    private DirectedMultigraph<V, E> graph;
    private V startvertex;
    private V sinkVertex;
    private DefaultDirectedWeightedGraph<Vertex, DefaultWeightedEdge> networkGraph;

    public FaceGenerator(DirectedMultigraph<V, E> graph, V startvertex, V sinkVertex, Hashtable<Vertex, ArrayList<Vertex>> embedding) {

        this.embedding = embedding;
        this.startvertex = startvertex;
        this.sinkVertex = sinkVertex;
        pairList = new ArrayList<>();
        for (E edge : graph.edgeSet()
        ) {
            visitsMap.put(edge, 0);
         /*   pairIntegerMap.put(new TupleEdge<>(graph.getEdgeSource(edge), graph.getEdgeTarget(edge)), 0);
            pairIntegerMap.put(new TupleEdge<>(graph.getEdgeTarget(edge), graph.getEdgeSource(edge)), 0);*/

            pairList.add(new TupleEdge<>(graph.getEdgeSource(edge), graph.getEdgeTarget(edge)));
            pairList.add(new TupleEdge<>(graph.getEdgeTarget(edge), graph.getEdgeSource(edge)));

            adjFaces.put(edge, new ArrayList<>());
        }

        this.graph = graph;

    }

    public List<List<V>> getListOfFaces2() {
        return listOfFaces2;
    }

    public Hashtable<Vertex, ArrayList<Vertex>> getEmbedding() {
        return embedding;
    }

    public void setEmbedding(Hashtable<Vertex, ArrayList<Vertex>> embedding) {
        this.embedding = embedding;
    }

    public List<PlanarGraphFace<V, E>> getPlanarGraphFaces() {
        return planarGraphFaces;
    }

    public HashMap<TupleEdge<V, V>, PlanarGraphFace<V, E>> getAdjFaces2() {
        return adjFaces2;
    }

    public DirectedMultigraph<V, E> getGraph() {
        return graph;
    }

    public void setGraph(DirectedMultigraph<V, E> graph) {
        this.graph = graph;
    }

    public V getStartvertex() {
        return startvertex;
    }

    public V getSinkVertex() {
        return sinkVertex;
    }

    public DefaultDirectedWeightedGraph<Vertex, DefaultWeightedEdge> getNetworkGraph() {
        return networkGraph;
    }


    public void generateFaces() { // läuft im Moment "rückwärts" von daher hat das äußere Face sink -> source als Ausgangsvertex


       // List<TupleEdge<V, V>> pairList = new ArrayList<>(pairIntegerMap.keySet());

        TupleEdge<V, V> startingEdge = new TupleEdge<>(startvertex, sinkVertex);
        int x = pairList.lastIndexOf(startingEdge);
        Collections.swap(pairList, 0, x); // die Kante (s,t) soll das erste besuchte Element sein, damit man die äußere Facette festlegen kann.

        LinkedHashMap<TupleEdge<V, V>, Boolean> tupleVisitedMap = new LinkedHashMap<>();
        for (TupleEdge<V, V> pair :
                pairList) {
            tupleVisitedMap.put(pair, false);
        }
        int i = 0;

        for (TupleEdge<V, V> pair :
               pairList) {
            if (!tupleVisitedMap.get(pair)) {
                List<V> face = new ArrayList<>();
                List<TupleEdge<V, V>> edgeList = new ArrayList<>();
                edgeList.add(pair);

                PlanarGraphFace<V, E> faceObj = new PlanarGraphFace<>(Integer.toString(i++));
                if (faceObj.getName().equals("0")) {
                    faceObj.setType(PlanarGraphFace.FaceType.EXTERNAL);
                }
                adjVertices.put(faceObj, new ArrayList<>());
                planarGraphFaces.add(faceObj);
                faceObj.setEdgeList(edgeList);

                V startVertex = pair.getLeft();
                List<V> tArrayList;
                V vertex = startVertex;
                V nextVertex = pair.getRight();
                tupleVisitedMap.put(pair, true);

                face.add(startVertex);
                face.add(nextVertex);

                adjVertices.get(faceObj).add(vertex);
                adjFaces2.put(pair, faceObj); // Hier zum checken, um die beiden Faces zu finden einfach adjFaces2 nach <a,b> und <b,a> untersuchen
                faceObj.getOrthogonalRep().put(pair, 999);


                // Bestimmung einer Facette: Nachdem man ein Startknoten gefunden hat sucht man ab jetzt immer den Folgeknoten bis man wieder am Startknoten ankommt
                while (nextVertex != startVertex) {

                    adjVertices.get(faceObj).add(nextVertex);

                    tArrayList = (List<V>) embedding.get(nextVertex);
                    V temp = nextVertex;
                    nextVertex = tArrayList.get(Math.floorMod((tArrayList.indexOf(vertex) - 1), tArrayList.size()));
                    TupleEdge<V, V> vvPair = new TupleEdge<>(temp, nextVertex);
                    vertex = temp;
                    adjFaces2.put(vvPair, faceObj);
                    faceObj.getOrthogonalRep().put(vvPair, 999);

                    tupleVisitedMap.put(vvPair, true);
                    edgeList.add(vvPair);
                    face.add(nextVertex);
                    visitsMap2.merge(vvPair, 1, Integer::sum);

                }
                listOfFaces2.add(face);
            }

        }


    }


    public DefaultDirectedWeightedGraph<Vertex, DefaultWeightedEdge> generateFlowNetworkLayout2() {
        networkGraph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);

        List<V> vertexList = listOfFaces2.get(0);
        Vertex outerFace = planarGraphFaces.get(0);
        networkGraph.addVertex(planarGraphFaces.get(0));

        supplyMap.put(outerFace, -1 * (2 * (vertexList.size() - 1) + 4));

        for (int j = 0; j < vertexList.size() - 1; j++) {
            Vertex temp = vertexList.get(j);
            networkGraph.addVertex(temp);
            supplyMap.put(temp, 4);
            DefaultWeightedEdge e = networkGraph.addEdge(temp, planarGraphFaces.get(0));
            networkGraph.setEdgeWeight(e, 1);
            upperMap.put(e, 4);
            lowerMap.put(e, 1);

        }


        for (int i = 1; i < listOfFaces2.size(); i++) {

            vertexList = listOfFaces2.get(i);
            Vertex innerFace = planarGraphFaces.get(i);
            networkGraph.addVertex(planarGraphFaces.get(i));
            supplyMap.put(innerFace, -1 * (2 * (vertexList.size() - 1) - 4));


            for (int j = 0; j < vertexList.size() - 1; j++) {
                Vertex temp = vertexList.get(j);
                networkGraph.addVertex(temp);
                supplyMap.put(temp, 4);
                DefaultWeightedEdge e = networkGraph.addEdge(temp, planarGraphFaces.get(i));
                networkGraph.setEdgeWeight(e, 1);
                upperMap.put(e, 4);
                lowerMap.put(e, 1);

            }


        }


        return networkGraph;
    }


    public MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> generateCapacities() {


        MinimumCostFlowProblem<Vertex,
                DefaultWeightedEdge> problem = new MinimumCostFlowProblem.MinimumCostFlowProblemImpl<>(
                networkGraph, v -> supplyMap.getOrDefault(v, 0), upperMap::get,
                e -> lowerMap.getOrDefault(e, 1));

        CapacityScalingMinimumCostFlow<Vertex, DefaultWeightedEdge> minimumCostFlowAlgorithm =
                new CapacityScalingMinimumCostFlow<>();


        MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> minimumCostFlow =
                minimumCostFlowAlgorithm.getMinimumCostFlow(problem);


        return minimumCostFlow;
    }


}
