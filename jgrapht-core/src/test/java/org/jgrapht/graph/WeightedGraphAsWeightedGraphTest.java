/*
 * (C) Copyright 2018, by Lukas Harzenetter and Contributors.
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
package org.jgrapht.graph;

import java.util.HashMap;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WeightedGraphAsWeightedGraphTest
{

    private DefaultWeightedEdge loop;
    private double defaultLoopWeight = 6781234453486d;

    private DefaultWeightedEdge e12;
    private double defaultE12Weight = 6d;
    private double e12Weight = -123.54d;

    private DefaultWeightedEdge e23;
    private double defaultE23Weight = 456d;
    private double e23Weight = 89d;

    private DefaultWeightedEdge e24;
    private double defaultE24Weight = 0.587d;
    private double e24Weight = 3d;

    private String v1 = "v1";
    private String v2 = "v2";
    private String v3 = "v3";
    private String v4 = "v4";

    private Graph<String, DefaultWeightedEdge> weightedGraph;

    /**
     * Similar set up as created by {@link AsUndirectedGraphTest}.
     */
    @Before public void setUp()
    {
        Graph<String, DefaultWeightedEdge> undirectedWeightedGraph =
            new DefaultUndirectedWeightedGraph<>(DefaultWeightedEdge.class);

        undirectedWeightedGraph.addVertex(v1);
        undirectedWeightedGraph.addVertex(v2);
        undirectedWeightedGraph.addVertex(v3);
        undirectedWeightedGraph.addVertex(v4);
        e12 = Graphs.addEdge(undirectedWeightedGraph, v1, v2, defaultE12Weight);
        e23 = Graphs.addEdge(undirectedWeightedGraph, v2, v3, defaultE23Weight);
        e24 = Graphs.addEdge(undirectedWeightedGraph, v2, v4, defaultE24Weight);
        loop = Graphs.addEdge(undirectedWeightedGraph, v4, v4, defaultLoopWeight);

        Map<DefaultWeightedEdge, Double> graphWeights = new HashMap<>();
        graphWeights.put(e12, e12Weight);
        graphWeights.put(e23, e23Weight);
        graphWeights.put(e24, e24Weight);

        this.weightedGraph = new AsWeightedGraph<>(undirectedWeightedGraph, graphWeights);
    }

    @Test public void testSetEdgeWeight()
    {
        double newEdgeWeight = -999;
        this.weightedGraph.setEdgeWeight(e12, newEdgeWeight);

        assertEquals(newEdgeWeight, this.weightedGraph.getEdgeWeight(e12), 0);
    }

    @Test public void testGetEdgeWeight()
    {
        assertEquals(e23Weight, this.weightedGraph.getEdgeWeight(e23), 0);
    }

    @Test public void testGetDefaultEdgeWeight()
    {
        assertEquals(defaultLoopWeight, this.weightedGraph.getEdgeWeight(loop), 0);
    }

    @Test public void testSetEdgeWeightIfNullIsPassed()
    {
        try {
            this.weightedGraph.setEdgeWeight(null, 0);
            fail("Expected a NullPointerException");
        } catch (Exception e) {
            assertTrue(e instanceof NullPointerException);
        }
    }

    @Test public void testGetEdgeWeightOfNull()
    {
        try {
            this.weightedGraph.getEdgeWeight(null);
            fail("Expected a NullPointerException");
        } catch (Exception e) {
            assertTrue(e instanceof NullPointerException);
        }
    }
}
