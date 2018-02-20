package org.jgrapht.alg.flow;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.alg.interfaces.*;
import org.junit.Test;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Test class that compares running time of the maximum flow algorithms.
 */

public class MaxFlowAlgorithmsComparison extends MaximumFlowAlgorithmTest
{
    /**
     * Network.
     */
    private Graph<Integer, DefaultWeightedEdge> network;

    /**
     * Instance of Dinic algorithm.
     */
    private MaximumFlowAlgorithm<Integer, DefaultWeightedEdge> dinic;

    /**
     * Instance of Edmonds Karp algorithm.
     */
    private MaximumFlowAlgorithm<Integer, DefaultWeightedEdge> edmondsKarp;

    /**
     * Instance of Push Relabel algorithm.
     */
    private MaximumFlowAlgorithm<Integer, DefaultWeightedEdge> pushRelabel;

    /**
     * Total time of the first algorithms over all test runs.
     */
    private long totalTimeAlg1 = 0;

    /**
     * Duration of the first algorithm on a single run.
     */
    private long durationAlg1;

    /**
     * Total time of the second algorithm over all test runs.
     */
    private long totalTimeAlg2 = 0;

    /**
     * Duration of the second algorithm on a single run.
     */
    private long durationAlg2;

    /**
     * Counts how many times the first algorithm beat the second algorithm.
     */
    private long cntAlg1 = 0;

    /**
     * Counts how many times the second algorithm beat the first algorithm.
     */
    private long cntAlg2 = 0;

    /**
     * Stores the maximum advantage of the first algorithm.
     */
    private long maxWinAlg1 = 0;

    /**
     * Stores the maximum advantage of the second algorithm.
     */
    private long maxWinAlg2 = 0;

    private static final String MS = "microSeconds";
    
    private static final int NUM_OF_TESTS = 1000;

    @Test
    public void DinicVsEdmondsKarpUndirected()
    {
        for (int i = 0; i < NUM_OF_TESTS; i++) {
            network = generateUndirectedGraph();
            generateDinicEdmondsKarp();
            compareAlg1VsAlg2(dinic, edmondsKarp);
        }
        printOverviewAlg1VsAlg2("Dinic", "Edmonds Karp", false);
    }

    @Test
    public void DinicVsEdmondsKarpDirected()
    {
        for (int i = 0; i < NUM_OF_TESTS; i++) {
            network = generateDirectedGraph();
            generateDinicEdmondsKarp();
            compareAlg1VsAlg2(dinic, edmondsKarp);
        }
        printOverviewAlg1VsAlg2("Dinic", "Edmonds Karp", true);
    }

    @Test
    public void DinicVsPushRelabelUndirected()
    {
        for (int i = 0; i < NUM_OF_TESTS; i++) {
            network = generateUndirectedGraph();
            generateDinicPushRelabel();
            compareAlg1VsAlg2(dinic, pushRelabel);
        }
        printOverviewAlg1VsAlg2("Dinic", "Push Relabel", false);
    }

    @Test
    public void DinicVsPushRelabelDirected()
    {
        for (int i = 0; i < NUM_OF_TESTS; i++) {
            network = generateDirectedGraph();
            generateDinicPushRelabel();
            compareAlg1VsAlg2(dinic, pushRelabel);
        }
        printOverviewAlg1VsAlg2("Dinic", "Push Relabel", true);
    }

    @Test
    public void EdmondsKarpVsPushRelabelUndirected()
    {
        for (int i = 0; i < NUM_OF_TESTS; i++) {
            network = generateUndirectedGraph();
            generateEdmondsKarpPushRelabel();
            compareAlg1VsAlg2(edmondsKarp, pushRelabel);
        }
        printOverviewAlg1VsAlg2("Edmonds Karp", "Push Relabel", false);
    }

    @Test
    public void EdmondsKarpVsPushRelabelDirected()
    {
        for (int i = 0; i < NUM_OF_TESTS; i++) {
            network = generateDirectedGraph();
            generateEdmondsKarpPushRelabel();
            compareAlg1VsAlg2(edmondsKarp, pushRelabel);
        }
        printOverviewAlg1VsAlg2("Edmonds Karp", "Push Relabel", true);
    }

    /**
     * Creates an instance of Dinic algorithm and an instance of Edmonds Karp algorithm.
     */
    private void generateDinicEdmondsKarp()
    {
        dinic = new DinicMFImpl<>(network);
        edmondsKarp = new EdmondsKarpMFImpl<>(network);
    }

    /**
     * Creates an instance of Dinic algorithm and an instance of Push Relabel algorithm.
     */
    private void generateDinicPushRelabel()
    {
        dinic = new DinicMFImpl<>(network);
        pushRelabel = new PushRelabelMFImpl<>(network);
    }

    /**
     * Creates an instance of Edmonds Karp algorithm and an instance of Push Relabel algorithm.
     */
    private void generateEdmondsKarpPushRelabel()
    {
        edmondsKarp = new EdmondsKarpMFImpl<>(network);
        pushRelabel = new PushRelabelMFImpl<>(network);
    }

    /**
     * Calculates running time of both algorithms on the specific network.
     * Updates the total time of both algorithms and number of wins.
     * @param alg1 first algorithm.
     * @param alg2 second algorithm.
     */
    private void compareAlg1VsAlg2(
        MaximumFlowAlgorithm<Integer, DefaultWeightedEdge> alg1,
        MaximumFlowAlgorithm<Integer, DefaultWeightedEdge> alg2)
    {

        long startTimeAlg1 = System.nanoTime();

        double flowAlg1 = alg1.calculateMaximumFlow(0, network.vertexSet().size() - 1);
            durationAlg1 = System.nanoTime() - startTimeAlg1;
            totalTimeAlg1 += TimeUnit.NANOSECONDS.toMicros(durationAlg1);

        long startTimeAlg2 = System.nanoTime();

        double flowAlg2 = alg2.calculateMaximumFlow(0, network.vertexSet().size() - 1);
            durationAlg2 = System.nanoTime() - startTimeAlg2;
            totalTimeAlg2 += TimeUnit.NANOSECONDS.toMicros(durationAlg2);

            calcWinAndMaxWin();

            assertEquals(flowAlg1, flowAlg2, 1e-9); 
    }

    /**
     * Prints overview of comparison of two algorithms.
     * @param alg1 first algorithm.
     * @param alg2 second algorithm.
     * @param isDirected contains information whether graph is directed or not.
     */
    private void printOverviewAlg1VsAlg2(String alg1, String alg2, boolean isDirected)
    {
        long draw = 1000 - cntAlg1 - cntAlg2;

        System.out.println(alg1 + " VS " + alg2);
        System.out.println();

        if (isDirected) {
            System.out.println("DIRECTED GRAPHS");
        }
        else {
            System.out.println("UNDIRECTED GRAPHS");
        }
        System.out.println();

        System.out.println(alg1 + " outperformed " + alg2 + " " + cntAlg1 +" times.");
        System.out.println(alg2 + " outperformed " + alg1 + " " + cntAlg2 +" times.");
        System.out.println(draw + " times both algorithms showed equal time.");
        System.out.println();

        System.out.println(alg1 + " total time: " + totalTimeAlg1 + " " + MS + ".");
        System.out.println(alg2 + " total time: " + totalTimeAlg2 + " " + MS + ".");

        long advantage = Math.abs(totalTimeAlg1 - totalTimeAlg2);
        
        if (totalTimeAlg1 <= totalTimeAlg2) {
            System.out.println("Total " + alg1 + " advantage is: " + advantage + " " + MS + ".");
        }
        else {
            System.out.println("Total " + alg2 + " advantage is: " + advantage + " " + MS + ".");
        }

        System.out.println();
        System.out.println(alg1 + " maximum advantage is: " + maxWinAlg1 + " " + MS + ".");
        System.out.println(alg2 + " maximum advantage is: " + maxWinAlg2 + " " + MS + ".");

        System.out.println();
        System.out.println("________________________________________________________________________");
        System.out.println();
    }

    /**
     * Updates total running time and number of wins.
     */
    private void calcWinAndMaxWin()
    {
        if (durationAlg1 < durationAlg2) {
            cntAlg1++;
            maxWinAlg1 = Math.max(maxWinAlg1, 
                TimeUnit.NANOSECONDS.toMicros(durationAlg2) - TimeUnit.NANOSECONDS.toMicros(durationAlg1));
        }

        else if (durationAlg1 > durationAlg2) {
            cntAlg2++;
            maxWinAlg2 = Math.max(maxWinAlg2, 
                TimeUnit.NANOSECONDS.toMicros(durationAlg1) - TimeUnit.NANOSECONDS.toMicros(durationAlg2));
        }
    }

    @Override MaximumFlowAlgorithm<Integer, DefaultWeightedEdge> createSolver(
        Graph<Integer, DefaultWeightedEdge> network)
    {
        return new DinicMFImpl<>(network);
    }
}
