/*
 * (C) Copyright 2015-2018, by Alexey Kudinkin and Contributors.
 *
 * JGraphT : a free Java graph-theory library
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
package org.jgrapht.alg.flow;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PushRelabelMFImplTest
    extends MaximumFlowAlgorithmTest
{
    public static void testPushRelabelMFImpl(int graphSize){
        int outWeight = 3;
        int inWeight = 4;

        DirectedWeightedMultigraph<Integer, DefaultWeightedEdge> g = new DirectedWeightedMultigraph<>(DefaultWeightedEdge.class);
        for (int i = 0; i<graphSize; i++){
            g.addVertex(i);
        }

        for(int i = 1; i < graphSize; i++) {
            if(i%2 == 0){
                DefaultWeightedEdge eOut = g.addEdge(0, i);
                g.setEdgeWeight(eOut, outWeight);

                DefaultWeightedEdge eInt = g.addEdge(i, 0);
                g.setEdgeWeight(eInt, inWeight);
            }else{
                DefaultWeightedEdge e = g.addEdge(0,i);
                g.setEdgeWeight(e, 0);
            }
        }

        //System.out.println("Graph constructed... Size = " + graphSize);

        long startTime = System.nanoTime();
        PushRelabelMFImpl<Integer, DefaultWeightedEdge> algorithm = new PushRelabelMFImpl<>(g);
        algorithm.getMaximumFlow(2,1);

        double time = (System.nanoTime() - startTime)/1e9;

        //System.out.println("Finished. Time used: " + time + "s.");
    }

    @Test
    public void testSize(){
        /*
            See https://github.com/jgrapht/jgrapht/issues/461
         */

        testPushRelabelMFImpl(1000);
    }

    @Override
    MaximumFlowAlgorithm<Integer, DefaultWeightedEdge> createSolver(
        Graph<Integer, DefaultWeightedEdge> network)
    {
        return new PushRelabelMFImpl<>(network);
    }

    @Test
    public void testPushRelabelWithNonIdenticalNode() {
        SimpleDirectedGraph<String,DefaultEdge> g1 = new SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge.class) ;

        g1.addVertex("v0");
        g1.addVertex("v1");
        g1.addVertex("v2");
        g1.addVertex("v3");
        g1.addVertex("v4");
        g1.addEdge("v0","v2");
        g1.addEdge("v3","v4");
        g1.addEdge("v1","v0");
        g1.addEdge("v0","v4");
        g1.addEdge("v0","v1");
        g1.addEdge("v2","v1");

        MaximumFlowAlgorithm<String, DefaultEdge> mf1 = new PushRelabelMFImpl<>(g1);
        String sourceFlow = "v" + new String("v3").substring(1) ;
        String sinkFlow = "v0" ;
        double flow = mf1.calculateMaximumFlow(sourceFlow,sinkFlow);
        assertEquals(0.0, flow,0);
    }
}
