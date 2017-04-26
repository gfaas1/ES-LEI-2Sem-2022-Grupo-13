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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A simple class which computes a random, maximum cardinality matching.
 * The matching is obtained by iterating through the edges and adding an edge if it doesn't conflict with the edges
 * already in the matching. The resulting matching is maximal, and is therefore guaranteed to contain at least half of the edges
 * that a maximum matching has.
 * Runtime complexity: O(E)
 *
 * @author Joris Kinable
 */
public class RandomMaxCardinalityMatching<V,E> implements MatchingAlgorithm<V,E>{

    private final Graph<V,E> graph;

    public RandomMaxCardinalityMatching(Graph<V, E> graph) {
        this.graph = GraphTests.requireUndirected(graph);
    }


    @Override
    public Matching<V, E> getMatching() {
        Set<V> matched=new HashSet<>();
        Set<E> edges=new LinkedHashSet<E>();
        for(V v : graph.vertexSet()){
            if(matched.contains(v))
                continue;

            for(E edge : graph.edgesOf(v)){
                V w = Graphs.getOppositeVertex(graph, edge, v);
                if(!matched.contains(w)) {
                    edges.add(edge);
                    matched.add(v);
                    matched.add(w);
                    break;
                }
            }
        }

        return new MatchingImpl<>(graph, edges, edges.stream().mapToDouble(graph::getEdgeWeight).sum());
    }
}
