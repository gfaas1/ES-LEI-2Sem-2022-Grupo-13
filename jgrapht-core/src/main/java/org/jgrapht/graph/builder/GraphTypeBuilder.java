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
package org.jgrapht.graph.builder;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.graph.*;
import org.jgrapht.util.TypeUtil;

/**
 * A builder class for the hierarchy of {@link Graph}s that the library provides.
 * 
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 * 
 * @author Dimitrios Michail
 */
public final class GraphTypeBuilder<V, E>
{
    private boolean undirected;
    private boolean directed;
    private boolean weighted;
    private boolean allowingMultipleEdges;
    private boolean allowingSelfLoops;
    private EdgeFactory<V, E> edgeFactory;

    private GraphTypeBuilder(boolean directed, boolean undirected)
    {
        this.directed = directed;
        this.undirected = undirected;
        this.weighted = false;
        this.allowingMultipleEdges = false;
        this.allowingSelfLoops = false;
    }

    /**
     * Create a graph type builder for a directed graph.
     * 
     * @return the graph type builder
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     */
    public static <V, E> GraphTypeBuilder<V, E> directed()
    {
        return new GraphTypeBuilder<>(true, false);
    }

    /**
     * Create a graph type builder for an undirected graph.
     * 
     * @return the graph type builder
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     */
    public static <V, E> GraphTypeBuilder<V, E> undirected()
    {
        return new GraphTypeBuilder<>(false, true);
    }

    /**
     * Create a graph type builder for a mixed graph.
     * 
     * @return the graph type builder
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     */
    public static <V, E> GraphTypeBuilder<V, E> mixed()
    {
        return new GraphTypeBuilder<>(true, true);
    }

    /**
     * Create a graph type builder which will create a graph with the same type as the one provided.
     * 
     * @param type the graph type
     * @return the graph type builder
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     */
    public static <V, E> GraphTypeBuilder<V, E> asGraphType(GraphType type)
    {
        GraphTypeBuilder<V, E> builder = new GraphTypeBuilder<>(
            type.isDirected() || type.isMixed(), type.isUndirected() || type.isMixed());
        builder.weighted = type.isWeighted();
        builder.allowingSelfLoops = type.isAllowingSelfLoops();
        builder.allowingMultipleEdges = type.isAllowingMultipleEdges();
        return builder;
    }

    /**
     * Create a graph type builder which will create the same graph type as the parameter graph.
     * 
     * @param graph a graph
     * @return a type builder
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     */
    public static <V, E> GraphTypeBuilder<V, E> asGraph(Graph<V, E> graph)
    {
        GraphTypeBuilder<V, E> builder = asGraphType(graph.getType());
        builder.edgeFactory = graph.getEdgeFactory();
        return builder;
    }

    /**
     * Set whether the graph will be weighted or not.
     * 
     * @param weighted if true the graph will be weighted
     * @return the graph type builder
     */
    public GraphTypeBuilder<V, E> weighted(boolean weighted)
    {
        this.weighted = weighted;
        return this;
    }

    /**
     * Set whether the graph will allow self loops (edges with same source and target vertices).
     * 
     * @param allowingSelfLoops if true the graph will allow self-loops
     * @return the graph type builder
     */
    public GraphTypeBuilder<V, E> allowingSelfLoops(boolean allowingSelfLoops)
    {
        this.allowingSelfLoops = allowingSelfLoops;
        return this;
    }

    /**
     * Set whether the graph will allow multiple (parallel) edges between the same two vertices.
     * 
     * @param allowingMultipleEdges if true the graph will allow multiple (parallel) edges
     * @return the graph type builder
     */
    public GraphTypeBuilder<V, E> allowingMultipleEdges(boolean allowingMultipleEdges)
    {
        this.allowingMultipleEdges = allowingMultipleEdges;
        return this;
    }

    /**
     * Set the edge factory.
     * 
     * @param edgeFactory the edge factory to use
     * @return the graph type builder
     * @param <V1> the graph vertex type
     * @param <E1> the graph edge type 
     */
    public <V1 extends V,
        E1 extends E> GraphTypeBuilder<V1, E1> edgeFactory(EdgeFactory<V1, E1> edgeFactory)
    {
        GraphTypeBuilder<V1, E1> newBuilder = TypeUtil.uncheckedCast(this, null);
        newBuilder.edgeFactory = edgeFactory;
        return newBuilder;
    }

    /**
     * Set the vertex class.
     * 
     * @param vertexClass the vertex class
     * @return the graph type builder
     * @param <V1> the graph vertex type
     */
    public <V1 extends V> GraphTypeBuilder<V1, E> vertexClass(Class<V1> vertexClass)
    {
        GraphTypeBuilder<V1, E> newBuilder = TypeUtil.uncheckedCast(this, null);
        return newBuilder;
    }

    /**
     * Set the edge class.
     * 
     * @param edgeClass the edge class
     * @return the graph type builder
     * @param <E1> the graph edge type 
     */
    public <E1 extends E> GraphTypeBuilder<V, E1> edgeClass(Class<E1> edgeClass)
    {
        GraphTypeBuilder<V, E1> newBuilder = TypeUtil.uncheckedCast(this, null);
        newBuilder.edgeFactory = new ClassBasedEdgeFactory<>(edgeClass);
        return newBuilder;
    }

    /**
     * Build the graph and acquire a {@link GraphBuilder} in order to add vertices and edges.
     * 
     * @return a graph builder
     */
    public GraphBuilder<V, E, Graph<V, E>> toGraphBuilder()
    {
        return new GraphBuilder<V, E, Graph<V, E>>(build());
    }

    /**
     * Build the actual graph.
     * 
     * @return the graph
     * @throws IllegalArgumentException if the edge factory is missing
     * @throws UnsupportedOperationException in case a graph type is not supported
     */
    public Graph<V, E> build()
    {
        if (directed && undirected) {
            throw new UnsupportedOperationException("Mixed graphs are not supported");
        } else if (edgeFactory == null) {
            throw new IllegalArgumentException("EdgeFactory missing");
        } else if (directed) {
            if (allowingSelfLoops && allowingMultipleEdges) {
                if (weighted) {
                    return new DirectedWeightedPseudograph<>(edgeFactory);
                } else {
                    return new DirectedPseudograph<>(edgeFactory);
                }
            } else if (allowingMultipleEdges) {
                if (weighted) {
                    return new DirectedWeightedMultigraph<>(edgeFactory);
                } else {
                    return new DirectedMultigraph<>(edgeFactory);
                }
            } else if (allowingSelfLoops) {
                if (weighted) {
                    return new DirectedWeightedGraph<>(edgeFactory);
                } else {
                    return new DirectedGraph<>(edgeFactory);
                }

            } else {
                if (weighted) {
                    return new SimpleDirectedWeightedGraph<>(edgeFactory);
                } else {
                    return new SimpleDirectedGraph<>(edgeFactory);
                }
            }
        } else {
            if (allowingSelfLoops && allowingMultipleEdges) {
                if (weighted) {
                    return new WeightedPseudograph<>(edgeFactory);
                } else {
                    return new Pseudograph<>(edgeFactory);
                }
            } else if (allowingMultipleEdges) {
                if (weighted) {
                    return new WeightedMultigraph<>(edgeFactory);
                } else {
                    return new Multigraph<>(edgeFactory);
                }
            } else if (allowingSelfLoops) {
                if (weighted) {
                    return new UndirectedWeightedGraph<>(edgeFactory);
                } else {
                    return new UndirectedGraph<>(edgeFactory);
                }

            } else {
                if (weighted) {
                    return new SimpleWeightedGraph<>(edgeFactory);
                } else {
                    return new SimpleGraph<>(edgeFactory);
                }
            }
        }
    }

}
