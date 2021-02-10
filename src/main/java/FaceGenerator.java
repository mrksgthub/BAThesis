import org.antlr.v4.runtime.misc.Pair;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.PlanarityTestingAlgorithm;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.*;

public class FaceGenerator<V extends TreeVertex, E> {

    List<List<E>> listOfFaces = new ArrayList<>();

    PlanarityTestingAlgorithm.Embedding<V, E> embedding;

    Map<E, Integer> visitsMap = new HashMap<>();

    Map<Pair<V, V>, Integer> pairIntegerMap = new HashMap<>();

    Set<PlanarGraphFace<V, E>> planarGraphFaces = new HashSet<>();

    HashMap<PlanarGraphFace<V,E>, ArrayList<V>> adjVertices = new HashMap<>();

    HashMap<E, ArrayList<PlanarGraphFace<V,E>>> adjFaces = new HashMap<>();
    private DirectedMultigraph<TreeVertex, DefaultEdge> flowNetworkLayout;
    V startvertex;
    AsUndirectedGraph<V, E> embeddingGraphAsUndirectred;


    public FaceGenerator(PlanarityTestingAlgorithm.Embedding<V, E> embedding) {
        this.embedding = embedding;
        embeddingGraphAsUndirectred = new AsUndirectedGraph<>(embedding.getGraph());
        for (E edge : embedding.getGraph().edgeSet()
        ) {
            visitsMap.put(edge, 0);
            pairIntegerMap.put(new Pair<V, V>(embedding.getGraph().getEdgeSource(edge), embedding.getGraph().getEdgeTarget(edge)), 0);
            pairIntegerMap.put(new Pair<V, V>(embedding.getGraph().getEdgeTarget(edge), embedding.getGraph().getEdgeSource(edge)), 0);
            adjFaces.put(edge, new ArrayList<PlanarGraphFace<V, E>>());
        }

    }




    public FaceGenerator(PlanarityTestingAlgorithm.Embedding<V, E> embedding, V startvertex) {
        this(embedding);
        this.startvertex = startvertex;





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

       Pair<V,V> startingEdge =  new Pair<V,V>(startvertex, Graphs.getOppositeVertex(embedding.getGraph(),  embedding.getEdgesAround(startvertex).get(0), startvertex));
        int x = pairList1.lastIndexOf(startingEdge);
        Collections.swap(pairList1, 0, x);


        Iterator<Pair<V, V>> pairIterator = pairList1.iterator();
        int i =0;




        while (pairIterator.hasNext()
        ) {

            Pair<V, V> edgePair = pairIterator.next();
            pairList.add(edgePair);
            List<E> face = new ArrayList<>();

            PlanarGraphFace<V, E> faceObj = new PlanarGraphFace<V,E>(Integer.toString(i++));
            adjVertices.put(faceObj, new ArrayList<>());
            planarGraphFaces.add(faceObj);


            V startVertex = edgePair.a;
            List<E> tArrayList = (ArrayList<E>) embedding.getEdgesAround(startVertex);
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

                tArrayList = embedding.getEdgesAround(nextVertex);
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

    private Graph<TreeVertex, DefaultEdge> generateFlowNetworkLayout() {
        DirectedMultigraph<TreeVertex, DefaultEdge> graph = new DirectedMultigraph<>(DefaultEdge.class);


        for ( TreeVertex nodes: embedding.getGraph().vertexSet()
             ) {
            graph.addVertex(nodes);

        }
        for (PlanarGraphFace<V,E> vePlanarGraphFace : planarGraphFaces
             ) {
            graph.addVertex (vePlanarGraphFace);
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
                graph.addEdge( (TreeVertex) vertex, vePlanarGraphFace);
            }
        }


        return graph;
    }


}
