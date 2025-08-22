package io.github.jameseec.treevisualize.model;

import io.github.jameseec.treevisualize.exceptions.InvalidNodeCountException;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a tree data structure. Disallows nodes with duplicate values.
 */
public abstract class Tree {
    protected static final int MAX_SIZE = 40;
    protected Node root;
    protected int size;

    public Tree() {
        this.root = null;
        this.size = 0;
    }

    /**
     * Inserts a node with a specified value into the tree if it doesn't already exist
     * Also fails if {@code size} exceeded {@code MAX_SIZE}. Increments {@code size}.
     *
     * @param val value to insert
     * @return true if val was inserted. false if unsuccessful.
     */
    public boolean insert(int val) throws InvalidNodeCountException {
        if (this.size >= MAX_SIZE) {
            throw new InvalidNodeCountException("Too many nodes! Maximum allowed is " + MAX_SIZE);
        }
        if (this.root == null) {
            this.root = new Node(val);
            this.size++;
            return true;
        }
        // Reject duplicates. Could be optimized (one less traversal).
        if (contains(val)) {
            return false;
        } else {
            this.size++;
            insertNode(this.root, val);
            return true;
        }
    }

    /**
     * Inserts a node with given val into node root, using tree-specific implementations.
     * Does not check for duplicates or increment {@code size}.
     *
     * @param current node to insert new node into
     * @param val     value of new node to insert
     */
    protected abstract void insertNode(Node current, int val);

    /**
     * Deletes the node with the specified value from the tree if it exists.
     * Decrements {@code size}.
     *
     * @return true if val was deleted. false if it doesn't exist.
     */
    public abstract boolean delete(int val);

    /**
     * Returns the Node with the specified value from the tree, or null if absent.
     *
     * @return Node with specified value if found. null if not found.
     */
    public Node find(int val) {
        return findNode(this.root, val);
    }

    private Node findNode(Node root, int val) {
        if (root == null) {
            return null;
        } else if (root.getValue() == val) {
            return root;
        } else if (root.getValue() < val) {
            return findNode(root.getRightChild(), val);
        } else {
            return findNode(root.getLeftChild(), val);
        }
    }

    /**
     * Returns a list of nodes visited while attempting to find specified value.
     *
     * @return a list of visited nodes, ordered from earliest to latest.
     */
    public List<Node> findWithPath(int val) {
        ArrayList<Node> path = new ArrayList<>();
        findWithPathHelper(root, path, val);
        return path;
    }

    private void findWithPathHelper(Node root, List<Node> path, int val) {
        if (root == null) {
            return;
        }
        path.add(root);
        if (root.getValue() < val) {
            findWithPathHelper(root.getRightChild(), path, val);
        } else if (root.getValue() > val){
            findWithPathHelper(root.getLeftChild(), path, val);
        }
    }

    public void clear() {
        root = null;
        size = 0;
    }

    /**
     * Gets the root node for this tree, or returns null if root was not set.
     */
    public Node getRoot() {
        return this.root;
    }

    public int getSize() {
        return this.size;
    }

    /**
     * Gets the in-order successor of the current node.
     * Returns the right child's leftmost node.
     *
     * @param current node to get in-order successor of
     * @return in-order successor node
     */
    protected Node inOrderSuccessor(Node current) {
        current = current.getRightChild();
        while (current.getLeftChild() != null) {
            current = current.getLeftChild();
        }
        return current;
    }

    /**
     * Checks if the tree contains a node with the specified value.
     *
     * @return true if tree contains node with val. false otherwise.
     */
    public boolean contains(int val) {
        return containNode(this.root, val);
    }

    private boolean containNode(Node root, int val) {
        if (root == null) {
            return false;
        } else if (root.getValue() == val) {
            return true;
        } else if (root.getValue() < val) {
            return containNode(root.getRightChild(), val);
        } else {
            return containNode(root.getLeftChild(), val);
        }
    }

    /**
     * Returns a string representation of the tree, using brackets to denote children of a node.
     */
    public String toString() {
        return toStringRecursive(this.root);
    }

    private String toStringRecursive(Node root) {
        if (root == null) {
            return "null";
        } else {
            String leftChild = toStringRecursive(root.getLeftChild());
            String rightChild = toStringRecursive(root.getRightChild());
            return root.getValue() + "[" + leftChild + ", " + rightChild + "]";
        }
    }
}
