/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
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
/* -----------------
 * WeightedVertexCoverTest.java
 * -----------------
 * (C) Copyright 2016, by Joris Kinable and Contributors.
 *
 * Original Author:  Joris Kinable
 * Contributor(s):   -
 *
 * $Id$
 *
 * Changes
 * -------
 * 28-Jul-2016 : Initial revision (JK);
 *
 */
package org.jgrapht.alg;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.MinimumVertexCoverAlgorithm.VertexCover;
import org.jgrapht.alg.interfaces.MinimumWeightedVertexCoverAlgorithm;
import org.jgrapht.alg.vertexcover.BarYehudaEvenTwoApproxVCImpl;
import org.jgrapht.alg.vertexcover.ClarksonTwoApproxVCImpl;
import org.jgrapht.alg.vertexcover.GreedyVCImpl;
import org.jgrapht.graph.DefaultEdge;

import java.util.HashMap;
import java.util.Map;

/**
 * Tests the weighted vertex cover algorithms
 *
 * @author Joris Kinable
 */
public class WeightedVertexCoverTest extends VertexCoverTest{


    // ------- Approximation algorithms ------

    /**
     * Test 2-approximation algorithm for the minimum vertex cover problem.
     * TODO: verify whether the objective indeed is smaller than 2 times the optimum solution.
     */
    public void testFind2ApproximationCover()
    {
        MinimumWeightedVertexCoverAlgorithm<Integer, DefaultEdge> mvc1=new ClarksonTwoApproxVCImpl<>();
        MinimumWeightedVertexCoverAlgorithm<Integer, DefaultEdge> mvc2=new BarYehudaEvenTwoApproxVCImpl<>();
        for (int i = 0; i < TEST_REPEATS; i++) {
            Graph<Integer, DefaultEdge> g = createRandomPseudoGraph();
            Map<Integer, Double> vertexWeights=getRandomVertexWeights(g);

            VertexCover<Integer> vertexCover=mvc1.getVertexCover(Graphs.undirectedGraph(g), vertexWeights);
            assertTrue(isCover(g, vertexCover));
            assertEquals(vertexCover.getWeight(), vertexCover.getVertices().stream().mapToDouble(vertexWeights::get).sum());

            VertexCover<Integer> vertexCover2=mvc2.getVertexCover(Graphs.undirectedGraph(g), vertexWeights);
            assertTrue(isCover(g, vertexCover2));
            assertEquals(vertexCover2.getWeight(), vertexCover2.getVertices().stream().mapToDouble(vertexWeights::get).sum());
        }
    }

    // ------- Greedy algorithms ------

    /**
     * Test greedy algorithm for the minimum weighted vertex cover problem.
     */
    public void testFindGreedyCover()
    {
        MinimumWeightedVertexCoverAlgorithm<Integer, DefaultEdge> mvc=new GreedyVCImpl<>();
        for (int i = 0; i < TEST_REPEATS; i++) {
            Graph<Integer, DefaultEdge> g = createRandomPseudoGraph();
            Map<Integer, Double> vertexWeights=getRandomVertexWeights(g);
            VertexCover<Integer> vertexCover=mvc.getVertexCover(Graphs.undirectedGraph(g), vertexWeights);
            assertTrue(isCover(g, vertexCover));
            assertEquals(vertexCover.getWeight(), vertexCover.getVertices().stream().mapToDouble(vertexWeights::get).sum());
        }
    }

    // ------- Exact algorithms ------


    // ------- Helper methods ------

    protected Map<Integer, Double> getRandomVertexWeights(Graph<Integer, DefaultEdge> graph){
        Map<Integer, Double> vertexWeights=new HashMap<>();
        for(Integer v : graph.vertexSet())
            vertexWeights.put(v, 1.0* rnd.nextInt(25));
        return vertexWeights;
    }
}

