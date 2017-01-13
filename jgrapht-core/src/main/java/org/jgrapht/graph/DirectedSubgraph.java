/*
 * (C) Copyright 2003-2017, by Barak Naveh and Contributors.
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

import java.util.*;
import java.util.stream.*;

import org.jgrapht.*;

/**
 * A directed graph that is a subgraph of another graph.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @see Subgraph
 */
public class DirectedSubgraph<V, E>
    extends Subgraph<V, E, DirectedGraph<V, E>>
    implements DirectedGraph<V, E>
{
    private static final long serialVersionUID = 3616445700507054133L;

    /**
     * Creates a new directed subgraph.
     *
     * @param base the base (backing) graph on which the subgraph will be based.
     * @param vertexSubset vertices to include in the subgraph. If <code>null</code> then all
     *        vertices are included.
     * @param edgeSubset edges to include in the subgraph. If <code>null</code> then all the edges
     *        whose vertices found in the graph are included.
     */
    public DirectedSubgraph(
        DirectedGraph<V, E> base, Set<? extends V> vertexSubset, Set<? extends E> edgeSubset)
    {
        super(base, vertexSubset, edgeSubset);
    }

    /**
     * Creates a new directed induced subgraph.
     *
     * @param base the base (backing) graph on which the subgraph will be based.
     * @param vertexSubset vertices to include in the subgraph. If <code>null</code> then all
     *        vertices are included.
     */
    public DirectedSubgraph(DirectedGraph<V, E> base, Set<? extends V> vertexSubset)
    {
        this(base, vertexSubset, null);
    }

    /**
     * Creates a new directed induced subgraph with all vertices included.
     *
     * @param base the base (backing) graph on which the subgraph will be based.
     */
    public DirectedSubgraph(DirectedGraph<V, E> base)
    {
        this(base, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int inDegreeOf(V vertex)
    {
        return incomingEdgesOf(vertex).size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> incomingEdgesOf(V vertex)
    {
        assertVertexExist(vertex);

        return base.incomingEdgesOf(vertex).stream().filter(e -> edgeSet.contains(e)).collect(
            Collectors.toCollection(() -> new LinkedHashSet<>()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int outDegreeOf(V vertex)
    {
        return outgoingEdgesOf(vertex).size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> outgoingEdgesOf(V vertex)
    {
        assertVertexExist(vertex);

        return base.outgoingEdgesOf(vertex).stream().filter(e -> edgeSet.contains(e)).collect(
            Collectors.toCollection(() -> new LinkedHashSet<>()));
    }
}

// End DirectedSubgraph.java
