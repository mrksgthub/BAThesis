package Datatypes;

import java.util.HashMap;

public class SPQPNode extends SPQNode {


    private int inDegreeCounterStart;
    private int inDegreeCounterSink;
    private int outDegreeCounterStart;
    private int outDegreeCounterSink;

    private double kul;
    private double kur;
    private double kvl;
    private double kvr;
    private int alphaul;
    private int alphavl;
    private int alphaur;
    private int alphavr;

    public SPQPNode(String name, boolean isRoot) {
        super(name);
        nodeType = NodeTypesEnum.NODETYPE.P;

    }

    public SPQPNode(Vertex edgeSource, Vertex edgeTarget) {
        super("P" + edgeSource.getName() + edgeTarget.getName() + id++);
        nodeType = NodeTypesEnum.NODETYPE.P;
        this.startVertex = edgeSource;
        this.sinkVertex = edgeTarget;
    }


    @Override
    public void generateQstarChildren() {
        for (SPQNode node :
                spqStarChildren) {

            if (node.getNodeType() == NodeTypesEnum.NODETYPE.Q && node.spqStarChildren.size() == 0) {
                SPQNode newQ = new SPQQNode("Qstar" + node.getName());
                newQ.setParent(this);
                newQ.setStartVertex(node.getStartVertex());
                newQ.setSinkVertex(node.getSinkVertex());
                newQ.spqStarChildren.add(node);
                this.spqStarChildren.set(this.spqStarChildren.indexOf(node), newQ);
            }
        }
    }


    // Backup
    @Override
    public boolean calculateRepresentabilityInterval() {


        if (spqStarChildren.size() == 3) {

            // Lemma 6
            double mL = spqStarChildren.get(0).getRepIntervalLowerBound();
            double mC = spqStarChildren.get(1).getRepIntervalLowerBound();
            double mR = spqStarChildren.get(2).getRepIntervalLowerBound();
            double ML = spqStarChildren.get(0).getRepIntervalUpperBound();
            double MC = spqStarChildren.get(1).getRepIntervalUpperBound();
            double MR = spqStarChildren.get(2).getRepIntervalUpperBound();

            if (mL - 2 <= MR + 2 && mL - 2 <= MC && mR + 2 <= ML - 2 && mR + 2 <= MC && mC <= ML - 2 && mC <= MR + 2) {
                repIntervalLowerBound = Math.max(mL - 2, mC);
                repIntervalLowerBound = Math.max(repIntervalLowerBound, mR + 2);
                repIntervalUpperBound = Math.min(ML - 2, MC);
                repIntervalUpperBound = Math.min(repIntervalUpperBound, MR + 2);
                //    System.out.println("3 Children" + " " + this.getName());


            } else {
                System.out.println("Intervals do not overlap P has 3 Children" + " " + this.getName());
                return false;
            }


        } else if (spqStarChildren.size() == 2) {

            SPQNode leftSNode = spqStarChildren.get(0);
            SPQNode rightSNode = spqStarChildren.get(1);


            double mL = leftSNode.getRepIntervalLowerBound();
            double mR = rightSNode.getRepIntervalLowerBound();
            double ML = leftSNode.getRepIntervalUpperBound();
            double MR = rightSNode.getRepIntervalUpperBound();


            //TODO wurde geändert von dem auskommentierten zu diesem hier
            inDegreeCounterStart = startNodes.size();
            inDegreeCounterSink = sinkNodes.size();

            outDegreeCounterStart = startVertex.adjacentVertices.size() - inDegreeCounterStart;
            outDegreeCounterSink = sinkVertex.adjacentVertices.size() - inDegreeCounterSink;


            if (inDegreeCounterStart == 2 && inDegreeCounterSink == 2) { // I_2O_alphaBeta

                // Lemmma 8
                double gamma = outDegreeCounterStart + outDegreeCounterSink - 2;

                double lBound = mL - MR;
                double upBound = ML - mR;


                if (lBound <= 4 - gamma && 2 <= upBound) {

                    repIntervalLowerBound = Math.max(mL - 2, mR) + gamma / 2;
                    repIntervalUpperBound = Math.min(ML, MR + 2) - gamma / 2;

                    //  System.out.println("I2Oab" + " " + this.getName());

                } else {
                    System.out.println("No rectalinear drawing possible I2Oab" + " " + this.getName());
                    return false;
                }

            } else if ((inDegreeCounterStart == 3 && inDegreeCounterSink == 3))  // I_3dd
            {
                double pdV = 9999;
                double pdU = 9999;

                mL = leftSNode.getRepIntervalLowerBound();
                mR = rightSNode.getRepIntervalLowerBound();
                ML = leftSNode.getRepIntervalUpperBound();
                MR = rightSNode.getRepIntervalUpperBound();

                // Was tun falls erstes oder zweites child eine Q node ist
                if ((spqStarChildren.get(0).startNodes.size() == 2) && (spqStarChildren.get(0).sinkNodes.size() == 2)) { //I3ll

                    pdU = 0;
                    pdV = 0;
                } else if ((spqStarChildren.get(1).startNodes.size() == 2) && (spqStarChildren.get(1).sinkNodes.size() == 2)) //I3rr
                { // FIxbar indem man Q2 zu nem Qstar macht

                    pdU = 1;
                    pdV = 1;
                } else if ((spqStarChildren.get(1).startNodes.size() == 2) && (spqStarChildren.get(0).sinkNodes.size() == 2)) { //I3lr

                    pdU = 0;
                    pdV = 1;

                } else if ((spqStarChildren.get(0).startNodes.size() == 2) && (spqStarChildren.get(1).sinkNodes.size() == 2)) { //I3rl

                    pdU = 1;
                    pdV = 0;

                    //    System.out.println("I_3rl reverse" + this.getName());

                }
                double lBound = mL - MR;
                double upBound = ML - mR;

                if ((lBound <= 3) && (3 <= upBound)) {

                    repIntervalLowerBound = Math.max(mL - 1, mR + 2) - (pdU + pdV) / 2;
                    repIntervalUpperBound = Math.min(ML - 1, MR + 2) - (pdU + pdV) / 2;

                    //     System.out.println("I3_dd" + " " + this.getName());

                } else {
                    System.out.println("No rectalinear I3dd'" + " " + this.getName());
                    return false;
                }

            } else { // I_3dO_alphaBeta

                double pd = 9999;
                double gamma = outDegreeCounterStart + outDegreeCounterSink - 2;

                if (inDegreeCounterStart == 3) {

                    //TODO wurde geändert
                    pd = (spqStarChildren.get(0).startNodes.size() == 2) ? 0 : 1;


                    //        System.out.println("I_3dOab reverse" + this.getName());

                } else { // check Sink
                    pd = (spqStarChildren.get(0).sinkNodes.size() == 2) ? 0 : 1;
                    //       System.out.println("NI_3dOab normal" + " " + this.getName());

                }

                double lBound = mL - MR;
                double upBound = ML - mR;

                if (lBound <= 3.5 - gamma && 2.5 <= upBound) {

                    repIntervalLowerBound = Math.max(mL - 1.5, mR + 1) + (gamma - pd) / 2;
                    repIntervalUpperBound = Math.min(ML - 0.5, MR + 2) - (gamma + pd) / 2;
                } else {
                    System.out.println("No rectalinear drawing possible I3dO" + " " + this.getName());
                    return false;
                }

            }


        } else {
            System.out.println("Invalid number of Children for P-Node");
            return false;
        }

        return true;
    }


    @Override
    public void computeAngles(HashMap<TupleEdge<Vertex, Vertex>, Integer> angleMap) {

        // Für innere Facetten nur der Winkel auf der rechten Seite relevant?
        Vertex vertex1 = spqStarChildren.get(0).startNodes.get(0);
        if (startNodes.size() == 3 && !this.isRoot) {
            // mergedChildren 3, oder 2 sind die Fälle die unterschieden werden müssen
            // Winkel um die Quelle festlegen.
            // Beispiel3-4-10  Außen
            Vertex nextVertexStarRight = startVertex.adjacentVertices.get(Math.floorMod((startVertex.adjacentVertices.indexOf(spqStarChildren.get(spqStarChildren.size() - 1).startNodes.get(spqStarChildren.get(spqStarChildren.size() - 1).startNodes.size() - 1)) + 1), startVertex.adjacentVertices.size()));
            angleMap.put((new TupleEdge<>(nextVertexStarRight, startVertex, 1)), 1);

            //Beispiel 9-4-10
            Vertex nextVertexMiddle = startVertex.adjacentVertices.get(Math.floorMod((startVertex.adjacentVertices.indexOf(spqStarChildren.get(spqStarChildren.size() - 1).startNodes.get(spqStarChildren.get(spqStarChildren.size() - 1).startNodes.size() - 1)) + 1), startVertex.adjacentVertices.size()));
            angleMap.put((new TupleEdge<>(startNodes.get(1), startVertex, 1)), 1);

            // Beispiel3-4-10  Außen
            angleMap.put((new TupleEdge<>(startNodes.get(2), startVertex, 1)), 1);

            // Beispiel5-4-3  Außen
            angleMap.put((new TupleEdge<>(startNodes.get(0), startVertex, 1)), 1);

        } else if (startNodes.size() == 2 && startVertex.adjacentVertices.size() > 2 && !this.isRoot) {
            // Beispiel 8-6-5 außen
            Vertex nextVertexStartLeft = startVertex.adjacentVertices.get(Math.floorMod((startVertex.adjacentVertices.indexOf(vertex1) - 1), startVertex.adjacentVertices.size()));
            angleMap.put(new TupleEdge<>(vertex1, startVertex, alphaul), alphaul);

            // Beispiel 5-6-7 Außen
            Vertex vertex2 = spqStarChildren.get(1).startNodes.get(0);
            Vertex nextVertexStarRight = startVertex.adjacentVertices.get(Math.floorMod((startVertex.adjacentVertices.indexOf(vertex2) + 1), startVertex.adjacentVertices.size()));
            angleMap.put((new TupleEdge<>(nextVertexStarRight, startVertex, alphaur)), alphaur);

            //Winkel zwischen der linken und rechten äußeren Kanten "innen" (Bsp. am Ende von Kante 7-6 an Knoten 6)
            angleMap.put((new TupleEdge<>(vertex2, startVertex, ((alphaur + alphaul == 2) && (startVertex.adjacentVertices.size() == 3)) ? 0 : 1)), ((alphaur + alphaul == 2) && (startVertex.adjacentVertices.size() == 3)) ? 0 : 1);

        } else if (startVertex.adjacentVertices.size() == 2 && this.isRoot) {
            // Sonderfall: Referenzkante an der Wurzel.
            angleMap.put((new TupleEdge<>(sinkVertex, startVertex, 1)), 1);
            angleMap.put((new TupleEdge<>(startNodes.get(0), startVertex, 1)), -1);


        }


        if (sinkNodes.size() == 3 && !this.isRoot) {
            // linker Winkel an SinkVertex (außen) 14-13-8 an Knoten 13
            Vertex nextVertexSinkLeft = sinkVertex.adjacentVertices.get(Math.floorMod((sinkVertex.adjacentVertices.indexOf(sinkNodes.get(0)) + 1), sinkVertex.adjacentVertices.size()));
            angleMap.put((new TupleEdge<>(nextVertexSinkLeft, sinkVertex, 1)), 1);

            // 8-13-7
            angleMap.put((new TupleEdge<>(sinkNodes.get(0), sinkVertex, 1)), 1);

            // Beispie 7-13-12  "Zwischen Innenkanten"
            angleMap.put((new TupleEdge<>(sinkNodes.get(1), sinkVertex, 1)), 1);

            // Beispie 12-13-14  "Zwischen Innenkanten"
            angleMap.put((new TupleEdge<>(sinkNodes.get(2), sinkVertex, 1)), 1);

        } else if (sinkNodes.size() == 2 && sinkVertex.adjacentVertices.size() > 2 && !this.isRoot) {
            // linker Winkel an SinkVertex (außen) 14-13-8 an Knoten 13
            Vertex nextVertexSinkLeft = sinkVertex.adjacentVertices.get(Math.floorMod((sinkVertex.adjacentVertices.indexOf(spqStarChildren.get(0).sinkNodes.get(0)) + 1), sinkVertex.adjacentVertices.size()));
            angleMap.put((new TupleEdge<>(nextVertexSinkLeft, sinkVertex, alphavl)), alphavl);

            // rechter Winkel an Sink Vertex (Außen)
            Vertex nextVertexSinkRight = sinkVertex.adjacentVertices.get(Math.floorMod((sinkVertex.adjacentVertices.indexOf(spqStarChildren.get(1).sinkNodes.get(0)) - 1), sinkVertex.adjacentVertices.size()));
            angleMap.put((new TupleEdge<>((spqStarChildren.get(1).sinkNodes.get(0)), sinkVertex, alphavr)), alphavr);

            //Winkel zwischen der linken und rechten äußeren Kanten "innen" (Bsp. am Ende von Kante 9-11-10 an Knoten 11)
            angleMap.put((new TupleEdge<>(sinkNodes.get(0), sinkVertex, (alphavr + alphavl == 2 && (sinkVertex.adjacentVertices.size() == 3)) ? 0 : 1)), (alphavr + alphavl == 2 && (sinkVertex.adjacentVertices.size() == 3)) ? 0 : 1);

        } else if (sinkVertex.adjacentVertices.size() == 2 && this.isRoot) {

            angleMap.put((new TupleEdge<>(startVertex, sinkVertex, -1)), -1);
            angleMap.put((new TupleEdge<>(sinkNodes.get(0), sinkVertex, 1)), 1);

            //  System.out.println("Test");


        }


    }


    @Override
    public void setSpiralityOfChildren() {

        if (this.getNodeType() == NodeTypesEnum.NODETYPE.P && this.getSpqStarChildren().size() == 3) {
            this.getSpqStarChildren().get(0).setSpiralityOfChildren(this.spirality + 2);
            this.getSpqStarChildren().get(1).setSpiralityOfChildren(this.spirality);
            this.getSpqStarChildren().get(2).setSpiralityOfChildren(this.spirality - 2);

        } else if (this.getNodeType() == NodeTypesEnum.NODETYPE.P && this.getSpqStarChildren().size() == 2) {
            int alphaul = 9999;
            int alphaur = 9999;

            int alphavl = 9999;
            int alphavr = 9999;

            // äquivalent zu outdeg(w)
            kul = ((this.startVertex.adjacentVertices.size() - startNodes.size()) == 1 && this.getSpqStarChildren().get(0).startNodes.size() == 1) ? 1 : 0.5;
            kur = ((this.startVertex.adjacentVertices.size() - startNodes.size()) == 1 && this.getSpqStarChildren().get(1).startNodes.size() == 1) ? 1 : 0.5;


            kvl = ((this.sinkVertex.adjacentVertices.size() - sinkNodes.size()) == 1 && this.getSpqStarChildren().get(0).sinkNodes.size() == 1) ? 1 : 0.5;
            kvr = ((this.sinkVertex.adjacentVertices.size() - sinkNodes.size()) == 1 && this.getSpqStarChildren().get(1).sinkNodes.size() == 1) ? 1 : 0.5;

            int[] arrU;
            if (startVertex.adjacentVertices.size() == 4) {
                alphaul = 1;
                alphaur = 1;
                arrU = new int[]{1};
            } else {
                arrU = new int[]{1, 0};
            }
            int[] arrV;
            if ((this.sinkVertex.adjacentVertices.size()) == 4) {
                alphavl = 1;
                alphavr = 1;
                arrV = new int[]{1};
            } else {
                arrV = new int[]{1, 0};
            }

            //Spirality des Linken Kindes Festlegen
            outerloop:
            for (int i = 0; i < arrU.length; i++) {
                for (int j = 0; j < arrV.length; j++) {
                    alphaul = arrU[i];
                    alphavl = arrV[j];
                    double temp = this.spirality + kul * arrU[i] + kvl * arrV[j];
                    if (this.getSpqStarChildren().get(0).getRepIntervalLowerBound() <= temp && temp <= this.getSpqStarChildren().get(0).getRepIntervalUpperBound()) {
                        this.getSpqStarChildren().get(0).setSpiralityOfChildren(this.spirality + kul * alphaul + kvl * alphavl);
                        break outerloop;
                    }
                }
            }

            //Spirality des rechten Kindes Festlegen
            outerloop2:
            for (int i = 0; i < arrU.length; i++) {
                for (int j = 0; j < arrV.length; j++) {
                    alphaur = arrU[i];
                    alphavr = arrV[j];
                    double temp = this.spirality - kur * arrU[i] - kvr * arrV[j];
                    if (this.getSpqStarChildren().get(1).getRepIntervalLowerBound() <= temp && temp <= this.getSpqStarChildren().get(1).getRepIntervalUpperBound() && alphaul + alphaur > 0 && alphavl + alphavr > 0) {
                        this.getSpqStarChildren().get(1).setSpiralityOfChildren(this.spirality - kur * alphaur - kvr * alphavr);
                        break outerloop2;
                    }
                }
            }


            assert (this.getSpqStarChildren().get(1).getRepIntervalLowerBound() <= this.getSpqStarChildren().get(1).getSpirality() && this.getSpqStarChildren().get(1).getSpirality() <= this.getSpqStarChildren().get(1).getRepIntervalUpperBound());
            assert (this.getSpqStarChildren().get(0).getRepIntervalLowerBound() <= this.getSpqStarChildren().get(0).getSpirality() && this.getSpqStarChildren().get(0).getSpirality() <= this.getSpqStarChildren().get(0).getRepIntervalUpperBound());


            // System.out.println("Test");

            this.alphavl = alphavl;
            this.alphavr = alphavr;
            this.alphaul = alphaul;
            this.alphaur = alphaur;

        }


    }







}
