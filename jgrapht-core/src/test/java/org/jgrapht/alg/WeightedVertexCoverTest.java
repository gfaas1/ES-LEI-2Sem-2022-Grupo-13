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
import org.jgrapht.alg.interfaces.MinimumVertexCoverAlgorithm;
import org.jgrapht.alg.interfaces.MinimumWeightedVertexCoverAlgorithm;
import org.jgrapht.alg.vertexcover.ClarksonsTwoApproxWeightedVCImpl;
import org.jgrapht.alg.vertexcover.GreedyWeightedVCImpl;
import org.jgrapht.graph.DefaultEdge;

import java.util.HashMap;
import java.util.Map;

/**
 * Tests the weighted vertex cover algorithms
 *
 * @author Joris Kinable
 */
public class WeightedVertexCoverTest extends VertexCoverTest{

    /**
     * Test 2-approximation algorithm for the minimum vertex cover problem.
     * TODO: verify whether the objective indeed is smaller than 2 times the optimum solution.
     */
    public void testFind2ApproximationCover()
    {
        MinimumWeightedVertexCoverAlgorithm<Integer, DefaultEdge> mvc=new ClarksonsTwoApproxWeightedVCImpl<>();
        for (int i = 0; i < TEST_REPEATS; i++) {
            Graph<Integer, DefaultEdge> g = createRandomGraph();
            Map<Integer, Integer> vertexWeights=getRandomVertexWeights(g);
            MinimumVertexCoverAlgorithm.VertexCover<Integer> vertexCover=mvc.getVertexCover(Graphs.undirectedGraph(g), vertexWeights);
            assertTrue(isCover(g, vertexCover));
            assertEquals(vertexCover.getWeight(), vertexCover.getVertices().stream().mapToInt(v -> vertexWeights.get(v)).sum());
        }
    }

    /**
     * Test greedy algorithm for the minimum weighted vertex cover problem.
     */
    public void testFindGreedyCover()
    {
        MinimumWeightedVertexCoverAlgorithm<Integer, DefaultEdge> mvc=new GreedyWeightedVCImpl<>();
        for (int i = 0; i < TEST_REPEATS; i++) {
            Graph<Integer, DefaultEdge> g = createRandomGraph();
            Map<Integer, Integer> vertexWeights=getRandomVertexWeights(g);
            MinimumVertexCoverAlgorithm.VertexCover<Integer> vertexCover=mvc.getVertexCover(Graphs.undirectedGraph(g), vertexWeights);
            assertTrue(isCover(g, vertexCover));
            assertEquals(vertexCover.getWeight(), vertexCover.getVertices().stream().mapToInt(v -> vertexWeights.get(v)).sum());
        }
    }

    protected Map<Integer, Integer> getRandomVertexWeights(Graph<Integer, DefaultEdge> graph){
        Map<Integer, Integer> vertexWeights=new HashMap<>();
        for(Integer v : graph.vertexSet())
            vertexWeights.put(v, rnd.nextInt(25));
        return vertexWeights;
    }
}

