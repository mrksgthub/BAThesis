package Visualizing;

import Datatypes.PlanarGraphFace;
import Datatypes.Vertex;
import Datatypes.TupleEdge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Orientator<E> {


    List<PlanarGraphFace<Vertex>> orientatedInnerFaces = new ArrayList<>();
    PlanarGraphFace<Vertex> orientatedOuterFace;
    private HashMap<TupleEdge<Vertex, Vertex>, PlanarGraphFace<Vertex>> edgeFaceNeighbourMap;


    public Orientator(List<PlanarGraphFace<Vertex>> rectangularInnerFaceMap, PlanarGraphFace<Vertex> outerFace) {

      //  originalFaceList.addAll(rectangularInnerFaceMap.keySet());

        orientatedInnerFaces = rectangularInnerFaceMap;
        this.orientatedOuterFace = outerFace;

    }


    public List<PlanarGraphFace<Vertex>> getOrientatedInnerFaces() {
        return orientatedInnerFaces;
    }

    public void run() {


        orientatedOuterFace.setOrientationsOuterFacette();
        List<PlanarGraphFace<Vertex>> undiscoveredFaces = new ArrayList<>(orientatedInnerFaces);
        List<PlanarGraphFace<Vertex>> discoveredFaces = new ArrayList<>();
        edgeFaceNeighbourMap = new HashMap<>();
        HashMap<PlanarGraphFace, Boolean> visitedMap = new HashMap<>();


        visitedMap.put(orientatedOuterFace, true);

        for (TupleEdge<Vertex, Vertex> edge : orientatedOuterFace.getEdgeList()
        ) {
            edgeFaceNeighbourMap.put(edge, (PlanarGraphFace<Vertex>) orientatedOuterFace);
        }

        for (PlanarGraphFace<Vertex> face : orientatedInnerFaces
        ) {
            for (TupleEdge<Vertex, Vertex> edge : face.getEdgeList()
            ) {
                edgeFaceNeighbourMap.put(edge, (PlanarGraphFace<Vertex>) face);
            }

        }


        PlanarGraphFace<Vertex> currentFace;

        // äußere Facette
        for (TupleEdge<Vertex, Vertex> edge : orientatedOuterFace.getEdgeList()
        ) {
            TupleEdge<Vertex, Vertex> reverseEdge = new TupleEdge<>(edge.getRight(), edge.getLeft());
            PlanarGraphFace<Vertex> face = edgeFaceNeighbourMap.get(reverseEdge);
            assert (face != null);
            if (visitedMap.get(face) == null) {
                visitedMap.putIfAbsent(face, true);
                face.setOrientations(reverseEdge, (orientatedOuterFace.getEdgeOrientationMap().get(edge)));
                discoveredFaces.add(face);
            }
        }

        // innere Facetten
        while (discoveredFaces.size() > 0) {

            PlanarGraphFace<Vertex> currFace = discoveredFaces.get(0);
            discoveredFaces.remove(0);
            for (TupleEdge<Vertex, Vertex> edge : currFace.getEdgeList()
            ) {
                TupleEdge<Vertex, Vertex> reverseEdge = new TupleEdge<>(edge.getRight(), edge.getLeft());
                PlanarGraphFace<Vertex> face = edgeFaceNeighbourMap.get(reverseEdge);
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
