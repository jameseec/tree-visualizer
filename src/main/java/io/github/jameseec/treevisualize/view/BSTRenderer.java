package io.github.jameseec.treevisualize.view;

import io.github.jameseec.treevisualize.model.BinarySearchTree;
import io.github.jameseec.treevisualize.model.Tree;
import javafx.scene.layout.Pane;

/**
 * Renderer for Binary Search Trees with phantom nodes.
 * Each node calculates its position based on the width requirements of its subtrees,
 * treating missing children as phantom nodes to maintain balanced spacing.
 */
public class BSTRenderer extends TreeRenderer {
    private final BinarySearchTree bst;
    /**
     * Constructs a BSTRenderer for the specified tree and content pane.
     *
     * @param tree the binary search tree to render
     * @param contentPane the pane where the tree will be drawn
     * @throws IllegalArgumentException if tree is not a BinarySearchTree instance
     */
    public BSTRenderer(Tree tree, Pane contentPane) {
        super(contentPane, tree);
        if (!(tree instanceof BinarySearchTree)) {
            throw new IllegalArgumentException("Tree must be a BinarySearchTree");
        }
        this.bst = (BinarySearchTree) tree;
    }

}