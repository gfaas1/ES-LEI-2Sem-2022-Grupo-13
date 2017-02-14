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
import static org.junit.Assert.fail;

import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.IntegerVertexFactory;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Test;

/**
 * @author Dimitrios Michail
 */
public class WattsStrogatzGraphGeneratorTest
{
    @Test(expected = IllegalArgumentException.class)
    public void testLessThan3Nodes()
    {
        new WattsStrogatzGraphGenerator<>(2, 1, 0.5);
    }

    @Test
    public void testBadParameters()
    {
        try {
            new WattsStrogatzGraphGenerator<>(-1, 2, 0.5);
            fail("Bad parameter");
        } catch (IllegalArgumentException e) {
        }

        try {
            new WattsStrogatzGraphGenerator<>(10, 9, 0.5);
            fail("Bad parameter");
        } catch (IllegalArgumentException e) {
        }

        try {
            new WattsStrogatzGraphGenerator<>(10, 9, 0.5);
            fail("Bad parameter");
        } catch (IllegalArgumentException e) {
        }

        try {
            new WattsStrogatzGraphGenerator<>(11, 11, 0.5);
            fail("Bad parameter");
        } catch (IllegalArgumentException e) {
        }

        try {
            new WattsStrogatzGraphGenerator<>(10, 2, -1.0);
            fail("Bad parameter");
        } catch (IllegalArgumentException e) {
        }

        try {
            new WattsStrogatzGraphGenerator<>(10, 2, 2.0);
            fail("Bad parameter");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void test4RegularNoRewiring()
    {
        final long seed = 5;

        GraphGenerator<Integer, DefaultEdge, Integer> gen =
            new WattsStrogatzGraphGenerator<>(6, 4, 0.0, seed);
        Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        gen.generateGraph(g, new IntegerVertexFactory(), null);

        assertEquals(
            "([0, 1, 2, 3, 4, 5], [{0,1}, {0,2}, {1,2}, {1,3}, {2,3}, {2,4}, {3,4}, {3,5}, {4,5}, {4,0}, {5,0}, {5,1}])",
            g.toString());
    }

    @Test
    public void test4RegularSomeRewiring()
    {
        final long seed = 5;

        GraphGenerator<Integer, DefaultEdge, Integer> gen =
            new WattsStrogatzGraphGenerator<>(6, 4, 0.5, seed);
        Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        gen.generateGraph(g, new IntegerVertexFactory(), null);

        assertEquals(
            "([0, 1, 2, 3, 4, 5], [{0,1}, {0,2}, {1,2}, {1,3}, {2,3}, {2,4}, {3,4}, {3,5}, {4,5}, {4,0}, {5,1}, {5,2}])",
            g.toString());
    }

    @Test
    public void test4RegularMoreRewiring()
    {
        final long seed = 5;

        GraphGenerator<Integer, DefaultEdge, Integer> gen =
            new WattsStrogatzGraphGenerator<>(6, 4, 0.8, seed);
        Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        gen.generateGraph(g, new IntegerVertexFactory(), null);

        assertEquals(
            "([0, 1, 2, 3, 4, 5], [{0,1}, {1,2}, {1,3}, {2,3}, {2,4}, {3,4}, {3,5}, {4,5}, {5,0}, {5,1}, {0,3}, {4,1}])",
            g.toString());
    }

    @Test
    public void test4RegularAddShortcutInsteadOfRewiring()
    {
        final long seed = 5;

        GraphGenerator<Integer, DefaultEdge, Integer> gen =
            new WattsStrogatzGraphGenerator<>(6, 4, 0.5, true, new Random(seed));
        Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        gen.generateGraph(g, new IntegerVertexFactory(), null);

        assertEquals(
            "([0, 1, 2, 3, 4, 5], [{0,1}, {0,2}, {1,2}, {1,3}, {2,3}, {2,4}, {3,4}, {3,5}, {4,5}, {4,0}, {5,0}, {5,1}, {5,2}])",
            g.toString());
    }

    @Test
    public void test6RegularNoRewiring()
    {
        final long seed = 5;

        GraphGenerator<Integer, DefaultEdge, Integer> gen =
            new WattsStrogatzGraphGenerator<>(12, 6, 0.0, seed);
        Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        gen.generateGraph(g, new IntegerVertexFactory(), null);

        // @formatter:off
        String expected = "([0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11], " + 
                          "[{0,1}, {0,2}, {0,3}, {1,2}, {1,3}, {1,4}, {2,3}, {2,4}, {2,5}, {3,4}, {3,5}, {3,6}, " + 
                          "{4,5}, {4,6}, {4,7}, {5,6}, {5,7}, {5,8}, {6,7}, {6,8}, {6,9}, {7,8}, {7,9}, {7,10}, " + 
                          "{8,9}, {8,10}, {8,11}, {9,10}, {9,11}, {9,0}, {10,11}, {10,0}, {10,1}, {11,0}, {11,1}, {11,2}])";
        // @formatter:on

        assertEquals(expected, g.toString());
    }

    @Test
    public void test6RegularSomeRewiring()
    {
        final long seed = 5;

        GraphGenerator<Integer, DefaultEdge, Integer> gen =
            new WattsStrogatzGraphGenerator<>(12, 6, 0.7, seed);
        Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        gen.generateGraph(g, new IntegerVertexFactory(), null);

        // @formatter:off
        String expected = "([0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11], [{0,1}, {0,2}, {1,3}, {1,4}, {2,3}, {3,4}, " + 
                          "{3,5}, {3,6}, {4,5}, {4,7}, {5,6}, {5,7}, {6,7}, {6,8}, {6,9}, {7,8}, {7,10}, {8,9}, " + 
                          "{9,11}, {9,0}, {10,0}, {10,1}, {11,0}, {11,1}, {11,2}, {1,6}, {9,1}, {10,3}, {2,1}, {4,2}, " + 
                          "{7,11}, {8,4}, {0,8}, {2,7}, {5,1}, {8,3}])";
        // @formatter:on

        assertEquals(expected, g.toString());
    }

    @Test
    public void test4RegularNoRewiringDirected()
    {
        final long seed = 5;

        GraphGenerator<Integer, DefaultEdge, Integer> gen =
            new WattsStrogatzGraphGenerator<>(6, 4, 0.0, seed);
        Graph<Integer, DefaultEdge> g = new SimpleDirectedGraph<>(DefaultEdge.class);
        gen.generateGraph(g, new IntegerVertexFactory(), null);

        assertEquals(
            "([0, 1, 2, 3, 4, 5], [(0,1), (0,2), (1,2), (1,3), (2,3), (2,4), (3,4), (3,5), (4,5), (4,0), (5,0), (5,1)])",
            g.toString());
    }

}
