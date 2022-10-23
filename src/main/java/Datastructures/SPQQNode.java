package Datastructures;

import java.util.HashMap;


/**
 * Diese Klasse implementiert die Methoden, um das respresentability interval und condition für einen Q-Knoten festzulegen.
 *
 *
 *
 */
public class SPQQNode extends SPQNode {

    /**
     * Konstruktor für SPQImporter
     *
     * @param q
     * @param b
     */
    public SPQQNode(String q, boolean b) {
        super(q);
        nodeType = NodeTypesEnum.NODETYPE.Q;
        isQ = true;
    }


    /**
     * Konstruktor für eines Q-Knotens für den Graphgenerator
     *
     * @param q Name
     * @param edgeSource Quelle
     * @param edgeTarget Senke
     */
    public SPQQNode(String q, Vertex edgeSource, Vertex edgeTarget) {
        super(q);
        nodeType = NodeTypesEnum.NODETYPE.Q;
        sourceVertex = edgeSource;
        sinkVertex = edgeTarget;
        isQ = false;
    }


    @Override
    public void addToSourceAndSinkLists(SPQNode nodes) {

            if (this.getSourceVertex() == nodes.getSourceVertex()) {
                sourceNodes.add(nodes.getSinkVertex());
            }
            if (this.getSinkVertex() == nodes.getSinkVertex()) {
                sinkNodes.add(nodes.getSourceVertex());
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
                angleMap.put(new TupleEdge<>(spqChildren.get(i).getSourceVertex(), spqChildren.get(i).getSinkVertex(),1), 1);
                angleMap.put(new TupleEdge<>(spqChildren.get(i + 1).getSinkVertex(), spqChildren.get(i + 1).getSourceVertex(),-1), -1);
            }
        } else {
            for (int i = 0; i < -spirality; i++) {
                angleMap.put(new TupleEdge<>(spqChildren.get(i).getSourceVertex(), spqChildren.get(i).getSinkVertex(),-1), -1);
                angleMap.put(new TupleEdge<>(spqChildren.get(i + 1).getSinkVertex(), spqChildren.get(i + 1).getSourceVertex(),1), 1);
            }
        }

    }


}