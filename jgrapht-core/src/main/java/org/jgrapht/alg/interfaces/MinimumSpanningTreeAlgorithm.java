/*
 * (C) Copyright 2013-2016, by Alexey Kudinkin and Contributors.
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

/**
 * An algorithm which computes a <a href=http://en.wikipedia.org/wiki/Minimum_spanning_tree> minimum
 * spanning tree</a> of a given connected graph. In the case of disconnected graphs it would rather
 * derive a minimum spanning <i>forest</i>.
 *
 * @param <V> vertex the graph vertex type
 * @param <E> edge the graph edge type
 */
public interface MinimumSpanningTreeAlgorithm<V, E>
{

    /**
     * Computes a minimum spanning tree.
     *
     * @param graph the graph
     * @return a minimum spanning tree
     * @throws IllegalArgumentException in case the graph type is not supported
     */
    SpanningTree<E> getSpanningTree(Graph<V, E> graph);

    /**
     * A spanning tree.
     *
     * @param <E> the graph edge type
     */
    interface SpanningTree<E>
    {

        /**
         * Returns the weight of the spanning tree.
         * 
         * @return weight of the spanning tree
         */
        double getWeight();

        /**
         * Set of edges of the spanning tree.
         * 
         * @return edge set of the spanning tree
         */
        Set<E> getEdges();
    }

}
