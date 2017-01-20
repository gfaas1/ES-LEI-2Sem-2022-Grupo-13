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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm.Coloring;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.generate.GnpRandomGraphGenerator;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.IntegerVertexFactory;
import org.jgrapht.graph.Pseudograph;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Test;

/**
 * Test class for graph colorings.
 * 
 * @author Dimitrios Michail
 */
public class ColoringTest
{

    @Test
    public void testGreedy()
    {
        Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        Graphs.addAllVertices(g, Arrays.asList(1, 2, 3, 4, 5));
        g.addEdge(1, 2);
        g.addEdge(1, 3);
        g.addEdge(1, 3);
        g.addEdge(1, 4);
        g.addEdge(1, 5);
        g.addEdge(2, 3);
        g.addEdge(3, 4);
        g.addEdge(3, 5);

        Coloring<Integer> coloring = new GreedyColoring<>(g).getColoring();
        assertEquals(3, coloring.getNumberColors());
        Map<Integer, Integer> colors = coloring.getColors();
        assertEquals(0, colors.get(1).intValue());
        assertEquals(1, colors.get(2).intValue());
        assertEquals(2, colors.get(3).intValue());
        assertEquals(1, colors.get(4).intValue());
        assertEquals(1, colors.get(5).intValue());
    }

    @Test
    public void testCompleteGreedyFit()
    {
        final int n = 20;
        Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        CompleteGraphGenerator<Integer, DefaultEdge> gen = new CompleteGraphGenerator<>(n);
        gen.generateGraph(g, new IntegerVertexFactory(), null);
        Coloring<Integer> coloring = new GreedyColoring<>(g).getColoring();
        assertEquals(n, coloring.getNumberColors());
    }

    @Test
    public void testSmallestDegreeLastColoring()
    {
        Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        Graphs.addAllVertices(g, Arrays.asList(1, 2, 3, 4, 5));
        g.addEdge(1, 2);
        g.addEdge(1, 3);
        g.addEdge(1, 3);
        g.addEdge(1, 4);
        g.addEdge(1, 5);
        g.addEdge(2, 3);
        g.addEdge(3, 4);
        g.addEdge(3, 5);

        Coloring<Integer> coloring = new SmallestDegreeLastColoring<>(g).getColoring();
        assertEquals(3, coloring.getNumberColors());
        Map<Integer, Integer> colors = coloring.getColors();
        assertEquals(2, colors.get(1).intValue());
        assertEquals(0, colors.get(2).intValue());
        assertEquals(1, colors.get(3).intValue());
        assertEquals(0, colors.get(4).intValue());
        assertEquals(0, colors.get(5).intValue());
    }

    @Test
    public void testSmallestDegreeLastColoringNonSimple()
    {
        Graph<Integer, DefaultEdge> g = new Pseudograph<>(DefaultEdge.class);
        Graphs.addAllVertices(g, Arrays.asList(1, 2, 3, 4, 5, 6));
        g.addEdge(2, 3);
        g.addEdge(4, 5);
        g.addEdge(4, 6);
        g.addEdge(5, 6);
        g.addEdge(5, 6);
        g.addEdge(5, 6);
        g.addEdge(5, 6);
        g.addEdge(5, 6);
        g.addEdge(5, 6);
        g.addEdge(5, 6);
        g.addEdge(5, 6);

        Coloring<Integer> coloring = new SmallestDegreeLastColoring<>(g).getColoring();
        assertEquals(3, coloring.getNumberColors());
        Map<Integer, Integer> colors = coloring.getColors();
        assertEquals(0, colors.get(1).intValue());
        assertEquals(1, colors.get(2).intValue());
        assertEquals(0, colors.get(3).intValue());
        assertEquals(2, colors.get(4).intValue());
        assertEquals(1, colors.get(5).intValue());
        assertEquals(0, colors.get(6).intValue());
    }

    @Test
    public void testSmallestDegreeLastColoringComplete()
    {
        final int n = 20;
        Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        CompleteGraphGenerator<Integer, DefaultEdge> gen = new CompleteGraphGenerator<>(n);
        gen.generateGraph(g, new IntegerVertexFactory(), null);

        Coloring<Integer> coloring = new SmallestDegreeLastColoring<>(g).getColoring();
        assertEquals(n, coloring.getNumberColors());
    }

    @Test
    public void testLargestDegreeFirstColoring()
    {
        Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        Graphs.addAllVertices(g, Arrays.asList(1, 2, 3, 4, 5));
        g.addEdge(1, 2);
        g.addEdge(1, 3);
        g.addEdge(1, 3);
        g.addEdge(1, 4);
        g.addEdge(1, 5);
        g.addEdge(2, 3);
        g.addEdge(3, 4);
        g.addEdge(3, 5);

        Coloring<Integer> coloring = new LargestDegreeFirstColoring<>(g).getColoring();
        assertEquals(3, coloring.getNumberColors());
        Map<Integer, Integer> colors = coloring.getColors();
        assertEquals(0, colors.get(1).intValue());
        assertEquals(2, colors.get(2).intValue());
        assertEquals(1, colors.get(3).intValue());
        assertEquals(2, colors.get(4).intValue());
        assertEquals(2, colors.get(5).intValue());
    }

    @Test
    public void testLargestDegreeFirstColoring1()
    {
        Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        Graphs.addAllVertices(g, Arrays.asList(1, 2, 3, 4, 5, 6));
        g.addEdge(1, 2);
        g.addEdge(1, 3);
        g.addEdge(1, 3);
        g.addEdge(1, 4);
        g.addEdge(1, 5);
        g.addEdge(2, 3);
        g.addEdge(3, 4);
        g.addEdge(3, 5);
        g.addEdge(3, 6);
        g.addEdge(5, 6);

        Coloring<Integer> coloring = new LargestDegreeFirstColoring<>(g).getColoring();
        assertEquals(3, coloring.getNumberColors());
        Map<Integer, Integer> colors = coloring.getColors();
        assertEquals(1, colors.get(1).intValue());
        assertEquals(2, colors.get(2).intValue());
        assertEquals(0, colors.get(3).intValue());
        assertEquals(2, colors.get(4).intValue());
        assertEquals(2, colors.get(5).intValue());
        assertEquals(1, colors.get(6).intValue());
    }

    @Test
    public void testLargestDegreeFirstColoringNonSimple()
    {
        Graph<Integer, DefaultEdge> g = new Pseudograph<>(DefaultEdge.class);
        Graphs.addAllVertices(g, Arrays.asList(1, 2, 3, 4, 5, 6));
        g.addEdge(2, 3);
        g.addEdge(4, 5);
        g.addEdge(4, 6);
        for (int i = 0; i < 20000; i++) {
            g.addEdge(5, 6);
        }

        Coloring<Integer> coloring = new LargestDegreeFirstColoring<>(g).getColoring();
        assertEquals(3, coloring.getNumberColors());
        Map<Integer, Integer> colors = coloring.getColors();
        assertEquals(0, colors.get(1).intValue());
        assertEquals(0, colors.get(2).intValue());
        assertEquals(1, colors.get(3).intValue());
        assertEquals(2, colors.get(4).intValue());
        assertEquals(0, colors.get(5).intValue());
        assertEquals(1, colors.get(6).intValue());
    }

    @Test
    public void testLargestDegreeFirstColoringSimple()
    {
        Graph<Integer, DefaultEdge> g = new Pseudograph<>(DefaultEdge.class);
        Graphs.addAllVertices(g, Arrays.asList(1, 2, 3, 4, 5, 6));
        g.addEdge(2, 3);
        g.addEdge(4, 5);
        g.addEdge(4, 6);
        g.addEdge(5, 6);
        g.addEdge(5, 3);

        Coloring<Integer> coloring = new LargestDegreeFirstColoring<>(g).getColoring();
        assertEquals(3, coloring.getNumberColors());
        Map<Integer, Integer> colors = coloring.getColors();
        assertEquals(0, colors.get(1).intValue());
        assertEquals(0, colors.get(2).intValue());
        assertEquals(1, colors.get(3).intValue());
        assertEquals(1, colors.get(4).intValue());
        assertEquals(0, colors.get(5).intValue());
        assertEquals(2, colors.get(6).intValue());
    }

    @Test
    public void testSaturationDegree()
    {
        Graph<Integer, DefaultEdge> g = new Pseudograph<>(DefaultEdge.class);
        Graphs.addAllVertices(g, Arrays.asList(1, 2, 3, 4, 5, 6));
        g.addEdge(2, 3);
        g.addEdge(4, 5);
        g.addEdge(4, 6);
        g.addEdge(5, 6);
        g.addEdge(5, 3);

        Coloring<Integer> coloring = new SaturationDegreeColoring<>(g).getColoring();
        assertEquals(3, coloring.getNumberColors());
        Map<Integer, Integer> colors = coloring.getColors();
        assertEquals(0, colors.get(1).intValue());
        assertEquals(0, colors.get(2).intValue());
        assertEquals(1, colors.get(3).intValue());
        assertEquals(1, colors.get(4).intValue());
        assertEquals(0, colors.get(5).intValue());
        assertEquals(2, colors.get(6).intValue());
    }

    @Test
    public void testRandomFixedSeed17()
    {
        final long seed = 17;
        Random rng = new Random(seed);
        testRandomGraphColoring(rng);
    }

    @Test
    public void testRandom()
    {
        Random rng = new Random();
        testRandomGraphColoring(rng);
    }

    private void testRandomGraphColoring(Random rng)
    {
        final int tests = 5;
        final int n = 20;
        final double p = 0.35;

        List<Function<Graph<Integer, DefaultEdge>, VertexColoringAlgorithm<Integer>>> algs =
            new ArrayList<>();
        algs.add((g) -> new GreedyColoring<>(g));
        algs.add((g) -> new RandomGreedyColoring<>(g, rng));
        algs.add((g) -> new SmallestDegreeLastColoring<>(g));
        algs.add((g) -> new LargestDegreeFirstColoring<>(g));
        algs.add((g) -> new SaturationDegreeColoring<>(g));

        GraphGenerator<Integer, DefaultEdge, Integer> gen =
            new GnpRandomGraphGenerator<>(n, p, rng, false);

        for (int i = 0; i < tests; i++) {
            Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
            gen.generateGraph(g, new IntegerVertexFactory(), null);

            for (Function<Graph<Integer, DefaultEdge>,
                VertexColoringAlgorithm<Integer>> algProvider : algs)
            {
                VertexColoringAlgorithm<Integer> alg = algProvider.apply(g);
                Coloring<Integer> coloring = alg.getColoring();
                assertTrue(coloring.getNumberColors() <= n);
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

    }

}
