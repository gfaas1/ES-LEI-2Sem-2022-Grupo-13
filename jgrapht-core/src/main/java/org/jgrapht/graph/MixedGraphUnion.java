/*
 * (C) Copyright 2015-2016, by Joris Kinable. and Contributors.
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

import org.jgrapht.*;
import org.jgrapht.util.*;

public class MixedGraphUnion<V, E>
    extends GraphUnion<V, E, Graph<V, E>>
    implements DirectedGraph<V, E>
{
    private static final long serialVersionUID = -1961714127770731054L;
    private final UndirectedGraph<V, E> undirectedGraph;
    private final DirectedGraph<V, E> directedGraph;

    public MixedGraphUnion(
        UndirectedGraph<V, E> g1, DirectedGraph<V, E> g2, WeightCombiner operator)
    {
        super(g1, g2, operator);
        this.undirectedGraph = g1;
        this.directedGraph = g2;
    }

    public MixedGraphUnion(UndirectedGraph<V, E> g1, DirectedGraph<V, E> g2)
    {
        super(g1, g2);
        this.undirectedGraph = g1;
        this.directedGraph = g2;
    }

    @Override
    public int inDegreeOf(V vertex)
    {
        Set<E> res = incomingEdgesOf(vertex);
        return res.size();
    }

    @Override
    public Set<E> incomingEdgesOf(V vertex)
    {
        Set<E> res = new LinkedHashSet<>();
        if (directedGraph.containsVertex(vertex)) {
            res.addAll(directedGraph.incomingEdgesOf(vertex));
        }
        if (undirectedGraph.containsVertex(vertex)) {
            res.addAll(undirectedGraph.edgesOf(vertex));
        }
        return Collections.unmodifiableSet(res);
    }

    @Override
    public int outDegreeOf(V vertex)
    {
        Set<E> res = outgoingEdgesOf(vertex);
        return res.size();
    }

    @Override
    public Set<E> outgoingEdgesOf(V vertex)
    {
        Set<E> res = new LinkedHashSet<>();
        if (directedGraph.containsVertex(vertex)) {
            res.addAll(directedGraph.outgoingEdgesOf(vertex));
        }
        if (undirectedGraph.containsVertex(vertex)) {
            res.addAll(undirectedGraph.edgesOf(vertex));
        }
        return Collections.unmodifiableSet(res);
    }
}

// End MixedGraphUnion.java
