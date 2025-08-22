package io.github.jameseec.treevisualize.view;

import io.github.jameseec.treevisualize.model.BinarySearchTree;
import io.github.jameseec.treevisualize.model.Node;
import io.github.jameseec.treevisualize.model.Tree;
import javafx.geometry.VPos;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Renderer for Binary Search Trees with phantom nodes.
 * Each node calculates its position based on the width requirements of its subtrees,
 * treating missing children as phantom nodes to maintain balanced spacing.
 */
public class BSTRenderer extends TreeRenderer {

    private final BinarySearchTree tree;

    /** Cache for subtree widths to avoid recalculation. */
    private Map<Node, Integer> widthCache;

    private static final double NODE_RADIUS = 20;

    /** Vertical spacing between tree levels in pixels. */
    private static final double VERTICAL_SPACING = 70;

    /** Base width unit for spacing. */
    private static final double BASE_WIDTH_UNIT = 60;

    /** Map relating node to visit order for rendering find operations */
    private Map<Node, Integer> searchOrder;

    /** Target node of find operation (null if it was unsuccessful) */
    private Node targetNodeSearch;

    private Node lastNodeSearch;

    /**
     * Constructs a BSTRenderer for the specified tree and content pane.
     *
     * @param tree the binary search tree to render
     * @param contentPane the pane where the tree will be drawn
     * @throws IllegalArgumentException if tree is not a BinarySearchTree instance
     */
    public BSTRenderer(Tree tree, Pane contentPane) {
        super(contentPane);
        if (!(tree instanceof BinarySearchTree)) {
            throw new IllegalArgumentException("Tree must be a BinarySearchTree");
        }
        this.tree = (BinarySearchTree) tree;
    }

    /**
     * Updates the pane by clearing existing content and redrawing all nodes.
     */
    @Override
    public void updatePane() {
        clearContent();
        if (tree.getRoot() != null) {
            drawTree();
        }
    }

    /**
     * Draws the tree by pre-calculating all subtree widths and calls recursive helper.
     * Highlights nodes and annotates with search order if searchOrder is not null.
     */
    private void drawTree() {
        // Pre-calculate all subtree widths once
        widthCache = new HashMap<>();
        calculateAndCacheWidths(tree.getRoot());

        // Calculate total width needed for the entire tree
        int totalWidth = widthCache.get(tree.getRoot());
        double treePixelWidth = totalWidth * BASE_WIDTH_UNIT;

        double centerX = contentPane.getWidth() / 2;
        double startX = centerX - treePixelWidth / 2;
        double startY = NODE_RADIUS * 2 + 20;

        // Draw the tree starting from calculated position
        drawSubtree(tree.getRoot(), startX, startY);
    }

    /**
     * Clears the content pane of all drawn elements.
     * Does not reset current tree.
     */
    @Override
    public void clearContent() {
        contentPane.getChildren().clear();
    }

    /**
     * Highlights the path taken to search for given val in the tree.
     * Each visited node is annotated with a number indicating the visit order.
     *
     * @param val value to show search path for.
     */
    @Override
    public void showSearchPath(int val) {
        // To render search numbers:
        List<Node> pathList = this.tree.findWithPath(val);
        searchOrder = new HashMap<>();
        for (int i = 0; i < pathList.size(); i++) {
            searchOrder.put(pathList.get(i), i + 1);
        }
        targetNodeSearch = (pathList.getLast().getValue() == val) ? pathList.getLast() : null;
        lastNodeSearch = pathList.getLast();
        updatePane();
        searchOrder = null;
        targetNodeSearch = null;
    }

    /**
     * Calculates and caches the widths for all subtrees.
     * Done in a single pass to avoid redundant calculations.
     * Width represents number of horizontal "units" required to draw the subtree.
     *
     * @param node the root of the subtree (can be null for phantom nodes)
     * @return the width requirement for this subtree
     */
    private int calculateAndCacheWidths(Node node) {
        // Phantom node: base width
        if (node == null) {
            return 1;
        }

        // Leaf node: base width
        if (node.getLeftChild() == null && node.getRightChild() == null) {
            widthCache.put(node, 1);
            return 1;
        }

        // Internal node: sum of children's widths (including phantom nodes)
        int leftWidth = calculateAndCacheWidths(node.getLeftChild());
        int rightWidth = calculateAndCacheWidths(node.getRightChild());
        int totalWidth = leftWidth + rightWidth;

        widthCache.put(node, totalWidth);
        return totalWidth;
    }

    /**
     * Gets the cached width for a subtree.
     *
     * @param node the root of the subtree (null for phantom nodes)
     * @return the cached width, or 1 for phantom nodes
     */
    private int getCachedWidth(Node node) {
        if (node == null) return 1;
        return widthCache.get(node);
    }

    /**
     * Draws a subtree at the specified position.
     * The node's position is based on the width requirements of its subtrees.
     *
     * @param node the root of the subtree to draw
     * @param leftX the leftmost x-coordinate available for this subtree
     * @param y the y-coordinate for this level
     */
    private void drawSubtree(Node node, double leftX, double y) {
        if (node == null) {
            return;
        }

        // Get cached widths for positioning
        int leftSubtreeWidth = getCachedWidth(node.getLeftChild());

        // Position current node between left and right subtrees
        double nodeX = leftX + (leftSubtreeWidth * BASE_WIDTH_UNIT);

        double childY = y + VERTICAL_SPACING;

        // Draw left subtree
        if (node.getLeftChild() != null) {
            // Left child positioned at the center of the left subtree's allocated space
            int leftChildLeftWidth = getCachedWidth(node.getLeftChild().getLeftChild());
            double leftChildX = leftX + (leftChildLeftWidth * BASE_WIDTH_UNIT);

            Line leftLine = new Line(nodeX, y, leftChildX, childY);
            leftLine.setStroke(Color.BLACK);
            leftLine.setStrokeWidth(2);
            contentPane.getChildren().add(leftLine);

            // Recursively draw left subtree
            drawSubtree(node.getLeftChild(), leftX, childY);
        }

        // Draw right subtree
        if (node.getRightChild() != null) {
            // Right subtree starts after the left subtree space
            double rightSubtreeStartX = leftX + (leftSubtreeWidth * BASE_WIDTH_UNIT);
            // Right child is positioned at the center of the right subtree's allocated space
            int rightChildLeftWidth = getCachedWidth(node.getRightChild().getLeftChild());
            double rightChildX = rightSubtreeStartX + (rightChildLeftWidth * BASE_WIDTH_UNIT);

            Line rightLine = new Line(nodeX, y, rightChildX, childY);
            rightLine.setStroke(Color.BLACK);
            rightLine.setStrokeWidth(2);
            contentPane.getChildren().add(rightLine);

            // Recursively draw right subtree
            drawSubtree(node.getRightChild(), rightSubtreeStartX, childY);
        }
        drawNodeCircleAndText(node, nodeX, y);
    }

    /**
     * Draws a single node (circle + text).
     * If {@code searchOrder} is not null and the node exists, highlights the node and
     * annotates it with its corresponding number in the search sequence.
     *
     * @param node the node to draw
     * @param x the center x-coordinate of the node.
     * @param y the center y-coordinate of the node.
     */
    private void drawNodeCircleAndText(Node node, double x, double y) {
        Circle circle = new Circle(x, y, NODE_RADIUS);

        // If currently searching and current node was visited,
        // highlight and label with the visit order.
        if (searchOrder != null && searchOrder.containsKey(node)) {
            searchRender(node, x, y, circle);
        } else {
            circle.setFill(Color.LIGHTBLUE);
            circle.setStroke(Color.DARKBLUE);
        }

        circle.setStrokeWidth(2);
        contentPane.getChildren().add(circle);

        Text nodeVal = new Text(Integer.toString(node.getValue()));
        nodeVal.setTextAlignment(TextAlignment.CENTER);
        nodeVal.setTextOrigin(VPos.CENTER);

        nodeVal.setX(x - nodeVal.getBoundsInLocal().getWidth() / 2);
        nodeVal.setY(y);
        nodeVal.setFill(Color.DARKBLUE);

        contentPane.getChildren().add(nodeVal);
    }

    private void searchRender(Node node, double x, double y, Circle circle) {
        if (node == targetNodeSearch) {
            circle.setFill(Color.LIGHTGREEN);
            circle.setStroke(Color.DARKGREEN);
        } else if (node == lastNodeSearch) {
            circle.setFill(Color.RED);
            circle.setStroke(Color.BLACK);
        } else {
            circle.setFill(Color.LIGHTYELLOW);
            circle.setStroke(Color.BLACK);
        }

        Text order = new Text(Integer.toString(searchOrder.get(node)));
        order.setTextOrigin(VPos.CENTER);
        order.setX(x + NODE_RADIUS + 5);
        order.setY(y);
        contentPane.getChildren().add(order);
    }
}