/*
 * (C) Copyright 2016-2016, by Dimitrios Michail and Contributors.
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
package org.jgrapht.alg.matching;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.WeightedMatchingAlgorithm;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedPseudograph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.WeightedPseudograph;

import junit.framework.TestCase;

/**
 * Unit tests for the approximate weighted matching algorithms.
 * 
 * @author Dimitrios Michail
 */
public abstract class ApproximateWeightedMatchingTest
    extends TestCase
{

    public abstract WeightedMatchingAlgorithm<Integer,
        DefaultWeightedEdge> getApproximationAlgorithm(Graph<Integer, DefaultWeightedEdge> graph);

    public void testPath1()
    {
        WeightedPseudograph<Integer, DefaultWeightedEdge> g =
            new WeightedPseudograph<>(DefaultWeightedEdge.class);

        Graphs.addAllVertices(g, Arrays.asList(0, 1, 2, 3, 4, 5));
        Graphs.addEdge(g, 0, 1, 1.0);
        Graphs.addEdge(g, 1, 2, 1.0);
        Graphs.addEdge(g, 2, 3, 1.0);
        Graphs.addEdge(g, 3, 4, 1.0);
        Graphs.addEdge(g, 4, 5, 1.0);

        WeightedMatchingAlgorithm<Integer, DefaultWeightedEdge> mm = getApproximationAlgorithm(g);

        assertEquals(3, mm.getMatching().size());
        assertEquals(3.0, mm.getMatchingWeight(), WeightedMatchingAlgorithm.DEFAULT_EPSILON);
        assertTrue(mm.getMatching().contains(g.getEdge(0, 1)));
        assertTrue(mm.getMatching().contains(g.getEdge(2, 3)));
        assertTrue(mm.getMatching().contains(g.getEdge(4, 5)));
        assertTrue(isMatching(g, mm.getMatching()));
    }

    public void testPath2()
    {
        WeightedPseudograph<Integer, DefaultWeightedEdge> g =
            new WeightedPseudograph<>(DefaultWeightedEdge.class);

        Graphs.addAllVertices(g, Arrays.asList(0, 1, 2, 3, 4, 5));
        Graphs.addEdge(g, 0, 1, 1.0);
        Graphs.addEdge(g, 1, 2, 5.0);
        Graphs.addEdge(g, 2, 3, 1.0);
        Graphs.addEdge(g, 3, 4, 5.0);
        Graphs.addEdge(g, 4, 5, 1.0);

        WeightedMatchingAlgorithm<Integer, DefaultWeightedEdge> mm = getApproximationAlgorithm(g);

        assertEquals(2, mm.getMatching().size());
        assertEquals(10.0, mm.getMatchingWeight(), WeightedMatchingAlgorithm.DEFAULT_EPSILON);
        assertTrue(mm.getMatching().contains(g.getEdge(1, 2)));
        assertTrue(mm.getMatching().contains(g.getEdge(3, 4)));
        assertTrue(isMatching(g, mm.getMatching()));
    }

    public void testNegativeAndZeroEdges()
    {
        WeightedPseudograph<Integer, DefaultWeightedEdge> g =
            new WeightedPseudograph<>(DefaultWeightedEdge.class);

        Graphs.addAllVertices(g, Arrays.asList(0, 1, 2, 3));
        Graphs.addEdge(g, 0, 1, -1.0);
        Graphs.addEdge(g, 1, 2, -5.0);
        Graphs.addEdge(g, 2, 3, -1.0);
        Graphs.addEdge(g, 3, 0, -1.0);
        Graphs.addEdge(g, 3, 1, 0d);
        Graphs.addEdge(g, 0, 2, 0d);

        WeightedMatchingAlgorithm<Integer, DefaultWeightedEdge> mm = getApproximationAlgorithm(g);

        assertEquals(0, mm.getMatching().size());
        assertEquals(0d, mm.getMatchingWeight(), WeightedMatchingAlgorithm.DEFAULT_EPSILON);
        assertTrue(isMatching(g, mm.getMatching()));
    }

    public void testNegativeAndZeroEdges1()
    {
        WeightedPseudograph<Integer, DefaultWeightedEdge> g =
            new WeightedPseudograph<>(DefaultWeightedEdge.class);

        Graphs.addAllVertices(g, Arrays.asList(0, 1, 2, 3));
        Graphs.addEdge(g, 0, 1, -1.0);
        Graphs.addEdge(g, 1, 2, -5.0);
        Graphs.addEdge(g, 2, 3, -1.0);
        Graphs.addEdge(g, 3, 0, -1.0);
        Graphs.addEdge(g, 3, 1, -1.0d);
        Graphs.addEdge(g, 0, 2, -1.0d);

        WeightedMatchingAlgorithm<Integer, DefaultWeightedEdge> mm = getApproximationAlgorithm(g);

        assertEquals(0, mm.getMatching().size());
        assertEquals(0d, mm.getMatchingWeight(), WeightedMatchingAlgorithm.DEFAULT_EPSILON);
        assertTrue(isMatching(g, mm.getMatching()));
    }

    public void testNegativeAndZeroEdges2()
    {
        WeightedPseudograph<Integer, DefaultWeightedEdge> g =
            new WeightedPseudograph<>(DefaultWeightedEdge.class);

        Graphs.addAllVertices(g, Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
        Graphs.addEdge(g, 0, 1, 1.0);
        Graphs.addEdge(g, 1, 3, 1.0);
        Graphs.addEdge(g, 2, 4, -1.0);
        Graphs.addEdge(g, 3, 5, -1.0);
        Graphs.addEdge(g, 4, 6, -1.0);
        Graphs.addEdge(g, 5, 6, -1.0);
        Graphs.addEdge(g, 6, 7, -1.0);
        Graphs.addEdge(g, 6, 8, -1.0);
        Graphs.addEdge(g, 7, 9, -1.0);
        Graphs.addEdge(g, 8, 10, -1.0);
        Graphs.addEdge(g, 9, 11, 1.0);
        Graphs.addEdge(g, 10, 12, 1.0);

        WeightedMatchingAlgorithm<Integer, DefaultWeightedEdge> mm = getApproximationAlgorithm(g);

        assertTrue(isMatching(g, mm.getMatching()));
        assertTrue(mm.getMatchingWeight() >= 2d);
        assertTrue(mm.getMatching().size() >= 2);
    }

    public void testGraph1()
    {
        WeightedPseudograph<Integer, DefaultWeightedEdge> g =
            new WeightedPseudograph<>(DefaultWeightedEdge.class);

        Graphs.addAllVertices(g, IntStream.range(0, 15).boxed().collect(Collectors.toList()));
        Graphs.addEdge(g, 0, 1, 5.0);
        Graphs.addEdge(g, 1, 2, 2.5);
        Graphs.addEdge(g, 2, 3, 5.0);
        Graphs.addEdge(g, 3, 4, 2.5);
        Graphs.addEdge(g, 4, 0, 2.5);
        Graphs.addEdge(g, 0, 13, 2.5);
        Graphs.addEdge(g, 13, 14, 5.0);
        Graphs.addEdge(g, 1, 11, 2.5);
        Graphs.addEdge(g, 11, 12, 5.0);
        Graphs.addEdge(g, 2, 9, 2.5);
        Graphs.addEdge(g, 9, 10, 5.0);
        Graphs.addEdge(g, 3, 7, 2.5);
        Graphs.addEdge(g, 7, 8, 5.0);
        Graphs.addEdge(g, 4, 5, 2.5);
        Graphs.addEdge(g, 5, 6, 5.0);

        WeightedMatchingAlgorithm<Integer, DefaultWeightedEdge> mm = getApproximationAlgorithm(g);

        assertEquals(7, mm.getMatching().size());
        assertEquals(35.0, mm.getMatchingWeight(), WeightedMatchingAlgorithm.DEFAULT_EPSILON);
        assertTrue(isMatching(g, mm.getMatching()));
    }

    public void test3over4Approximation()
    {
        WeightedPseudograph<Integer, DefaultWeightedEdge> g =
            new WeightedPseudograph<>(DefaultWeightedEdge.class);

        Graphs.addAllVertices(g, Arrays.asList(0, 1, 2, 3));
        Graphs.addEdge(g, 0, 1, 1.0);
        Graphs.addEdge(g, 1, 2, 1.0);
        Graphs.addEdge(g, 2, 3, 1.0);
        Graphs.addEdge(g, 3, 0, 1.0);
        Graphs.addAllVertices(g, Arrays.asList(4, 5, 6, 7));
        Graphs.addEdge(g, 4, 5, 1.0);
        Graphs.addEdge(g, 5, 6, 1.0);
        Graphs.addEdge(g, 6, 7, 1.0);
        Graphs.addEdge(g, 7, 4, 1.0);

        WeightedMatchingAlgorithm<Integer, DefaultWeightedEdge> mm = getApproximationAlgorithm(g);

        assertEquals(4, mm.getMatching().size());
        assertEquals(4.0, mm.getMatchingWeight(), WeightedMatchingAlgorithm.DEFAULT_EPSILON);
        assertTrue(isMatching(g, mm.getMatching()));
    }

    public void testSelfLoops()
    {
        WeightedPseudograph<Integer, DefaultWeightedEdge> g =
            new WeightedPseudograph<>(DefaultWeightedEdge.class);

        Graphs.addAllVertices(g, Arrays.asList(0, 1, 2, 3));
        Graphs.addEdge(g, 0, 1, 1.0);
        Graphs.addEdge(g, 1, 2, 1.0);
        Graphs.addEdge(g, 2, 3, 1.0);
        Graphs.addEdge(g, 3, 0, 1.0);
        Graphs.addAllVertices(g, Arrays.asList(4, 5, 6, 7));
        Graphs.addEdge(g, 4, 5, 1.0);
        Graphs.addEdge(g, 5, 6, 1.0);
        Graphs.addEdge(g, 6, 7, 1.0);
        Graphs.addEdge(g, 7, 4, 1.0);

        // add self loops
        Graphs.addEdge(g, 0, 0, 100.0);
        Graphs.addEdge(g, 1, 1, 200.0);
        Graphs.addEdge(g, 2, 2, -200.0);
        Graphs.addEdge(g, 3, 3, -100.0);
        Graphs.addEdge(g, 4, 4, 0.0);

        WeightedMatchingAlgorithm<Integer, DefaultWeightedEdge> mm = getApproximationAlgorithm(g);

        assertEquals(4, mm.getMatching().size());
        assertEquals(4.0, mm.getMatchingWeight(), WeightedMatchingAlgorithm.DEFAULT_EPSILON);
        assertTrue(isMatching(g, mm.getMatching()));
    }

    public void testMultiGraph()
    {
        WeightedPseudograph<Integer, DefaultWeightedEdge> g =
            new WeightedPseudograph<>(DefaultWeightedEdge.class);

        Graphs.addAllVertices(g, Arrays.asList(0, 1, 2, 3));
        Graphs.addEdge(g, 0, 1, 1.0);
        Graphs.addEdge(g, 1, 2, 1.0);
        Graphs.addEdge(g, 2, 3, 1.0);
        Graphs.addEdge(g, 3, 0, 1.0);
        Graphs.addAllVertices(g, Arrays.asList(4, 5, 6, 7));
        Graphs.addEdge(g, 4, 5, 1.0);
        Graphs.addEdge(g, 5, 6, 1.0);
        Graphs.addEdge(g, 6, 7, 1.0);
        Graphs.addEdge(g, 7, 4, 1.0);

        // add multiple edges
        Graphs.addEdge(g, 0, 1, 2.0);
        Graphs.addEdge(g, 1, 2, 2.0);
        Graphs.addEdge(g, 2, 3, 2.0);
        Graphs.addEdge(g, 3, 0, 2.0);
        Graphs.addEdge(g, 4, 5, 2.0);
        Graphs.addEdge(g, 5, 6, 2.0);
        Graphs.addEdge(g, 6, 7, 2.0);
        Graphs.addEdge(g, 7, 4, 2.0);

        WeightedMatchingAlgorithm<Integer, DefaultWeightedEdge> mm = getApproximationAlgorithm(g);

        // greedy finds maximum here 8.0
        assertEquals(4, mm.getMatching().size());
        assertEquals(8.0, mm.getMatchingWeight(), WeightedMatchingAlgorithm.DEFAULT_EPSILON);
        assertTrue(isMatching(g, mm.getMatching()));
    }

    public void testDirected()
    {
        DirectedWeightedPseudograph<Integer, DefaultWeightedEdge> g =
            new DirectedWeightedPseudograph<>(DefaultWeightedEdge.class);

        Graphs.addAllVertices(g, Arrays.asList(0, 1, 2, 3));
        Graphs.addEdge(g, 0, 1, 1.0);
        Graphs.addEdge(g, 1, 2, 1.0);
        Graphs.addEdge(g, 2, 3, 1.0);
        Graphs.addEdge(g, 3, 0, 1.0);
        Graphs.addAllVertices(g, Arrays.asList(4, 5, 6, 7));
        Graphs.addEdge(g, 4, 5, 1.0);
        Graphs.addEdge(g, 5, 6, 1.0);
        Graphs.addEdge(g, 6, 7, 1.0);
        Graphs.addEdge(g, 7, 4, 1.0);

        // add multiple edges
        Graphs.addEdge(g, 0, 1, 2.0);
        Graphs.addEdge(g, 1, 2, 2.0);
        Graphs.addEdge(g, 2, 3, 2.0);
        Graphs.addEdge(g, 3, 0, 2.0);
        Graphs.addEdge(g, 4, 5, 2.0);
        Graphs.addEdge(g, 5, 6, 2.0);
        Graphs.addEdge(g, 6, 7, 2.0);
        Graphs.addEdge(g, 7, 4, 2.0);

        WeightedMatchingAlgorithm<Integer, DefaultWeightedEdge> mm = getApproximationAlgorithm(g);

        assertEquals(4, mm.getMatching().size());
        assertEquals(8.0, mm.getMatchingWeight(), WeightedMatchingAlgorithm.DEFAULT_EPSILON);
        assertTrue(isMatching(g, mm.getMatching()));
    }

    public void testDisconnectedAndIsolatedVertices()
    {
        WeightedPseudograph<Integer, DefaultWeightedEdge> g =
            new WeightedPseudograph<>(DefaultWeightedEdge.class);

        Graphs.addAllVertices(g, Arrays.asList(0, 1, 2, 3));
        Graphs.addEdge(g, 0, 1, 1.0);
        Graphs.addEdge(g, 1, 2, 1.0);
        Graphs.addEdge(g, 2, 3, 1.0);
        Graphs.addEdge(g, 3, 0, 1.0);
        Graphs.addAllVertices(g, Arrays.asList(4, 5, 6, 7));
        Graphs.addEdge(g, 4, 5, 1.0);
        Graphs.addEdge(g, 5, 6, 1.0);
        Graphs.addEdge(g, 6, 7, 1.0);
        Graphs.addEdge(g, 7, 4, 1.0);
        Graphs.addAllVertices(g, Arrays.asList(8, 9, 10, 11));

        WeightedMatchingAlgorithm<Integer, DefaultWeightedEdge> mm = getApproximationAlgorithm(g);

        assertTrue(mm.getMatchingWeight() >= 2.0);
        assertTrue(isMatching(g, mm.getMatching()));
    }

    public void testBnGraph()
    {
        // create graphs which have a perfect matching
        for (int size = 1; size < 100; size++) {

            SimpleGraph<Integer, DefaultWeightedEdge> graph =
                new SimpleGraph<>(DefaultWeightedEdge.class);

            for (int i = 0; i < size; i++) {
                graph.addVertex(i);
            }

            for (int i = 0; i < size; i++) {
                graph.addVertex(i + size);
                graph.addEdge(i, i + size);
            }

            for (int i = 0; i < size; i++) {
                for (int j = i + 1; j < size; j++) {
                    graph.addEdge(i, j);
                }
            }

            WeightedMatchingAlgorithm<Integer, DefaultWeightedEdge> maxAlg =
                getApproximationAlgorithm(graph);
            Set<DefaultWeightedEdge> matching = maxAlg.getMatching();
            double weight = maxAlg.getMatchingWeight();

            assertTrue(isMatching(graph, matching));
            assertTrue(weight >= size / 2.0);
        }
    }

    protected <V, E> boolean isMatching(Graph<V, E> g, Set<E> m)
    {
        Set<V> matched = new HashSet<>();
        for (E e : m) {
            V source = g.getEdgeSource(e);
            V target = g.getEdgeTarget(e);
            if (matched.contains(source)) {
                return false;
            }
            matched.add(source);
            if (matched.contains(target)) {
                return false;
            }
            matched.add(target);
        }
        return true;
    }

}

// End PathGrowingWeightedMatchingTest.java
