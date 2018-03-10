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
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;

import java.util.*;

/**
 * A maximum cardinality iterator for an undirected graph.
 * <p>
 * For every vertex in graph its cardinality is defined as the number of its neighbours, which
 * have been already visited by this iterator. Iterator chooses vertex with the maximum cardinality,
 * breaking ties arbitrarily. For more information of maximum cardinality search see
 * <a href="http://www.ii.uib.no/~pinar/MCS-M.pdf"><i>Maximum Cardinality Search for Computing Minimal Triangulations</i></a>.
 * <p>
 * For this iterator to work correctly the graph must not be modified during iteration. Currently
 * there are no means to ensure that, nor to fail-fast. The results of such modifications are
 * undefined.
 *
 * @param <V> the graph vertex type.
 * @param <E> the graph edge type.
 * @author Timofey Chudakov
 */
public class MaximumCardinalityIterator<V, E> extends AbstractGraphIterator<V, E> {
    /**
     * The maximum index of non-empty set in {@code buckets}.
     */
    private int maxCardinality;
    /**
     * Number of unvisited vertices.
     */
    private int remainingVertices;
    /**
     * Disjoint sets of vertices of the graph, indexed by the cardinalities of already visited neighbours.
     */
    private ArrayList<Set<V>> buckets;
    /**
     * Map for mapping every vertex to the cardinality of its neighbours, that have been already visited.
     */
    private Map<V, Integer> cardinalityMap;

    /**
     * Creates a maximum cardinality iterator for the {@code graph}.
     *
     * @param graph the graph to be iterated.
     */
    public MaximumCardinalityIterator(Graph<V, E> graph) {
        super(graph);
        GraphTests.requireUndirected(graph);
        buckets = new ArrayList<>(Collections.nCopies(graph.vertexSet().size(), null));
        buckets.set(0, new HashSet<>(graph.vertexSet()));
        cardinalityMap = new HashMap<>(graph.vertexSet().size());
        for (V v : graph.vertexSet()) {
            cardinalityMap.put(v, 0);
        }
        maxCardinality = 0;
        remainingVertices = graph.vertexSet().size();
    }

    /**
     * Checks whether there exist unvisited vertices.
     *
     * @return true if there exist unvisited vertices.
     */
    @Override
    public boolean hasNext() {
        return remainingVertices > 0;
    }

    /**
     * Returns a vertex with the maximum cardinality of visited neighbours among other unvisited vertices.
     * Updates cardinalities of its unvisited neighbours. Ensures that {@code maxCardinality} contains the
     * maximum index of non-empty set in {@code buckets}.
     *
     * @return the vertex with the maximum cardinality of visited neighbours.
     */
    @Override
    public V next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        Set<V> bucket = buckets.get(maxCardinality);
        V vertex = bucket.iterator().next();
        bucket.remove(vertex);
        cardinalityMap.remove(vertex);
        if (bucket.isEmpty()) {
            buckets.set(maxCardinality, null);
            do {
                --maxCardinality;
            } while (maxCardinality >= 0 && buckets.get(maxCardinality) == null);
        }
        updateNeighbours(vertex);
        --remainingVertices;
        return vertex;
    }

    /**
     * Increments the cardinalities of the neighbours of {@code vertex} by 1. Is the maximum cardinality
     * increases, increments {@code maxCardinality} by 1.
     *
     * @param vertex the vertex whose neighbours are to be updated.
     */
    private void updateNeighbours(V vertex) {
        Set<V> processed = new HashSet<>();
        for (E edge : graph.edgesOf(vertex)) {
            V opposite = Graphs.getOppositeVertex(graph, edge, vertex);
            if (cardinalityMap.containsKey(opposite) && !processed.contains(opposite)) {
                processed.add(opposite);
                int cardinality = cardinalityMap.get(opposite);

                cardinalityMap.put(opposite, cardinality + 1);
                buckets.get(cardinality).remove(opposite);
                if (buckets.get(cardinality).isEmpty()) {
                    buckets.set(cardinality, null);
                }

                if (buckets.get(cardinality + 1) == null) {
                    buckets.set(cardinality + 1, new HashSet<>());
                }
                buckets.get(cardinality + 1).add(opposite);
            }
        }
        if (maxCardinality < graph.vertexSet().size()
                && maxCardinality >= 0
                && buckets.get(maxCardinality + 1) != null) {
            ++maxCardinality;
        }
    }
}
