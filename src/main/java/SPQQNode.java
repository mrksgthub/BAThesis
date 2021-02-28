import org.antlr.v4.runtime.misc.Pair;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.HashMap;

public class SPQQNode extends SPQNode {
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


    @Override
    public void calculateRepresentabilityInterval(DirectedMultigraph<TreeVertex, DefaultEdge> graph) {
        int l = mergedChildren.size();
        if (l == 0) {
            l = 1;
        }
        repIntervalLowerBound = -l + 1;
        repIntervalUpperBound = l - 1;
    }
    
    @Override
    public void computeOrthogonalRepresentation(HashMap<Pair<TreeVertex, TreeVertex>, Integer> hashMap) {

        for (int i = 0; i < spirality; i++) {
            hashMap.put(new Pair<TreeVertex, TreeVertex>(mergedChildren.get(i).getStartVertex(), mergedChildren.get(i).getSinkVertex()), -1);
            hashMap.put(new Pair<TreeVertex, TreeVertex>( mergedChildren.get(i+1).getSinkVertex(), mergedChildren.get(i+1).getStartVertex()), 1);
        }



        System.out.println("Test");
    }


}