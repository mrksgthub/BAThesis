package Datatypes;

import java.util.ArrayList;
import java.util.List;

public class SPQSNode extends SPQNode {


    public SPQSNode(String s) {
        super(s);
        nodeType = NodeTypesEnum.NODETYPE.S;
    }

    public SPQSNode(Vertex edgeSource, Vertex edgeTarget) {

        super("S" + edgeSource.getName() + edgeTarget.getName()+ id++);
        nodeType = NodeTypesEnum.NODETYPE.S;
        this.startVertex = edgeSource;
        this.sinkVertex = edgeTarget;
    }





    @Override
    public void generateQstarChildren() { // ehemals mergeQNodes
        List<SPQNode> qNodes = new ArrayList<>();
        List<SPQNode> replacementmergedChildren = new ArrayList<>();


        List<SPQNode> mergedChildren = this.getMergedChildren();

        for (int i = 0; i < mergedChildren.size(); i++) {

            if ((mergedChildren.get(i).getNodeType() == NodeTypesEnum.NODETYPE.Q)) {
                qNodes.add(mergedChildren.get(i));
            }
            if (mergedChildren.get(i).getNodeType() != NodeTypesEnum.NODETYPE.Q) {
                if (qNodes.size() > 0) {
                    SPQQNode newQ = new SPQQNode("Qstar" + counter++ + this.getName());
                    newQ.setMergedChildren(qNodes);
                    newQ.setSinkVertex(newQ.getMergedChildren().get(newQ.getMergedChildren().size() - 1).getSinkVertex());
                    newQ.setStartVertex(newQ.getMergedChildren().get(0).getStartVertex());
                    replacementmergedChildren.add(newQ);

                    qNodes = new ArrayList<>();
                }
                replacementmergedChildren.add(mergedChildren.get(i));
            }
        }
        if (qNodes.size() > 0) {
            SPQQNode newQ = new SPQQNode("Qstar" + counter++ + this.getName());
            newQ.setMergedChildren(qNodes);
            newQ.setSinkVertex(newQ.getMergedChildren().get(newQ.getMergedChildren().size() - 1).getSinkVertex());
            newQ.setStartVertex(newQ.getMergedChildren().get(0).getStartVertex());
            replacementmergedChildren.add(newQ);

            this.setMergedChildren(replacementmergedChildren);
            if (getMergedChildren().size() == 1) {
                this.getParent().getMergedChildren().set(this.getParent().getMergedChildren().indexOf(this), newQ);
            }
        } else if (replacementmergedChildren.size() > 1) {
            this.setMergedChildren(replacementmergedChildren);

        }
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


    @Override
    public void setSpiralityOfChildren() {
        if (this.getNodeType() == NodeTypesEnum.NODETYPE.S) {

            double delta = 0;
            for (SPQNode node :
                    mergedChildren) {
                node.setSpiralityOfChildren(node.getRepIntervalUpperBound());
                delta += (node.getRepIntervalUpperBound());
            }
            delta -= this.spirality;

            while (delta != 0) {
                for (SPQNode node :
                        mergedChildren) {
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


