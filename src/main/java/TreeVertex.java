import org.jbpt.hypergraph.abs.Vertex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.function.Supplier;

public class TreeVertex extends Vertex implements Serializable {

    public static Supplier<TreeVertex> getvSupplier = new Supplier<>() {
        private int id = 0;

        @Override
        public TreeVertex get() {

            return new TreeVertex("v" + id++);
        }
    };

    static int counter = 0;
    int depth;
    TreeVertex parent;
    boolean dummy = false;
    String name = super.getName();
    ArrayList<TreeVertex> adjecentVertices = new ArrayList<>();
    int id;

    public TreeVertex(String name) {
        super(name);
        this.depth = 0;
        id = counter++;
    }

    public TreeVertex(String name, boolean dummy) {
        super(name);
        this.depth = 0;
        this.dummy = dummy;
        id = counter++;
    }

    public TreeVertex(String name, TreeVertex parent) {
        super(name);
        this.parent = parent;
        this.depth = 0;
        id = counter++;
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

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + id;
        return result;
    }


}
