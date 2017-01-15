/*
 * (C) Copyright 2006-2017, by France Telecom and Contributors.
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

import org.jgrapht.*;
import org.jgrapht.graph.*;

/**
 * The <a href="https://en.wikipedia.org/wiki/Bellman%E2%80%93Ford_algorithm">Bellman-Ford
 * algorithm</a>. Computes shortest paths form a single source vertex to all the other vertices in a
 * weighted graph. Supports graphs with negative weights. Additionally the user can constrain the
 * paths by a maximum number of edges.
 * 
 * The algorithm has worst-case complexity O(nm) when n is the number of vertices and m the number
 * of edges of the input graph.
 * 
 * 
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 */
public class BellmanFordShortestPath<V, E>
    extends BaseShortestPathAlgorithm<V, E>
{
    private static final double DEFAULT_EPSILON = 0.000000001;

    /**
     * Start vertex.
     */
    protected V startVertex;

    /**
     * Maximum number of edges of the calculated paths.
     */
    protected int nMaxHops;

    /**
     * Tolerance when comparing floating point values.
     */
    protected double epsilon;

    /**
     * Construct a new instance of the Bellman-Ford algorithm.
     *
     * @param graph the input graph
     */
    public BellmanFordShortestPath(Graph<V, E> graph)
    {
        this(graph, graph.vertexSet().size() - 1);
    }

    /**
     * Construct a new instance of the Bellman-Ford algorithm.
     *
     * @param graph the input graph
     * @param nMaxHops maximum number of edges of the calculated paths
     */
    public BellmanFordShortestPath(Graph<V, E> graph, int nMaxHops)
    {
        this(graph, nMaxHops, DEFAULT_EPSILON);
    }

    /**
     * Construct a new instance of the Bellman-Ford algorithm.
     *
     * @param graph the input graph
     * @param nMaxHops maximum number of edges of the calculated paths.
     * @param epsilon tolerance factor when comparing floating point values
     */
    public BellmanFordShortestPath(Graph<V, E> graph, int nMaxHops, double epsilon)
    {
        super(graph);
        this.nMaxHops = nMaxHops;
        this.epsilon = epsilon;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphPath<V, E> getPath(V source, V sink)
    {
        if (!graph.containsVertex(sink)) {
            throw new IllegalArgumentException("Graph must contain the sink vertex!");
        }
        return getPaths(source).getPath(sink);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SingleSourcePaths<V, E> getPaths(V source)
    {
        if (!graph.containsVertex(source)) {
            throw new IllegalArgumentException("Graph must contain the source vertex!");
        }

        BellmanFordIterator<V, E> iter = new BellmanFordIterator<>(graph, source, epsilon);

        // at the i-th pass the shortest paths with less (or equal) than i edges
        // are calculated.
        for (int passNumber = 1; (passNumber <= nMaxHops) && iter.hasNext(); passNumber++) {
            iter.next();
        }

        return new PathElementSingleSourcePaths(iter);
    }

    /**
     * Find a path between two vertices. For a more advanced search (e.g. limited by radius), use
     * the constructor instead.
     * 
     * @param graph the graph to be searched
     * @param source the vertex at which the path should start
     * @param sink the vertex at which the path should end
     * 
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     *
     * @return a shortest path, or null if no path exists
     */
    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph, V source, V sink)
    {
        return new BellmanFordShortestPath<>(graph).getPath(source, sink);
    }

    // interface wrapper
    private class PathElementSingleSourcePaths
        implements SingleSourcePaths<V, E>
    {
        private BellmanFordIterator<V, E> it;

        PathElementSingleSourcePaths(BellmanFordIterator<V, E> it)
        {
            this.it = it;
        }

        @Override
        public Graph<V, E> getGraph()
        {
            return it.graph;
        }

        @Override
        public V getSourceVertex()
        {
            return it.startVertex;
        }

        @Override
        public double getWeight(V targetVertex)
        {
            if (targetVertex.equals(it.startVertex)) {
                return 0d;
            }

            BellmanFordPathElement<V, E> pathElement = it.getPathElement(targetVertex);

            if (pathElement == null) {
                return Double.POSITIVE_INFINITY;
            } else {
                return pathElement.getCost();
            }
        }

        @Override
        public GraphPath<V, E> getPath(V targetVertex)
        {
            if (targetVertex.equals(it.startVertex)) {
                return createEmptyPath(it.startVertex, targetVertex);
            }

            BellmanFordPathElement<V, E> pathElement = it.getPathElement(targetVertex);

            if (pathElement == null) {
                return createEmptyPath(it.startVertex, targetVertex);
            } else {
                return new GraphWalk<>(
                    graph, it.startVertex, targetVertex, null, pathElement.createEdgeListPath(),
                    pathElement.getCost());
            }
        }

    }

}

// End BellmanFordShortestPath.java
