package io.github.jameseec.treevisualize.model;

import io.github.jameseec.treevisualize.exceptions.InvalidNodeCountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BinarySearchTreeTest {
    private BinarySearchTree tree;

    @BeforeEach
    void setUp() throws InvalidNodeCountException {
        tree = new BinarySearchTree();
        tree.insert(5);
        tree.insert(3);
        tree.insert(7);
    }

    @Test
    void testInsertAndContains() {
        assertTrue(tree.contains(5));
        assertTrue(tree.contains(3));
        assertTrue(tree.contains(7));
        assertFalse(tree.contains(10));
    }

    @Test
    void testDuplicateInsert() throws InvalidNodeCountException {
        assertFalse(tree.insert(5)); // duplicate root
        assertEquals(3, tree.getSize());
    }

    @Test
    void testDeleteLeafNode() {
        assertTrue(tree.delete(3)); // delete leaf
        assertEquals(2, tree.getSize());
        assertFalse(tree.contains(3));
    }

    @Test
    void testDeleteNodeWithOneChild() throws InvalidNodeCountException {
        tree.insert(2); // make 3 have a left child
        assertTrue(tree.delete(3));
        assertEquals(3, tree.getSize());
        assertTrue(tree.contains(2));
        assertFalse(tree.contains(3));
    }

    @Test
    void testDeleteNodeWithTwoChildren() throws InvalidNodeCountException {
        tree.insert(6);
        tree.insert(8);
        assertTrue(tree.delete(7));
        assertEquals(4, tree.getSize());
        assertFalse(tree.contains(7));
        assertTrue(tree.contains(6));
        assertTrue(tree.contains(8));
    }

    @Test
    void testDeleteNonExistentNode() {
        assertFalse(tree.delete(10));
        assertEquals(3, tree.getSize());
    }

    @Test
    void testFind() {
        Node node = tree.find(7);
        assertNotNull(node);
        assertEquals(7, node.getValue());
        assertNull(tree.find(10));
    }

    @Test
    void testFindWithPath() throws InvalidNodeCountException {
        tree.insert(6);
        var path = tree.findWithPath(6);
        assertEquals(3, path.size());
        assertEquals(5, path.get(0).getValue());
        assertEquals(7, path.get(1).getValue());
        assertEquals(6, path.get(2).getValue());

        BinarySearchTree bst = new BinarySearchTree();
        bst.insert(10);
        bst.insert(5);
        bst.insert(15);
        bst.insert(12);

        List<Node> pathInternal = bst.findWithPath(15);
        assertEquals(List.of(
                bst.getRoot(),                     // 10
                bst.getRoot().getRightChild()     // 15
        ), pathInternal);

        List<Node> notFoundPath = bst.findWithPath(99);
        assertEquals(List.of(
                bst.getRoot(),                  // 10
                bst.getRoot().getRightChild()  // 15
        ), notFoundPath);
    }

    @Test
    void testClear() {
        tree.clear();
        assertEquals(0, tree.getSize());
        assertNull(tree.getRoot());
    }

    @Test
    void testExceedsMaxSize() throws InvalidNodeCountException {
        // reset tree since our @BeforeEach inserts already
        tree.clear();
        for (int i = 1; i <= Tree.MAX_SIZE; i++) {
            assertTrue(tree.insert(i));
        }
        assertThrows(InvalidNodeCountException.class, () -> tree.insert(Tree.MAX_SIZE + 1));
    }
}

