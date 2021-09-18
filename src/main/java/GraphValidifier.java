import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.HashMap;
import java.util.List;

public class GraphValidifier {


    private DirectedMultigraph<TreeVertex, DefaultEdge> constructedGraph;
    private List<PlanarGraphFace<TreeVertex, DefaultEdge>> planarGraphFaces;

    public GraphValidifier(DirectedMultigraph<TreeVertex, DefaultEdge> constructedGraph, List<PlanarGraphFace<TreeVertex, DefaultEdge>> planarGraphFaces) {
        this.constructedGraph = constructedGraph;
        this.planarGraphFaces = planarGraphFaces;
    }


    public void run() throws Exception {

        //TODO Prüfe: 1. Jeder Knoten ist genau Teil von 2 Faces
        // Maximaler Knotengrad = 4 und minimaler Grad ist 2

        for (TreeVertex vertex : constructedGraph.vertexSet()
        ) {
            int i = constructedGraph.degreeOf(vertex);
            if (i > 4 || 2 > i) {
                System.out.println("Zu viele Knoten");
                throw new Exception("Illegal Graph: maxDegree of nodes = " + i);
            }
        }

        HashMap<Object, Integer> vertexMap = new HashMap<>();
        HashMap<Object, Object> EdgeMap = new HashMap<>();

        for (PlanarGraphFace face : planarGraphFaces
        ) {
            for ( Object edge :  face.orthogonalRep.keySet()
                    ) {
                TupleEdge<TreeVertex, TreeVertex> tuple = (TupleEdge<TreeVertex, TreeVertex>) edge;

                if (vertexMap.putIfAbsent( tuple.left, 1) != null) {
                    vertexMap.put(tuple.left, vertexMap.get(tuple.left)+1);
                }
                if (EdgeMap.get(tuple.left) == face) {
                    throw new Exception("Vertex appears twice in edgelist of a face");
                }

            }
        }
        for (TreeVertex vertex :
                constructedGraph.vertexSet()) {
           int value = vertexMap.get(vertex);

            if (value < 2 || value > 4 || value != constructedGraph.degreeOf(vertex)) {
                throw new Exception("AHHH");
            }
        }
    }




}













