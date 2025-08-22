package io.github.jameseec.treevisualize.view;

import javafx.scene.layout.Pane;

/**
 * Base class for tree renderers.
 * Abstracts common rendering logic for all tree types, allowing tree-specific renderers to add
 * tree type-specific features later.
 */
public abstract class TreeRenderer {
    protected Pane contentPane;

    protected TreeRenderer(Pane contentPane) {
        this.contentPane = contentPane;
    }

    /**
     * Updates the pane and redraws all nodes.
     */
    public abstract void updatePane();

    /**
     * Clears pane of all nodes.
     */
    public abstract void clearContent();

    /**
     * Highlights the path taken to search for given val in the tree.
     * Each visited node is annotated with a number indicating the visit order.
     *
     * @param val value to show search path for.
     */
    public abstract void showSearchPath(int val);

}
