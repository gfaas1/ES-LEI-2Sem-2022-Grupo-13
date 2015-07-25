package org.jgrapht.alg.flow;

import junit.framework.TestCase;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.Map;

public abstract class MaximumFlowAlgorithmTestBase extends TestCase {


    abstract MaximumFlowAlgorithm<Integer, DefaultWeightedEdge> createSolver(DirectedGraph<Integer, DefaultWeightedEdge> network);

    public void testLogic()
    {
        runTest(
            new int[] {},
            new int[] {},
            new double[] {},
            new int[] { 1 },
            new int[] { 4057218 },
            new double[] { 0.0 });
        runTest(
            new int[] { 3, 1, 4, 3, 2, 8, 2, 5, 7 },
            new int[] { 1, 4, 8, 2, 8, 6, 5, 7, 6 },
            new double[] { 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            new int[] { 3 },
            new int[] { 6 },
            new double[] { 2 });
        runTest(
            new int[] { 5, 5, 5, 1, 1, 4, 2, 7, 8, 3 },
            new int[] { 1, 4, 2, 7, 8, 3, 8, 6, 6, 6 },
            new double[] { 7, 8, 573146, 31337, 1, 1, 1, 1, 2391717, 170239 },
            new int[] { 5 },
            new int[] { 6 },
            new double[] { 4.0 });
        runTest(
            new int[] { 1, 1, 2, 2, 3 },
            new int[] { 2, 3, 3, 4, 4 },
            new double[] {
                1000000000.0, 1000000000.0, 1.0, 1000000000.0, 1000000000.0
            },
            new int[] { 1 },
            new int[] { 4 },
            new double[] { 2000000000.0 });
    }

    private void runTest(
        int [] tails,
        int [] heads,
        double [] capacities,
        int [] sources,
        int [] sinks,
        double [] expectedResults)
    {
        assertTrue(tails.length == heads.length);
        assertTrue(tails.length == capacities.length);
        DirectedWeightedMultigraph<Integer, DefaultWeightedEdge> network =
            new DirectedWeightedMultigraph<Integer, DefaultWeightedEdge>(
                DefaultWeightedEdge.class);
        int m = tails.length;
        for (int i = 0; i < m; i++) {
            network.addVertex(tails[i]);
            network.addVertex(heads[i]);
            DefaultWeightedEdge e = network.addEdge(tails[i], heads[i]);
            network.setEdgeWeight(e, capacities[i]);
        }
        assertTrue(sources.length == sinks.length);
        int q = sources.length;
        for (int i = 0; i < q; i++) {
            network.addVertex(sources[i]);
            network.addVertex(sinks[i]);
        }
        MaximumFlowAlgorithm<Integer, DefaultWeightedEdge> solver = createSolver(network);

        for (int i = 0; i < q; i++) {
            MaximumFlowAlgorithm.MaximumFlow<Integer, DefaultWeightedEdge> maxFlow = solver.buildMaximumFlow(sources[i], sinks[i]);

            assertEquals(
                expectedResults[i],
                maxFlow.getValue(),
                EdmondsKarpMaximumFlow.DEFAULT_EPSILON);

            Double flowValue = maxFlow.getValue();
            Map<DefaultWeightedEdge, Double> flow = maxFlow.getFlow();

            for (DefaultWeightedEdge e : network.edgeSet()) {
                assertTrue(flow.containsKey(e));
            }

            for (DefaultWeightedEdge e : flow.keySet()) {
                assertTrue(network.containsEdge(e));
                assertTrue(
                    flow.get(e) >= -EdmondsKarpMaximumFlow.DEFAULT_EPSILON);
                assertTrue(
                    flow.get(e)
                        <= (network.getEdgeWeight(e)
                        + EdmondsKarpMaximumFlow.DEFAULT_EPSILON));
            }

            for (Integer v : network.vertexSet()) {
                double balance = 0.0;
                for (DefaultWeightedEdge e : network.outgoingEdgesOf(v)) {
                    balance -= flow.get(e);
                }
                for (DefaultWeightedEdge e : network.incomingEdgesOf(v)) {
                    balance += flow.get(e);
                }
                if (v.equals(sources[i])) {
                    assertEquals(
                        -flowValue,
                        balance,
                        MaximumFlowAlgorithmBase.DEFAULT_EPSILON);
                } else if (v.equals(sinks[i])) {
                    assertEquals(
                        flowValue,
                        balance,
                        MaximumFlowAlgorithmBase.DEFAULT_EPSILON);
                } else {
                    assertEquals(
                        0.0,
                        balance,
                        MaximumFlowAlgorithmBase.DEFAULT_EPSILON);
                }
            }
        }
    }

}
