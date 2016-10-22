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
package org.jgrapht.alg.shortestpath;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.graph.GraphWalk;

/**
 * A base implementation of the shortest path interface.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Dimitrios Michail
 */
abstract class BaseShortestPathAlgorithm<V, E>
    implements ShortestPathAlgorithm<V, E>
{
    protected final Graph<V, E> graph;

    /**
     * Constructs a new instance of the algorithm for a given graph.
     * 
     * @param graph the graph
     */
    public BaseShortestPathAlgorithm(Graph<V, E> graph)
    {
        this.graph = Objects.requireNonNull(graph, "Graph is null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SingleSourcePaths<V, E> getPaths(V source)
    {
        if (!graph.containsVertex(source)) {
            throw new IllegalArgumentException("graph must contain the source vertex");
        }

        Map<V, GraphPath<V, E>> paths = new HashMap<>();
        for (V v : graph.vertexSet()) {
            paths.put(v, getPath(source, v));
        }
        return new ListSingleSourcePaths<>(graph, source, paths);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AllPairsPaths<V, E> getPaths()
    {
        Map<V, SingleSourcePaths<V, E>> paths = new HashMap<>();
        for (V v : graph.vertexSet()) {
            paths.put(v, getPaths(v));
        }
        return new DefaultAllPairsPaths<>(graph, paths);
    }

    /**
     * Create an empty path.
     * 
     * @param source the source vertex
     * @param sink the sink vertex
     * @return an empty path
     */
    GraphPath<V, E> createEmptyPath(V source, V sink)
    {
        if (source.equals(sink)) {
            return new GraphWalk<>(
                graph, source, sink, Collections.singletonList(source), Collections.emptyList(),
                0d);
        } else {
            return new GraphWalk<>(
                graph, source, sink, Collections.emptyList(), Collections.emptyList(),
                Double.POSITIVE_INFINITY);
        }
    }

}
