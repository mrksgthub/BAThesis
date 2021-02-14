import java.util.ArrayList;
import java.util.List;

public class DFSNode extends TreeVertex {

    DFSNode parent;
    List<DFSNode> children = new ArrayList<>();
    List<DFSNode> backEdges = new ArrayList<>();


    public DFSNode(String name) {
        super(name);
    }
}
