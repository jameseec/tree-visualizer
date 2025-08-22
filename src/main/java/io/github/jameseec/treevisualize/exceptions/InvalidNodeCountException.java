package io.github.jameseec.treevisualize.exceptions;

/**
 * Thrown when an operation causes the tree to exceed the maximum allowed number of nodes.
 */
public class InvalidNodeCountException extends Exception {
    public InvalidNodeCountException(String message) {
        super(message);
    }
}
