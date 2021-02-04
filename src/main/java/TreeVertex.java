import org.antlr.v4.runtime.tree.Tree;
import org.jbpt.hypergraph.abs.Vertex;

import java.util.List;
import java.util.function.Supplier;

public class TreeVertex extends Vertex

{

    int depth;
    TreeVertex parent;
    List<TreeVertex> children;
    TreeVertex firstVertexInEdge;
    TreeVertex secondVertexInEdge;
    boolean visited = false;
    String realName;

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public TreeVertex getFirstVertexInEdge() {
        return firstVertexInEdge;
    }

    public void setFirstVertexInEdge(TreeVertex firstVertexInEdge) {
        this.firstVertexInEdge = firstVertexInEdge;
    }

    public TreeVertex getSecondVertexInEdge() {
        return secondVertexInEdge;
    }

    public void setSecondVertexInEdge(TreeVertex secondVertexInEdge) {
        this.secondVertexInEdge = secondVertexInEdge;
    }

    public TreeVertex(String name) {
        super(name);
        this.depth = 0;
    }

    public TreeVertex(String name, TreeVertex parent) {
        super(name);
        this.parent = parent;
        this.depth = 0;
    }

    public List<TreeVertex> getChildren() {
        return children;
    }

    public void setChildren(List<TreeVertex> children) {
        this.children = children;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public TreeVertex getParent() {
        return parent;
    }

    public void setParent(TreeVertex parent) {
        this.parent = parent;
    }



    public static Supplier<TreeVertex> getvSupplier = new Supplier<TreeVertex>() {
        private int id = 0;

        @Override
        public TreeVertex get() {

            return new TreeVertex("v" + id++);
        }
    };










}
