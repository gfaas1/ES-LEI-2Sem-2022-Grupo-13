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
import org.junit.Test;

import static org.junit.Assert.*;

public class LexicographicalBfsIteratorTest {
    @Test
    public void testLexBFSIterator1(){
        Graph<Integer, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addVertex(4);
        graph.addEdge(1,2);
        graph.addEdge(2,3);
        graph.addEdge(3,4);
        LexicographicalBfsIterator<Integer, DefaultEdge> iterator = new LexicographicalBfsIterator<>(graph);

        assertTrue(iterator.hasNext());
        Integer vertex = iterator.next();
        assertTrue(graph.containsVertex(vertex));

        assertTrue(iterator.hasNext());
        vertex = iterator.next();
        assertTrue(graph.containsVertex(vertex));

        assertTrue(iterator.hasNext());
        vertex = iterator.next();
        assertTrue(graph.containsVertex(vertex));

        assertTrue(iterator.hasNext());
        vertex = iterator.next();
        assertTrue(graph.containsVertex(vertex));

        assertFalse(iterator.hasNext());
    }
}
