package io.github.jameseec.treevisualize.model;

/**
 * Represents an AVL Tree that disallows duplicates.
 */
public class AVLTree extends Tree {

    /**
     * Inserts a node with given val into node root, using tree-specific implementations.
     * Does not check for duplicates or increment {@code size}.
     *
     * @param current node to insert new node into
     * @param val value of new node to insert
     */
    @Override
    protected void insertNode(Node current, int val) {
        root = insertRecursive(current, val);
    }

    private Node insertRecursive(Node current, int val) {
        if (current == null) {
            return new Node(val);
        }
        if (val < current.getValue()) {
            current.setLeftChild(insertRecursive(current.getLeftChild(), val));
        } else {
            current.setRightChild(insertRecursive(current.getRightChild(), val));
        }
        updateHeight(current);

        // Handle rotations
        return balance(current);
    }

    private Node balance(Node node) {
        int balance = getBalance(node);

        // Left heavy
        if (balance > 1) {
            if (getBalance(node.getLeftChild()) < 0) {
                node.setLeftChild(rotateLeft(node.getLeftChild())); // LR case
            }
            return rotateRight(node); // LL case
        }

        // Right heavy
        if (balance < -1) {
            if (getBalance(node.getRightChild()) > 0) {
                node.setRightChild(rotateRight(node.getRightChild())); // RL case
            }
            return rotateLeft(node); // RR case
        }

        return node; // already balanced
    }

    /**
     * Deletes the node with the specified value from the tree if it exists.
     *
     * @return true if val was deleted. false if it doesn't exist.
     */
    @Override
    public boolean delete(int val) {
        if (!contains(val)) return false;
        root = deleteRecursive(root, val);
        size--;
        return true;
    }

    private Node deleteRecursive(Node node, int val) {
        if (node == null) return null;

        if (val < node.getValue()) {
            node.setLeftChild(deleteRecursive(node.getLeftChild(), val));
        } else if (val > node.getValue()) {
            node.setRightChild(deleteRecursive(node.getRightChild(), val));
        } else {
            // Node with only one child or no child
            if (node.getLeftChild() == null) return node.getRightChild();
            if (node.getRightChild() == null) return node.getLeftChild();

            // Node with two children: get inorder successor
            Node successor = inOrderSuccessor(node);
            node.setValue(successor.getValue());
            node.setRightChild(deleteRecursive(node.getRightChild(), successor.getValue()));
        }

        updateHeight(node);
        return balance(node);
    }

    // Gets balance of given node.
    private int getBalance(Node root) {
        if (root == null) {
            return 0;
        } else {
            return height(root.getLeftChild()) - height(root.getRightChild());
        }
    }

    // Returns stored height, -1 if node is null
    private int height(Node node) {
        return (node == null) ? -1 : node.getHeight();
    }

    private Node rotateRight(Node root) {
        Node newRoot = root.getLeftChild();
        Node movedSubtree = newRoot.getRightChild();

        newRoot.setRightChild(root);
        root.setLeftChild(movedSubtree);

        updateHeight(root);
        updateHeight(newRoot);

        return newRoot;
    }

    private Node rotateLeft(Node root) {
        Node newRoot = root.getRightChild();
        Node movedSubtree = newRoot.getLeftChild();

        newRoot.setLeftChild(root);
        root.setRightChild(movedSubtree);

        updateHeight(root);
        updateHeight(newRoot);

        return newRoot;
    }

    private void updateHeight(Node node) {
        node.setHeight(1 + Math.max(height(node.getLeftChild()), height(node.getRightChild())));
    }
}
