/*
 * (C) Copyright 2016-2017, by Dimitrios Michail and Contributors.
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

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.alg.util.*;

/**
 * The greedy algorithm for computing a maximum weight matching in an arbitrary graph. The algorithm
 * is a 1/2-approximation algorithm and runs in O(n + m log n) where n is the number of vertices and
 * m is the number of edges of the graph. This implementation accepts directed and undirected graphs
 * which may contain self-loops and multiple edges. There is no assumption on the edge weights, i.e.
 * they can also be negative or zero.
 * 
 * <p>
 * The greedy algorithm is the algorithm that first orders the edge set in non-increasing order of
 * weights and then greedily constructs a maximal cardinality matching out of the edges with
 * positive weight. A maximal cardinality matching (not to be confused with maximum cardinality) is
 * a matching that cannot be increased in cardinality without removing an edge first.
 *
 * <p>
 * For more information about approximation algorithms for the maximum weight matching problem in
 * arbitrary graphs see:
 * <ul>
 * <li>R. Preis, Linear Time 1/2-Approximation Algorithm for Maximum Weighted Matching in General
 * Graphs. Symposium on Theoretical Aspects of Computer Science, 259-269, 1999.</li>
 * <li>D.E. Drake, S. Hougardy, A Simple Approximation Algorithm for the Weighted Matching Problem,
 * Information Processing Letters 85, 211-213, 2003.</li>
 * </ul>
 * 
 * @see PathGrowingWeightedMatching
 * 
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 * 
 * @author Dimitrios Michail
 * @since September 2016
 */
public class GreedyWeightedMatching<V, E>
    implements MatchingAlgorithm<V, E>
{
    private final Graph<V, E> graph;
    private final Comparator<Double> comparator;

    /**
     * Create and execute a new instance of the greedy maximum weight matching algorithm. Floating
     * point values are compared using {@link #DEFAULT_EPSILON} tolerance.
     * 
     * @param graph the input graph
     */
    public GreedyWeightedMatching(Graph<V, E> graph)
    {
        this(graph, DEFAULT_EPSILON);
    }

    /**
     * Create and execute a new instance of the greedy maximum weight matching algorithm.
     * 
     * @param graph the input graph
     * @param epsilon tolerance when comparing floating point values
     */
    public GreedyWeightedMatching(Graph<V, E> graph, double epsilon)
    {
        if (graph == null) {
            throw new IllegalArgumentException("Input graph cannot be null");
        }
        this.graph = graph;
        this.comparator = new ToleranceDoubleComparator(epsilon);
    }

    /**
     * Get a matching that is a 1/2-approximation of the maximum weighted matching.
     * 
     * @return a matching
     */
    @Override
    public Matching<E> computeMatching()
    {
        // sort edges in non-decreasing order of weight
        // (the lambda uses e1 and e2 in the reverse order on purpose)
        List<E> allEdges = new ArrayList<>(graph.edgeSet());
        Collections.sort(
            allEdges,
            (e1, e2) -> comparator.compare(graph.getEdgeWeight(e2), graph.getEdgeWeight(e1)));

        double matchingWeight = 0d;
        Set<E> matching = new HashSet<>();
        Set<V> matchedVertices = new HashSet<V>();

        // find maximal matching
        for (E e : allEdges) {
            double edgeWeight = graph.getEdgeWeight(e);
            V s = graph.getEdgeSource(e);
            V t = graph.getEdgeTarget(e);
            if (!s.equals(t) && comparator.compare(edgeWeight, 0d) > 0) {
                if (!matchedVertices.contains(s) && !matchedVertices.contains(t)) {
                    matching.add(e);
                    matchedVertices.add(s);
                    matchedVertices.add(t);
                    matchingWeight += edgeWeight;
                }
            }
        }

        // return matching
        return new MatchingImpl<>(matching, matchingWeight);
    }

}

// End GreedyWeightedMatching.java
