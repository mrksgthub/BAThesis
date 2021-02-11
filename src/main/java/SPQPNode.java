public class SPQPNode extends SPQNode


{

    NodeTypesEnum.NODETYPE nodeType = NodeTypesEnum.NODETYPE.P;


    public SPQPNode(String name) {
        super(name);
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
