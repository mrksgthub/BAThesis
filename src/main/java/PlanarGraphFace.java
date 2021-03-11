
import org.apache.commons.lang3.tuple.MutablePair;
import org.jbpt.hypergraph.abs.Vertex;

import java.util.*;
import java.util.function.Supplier;

public class PlanarGraphFace<V,E> extends TreeVertex {

    Set<MutablePair<V, V>> pairSet = new HashSet<>();
    Set<V> vSet = new HashSet<>();
    Set<E> edgeSet = new HashSet<>();
    Map<MutablePair<V,V>, Integer> orthogonalRep= new LinkedHashMap<>();
    Map<MutablePair<V,V>, Integer> edgeOrientationMap= new LinkedHashMap<>();

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
    }
    public PlanarGraphFace(String name) {
        super(name);
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
        int counter =0;
        for (MutablePair<V,V> edge: orthogonalRep.keySet()
             ) {
            if (counter >= 0) {
                getEdgeOrientationMap().put(edge, orientations[(counter % 4)]);
            } else {
                getEdgeOrientationMap().put(edge, orientations[(counter % 4)+7]);
            }


            counter += orthogonalRep.get(edge);
        }










    }





}



