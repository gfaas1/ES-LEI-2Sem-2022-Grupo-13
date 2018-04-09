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
package org.jgrapht.graph.guava;

import java.io.Serializable;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.graph.AbstractGraph;
import org.jgrapht.graph.DefaultGraphType;

import com.google.common.graph.Network;

/**
 * A base abstract implementation for the graph adapter class using Guava's {@link Network}. This is
 * a helper class in order to support both mutable and immutable networks.
 * 
 * @author Dimitrios Michail
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 * @param <N> type of the underlying Guava's network
 */
public abstract class BaseNetworkAdapter<V, E, N extends Network<V, E>>
    extends AbstractGraph<V, E>
    implements Graph<V, E>, Cloneable, Serializable
{
    private static final long serialVersionUID = -6233085794632237761L;

    protected static final String LOOPS_NOT_ALLOWED = "loops not allowed";

    protected transient Set<V> unmodifiableVertexSet = null;
    protected transient Set<E> unmodifiableEdgeSet = null;

    protected EdgeFactory<V, E> edgeFactory;
    protected transient N network;

    /**
     * Create a new network adapter.
     * 
     * @param network the mutable network
     * @param ef the edge factory of the new graph
     */
    public BaseNetworkAdapter(N network, EdgeFactory<V, E> ef)
    {
        this.edgeFactory = Objects.requireNonNull(ef);
        this.network = Objects.requireNonNull(network);
    }

    @Override
    public E getEdge(V sourceVertex, V targetVertex)
    {
        return network
            .edgesConnecting(sourceVertex, targetVertex).stream().findFirst().orElse(null);
    }

    @Override
    public EdgeFactory<V, E> getEdgeFactory()
    {
        return edgeFactory;
    }

    @Override
    public Set<V> vertexSet()
    {
        if (unmodifiableVertexSet == null) {
            unmodifiableVertexSet = Collections.unmodifiableSet(network.nodes());
        }
        return unmodifiableVertexSet;
    }

    @Override
    public V getEdgeSource(E e)
    {
        return network.incidentNodes(e).nodeU();
    }

    @Override
    public V getEdgeTarget(E e)
    {
        return network.incidentNodes(e).nodeV();
    }

    @Override
    public GraphType getType()
    {
        return (network.isDirected() ? new DefaultGraphType.Builder().directed()
            : new DefaultGraphType.Builder().undirected())
                .weighted(false).allowMultipleEdges(network.allowsParallelEdges())
                .allowSelfLoops(network.allowsSelfLoops()).build();
    }

    @Override
    public boolean containsEdge(E e)
    {
        return network.edges().contains(e);
    }

    @Override
    public boolean containsVertex(V v)
    {
        return network.nodes().contains(v);
    }

    @Override
    public Set<E> edgeSet()
    {
        if (unmodifiableEdgeSet == null) {
            unmodifiableEdgeSet = Collections.unmodifiableSet(network.edges());
        }
        return unmodifiableEdgeSet;
    }

    @Override
    public int degreeOf(V vertex)
    {
        return network.degree(vertex);
    }

    @Override
    public Set<E> edgesOf(V vertex)
    {
        return network.incidentEdges(vertex);
    }

    @Override
    public int inDegreeOf(V vertex)
    {
        return network.inDegree(vertex);
    }

    @Override
    public Set<E> incomingEdgesOf(V vertex)
    {
        return network.inEdges(vertex);
    }

    @Override
    public int outDegreeOf(V vertex)
    {
        return network.outDegree(vertex);
    }

    @Override
    public Set<E> outgoingEdgesOf(V vertex)
    {
        return network.outEdges(vertex);
    }

    @Override
    public double getEdgeWeight(E e)
    {
        if (e == null) { 
            throw new NullPointerException();
        } else if (!network.edges().contains(e)) { 
            throw new IllegalArgumentException("no such edge in graph: " + e.toString());
        } else { 
            return Graph.DEFAULT_EDGE_WEIGHT;
        }
    }

    @Override
    public Set<E> getAllEdges(V sourceVertex, V targetVertex)
    {
        return network.edgesConnecting(sourceVertex, targetVertex);
    }

}
