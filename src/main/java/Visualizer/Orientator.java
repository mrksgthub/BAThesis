package Visualizer;

import Datastructures.PlanarGraphFace;
import Datastructures.Vertex;
import Datastructures.TupleEdge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Orientator {


    private List<PlanarGraphFace<Vertex>> orientatedInnerFaces;
    PlanarGraphFace<Vertex> orientatedOuterFace;
    private HashMap<TupleEdge<Vertex, Vertex>, PlanarGraphFace<Vertex>> edgeFaceNeighbourMap;


    public Orientator(List<PlanarGraphFace<Vertex>> rectangularInnerFaceMap, PlanarGraphFace<Vertex> outerFace) {

      //  originalFaceList.addAll(rectangularInnerFaceMap.keySet());



    }


    public List<PlanarGraphFace<Vertex>> getOrientatedInnerFaces() {
        return orientatedInnerFaces;
    }

    /**
     * Legt die Orientierung der Kannten in PlanarGraphFace<Vertex> Objekten fest. In sidesMap und edgeOrientationMap
     *
     * @param rectangularOuterFace - die rechteckige äußere Facette.
     * @param rectangularInnerFaces - die rechteckige inneren Facetten.
     */
    public void run(PlanarGraphFace<Vertex> rectangularOuterFace, List<PlanarGraphFace<Vertex>> rectangularInnerFaces) {


        rectangularOuterFace.setOrientationsOuterFacette();
        List<PlanarGraphFace<Vertex>> undiscoveredFaces = new ArrayList<>(rectangularInnerFaces);
        List<PlanarGraphFace<Vertex>> discoveredFaces = new ArrayList<>();
        edgeFaceNeighbourMap = new HashMap<>();
        HashMap<PlanarGraphFace<Vertex>, Boolean> visitedMap = new HashMap<>();


        visitedMap.put(rectangularOuterFace, true);

        for (TupleEdge<Vertex, Vertex> edge : rectangularOuterFace.getEdgeList()
        ) {
            edgeFaceNeighbourMap.put(edge,  rectangularOuterFace);
        }

        for (PlanarGraphFace<Vertex> face : rectangularInnerFaces
        ) {
            for (TupleEdge<Vertex, Vertex> edge : face.getEdgeList()
            ) {
                edgeFaceNeighbourMap.put(edge,  face);
            }
        }

        PlanarGraphFace<Vertex> currentFace;

        // äußere Facette
        for (TupleEdge<Vertex, Vertex> edge : rectangularOuterFace.getEdgeList()
        ) {
            TupleEdge<Vertex, Vertex> reverseEdge = new TupleEdge<>(edge.getRight(), edge.getLeft());
            PlanarGraphFace<Vertex> face = edgeFaceNeighbourMap.get(reverseEdge);
            assert (face != null);
            if (visitedMap.get(face) == null) {
                visitedMap.putIfAbsent(face, true);
                face.setOrientationsInnerFace(reverseEdge, (rectangularOuterFace.getEdgeOrientationMap().get(edge)));
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
                    face.setOrientationsInnerFace(reverseEdge, ((currFace.getEdgeOrientationMap().get(edge) + 2) % 4));
                    discoveredFaces.add(face);
                }
            }

        }


        System.out.println("Hello");

    }


}
