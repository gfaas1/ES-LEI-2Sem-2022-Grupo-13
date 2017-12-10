package org.jgrapht.alg.tour;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.TSPAlgorithm;
import org.jgrapht.graph.GraphWalk;

import java.util.*;

/**
 * TODO: update docs
 *
 * A dp algorithm for the TSP problem.
 *
 * <p>
 * The travelling salesman problem (TSP) asks the following question: "Given a list of cities and
 * the distances between each pair of cities, what is the shortest possible route that visits each
 * city exactly once and returns to the origin city?". In the metric TSP, the intercity distances
 * satisfy the triangle inequality.
 *
 *
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/Travelling_salesman_problem">wikipedia</a> for more
 * details.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Alexandru Valeanu
 */
public class NaiveTSP<V, E>
        implements TSPAlgorithm<V, E> {

    /**
     * Construct a new instance
     */
    public NaiveTSP() {
    }

    private void printState(int state, int n){
        System.out.printf("State %d: ", state);
        for (int i = 0; i < n; i++) {
            if (((state >> i) & 1) > 0){
                System.out.printf("%d ", i);
            }
        }
        System.out.println();
    }

    private double memo(int previousNode, int state, double[][] C, double[][] W){
        // have we seen this state before?
        if (C[previousNode][state] != Double.MIN_VALUE)
            return C[previousNode][state];

        // no cycle has been found yet
        double totalCost = Double.MAX_VALUE;

        // check if all nodes have been visited (i.e. state + 1 == 2^n)
        if (state == (1 << W.length) - 1){
            // check if there is a return edge we can use
            if (W[previousNode][0] != Double.MAX_VALUE)
                totalCost = W[previousNode][0];
            else
                totalCost = Double.MAX_VALUE;
        }
        else{
            // try to find the 'best' next (i.e. unvisited and adjacent to previousNode) node in the tour
            for (int i = 0; i < W.length; i++) {
                if (((state >> i) & 1) == 0 && W[previousNode][i] != Double.MAX_VALUE){
                    totalCost = Math.min(totalCost, W[previousNode][i] + memo(i, state ^ (1 << i), C, W));
                }
            }
        }

        return C[previousNode][state] = totalCost;
    }

    /**
     * Computes a Hamiltonian tour.
     *
     * @param graph the input graph
     * @return a tour
     * @throws IllegalArgumentException if the graph contains no vertices
     */
    @Override
    public GraphPath<V, E> getTour(Graph<V, E> graph) {
        final int n = graph.vertexSet().size(); // number of nodes

        if (n == 0) {
            throw new IllegalArgumentException("Graph contains no vertices");
        }

        double[][] W = new double[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(W[i], Double.MAX_VALUE);
        }

        /*
         * Normalize the graph
         */
        Map<V, Integer> mapToInt = new HashMap<>();
        List<V> mapToV = new ArrayList<>();
        int newNode = 0;
        for (E e: graph.edgeSet()){
            V source = graph.getEdgeSource(e);
            V target = graph.getEdgeTarget(e);

            if (!mapToInt.containsKey(source)){
                mapToInt.put(source, newNode);
                mapToV.add(source);
                newNode++;
            }

            if (!mapToInt.containsKey(target)){
                mapToInt.put(target, newNode);
                mapToV.add(target);
                newNode++;
            }

            int u = mapToInt.get(source);
            int v = mapToInt.get(target);

            W[u][v] = Math.min(W[u][v], graph.getEdgeWeight(e));

            if (graph.getType().isUndirected())
                W[v][u] = Math.min(W[v][u], graph.getEdgeWeight(e));
        }

        double[][] C = new double[n][1 << n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(C[i], Double.MIN_VALUE);
        }

        double tourWeight = memo(0, 1, C, W);

        // check if there is no tour
        if (tourWeight == Double.MAX_VALUE)
            return null;

        List<V> vertexList = new ArrayList<>(n);
        List<E> edgeList = new ArrayList<>(n);

        int lastNode = 0;
        int lastState = 1 << lastNode;

        vertexList.add(mapToV.get(lastNode));

        for (int step = 1; step < n; step++) {
            int nextNode = -1;
            for (int node = 0; node < n; node++) {
                if (C[node][lastState ^ (1 << node)] + W[lastNode][node] == C[lastNode][lastState]){
                    nextNode = node;
                    break;
                }
            }

            assert nextNode != -1;
            vertexList.add(mapToV.get(nextNode));
            edgeList.add(graph.getEdge(mapToV.get(lastNode), mapToV.get(nextNode)));
            lastState ^= 1 << nextNode;
            lastNode = nextNode;
        }

        vertexList.add(mapToV.get(0));
        edgeList.add(graph.getEdge(mapToV.get(lastNode), mapToV.get(0)));

        return new GraphWalk<>(graph, mapToV.get(0), mapToV.get(0), vertexList, edgeList, tourWeight);
    }
}
