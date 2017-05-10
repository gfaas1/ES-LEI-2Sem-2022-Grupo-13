/*
 * (C) Copyright 2017-2017, by Joris Kinable and Contributors.
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

import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;

import java.util.*;

/**
 * A simple class which computes a random, maximum cardinality matching. The algorithm can run in two modes: sorted or
 * not sorted.
 * When not sorted, the matching is obtained by iterating through the edges and adding an edge if it doesn't conflict with the edges
 * already in the matching. When sorted, the edges are first sorted by the sum of degrees of their endpoints. After that, the algorithm
 * proceeds in the same manner. Running this algorithm in sorted mode can sometimes produce better results, albeit at the cost of some
 * additional computational overhead.
 * <p>
 * Independent of the mode, the resulting matching is maximal, and is therefore guaranteed to contain at least half of the edges
 * that a maximum matching has (1/2 approximation).
 * Runtime complexity: O(m) when the edges are not sorted, O(m log n) otherwise, where n is the number of vertices,
 * and m the number of edges.
 *
 *
 * @author Joris Kinable
 */
public class GreedyMaximumCardinalityMatching<V,E> implements MatchingAlgorithm<V,E>{

    private final Graph<V,E> graph;
    private final boolean sort;


    /**
     * Creates a new GreedyMaximumCardinalityMatching instance.
     * @param graph graph
     * @param sort sort the edges prior to starting the greedy algorithm
     */
    public GreedyMaximumCardinalityMatching(Graph<V, E> graph, boolean sort) {
        this.graph = GraphTests.requireUndirected(graph);
        this.sort=sort;
    }


    /**
     * Get a matching that is a 1/2-approximation of the maximum cardinality matching.
     *
     * @return a matching
     */
    @Override
    public Matching<V, E> getMatching() {
        Set<V> matched = new HashSet<>();
        Set<E> edges = new LinkedHashSet<>();

        if(sort){
            // sort edges in increasing order of the total degree of their endpoints
            List<E> allEdges = new ArrayList<>(graph.edgeSet());
            allEdges.sort(new EdgeDegreeComparator());

            for(E e : allEdges){
                V v =graph.getEdgeSource(e);
                V w = graph.getEdgeTarget(e);
                if(!matched.contains(v) && !matched.contains(w)){
                    edges.add(e);
                    matched.add(v);
                    matched.add(w);
                }
            }
        }else {

            for (V v : graph.vertexSet()) {
                if (matched.contains(v))
                    continue;

                for (E e : graph.edgesOf(v)) {
                    V w = Graphs.getOppositeVertex(graph, e, v);
                    if (!matched.contains(w)) {
                        edges.add(e);
                        matched.add(v);
                        matched.add(w);
                        break;
                    }
                }
            }
        }
        return new MatchingImpl<>(graph, edges, edges.stream().mapToDouble(graph::getEdgeWeight).sum());
    }

    private class EdgeDegreeComparator implements Comparator<E>{

        @Override
        public int compare(E e1, E e2) {
            int degreeE1=graph.degreeOf(graph.getEdgeSource(e1))+graph.degreeOf(graph.getEdgeTarget(e1));
            int degreeE2=graph.degreeOf(graph.getEdgeSource(e2))+graph.degreeOf(graph.getEdgeTarget(e2));
            return Integer.compare(degreeE1, degreeE2);
        }
    }
}
