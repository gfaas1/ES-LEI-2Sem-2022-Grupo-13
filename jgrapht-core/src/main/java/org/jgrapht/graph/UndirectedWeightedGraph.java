/*
 * (C) Copyright 2018-2018, by Dimitrios Michail and Contributors.
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
package org.jgrapht.graph;

import org.jgrapht.*;
import org.jgrapht.graph.builder.*;

/**
 * A undirected weighted graph. An undirected weighted graph is a non-simple undirected graph in
 * which multiple edges between any two vertices are <i>not</i> permitted, but loops are. The edges
 * of a weighted undirected graph have weights.
 * 
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 */
public class UndirectedWeightedGraph<V, E>
    extends UndirectedGraph<V, E>
{
    private static final long serialVersionUID = -1008165881690129042L;

    /**
     * Creates a new weighted undirected graph with the specified edge factory.
     *
     * @param ef the edge factory of the new graph.
     */
    public UndirectedWeightedGraph(EdgeFactory<V, E> ef)
    {
        super(ef, true);
    }

    /**
     * Creates a new weighted undirected graph.
     *
     * @param edgeClass class on which to base factory for edges
     */
    public UndirectedWeightedGraph(Class<? extends E> edgeClass)
    {
        this(new ClassBasedEdgeFactory<>(edgeClass));
    }

    /**
     * Create a builder for this kind of graph.
     * 
     * @param edgeClass class on which to base factory for edges
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     * @return a builder for this kind of graph
     */
    public static <V, E> GraphBuilder<V, E, ? extends UndirectedWeightedGraph<V, E>> createBuilder(
        Class<? extends E> edgeClass)
    {
        return new GraphBuilder<>(new UndirectedWeightedGraph<>(edgeClass));
    }

    /**
     * Create a builder for this kind of graph.
     * 
     * @param ef the edge factory of the new graph
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     * @return a builder for this kind of graph
     */
    public static <V, E> GraphBuilder<V, E, ? extends UndirectedWeightedGraph<V, E>> createBuilder(
        EdgeFactory<V, E> ef)
    {
        return new GraphBuilder<>(new UndirectedWeightedGraph<>(ef));
    }
}
