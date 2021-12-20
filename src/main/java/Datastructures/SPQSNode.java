package Datastructures;

import java.util.ArrayList;
import java.util.List;

/**
 *  Diese Klasse implementiert die Methoden, um das respresentability interval und condition für einen Q-Knoten festzulegen.
 *
 *
 */
public class SPQSNode extends SPQNode {


    public SPQSNode(String s) {
        super(s);
        nodeType = NodeTypesEnum.NODETYPE.S;
    }

    public SPQSNode(Vertex edgeSource, Vertex edgeTarget) {

        super("S" + edgeSource.getName() + edgeTarget.getName()+ id++);
        nodeType = NodeTypesEnum.NODETYPE.S;
        this.sourceVertex = edgeSource;
        this.sinkVertex = edgeTarget;
    }





    @Override
    public void generateQstarChildren() { // ehemals mergeQNodes
        List<SPQNode> qNodes = new ArrayList<>();
        List<SPQNode> replacementmergedChildren = new ArrayList<>();


        List<SPQNode> mergedChildren = this.getSpqChildren();

        for (int i = 0; i < mergedChildren.size(); i++) {

            if ((mergedChildren.get(i).getNodeType() == NodeTypesEnum.NODETYPE.Q)) {
                qNodes.add(mergedChildren.get(i));
            }
            if (mergedChildren.get(i).getNodeType() != NodeTypesEnum.NODETYPE.Q) {
                if (qNodes.size() > 0) {
                    SPQQNode newQ = new SPQQNode("Qstar" + counter++ + this.getName(), false);
                    newQ.setSpqChildren(qNodes);
                    newQ.setSinkVertex(newQ.getSpqChildren().get(newQ.getSpqChildren().size() - 1).getSinkVertex());
                    newQ.setSourceVertex(newQ.getSpqChildren().get(0).getSourceVertex());
                    replacementmergedChildren.add(newQ);

                    qNodes = new ArrayList<>();
                }
                replacementmergedChildren.add(mergedChildren.get(i));
            }
        }
        if (qNodes.size() > 0) {
            SPQQNode newQ = new SPQQNode("Qstar" + counter++ + this.getName(), false);
            newQ.setSpqChildren(qNodes);
            newQ.setSinkVertex(newQ.getSpqChildren().get(newQ.getSpqChildren().size() - 1).getSinkVertex());
            newQ.setSourceVertex(newQ.getSpqChildren().get(0).getSourceVertex());
            replacementmergedChildren.add(newQ);

            this.setSpqChildren(replacementmergedChildren);
            if (getSpqChildren().size() == 1) {
                this.getParent().getSpqChildren().set(this.getParent().getSpqChildren().indexOf(this), newQ);
            }
        } else if (replacementmergedChildren.size() > 1) {
            this.setSpqChildren(replacementmergedChildren);

        }
    }




    @Override
    public boolean calculateRepresentabilityInterval() {
        repIntervalLowerBound = 0;
        repIntervalUpperBound = 0;

        for (SPQNode node : spqChildren
        ) {
            repIntervalLowerBound += node.getRepIntervalLowerBound();
            repIntervalUpperBound += node.getRepIntervalUpperBound();
        }

        return true;
    }


    @Override
    public void setSpiralityOfChildren() {
        if (this.getNodeType() == NodeTypesEnum.NODETYPE.S) {

            double delta = 0;
            for (SPQNode node :
                    spqChildren) {
                node.setSpiralityOfChildren(node.getRepIntervalUpperBound());
                delta += (node.getRepIntervalUpperBound());
            }
            delta -= this.spirality;

            while (delta != 0) {
                for (SPQNode node :
                        spqChildren) {
                    double temp = Math.min(delta, node.getRepIntervalUpperBound() - node.getRepIntervalLowerBound());
                    node.setSpiralityOfChildren(node.getSpirality() - temp);
                    delta -= temp;
                    if (delta == 0) {
                        break;
                    }
                }
            }
            assert delta == 0;

        }


    }

}


