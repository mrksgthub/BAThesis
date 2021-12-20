package Helperclasses;

import Datastructures.SPQNode;

import java.util.ArrayDeque;
import java.util.Deque;


/**
 * Implementiert die Erzeugung von Deques, welche pre-order und post-order Durchläufe von SPQ- oder SPQ*-Bäumen deren
 * Wurzel ein SPQNode Objekt ist.
 *
 */
public class DFSIterator {

    /**
     * Kopiert und angepasst von
     * https://www.techiedelight.com/preorder-tree-traversal-iterative-recursive/
     *
     * @param root - Wurzel des Baums
     * @return
     */
    public static Deque<SPQNode> buildPreOrderStack(SPQNode root) {

        // return if the tree is empty
        if (root == null) {
            return null;
        }

        Deque<SPQNode> out = new ArrayDeque<>();
        // create an empty stack and push the root node
        Deque<SPQNode> stack = new ArrayDeque<>();
        stack.push(root);


        // loop till stack is empty
        while (!stack.isEmpty()) {
            // pop a node from the stack and print it
            SPQNode curr = stack.pop();
            out.offer(curr);

            for (int i = curr.getSpqChildren().size() - 1; i >= 0; i--) {
                if (curr.getSpqChildren().get(i) != null) {
                    stack.push(curr.getSpqChildren().get(i));
                }
            }
        }
        return out;
    }


    /**
     * Kopiert und angepasst von
     * https://www.techiedelight.com/postorder-tree-traversal-iterative-recursive/
     *
     * @param root Wurzel des Baums
     * @return Deque<SPQNode> in post order Reihenfolge.
     */
    public static Deque<SPQNode> buildPostOrderStack(SPQNode root) {


        if (root == null) {
            return null;
        }


        Deque<SPQNode> stack = new ArrayDeque<>();
        stack.push(root);


        Deque<SPQNode> out = new ArrayDeque<>();


        while (!stack.isEmpty()) {

            SPQNode curr = stack.pop();
            out.push(curr);

            for (SPQNode child : curr.getSpqChildren()
            ) {
                if (child != null) {
                    stack.push(child);
                }
            }
        }
        return out;
    }

    /**
     * Baut den post order Deque auf, fügt aber keine Kinder von Q*-Knoten in den Deque hinzu.
     *
     * Kopiert und angepasst von
     * https://www.techiedelight.com/postorder-tree-traversal-iterative-recursive/
     *
     * @param root
     * @return
     */
    public static Deque<SPQNode> buildPostOrderStackPlanarityTest(SPQNode root) {

        // return if the tree is empty
        if (root == null) {
            return null;
        }

        // create an empty stack and push the root node
        Deque<SPQNode> stack = new ArrayDeque<>();
        stack.push(root);

        // create another stack to store postorder traversal
        Deque<SPQNode> out = new ArrayDeque<>();

        // loop till stack is empty
        while (!stack.isEmpty()) {
            // pop a node from the stack and push the data into the output stack
            SPQNode curr = stack.pop();
            out.push(curr);

            // Stellt sicher, dass keine Kinder von Q*-Knoten zum Deque hinzugefügt werden.
            if (!curr.isNotQNode()) {
                for (SPQNode child : curr.getSpqChildren()
                ) {
                    if (child != null) {
                        stack.push(child);
                    }
                }
            }
        }
        return out;
    }


}