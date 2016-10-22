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
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.AllPairsPaths;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.SingleSourcePaths;
import org.jgrapht.graph.GraphWalk;

/**
 * A default implementation of {@link AllPairsPaths}.
 * 
 * @author Dimitrios Michail
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 */
class DefaultAllPairsPaths<V, E>
    implements AllPairsPaths<V, E>
{
    private Graph<V, E> graph;
    private Map<V, SingleSourcePaths<V, E>> paths;

    /**
     * Construct a new instance.
     * 
     * @param graph the graph
     * @param paths one single-source shortest path run for each vertex
     */
    public DefaultAllPairsPaths(Graph<V, E> graph, Map<V, SingleSourcePaths<V, E>> paths)
    {
        this.graph = Objects.requireNonNull(graph, "Graph is null");
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
    public double getWeight(V sourceVertex, V targetVertex)
    {
        SingleSourcePaths<V, E> ss = paths.get(sourceVertex);
        if (ss == null) {
            if (sourceVertex.equals(targetVertex)) {
                return 0d;
            } else {
                return Double.POSITIVE_INFINITY;
            }
        } else {
            return paths.get(sourceVertex).getWeight(targetVertex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphPath<V, E> getPath(V sourceVertex, V targetVertex)
    {
        SingleSourcePaths<V, E> ss = paths.get(sourceVertex);
        if (ss == null) {
            if (sourceVertex.equals(targetVertex)) {
                return new GraphWalk<>(
                    graph, sourceVertex, targetVertex, Collections.singletonList(sourceVertex),
                    Collections.emptyList(), 0d);
            } else {
                return new GraphWalk<>(
                    graph, sourceVertex, targetVertex, Collections.emptyList(),
                    Collections.emptyList(), Double.POSITIVE_INFINITY);
            }
        } else {
            return ss.getPath(targetVertex);
        }
    }

}
