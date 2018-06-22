/*
 * (C) Copyright 2018, by Lukas Harzenetter and Contributors.
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
import java.util.Map;
import java.util.Objects;

import org.jgrapht.Graph;
import org.jgrapht.GraphType;

/**
 * Provides a weighted view on a graph
 *
 * Algorithms designed for weighted graphs should also work on unweighted graphs. This class
 * emulates an weighted graph based on a unweighted one by handling the storage of edge weights
 * internally and passing all other operations on the underlying graph. As a consequence, the edges
 * returned are the edges of the original graph.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 */
public class AsWeightedGraph<V, E>
    extends GraphDelegator<V, E>
    implements Serializable, Graph<V, E>
{

    private static final long serialVersionUID = -6838132233557L;
    private final Map<E, Double> weights;

    /**
     * Constructor for AsWeightedGraph
     *
     * @param graph   the backing graph over which an weighted view is to be created.
     * @param weights the map containing the edge weights
     * @throws NullPointerException if the graph is null
     */
    public AsWeightedGraph(Graph<V, E> graph, Map<E, Double> weights)
    {
        super(graph);
        this.weights = weights;
    }

    /**
     * Returns the weight assigned to a given edge.
     * If there is no edge weight set for the given edge, the value of the backing graph's
     * getEdgeWeight method is returned.
     *
     * @param e edge of interest
     * @return the edge weight
     * @throws NullPointerException if the edge is null
     */
    @Override public double getEdgeWeight(E e)
    {
        Objects.requireNonNull(e);
        Double weight = this.weights.get(e);

        if (Objects.isNull(weight)) {
            weight = super.getEdgeWeight(e);
        }

        return weight;
    }

    /**
     * Assigns a weight to an edge.
     *
     * @param e      edge on which to set weight
     * @param weight new weight for edge
     * @throws NullPointerException if the edge is null
     */
    @Override public void setEdgeWeight(E e, double weight)
    {
        Objects.requireNonNull(e);
        this.weights.put(e, weight);
    }

    @Override public GraphType getType()
    {
        return super.getType().asWeighted();
    }

}
