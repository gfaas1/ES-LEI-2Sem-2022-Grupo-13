/*
 * (C) Copyright 2018, by Timofey Chudakov and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package org.jgrapht.traverse;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.Pseudograph;
import org.junit.Test;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the {@link LexBreadthFirstIterator}
 *
 * @author Timofey Chudakov
 */
public class LexBreadthFirstIteratorTest {

    /**
     * Tests iterator on empty graph.
     */
    @Test(expected = NoSuchElementException.class)
    public void testLexicographicalBfsIterator1() {
        Graph<Integer, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        LexBreadthFirstIterator<Integer, DefaultEdge> iterator = new LexBreadthFirstIterator<>(graph);

        assertFalse(iterator.hasNext());

        iterator.next();
    }

    /**
     * Tests iterator for basic invariants.
     */
    @Test
    public void testLexicographicalBfsIterator2() {
        Graph<Integer, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addVertex(4);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(3, 4);
        LexBreadthFirstIterator<Integer, DefaultEdge> iterator = new LexBreadthFirstIterator<>(graph);
        Set<Integer> returned = new HashSet<>();

        assertTrue(iterator.hasNext());
        Integer vertex = iterator.next();
        returned.add(vertex);
        assertTrue(graph.containsVertex(vertex));

        assertTrue(iterator.hasNext());
        vertex = iterator.next();
        returned.add(vertex);
        assertTrue(graph.containsVertex(vertex));

        assertTrue(iterator.hasNext());
        vertex = iterator.next();
        returned.add(vertex);
        assertTrue(graph.containsVertex(vertex));

        assertTrue(iterator.hasNext());
        vertex = iterator.next();
        returned.add(vertex);
        assertTrue(graph.containsVertex(vertex));

        assertTrue(graph.vertexSet().equals(returned));

        assertFalse(iterator.hasNext());
    }

    /**
     * Tests iterator on disconnected graph.
     */
    @Test
    public void testLexicographicalBfsIterator3(){
        Graph<String, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        graph.addVertex("a");
        graph.addVertex("b");
        graph.addVertex("c");
        graph.addVertex("d");
        LexBreadthFirstIterator<String, DefaultEdge> iterator = new LexBreadthFirstIterator<>(graph);

        Set<String> returned = new HashSet<>();

        assertTrue(iterator.hasNext());
        String vertex = iterator.next();
        returned.add(vertex);
        assertTrue(graph.containsVertex(vertex));

        assertTrue(iterator.hasNext());
        vertex = iterator.next();
        returned.add(vertex);
        assertTrue(graph.containsVertex(vertex));

        assertTrue(iterator.hasNext());
        vertex = iterator.next();
        returned.add(vertex);
        assertTrue(graph.containsVertex(vertex));

        assertTrue(iterator.hasNext());
        vertex = iterator.next();
        returned.add(vertex);
        assertTrue(graph.containsVertex(vertex));

        assertTrue(graph.vertexSet().equals(returned));

        assertFalse(iterator.hasNext());
    }

    /**
     * Tests iterator on pseudograph.
     */
    @Test
    public void testLexicographicalBfsIterator4(){
        Graph<Integer, DefaultEdge> graph = new Pseudograph<>(DefaultEdge.class);
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addEdge(1,1);
        graph.addEdge(1,2);
        graph.addEdge(1,2);
        graph.addEdge(1,3);
        graph.addEdge(1,3);
        graph.addEdge(2,3);
        graph.addEdge(2,3);
        graph.addEdge(3,3);
        graph.addEdge(3,3);
        LexBreadthFirstIterator<Integer, DefaultEdge> iterator = new LexBreadthFirstIterator<>(graph);
        Set<Integer> returned = new HashSet<>();

        assertTrue(iterator.hasNext());
        Integer vertex = iterator.next();
        returned.add(vertex);
        assertTrue(graph.containsVertex(vertex));

        assertTrue(iterator.hasNext());
        vertex = iterator.next();
        returned.add(vertex);
        assertTrue(graph.containsVertex(vertex));

        assertTrue(iterator.hasNext());
        vertex = iterator.next();
        returned.add(vertex);
        assertTrue(graph.containsVertex(vertex));

        assertTrue(graph.vertexSet().equals(returned));

        assertFalse(iterator.hasNext());

    }
}

