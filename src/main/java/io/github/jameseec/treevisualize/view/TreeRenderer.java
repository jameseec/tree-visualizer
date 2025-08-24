package io.github.jameseec.treevisualize.view;

import io.github.jameseec.treevisualize.model.Node;
import io.github.jameseec.treevisualize.model.Tree;
import javafx.geometry.Point2D;
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
 * Base class for tree renderers.
 * Abstracts shared rendering operations between all trees, allowing tree-specific renderers to add
 * tree type-specific features later.
 */
public abstract class TreeRenderer {
    private static final double NODE_RADIUS = 20;
    /** Vertical spacing between tree levels in pixels. */
    private static final double VERTICAL_SPACING = 70;
    /** Base width unit for spacing. */
    private static final double BASE_WIDTH_UNIT = 60;
    private static final double startY = NODE_RADIUS * 2 + 20;
    /** Map to store positions of nodes on contentPane */
    private Map<Integer, Point2D> nodePositions;    // node's value : center position
    private Map<Integer, Circle> nodeCircles;       // node's value : node circle
    protected final Tree tree;
    protected Pane contentPane;
    /** Cache for subtree widths to avoid recalculation. */
    private Map<Node, Integer> widthCache;

    protected TreeRenderer(Pane contentPane, Tree tree) {
        this.contentPane = contentPane;
        this.tree = tree;
        nodePositions = new HashMap<>();
        nodeCircles = new HashMap<>();
    }

    /**
     * Updates the pane by clearing existing content and redrawing all nodes.
     */
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

        // Draw the tree starting from calculated position
        drawSubtree(tree.getRoot(), startX, startY);
    }

    /**
     * Clears the content pane of all drawn elements.
     * Does not reset current tree.
     */
    public void clearContent() {
        contentPane.getChildren().clear();
        nodePositions = new HashMap<>();
        nodeCircles = new HashMap<>();
    }

    /**
     * Highlights the path taken to search for given val in the tree.
     * Each visited node is annotated with a number indicating the visit order.
     *
     * @param val value to show search path for.
     */
    public void showSearchPath(int val) {
        // To render search numbers:
        updatePane();
        List<Node> pathList = this.tree.findWithPath(val);
        Map<Node, Integer> searchOrder = new HashMap<>();
        for (int i = 0; i < pathList.size(); i++) {
            searchOrder.put(pathList.get(i), i + 1);
        }
        renderSearch(searchOrder, tree.getRoot(), val);
    }

    public void showPreOrderTraversal() {
        updatePane();
        int[] visitCount = new int[] {0};
        renderPreOrderTraversal(tree.getRoot(), visitCount);
    }

    public void showInOrderTraversal() {
        updatePane();
        int[] visitCount = new int[] {0};
        renderInOrderTraversal(tree.getRoot(), visitCount);
    }

    public void showPostOrderTraversal() {
        updatePane();
        int[] visitCount = new int[] {0};
        renderPostOrderTraversal(tree.getRoot(), visitCount);
    }


    private void renderPreOrderTraversal(Node node, int[] visitCount) {
        if (node != null) {
            visitCount[0]++;
            drawNodeOrderLabel(node, Integer.toString(visitCount[0]));
            renderPreOrderTraversal(node.getLeftChild(), visitCount);
            renderPreOrderTraversal(node.getRightChild(), visitCount);
        }
    }

    private void renderInOrderTraversal(Node node, int[] visitCount) {
        if (node != null) {
            renderInOrderTraversal(node.getLeftChild(), visitCount);
            visitCount[0]++;
            drawNodeOrderLabel(node, Integer.toString(visitCount[0]));
            renderInOrderTraversal(node.getRightChild(), visitCount);
        }
    }

    private void renderPostOrderTraversal(Node node, int[] visitCount) {
        if (node != null) {
            renderPostOrderTraversal(node.getLeftChild(), visitCount);
            renderPostOrderTraversal(node.getRightChild(), visitCount);
            visitCount[0]++;
            drawNodeOrderLabel(node, Integer.toString(visitCount[0]));
        }
    }

    // Recursively traverses tree and adds labels to show search order and highlight search path.
    private void renderSearch(Map<Node, Integer> searchOrder, Node node, int searchVal) {
        // Uses widthCache from last update operation
        if (node == null) {
            return;
        }

        renderSearchNode(searchOrder, node, searchVal);
        if (node.getValue() < searchVal) {
            renderSearch(searchOrder, node.getRightChild(), searchVal);
        } else if (node.getValue() > searchVal) {
            renderSearch(searchOrder, node.getLeftChild(), searchVal);
        }
        // Stop recursion if we reached searchVal.
    }

    private void renderSearchNode(Map<Node, Integer> searchOrder, Node node, int searchVal) {
        Circle circle = nodeCircles.get(node.getValue());
        if (node.getValue() == searchVal) {
            circle.setFill(Color.LIGHTGREEN);
            circle.setStroke(Color.DARKGREEN);
        } else if (node.getLeftChild() == null && node.getRightChild() == null) {
            // Search unsuccessful: reached leaf node
            circle.setFill(Color.RED);
            circle.setStroke(Color.BLACK);
        } else {
            circle.setFill(Color.LIGHTYELLOW);
            circle.setStroke(Color.GREY);
        }

        int order = searchOrder.get(node);

        drawNodeOrderLabel(node, Integer.toString(order));
    }

    // Draws a label next to the node
    private void drawNodeOrderLabel(Node node, String labelVal) {
        Point2D pos = nodePositions.get(node.getValue());
        double x = pos.getX();
        double y = pos.getY();
        Text order = new Text(labelVal);
        order.setTextOrigin(VPos.CENTER);
        order.setX(x + NODE_RADIUS + 3);
        order.setY(y);
        contentPane.getChildren().add(order);
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
            widthCache.put(node, 2);
            return 2;
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
     * Stores the center positions of all nodes into {@code nodePositions}
     * @param node the root of the subtree to draw
     * @param leftX the leftmost x-coordinate available for this subtree
     * @param y the y-coordinate for this level
     */
    private void drawSubtree(Node node, double leftX, double y) {
        if (node == null) {
            return;
        }

        int leftSubtreeWidth = getCachedWidth(node.getLeftChild());

        double nodeX = leftX + (leftSubtreeWidth * BASE_WIDTH_UNIT);

        double childY = y + VERTICAL_SPACING;

        // left subtree
        if (node.getLeftChild() != null) {
            // Left child positioned at the center of the left subtree's allocated space
            int leftChildLeftWidth = getCachedWidth(node.getLeftChild().getLeftChild());
            double leftChildX = leftX + (leftChildLeftWidth * BASE_WIDTH_UNIT);

            Line leftLine = new Line(nodeX, y, leftChildX, childY);
            leftLine.setStroke(Color.BLACK);
            leftLine.setStrokeWidth(2);
            contentPane.getChildren().add(leftLine);

            // recursively draw left subtree
            drawSubtree(node.getLeftChild(), leftX, childY);
        }

        // right subtree
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
     * Stores position of nodes and node circles into maps.
     *
     * @param node the node to draw
     * @param x the center x-coordinate of the node.
     * @param y the center y-coordinate of the node.
     */
    private void drawNodeCircleAndText(Node node, double x, double y) {
        Circle circle = new Circle(x, y, NODE_RADIUS);
        nodePositions.put(node.getValue(), new Point2D(x, y));
        nodeCircles.put(node.getValue(), circle);
        circle.setFill(Color.WHITE);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);
        contentPane.getChildren().add(circle);

        Text nodeVal = new Text(Integer.toString(node.getValue()));
        nodeVal.setTextAlignment(TextAlignment.CENTER);
        nodeVal.setTextOrigin(VPos.CENTER);

        nodeVal.setX(x - nodeVal.getBoundsInLocal().getWidth() / 2);
        nodeVal.setY(y);
        nodeVal.setFill(Color.BLACK);

        contentPane.getChildren().add(nodeVal);
    }
}
