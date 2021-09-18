import org.apache.commons.lang3.tuple.MutablePair;

import java.util.*;
import java.util.function.Supplier;

public class PlanarGraphFace<V, E> extends TreeVertex {

    public static Supplier<TreeVertex> getvSupplier = new Supplier<TreeVertex>() {
        private int id = 0;

        @Override
        public TreeVertex get() {

            return new TreeVertex("v" + id++);
        }
    };

    static int faceCounter = 0;
    Set<MutablePair<V, V>> pairSet = new HashSet<>();
    Set<V> vSet = new HashSet<>();
    Set<E> edgeSet = new HashSet<>();
    Map<MutablePair<V, V>, Integer> orthogonalRep = new LinkedHashMap<>();
    Map<MutablePair<V, V>, Integer> edgeOrientationMap = new LinkedHashMap<>();
    Map<Integer, ArrayList<MutablePair<V, V>>> sidesMap = new LinkedHashMap<>();
    List<MutablePair<V, V>> edgeList = new ArrayList<>();
    FaceType type = FaceType.INTERNAL;
    int faceIndex;

    public PlanarGraphFace() {
        super(Integer.toString(new Random().nextInt(500)));
        faceIndex = faceCounter++;
        sidesMap.put(0, new ArrayList<>());
        sidesMap.put(1, new ArrayList<>());
        sidesMap.put(2, new ArrayList<>());
        sidesMap.put(3, new ArrayList<>());


    }

    public PlanarGraphFace(String name) {
        super(name);
        faceIndex = faceCounter++;
        sidesMap.put(0, new ArrayList<>());
        sidesMap.put(1, new ArrayList<>());
        sidesMap.put(2, new ArrayList<>());
        sidesMap.put(3, new ArrayList<>());


    }

    public PlanarGraphFace(String name, TreeVertex parent) {
        super(name, parent);
        faceIndex = faceCounter++;
    }

    public FaceType getType() {
        return type;
    }

    public void setType(FaceType type) {
        this.type = type;
    }

    public List<MutablePair<V, V>> getEdgeList() {
        return edgeList;
    }

    public void setEdgeList(List<MutablePair<V, V>> edgeList) {
        this.edgeList = edgeList;
    }

    public Map<MutablePair<V, V>, Integer> getEdgeOrientationMap() {
        return edgeOrientationMap;
    }

    public void setEdgeOrientationMap(Map<MutablePair<V, V>, Integer> edgeOrientationMap) {
        this.edgeOrientationMap = edgeOrientationMap;
    }

    public Map<MutablePair<V, V>, Integer> getOrthogonalRep() {
        return orthogonalRep;
    }

    public void setOrthogonalRep(Map<MutablePair<V, V>, Integer> orthogonalRep) {
        this.orthogonalRep = orthogonalRep;
    }

    public Set<MutablePair<V, V>> getPairSet() {
        return pairSet;
    }

    public void setPairSet(Set<MutablePair<V, V>> pairSet) {
        this.pairSet = pairSet;
    }

    public Set<V> getvSet() {
        return vSet;
    }

    public void setvSet(Set<V> vSet) {
        this.vSet = vSet;
    }

    public Set<E> getEdgeSet() {
        return edgeSet;
    }

    public void setEdgeSet(Set<E> edgeSet) {
        this.edgeSet = edgeSet;
    }


    /**
     * Legt Orientierung für das äuere Face fest. Dabei wird einer der Kanten die Orientierung 0 zugewiesen und davon
     * ausgehend der Rest.
     *
     */
    public void setOrientations() {

        sidesMap.put(0, new ArrayList<>());
        sidesMap.put(1, new ArrayList<>());
        sidesMap.put(2, new ArrayList<>());
        sidesMap.put(3, new ArrayList<>());


        for (ArrayList<MutablePair<V, V>> arr : sidesMap.values()) {
            assert (arr.size() == 0);
        }

        int[] orientations = {0, 1, 2, 3, 1, 2, 3, 0};
        int counter = 0;
        for (MutablePair<V, V> edge : edgeList
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


        for (ArrayList<MutablePair<V, V>> arr : sidesMap.values()) {

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
     * Legt Orientierungen einer Facette bei der die Orientierung von edge festgelegt ist.
     * @param edge - edge in der Facette
     * @param orientation - festgelegte Orientierung der edge in der Facette
     */
    public void setOrientations(MutablePair<V, V> edge, int orientation) {

        sidesMap.put(0, new ArrayList<>());
        sidesMap.put(1, new ArrayList<>());
        sidesMap.put(2, new ArrayList<>());
        sidesMap.put(3, new ArrayList<>());


        for (ArrayList<MutablePair<V, V>> arr : sidesMap.values()) {
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


        for (ArrayList<MutablePair<V, V>> arr : sidesMap.values()) {

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


    enum FaceType {INTERNAL, EXTERNAL, EXTERNAL_PROCESSED}


}



