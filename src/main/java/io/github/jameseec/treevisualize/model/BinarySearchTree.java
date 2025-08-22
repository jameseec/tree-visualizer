package io.github.jameseec.treevisualize.model;

/**
 * Represents a simple binary search tree which disallows duplicates
 */
public class BinarySearchTree extends Tree {

    /**
     * Constructor that creates an empty tree.
     */
    public BinarySearchTree() {
        super();

    }

    /**
     * Inserts a node with given val into node root.
     * Does not check for duplicates or increment {@code size}.
     * @param current node to insert new node into
     * @param val value of new node to insert
     */
    @Override
    protected void insertNode(Node current, int val) {
        if (val > current.getValue()) {
            if (current.getRightChild() == null) {
                current.setRightChild(new Node(val));
            } else {
                insertNode(current.getRightChild(), val);
            }
        } else {
            if (current.getLeftChild() == null) {
                current.setLeftChild(new Node(val));
            } else {
                insertNode(current.getLeftChild(), val);
            }
        }
    }

    /**
     * Deletes node with specified value from tree if it exists.
     * If the node has two children, replaces with its in-order successor.
     * Decrements {@code size}.
     *
     * @param val value of node to be deleted
     * @return true if deletion successful, or false if node not found
     */
    @Override
    public boolean delete(int val) {
        // Contains check could be optimized (one less traversal).
        if (!contains(val)) {
            return false;
        } else {
            this.root = deleteNode(this.root, val);
            this.size--;
            return true;
        }
    }

    /**
     * Recursive deletion function for simple BST using in-order successor.
     *
     * @param current current node being checked for deletion
     * @param val value of node to be deleted.
     * @return modified node with deletion completed
     */
    private Node deleteNode(Node current, int val) {
        if (current == null) {
            return null;
        } else if (val < current.getValue()) {
            current.setLeftChild(deleteNode(current.getLeftChild(), val));
        } else if (val > current.getValue()) {
            current.setRightChild(deleteNode(current.getRightChild(), val));
        } else {
            // Case 1: No children or one child
            if (current.getLeftChild() == null) {
                return current.getRightChild(); // replaces with right child or null
            } else if (current.getRightChild() == null) {
                return current.getLeftChild();
            } else {
                // get in-order successor
                Node successor = inOrderSuccessor(current);
                // swap values of current and in-order successor
                int tempValue = current.getValue();
                current.setValue(successor.getValue());
                successor.setValue(tempValue);
                // delete in-order successor (now with current's original value)
                current.setRightChild(deleteNode(current.getRightChild(), tempValue));
            }
        }
        return current;
    }

}
