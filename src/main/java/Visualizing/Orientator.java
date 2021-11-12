package Visualizing;

import Datatypes.PlanarGraphFace;
import Datatypes.Vertex;
import Datatypes.TupleEdge;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Orientator<E> {


    List<PlanarGraphFace<Vertex, DefaultEdge>> originalFaceList = new ArrayList<>();
    PlanarGraphFace<Vertex, DefaultEdge> outerFace;
    HashMap<TupleEdge<Vertex, Vertex>, PlanarGraphFace<Vertex, E>> edgeFaceNeighbourMap;


    public Orientator(HashMap<PlanarGraphFace<Vertex, DefaultEdge>, PlanarGraphFace<Vertex, DefaultEdge>> rectangularFaceMap, PlanarGraphFace<Vertex, DefaultEdge> outerFace) {

        originalFaceList.addAll(rectangularFaceMap.keySet());
        this.outerFace = outerFace;

    }

    public List<PlanarGraphFace<Vertex, DefaultEdge>> getOriginalFaceList() {
        return originalFaceList;
    }

    public void run() {


        //   outerFace.setOrientations();
        List<PlanarGraphFace<Vertex, DefaultEdge>> undiscoveredFaces = new ArrayList<>(originalFaceList);
        List<PlanarGraphFace<Vertex, E>> discoveredFaces = new ArrayList<>();
        edgeFaceNeighbourMap = new HashMap<>();
        HashMap<PlanarGraphFace, Boolean> visitedMap = new HashMap<>();


        visitedMap.put(outerFace, true);

        for (TupleEdge<Vertex, Vertex> edge : outerFace.getEdgeList()
        ) {
            edgeFaceNeighbourMap.put(edge, (PlanarGraphFace<Vertex, E>) outerFace);
        }

        for (PlanarGraphFace<Vertex, DefaultEdge> face : originalFaceList
        ) {
            for (TupleEdge<Vertex, Vertex> edge : face.getEdgeList()
            ) {
                edgeFaceNeighbourMap.put(edge, (PlanarGraphFace<Vertex, E>) face);
            }

        }


        PlanarGraphFace<Vertex, DefaultEdge> currentFace;

        // äußere Facette
        for (TupleEdge<Vertex, Vertex> edge : outerFace.getEdgeList()
        ) {
            TupleEdge<Vertex, Vertex> reverseEdge = new TupleEdge<>(edge.getRight(), edge.getLeft());
            PlanarGraphFace<Vertex, E> face = edgeFaceNeighbourMap.get(reverseEdge);
            assert (face != null);
            if (visitedMap.get(face) == null) {
                visitedMap.putIfAbsent(face, true);
                face.setOrientations(reverseEdge, (outerFace.getEdgeOrientationMap().get(edge)));
                discoveredFaces.add(face);
            }
        }

        int i = 0;

        // innere Facetten
        while (discoveredFaces.size() > 0) {

            PlanarGraphFace<Vertex, E> currFace = discoveredFaces.get(0);
            discoveredFaces.remove(0);
            for (TupleEdge<Vertex, Vertex> edge : currFace.getEdgeList()
            ) {
                TupleEdge<Vertex, Vertex> reverseEdge = new TupleEdge<>(edge.getRight(), edge.getLeft());
                PlanarGraphFace<Vertex, E> face = edgeFaceNeighbourMap.get(reverseEdge);
                assert (face != null);
                if (visitedMap.get(face) == null) {
                    visitedMap.putIfAbsent(face, true);
                    face.setOrientations(reverseEdge, ((currFace.getEdgeOrientationMap().get(edge) + 2) % 4));
                    discoveredFaces.add(face);
                }
            }

        }


        System.out.println("Hello");

    }


}
