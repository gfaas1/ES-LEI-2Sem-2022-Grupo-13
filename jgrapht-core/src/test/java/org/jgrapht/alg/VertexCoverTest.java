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
import org.jgrapht.alg.vertexcover.ClarksonTwoApproxVCImpl;
import org.jgrapht.alg.vertexcover.EdgeBasedTwoApproxVCImpl;
import org.jgrapht.alg.vertexcover.GreedyVCImpl;
import org.jgrapht.alg.vertexcover.RecursiveExactVCImpl;
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

    protected static final int TEST_GRAPH_SIZE = 140;
    protected static final int TEST_REPEATS = 20;

    protected final Random rnd = new Random(0);
    //~ Methods ----------------------------------------------------------------



    // ------- Approximation algorithms ------

    /**
     * Test 2-approximation algorithms for the minimum vertex cover problem.
     */
    public void testFind2ApproximationCover()
    {
        MinimumVertexCoverAlgorithm<Integer, DefaultEdge> mvc1=new EdgeBasedTwoApproxVCImpl<>();
        MinimumVertexCoverAlgorithm<Integer, DefaultEdge> mvc2=new ClarksonTwoApproxVCImpl<>();
        for (int i = 0; i < TEST_REPEATS; i++) {
            Graph<Integer, DefaultEdge> g = createRandomPseudoGraph();

            VertexCover<Integer> optimalCover=new RecursiveExactVCImpl<Integer, DefaultEdge>().getVertexCover(Graphs.undirectedGraph(g));

            VertexCover<Integer> vertexCover=mvc1.getVertexCover(Graphs.undirectedGraph(g));
            assertTrue(isCover(g, vertexCover));
            assertEquals(vertexCover.getWeight(), 1.0*vertexCover.getVertices().size());
            assertTrue(vertexCover.getWeight() <= optimalCover.getWeight()*2); //Verify 2-approximation

            VertexCover<Integer> vertexCover2=mvc2.getVertexCover(Graphs.undirectedGraph(g));
            assertTrue(isCover(g, vertexCover2));
            assertEquals(vertexCover2.getWeight(), 1.0*vertexCover2.getVertices().size());
            assertTrue(vertexCover2.getWeight() <= optimalCover.getWeight()*2); //Verify 2-approximation
        }
    }

    // ------- Greedy algorithms ------

    /**
     * Test greedy algorithm for the minimum vertex cover problem.
     */
    public void testFindGreedyCover()
    {
        MinimumVertexCoverAlgorithm<Integer, DefaultEdge> mvc=new GreedyVCImpl<>();
        for (int i = 0; i < TEST_REPEATS; i++) {
            Graph<Integer, DefaultEdge> g = createRandomPseudoGraph();
            VertexCover<Integer> vertexCover=mvc.getVertexCover(Graphs.undirectedGraph(g));
            assertTrue(isCover(g, vertexCover));
            assertEquals(vertexCover.getWeight(), 1.0*vertexCover.getVertices().size());
        }
    }

    // ------- Exact algorithms ------

    /**
     * 4-cyle graph (optimal=2)
     */
    public void test4Cycle(){
        UndirectedGraph<Integer, DefaultEdge> g1=new SimpleGraph<>(DefaultEdge.class);
        Graphs.addAllVertices(g1, Arrays.asList(0, 1, 2, 3));
        g1.addEdge(0,1);
        g1.addEdge(1,2);
        g1.addEdge(2,3);
        g1.addEdge(3,0);
        MinimumVertexCoverAlgorithm<Integer, DefaultEdge> mvc1=new RecursiveExactVCImpl<>();
        VertexCover<Integer> vertexCover=mvc1.getVertexCover(g1);
        assertTrue(isCover(g1, vertexCover));
        assertEquals(vertexCover.getWeight(), 2.0);
    }

    /**
     * Wheel graph W_8 (Optimal=5)
     */
    public void testWheel(){
        UndirectedGraph<Integer, DefaultEdge> g1=new SimpleGraph<>(DefaultEdge.class);
        Graphs.addAllVertices(g1, Arrays.asList(0,1,2,3,4,5,6,7));
        g1.addEdge(1,2);
        g1.addEdge(2,3);
        g1.addEdge(3,4);
        g1.addEdge(4,5);
        g1.addEdge(5,6);
        g1.addEdge(6,7);
        g1.addEdge(7,1);
        g1.addEdge(0,1);
        g1.addEdge(0,2);
        g1.addEdge(0,3);
        g1.addEdge(0,4);
        g1.addEdge(0,5);
        g1.addEdge(0,6);
        g1.addEdge(0,7);
        MinimumVertexCoverAlgorithm<Integer, DefaultEdge> mvc1=new RecursiveExactVCImpl<>();
        VertexCover<Integer> vertexCover=mvc1.getVertexCover(g1);
        assertTrue(isCover(g1, vertexCover));
        assertEquals(vertexCover.getWeight(), 5.0);
    }

    /**
     * Cubic graph with 8 vertices (Optimal=7)
     */
    public void testCubic(){
        UndirectedGraph<Integer, DefaultEdge> g1=new SimpleGraph<>(DefaultEdge.class);
        Graphs.addAllVertices(g1, Arrays.asList(0,1,2,3,4,5,6,7,8,9,10,11));
        g1.addEdge(0,1);
        g1.addEdge(0,9);
        g1.addEdge(0,7);
        g1.addEdge(1, 2);
        g1.addEdge(1,5);
        g1.addEdge(2,3);
        g1.addEdge(2,4);
        g1.addEdge(3,4);
        g1.addEdge(3,5);
        g1.addEdge(4,11);
        g1.addEdge(5,6);
        g1.addEdge(6,7);
        g1.addEdge(6,8);
        g1.addEdge(7,8);
        g1.addEdge(8,10);
        g1.addEdge(9,10);
        g1.addEdge(9,11);
        g1.addEdge(10,11);
        MinimumVertexCoverAlgorithm<Integer, DefaultEdge> mvc1=new RecursiveExactVCImpl<>();
        VertexCover<Integer> vertexCover=mvc1.getVertexCover(g1);
        assertTrue(isCover(g1, vertexCover));
        assertEquals(vertexCover.getWeight(), 7.0);
    }

    /**
     * Graph with 6 vertices in the shape >-< (Optimal=2)
     */
    public void testWhisker(){
        UndirectedGraph<Integer, DefaultEdge> g1=new SimpleGraph<>(DefaultEdge.class);
        Graphs.addAllVertices(g1, Arrays.asList(0,1,2,3,4,5));
        g1.addEdge(0,2);
        g1.addEdge(1,2);
        g1.addEdge(2,3);
        g1.addEdge(3,4);
        g1.addEdge(3,5);
        MinimumVertexCoverAlgorithm<Integer, DefaultEdge> mvc1=new RecursiveExactVCImpl<>();
        VertexCover<Integer> vertexCover=mvc1.getVertexCover(g1);
        assertTrue(isCover(g1, vertexCover));
        assertEquals(vertexCover.getWeight(), 2.0);
    }

    // ------- Helper methods ------

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
     * Create a random PSEUDO graph of TEST_GRAPH_SIZE nodes.
     *
     * @return
     */
    protected Graph<Integer, DefaultEdge> createRandomPseudoGraph()
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
