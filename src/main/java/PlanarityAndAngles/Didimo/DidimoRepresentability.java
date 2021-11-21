package PlanarityAndAngles.Didimo;

import Datatypes.SPQNode;

public class DidimoRepresentability {


    public DidimoRepresentability() {
    }

    public boolean run(SPQNode root) {
        Boolean check = true;
        check = computeRepresentabilityIntervals(root, check);
        if (check) {
            check = (computeNofRoot(root)) ? check : false;
            if (!check) {
                System.out.println("Didimo rejected at source Node");
            }
        }
        return check;

    }





    public boolean computeNofRoot(SPQNode root) { // Änderungen in neuer v4 aus Paper eingefügt

        int spirality = 99999;

        if (root.getSpqChildren().get(0).getStartNodes().size() == 1 && root.getSpqChildren().get(0).getSinkNodes().size() == 1) {

            if (root.getSpqChildren().get(0).getRepIntervalLowerBound() <= 6 && 2 <= root.getSpqChildren().get(0).getRepIntervalUpperBound()) {

                spirality = (int) Math.ceil(Math.max(2.0, root.getSpqChildren().get(0).getRepIntervalLowerBound()));
                //  spirality = 2;
            }

        } else if (root.getSpqChildren().get(0).getStartNodes().size() >= 2 && root.getSpqChildren().get(0).getSinkNodes().size() >= 2) {
            if (root.getSpqChildren().get(0).getRepIntervalLowerBound() <= 4 && 4 <= root.getSpqChildren().get(0).getRepIntervalUpperBound()) {
                spirality = 4;
            }


        } else {
            if (root.getSpqChildren().get(0).getRepIntervalLowerBound() <= 5 && 3 <= root.getSpqChildren().get(0).getRepIntervalUpperBound()) {
                spirality = (int) Math.ceil(Math.max(3.0, root.getSpqChildren().get(0).getRepIntervalLowerBound()));
                //  spirality = 3;
            }

        }


        if (root.getSpqChildren().get(0).getRepIntervalLowerBound() <= spirality && spirality <= root.getSpqChildren().get(0).getRepIntervalUpperBound()) {
            root.getSpqChildren().get(0).setSpiralityOfChildren(spirality);
            return true;
        } else {
            System.out.println("Fehler?");
            return false;
        }
    }



    public Boolean computeRepresentabilityIntervals(SPQNode root, Boolean check) {

        boolean temp;
        for (SPQNode node : root.getSpqChildren()
        ) {
            temp = this.computeRepresentabilityIntervals(node, check);
            if (!temp) {
                check = temp;
            }
        }

        if (root.getSpqChildren().size() != 0 && !root.isRoot()) {
            if (!root.calculateRepresentabilityInterval()) {
                check = false;
            }
        }
        return check;
    }






















}