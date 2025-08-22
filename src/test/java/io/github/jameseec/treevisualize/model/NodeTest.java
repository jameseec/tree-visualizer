package io.github.jameseec.treevisualize.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NodeTest {
    private Node node1;
    private Node node2;

    @BeforeEach
    void setUp() {
        node1 = new Node(1);
        node2 = new Node(2);
    }
    @Test
    void testNodeProperties() {
        assertEquals(1, node1.getValue());
        assertNull(node1.getLeftChild());
        assertNull(node1.getRightChild());
        assertNull(node1.getColor());
        node1.setColor(Color.RED);
        assertEquals(Color.RED, node1.getColor());
        node2.setColor(Color.BLACK);
        assertEquals(Color.BLACK, node2.getColor());
    }

    @Test
    void testNodeChildren() {
        node1.setLeftChild(node2);
        assertNull(node1.getRightChild());
        assertEquals(node2, node1.getLeftChild());
        assertEquals(2, node1.getLeftChild().getValue());
        assertNull(node1.getLeftChild().getLeftChild());
        assertNull(node1.getLeftChild().getRightChild());
    }

}