package Datastructures;

import java.util.HashMap;

public class SPQQNode extends SPQNode {


    public SPQQNode(String q) {
        super(q);
        nodeType = NodeTypesEnum.NODETYPE.Q;
    }

    public SPQQNode(Vertex source, Vertex sink, boolean b) {
        super("Q"+source.getName()+sink.getName()+ id++);
        nodeType = NodeTypesEnum.NODETYPE.Q;

        if (!b) {
            super.setName(source.getName() + sink.getName());
        } else {
            super.setName("QStar" + source.getName() + sink.getName() + id++);
        }

        startVertex = source;
        sinkVertex = sink;
    }

    public SPQQNode(String q, Vertex edgeSource, Vertex edgeTarget) {
        super(q);
        nodeType = NodeTypesEnum.NODETYPE.Q;
        startVertex = edgeSource;
        sinkVertex = edgeTarget;
    }


    @Override
    public void addToSourceAndSinkLists(SPQNode nodes) {

            if (this.getStartVertex() == nodes.getStartVertex()) {
                startNodes.add(nodes.getSinkVertex());
            }
            if (this.getSinkVertex() == nodes.getSinkVertex()) {
                sinkNodes.add(nodes.getStartVertex());
            }

    }


    @Override
    public boolean calculateRepresentabilityInterval() {
        int l = spqChildren.size();
        if (l == 0) {
            l = 1;
        }
        repIntervalLowerBound = -l + 1;
        repIntervalUpperBound = l - 1;

        return true;
    }

    @Override
    public void computeAngles(HashMap<TupleEdge<Vertex, Vertex>, Integer> angleMap) {

        if (spirality >= 0) {
            for (int i = 0; i < spirality; i++) {
                angleMap.put(new TupleEdge<>(spqChildren.get(i).getStartVertex(), spqChildren.get(i).getSinkVertex(),1), 1);
                angleMap.put(new TupleEdge<>(spqChildren.get(i + 1).getSinkVertex(), spqChildren.get(i + 1).getStartVertex(),-1), -1);
            }
        } else {
            for (int i = 0; i < -spirality; i++) {
                angleMap.put(new TupleEdge<>(spqChildren.get(i).getStartVertex(), spqChildren.get(i).getSinkVertex(),-1), -1);
                angleMap.put(new TupleEdge<>(spqChildren.get(i + 1).getSinkVertex(), spqChildren.get(i + 1).getStartVertex(),1), 1);
            }
        }

    }


}