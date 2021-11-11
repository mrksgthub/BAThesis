package Datatypes;

import java.util.HashMap;

public class SPQQNode extends SPQNode {
    NodeTypesEnum.NODETYPE nodeType = NodeTypesEnum.NODETYPE.Q;

    public SPQQNode(String q) {
        super(q);
    }

    public SPQQNode(Vertex source, Vertex sink, boolean b) {
        super("Q"+source.getName()+sink.getName()+ id++);

        if (!b) {
            super.setName(source.getName() + sink.getName());
        } else {
            super.setName("QStar" + source.getName() + sink.getName() + id++);
        }

        startVertex = source;
        sinkVertex = sink;
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
        int l = mergedChildren.size();
        if (l == 0) {
            l = 1;
        }
        repIntervalLowerBound = -l + 1;
        repIntervalUpperBound = l - 1;

        return true;
    }

    @Override
    public void computeOrthogonalRepresentation(HashMap<TupleEdge<Vertex, Vertex>, Integer> hashMap) {


        if (spirality >= 0) {
            for (int i = 0; i < spirality; i++) {
                hashMap.put(new TupleEdge<>(mergedChildren.get(i).getStartVertex(), mergedChildren.get(i).getSinkVertex(),1), 1);
                hashMap.put(new TupleEdge<>(mergedChildren.get(i + 1).getSinkVertex(), mergedChildren.get(i + 1).getStartVertex(),-1), -1);
            }
        } else {
            for (int i = 0; i < -spirality; i++) {
                hashMap.put(new TupleEdge<>(mergedChildren.get(i).getStartVertex(), mergedChildren.get(i).getSinkVertex(),-1), -1);
                hashMap.put(new TupleEdge<>(mergedChildren.get(i + 1).getSinkVertex(), mergedChildren.get(i + 1).getStartVertex(),1), 1);
            }
        }


        //  System.out.println("Test");
    }


}