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
/* ---------------------
 * VertexCoverTest.java
 * ---------------------
 * (C) Copyright 2003-2008, by Linda Buisman and Contributors.
 *
 * Original Author:  Linda Buisman
 * Contributor(s):   Barak Naveh
 *                   Joris Kinable
 *
 * $Id$
 *
 * Changes
 * -------
 * 06-Nov-2003 : Initial revision (LB);
 * 10-Nov-2003 : Adapted to VertexCovers (BN);
 * 29-Jul-2016 : Remodelled tests around new vertex cover interface (JK);
 *
 */
package org.jgrapht.alg;

import java.util.*;

import junit.framework.*;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.MinimumVertexCoverAlgorithm;
import org.jgrapht.alg.interfaces.MinimumVertexCoverAlgorithm.VertexCover;
import org.jgrapht.alg.vertexcover.ClarksonsTwoApproxWeightedVCImpl;
import org.jgrapht.alg.vertexcover.EdgeBasedTwoApproxVCImpl;
import org.jgrapht.alg.vertexcover.GreedyWeightedVCImpl;
import org.jgrapht.graph.*;


/**
 * Tests the vertex cover algorithms.
 *
 * @author Linda Buisman
 * @since Nov 6, 2003
 */
public class VertexCoverTest
    extends TestCase
{
    //~ Static fields/initializers ---------------------------------------------

    protected static final int TEST_GRAPH_SIZE = 200;
    protected static final int TEST_REPEATS = 20;

    protected final Random rnd = new Random(0);
    //~ Methods ----------------------------------------------------------------

    /**
     * Test 2-approximation algorithms for the minimum vertex cover problem.
     * TODO: verify whether the objective indeed is smaller than 2 times the optimum solution.
     */
    public void testFind2ApproximationCover()
    {
        MinimumVertexCoverAlgorithm<Integer, DefaultEdge> mvc1=new EdgeBasedTwoApproxVCImpl<>();
        MinimumVertexCoverAlgorithm<Integer, DefaultEdge> mvc2=new ClarksonsTwoApproxWeightedVCImpl<>();
        for (int i = 0; i < TEST_REPEATS; i++) {
            Graph<Integer, DefaultEdge> g = createRandomGraph();

            VertexCover<Integer> vertexCover=mvc1.getVertexCover(Graphs.undirectedGraph(g));
            assertTrue(isCover(g, vertexCover));
            assertEquals(vertexCover.getWeight(), vertexCover.getVertices().size());

            VertexCover<Integer> vertexCover2=mvc2.getVertexCover(Graphs.undirectedGraph(g));
            assertTrue(isCover(g, vertexCover2));
            assertEquals(vertexCover2.getWeight(), vertexCover2.getVertices().size());
        }
    }

    /**
     * Test greedy algorithm for the minimum vertex cover problem.
     */
    public void testFindGreedyCover()
    {
        MinimumVertexCoverAlgorithm<Integer, DefaultEdge> mvc=new GreedyWeightedVCImpl<>();
        for (int i = 0; i < TEST_REPEATS; i++) {
            Graph<Integer, DefaultEdge> g = createRandomGraph();
            VertexCover<Integer> vertexCover=mvc.getVertexCover(Graphs.undirectedGraph(g));
            assertTrue(isCover(g, vertexCover));
            assertEquals(vertexCover.getWeight(), vertexCover.getVertices().size());
        }
    }

    /**
     * Checks if the specified vertex set covers every edge of the graph. Uses
     * the definition of Vertex Cover - removes every edge that is incident on a
     * vertex in vertexSet. If no edges are left, vertexSet is a vertex cover
     * for the specified graph.
     *
     * @param vertexCover the vertex cover to be tested for covering the graph.
     * @param g the graph to be covered.
     *
     * @return returns true if the provided vertex cover is a valid cover in the given graph
     */
    protected boolean isCover(
        Graph<Integer, DefaultEdge> g,
        VertexCover<Integer> vertexCover)
    {
        Set<DefaultEdge> uncoveredEdges = new HashSet<>(g.edgeSet());
        for (Integer v : vertexCover.getVertices())
            uncoveredEdges.removeAll(g.edgesOf(v));

        return uncoveredEdges.isEmpty();
    }

    /**
     * Create a random graph of TEST_GRAPH_SIZE nodes.
     *
     * @return
     */
    protected Graph<Integer, DefaultEdge> createRandomGraph()
    {
        // TODO: move random graph generator to be under GraphGenerator
        // framework.
        Pseudograph<Integer, DefaultEdge> g =
                new Pseudograph<>(DefaultEdge.class);

        for (int i = 0; i < TEST_GRAPH_SIZE; i++) {
            g.addVertex(i);
        }

        List<Integer> vertices = new ArrayList<>(g.vertexSet());
        // join every vertex with a random number of other vertices
        for (int source = 0; source < TEST_GRAPH_SIZE; source++) {
            int numEdgesToCreate = rnd.nextInt(TEST_GRAPH_SIZE / 2) + 1;

            for (int j = 0; j < numEdgesToCreate; j++) {
                // find a random vertex to join to
                int target = (int) Math.floor(Math.random() * TEST_GRAPH_SIZE);
                g.addEdge(vertices.get(source), vertices.get(target));
            }
        }

        return g;
    }
}

// End VertexCoverTest.java
