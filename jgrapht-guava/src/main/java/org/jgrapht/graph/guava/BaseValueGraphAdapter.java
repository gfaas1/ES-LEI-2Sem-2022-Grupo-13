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
import java.util.function.ToDoubleFunction;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toSet;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.graph.AbstractGraph;
import org.jgrapht.graph.DefaultGraphType;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.ValueGraph;

/**
 * A base abstract implementation for the graph adapter class using Guava's {@link ValueGraph}. This
 * is a helper class in order to support both mutable and immutable value graphs.
 * 
 * @author Dimitrios Michail
 *
 * @param <V> the graph vertex type
 * @param <W> the value type
 * @param <VG> type of the underlying Guava's value graph
 */
public abstract class BaseValueGraphAdapter<V, W, VG extends ValueGraph<V, W>>
    extends AbstractGraph<V, EndpointPair<V>>
    implements Graph<V, EndpointPair<V>>, Cloneable, Serializable
{
    private static final long serialVersionUID = 3833510139696864917L;

    protected static final String LOOPS_NOT_ALLOWED = "loops not allowed";

    protected transient Set<V> unmodifiableVertexSet = null;
    protected transient Set<EndpointPair<V>> unmodifiableEdgeSet = null;

    protected ToDoubleFunction<W> valueConverter;
    protected transient VG valueGraph;

    /**
     * Create a new adapter.
     * 
     * @param valueGraph the mutable value graph
     * @param valueConverter a function that converts a value to a double
     */
    public BaseValueGraphAdapter(VG valueGraph, ToDoubleFunction<W> valueConverter)
    {
        this.valueGraph = Objects.requireNonNull(valueGraph);
        this.valueConverter = Objects.requireNonNull(valueConverter);
    }

    @Override
    public EndpointPair<V> getEdge(V sourceVertex, V targetVertex)
    {
        if (sourceVertex == null || targetVertex == null) {
            return null;
        } else if (!valueGraph.hasEdgeConnecting(sourceVertex, targetVertex)) {
            return null;
        } else {
            return createEdge(sourceVertex, targetVertex);
        }
    }

    @Override
    public EdgeFactory<V, EndpointPair<V>> getEdgeFactory()
    {
        return this::createEdge;
    }

    @Override
    public Set<V> vertexSet()
    {
        if (unmodifiableVertexSet == null) {
            unmodifiableVertexSet = Collections.unmodifiableSet(valueGraph.nodes());
        }
        return unmodifiableVertexSet;
    }

    @Override
    public V getEdgeSource(EndpointPair<V> e)
    {
        return e.nodeU();
    }

    @Override
    public V getEdgeTarget(EndpointPair<V> e)
    {
        return e.nodeV();
    }

    @Override
    public GraphType getType()
    {
        return (valueGraph.isDirected() ? new DefaultGraphType.Builder().directed()
            : new DefaultGraphType.Builder().undirected())
                .weighted(true).allowMultipleEdges(false)
                .allowSelfLoops(valueGraph.allowsSelfLoops()).build();
    }

    @Override
    public boolean containsEdge(EndpointPair<V> e)
    {
        return valueGraph.edges().contains(e);
    }

    @Override
    public boolean containsVertex(V v)
    {
        return valueGraph.nodes().contains(v);
    }

    @Override
    public Set<EndpointPair<V>> edgeSet()
    {
        if (unmodifiableEdgeSet == null) {
            unmodifiableEdgeSet = Collections.unmodifiableSet(valueGraph.edges());
        }
        return unmodifiableEdgeSet;
    }

    @Override
    public int degreeOf(V vertex)
    {
        return valueGraph.degree(vertex);
    }

    @Override
    public Set<EndpointPair<V>> edgesOf(V vertex)
    {
        return valueGraph.incidentEdges(vertex);
    }

    @Override
    public int inDegreeOf(V vertex)
    {
        return valueGraph.inDegree(vertex);
    }

    @Override
    public Set<EndpointPair<V>> incomingEdgesOf(V vertex)
    {
        return valueGraph
            .predecessors(vertex).stream().map(other -> createEdge(other, vertex))
            .collect(collectingAndThen(toSet(), Collections::unmodifiableSet));
    }

    @Override
    public int outDegreeOf(V vertex)
    {
        return valueGraph.outDegree(vertex);
    }

    @Override
    public Set<EndpointPair<V>> outgoingEdgesOf(V vertex)
    {
        return valueGraph
            .successors(vertex).stream().map(other -> createEdge(vertex, other))
            .collect(collectingAndThen(toSet(), Collections::unmodifiableSet));
    }

    @Override
    public double getEdgeWeight(EndpointPair<V> e)
    {
        if (e == null) {
            throw new NullPointerException();
        } else if (!valueGraph.hasEdgeConnecting(e.nodeU(), e.nodeV())) {
            throw new IllegalArgumentException("no such edge in graph: " + e.toString());
        } else {
            return valueGraph
                .edgeValue(e.nodeU(), e.nodeV()).map(valueConverter::applyAsDouble)
                .orElse(Graph.DEFAULT_EDGE_WEIGHT);
        }
    }

    @Override
    public Set<EndpointPair<V>> getAllEdges(V sourceVertex, V targetVertex)
    {
        if (sourceVertex == null || targetVertex == null
            || !valueGraph.nodes().contains(sourceVertex)
            || !valueGraph.nodes().contains(targetVertex))
        {
            return null;
        } else if (!valueGraph.hasEdgeConnecting(sourceVertex, targetVertex)) {
            return Collections.emptySet();
        } else {
            return Collections.singleton(createEdge(sourceVertex, targetVertex));
        }
    }

    /**
     * Create an edge
     * 
     * @param s the source vertex
     * @param t the target vertex
     * @return the edge
     */
    EndpointPair<V> createEdge(V s, V t)
    {
        return valueGraph.isDirected() ? EndpointPair.ordered(s, t) : EndpointPair.unordered(s, t);
    }

}
