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
 * PushRelabelMinimumSTCutTest.java
 * -----------------
 * (C) Copyright 2016, by Joris Kinable and Contributors.
 *
 * Original Author:  Joris Kinable
 * Contributor(s): -
 *
 * $Id$
 *
 * Changes
 * -------
 * Aug-2016 : Initial version (JK);
 */
package org.jgrapht.alg.flow;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.interfaces.MinimumSTCutAlgorithm;
import org.jgrapht.generate.RandomGraphGenerator;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.Random;
import java.util.Set;

/**
 * @author Joris Kinable
 */
public class PushRelabelMinimumSTCutTest extends MinimumSourceSinkCutTest{
    @Override
    MinimumSTCutAlgorithm<Integer, DefaultWeightedEdge> createSolver(Graph<Integer, DefaultWeightedEdge> network) {
        return new PushRelabelMFImpl<>(network);
    }

    public void testSmall()
    {
        SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> network =
            new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        network.addVertex(0);
        network.addVertex(1);
        network.addVertex(2);
        network.addVertex(3);
        network.addVertex(4);
        network.addVertex(5);
        network.setEdgeWeight(network.addEdge(2, 4), 62.0);
        network.setEdgeWeight(network.addEdge(3, 4), 52.0);
        network.setEdgeWeight(network.addEdge(1, 4), 3.0);
        network.setEdgeWeight(network.addEdge(0, 1), 58.0);
        network.setEdgeWeight(network.addEdge(2, 0), 67.0);
        network.setEdgeWeight(network.addEdge(1, 0), 5.0);
        network.setEdgeWeight(network.addEdge(4, 0), 11.0);
        network.setEdgeWeight(network.addEdge(4, 1), 46.0);
        network.setEdgeWeight(network.addEdge(1, 3), 62.0);
        network.setEdgeWeight(network.addEdge(4, 3), 27.0);

        MinimumSTCutAlgorithm<Integer, DefaultWeightedEdge> prSolver = this.createSolver(network);
        double cutWeight = prSolver.calculateMinCut(0, 5);
        assertEquals(0d, cutWeight);
    }
    
    public void testRandomDirectedGraphs(){
        for(int test=0; test<NR_RANDOM_TESTS; test++){
            DirectedGraph<Integer, DefaultWeightedEdge> network=generateDirectedGraph();
            int source=0;
            int sink=network.vertexSet().size()-1;

            MinimumSTCutAlgorithm<Integer, DefaultWeightedEdge> prSolver=this.createSolver(network);
            MinimumSTCutAlgorithm<Integer, DefaultWeightedEdge> ekSolver=new EdmondsKarpMFImpl<>(network);

            double expectedCutWeight=ekSolver.calculateMinCut(source, sink);

            double cutWeight=prSolver.calculateMinCut(source, sink);
            Set<Integer> sourcePartition=prSolver.getSourcePartition();
            Set<Integer> sinkPartition=prSolver.getSinkPartition();
            Set<DefaultWeightedEdge> cutEdges=prSolver.getCutEdges();

            this.verifyDirected(network, source, sink, expectedCutWeight, cutWeight, sourcePartition, sinkPartition, cutEdges);
        }
    }

    public void testRandomUndirectedGraphs(){
        for(int test=0; test<NR_RANDOM_TESTS; test++){
            UndirectedGraph<Integer, DefaultWeightedEdge> network=generateUndirectedGraph();
            int source=0;
            int sink=network.vertexSet().size()-1;

            MinimumSTCutAlgorithm<Integer, DefaultWeightedEdge> prSolver=this.createSolver(network);
            MinimumSTCutAlgorithm<Integer, DefaultWeightedEdge> ekSolver=new EdmondsKarpMFImpl<>(network);

            double expectedCutWeight=ekSolver.calculateMinCut(source, sink);

            double cutWeight=prSolver.calculateMinCut(source, sink);
            Set<Integer> sourcePartition=prSolver.getSourcePartition();
            Set<Integer> sinkPartition=prSolver.getSinkPartition();
            Set<DefaultWeightedEdge> cutEdges=prSolver.getCutEdges();

            this.verifyUndirected(network, source, sink, expectedCutWeight, cutWeight, sourcePartition, sinkPartition, cutEdges);
        }
    }
}
