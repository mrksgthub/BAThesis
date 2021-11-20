package Datatypes;

import java.util.*;

public class PlanarGraphFace<V> extends Vertex {

    private static int faceCounter = 0;
    private Map<TupleEdge<V, V>, Integer> orthogonalRep = new LinkedHashMap<>();
    private Map<TupleEdge<V, V>, Integer> edgeOrientationMap = new LinkedHashMap<>();
    private Map<Integer, ArrayList<TupleEdge<V, V>>> sidesMap = new LinkedHashMap<>();
    private List<TupleEdge<V, V>> edgeList = new ArrayList<>();
    private FaceType type = FaceType.INTERNAL;


    public Map<Integer, ArrayList<TupleEdge<V, V>>> getSidesMap() {
        return sidesMap;
    }

    public PlanarGraphFace(String name) {
        super(name);
        sidesMap.put(0, new ArrayList<>());
        sidesMap.put(1, new ArrayList<>());
        sidesMap.put(2, new ArrayList<>());
        sidesMap.put(3, new ArrayList<>());


    }

    public FaceType getType() {
        return type;
    }

    public void setType(FaceType type) {
        this.type = type;
    }

    public List<TupleEdge<V, V>> getEdgeList() {
        return edgeList;
    }

    public void setEdgeList(List<TupleEdge<V, V>> edgeList) {
        this.edgeList = edgeList;
    }

    public Map<TupleEdge<V, V>, Integer> getEdgeOrientationMap() {
        return edgeOrientationMap;
    }

    public Map<TupleEdge<V, V>, Integer> getOrthogonalRep() {
        return orthogonalRep;
    }

    public int getEdgeAngle(TupleEdge<V,V> edge) throws Exception {

        if (orthogonalRep.containsKey(edge)) {
            return orthogonalRep.get(edge);
        } else {
            throw new Exception("Angle not Set");
        }

    }
    public void setEdgeAngle(TupleEdge<V,V> pair, int angle)  {

        orthogonalRep.put(pair, angle);
        pair.setWinkel(angle);

    }



    /**
     * Legt Orientierung für das äuere Face fest. Dabei wird einer der Kanten die Orientierung 0 zugewiesen und davon
     * ausgehend der Rest.
     *
     */
    public void setOrientationsOuterFacette() {

        sidesMap.put(0, new ArrayList<>());
        sidesMap.put(1, new ArrayList<>());
        sidesMap.put(2, new ArrayList<>());
        sidesMap.put(3, new ArrayList<>());


        for (ArrayList<TupleEdge<V, V>> arr : sidesMap.values()) {
            assert (arr.size() == 0);
        }

        int[] orientations = {0, 1, 2, 3, 1, 2, 3, 0};
        int counter = 0;
        for (TupleEdge<V, V> edge : edgeList
        ) {
            if (counter >= 0) {
                getEdgeOrientationMap().put(edge, orientations[(counter % 4)]);
                sidesMap.get(orientations[(counter % 4)]).add(edge);
            } else {
                getEdgeOrientationMap().put(edge, orientations[(counter % 4) + 7]);
                sidesMap.get(orientations[(counter % 4) + 7]).add(edge);
            }


            counter += orthogonalRep.get(edge);
        }


        for (ArrayList<TupleEdge<V, V>> arr : sidesMap.values()) {

            for (int i = 0; i < arr.size(); i++) {

                if (orthogonalRep.get(arr.get(i)) == -1) {
                    if (i != arr.size() - 1) {
                        Collections.rotate(arr, arr.size() - 1 - i);
                        break;
                    }
                }
            }

        }


    }

    /**
     * Legt Orientierungen einer Facette fest, bei der die Orientierung von edge festgelegt ist.
     * @param edge - edge in der Facette
     * @param orientation - festgelegte Orientierung der edge in der Facette
     */
    public void setOrientations(TupleEdge<V, V> edge, int orientation) {

        sidesMap.put(0, new ArrayList<>());
        sidesMap.put(1, new ArrayList<>());
        sidesMap.put(2, new ArrayList<>());
        sidesMap.put(3, new ArrayList<>());


        for (ArrayList<TupleEdge<V, V>> arr : sidesMap.values()) {
            assert (arr.size() == 0);
        }


        int[] orientations = {0, 1, 2, 3, 1, 2, 3, 0};
        int counter = orientation;
        int startIndex = edgeList.indexOf(edge);


        for (int i = 0; i < edgeList.size(); i++) {

            int index = (startIndex + i) % edgeList.size();
            if (counter >= 0) {
                getEdgeOrientationMap().put(edgeList.get(index), orientations[(counter % 4)]);
                sidesMap.get(orientations[(counter % 4)]).add(edgeList.get(index));
            } else {
                getEdgeOrientationMap().put(edgeList.get(index), orientations[(counter % 4) + 7]);
                sidesMap.get(orientations[(counter % 4) + 7]).add(edgeList.get(index));
            }


            counter += orthogonalRep.get(edgeList.get(index));
        }


        for (ArrayList<TupleEdge<V, V>> arr : sidesMap.values()) {

            for (int i = 0; i < arr.size(); i++) {

                if (orthogonalRep.get(arr.get(i)) == 1) {
                    if (i != arr.size() - 1) {
                        Collections.rotate(arr, arr.size() - 1 - i);
                        break;
                    }
                }
            }

        }


    }

    public enum FaceType {INTERNAL, EXTERNAL, EXTERNAL_PROCESSED}


}



