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
package org.jgrapht.alg.interfaces;

import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.MatchingAlgorithm.Matching;

/**
 * Allows to derive a <a href="http://en.wikipedia.org/wiki/Matching_(graph_theory)">matching</a> of
 * a given bipartite graph.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 * 
 * @author Dimitrios Michail
 */
public interface BipartiteMatchingAlgorithm<V, E>
{
    /**
     * Default tolerance used by algorithms comparing floating point values.
     */
    double DEFAULT_EPSILON = 1e-9;

    /**
     * Compute a matching for a given graph.
     * 
     * @param graph the input graph
     * @param partition1 the first partition of the vertices
     * @param partition2 the second partition of the vertices
     * @return a matching
     */
    Matching<E> getMatching(
        Graph<V, E> graph, Set<? extends V> partition1, Set<? extends V> partition2);

}
