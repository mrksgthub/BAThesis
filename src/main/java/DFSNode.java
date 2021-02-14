import java.util.ArrayList;
import java.util.List;

public class DFSNode extends TreeVertex {

    DFSNode parent;
    List<DFSNode> children = new ArrayList<>();
    List<DFSNode> backEdges = new ArrayList<>();
    boolean visible = false;


    public DFSNode(String name) {
        super(name);
    }


    public List<DFSNode> getBackEdges() {
        return backEdges;
    }

    public void setBackEdges(List<DFSNode> backEdges) {
        this.backEdges = backEdges;
    }
}
