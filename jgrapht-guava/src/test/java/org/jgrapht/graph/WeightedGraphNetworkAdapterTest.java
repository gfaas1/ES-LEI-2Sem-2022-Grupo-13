/*
 * (C) Copyright 2017-2018, by Dimitrios Michail and Contributors.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.jgrapht.Graph;
import org.junit.*;

import com.google.common.graph.ImmutableNetwork;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;

/**
 * Tests for different edge types on weighted graphs.
 * 
 * @author Dimitrios Michail
 */
public class WeightedGraphNetworkAdapterTest
{
    @Test
    public void testDefaultWeightedEdge()
    {
        Graph<Integer,
            DefaultWeightedEdge> g = new WeightedGraphMutableNetworkAdapter<>(
                NetworkBuilder.undirected().allowsParallelEdges(true).allowsSelfLoops(true).build(),
                DefaultWeightedEdge.class);
        g.addVertex(1);
        g.addVertex(2);
        DefaultWeightedEdge e = g.addEdge(1, 2);
        assertEquals(g.getEdgeWeight(e), 1d, 1e-9);
        g.setEdgeWeight(e, 3d);
        assertEquals(g.getEdgeWeight(e), 3d, 1e-9);
    }

    @Test
    public void testStringAsWeightedEdge()
    {
        Graph<Integer,
            String> g = new WeightedGraphMutableNetworkAdapter<>(
                NetworkBuilder.undirected().allowsParallelEdges(true).allowsSelfLoops(true).build(),
                String.class);
        g.addVertex(1);
        g.addVertex(2);
        assertTrue(g.addEdge(1, 2, "1-2"));
        assertEquals(g.getEdgeWeight("1-2"), 1d, 1e-9);
        g.setEdgeWeight("1-2", 3d);
        assertEquals(g.getEdgeWeight("1-2"), 3d, 1e-9);
        assertTrue(g.containsEdge("1-2"));
        g.removeEdge("1-2");
        assertFalse(g.containsEdge("1-2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidEdgeOnWeightedGraph()
    {
        Graph<Integer,
            String> g = new WeightedGraphMutableNetworkAdapter<>(
                NetworkBuilder.undirected().allowsParallelEdges(true).allowsSelfLoops(true).build(),
                String.class);
        g.getEdgeWeight("1-2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidEdgeOnWeightedGraphSet()
    {
        Graph<Integer,
            String> g = new WeightedGraphMutableNetworkAdapter<>(
                NetworkBuilder.undirected().allowsParallelEdges(true).allowsSelfLoops(true).build(),
                String.class);
        g.setEdgeWeight("1-2", 2d);
    }

    public void testInvalidEdgeOnUnweightedGraph()
    {
        Graph<Integer,
            String> g = new GraphMutableNetworkAdapter<>(
                NetworkBuilder.undirected().allowsParallelEdges(true).allowsSelfLoops(true).build(),
                String.class);
        assertEquals(1d, g.getEdgeWeight("1-2"), 1e-9);
    }

    @Test
    public void testDefaultEdgeOnWeightedGraphs()
    {
        Graph<Integer,
            DefaultEdge> g = new WeightedGraphMutableNetworkAdapter<>(
                NetworkBuilder.directed().allowsParallelEdges(true).allowsSelfLoops(true).build(),
                DefaultEdge.class);
        g.addVertex(1);
        g.addVertex(2);
        DefaultEdge e = g.addEdge(1, 2);
        assertEquals(g.getEdgeWeight(e), 1d, 1e-9);
        g.setEdgeWeight(e, 3d);
        assertEquals(g.getEdgeWeight(e), 3d, 1e-9);
        assertEquals(Integer.valueOf(1), g.getEdgeSource(e));
        assertEquals(Integer.valueOf(2), g.getEdgeTarget(e));
    }

    /**
     * Tests serialization
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testSerialization()
        throws Exception
    {
        Graph<Integer,
            DefaultWeightedEdge> g = new WeightedGraphMutableNetworkAdapter<>(
                NetworkBuilder.undirected().allowsParallelEdges(true).allowsSelfLoops(true).build(),
                DefaultWeightedEdge.class);

        assertTrue(g.getType().isAllowingMultipleEdges());
        assertTrue(g.getType().isAllowingSelfLoops());
        assertFalse(g.getType().isDirected());
        assertTrue(g.getType().isUndirected());
        assertTrue(g.getType().isAllowingCycles());
        assertTrue(g.getType().isWeighted());

        g.addVertex(1);
        g.addVertex(2);
        DefaultWeightedEdge e = g.addEdge(1, 2);
        g.setEdgeWeight(e, 5.5d);

        Graph<Integer, DefaultWeightedEdge> g2 = (Graph<Integer, DefaultWeightedEdge>) serializeAndDeserialize(g);

        assertTrue(g2.getType().isAllowingMultipleEdges());
        assertTrue(g2.getType().isAllowingSelfLoops());
        assertFalse(g2.getType().isDirected());
        assertTrue(g2.getType().isUndirected());
        assertTrue(g2.getType().isAllowingCycles());
        assertTrue(g2.getType().isWeighted());
        
        assertTrue(g2.containsVertex(1));
        assertTrue(g2.containsVertex(2));
        assertTrue(g2.vertexSet().size() == 2);
        assertTrue(g2.edgeSet().size() == 1);
        assertTrue(g2.containsEdge(1, 2));
        
        assertEquals(g2.getEdgeWeight(g2.getEdge(1, 2)), 5.5d, 1e-9);
    }
    
    /**
     * Tests serialization
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testImmutableSerialization()
        throws Exception
    {
        MutableNetwork<String, DefaultWeightedEdge> network =
            NetworkBuilder.directed().allowsParallelEdges(true).allowsSelfLoops(true).build();

        network.addNode("v1");
        network.addNode("v2");
        DefaultWeightedEdge e12 = new DefaultWeightedEdge();
        network.addEdge("v1", "v2", e12);

        Graph<String, DefaultWeightedEdge> g =
            new WeightedGraphImmutableNetworkAdapter<>(ImmutableNetwork.copyOf(network), DefaultWeightedEdge.class);
        
        g.setEdgeWeight(e12, 5.5d);
        
        assertTrue(g.getType().isAllowingMultipleEdges());
        assertTrue(g.getType().isAllowingSelfLoops());
        assertTrue(g.getType().isDirected());
        assertFalse(g.getType().isUndirected());
        assertTrue(g.getType().isAllowingCycles());
        assertTrue(g.getType().isWeighted());

        Graph<String, DefaultWeightedEdge> g2 = (Graph<String, DefaultWeightedEdge>) serializeAndDeserialize(g);

        assertTrue(g2.getType().isAllowingMultipleEdges());
        assertTrue(g2.getType().isAllowingSelfLoops());
        assertTrue(g2.getType().isDirected());
        assertFalse(g2.getType().isUndirected());
        assertTrue(g2.getType().isAllowingCycles());
        assertTrue(g2.getType().isWeighted());
        
        assertTrue(g2.containsVertex("v1"));
        assertTrue(g2.containsVertex("v2"));
        assertTrue(g2.vertexSet().size() == 2);
        assertTrue(g2.edgeSet().size() == 1);
        assertTrue(g2.containsEdge("v1", "v2"));
        
        assertEquals(g2.getEdgeWeight(g2.getEdge("v1", "v2")), 5.5d, 1e-9);
    }

    private Object serializeAndDeserialize(Object obj)
        throws Exception
    {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bout);

        out.writeObject(obj);
        out.flush();

        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bin);

        obj = in.readObject();
        return obj;
    }

}
