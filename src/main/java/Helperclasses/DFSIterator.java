package Helperclasses;

import Datastructures.SPQNode;

import java.util.ArrayDeque;
import java.util.Deque;

public class DFSIterator {

    /**
     * https://www.techiedelight.com/preorder-tree-traversal-iterative-recursive/
     *
     * @param root
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
     * https://www.techiedelight.com/postorder-tree-traversal-iterative-recursive/
     *
     * @param root
     * @return
     */
    public static Deque<SPQNode> buildPostOrderStack(SPQNode root) {

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

            // push the left and right child of the popped node into the stack
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

            // push the left and right child of the popped node into the stack
            if (curr.getSpqChildren().size() != 0) {


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