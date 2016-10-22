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
import java.util.Map;
import java.util.Objects;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.SingleSourcePaths;
import org.jgrapht.graph.GraphWalk;

/**
 * An implementation of {@link SingleSourcePaths} which keeps one path per vertex.
 * 
 * @author Dimitrios Michail
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 */
class ListSingleSourcePaths<V, E>
    implements SingleSourcePaths<V, E>
{
    private Graph<V, E> graph;
    private V source;
    private Map<V, GraphPath<V, E>> paths;

    /**
     * Construct a new instance.
     * 
     * @param graph the graph
     * @param source the source vertex
     * @param paths one path per target vertex
     */
    public ListSingleSourcePaths(Graph<V, E> graph, V source, Map<V, GraphPath<V, E>> paths)
    {
        this.graph = Objects.requireNonNull(graph, "Graph is null");
        this.source = Objects.requireNonNull(source, "Source vertex is null");
        this.paths = Objects.requireNonNull(paths, "Paths are null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Graph<V, E> getGraph()
    {
        return graph;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V getSourceVertex()
    {
        return source;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getWeight(V targetVertex)
    {
        GraphPath<V, E> p = paths.get(targetVertex);
        if (p == null) {
            if (source.equals(targetVertex)) {
                return 0d;
            } else {
                return Double.POSITIVE_INFINITY;
            }
        } else {
            return p.getWeight();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphPath<V, E> getPath(V targetVertex)
    {
        GraphPath<V, E> p = paths.get(targetVertex);
        if (p == null) {
            if (source.equals(targetVertex)) {
                return new GraphWalk<>(
                    graph, source, targetVertex, Collections.singletonList(source),
                    Collections.emptyList(), 0d);
            } else {
                return new GraphWalk<>(
                    graph, source, targetVertex, Collections.emptyList(), Collections.emptyList(),
                    Double.POSITIVE_INFINITY);
            }
        } else {
            return p;
        }
    }

}
