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
package org.jgrapht.alg.tour;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.spanning.KruskalMinimumSpanningTree;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.graph.ClassBasedVertexFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.junit.Test;

/**
 * @author Dimitrios Michail
 */
public class TwoApproxMetricTSPTest
{

    @Test
    public void testWikiExampleSymmetric4Cities()
    {
        SimpleWeightedGraph<String, DefaultWeightedEdge> g =
            new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        g.addVertex("A");
        g.addVertex("B");
        g.addVertex("C");
        g.addVertex("D");
        g.setEdgeWeight(g.addEdge("A", "B"), 20d);
        g.setEdgeWeight(g.addEdge("A", "C"), 42d);
        g.setEdgeWeight(g.addEdge("A", "D"), 35d);
        g.setEdgeWeight(g.addEdge("B", "C"), 30d);
        g.setEdgeWeight(g.addEdge("B", "D"), 34d);
        g.setEdgeWeight(g.addEdge("C", "D"), 12d);

        GraphPath<String, DefaultWeightedEdge> tour =
            new TwoApproxMetricTSP<String, DefaultWeightedEdge>().getTour(g);
        assertHamiltonian(g, tour);
        assertTrue(
            2 * new KruskalMinimumSpanningTree<>(g).getSpanningTree().getWeight() >= tour
                .getWeight());
    }

    @Test
    public void testComplete()
    {
        final int maxSize = 50;

        for (int i = 1; i < maxSize; i++) {
            SimpleGraph<Object, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
            CompleteGraphGenerator<Object, DefaultEdge> generator = new CompleteGraphGenerator<>(i);
            generator.generateGraph(g, new ClassBasedVertexFactory<>(Object.class), null);

            GraphPath<Object, DefaultEdge> tour =
                new TwoApproxMetricTSP<Object, DefaultEdge>().getTour(g);
            assertHamiltonian(g, tour);

            double mstWeight = new KruskalMinimumSpanningTree<>(g).getSpanningTree().getWeight();
            double tourWeight = tour.getWeight();
            assertTrue(2 * mstWeight >= tourWeight);
        }
    }

    @Test
    public void testStar()
    {
        SimpleWeightedGraph<String, DefaultWeightedEdge> g =
            new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        g.addVertex("1");
        g.addVertex("2");
        g.addVertex("3");
        g.addVertex("4");
        g.addVertex("5");
        g.addVertex("6");

        g.setEdgeWeight(g.addEdge("1", "2"), 1d);
        g.setEdgeWeight(g.addEdge("1", "3"), 1d);
        g.setEdgeWeight(g.addEdge("1", "4"), 1d);
        g.setEdgeWeight(g.addEdge("1", "5"), 2d);
        g.setEdgeWeight(g.addEdge("1", "6"), 2d);

        g.setEdgeWeight(g.addEdge("2", "3"), 2d);
        g.setEdgeWeight(g.addEdge("2", "4"), 1d);
        g.setEdgeWeight(g.addEdge("2", "5"), 1d);
        g.setEdgeWeight(g.addEdge("2", "6"), 2d);

        g.setEdgeWeight(g.addEdge("3", "4"), 1d);
        g.setEdgeWeight(g.addEdge("3", "5"), 2d);
        g.setEdgeWeight(g.addEdge("3", "6"), 1d);

        g.setEdgeWeight(g.addEdge("4", "5"), 1d);
        g.setEdgeWeight(g.addEdge("4", "6"), 1d);

        g.setEdgeWeight(g.addEdge("5", "6"), 1d);

        GraphPath<String, DefaultWeightedEdge> tour =
            new TwoApproxMetricTSP<String, DefaultWeightedEdge>().getTour(g);
        assertHamiltonian(g, tour);

        double mstWeight = new KruskalMinimumSpanningTree<>(g).getSpanningTree().getWeight();
        double tourWeight = tour.getWeight();
        assertTrue(2 * mstWeight >= tourWeight);

    }

    private static <V, E> void assertHamiltonian(UndirectedGraph<V, E> g, GraphPath<V, E> path)
    {
        // check that all vertices are visited
        List<V> vertices = path.getVertexList();
        Iterator<V> vIt = vertices.iterator();
        V start = vIt.next();
        Set<V> visited = new HashSet<>();
        while (vIt.hasNext()) {
            V v = vIt.next();
            assertTrue(visited.add(v));
            if (!vIt.hasNext()) {
                assert (v.equals(start));
            }
        }
        visited.add(start);
        assertEquals(visited.size(), g.vertexSet().size());

        // check that edges are valid
        List<E> edges = path.getEdgeList();
        if (edges.isEmpty()) {
            return;
        }

        E prev = null;
        E first = null, last = null;
        Iterator<E> it = edges.iterator();
        while (it.hasNext()) {
            E cur = it.next();
            if (prev == null) {
                first = cur;
            } else {
                assertTrue(
                    g.getEdgeSource(cur).equals(g.getEdgeSource(prev))
                        || g.getEdgeSource(cur).equals(g.getEdgeTarget(prev))
                        || g.getEdgeTarget(cur).equals(g.getEdgeSource(prev))
                        || g.getEdgeTarget(cur).equals(g.getEdgeTarget(prev)));
            }
            if (!it.hasNext()) {
                last = cur;
            }
            prev = cur;
        }
        if (edges.size() > 1) {
            assertTrue(
                g.getEdgeSource(first).equals(g.getEdgeSource(last))
                    || g.getEdgeSource(first).equals(g.getEdgeTarget(last))
                    || g.getEdgeTarget(first).equals(g.getEdgeSource(last))
                    || g.getEdgeTarget(first).equals(g.getEdgeTarget(last)));
        }
        assertEquals(edges.size(), g.vertexSet().size());
    }

}
