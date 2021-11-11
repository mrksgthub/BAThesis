package Algorithms;

import Datatypes.PlanarGraphFace;
import Datatypes.Vertex;
import Datatypes.TupleEdge;
import org.jgrapht.alg.flow.mincost.CapacityScalingMinimumCostFlow;
import org.jgrapht.alg.flow.mincost.MinimumCostFlowProblem;
import org.jgrapht.alg.interfaces.MinimumCostFlowAlgorithm;
import org.jgrapht.alg.planar.BoyerMyrvoldPlanarityInspector;
import org.jgrapht.graph.*;

import java.io.Serializable;
import java.util.*;

public class FaceGenerator<V extends Vertex, E> implements Serializable {

    public Vertex sink;
    public Vertex source;
    Map<Vertex, Integer> supplyMap = new HashMap<>();
    Map<DefaultWeightedEdge, Integer> lowerMap = new HashMap<>();
    Map<DefaultWeightedEdge, Integer> upperMap = new HashMap<>();
    List<List<E>> listOfFaces = new ArrayList<>();
    List<List<V>> listOfFaces2 = new ArrayList<>();
    Hashtable<Vertex, ArrayList<Vertex>> embedding;
    BoyerMyrvoldPlanarityInspector.Embedding embedding2;
    Map<E, Integer> visitsMap = new HashMap<>();
    Map<TupleEdge<V, V>, Integer> visitsMap2 = new HashMap<>();
    Map<TupleEdge<V, V>, Integer> pairIntegerMap = new HashMap<>();
    List<PlanarGraphFace<V, E>> planarGraphFaces = new ArrayList<>();
    HashMap<PlanarGraphFace<V, E>, ArrayList<V>> adjVertices = new HashMap<>();
    HashMap<E, ArrayList<PlanarGraphFace<V, E>>> adjFaces = new HashMap<>();
    HashMap<TupleEdge<V, V>, PlanarGraphFace<V, E>> adjFaces2 = new HashMap<TupleEdge<V, V>, PlanarGraphFace<V, E>>();
    DirectedMultigraph<V, E> graph;
    V startvertex;
    V sinkVertex;
    AsUndirectedGraph<V, E> embeddingGraphAsUndirectred;
    private DirectedMultigraph<Vertex, DefaultEdge> flowNetworkLayout;
    private DefaultDirectedWeightedGraph<Vertex, DefaultWeightedEdge> networkGraph;

    public FaceGenerator(DirectedMultigraph<V, E> graph, V startvertex, V sinkVertex, Hashtable<Vertex, ArrayList<Vertex>> embedding) {

        this.embedding = embedding;
        this.startvertex = startvertex;
        this.sinkVertex = sinkVertex;
        for (E edge : graph.edgeSet()
        ) {
            visitsMap.put(edge, 0);
            pairIntegerMap.put(new TupleEdge<>(graph.getEdgeSource(edge), graph.getEdgeTarget(edge)), 0);
            pairIntegerMap.put(new TupleEdge<V, V>(graph.getEdgeTarget(edge), graph.getEdgeSource(edge)), 0);
            adjFaces.put(edge, new ArrayList<PlanarGraphFace<V, E>>());
        }

        this.graph = graph;

    }

    public Map<Vertex, Integer> getSupplyMap() {
        return supplyMap;
    }

    public void setSupplyMap(Map<Vertex, Integer> supplyMap) {
        this.supplyMap = supplyMap;
    }

    public Map<DefaultWeightedEdge, Integer> getLowerMap() {
        return lowerMap;
    }

    public void setLowerMap(Map<DefaultWeightedEdge, Integer> lowerMap) {
        this.lowerMap = lowerMap;
    }

    public Map<DefaultWeightedEdge, Integer> getUpperMap() {
        return upperMap;
    }

    public void setUpperMap(Map<DefaultWeightedEdge, Integer> upperMap) {
        this.upperMap = upperMap;
    }

    public List<List<E>> getListOfFaces() {
        return listOfFaces;
    }

    public void setListOfFaces(List<List<E>> listOfFaces) {
        this.listOfFaces = listOfFaces;
    }

    public List<List<V>> getListOfFaces2() {
        return listOfFaces2;
    }

    public void setListOfFaces2(List<List<V>> listOfFaces2) {
        this.listOfFaces2 = listOfFaces2;
    }

    public Hashtable<Vertex, ArrayList<Vertex>> getEmbedding() {
        return embedding;
    }

    public void setEmbedding(Hashtable<Vertex, ArrayList<Vertex>> embedding) {
        this.embedding = embedding;
    }

    public BoyerMyrvoldPlanarityInspector.Embedding getEmbedding2() {
        return embedding2;
    }

    public void setEmbedding2(BoyerMyrvoldPlanarityInspector.Embedding embedding2) {
        this.embedding2 = embedding2;
    }

    public Map<E, Integer> getVisitsMap() {
        return visitsMap;
    }

    public void setVisitsMap(Map<E, Integer> visitsMap) {
        this.visitsMap = visitsMap;
    }

    public Map<TupleEdge<V, V>, Integer> getVisitsMap2() {
        return visitsMap2;
    }

    public void setVisitsMap2(Map<TupleEdge<V, V>, Integer> visitsMap2) {
        this.visitsMap2 = visitsMap2;
    }

    public Map<TupleEdge<V, V>, Integer> getPairIntegerMap() {
        return pairIntegerMap;
    }

    public void setPairIntegerMap(Map<TupleEdge<V, V>, Integer> pairIntegerMap) {
        this.pairIntegerMap = pairIntegerMap;
    }

    public List<PlanarGraphFace<V, E>> getPlanarGraphFaces() {
        return planarGraphFaces;
    }

    public void setPlanarGraphFaces(ArrayList<PlanarGraphFace<V, E>> planarGraphFaces) {
        this.planarGraphFaces = planarGraphFaces;
    }

    public HashMap<PlanarGraphFace<V, E>, ArrayList<V>> getAdjVertices() {
        return adjVertices;
    }

    public void setAdjVertices(HashMap<PlanarGraphFace<V, E>, ArrayList<V>> adjVertices) {
        this.adjVertices = adjVertices;
    }

    public HashMap<E, ArrayList<PlanarGraphFace<V, E>>> getAdjFaces() {
        return adjFaces;
    }

    public void setAdjFaces(HashMap<E, ArrayList<PlanarGraphFace<V, E>>> adjFaces) {
        this.adjFaces = adjFaces;
    }

    public HashMap<TupleEdge<V, V>, PlanarGraphFace<V, E>> getAdjFaces2() {
        return adjFaces2;
    }

    public void setAdjFaces2(HashMap<TupleEdge<V, V>, PlanarGraphFace<V, E>> adjFaces2) {
        this.adjFaces2 = adjFaces2;
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

    public void setStartvertex(V startvertex) {
        this.startvertex = startvertex;
    }

    public V getSinkVertex() {
        return sinkVertex;
    }

    public void setSinkVertex(V sinkVertex) {
        this.sinkVertex = sinkVertex;
    }

    public AsUndirectedGraph<V, E> getEmbeddingGraphAsUndirectred() {
        return embeddingGraphAsUndirectred;
    }

    public void setEmbeddingGraphAsUndirectred(AsUndirectedGraph<V, E> embeddingGraphAsUndirectred) {
        this.embeddingGraphAsUndirectred = embeddingGraphAsUndirectred;
    }

    public Vertex getSink() {
        return sink;
    }

    public void setSink(Vertex sink) {
        this.sink = sink;
    }

    public Vertex getSource() {
        return source;
    }

    public void setSource(Vertex source) {
        this.source = source;
    }

    public DefaultDirectedWeightedGraph<Vertex, DefaultWeightedEdge> getNetworkGraph() {
        return networkGraph;
    }

    public void setNetworkGraph(DefaultDirectedWeightedGraph<Vertex, DefaultWeightedEdge> networkGraph) {
        this.networkGraph = networkGraph;
    }

    public DirectedMultigraph<Vertex, DefaultEdge> getFlowNetworkLayout() {
        return flowNetworkLayout;
    }

    public void setFlowNetworkLayout(DirectedMultigraph<Vertex, DefaultEdge> flowNetworkLayout) {
        this.flowNetworkLayout = flowNetworkLayout;
    }


    public void generateFaces2() { // läuft im Moment "rückwärts" von daher hat das äußere Face sink -> source als Ausgangsvertex


        List<TupleEdge<V, V>> pairList = new ArrayList<>(pairIntegerMap.keySet());

        TupleEdge<V, V> startingEdge = new TupleEdge<>(startvertex, sinkVertex);
        int x = pairList.lastIndexOf(startingEdge);
        Collections.swap(pairList, 0, x);

        LinkedHashMap<TupleEdge<V, V>, Boolean> pairBooleanHashtable = new LinkedHashMap<>();
        for (TupleEdge<V, V> pair :
                pairList) {
            pairBooleanHashtable.put(pair, false);
        }
        int i = 0;

        for (TupleEdge<V, V> pair :
                pairBooleanHashtable.keySet()) {
            if (pairBooleanHashtable.get(pair) == false) {
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
                List<V> tArrayList = (ArrayList<V>) embedding.get(startVertex);
                V vertex = startVertex;
                V nextVertex = pair.getRight();
                pairBooleanHashtable.put(pair, true);

                face.add(startVertex);
                face.add(nextVertex);

                //     faceObj.getvSet().add(vertex);
                adjVertices.get(faceObj).add(vertex);
                adjFaces2.put(pair, faceObj); // Hier zum checken einfach um die beiden Faces zu finden einfach adjFaces2 nach <a,b> und <b,a> untersuchen
                faceObj.getOrthogonalRep().put(pair, 999);


                while (nextVertex != startVertex) {

                    //         faceObj.getvSet().add(nextVertex);
                    adjVertices.get(faceObj).add(nextVertex);

                    tArrayList = (List<V>) embedding.get(nextVertex);
                    V temp = nextVertex;
                    nextVertex = tArrayList.get(Math.floorMod((tArrayList.indexOf(vertex) - 1), tArrayList.size()));
                    TupleEdge<V, V> vvPair = new TupleEdge<>(temp, nextVertex);
                    vertex = temp;
                    adjFaces2.put(vvPair, faceObj);
                    faceObj.getOrthogonalRep().put(vvPair, 999);

                    pairBooleanHashtable.put(vvPair, true);
                    //.add(edgePair);
                    edgeList.add(vvPair);
                    face.add(nextVertex);
                    visitsMap2.merge(vvPair, 1, Integer::sum);

                }
                faceObj.computeEdgeToIndexMap();
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


    public DefaultDirectedWeightedGraph<Vertex, DefaultWeightedEdge> generateFlowNetworkLayout3() {
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
