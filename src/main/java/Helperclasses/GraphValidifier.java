package Helperclasses;

import Datatypes.PlanarGraphFace;
import Datatypes.Vertex;
import Datatypes.TupleEdge;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.HashMap;
import java.util.List;

public class GraphValidifier {


    private DirectedMultigraph<Vertex, DefaultEdge> constructedGraph;
    private List<PlanarGraphFace<Vertex>> planarGraphFaces;

    public GraphValidifier(DirectedMultigraph<Vertex, DefaultEdge> constructedGraph, List<PlanarGraphFace<Vertex>> planarGraphFaces) {
        this.constructedGraph = constructedGraph;
        this.planarGraphFaces = planarGraphFaces;
    }


    public void run() throws Exception {

        //TODO Prüfe: 1. Jeder Knoten ist genau Teil von 2 Faces
        // Maximaler Knotengrad = 4 und minimaler Grad ist 2

        for (Vertex vertex : constructedGraph.vertexSet()
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
            for ( Object edge :  face.getOrthogonalRep().keySet()
                    ) {
                TupleEdge<Vertex, Vertex> tuple = (TupleEdge<Vertex, Vertex>) edge;

                if (vertexMap.putIfAbsent( tuple.left, 1) != null) {
                    vertexMap.put(tuple.left, vertexMap.get(tuple.left)+1);
                }
                if (EdgeMap.get(tuple.left) == face) {
                    throw new Exception("Vertex appears twice in edgelist of a face");
                }

            }
        }
        for (Vertex vertex :
                constructedGraph.vertexSet()) {
           int value = vertexMap.get(vertex);

            if (value < 2 || value > 4 || value != constructedGraph.degreeOf(vertex)) {
                throw new Exception("AHHH");
            }
        }
    }




}













