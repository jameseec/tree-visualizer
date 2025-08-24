package io.github.jameseec.treevisualize.view;


import io.github.jameseec.treevisualize.model.AVLTree;
import io.github.jameseec.treevisualize.model.Tree;
import javafx.scene.layout.Pane;

/**
 * Renderer for AVL tree with phantom nodes.
 */
public class AVLRenderer extends TreeRenderer {
    private final AVLTree avlTree;

    public AVLRenderer(Tree tree, Pane contentPane) {
        super(contentPane, tree);
        avlTree = (AVLTree) tree;
    }

}
