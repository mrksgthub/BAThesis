import org.apache.commons.lang3.tuple.MutablePair;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Orientator<E> {


    List<PlanarGraphFace<TreeVertex, DefaultEdge>> originalFaceList = new ArrayList<>();
    PlanarGraphFace<TreeVertex, DefaultEdge> outerFace;
    HashMap<MutablePair<TreeVertex, TreeVertex>, PlanarGraphFace<TreeVertex, E>> edgeFaceNeighbourMap;


    public Orientator(HashMap<PlanarGraphFace<TreeVertex, DefaultEdge>, PlanarGraphFace<TreeVertex, DefaultEdge>> rectangularFaceMap, PlanarGraphFace<TreeVertex, DefaultEdge> outerFace) {

        originalFaceList.addAll(rectangularFaceMap.keySet());
        this.outerFace = outerFace;

    }


    public void run() {



        //   outerFace.setOrientations();
        List<PlanarGraphFace<TreeVertex, DefaultEdge>> undiscoveredFaces = new ArrayList<>(originalFaceList);
        List<PlanarGraphFace<TreeVertex, E>> discoveredFaces = new ArrayList<>();
        edgeFaceNeighbourMap = new HashMap<>();
        HashMap<PlanarGraphFace, Boolean> visitedMap = new HashMap<>();


        visitedMap.put(outerFace, true);

        for (MutablePair<TreeVertex, TreeVertex> edge : outerFace.getEdgeList()
        ) {
            edgeFaceNeighbourMap.put(edge, (PlanarGraphFace<TreeVertex, E>) outerFace);
        }

        for (PlanarGraphFace<TreeVertex, DefaultEdge> face : originalFaceList
        ) {
            for (MutablePair<TreeVertex, TreeVertex> edge : face.getEdgeList()
            ) {
                edgeFaceNeighbourMap.put(edge, (PlanarGraphFace<TreeVertex, E>) face);
            }

        }


        PlanarGraphFace<TreeVertex, DefaultEdge> currentFace;

        for (MutablePair<TreeVertex, TreeVertex> edge : outerFace.getEdgeList()
        ) {
            MutablePair<TreeVertex, TreeVertex> reverseEdge = new Tuple<>(edge.getRight(), edge.getLeft());
            PlanarGraphFace<TreeVertex, E> face = edgeFaceNeighbourMap.get(reverseEdge);
            assert (face != null);
            if (visitedMap.get(face) == null) {
                visitedMap.putIfAbsent(face, true);
                face.setOrientations(reverseEdge, (outerFace.getEdgeOrientationMap().get(edge)));
                discoveredFaces.add(face);
            }
        }

        int i = 0;

        while (discoveredFaces.size() > 0) {


            PlanarGraphFace<TreeVertex, E> currFace = discoveredFaces.get(0);
            discoveredFaces.remove(0);
            for (MutablePair<TreeVertex, TreeVertex> edge : currFace.getEdgeList()
            ) {

                MutablePair<TreeVertex, TreeVertex> reverseEdge = new Tuple<>(edge.getRight(), edge.getLeft());
                PlanarGraphFace<TreeVertex, E> face = edgeFaceNeighbourMap.get(reverseEdge);
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
