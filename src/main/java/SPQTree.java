import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultUndirectedGraph;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class SPQTree<V,E> extends DefaultUndirectedGraph<V,E> {


    V root;

    public SPQTree(Supplier<V> vSupplier, Supplier<E> defaultEdgeSupplier, boolean b) {
        super(vSupplier, defaultEdgeSupplier, b);
    }

    public V getRoot() {
        return root;
    }

    public void setRoot(V root) {
        this.root = root;
    }

    public SPQTree(Class<? extends E> edgeClass) {
        super(edgeClass);
    }


    public void addChildren(V toBeParent, V child){
        if(!this.containsVertex(toBeParent)){
            throw new IllegalArgumentException("Parent not in Tree");
        }

        this.addVertex(child);
        this.addEdge(toBeParent,child);



    }

    public void addChildren(SPQTree<V,E> tree, V toBeParent){
        if(!this.containsVertex(toBeParent)){
            throw new IllegalArgumentException("Parent not in Tree");
        }

        Graphs.addGraph(this, tree);
        this.addEdge(toBeParent, tree.getRoot());




    }

    public void determineRoot(){

        List<V> linkedList = new LinkedList<V>();
        // TODO Shuffling required?



        for (V v: this.vertexSet()
             ) {
            if(this.outDegreeOf(v) > 1) {
                this.root = v;
                return;
            }
        }




    }






}
