package io.github.jameseec.treevisualize.model;

import io.github.jameseec.treevisualize.exceptions.InvalidNodeCountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AVLTreeTest {

    private AVLTree avl;

    @BeforeEach
    void setUp() {
        avl = new AVLTree();
    }

    // Insertions

    @Test
    void testInsertSingleNode() throws InvalidNodeCountException {
        assertTrue(avl.insert(10));
        assertTrue(avl.contains(10));
        assertEquals(1, avl.getSize());
    }

    @Test
    void testInsertMultipleNodesMaintainsBalance() throws InvalidNodeCountException {
        // Insert nodes in ascending order (triggers rotations)
        assertTrue(avl.insert(10));
        assertTrue(avl.insert(20));
        assertTrue(avl.insert(30)); // should cause single left rotation at root

        assertTrue(avl.contains(10));
        assertTrue(avl.contains(20));
        assertTrue(avl.contains(30));

        Node root = avl.getRoot();
        assertEquals(20, root.getValue()); // root should be balanced after rotation
    }

    @Test
    void testLLRotationOnInsert() throws InvalidNodeCountException {
        avl.insert(30);
        avl.insert(20);
        avl.insert(10); // triggers LL

        Node root = avl.getRoot();
        assertEquals(20, root.getValue());
        assertEquals(10, root.getLeftChild().getValue());
        assertEquals(30, root.getRightChild().getValue());

        checkAVLInvariant(root);

        String expected = "20[10[null, null], 30[null, null]]";
        assertEquals(expected, avl.toString());
    }

    @Test
    void testRRRotationOnInsert() throws InvalidNodeCountException {
        avl.insert(10);
        avl.insert(20);
        avl.insert(30); // triggers RR

        Node root = avl.getRoot();
        assertEquals(20, root.getValue());
        assertEquals(10, root.getLeftChild().getValue());
        assertEquals(30, root.getRightChild().getValue());

        checkAVLInvariant(root);

        String expected = "20[10[null, null], 30[null, null]]";
        assertEquals(expected, avl.toString());
    }

    @Test
    void testLRRotationOnInsert() throws InvalidNodeCountException {
        avl.insert(30);
        avl.insert(10);
        avl.insert(20); // triggers LR

        Node root = avl.getRoot();
        assertEquals(20, root.getValue());
        assertEquals(10, root.getLeftChild().getValue());
        assertEquals(30, root.getRightChild().getValue());

        checkAVLInvariant(root);

        String expected = "20[10[null, null], 30[null, null]]";
        assertEquals(expected, avl.toString());
    }

    @Test
    void testRLRotationOnInsert() throws InvalidNodeCountException {
        avl.insert(10);
        avl.insert(30);
        avl.insert(20); // triggers RL

        Node root = avl.getRoot();
        assertEquals(20, root.getValue());
        assertEquals(10, root.getLeftChild().getValue());
        assertEquals(30, root.getRightChild().getValue());

        checkAVLInvariant(root);

        String expected = "20[10[null, null], 30[null, null]]";
        assertEquals(expected, avl.toString());
    }

    // Deletions

    @Test
    void testDeleteNodeWithOneChild() throws InvalidNodeCountException {
        avl.insert(10);
        avl.insert(5);
        avl.insert(15);
        avl.insert(12);

        assertTrue(avl.delete(15));
        assertFalse(avl.contains(15));
        assertTrue(avl.contains(12));
        assertEquals(3, avl.getSize());
    }

    @Test
    void testDeleteNullNode() {
        assertFalse(avl.delete(4));
    }


    @Test
    void testDeleteLeafNodeWithRotation() throws InvalidNodeCountException {
        avl.insert(30);
        avl.insert(20);
        avl.insert(40);
        avl.insert(10);

        assertTrue(avl.delete(10));

        Node root = avl.getRoot();
        checkAVLInvariant(root);

        String expected = "30[20[null, null], 40[null, null]]";
        assertEquals(expected, avl.toString());
    }

    @Test
    void testDeleteNodeWithOneChildAndRotation() throws InvalidNodeCountException {
        avl.insert(30);
        avl.insert(20);
        avl.insert(40);
        avl.insert(10);
        avl.insert(25);

        assertTrue(avl.delete(20));

        Node root = avl.getRoot();
        checkAVLInvariant(root);

        String expected = "30[25[10[null, null], null], 40[null, null]]";
        assertEquals(expected, avl.toString());
    }

    @Test
    void testDeleteNodeWithTwoChildrenAndRotation() throws InvalidNodeCountException {
        avl.insert(50);
        avl.insert(30);
        avl.insert(70);
        avl.insert(10);
        avl.insert(40);
        avl.insert(60);
        avl.insert(80);

        assertTrue(avl.delete(30)); // node with two children

        Node root = avl.getRoot();
        assertFalse(avl.contains(30));
        checkAVLInvariant(root);

        String expected = "50[40[10[null, null], null], 70[60[null, null], 80[null, null]]]";
        assertEquals(expected, avl.toString());
    }

    @Test
    void testDeleteRootWithTwoChildren() throws InvalidNodeCountException {
        avl.insert(50);
        avl.insert(30);
        avl.insert(70);
        avl.insert(20);
        avl.insert(40);
        avl.insert(60);
        avl.insert(80);

        assertTrue(avl.delete(50));

        Node root = avl.getRoot();
        checkAVLInvariant(root);

        String expected = "60[30[20[null, null], 40[null, null]], 70[null, 80[null, null]]]";
        assertEquals(expected, avl.toString());
    }

    // Duplication and max size

    @Test
    void testInsertDuplicateReturnsFalse() throws InvalidNodeCountException {
        assertTrue(avl.insert(10));
        assertFalse(avl.insert(10));

        checkAVLInvariant(avl.getRoot());
        String expected = "10[null, null]";
        assertEquals(expected, avl.toString());
    }

    @Test
    void testInsertThrowsOnMaxSizeExceeded() throws InvalidNodeCountException {
        for (int i = 0; i < AVLTree.MAX_SIZE; i++) {
            assertTrue(avl.insert(i));
        }
        assertThrows(io.github.jameseec.treevisualize.exceptions.InvalidNodeCountException.class,
                () -> avl.insert(999));
    }

    /**
     * Recursively verifies AVL invariants: node heights correct and balance factor in [-1,1]
     */
    private void checkAVLInvariant(Node node) {
        if (node == null) return;

        Node left = node.getLeftChild();
        Node right = node.getRightChild();

        int leftHeight = (left == null) ? -1 : left.getHeight();
        int rightHeight = (right == null) ? -1 : right.getHeight();

        // check node height
        assertEquals(1 + Math.max(leftHeight, rightHeight), node.getHeight(),
                "Height mismatch at node " + node.getValue());

        // check balance
        int balance = leftHeight - rightHeight;
        assertTrue(balance >= -1 && balance <= 1,
                "Balance factor out of range at node " + node.getValue());

        checkAVLInvariant(left);
        checkAVLInvariant(right);
    }
}
