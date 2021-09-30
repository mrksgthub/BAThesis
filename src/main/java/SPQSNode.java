public class SPQSNode extends SPQNode {

    NodeTypesEnum.NODETYPE nodeType = NodeTypesEnum.NODETYPE.S;

    public SPQSNode() {
        super();
    }


    public SPQSNode(String s) {
        super(s);
    }

    public SPQSNode(TreeVertex edgeSource, TreeVertex edgeTarget) {

        super("S" + edgeSource.getName() + edgeTarget.getName()+ id++);
        this.startVertex = edgeSource;
        this.sinkVertex = edgeTarget;
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
    public boolean calculateRepresentabilityInterval() {
        repIntervalLowerBound = 0;
        repIntervalUpperBound = 0;

        for (SPQNode node : mergedChildren
        ) {
            repIntervalLowerBound += node.getRepIntervalLowerBound();
            repIntervalUpperBound += node.getRepIntervalUpperBound();
        }

        return true;
    }


}


