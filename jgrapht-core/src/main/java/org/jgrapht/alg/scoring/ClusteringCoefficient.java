package org.jgrapht.alg.scoring;

import org.jgrapht.Graph;
import org.jgrapht.GraphMetrics;
import org.jgrapht.alg.interfaces.VertexScoringAlgorithm;

import java.util.*;
import java.util.stream.Collectors;

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
     * GlobalClusteringCoefficient
     */
    private boolean computed = false;
    private double globalClusteringCoefficient;

    private boolean computedAverage = false;
    private double averageClusteringCoefficient;

    /**
     * Constructor
     *
     * @param graph the input graph
     */
    public ClusteringCoefficient(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph);
    }

    public double getGlobalClusteringCoefficient() {
        if (!computed){
            computeGlobalClusteringCoefficient();
        }

        return globalClusteringCoefficient;
    }

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
