/*
 * (C) Copyright 2018-2018, by Assaf Mizrachi and Contributors.
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
package org.jgrapht.alg.shortestpath;

import static org.junit.Assert.*;

import org.junit.*;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.util.*;
import org.jgrapht.generate.*;
import org.jgrapht.graph.*;

/**
 * 
 * Tests for the {@link KDisjointShortestPaths} class.
 * 
 * @author Assaf Mizrachi
 */
public class KDisjointShortestPathsTest {

        
    /**
     * Tests single path
     * 
     * Edges expected in path
     * ---------------
     * {@literal 1 --> 2}
     */
    @Test
    public void testSinglePath() {
        DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);        
        graph.addVertex(1);
        graph.addVertex(2);
        DefaultWeightedEdge edge = graph.addEdge(1, 2);
        graph.setEdgeWeight(edge, 8);
        KDisjointShortestPaths<Integer, DefaultWeightedEdge> alg = new KDisjointShortestPaths<>(graph, 5);
        List<GraphPath<Integer, DefaultWeightedEdge>> pathList = alg.getPaths(1, 2);
        assertEquals(1, pathList.size());
        assertEquals(1, pathList.get(0).getLength());
        assertTrue(pathList.get(0).getEdgeList().contains(edge));
        assertEquals(new Integer(2), pathList.get(0).getEndVertex());
//        assertEquals(graph, pathList.get(0).getGraph());
        assertEquals(new Integer(1), pathList.get(0).getStartVertex());
        assertEquals(2, pathList.get(0).getVertexList().size());
        assertTrue(pathList.get(0).getVertexList().contains(1));
        assertTrue(pathList.get(0).getVertexList().contains(2));
        assertEquals(pathList.get(0).getWeight(), 8.0, 0.0);
    }
    
    /**
     * Tests two disjoint paths from 1 to 3
     * 
     * Edges expected in path 1
     * ---------------
     * {@literal 1 --> 3}
     * 
     * Edges expected in path 2
     * ---------------
     * {@literal 1 --> 2}
     * {@literal 2 --> 3}
     * 
     */
    @Test
    public void testTwoDisjointPaths() {
        DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);        
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        DefaultWeightedEdge e12 = graph.addEdge(1, 2);
        DefaultWeightedEdge e23 = graph.addEdge(2, 3);
        DefaultWeightedEdge e13 = graph.addEdge(1, 3);
        KDisjointShortestPaths<Integer, DefaultWeightedEdge> alg = new KDisjointShortestPaths<>(graph, 5);
        
        List<GraphPath<Integer, DefaultWeightedEdge>> pathList = alg.getPaths(1, 3);
        
        assertEquals(2, pathList.size());
        
        assertEquals(1, pathList.get(0).getLength());
        assertEquals(2, pathList.get(1).getLength());
        
        assertEquals(1.0, pathList.get(0).getWeight(), 0.0);
        assertEquals(2.0, pathList.get(1).getWeight(), 0.0);
        
        assertTrue(pathList.get(0).getEdgeList().contains(e13));
        
        assertTrue(pathList.get(1).getEdgeList().contains(e12));
        assertTrue(pathList.get(1).getEdgeList().contains(e23));
        
                
    }
    
    /**
     * Tests two joint paths from 1 to 4
     * 
     * Edges expected in path 1
     * ---------------
     * {@literal 1 --> 2}, w=1
     * {@literal 2 --> 6}, w=1
     * {@literal 6 --> 4}, w=1
     * 
     * Edges expected in path 2
     * ---------------
     * {@literal 1 --> 5}, w=2
     * {@literal 5 --> 3}, w=2
     * {@literal 3 --> 4}, w=2
     * 
     * Edges expected in no path 
     * ---------------
     * {@literal 2 --> 3}, w=3
     * 
     */
    @Test
    public void testTwoDisjointPaths2() {
        DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);        
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addVertex(4);
        graph.addVertex(5);
        graph.addVertex(6);
        
        DefaultWeightedEdge e12 = graph.addEdge(1, 2);
        //this edge should not be used
        DefaultWeightedEdge e23 = graph.addEdge(2, 3);
        DefaultWeightedEdge e34 = graph.addEdge(3, 4);
        DefaultWeightedEdge e15 = graph.addEdge(1, 5);
        DefaultWeightedEdge e53 = graph.addEdge(5, 3);
        DefaultWeightedEdge e26 = graph.addEdge(2, 6);
        DefaultWeightedEdge e64 = graph.addEdge(6, 4);
        
        graph.setEdgeWeight(e12, 1);
        graph.setEdgeWeight(e23, 3);
        graph.setEdgeWeight(e34, 2);
        graph.setEdgeWeight(e15, 2);
        graph.setEdgeWeight(e53, 2);
        graph.setEdgeWeight(e26, 1);
        graph.setEdgeWeight(e64, 1);
        
        KDisjointShortestPaths<Integer, DefaultWeightedEdge> alg = new KDisjointShortestPaths<>(graph, 5);
        
        List<GraphPath<Integer, DefaultWeightedEdge>> pathList = alg.getPaths(1, 4);
        assertEquals(2, pathList.size());
        assertEquals(3, pathList.get(0).getLength());
        assertEquals(3, pathList.get(1).getLength());
        assertEquals(3.0, pathList.get(0).getWeight(), 0.0);
        assertEquals(6.0, pathList.get(1).getWeight(), 0.0);
        
        assertTrue(pathList.get(0).getEdgeList().contains(e12));
        assertTrue(pathList.get(0).getEdgeList().contains(e26));
        assertTrue(pathList.get(0).getEdgeList().contains(e64));
        
        assertTrue(pathList.get(1).getEdgeList().contains(e15));
        assertTrue(pathList.get(1).getEdgeList().contains(e53));
        assertTrue(pathList.get(1).getEdgeList().contains(e34));
    }
    
    /**
     * Tests three joint paths from 1 to 5
     * Edges expected in path 1
     * ---------------
     * {@literal 1 --> 4}, w=4
     * {@literal 4 --> 5}, w=1     
     * 
     * Edges expected in path 2
     * ---------------
     * {@literal 1 --> 2}, w=1
     * {@literal 2 --> 5}, w=6
     * 
     * Edges expected in path 3
     * ---------------
     * {@literal 1 --> 3}, w=4
     * {@literal 3 --> 5}, w=5
     * 
     * Edges expected in no path 
     * ---------------
     * {@literal 2 --> 3}, w=1
     * {@literal 3 --> 4}, w=1
     */
    @Test
    public void testThreeDisjointPaths() {
        Graph<Integer, DefaultWeightedEdge> graph = createThreeDisjointPathsGraph();
        
        DefaultWeightedEdge e12 = graph.getEdge(1, 2);
        DefaultWeightedEdge e25 = graph.getEdge(2, 5);
        DefaultWeightedEdge e13 = graph.getEdge(1, 3);
        DefaultWeightedEdge e35 = graph.getEdge(3, 5);
        DefaultWeightedEdge e14 = graph.getEdge(1, 4);
        DefaultWeightedEdge e45 = graph.getEdge(4, 5);
        
        KDisjointShortestPaths<Integer, DefaultWeightedEdge> alg = new KDisjointShortestPaths<>(graph, 5);
        
        List<GraphPath<Integer, DefaultWeightedEdge>> pathList = alg.getPaths(1, 5);
        assertEquals(3, pathList.size());
        assertEquals(2, pathList.get(0).getLength());
        assertEquals(2, pathList.get(1).getLength());
        assertEquals(2, pathList.get(2).getLength());
        
        assertEquals(5.0, pathList.get(0).getWeight(), 0.0);
        assertEquals(7.0, pathList.get(1).getWeight(), 0.0);
        assertEquals(9.0, pathList.get(2).getWeight(), 0.0);
        
        assertTrue(pathList.get(0).getEdgeList().contains(e14));
        assertTrue(pathList.get(0).getEdgeList().contains(e45));
        
        assertTrue(pathList.get(1).getEdgeList().contains(e12));
        assertTrue(pathList.get(1).getEdgeList().contains(e25));                       
        
        assertTrue(pathList.get(2).getEdgeList().contains(e13));
        assertTrue(pathList.get(2).getEdgeList().contains(e35));
    }
    
    private Graph<Integer, DefaultWeightedEdge> createThreeDisjointPathsGraph() {
        DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);        
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addVertex(4);
        graph.addVertex(5);
        
        DefaultWeightedEdge e12 = graph.addEdge(1, 2);
        DefaultWeightedEdge e25 = graph.addEdge(2, 5);
        DefaultWeightedEdge e13 = graph.addEdge(1, 3);
        DefaultWeightedEdge e35 = graph.addEdge(3, 5);
        DefaultWeightedEdge e14 = graph.addEdge(1, 4);
        DefaultWeightedEdge e45 = graph.addEdge(4, 5);
        DefaultWeightedEdge e23 = graph.addEdge(2, 3);
        DefaultWeightedEdge e34 = graph.addEdge(3, 4);
        
        graph.setEdgeWeight(e12, 1);
        graph.setEdgeWeight(e25, 6);
        graph.setEdgeWeight(e13, 4);
        graph.setEdgeWeight(e35, 5);
        graph.setEdgeWeight(e14, 4);
        graph.setEdgeWeight(e45, 1);
        graph.setEdgeWeight(e23, 1);
        graph.setEdgeWeight(e34, 1);
        
        return graph;
    }
    
    @Test
    public void testGraphIsNotChanged() {
        Graph<Integer, DefaultWeightedEdge> source = createThreeDisjointPathsGraph();
        Graph<Integer, DefaultWeightedEdge> destination = new DefaultDirectedWeightedGraph<>(source.getEdgeFactory());
        Graphs.addGraph(destination, source);
        
        new KDisjointShortestPaths<>(source, 5).getPaths(1, 5);
        
        assertEquals(destination, source);
    }
    
    /**
     * Only single disjoint path should exist on the line
     */
    @Test
    public void testLinear() {
        Graph<Integer, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);  
        GraphGenerator<Integer, DefaultWeightedEdge, Integer> graphGenerator = new LinearGraphGenerator<>(20);
        graphGenerator.generateGraph(graph, new IntegerVertexFactory(1), null);
        
        KDisjointShortestPaths<Integer, DefaultWeightedEdge> alg = new KDisjointShortestPaths<>(graph, 2);
        List<GraphPath<Integer, DefaultWeightedEdge>> pathList = alg.getPaths(1, 20);
        
        assertEquals(1, pathList.size());
        assertEquals(19, pathList.get(0).getLength());
        assertEquals(19.0, pathList.get(0).getWeight(), 0.0);
        
        for (int i = 1; i < 21; i++) {
            assertTrue(pathList.get(0).getVertexList().contains(i));
        }
    }
    
    /**
     * Exactly single disjoint path should exist on the ring
     */
    @Test
    public void testRing() {
        Graph<Integer, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);  
        GraphGenerator<Integer, DefaultWeightedEdge, Integer> graphGenerator = new RingGraphGenerator<>(20);
        graphGenerator.generateGraph(graph, new IntegerVertexFactory(1), null);
        
        KDisjointShortestPaths<Integer, DefaultWeightedEdge> alg = new KDisjointShortestPaths<>(graph, 2);
        List<GraphPath<Integer, DefaultWeightedEdge>> pathList = alg.getPaths(1, 10);
        
        assertEquals(1, pathList.size());
        assertEquals(9, pathList.get(0).getLength());
        assertEquals(9.0, pathList.get(0).getWeight(), 0.0);
        
        for (int i = 1; i < 10; i++) {
            assertTrue(pathList.get(0).getVertexList().contains(i));
        }
    }
}