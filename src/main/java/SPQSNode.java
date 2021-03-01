import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

public class SPQSNode extends SPQNode{

    NodeTypesEnum.NODETYPE nodeType = NodeTypesEnum.NODETYPE.S;

    public SPQSNode() {
        super();
    }


    public SPQSNode(String s) {
        super(s);
    }


    @Override
    public NodeTypesEnum.NODETYPE getNodeType() {
        return nodeType;
    }

    @Override
    public void setNodeType(NodeTypesEnum.NODETYPE nodeType) {
        this.nodeType = nodeType;
    }

    @Override
    public boolean calculateRepresentabilityInterval(DirectedMultigraph<TreeVertex, DefaultEdge> graph, boolean check) {
        for (SPQNode node : mergedChildren
        ) {
            repIntervalLowerBound += node.getRepIntervalLowerBound();
            repIntervalUpperBound += node.getRepIntervalUpperBound();
        }

        return check;
    }







}


