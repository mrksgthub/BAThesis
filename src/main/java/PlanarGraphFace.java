
import org.apache.commons.lang3.tuple.MutablePair;
import org.jbpt.hypergraph.abs.Vertex;

import java.util.*;
import java.util.function.Supplier;

public class PlanarGraphFace<V, E> extends TreeVertex {

    Set<MutablePair<V, V>> pairSet = new HashSet<>();
    Set<V> vSet = new HashSet<>();
    Set<E> edgeSet = new HashSet<>();
    Map<MutablePair<V, V>, Integer> orthogonalRep = new LinkedHashMap<>();
    Map<MutablePair<V, V>, Integer> edgeOrientationMap = new LinkedHashMap<>();
    Map<Integer, ArrayList<MutablePair<V, V>>> sidesMap = new LinkedHashMap<>();
    List<MutablePair<V, V>> edgeList = new ArrayList<>();
    FaceType type = FaceType.INTERAL;

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

    public PlanarGraphFace() {

        super(Integer.toString(new Random().nextInt(500)));
        sidesMap.put(0, new ArrayList<>());
        sidesMap.put(1, new ArrayList<>());
        sidesMap.put(2, new ArrayList<>());
        sidesMap.put(3, new ArrayList<>());




    }

    public PlanarGraphFace(String name) {
        super(name);
        sidesMap.put(0, new ArrayList<>());
        sidesMap.put(1, new ArrayList<>());
        sidesMap.put(2, new ArrayList<>());
        sidesMap.put(3, new ArrayList<>());


    }

    public PlanarGraphFace(String name, TreeVertex parent) {
        super(name, parent);
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

    public static Supplier<TreeVertex> getvSupplier = new Supplier<TreeVertex>() {
        private int id = 0;

        @Override
        public TreeVertex get() {

            return new TreeVertex("v" + id++);
        }
    };


    public void setOrientations() {
        int[] orientations = {0, 1, 2, 3, 1, 2, 3, 0};
        int counter = 0;
        for (MutablePair<V, V> edge : edgeList
        ) {
            if (counter >= 0) {
                getEdgeOrientationMap().put(edge, orientations[(counter % 4)]);
                sidesMap.get(orientations[(counter % 4)]).add(edge);
            } else {
                getEdgeOrientationMap().put(edge, orientations[(counter % 4) + 7]);
                sidesMap.get(orientations[(counter % 4)+7]).add(edge);
            }


            counter += orthogonalRep.get(edge);
        }


    }


    enum FaceType {INTERAL, EXTERNAL}






}



