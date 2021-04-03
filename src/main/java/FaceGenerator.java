
import org.apache.commons.lang3.tuple.MutablePair;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.flow.mincost.CapacityScalingMinimumCostFlow;
import org.jgrapht.alg.flow.mincost.MinimumCostFlowProblem;
import org.jgrapht.alg.interfaces.MinimumCostFlowAlgorithm;
import org.jgrapht.alg.interfaces.PlanarityTestingAlgorithm;
import org.jgrapht.alg.planar.BoyerMyrvoldPlanarityInspector;
import org.jgrapht.graph.*;
import org.jgrapht.graph.DefaultEdge;

import java.io.Serializable;
import java.util.*;

public class FaceGenerator<V extends TreeVertex, E> implements Serializable {

    public  TreeVertex sink;
    public  TreeVertex source;
    Map<TreeVertex, Integer> supplyMap = new HashMap<>();
    Map<DefaultWeightedEdge, Integer> lowerMap = new HashMap<>();
    Map<DefaultWeightedEdge, Integer> upperMap = new HashMap<>();
    List<List<E>> listOfFaces = new ArrayList<>();
    List<List<V>> listOfFaces2 = new ArrayList<>();
    Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding;
     BoyerMyrvoldPlanarityInspector.Embedding embedding2;
    Map<E, Integer> visitsMap = new HashMap<>();
    Map<MutablePair<V, V>, Integer> visitsMap2 = new HashMap<>();
    Map<MutablePair<V, V>, Integer> pairIntegerMap = new HashMap<>();
    Set<PlanarGraphFace<V, E>> planarGraphFaces = new LinkedHashSet<>();
    HashMap<PlanarGraphFace<V, E>, ArrayList<V>> adjVertices = new HashMap<>();
    HashMap<E, ArrayList<PlanarGraphFace<V, E>>> adjFaces = new HashMap<>();
    HashMap<MutablePair<V, V>, PlanarGraphFace<V, E>> adjFaces2 = new HashMap<MutablePair<V, V>, PlanarGraphFace<V, E>>();
     DirectedMultigraph<V, E> graph;
     V startvertex;
     V sinkVertex;
     AsUndirectedGraph<V, E> embeddingGraphAsUndirectred;
    private   DirectedMultigraph<TreeVertex, DefaultEdge> flowNetworkLayout;
    private  DefaultDirectedWeightedGraph<TreeVertex, DefaultWeightedEdge> networkGraph;

    public FaceGenerator(DirectedMultigraph<V, E> graph, V startvertex, V sinkVertex, Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding) {

        this.embedding = embedding;
        this.startvertex = startvertex;
        this.sinkVertex = sinkVertex;
        for (E edge : graph.edgeSet()
        ) {
            visitsMap.put(edge, 0);
            pairIntegerMap.put(new Tuple<>(graph.getEdgeSource(edge), graph.getEdgeTarget(edge)), 0);
            pairIntegerMap.put(new Tuple<V, V>(graph.getEdgeTarget(edge), graph.getEdgeSource(edge)), 0);
            adjFaces.put(edge, new ArrayList<PlanarGraphFace<V, E>>());
        }

        this.graph = graph;

    }

    public Map<TreeVertex, Integer> getSupplyMap() {
        return supplyMap;
    }

    public void setSupplyMap(Map<TreeVertex, Integer> supplyMap) {
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

    public Hashtable<TreeVertex, ArrayList<TreeVertex>> getEmbedding() {
        return embedding;
    }

    public void setEmbedding(Hashtable<TreeVertex, ArrayList<TreeVertex>> embedding) {
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

    public Map<MutablePair<V, V>, Integer> getVisitsMap2() {
        return visitsMap2;
    }

    public void setVisitsMap2(Map<MutablePair<V, V>, Integer> visitsMap2) {
        this.visitsMap2 = visitsMap2;
    }

    public Map<MutablePair<V, V>, Integer> getPairIntegerMap() {
        return pairIntegerMap;
    }

    public void setPairIntegerMap(Map<MutablePair<V, V>, Integer> pairIntegerMap) {
        this.pairIntegerMap = pairIntegerMap;
    }

    public Set<PlanarGraphFace<V, E>> getPlanarGraphFaces() {
        return planarGraphFaces;
    }

    public void setPlanarGraphFaces(Set<PlanarGraphFace<V, E>> planarGraphFaces) {
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

    public HashMap<MutablePair<V, V>, PlanarGraphFace<V, E>> getAdjFaces2() {
        return adjFaces2;
    }

    public void setAdjFaces2(HashMap<MutablePair<V, V>, PlanarGraphFace<V, E>> adjFaces2) {
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

    public TreeVertex getSink() {
        return sink;
    }

    public void setSink(TreeVertex sink) {
        this.sink = sink;
    }

    public TreeVertex getSource() {
        return source;
    }

    public void setSource(TreeVertex source) {
        this.source = source;
    }

    public DefaultDirectedWeightedGraph<TreeVertex, DefaultWeightedEdge> getNetworkGraph() {
        return networkGraph;
    }

    public void setNetworkGraph(DefaultDirectedWeightedGraph<TreeVertex, DefaultWeightedEdge> networkGraph) {
        this.networkGraph = networkGraph;
    }

    public DirectedMultigraph<TreeVertex, DefaultEdge> getFlowNetworkLayout() {
        return flowNetworkLayout;
    }

    public void setFlowNetworkLayout(DirectedMultigraph<TreeVertex, DefaultEdge> flowNetworkLayout) {
        this.flowNetworkLayout = flowNetworkLayout;
    }

    public void generateFaces() {


        List<MutablePair<V, V>> pairList = new ArrayList<>();
        E edge;


        List<MutablePair<V, V>> pairList1 = new ArrayList<>(pairIntegerMap.keySet());

        MutablePair<V, V> startingEdge = new Tuple<V, V>(startvertex, sinkVertex);
        int x = pairList1.lastIndexOf(startingEdge);
        Collections.swap(pairList1, 0, x);


        Iterator<MutablePair<V, V>> pairIterator = pairList1.iterator();
        int i = 0;


        while (pairIterator.hasNext()
        ) {

            MutablePair<V, V> edgePair = pairIterator.next();
            pairList.add(edgePair);
            List<E> face = new ArrayList<>();

            PlanarGraphFace<V, E> faceObj = new PlanarGraphFace<V, E>(Integer.toString(i++));
            adjVertices.put(faceObj, new ArrayList<>());
            planarGraphFaces.add(faceObj);


            V startVertex = edgePair.getLeft();
            List<E> tArrayList = (ArrayList<E>) embedding2.getEdgesAround(startVertex);
            //     E edge = tArrayList.get(0);
            V vertex = startVertex;
            V nextVertex = edgePair.getRight();
            pairList1.remove(edgePair);

            edge = embeddingGraphAsUndirectred.getEdge(edgePair.getLeft(), edgePair.getRight());
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
                edgePair = new Tuple<V, V>(vertex, nextVertex);
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


        List<MutablePair<V, V>> pairList = new ArrayList<>(pairIntegerMap.keySet());

        MutablePair<V, V> startingEdge = new Tuple<>(startvertex, sinkVertex);
        int x = pairList.lastIndexOf(startingEdge);
        Collections.swap(pairList, 0, x);

        LinkedHashMap<MutablePair<V, V>, Boolean> pairBooleanHashtable = new LinkedHashMap<>();
        for (MutablePair<V, V> pair :
                pairList) {
            pairBooleanHashtable.put(pair, false);
        }
        int i = 0;

        for (MutablePair<V, V> pair :
                pairBooleanHashtable.keySet()) {
            if (pairBooleanHashtable.get(pair) == false) {
                List<V> face = new ArrayList<>();
                List<MutablePair<V, V>> edgeList = new ArrayList<>();
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
                    MutablePair<V, V> vvPair = new Tuple<>(temp, nextVertex);
                    vertex = temp;
                    adjFaces2.put(vvPair, faceObj);
                    faceObj.getOrthogonalRep().put(vvPair, 999);


                    pairBooleanHashtable.put(vvPair, true);
                    //.add(edgePair);
                    edgeList.add(vvPair);
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
        for (DefaultWeightedEdge edge :
                networkGraph.edgeSet()) {


        }
        ;

        MinimumCostFlowAlgorithm.MinimumCostFlow<DefaultWeightedEdge> minimumCostFlow =
                minimumCostFlowAlgorithm.getMinimumCostFlow(problem);


    }


}
