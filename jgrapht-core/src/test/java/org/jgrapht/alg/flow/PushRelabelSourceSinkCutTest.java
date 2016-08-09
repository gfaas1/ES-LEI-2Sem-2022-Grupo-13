package org.jgrapht.alg.flow;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.MinimumSourceSinkCutAlgorithm;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Created by jkinable on 8/8/16.
 */
public class PushRelabelSourceSinkCutTest extends MinimumSourceSinkCutTest{
    @Override
    MinimumSourceSinkCutAlgorithm<Integer, DefaultWeightedEdge> createSolver(Graph<Integer, DefaultWeightedEdge> network) {
        return new PushRelabelMFImpl<>(network);
    }
}
