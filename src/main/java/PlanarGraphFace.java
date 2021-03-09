
import org.apache.commons.lang3.tuple.MutablePair;
import org.jbpt.hypergraph.abs.Vertex;

import java.util.*;
import java.util.function.Supplier;

public class PlanarGraphFace<V,E> extends TreeVertex {

    Set<MutablePair<V, V>> pairSet = new HashSet<>();
    Set<V> vSet = new HashSet<>();
    Set<E> edgeSet = new HashSet<>();
    Map<MutablePair<V,V>, Integer> orthogonalRep= new LinkedHashMap<>();

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
}
