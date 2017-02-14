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
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.graph.IntegerVertexFactory;
import org.jgrapht.graph.Multigraph;
import org.jgrapht.graph.Pseudograph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Test;

/**
 * Tests for {@link LinearizedChordDiagramGraphGenerator}.
 * 
 * @author Dimitrios Michail
 */
public class LinearizedChordDiagramGraphGeneratorTest
{
    @Test
    public void testBadParameters()
    {
        try {
            new LinearizedChordDiagramGraphGenerator<>(0, 10);
            fail("Bad parameter");
        } catch (IllegalArgumentException e) {
        }

        try {
            new LinearizedChordDiagramGraphGenerator<>(-1, 10);
            fail("Bad parameter");
        } catch (IllegalArgumentException e) {
        }

        try {
            new LinearizedChordDiagramGraphGenerator<>(5, 0);
            fail("Bad parameter");
        } catch (IllegalArgumentException e) {
        }

        try {
            new LinearizedChordDiagramGraphGenerator<>(5, -1);
            fail("Bad parameter");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMultiGraph()
    {
        final long seed = 5;
        GraphGenerator<Integer, DefaultEdge, Integer> gen =
            new LinearizedChordDiagramGraphGenerator<>(10, 2, seed);
        Graph<Integer, DefaultEdge> g = new Multigraph<>(DefaultEdge.class);
        gen.generateGraph(g, new IntegerVertexFactory(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSimpleGraph()
    {
        final long seed = 5;
        GraphGenerator<Integer, DefaultEdge, Integer> gen =
            new LinearizedChordDiagramGraphGenerator<>(10, 2, seed);
        Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        gen.generateGraph(g, new IntegerVertexFactory(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDirectedMultiGraph()
    {
        final long seed = 5;
        GraphGenerator<Integer, DefaultEdge, Integer> gen =
            new LinearizedChordDiagramGraphGenerator<>(10, 2, seed);
        Graph<Integer, DefaultEdge> g = new DirectedMultigraph<>(DefaultEdge.class);
        gen.generateGraph(g, new IntegerVertexFactory(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDirectedSimpleGraph()
    {
        final long seed = 5;
        GraphGenerator<Integer, DefaultEdge, Integer> gen =
            new LinearizedChordDiagramGraphGenerator<>(10, 2, seed);
        Graph<Integer, DefaultEdge> g = new SimpleDirectedGraph<>(DefaultEdge.class);
        gen.generateGraph(g, new IntegerVertexFactory(), null);
    }

    @Test
    public void testUndirected()
    {
        final long seed = 5;

        GraphGenerator<Integer, DefaultEdge, Integer> gen =
            new LinearizedChordDiagramGraphGenerator<>(20, 1, seed);
        Graph<Integer, DefaultEdge> g = new Pseudograph<>(DefaultEdge.class);
        gen.generateGraph(g, new IntegerVertexFactory(), null);

        int[][] edges = { { 0, 0 }, { 1, 0 }, { 2, 2 }, { 3, 0 }, { 4, 0 }, { 5, 0 }, { 6, 2 },
            { 7, 0 }, { 8, 2 }, { 9, 0 }, { 10, 1 }, { 11, 1 }, { 12, 11 }, { 13, 3 }, { 14, 4 },
            { 15, 0 }, { 16, 0 }, { 17, 5 }, { 18, 7 }, { 19, 0 } };

        assertEquals(20, g.vertexSet().size());
        for (int[] e : edges) {
            assertTrue(g.containsEdge(e[0], e[1]));
        }
        assertEquals(edges.length, g.edgeSet().size());
    }

    @Test
    public void testUndirectedTwoEdges()
    {
        final long seed = 5;

        GraphGenerator<Integer, DefaultEdge, Integer> gen =
            new LinearizedChordDiagramGraphGenerator<>(20, 2, seed);
        Graph<Integer, DefaultEdge> g = new Pseudograph<>(DefaultEdge.class);
        gen.generateGraph(g, new IntegerVertexFactory(), null);

        int[][] edges = { { 0, 0 }, { 0, 0 }, { 1, 1 }, { 1, 0 }, { 2, 0 }, { 2, 0 }, { 3, 1 },
            { 3, 0 }, { 4, 1 }, { 4, 0 }, { 5, 0 }, { 5, 0 }, { 6, 5 }, { 6, 1 }, { 7, 2 },
            { 7, 0 }, { 8, 0 }, { 8, 2 }, { 9, 3 }, { 9, 0 }, { 10, 2 }, { 10, 2 }, { 11, 0 },
            { 11, 2 }, { 12, 11 }, { 12, 2 }, { 13, 11 }, { 13, 1 }, { 14, 3 }, { 14, 14 },
            { 15, 9 }, { 15, 2 }, { 16, 3 }, { 16, 8 }, { 17, 3 }, { 17, 6 }, { 18, 8 }, { 18, 4 },
            { 19, 0 }, { 19, 8 } };

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
            new LinearizedChordDiagramGraphGenerator<>(20, 1, seed);
        Graph<Integer, DefaultEdge> g = new DirectedPseudograph<>(DefaultEdge.class);
        gen.generateGraph(g, new IntegerVertexFactory(), null);

        int[][] edges = { { 0, 0 }, { 1, 0 }, { 2, 2 }, { 3, 0 }, { 4, 0 }, { 5, 0 }, { 6, 2 },
            { 7, 0 }, { 8, 2 }, { 9, 0 }, { 10, 1 }, { 11, 1 }, { 12, 11 }, { 13, 3 }, { 14, 4 },
            { 15, 0 }, { 16, 0 }, { 17, 5 }, { 18, 7 }, { 19, 0 } };

        assertEquals(20, g.vertexSet().size());
        for (int[] e : edges) {
            assertTrue(g.containsEdge(e[0], e[1]));
        }
        assertEquals(edges.length, g.edgeSet().size());

    }

}
