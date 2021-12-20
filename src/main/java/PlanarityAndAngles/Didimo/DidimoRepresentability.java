package PlanarityAndAngles.Didimo;

import Datastructures.SPQNode;
import Helperclasses.DFSIterator;

import java.util.Deque;

/**
 * Implementiert das Ablaufen lassen des festlegen des representability intervals, der representability condition und
 * root condition.
 * Wobei die Berechnung der intervals und conditions in den Knoten selbst abläuft.
 *
 *
 */
public class DidimoRepresentability {


    public DidimoRepresentability() {
    }

    /**
     * Festlegung der
     *
     *
     * @param root
     * @return
     */
    public boolean run(SPQNode root) {
        boolean check = true;
        check = computeRepresentabilityIntervals(root);
        if (check) {
            check = computeNofRoot(root) && check;
            if (!check) {
                System.out.println("Didimo rejected at source Node");
            }
        }
        return check;

    }


    /**
     * Überprüfung der root condition.
     *
     * @param root Wurzel des SPQ*-Baums
     * @return
     */
    private boolean computeNofRoot(SPQNode root) { // Änderungen aus v4 des Paper eingefügt

        int spirality = 99999;

        if (root.getSpqChildren().get(0).getSourceNodes().size() == 1 && root.getSpqChildren().get(0).getSinkNodes().size() == 1) {

            if (root.getSpqChildren().get(0).getRepIntervalLowerBound() <= 6 && 2 <= root.getSpqChildren().get(0).getRepIntervalUpperBound()) {

                spirality = (int) Math.ceil(Math.max(2.0, root.getSpqChildren().get(0).getRepIntervalLowerBound()));
                //  spirality = 2; (Alte Version des Didimo Papers, wurde in der 2021 Version mit der darüberliegenden Zeile ersetzt)
            }

        } else if (root.getSpqChildren().get(0).getSourceNodes().size() >= 2 && root.getSpqChildren().get(0).getSinkNodes().size() >= 2) {
            if (root.getSpqChildren().get(0).getRepIntervalLowerBound() <= 4 && 4 <= root.getSpqChildren().get(0).getRepIntervalUpperBound()) {
                spirality = 4;
            }


        } else {
            if (root.getSpqChildren().get(0).getRepIntervalLowerBound() <= 5 && 3 <= root.getSpqChildren().get(0).getRepIntervalUpperBound()) {
                spirality = (int) Math.ceil(Math.max(3.0, root.getSpqChildren().get(0).getRepIntervalLowerBound()));
                //  spirality = 3; (Alte Version des Didimo Papers, wurde in der 2021 Version mit der darüberliegenden Zeile ersetzt)
            }

        }


        if (root.getSpqChildren().get(0).getRepIntervalLowerBound() <= spirality && spirality <= root.getSpqChildren().get(0).getRepIntervalUpperBound()) {
            root.getSpqChildren().get(0).setSpiralityOfChildren(spirality);
            return true;
        } else {
            return false;
        }
    }


    /**
     * Diese Methode führt man aus, um  für den SPQ*-Baum die representability intervals zu berechnen
     * und überprüft dabei die representability conditions.
     * @param root Wurzel des SPQ*-Baums
     * @return
     */
    private Boolean computeRepresentabilityIntervals(SPQNode root) {


       Deque<SPQNode> stack = DFSIterator.buildPostOrderStackPlanarityTest(root);

        while (!stack.isEmpty()) {

            SPQNode node = stack.pop();
            if (node.getSpqChildren().size() != 0 && !node.isRoot()) {
                if (!node.calculateRepresentabilityInterval()) {
                    return false;
                }
            }

        }
        return true;

    }


}