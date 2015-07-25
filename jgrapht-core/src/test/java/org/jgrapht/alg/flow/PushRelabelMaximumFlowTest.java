package org.jgrapht.alg.flow;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
import org.jgrapht.graph.DefaultWeightedEdge;

public class PushRelabelMaximumFlowTest extends MaximumFlowAlgorithmTestBase {

    @Override
    MaximumFlowAlgorithm<Integer, DefaultWeightedEdge> createSolver(DirectedGraph<Integer, DefaultWeightedEdge> network) {
        return new PushRelabelMaximumFlow<Integer, DefaultWeightedEdge>(network);
    }

}
