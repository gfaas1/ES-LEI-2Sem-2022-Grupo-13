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
package org.jgrapht.alg.color;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm.Coloring;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm.ColoringImpl;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Test;

/**
 * Test class for graph colorings.
 * 
 * @author Dimitrios Michail
 */
public class DifficultColoringTest
{
    /**
     * Test instance where DSatur greedy coloring is non-optimal.
     */
    @Test
    public void testDSaturNonOptimal()
    {
        Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        Graphs.addAllVertices(g, IntStream.range(1, 8).boxed().collect(Collectors.toList()));

        g.addEdge(1, 2);
        g.addEdge(1, 3);
        g.addEdge(1, 4);
        g.addEdge(2, 3);
        g.addEdge(2, 5);
        g.addEdge(4, 6);
        g.addEdge(4, 7);
        g.addEdge(5, 6);
        g.addEdge(5, 7);
        g.addEdge(6, 7);

        assertColoring(g, new SaturationDegreeColoring<>(g).getColoring(), 4);
        assertColoring(g, new GreedyColoring<>(g).getColoring(), 4);
        assertColoring(g, new SmallestDegreeLastColoring<>(g).getColoring(), 3);
        assertColoring(g, new LargestDegreeFirstColoring<>(g).getColoring(), 4);
        assertColoring(g, new RandomGreedyColoring<>(g, new Random(13)).getColoring(), 3);
        assertColoring(g, new RandomGreedyColoring<>(g, new Random(15)).getColoring(), 4);

        Map<Integer, Integer> opt = new HashMap<Integer, Integer>();
        opt.put(1, 1);
        opt.put(2, 0);
        opt.put(3, 2);
        opt.put(4, 2);
        opt.put(5, 2);
        opt.put(6, 0);
        opt.put(7, 1);
        Coloring<Integer> optimalColoring = new ColoringImpl<>(opt, 3);
        assertColoring(g, optimalColoring, 3);
    }

    @Test
    public void testColorGroups()
    {
        Map<Integer, Integer> opt = new HashMap<Integer, Integer>();
        opt.put(1, 1);
        opt.put(2, 0);
        opt.put(3, 2);
        opt.put(4, 2);
        opt.put(5, 2);
        opt.put(6, 0);
        opt.put(7, 1);
        Coloring<Integer> coloring = new ColoringImpl<>(opt, 3);

        Map<Integer, Set<Integer>> groups = coloring.getColorGroups();
        assertEquals(3, groups.keySet().size());
        assertTrue(groups.get(0).contains(2));
        assertTrue(groups.get(0).contains(6));
        assertEquals(2, groups.get(0).size());
        assertTrue(groups.get(1).contains(1));
        assertTrue(groups.get(1).contains(7));
        assertEquals(2, groups.get(1).size());
        assertTrue(groups.get(2).contains(3));
        assertTrue(groups.get(2).contains(4));
        assertTrue(groups.get(2).contains(5));
        assertEquals(3, groups.get(2).size());
    }

    @Test
    public void testMyciel3()
    {
        // This is a graph from http://mat.gsia.cmu.edu/COLOR/instances/myciel3.col.
        // SOURCE: Michael Trick (trick@cmu.edu)
        // DESCRIPTION: Graph based on Mycielski transformation.
        // Triangle free (clique number 2) but increasing coloring number
        Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        Graphs.addAllVertices(g, IntStream.range(1, 12).boxed().collect(Collectors.toList()));

        g.addEdge(1, 2);
        g.addEdge(1, 4);
        g.addEdge(1, 7);
        g.addEdge(1, 9);
        g.addEdge(2, 3);
        g.addEdge(2, 6);
        g.addEdge(2, 8);
        g.addEdge(3, 5);
        g.addEdge(3, 7);
        g.addEdge(3, 10);
        g.addEdge(4, 5);
        g.addEdge(4, 6);
        g.addEdge(4, 10);
        g.addEdge(5, 8);
        g.addEdge(5, 9);
        g.addEdge(6, 11);
        g.addEdge(7, 11);
        g.addEdge(8, 11);
        g.addEdge(9, 11);
        g.addEdge(10, 11);

        assertColoring(g, new GreedyColoring<>(g).getColoring(), 4);
        assertColoring(g, new RandomGreedyColoring<>(g).getColoring(), 4);
        assertColoring(g, new SmallestDegreeLastColoring<>(g).getColoring(), 4);
        assertColoring(g, new LargestDegreeFirstColoring<>(g).getColoring(), 4);
        assertColoring(g, new SaturationDegreeColoring<>(g).getColoring(), 4);
    }

    @Test
    public void testMyciel4()
    {
        // This is a graph from http://mat.gsia.cmu.edu/COLOR/instances/myciel4.col.
        // SOURCE: Michael Trick (trick@cmu.edu)
        // DESCRIPTION: Graph based on Mycielski transformation.
        // Triangle free (clique number 2) but increasing coloring number
        Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        Graphs.addAllVertices(g, IntStream.range(1, 24).boxed().collect(Collectors.toList()));

        g.addEdge(1, 2);
        g.addEdge(1, 4);
        g.addEdge(1, 7);
        g.addEdge(1, 9);
        g.addEdge(1, 13);
        g.addEdge(1, 15);
        g.addEdge(1, 18);
        g.addEdge(1, 20);
        g.addEdge(2, 3);
        g.addEdge(2, 6);
        g.addEdge(2, 8);
        g.addEdge(2, 12);
        g.addEdge(2, 14);
        g.addEdge(2, 17);
        g.addEdge(2, 19);
        g.addEdge(3, 5);
        g.addEdge(3, 7);
        g.addEdge(3, 10);
        g.addEdge(3, 13);
        g.addEdge(3, 16);
        g.addEdge(3, 18);
        g.addEdge(3, 21);
        g.addEdge(4, 5);
        g.addEdge(4, 6);
        g.addEdge(4, 10);
        g.addEdge(4, 12);
        g.addEdge(4, 16);
        g.addEdge(4, 17);
        g.addEdge(4, 21);
        g.addEdge(5, 8);
        g.addEdge(5, 9);
        g.addEdge(5, 14);
        g.addEdge(5, 15);
        g.addEdge(5, 19);
        g.addEdge(5, 20);
        g.addEdge(6, 11);
        g.addEdge(6, 13);
        g.addEdge(6, 15);
        g.addEdge(6, 22);
        g.addEdge(7, 11);
        g.addEdge(7, 12);
        g.addEdge(7, 14);
        g.addEdge(7, 22);
        g.addEdge(8, 11);
        g.addEdge(8, 13);
        g.addEdge(8, 16);
        g.addEdge(8, 22);
        g.addEdge(9, 11);
        g.addEdge(9, 12);
        g.addEdge(9, 16);
        g.addEdge(9, 22);
        g.addEdge(10, 11);
        g.addEdge(10, 14);
        g.addEdge(10, 15);
        g.addEdge(10, 22);
        g.addEdge(11, 17);
        g.addEdge(11, 18);
        g.addEdge(11, 19);
        g.addEdge(11, 20);
        g.addEdge(11, 21);
        g.addEdge(12, 23);
        g.addEdge(13, 23);
        g.addEdge(14, 23);
        g.addEdge(15, 23);
        g.addEdge(16, 23);
        g.addEdge(17, 23);
        g.addEdge(18, 23);
        g.addEdge(19, 23);
        g.addEdge(20, 23);
        g.addEdge(21, 23);
        g.addEdge(22, 23);

        assertColoring(g, new GreedyColoring<>(g).getColoring(), 5);
        assertColoring(g, new RandomGreedyColoring<>(g).getColoring(), 5);
        assertColoring(g, new SmallestDegreeLastColoring<>(g).getColoring(), 5);
        assertColoring(g, new LargestDegreeFirstColoring<>(g).getColoring(), 5);
        assertColoring(g, new SaturationDegreeColoring<>(g).getColoring(), 5);
    }

    private void assertColoring(
        Graph<Integer, DefaultEdge> g, Coloring<Integer> coloring, int expectedColors)
    {
        int n = g.vertexSet().size();
        assertTrue(coloring.getNumberColors() <= n);
        assertEquals(expectedColors, coloring.getNumberColors());
        Map<Integer, Integer> colors = coloring.getColors();

        for (Integer v : g.vertexSet()) {
            Integer c = colors.get(v);
            assertNotNull(c);
            assertTrue(c >= 0);
            assertTrue(c < n);
        }

        for (DefaultEdge e : g.edgeSet()) {
            assertNotEquals(colors.get(g.getEdgeSource(e)), colors.get(g.getEdgeTarget(e)));
        }
    }

}
