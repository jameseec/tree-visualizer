package io.github.jameseec.treevisualize.controller;

import io.github.jameseec.treevisualize.exceptions.InvalidNodeCountException;
import io.github.jameseec.treevisualize.model.AVLTree;
import io.github.jameseec.treevisualize.model.BinarySearchTree;
import io.github.jameseec.treevisualize.model.Tree;
import io.github.jameseec.treevisualize.view.AVLRenderer;
import io.github.jameseec.treevisualize.view.BSTRenderer;
import io.github.jameseec.treevisualize.view.TreeRenderer;
import io.github.jameseec.treevisualize.view.ZoomPanPane;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class VisualizeController {
    @FXML
    private Pane contentPane;
    @FXML
    private StackPane viewportStack;
    @FXML
    private TextField addField;
    @FXML
    private TextField deleteField;
    @FXML
    private TextField findField;
    @FXML
    private Label infoLabel;

    private Tree currentTree;
    private TreeRenderer currentRenderer;
    private ZoomPanPane zoomPanPane;
    private Stage stage;
    public VisualizeController() {
        System.out.println("Controller created.");
    }

    public void initialize() {
        // Wrap contentPane with a ZoomPanPane
        viewportStack.getChildren().removeFirst();
        zoomPanPane = new ZoomPanPane(contentPane);
        viewportStack.getChildren().addFirst(zoomPanPane);

        currentTree = new BinarySearchTree();
        System.out.println("New BinarySearchTree created.");
        currentRenderer = new BSTRenderer(currentTree, contentPane);
        addTextFormatters();
        Platform.runLater(() -> zoomPanPane.resetView());
        Platform.runLater(() -> stage = (Stage) infoLabel.getScene().getWindow());
    }

    /**
     * Adds text formatters to all fields to allow only integers.
     */
    private void addTextFormatters() {
        // Regex filter: allow optional "-" followed by digits
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("-?\\d*")) {
                return change;
            }
            return null;
        };

        addField.setTextFormatter(new TextFormatter<>(integerFilter));
        deleteField.setTextFormatter(new TextFormatter<>(integerFilter));
        findField.setTextFormatter(new TextFormatter<>(integerFilter));
    }

    public void onAddNode() {
        handleIntInput(addField, value -> {
            try {
                if (currentTree.insert(value)) {
                    currentRenderer.updatePane();
                } else {
                    infoLabel.setText("The value " + value + " is already in the tree!");
                }
            } catch (InvalidNodeCountException e) {
                infoLabel.setText(e.getMessage());
            }
        });
    }

    public void onDeleteNode() {
        handleIntInput(deleteField, value -> {
            if (currentTree.delete(value)) {
                currentRenderer.updatePane();
            } else {
                infoLabel.setText("The value " + value + " is not in the tree!");
            }
        });
    }

    public void onFindNode() {
        handleIntInput(findField, value -> {
            currentRenderer.showSearchPath(value);
            if (currentTree.contains(value)) {
                infoLabel.setText("Found " + value + " in the tree!");
            } else {
                infoLabel.setText("Value " + value + " not found.");
            }
        });
    }

    public void onClear() {
        currentTree.clear();
        zoomPanPane.resetView();
        currentRenderer.updatePane();
        infoLabel.setText("Tree has been cleared!");
    }

    public void onResetZoom() {
        zoomPanPane.resetZoom();
    }

    public void onResetPanning() {
        zoomPanPane.resetPanning();
    }

    public void onSwitchAVL() {
        stage.setTitle("AVL Tree Visualizer");
        currentTree = new AVLTree();
        System.out.println("New AVL Tree created.");

        currentRenderer = new AVLRenderer(currentTree, contentPane);
        currentRenderer.updatePane();

        zoomPanPane.resetView();
        infoLabel.setText("Switched to AVL Tree!");
    }

    public void onSwitchBST() {
        stage.setTitle("Simple BST visualizer");
        currentTree = new BinarySearchTree();
        System.out.println("New BinarySearchTree created.");

        currentRenderer = new BSTRenderer(currentTree, contentPane);
        currentRenderer.updatePane();

        zoomPanPane.resetView();

        infoLabel.setText("Switched to simple binary search tree!");
    }

    public void onInOrder() {
        currentRenderer.showInOrderTraversal();
        infoLabel.setText("Showing In-Order Traversal.");
    }

    public void onPreOrder() {
        currentRenderer.showPreOrderTraversal();
        infoLabel.setText("Showing Pre-Order Traversal.");
    }

    public void onPostOrder() {
        currentRenderer.showPostOrderTraversal();
        infoLabel.setText("Showing Post-Order Traversal.");
    }

    /**
     * Helper for parsing an int from a text field, then applying a function if successful.
     *
     * @param field TextField to attempt to parse int from.
     * @param action function to perform on int value from text field
     */
    private void handleIntInput(TextField field, Consumer<Integer> action) {
        try {
            int value = Integer.parseInt(field.getText());
            action.accept(value);
            field.clear();
        } catch (NumberFormatException e) {
            infoLabel.setText("Not a valid number!");
        }
    }
}