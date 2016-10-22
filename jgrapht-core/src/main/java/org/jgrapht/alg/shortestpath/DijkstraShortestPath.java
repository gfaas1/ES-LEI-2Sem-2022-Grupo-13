/*
 * (C) Copyright 2003-2016, by John V Sichi and Contributors.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.traverse.ClosestFirstIterator;

/**
 * An implementation of <a href="http://mathworld.wolfram.com/DijkstrasAlgorithm.html">Dijkstra's
 * shortest path algorithm</a> using <code>ClosestFirstIterator</code>.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author John V. Sichi
 * @since Sep 2, 2003
 */
public final class DijkstraShortestPath<V, E>
    extends BaseShortestPathAlgorithm<V, E>
{
    private final double radius;

    /**
     * Constructs a new instance of the algorithm for a given graph.
     * 
     * @param graph the graph
     */
    public DijkstraShortestPath(Graph<V, E> graph)
    {
        this(graph, Double.POSITIVE_INFINITY);
    }

    /**
     * Constructs a new instance of the algorithm for a given graph.
     *
     * @param graph the graph
     * @param radius limit on path length, or Double.POSITIVE_INFINITY for unbounded search
     */
    public DijkstraShortestPath(Graph<V, E> graph, double radius)
    {
        super(graph);
        this.radius = radius;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphPath<V, E> getPath(V source, V sink)
    {
        if (!graph.containsVertex(source)) {
            throw new IllegalArgumentException("graph must contain the source vertex");
        }
        if (!graph.containsVertex(sink)) {
            throw new IllegalArgumentException("graph must contain the sink vertex");
        }

        ClosestFirstIterator<V, E> iter = new ClosestFirstIterator<>(graph, source, radius);

        while (iter.hasNext()) {
            V vertex = iter.next();

            if (vertex.equals(sink)) {
                return createPath(graph, iter, source, sink);
            }
        }

        return createEmptyPath(source, sink);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SingleSourcePaths<V, E> getPaths(V source)
    {
        if (!graph.containsVertex(source)) {
            throw new IllegalArgumentException("graph must contain the start vertex");
        }

        // traverse
        ClosestFirstIterator<V, E> iter = new ClosestFirstIterator<>(graph, source, radius);

        while (iter.hasNext()) {
            iter.next();
        }

        // compute distance and predecessor map
        Map<V, Pair<Double, E>> map = new HashMap<>();
        for (V v : graph.vertexSet()) {
            E e = iter.getSpanningTreeEdge(v);
            if (e != null) {
                map.put(v, Pair.of(iter.getShortestPathLength(v), e));
            }
        }

        return new TreeSingleSourcePaths<>(graph, source, map);
    }

    private GraphPath<V, E> createPath(
        Graph<V, E> graph, ClosestFirstIterator<V, E> iter, V startVertex, V endVertex)
    {
        List<E> edgeList = new ArrayList<>();
        List<V> vertexList = new ArrayList<>();
        vertexList.add(endVertex);

        V v = endVertex;

        while (true) {
            E edge = iter.getSpanningTreeEdge(v);

            if (edge == null) {
                break;
            }

            edgeList.add(edge);
            v = Graphs.getOppositeVertex(graph, edge, v);
            vertexList.add(v);
        }

        Collections.reverse(edgeList);
        Collections.reverse(vertexList);
        double pathLength = iter.getShortestPathLength(endVertex);
        return new GraphWalk<>(graph, startVertex, endVertex, vertexList, edgeList, pathLength);
    }

}
