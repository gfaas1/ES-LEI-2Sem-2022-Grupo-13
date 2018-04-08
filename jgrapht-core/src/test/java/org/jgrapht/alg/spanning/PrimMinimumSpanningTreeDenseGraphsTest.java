/*
 * (C) Copyright 2018-2018, by Alexandru Valeanu and Contributors.
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
package org.jgrapht.alg.spanning;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class PrimMinimumSpanningTreeDenseGraphsTest extends MinimumSpanningTreeTest {

    @Override
    SpanningTreeAlgorithm<DefaultWeightedEdge> createSolver(Graph<String, DefaultWeightedEdge> network) {
        return new PrimMinimumSpanningTreeDenseGraphs<>(network);
    }

    /*
        The above code can be used test the time diff between PrimMinimumSpanningTree and
        PrimMinimumSpanningTreeDenseGraphs
     */

    private static Graph<Integer, DefaultWeightedEdge> bigGraph;

    @BeforeClass
    public static void generateBigGraph(){
        Random random = new Random(991199);
        bigGraph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        final int N = 2_000;

        for (int i = 0; i < N; i++) {
            bigGraph.addVertex(i);
        }

        int P = 70;

        for (int i = 0; i < N; i++) {
            for (int j = i + 1; j < N; j++) {
                if (1 + random.nextInt(100) <= P){
                    bigGraph.setEdgeWeight(bigGraph.addEdge(i, j), 1 + random.nextInt(100));
                }
            }
        }
    }

    @Test
    public void testDenseGraphNormal(){
        assertEquals(new PrimMinimumSpanningTree<>(bigGraph).getSpanningTree().getWeight(), 1999, 1e-9);
    }

    @Test
    public void testDenseGraphSpecial(){
        assertEquals(new PrimMinimumSpanningTreeDenseGraphs<>(bigGraph).getSpanningTree().getWeight(), 1999, 1e-9);
    }
}