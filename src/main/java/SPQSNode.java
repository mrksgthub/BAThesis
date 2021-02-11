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
}


