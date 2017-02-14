/*
 * (C) Copyright 2017-2017, by Dimitrios Michail and Contributors.
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
package org.jgrapht.generate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.IntegerVertexFactory;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Test;

/**
 * Tests for {@link BarabasiAlbertGraphGenerator}.
 * 
 * @author Dimitrios Michail
 */
public class BarabasiAlbertGraphGeneratorTest
{
    @Test
    public void testBadParameters()
    {
        try {
            new BarabasiAlbertGraphGenerator<>(0, 10, 100);
            fail("Bad parameter");
        } catch (IllegalArgumentException e) {
        }

        try {
            new BarabasiAlbertGraphGenerator<>(2, 0, 100);
            fail("Bad parameter");
        } catch (IllegalArgumentException e) {
        }

        try {
            new BarabasiAlbertGraphGenerator<>(2, 3, 100);
            fail("Bad parameter");
        } catch (IllegalArgumentException e) {
        }

        try {
            new BarabasiAlbertGraphGenerator<>(3, 2, 2);
            fail("Bad parameter");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testUndirected()
    {
        final long seed = 5;

        GraphGenerator<Integer, DefaultEdge, Integer> gen =
            new BarabasiAlbertGraphGenerator<>(3, 2, 10, seed);
        Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        gen.generateGraph(g, new IntegerVertexFactory(), null);

        int[][] edges = { { 0, 1 }, { 0, 2 }, { 1, 2 }, { 3, 2 }, { 3, 1 }, { 4, 1 }, { 4, 3 },
            { 5, 2 }, { 5, 3 }, { 6, 3 }, { 6, 0 }, { 7, 3 }, { 7, 0 }, { 8, 1 }, { 8, 4 },
            { 9, 3 }, { 9, 4 } };

        assertEquals(10, g.vertexSet().size());
        for (int[] e : edges) {
            assertTrue(g.containsEdge(e[0], e[1]));
        }
        assertEquals(edges.length, g.edgeSet().size());
    }

    @Test
    public void testUndirectedWithOneInitialNode()
    {
        final long seed = 7;

        GraphGenerator<Integer, DefaultEdge, Integer> gen =
            new BarabasiAlbertGraphGenerator<>(1, 1, 20, seed);
        Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        gen.generateGraph(g, new IntegerVertexFactory(), null);

        int[][] edges = { { 1, 0 }, { 2, 1 }, { 3, 2 }, { 4, 3 }, { 5, 2 }, { 6, 3 }, { 7, 3 },
            { 8, 3 }, { 9, 3 }, { 10, 4 }, { 11, 0 }, { 12, 5 }, { 13, 7 }, { 14, 3 }, { 15, 3 },
            { 16, 12 }, { 17, 2 }, { 18, 10 }, { 19, 3 } };

        assertEquals(20, g.vertexSet().size());
        for (int[] e : edges) {
            assertTrue(g.containsEdge(e[0], e[1]));
        }
        assertEquals(edges.length, g.edgeSet().size());
    }

    @Test
    public void testDirected()
    {
        final long seed = 5;

        GraphGenerator<Integer, DefaultEdge, Integer> gen =
            new BarabasiAlbertGraphGenerator<>(3, 2, 10, seed);
        Graph<Integer, DefaultEdge> g = new SimpleDirectedGraph<>(DefaultEdge.class);
        gen.generateGraph(g, new IntegerVertexFactory(), null);

        int[][] edges = { { 0, 1 }, { 1, 0 }, { 0, 2 }, { 2, 0 }, { 1, 2 }, { 2, 1 }, { 3, 2 },
            { 3, 1 }, { 4, 1 }, { 4, 3 }, { 5, 2 }, { 5, 3 }, { 6, 3 }, { 6, 0 }, { 7, 3 },
            { 7, 0 }, { 8, 1 }, { 8, 4 }, { 9, 3 }, { 9, 4 } };

        assertEquals(10, g.vertexSet().size());
        for (int[] e : edges) {
            assertTrue(g.containsEdge(e[0], e[1]));
        }
        assertEquals(edges.length, g.edgeSet().size());
    }

    @Test
    public void testDirectedWithOneInitialNode()
    {
        final long seed = 13;

        GraphGenerator<Integer, DefaultEdge, Integer> gen =
            new BarabasiAlbertGraphGenerator<>(1, 1, 20, seed);
        Graph<Integer, DefaultEdge> g = new SimpleDirectedGraph<>(DefaultEdge.class);
        gen.generateGraph(g, new IntegerVertexFactory(), null);

        int[][] edges = { { 1, 0 }, { 2, 0 }, { 3, 1 }, { 4, 3 }, { 5, 0 }, { 6, 0 }, { 7, 1 },
            { 8, 0 }, { 9, 4 }, { 10, 5 }, { 11, 0 }, { 12, 5 }, { 13, 1 }, { 14, 0 }, { 15, 2 },
            { 16, 8 }, { 17, 13 }, { 18, 8 }, { 19, 3 } };

        assertEquals(20, g.vertexSet().size());
        for (int[] e : edges) {
            assertTrue(g.containsEdge(e[0], e[1]));
        }
        assertEquals(edges.length, g.edgeSet().size());

    }

}
