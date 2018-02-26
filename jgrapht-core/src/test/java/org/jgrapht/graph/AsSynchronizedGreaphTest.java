package org.jgrapht.graph;

import java.util.*;
import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.junit.*;

import static org.junit.Assert.*;

/**
 * Test class AsSynchronizedIterateOnCopySet.
 *
 * @author CHEN Kui
 */
public class AsSynchronizedGreaphTest
{
    @Test
    public void getAllEdgesTest()
    {
        Graph<Integer, DefaultEdge> g = new DirectedMultigraph<>(DefaultEdge.class);
        g.addVertex(1);
        g.addVertex(2);
        g.addVertex(3);
        g.addEdge(1,2);
        g.addEdge(1,2);
        Set set = g.getAllEdges(1,2);
        assertEquals(2, set.size());
        g.removeVertex(2);
        g.addEdge(1,3);
        set = g.getAllEdges(1,3);
        assertEquals(1, set.size());
    }

    @Test
    public void edgeSetTest()
    {

        Graph<Integer, DefaultEdge> g = new AsSynchronizedGeaph<>(new SimpleDirectedGraph<>(DefaultEdge.class));

        g.addVertex(1);
        g.addVertex(2);
        g.addVertex(3);
        g.addEdge(1,2);


        Set<DefaultEdge> set = g.edgeSet();
        assertEquals(1, set.size());


        g.addEdge(2,3);
        assertEquals(2, set.size());

        g.addEdge(1,3);
        assertEquals(3, set.size());

        g.removeVertex(1);
        assertEquals(1, set.size());
    }

    @Test
    public void vertexSetTest()
    {
        Graph<Integer, DefaultEdge> g = new AsSynchronizedGeaph<>(new SimpleDirectedGraph<>(DefaultEdge.class));
        g.addVertex(1);
        g.addVertex(2);
        g.addVertex(3);
        Set<Integer> set = g.vertexSet();
        assertEquals(3, set.size());

        g.removeVertex(2);
        assertEquals(2, set.size());
    }

    @Test
    public void edgeOfSetTest()
    {
        Graph<Integer, DefaultEdge> g = new AsSynchronizedGeaph<>(new SimpleDirectedGraph<>(DefaultEdge.class));
        g.addVertex(1);
        g.addVertex(2);
        g.addVertex(3);
        Set<DefaultEdge> set = g.edgesOf(1);
        assertEquals(0, set.size());

        g.addEdge(1, 2);
        set = g.edgesOf(1);
        assertEquals(1, set.size());

        g.removeVertex(2);
        
        set = g.edgesOf(1);
        assertEquals(0, set.size());
        
        g.addEdge(1, 3);
        set = g.edgesOf(1);
        
        assertEquals(1, set.size());
    }

    @Test
    public void incomingEdgesOfTest()
    {
        Graph<Integer, DefaultEdge> g = new AsSynchronizedGeaph<>(new SimpleGraph<>(DefaultEdge.class));
        g.addVertex(1);
        g.addVertex(2);
        g.addVertex(3);
        Set<DefaultEdge> set = g.incomingEdgesOf(1);
        assertEquals(0, set.size());
        
        g.addEdge(1, 2);
        set =g.incomingEdgesOf(1);
        assertEquals(1, set.size());
        
        g.addEdge(1, 3);
        set = g.incomingEdgesOf(1);
        assertEquals(2, set.size());
        
        g.removeVertex(2);
        set = g.incomingEdgesOf(1);
        assertEquals(1, set.size());

        g = new AsSynchronizedGeaph<>(new SimpleDirectedGraph<>(DefaultEdge.class));
        g.addVertex(1);
        g.addVertex(2);
        g.addVertex(3);
        set = g.incomingEdgesOf(1);
        assertEquals(0, set.size());
        
        g.addEdge(1,2);
        set = g.incomingEdgesOf(1);
        assertEquals(0, set.size());

        g.addEdge(3, 1);
        set = g.incomingEdgesOf(1);
        assertEquals(1, set.size());
    }

    @Test
    public void outgoingEdgesOfTest()
    {
        Graph<Integer, DefaultEdge> g = new AsSynchronizedGeaph<>(new SimpleGraph<>(DefaultEdge.class));
        g.addVertex(1);
        g.addVertex(2);
        g.addVertex(3);
        Set<DefaultEdge> set = g.outgoingEdgesOf(1);
        assertEquals(0, set.size());

        g.addEdge(1, 2);
        set =g.outgoingEdgesOf(1);
        assertEquals(1, set.size());

        g.addEdge(1, 3);
        set = g.outgoingEdgesOf(1);
        assertEquals(2, set.size());

        g.removeVertex(2);
        set = g.outgoingEdgesOf(1);
        assertEquals(1, set.size());

        g = new AsSynchronizedGeaph<>(new SimpleDirectedGraph<>(DefaultEdge.class));
        g.addVertex(1);
        g.addVertex(2);
        g.addVertex(3);
        set = g.outgoingEdgesOf(1);
        assertEquals(0, set.size());

        g.addEdge(1,2);
        set = g.outgoingEdgesOf(1);
        assertEquals(1, set.size());

        g.addEdge(3, 1);
        set = g.outgoingEdgesOf(1);
        assertEquals(1, set.size());
    }

}
