import org.antlr.v4.runtime.misc.Pair;
import org.jbpt.hypergraph.abs.Vertex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

public class PlanarGraphFace<V,E> extends TreeVertex {

    Set<Pair<V, V>> pairSet = new HashSet<Pair<V,V>>();
    Set<V> vSet = new HashSet<>();
    Set<E> edgeSet = new HashSet<>();


    public PlanarGraphFace() {

        super(Integer.toString(new Random().nextInt(500)));
    }
    public PlanarGraphFace(String name) {
        super(name);
    }

    public PlanarGraphFace(String name, TreeVertex parent) {
        super(name, parent);
    }


    public Set<Pair<V, V>> getPairSet() {
        return pairSet;
    }

    public void setPairSet(Set<Pair<V, V>> pairSet) {
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
