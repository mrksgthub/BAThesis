public class SPQQNode extends SPQNode{
    NodeTypesEnum.NODETYPE nodeType = NodeTypesEnum.NODETYPE.Q;

    public SPQQNode(String q) {
        super(q);
    }

    @Override
    public NodeTypesEnum.NODETYPE getNodeType() {
        return nodeType;
    }

    @Override
    public void setNodeType(NodeTypesEnum.NODETYPE nodeType) {
        this.nodeType = nodeType;
    }
}

