/*
 * (C) Copyright 2016-2016, by Dimitrios Michail and Contributors.
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
package org.jgrapht;

import java.util.Arrays;
import java.util.stream.IntStream;

import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.generate.GnpRandomBipartiteGraphGenerator;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.graph.IntegerVertexFactory;
import org.jgrapht.graph.Pseudograph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class GraphTests.
 * 
 * @author Dimitrios Michail
 */
public class GraphTestsTest
{

    @Test
    public void testIsEmpty()
    {
        Graph<Integer, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);
        Assert.assertTrue(GraphTests.isEmpty(g));
        g.addVertex(1);
        Assert.assertTrue(GraphTests.isEmpty(g));
        g.addVertex(2);
        Assert.assertTrue(GraphTests.isEmpty(g));
        DefaultEdge e = g.addEdge(1, 2);
        Assert.assertFalse(GraphTests.isEmpty(g));
        g.removeEdge(e);
        Assert.assertTrue(GraphTests.isEmpty(g));
    }

    @Test
    public void testIsSimple()
    {
        // test empty
        Graph<Integer, DefaultEdge> g1 = new DefaultDirectedGraph<>(DefaultEdge.class);
        Assert.assertTrue(GraphTests.isSimple(g1));

        Graph<Integer, DefaultEdge> g2 = new SimpleGraph<>(DefaultEdge.class);
        Assert.assertTrue(GraphTests.isSimple(g2));

        Graph<Integer, DefaultEdge> g3 = new DirectedPseudograph<>(DefaultEdge.class);
        Assert.assertTrue(GraphTests.isSimple(g1));

        Graphs.addAllVertices(g3, Arrays.asList(1, 2));
        g3.addEdge(1, 2);
        g3.addEdge(2, 1);
        Assert.assertTrue(GraphTests.isSimple(g3));
        DefaultEdge g3e11 = g3.addEdge(1, 1);
        Assert.assertFalse(GraphTests.isSimple(g3));
        g3.removeEdge(g3e11);
        Assert.assertTrue(GraphTests.isSimple(g3));
        g3.addEdge(2, 1);
        Assert.assertFalse(GraphTests.isSimple(g3));

        Graph<Integer, DefaultEdge> g4 = new Pseudograph<>(DefaultEdge.class);
        Graphs.addAllVertices(g4, Arrays.asList(1, 2));
        Assert.assertTrue(GraphTests.isSimple(g4));
        DefaultEdge g4e12 = g4.addEdge(1, 2);
        g4.addEdge(2, 1);
        Assert.assertFalse(GraphTests.isSimple(g4));
        g4.removeEdge(g4e12);
        Assert.assertTrue(GraphTests.isSimple(g4));
        g4.addEdge(1, 1);
        Assert.assertFalse(GraphTests.isSimple(g4));
    }

    @Test
    public void testIsCompleteDirected()
    {
        Graph<Integer, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);
        Assert.assertTrue(GraphTests.isComplete(g));
        g.addVertex(1);
        Assert.assertTrue(GraphTests.isComplete(g));
        g.addVertex(2);
        Assert.assertFalse(GraphTests.isComplete(g));
        g.addEdge(1, 2);
        Assert.assertFalse(GraphTests.isComplete(g));
        g.addEdge(2, 1);
        Assert.assertTrue(GraphTests.isComplete(g));
        g.addVertex(3);
        Assert.assertFalse(GraphTests.isComplete(g));
        g.addEdge(1, 3);
        Assert.assertFalse(GraphTests.isComplete(g));
        g.addEdge(3, 1);
        Assert.assertFalse(GraphTests.isComplete(g));
        g.addEdge(2, 3);
        Assert.assertFalse(GraphTests.isComplete(g));
        g.addEdge(3, 2);
        Assert.assertTrue(GraphTests.isComplete(g));

        // check loops
        Graph<Integer, DefaultEdge> g1 = new DirectedPseudograph<>(DefaultEdge.class);
        Assert.assertTrue(GraphTests.isComplete(g1));
        g1.addVertex(1);
        Assert.assertTrue(GraphTests.isComplete(g1));
        g1.addVertex(2);
        Assert.assertFalse(GraphTests.isComplete(g1));
        g1.addEdge(1, 1);
        g1.addEdge(2, 2);
        Assert.assertFalse(GraphTests.isComplete(g1));

        // check multiple edges
        Graph<Integer, DefaultEdge> g2 = new DirectedPseudograph<>(DefaultEdge.class);
        Assert.assertTrue(GraphTests.isComplete(g2));
        Graphs.addAllVertices(g2, Arrays.asList(1, 2, 3));
        Assert.assertFalse(GraphTests.isComplete(g2));
        g2.addEdge(1, 2);
        g2.addEdge(1, 3);
        g2.addEdge(2, 3);
        g2.addEdge(1, 1);
        g2.addEdge(2, 2);
        g2.addEdge(3, 3);
        Assert.assertFalse(GraphTests.isComplete(g2));
    }

    @Test
    public void testIsCompleteUndirected()
    {
        Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        Assert.assertTrue(GraphTests.isComplete(g));
        g.addVertex(1);
        Assert.assertTrue(GraphTests.isComplete(g));
        g.addVertex(2);
        Assert.assertFalse(GraphTests.isComplete(g));
        g.addEdge(1, 2);
        Assert.assertTrue(GraphTests.isComplete(g));
        g.addVertex(3);
        Assert.assertFalse(GraphTests.isComplete(g));
        g.addEdge(1, 3);
        Assert.assertFalse(GraphTests.isComplete(g));
        g.addEdge(2, 3);
        Assert.assertTrue(GraphTests.isComplete(g));

        // check loops
        Graph<Integer, DefaultEdge> g1 = new Pseudograph<>(DefaultEdge.class);
        Assert.assertTrue(GraphTests.isComplete(g1));
        g1.addVertex(1);
        Assert.assertTrue(GraphTests.isComplete(g1));
        g1.addVertex(2);
        Assert.assertFalse(GraphTests.isComplete(g1));
        g1.addEdge(1, 1);
        Assert.assertFalse(GraphTests.isComplete(g1));

        // check multiple edges
        Graph<Integer, DefaultEdge> g2 = new Pseudograph<>(DefaultEdge.class);
        Assert.assertTrue(GraphTests.isComplete(g2));
        g2.addVertex(1);
        Assert.assertTrue(GraphTests.isComplete(g2));
        g2.addVertex(2);
        Assert.assertFalse(GraphTests.isComplete(g2));
        g2.addEdge(1, 2);
        Assert.assertTrue(GraphTests.isComplete(g2));
        g2.addEdge(1, 2);
        Assert.assertFalse(GraphTests.isComplete(g2));
        g2.addVertex(3);
        g2.addEdge(1, 3);
        Assert.assertFalse(GraphTests.isComplete(g2));
    }

    @Test
    public void testIsConnectedUndirected()
    {
        UndirectedGraph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        Assert.assertFalse(GraphTests.isConnected(g));
        g.addVertex(1);
        Assert.assertTrue(GraphTests.isConnected(g));
        g.addVertex(2);
        Assert.assertFalse(GraphTests.isConnected(g));
        g.addEdge(1, 2);
        Assert.assertTrue(GraphTests.isConnected(g));
        g.addVertex(3);
        Assert.assertFalse(GraphTests.isConnected(g));
        g.addEdge(1, 3);
        Assert.assertTrue(GraphTests.isConnected(g));
    }

    @Test
    public void testIsConnectedDirected()
    {
        DirectedGraph<Integer, DefaultEdge> g = new SimpleDirectedGraph<>(DefaultEdge.class);
        Assert.assertFalse(GraphTests.isWeaklyConnected(g));
        Assert.assertFalse(GraphTests.isStronglyConnected(g));
        g.addVertex(1);
        Assert.assertTrue(GraphTests.isWeaklyConnected(g));
        Assert.assertTrue(GraphTests.isStronglyConnected(g));
        g.addVertex(2);
        Assert.assertFalse(GraphTests.isWeaklyConnected(g));
        Assert.assertFalse(GraphTests.isStronglyConnected(g));
        g.addEdge(1, 2);
        Assert.assertTrue(GraphTests.isWeaklyConnected(g));
        Assert.assertFalse(GraphTests.isStronglyConnected(g));
        g.addVertex(3);
        Assert.assertFalse(GraphTests.isWeaklyConnected(g));
        Assert.assertFalse(GraphTests.isStronglyConnected(g));
        g.addEdge(2, 3);
        Assert.assertTrue(GraphTests.isWeaklyConnected(g));
        Assert.assertFalse(GraphTests.isStronglyConnected(g));
        g.addEdge(3, 1);
        Assert.assertTrue(GraphTests.isWeaklyConnected(g));
        Assert.assertTrue(GraphTests.isStronglyConnected(g));
    }

    @Test
    public void testIsTree()
    {
        UndirectedGraph<Integer, DefaultEdge> g = new Pseudograph<>(DefaultEdge.class);
        Assert.assertFalse(GraphTests.isTree(g));
        g.addVertex(1);
        Assert.assertTrue(GraphTests.isTree(g));
        g.addVertex(2);
        Assert.assertFalse(GraphTests.isTree(g));
        g.addEdge(1, 2);
        Assert.assertTrue(GraphTests.isTree(g));
        g.addVertex(3);
        Assert.assertFalse(GraphTests.isTree(g));
        g.addEdge(1, 3);
        Assert.assertTrue(GraphTests.isTree(g));
        g.addEdge(2, 3);
        Assert.assertFalse(GraphTests.isTree(g));

        // disconnected but with correct number of edges
        UndirectedGraph<Integer, DefaultEdge> g1 = new Pseudograph<>(DefaultEdge.class);
        Assert.assertFalse(GraphTests.isTree(g1));
        g1.addVertex(1);
        g1.addVertex(2);
        g.addEdge(1, 1);
        Assert.assertFalse(GraphTests.isTree(g1));
    }

    @Test
    public void testBipartite1()
    {
        UndirectedGraph<Integer, DefaultEdge> g = new Pseudograph<>(DefaultEdge.class);
        Assert.assertTrue(GraphTests.isBipartite(g));
        g.addVertex(1);
        Assert.assertTrue(GraphTests.isBipartite(g));
        g.addVertex(2);
        Assert.assertTrue(GraphTests.isBipartite(g));
        g.addEdge(1, 2);
        Assert.assertTrue(GraphTests.isBipartite(g));
        g.addVertex(3);
        Assert.assertTrue(GraphTests.isBipartite(g));
        g.addEdge(2, 3);
        Assert.assertTrue(GraphTests.isBipartite(g));
        g.addEdge(3, 1);
        Assert.assertFalse(GraphTests.isBipartite(g));
    }

    @Test
    public void testBipartite2()
    {
        UndirectedGraph<Integer, DefaultEdge> g = new Pseudograph<>(DefaultEdge.class);

        for (int i = 0; i < 100; i++) {
            g.addVertex(i);
            if (i > 0) {
                g.addEdge(i, i - 1);
            }
        }
        g.addEdge(99, 0);
        Assert.assertTrue(GraphTests.isBipartite(g));
    }

    @Test
    public void testBipartite3()
    {
        UndirectedGraph<Integer, DefaultEdge> g = new Pseudograph<>(DefaultEdge.class);

        for (int i = 0; i < 101; i++) {
            g.addVertex(i);
            if (i > 0) {
                g.addEdge(i, i - 1);
            }
        }
        g.addEdge(100, 0);
        Assert.assertFalse(GraphTests.isBipartite(g));
    }

    @Test
    public void testBipartite4()
    {
        DirectedGraph<Integer, DefaultEdge> g = new DirectedPseudograph<>(DefaultEdge.class);

        for (int i = 0; i < 101; i++) {
            g.addVertex(i);
            if (i > 0) {
                g.addEdge(i, i - 1);
            }
        }
        g.addEdge(100, 0);
        Assert.assertFalse(GraphTests.isBipartite(g));
    }

    @Test
    public void testRandomBipartite()
    {
        GnpRandomBipartiteGraphGenerator<Integer, DefaultEdge> generator =
            new GnpRandomBipartiteGraphGenerator<>(10, 10, 0.8);
        for (int i = 0; i < 100; i++) {
            Graph<Integer, DefaultEdge> g = new Pseudograph<>(DefaultEdge.class);
            generator.generateGraph(g, new IntegerVertexFactory(), null);
            Assert.assertTrue(GraphTests.isBipartite(g));
        }
    }

    @Test
    public void testUndirectedEulerian1()
    {
        // complete graph of 6 vertices
        UndirectedGraph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        CompleteGraphGenerator<Integer, DefaultEdge> gen = new CompleteGraphGenerator<>(6);
        gen.generateGraph(g, new IntegerVertexFactory(), null);
        Assert.assertFalse(GraphTests.isEulerian(g));
    }

    @Test
    public void testUndirectedEulerian2()
    {
        // even degrees but disconnected
        UndirectedGraph<Integer, DefaultEdge> g = new Pseudograph<>(DefaultEdge.class);
        Graphs.addAllVertices(g, Arrays.asList(1, 2, 3, 4, 5, 6));
        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(3, 1);
        g.addEdge(4, 5);
        g.addEdge(5, 6);
        g.addEdge(6, 4);
        Assert.assertFalse(GraphTests.isEulerian(g));
    }

    @Test
    public void testUndirectedEulerian3()
    {
        // even degrees
        UndirectedGraph<Integer, DefaultEdge> g = new Pseudograph<>(DefaultEdge.class);
        Graphs.addAllVertices(g, Arrays.asList(1, 2, 3, 4, 5, 6));
        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(3, 1);
        g.addEdge(4, 5);
        g.addEdge(5, 6);
        g.addEdge(6, 4);
        g.addEdge(3, 4);
        g.addEdge(3, 4);
        Assert.assertTrue(GraphTests.isEulerian(g));
    }

    @Test
    public void testUndirectedEulerian4()
    {
        // even degrees
        UndirectedGraph<Integer, DefaultEdge> g = new Pseudograph<>(DefaultEdge.class);
        g.addVertex(1);
        Assert.assertTrue(GraphTests.isEulerian(g));
    }

    @Test
    public void testUndirectedEulerian5()
    {
        // with loops
        UndirectedGraph<Integer, DefaultEdge> g = new Pseudograph<>(DefaultEdge.class);
        Graphs.addAllVertices(g, Arrays.asList(1, 2, 3, 4, 5, 6));
        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(3, 1);
        g.addEdge(4, 5);
        g.addEdge(5, 6);
        g.addEdge(6, 4);
        g.addEdge(3, 4);
        g.addEdge(3, 4);
        IntStream.rangeClosed(1, 6).forEach(i -> g.addEdge(i, i));
        Assert.assertTrue(GraphTests.isEulerian(g));
    }

    @Test
    public void testUndirectedEulerian6()
    {
        // with loops
        UndirectedGraph<Integer, DefaultEdge> g = new Pseudograph<>(DefaultEdge.class);
        Graphs.addAllVertices(g, Arrays.asList(1, 2, 3, 4, 5, 6));
        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(3, 1);
        g.addEdge(4, 5);
        g.addEdge(5, 6);
        g.addEdge(6, 4);
        IntStream.rangeClosed(1, 6).forEach(i -> g.addEdge(i, i));
        Assert.assertFalse(GraphTests.isEulerian(g));
    }

    @Test
    public void testUndirectedEulerian7()
    {
        // complete graph of 5 vertices
        UndirectedGraph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        CompleteGraphGenerator<Integer, DefaultEdge> gen = new CompleteGraphGenerator<>(5);
        gen.generateGraph(g, new IntegerVertexFactory(), null);
        Assert.assertTrue(GraphTests.isEulerian(g));
    }

    @Test
    public void testDirectedEulerian1()
    {
        // complete graph of 6 vertices
        DirectedGraph<Integer, DefaultEdge> g1 = new SimpleDirectedGraph<>(DefaultEdge.class);
        CompleteGraphGenerator<Integer, DefaultEdge> gen1 = new CompleteGraphGenerator<>(6);
        gen1.generateGraph(g1, new IntegerVertexFactory(), null);
        Assert.assertTrue(GraphTests.isEulerian(g1));

        // complete graph of 7 vertices
        DirectedGraph<Integer, DefaultEdge> g2 = new SimpleDirectedGraph<>(DefaultEdge.class);
        CompleteGraphGenerator<Integer, DefaultEdge> gen2 = new CompleteGraphGenerator<>(7);
        gen2.generateGraph(g2, new IntegerVertexFactory(), null);
        Assert.assertTrue(GraphTests.isEulerian(g2));
    }

    @Test
    public void testDirectedEulerian2()
    {
        DirectedGraph<Integer, DefaultEdge> g = new DirectedPseudograph<>(DefaultEdge.class);
        Graphs.addAllVertices(g, Arrays.asList(1));
        Assert.assertTrue(GraphTests.isEulerian(g));
        g.addEdge(1, 1);
        g.addEdge(1, 1);
        Assert.assertTrue(GraphTests.isEulerian(g));
        Graphs.addAllVertices(g, Arrays.asList(2));
        g.addEdge(2, 1);
        Assert.assertFalse(GraphTests.isEulerian(g));
    }

    @Test
    public void testDirectedEulerian3()
    {
        DirectedGraph<Integer, DefaultEdge> g = new DirectedPseudograph<>(DefaultEdge.class);
        Graphs.addAllVertices(g, Arrays.asList(1, 2, 3, 4, 5));
        Assert.assertFalse(GraphTests.isEulerian(g));
        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(3, 4);
        g.addEdge(4, 5);
        g.addEdge(5, 1);
        Assert.assertTrue(GraphTests.isEulerian(g));
        g.addEdge(2, 1);
        g.addEdge(3, 2);
        g.addEdge(4, 3);
        g.addEdge(5, 4);
        Assert.assertFalse(GraphTests.isEulerian(g));
        g.addEdge(1, 1);
        g.addEdge(2, 2);
        g.addEdge(3, 3);
        g.addEdge(4, 4);
        g.addEdge(5, 5);
        Assert.assertFalse(GraphTests.isEulerian(g));
        g.addEdge(1, 5);
        Assert.assertTrue(GraphTests.isEulerian(g));
    }

}

// End GraphTestsTest.java
