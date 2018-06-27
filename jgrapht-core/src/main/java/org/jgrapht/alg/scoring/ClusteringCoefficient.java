/*
 * (C) Copyright 2018-2018, by Alexandru Valeanu and Contributors.
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

import org.jgrapht.Graph;
import org.jgrapht.GraphMetrics;
import org.jgrapht.alg.interfaces.VertexScoringAlgorithm;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Clustering coefficient.
 *
 * <p>
 * Computes the local clustering coefficient of each vertex of a graph. The local clustering coefficient of a
 * node $v$ is given by the expression: $g(v)= \sum_{s \neq v \neq
 * t}\frac{\sigma_{st}(v)}{\sigma_{st}}$ where $\sigma_{st}$ is the total number of shortest paths
 * from node $s$ to node $t$ and $\sigma_{st}(v)$ is the number of those paths that pass through
 * $v$. For more details see
 * <a href="https://en.wikipedia.org/wiki/Clustering_coefficient">wikipedia</a>.
 *
 * <p>
 * This implementation also computes the global clustering coefficient as well as the average clustering coefficient.
 *
 * <p>
 * The running time is $O(|V| + |D|^2) where $|V|$ is the number of vertices and $|D|$ is the maximum degree of a
 * vertex. The space complexity is $O(|V|)$.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Alexandru Valeanu
 * @since June 2018
 */
public class ClusteringCoefficient<V, E> implements VertexScoringAlgorithm<V, Double> {

    /**
     * Underlying graph
     */
    private final Graph<V, E> graph;

    /**
     * The actual scores
     */
    private Map<V, Double> scores;

    /**
     * Global Clustering Coefficient
     */
    private boolean computed = false;
    private double globalClusteringCoefficient;

    /**
     * Average Clustering Coefficient
     */
    private boolean computedAverage = false;
    private double averageClusteringCoefficient;

    /**
     * Construct a new instance
     *
     * @param graph the input graph
     * @throws NullPointerException if {@code graph} is {@code null}
     */
    public ClusteringCoefficient(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph);
    }

    /**
     * Computes the global clustering coefficient. The global clustering coefficient $C$ is defined as
     * $C = 3 \times number\_of\_triangles / number\_of\_triplets$.
     *
     * @return the global clustering coefficient
     */
    public double getGlobalClusteringCoefficient() {
        if (!computed){
            computeGlobalClusteringCoefficient();
        }

        return globalClusteringCoefficient;
    }

    /**
     * Computes the average clustering coefficient. The average clustering coefficient $\={C}$ is defined as
     * $\={C} = \frac{\sum_{i=1}^{n} C_i}{n}$ where $n$ is the number of vertices.
     *
     * Note: the average is $0$ is the graph is empty
     *
     * @return the average clustering coefficient
     */
    public double getAverageClusteringCoefficient(){
        if (graph.vertexSet().size() == 0)
            return 0;

        if (!computedAverage){
            computedAverage = true;
            averageClusteringCoefficient = 0;

            for (double value: scores.values())
                averageClusteringCoefficient += value;

            averageClusteringCoefficient /= graph.vertexSet().size();
        }

        return averageClusteringCoefficient;
    }

    // https://data.graphstream-project.org/api/gs-algo/current/
    // https://en.wikipedia.org/wiki/Clustering_coefficient
    // https://math.stackexchange.com/questions/2657701/what-is-mean-number-of-connected-triplets-of-vertices-in-global-clustering
    // https://github.com/jgrapht/jgrapht/pull/607

    private void computeGlobalClusteringCoefficient(){
        computed = true;
        double numberTriplets = 0;

        for (V v: graph.vertexSet()){
            if (graph.getType().isUndirected()){
                numberTriplets += 1.0 * graph.degreeOf(v) * (graph.degreeOf(v) - 1) / 2;
            }
            else{
                numberTriplets += 1.0 * graph.inDegreeOf(v) * graph.outDegreeOf(v);
            }
        }

        globalClusteringCoefficient = 3 * GraphMetrics.getNumberOfTriangles(graph) / numberTriplets;
    }

    private void compute(){
        scores = new HashMap<>(graph.vertexSet().size());

        for (V v: graph.vertexSet()){
            Set<V> neighbourhood = new HashSet<>();

            neighbourhood.addAll(
                    graph.incomingEdgesOf(v).stream().map(graph::getEdgeTarget).collect(Collectors.toSet()));
            neighbourhood.addAll(
                    graph.outgoingEdgesOf(v).stream().map(graph::getEdgeSource).collect(Collectors.toSet()));

            neighbourhood.remove(v);

            final double k = neighbourhood.size();
            double numberTriplets = 0;

            for (V p: neighbourhood)
                for (V q: neighbourhood)
                    if (graph.containsEdge(p, q))
                        numberTriplets++;

            if (k == 1)
                scores.put(v, 0.0);
            else
                scores.put(v, numberTriplets / (k * (k - 1)));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<V, Double> getScores() {
        if (scores == null) {
            compute();
        }

        return Collections.unmodifiableMap(scores);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double getVertexScore(V v) {
        if (!graph.containsVertex(v)) {
            throw new IllegalArgumentException("Cannot return score of unknown vertex");
        }

        if (scores == null) {
            compute();
        }

        return scores.get(v);
    }
}
