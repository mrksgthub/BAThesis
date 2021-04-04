import org.antlr.v4.runtime.tree.Tree;
import org.jbpt.hypergraph.abs.Vertex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TreeVertex extends Vertex implements Serializable

{

    public static Supplier<TreeVertex> getvSupplier = new Supplier<TreeVertex>() {
        private int id = 0;

        @Override
        public TreeVertex get() {

            return new TreeVertex("v" + id++);
        }
    };
    int depth;
    TreeVertex parent;
    List<TreeVertex> children;
    TreeVertex firstVertexInEdge;
    TreeVertex secondVertexInEdge;
    boolean visited = false;
    boolean dummy = false;
    String realName;
    int numbering;
    int leftAngle;
    int rightAngle;
    String name = super.getName();
    ArrayList<TreeVertex> adjecentVertices = new ArrayList<>();

    public TreeVertex(String name) {
        super(name);
        this.depth = 0;
    }

    public TreeVertex(String name, boolean dummy) {
        super(name);
        this.depth = 0;
        this.dummy = dummy;
    }



    public TreeVertex(String name, TreeVertex parent) {
        super(name);
        this.parent = parent;
        this.depth = 0;
    }

    public boolean isDummy() {
        return dummy;
    }

    public void setDummy(boolean dummy) {
        this.dummy = dummy;
    }

    public int getLeftAngle() {
        return leftAngle;
    }

    public void setLeftAngle(int leftAngle) {
        this.leftAngle = leftAngle;
    }

    public int getRightAngle() {
        return rightAngle;
    }

    public void setRightAngle(int rightAngle) {
        this.rightAngle = rightAngle;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

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

    public String getName() {
        if (super.getName().length() != 0) {

            return super.getName();
        } else {
            return name;
        }



    }







}
