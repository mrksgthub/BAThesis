package PlanarityAndAngles;

import Datastructures.PlanarGraphFace;
import Datastructures.TupleEdge;
import Datastructures.Vertex;
import org.jgrapht.graph.DirectedMultigraph;

import java.io.Serializable;
import java.util.*;

/**
 * Implementiert die Erzeugung der Facetten. Nutzt dabei eine Einbettung eines Graphen in der Form eines DirectedMultigraph
 * Objektes von JGraphT.
 *
 * @param <V> Klasse von Knoten
 * @param <E> Eine Kante, welche ein Objekt eine JGraphT-Kante sein.
 */
public class FaceGenerator<V extends Vertex, E> implements Serializable {

    private final List<TupleEdge<V, V>> pairList;
    private final List<PlanarGraphFace<V>> planarGraphFaces = new ArrayList<>();
    private final HashMap<PlanarGraphFace<V>, ArrayList<V>> adjVertices = new HashMap<>();
    private final HashMap<TupleEdge<V, V>, PlanarGraphFace<V>> tupleToFaceMap = new HashMap<>();
    private final V startvertex;
    private final V sinkVertex;

    public FaceGenerator(DirectedMultigraph<V, E> graph, V startvertex, V sinkVertex) {

        this.startvertex = startvertex;
        this.sinkVertex = sinkVertex;
        pairList = new ArrayList<>();
        for (E edge : graph.edgeSet()
        ) {
            pairList.add(new TupleEdge<>(graph.getEdgeSource(edge), graph.getEdgeTarget(edge)));
            pairList.add(new TupleEdge<>(graph.getEdgeTarget(edge), graph.getEdgeSource(edge)));

        }

    }

    public List<PlanarGraphFace<V>> getPlanarGraphFaces() {
        return planarGraphFaces;
    }

    public HashMap<TupleEdge<V, V>, PlanarGraphFace<V>> getTupleToFaceMap() {
        return tupleToFaceMap;
    }

    public void generateFaces() { // läuft im Moment "rückwärts" von daher hat das äußere Face sink -> source als Ausgangsvertex

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

                PlanarGraphFace<V> faceObj = new PlanarGraphFace<>(Integer.toString(i++));
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
                tupleToFaceMap.put(pair, faceObj); // HashMap, um das Facettenobjekt zu finden.
                faceObj.setEdgeAngle(pair, 999); // Initialisiere mit einem illegalen Winkel (für debugging).


                // Bestimmung einer Facette: Nachdem man ein Startknoten gefunden hat, sucht man ab jetzt immer den Folgeknoten bis man wieder am Startknoten ankommt
                while (nextVertex != startVertex) {

                    adjVertices.get(faceObj).add(nextVertex);
                    tArrayList = (List<V>) nextVertex.getAdjacentVertices();
                    V temp = nextVertex;
                    nextVertex = tArrayList.get(Math.floorMod((tArrayList.indexOf(vertex) - 1), tArrayList.size()));
                    TupleEdge<V, V> vvPair = new TupleEdge<>(temp, nextVertex);
                    vertex = temp;
                    tupleToFaceMap.put(vvPair, faceObj);
                    faceObj.setEdgeAngle(vvPair, 999);
                    tupleVisitedMap.put(vvPair, true);
                    edgeList.add(vvPair);
                    face.add(nextVertex);


                }
            }

        }


    }


}
