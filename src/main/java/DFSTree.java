import org.jgrapht.graph.DefaultDirectedGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DFSTree<V extends TreeVertex, E> extends DefaultDirectedGraph {

    V source;
    V sink;

    private List<E> treeEdges;

    private List<E> backEdges;



    public DFSTree(Supplier vertexSupplier, Supplier edgeSupplier, boolean weighted) {
        super(vertexSupplier, edgeSupplier, weighted);
    }

    public DFSTree(Class edgeClass, V sink) {
        super(edgeClass);
        this.sink = sink;
        this.treeEdges = new ArrayList<E>();
        this.backEdges = new ArrayList<E>();
    }































}
