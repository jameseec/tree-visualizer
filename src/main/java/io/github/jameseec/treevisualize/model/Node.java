package io.github.jameseec.treevisualize.model;

/**
 * Node class representing a node in a binary tree
 */
public class Node {
    private int value;
    private int height; // should only used by AVL trees
    private Color color; // only used by Red-Black trees
    private Node leftChild;
    private Node rightChild;

    /**
     * Constructs a node with specified value and no children.
     * @param value value to assign to node.
     */
    public Node(int value) {
        this.value = value;
        this.leftChild = null;
        this.rightChild = null;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Node getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(Node leftChild) {
        this.leftChild = leftChild;
    }

    public Node getRightChild() {
        return rightChild;
    }

    public void setRightChild(Node rightChild) {
        this.rightChild = rightChild;
    }

    /**
     * Getter for height. This should only be used by AVL Trees.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Setter for height. This should only be used by AVL trees.
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Getter for color. This should only be used by red-black trees.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Setter for color. This should only be used by red-black trees.
     */
    public void setColor(Color color) {
        this.color = color;
    }
}
