/*
 * (C) Copyright 2017-2017, by Dimitrios Michail and Contributors.
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
package org.jgrapht.alg.scoring;

import static org.junit.Assert.assertEquals;

import org.jgrapht.alg.interfaces.VertexScoringAlgorithm;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.graph.DirectedWeightedPseudograph;
import org.jgrapht.graph.Pseudograph;
import org.junit.Test;

/**
 * Unit tests for closeness centrality.
 * 
 * @author Dimitrios Michail
 */
public class ClosenessCentralityTest
{

    @Test
    public void testOutgoing()
    {
        DirectedPseudograph<String, DefaultEdge> g = new DirectedPseudograph<>(DefaultEdge.class);

        g.addVertex("1");
        g.addVertex("2");
        g.addVertex("3");
        g.addVertex("4");
        g.addVertex("5");
        g.addEdge("1", "2");
        g.addEdge("1", "3");
        g.addEdge("2", "3");
        g.addEdge("3", "4");
        g.addEdge("4", "1");
        g.addEdge("4", "5");
        g.addEdge("5", "3");

        VertexScoringAlgorithm<String, Double> pr = new ClosenessCentrality<>(g, false, true);

        assertEquals(4d / 7, pr.getVertexScore("1"), 1e-9);
        assertEquals(4d / 9, pr.getVertexScore("2"), 1e-9);
        assertEquals(4d / 8, pr.getVertexScore("3"), 1e-9);
        assertEquals(4d / 6, pr.getVertexScore("4"), 1e-9);
        assertEquals(4d / 10, pr.getVertexScore("5"), 1e-9);
    }

    @Test
    public void testIncoming()
    {
        DirectedPseudograph<String, DefaultEdge> g = new DirectedPseudograph<>(DefaultEdge.class);

        g.addVertex("1");
        g.addVertex("2");
        g.addVertex("3");
        g.addVertex("4");
        g.addVertex("5");
        g.addEdge("1", "2");
        g.addEdge("1", "3");
        g.addEdge("2", "3");
        g.addEdge("3", "4");
        g.addEdge("4", "1");
        g.addEdge("4", "5");
        g.addEdge("5", "3");

        VertexScoringAlgorithm<String, Double> pr = new ClosenessCentrality<>(g, true, true);

        assertEquals(4d / 9, pr.getVertexScore("1"), 1e-9);
        assertEquals(4d / 10, pr.getVertexScore("2"), 1e-9);
        assertEquals(4d / 5, pr.getVertexScore("3"), 1e-9);
        assertEquals(4d / 7, pr.getVertexScore("4"), 1e-9);
        assertEquals(4d / 9, pr.getVertexScore("5"), 1e-9);
    }
    
    @Test
    public void testIncomingNoNormalization()
    {
        DirectedPseudograph<String, DefaultEdge> g = new DirectedPseudograph<>(DefaultEdge.class);

        g.addVertex("1");
        g.addVertex("2");
        g.addVertex("3");
        g.addVertex("4");
        g.addVertex("5");
        g.addEdge("1", "2");
        g.addEdge("1", "3");
        g.addEdge("2", "3");
        g.addEdge("3", "4");
        g.addEdge("4", "1");
        g.addEdge("4", "5");
        g.addEdge("5", "3");

        VertexScoringAlgorithm<String, Double> pr = new ClosenessCentrality<>(g, true, false);

        assertEquals(1d / 9, pr.getVertexScore("1"), 1e-9);
        assertEquals(1d / 10, pr.getVertexScore("2"), 1e-9);
        assertEquals(1d / 5, pr.getVertexScore("3"), 1e-9);
        assertEquals(1d / 7, pr.getVertexScore("4"), 1e-9);
        assertEquals(1d / 9, pr.getVertexScore("5"), 1e-9);
    }

    @Test
    public void testUndirected()
    {
        Pseudograph<String, DefaultEdge> g = new Pseudograph<>(DefaultEdge.class);

        g.addVertex("1");
        g.addVertex("2");
        g.addVertex("3");
        g.addVertex("4");
        g.addVertex("5");
        g.addEdge("1", "2");
        g.addEdge("1", "3");
        g.addEdge("2", "3");
        g.addEdge("3", "4");
        g.addEdge("4", "1");
        g.addEdge("4", "5");
        g.addEdge("5", "3");

        VertexScoringAlgorithm<String, Double> pr1 = new ClosenessCentrality<>(g, true, true);
        VertexScoringAlgorithm<String, Double> pr2 = new ClosenessCentrality<>(g, false, true);

        assertEquals(4d / 5, pr1.getVertexScore("1"), 1e-9);
        assertEquals(4d / 5, pr2.getVertexScore("1"), 1e-9);
        assertEquals(4d / 6, pr1.getVertexScore("2"), 1e-9);
        assertEquals(4d / 6, pr2.getVertexScore("2"), 1e-9);
        assertEquals(4d / 4, pr1.getVertexScore("3"), 1e-9);
        assertEquals(4d / 4, pr2.getVertexScore("3"), 1e-9);
        assertEquals(4d / 5, pr1.getVertexScore("4"), 1e-9);
        assertEquals(4d / 5, pr2.getVertexScore("4"), 1e-9);
        assertEquals(4d / 6, pr1.getVertexScore("5"), 1e-9);
        assertEquals(4d / 6, pr2.getVertexScore("5"), 1e-9);
    }

    @Test
    public void testNegativeWeights()
    {
        DirectedWeightedPseudograph<String, DefaultWeightedEdge> g =
            new DirectedWeightedPseudograph<>(DefaultWeightedEdge.class);

        g.addVertex("1");
        g.addVertex("2");
        g.addVertex("3");
        g.addVertex("4");
        g.addVertex("5");
        g.addEdge("1", "2");
        DefaultWeightedEdge e13 = g.addEdge("1", "3");
        g.addEdge("2", "3");
        g.addEdge("3", "4");
        g.addEdge("4", "1");
        g.addEdge("4", "5");
        g.addEdge("5", "3");

        g.setEdgeWeight(e13, -1d);

        VertexScoringAlgorithm<String, Double> pr = new ClosenessCentrality<>(g, false, true);

        assertEquals(4d / 1, pr.getVertexScore("1"), 1e-9);
        assertEquals(4d / 9, pr.getVertexScore("2"), 1e-9);
        assertEquals(4d / 8, pr.getVertexScore("3"), 1e-9);
        assertEquals(4d / 4, pr.getVertexScore("4"), 1e-9);
        assertEquals(4d / 10, pr.getVertexScore("5"), 1e-9);
    }
    
    @Test
    public void testDisconnectedOutgoing()
    {
        DirectedPseudograph<String, DefaultEdge> g = new DirectedPseudograph<>(DefaultEdge.class);

        g.addVertex("1");
        g.addVertex("2");
        g.addVertex("3");
        g.addVertex("4");
        g.addVertex("5");
        g.addVertex("6");
        g.addEdge("1", "2");
        g.addEdge("1", "3");
        g.addEdge("2", "3");
        g.addEdge("3", "4");
        g.addEdge("4", "1");
        g.addEdge("4", "5");
        g.addEdge("5", "3");

        VertexScoringAlgorithm<String, Double> pr = new ClosenessCentrality<>(g, false, true);

        assertEquals(5d / 13, pr.getVertexScore("1"), 1e-9);
        assertEquals(5d / 15, pr.getVertexScore("2"), 1e-9);
        assertEquals(5d / 14, pr.getVertexScore("3"), 1e-9);
        assertEquals(5d / 12, pr.getVertexScore("4"), 1e-9);
        assertEquals(5d / 16, pr.getVertexScore("5"), 1e-9);
        assertEquals(5d / 30, pr.getVertexScore("6"), 1e-9);
    }

}
