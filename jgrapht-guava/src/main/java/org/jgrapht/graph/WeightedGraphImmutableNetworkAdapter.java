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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.util.TypeUtil;

import com.google.common.graph.ImmutableNetwork;

/**
 * A weighted graph adapter class using Guava's {@link ImmutableNetwork}.
 * 
 * @author Dimitrios Michail
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 */
public class WeightedGraphImmutableNetworkAdapter<V, E>
    extends GraphImmutableNetworkAdapter<V, E>
    implements Cloneable, Serializable
{
    private static final long serialVersionUID = -2536810479875607753L;

    protected Map<E, Double> weights;

    /**
     * Create a new network adapter.
     * 
     * @param network the immutable network
     * @param ef the edge factory of the new graph
     */
    public WeightedGraphImmutableNetworkAdapter(
        ImmutableNetwork<V, E> network, EdgeFactory<V, E> ef)
    {
        super(network, ef);
        this.weights = new HashMap<>();
    }

    /**
     * Create a new network adapter.
     * 
     * @param network the immutable network
     * @param edgeClass class on which to base factory for edges
     */
    public WeightedGraphImmutableNetworkAdapter(
        ImmutableNetwork<V, E> network, Class<? extends E> edgeClass)
    {
        this(network, new ClassBasedEdgeFactory<>(edgeClass));
    }

    @Override
    public GraphType getType()
    {
        return super.getType().asWeighted();
    }

    @Override
    public double getEdgeWeight(E e)
    {
        if (e == null) {
            throw new NullPointerException();
        } else if (!super.containsEdge(e)) {
            throw new IllegalArgumentException("no such edge in graph: " + e.toString());
        } else if (e instanceof IntrusiveWeightedEdge) {
            return ((IntrusiveWeightedEdge) e).weight;
        } else {
            return weights.getOrDefault(e, Graph.DEFAULT_EDGE_WEIGHT);
        }
    }

    @Override
    public void setEdgeWeight(E e, double weight)
    {
        if (e == null) {
            throw new NullPointerException();
        } else if (!super.containsEdge(e)) {
            throw new IllegalArgumentException("no such edge in graph: " + e.toString());
        } else if (e instanceof IntrusiveWeightedEdge) {
            ((IntrusiveWeightedEdge) e).weight = weight;
        } else {
            weights.put(e, weight);
        }
    }

    /**
     * Returns a shallow copy of this graph instance. Neither edges nor vertices are cloned.
     *
     * @return a shallow copy of this set.
     *
     * @throws RuntimeException in case the clone is not supported
     *
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone()
    {
        WeightedGraphImmutableNetworkAdapter<V, E> newGraph = TypeUtil.uncheckedCast(super.clone());
        newGraph.weights = new HashMap<>(this.weights);
        return newGraph;
    }

}
