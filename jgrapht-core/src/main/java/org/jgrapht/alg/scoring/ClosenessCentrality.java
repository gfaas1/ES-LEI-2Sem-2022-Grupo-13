/*
 * (C) Copyright 2017-2017, by Dimitrios Michail and Contributors.
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
package org.jgrapht.alg.scoring;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.SingleSourcePaths;
import org.jgrapht.alg.interfaces.VertexScoringAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.graph.EdgeReversedGraph;

/**
 * Closeness centrality. Computes the closeness centrality of each vertex of a graph. See
 * <a href="https://en.wikipedia.org/wiki/Closeness_centrality">wikipedia</a> for the definition of
 * closeness centrality.
 *
 * <p>
 * This implementation computes by default the closeness centrality using outgoing paths and
 * normalizes the scores. This behavior can be adjusted by the constructor arguments.
 * 
 * <p>
 * Note that the closeness centrality is not defined when the graph is disconnected. Nevertheless,
 * if there is no (directed) path between two vertices then the total number of vertices is used in
 * the formula instead of the path length.
 * 
 * <p>
 * Shortest paths are computed either by using Dijkstra's algorithm or Floyd-Warshall depending on
 * whether the graph has edges with negative edge weights. Thus, the running time is either O(n (m +
 * n logn)) or O(n^3) respectively, where n is the number of vertices and m the number of edges of
 * the graph.
 * 
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 * 
 * @author Dimitrios Michail
 * @since January 2017
 */
public final class ClosenessCentrality<V, E>
    implements VertexScoringAlgorithm<V, Double>
{
    private final Graph<V, E> graph;
    private final boolean incoming;
    private final boolean normalize;
    private Map<V, Double> scores;

    /**
     * Construct a new instance. By default the closeness centrality is normalized and computed
     * using outgoing paths.
     * 
     * @param graph the input graph
     */
    public ClosenessCentrality(Graph<V, E> graph)
    {
        this(graph, false, true);
    }

    /**
     * Construct a new instance.
     * 
     * @param graph the input graph
     * @param incoming if true incoming paths are used, otherwise outgoing paths
     * @param normalize whether to normalize by multiplying the closeness by n-1, where n is the
     *        number of vertices of the graph
     */
    public ClosenessCentrality(Graph<V, E> graph, boolean incoming, boolean normalize)
    {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        this.incoming = incoming;
        this.normalize = normalize;
        this.scores = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<V, Double> getScores()
    {
        if (scores == null) {
            compute();
        }
        return Collections.unmodifiableMap(scores);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double getVertexScore(V v)
    {
        if (!graph.containsVertex(v)) {
            throw new IllegalArgumentException("Cannot return score of unknown vertex");
        }
        if (scores == null) {
            compute();
        }
        return scores.get(v);
    }

    private void compute()
    {
        // setup graph
        Graph<V, E> g;
        if (incoming && graph instanceof DirectedGraph<?, ?>) {
            g = new EdgeReversedGraph<>((DirectedGraph<V, E>) graph);
        } else {
            g = graph;
        }
        int n = g.vertexSet().size();

        // create result container
        this.scores = new HashMap<>();

        // test if we can use Dijkstra
        boolean nonNegativeWeights = true;
        for (E e : g.edgeSet()) {
            double w = g.getEdgeWeight(e);
            if (w < 0.0) {
                nonNegativeWeights = false;
                break;
            }
        }

        // initialize shortest path algorithm
        ShortestPathAlgorithm<V, E> alg;
        if (nonNegativeWeights) {
            alg = new DijkstraShortestPath<>(g);
        } else {
            alg = new FloydWarshallShortestPaths<>(g);
        }

        // compute shortest paths
        for (V v : g.vertexSet()) {
            double sum = 0d;

            SingleSourcePaths<V, E> paths = alg.getPaths(v);
            for (V u : g.vertexSet()) {
                if (!u.equals(v)) {
                    double pathWeight = paths.getWeight(u);
                    if (Double.isFinite(pathWeight)) {
                        sum += paths.getWeight(u);
                    } else {
                        sum += n;
                    }
                }
            }

            if (normalize) {
                this.scores.put(v, (n - 1) / sum);
            } else {
                this.scores.put(v, 1 / sum);
            }
        }
    }

}
