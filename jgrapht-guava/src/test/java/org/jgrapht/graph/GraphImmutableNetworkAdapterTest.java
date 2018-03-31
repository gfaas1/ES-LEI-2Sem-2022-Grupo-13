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
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Test;

import com.google.common.graph.ImmutableNetwork;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;

/**
 * Check Incoming/Outgoing edges in directed and undirected graphs.
 *
 * @author Dimitrios Michail
 */
public class GraphImmutableNetworkAdapterTest
{

    /**
     * Test the most general version of the directed graph.
     */
    @Test
    public void testDirectedGraph()
    {
        MutableNetwork<String, DefaultEdge> network =
            NetworkBuilder.directed().allowsParallelEdges(true).allowsSelfLoops(true).build();

        network.addNode("v1");
        network.addNode("v2");
        network.addNode("v3");
        network.addNode("v4");
        network.addNode("v5");
        DefaultEdge e12 = new DefaultEdge();
        network.addEdge("v1", "v2", e12);
        DefaultEdge e23_1 = new DefaultEdge();
        network.addEdge("v2", "v3", e23_1);
        DefaultEdge e23_2 = new DefaultEdge();
        network.addEdge("v2", "v3", e23_2);
        DefaultEdge e24 = new DefaultEdge();
        network.addEdge("v2", "v4", e24);
        DefaultEdge e44 = new DefaultEdge();
        network.addEdge("v4", "v4", e44);
        DefaultEdge e55_1 = new DefaultEdge();
        network.addEdge("v5", "v5", e55_1);
        DefaultEdge e52 = new DefaultEdge();
        network.addEdge("v5", "v2", e52);
        DefaultEdge e55_2 = new DefaultEdge();
        network.addEdge("v5", "v5", e55_2);

        Graph<String, DefaultEdge> g =
            new GraphImmutableNetworkAdapter<>(ImmutableNetwork.copyOf(network), DefaultEdge.class);

        assertTrue(g.getType().isAllowingMultipleEdges());
        assertTrue(g.getType().isAllowingSelfLoops());
        assertTrue(g.getType().isDirected());
        assertFalse(g.getType().isUndirected());
        assertFalse(g.getType().isWeighted());
        assertTrue(g.getType().isAllowingCycles());

        assertEquals(1, g.degreeOf("v1"));
        assertEquals(5, g.degreeOf("v2"));
        assertEquals(2, g.degreeOf("v3"));
        assertEquals(3, g.degreeOf("v4"));
        assertEquals(5, g.degreeOf("v5"));

        assertEquals(new HashSet<>(Arrays.asList(e12)), g.edgesOf("v1"));
        assertEquals(new HashSet<>(Arrays.asList(e12, e23_1, e23_2, e24, e52)), g.edgesOf("v2"));
        assertEquals(new HashSet<>(Arrays.asList(e23_1, e23_2)), g.edgesOf("v3"));
        assertEquals(new HashSet<>(Arrays.asList(e24, e44)), g.edgesOf("v4"));
        assertEquals(new HashSet<>(Arrays.asList(e52, e55_1, e55_2)), g.edgesOf("v5"));

        assertEquals(0, g.inDegreeOf("v1"));
        assertEquals(2, g.inDegreeOf("v2"));
        assertEquals(2, g.inDegreeOf("v3"));
        assertEquals(2, g.inDegreeOf("v4"));
        assertEquals(2, g.inDegreeOf("v5"));

        assertEquals(new HashSet<>(), g.incomingEdgesOf("v1"));
        assertEquals(new HashSet<>(Arrays.asList(e12, e52)), g.incomingEdgesOf("v2"));
        assertEquals(new HashSet<>(Arrays.asList(e23_1, e23_2)), g.incomingEdgesOf("v3"));
        assertEquals(new HashSet<>(Arrays.asList(e24, e44)), g.incomingEdgesOf("v4"));
        assertEquals(new HashSet<>(Arrays.asList(e55_1, e55_2)), g.incomingEdgesOf("v5"));

        assertEquals(1, g.outDegreeOf("v1"));
        assertEquals(3, g.outDegreeOf("v2"));
        assertEquals(0, g.outDegreeOf("v3"));
        assertEquals(1, g.outDegreeOf("v4"));
        assertEquals(3, g.outDegreeOf("v5"));

        assertEquals(new HashSet<>(Arrays.asList(e12)), g.outgoingEdgesOf("v1"));
        assertEquals(new HashSet<>(Arrays.asList(e23_1, e23_2, e24)), g.outgoingEdgesOf("v2"));
        assertEquals(new HashSet<>(), g.outgoingEdgesOf("v3"));
        assertEquals(new HashSet<>(Arrays.asList(e44)), g.outgoingEdgesOf("v4"));
        assertEquals(new HashSet<>(Arrays.asList(e52, e55_1, e55_2)), g.outgoingEdgesOf("v5"));
        
        // test indeed immutable
        try { 
            g.addVertex("new");
            fail("Network not immutable");
        } 
        catch(UnsupportedOperationException e) { 
            // nothing
        }
        
        try { 
            g.addEdge("v1", "v5");
            fail("Network not immutable");
        } 
        catch(UnsupportedOperationException e) { 
            // nothing
        }
        
        try { 
            g.addEdge("v1", "v5", new DefaultEdge());
            fail("Network not immutable");
        } 
        catch(UnsupportedOperationException e) { 
            // nothing
        }
        
        try { 
            g.removeVertex("v1");
            fail("Network not immutable");
        } 
        catch(UnsupportedOperationException e) { 
            // nothing
        }
        
        try { 
            g.removeEdge("v1", "v2");
            fail("Network not immutable");
        } 
        catch(UnsupportedOperationException e) { 
            // nothing
        }
        
        try { 
            g.removeEdge(e12);
            fail("Network not immutable");
        } 
        catch(UnsupportedOperationException e) { 
            // nothing
        }
        
    }

}
