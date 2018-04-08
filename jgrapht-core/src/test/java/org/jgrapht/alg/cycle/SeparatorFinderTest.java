package org.jgrapht.alg.cycle;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SeparatorFinderTest {

    /**
     * Basic test of finding a separator
     */
    @Test
    public void testFindSeparators1() {
        Graph<Integer, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        Graphs.addEdgeWithVertices(graph, 1, 2);
        Graphs.addEdgeWithVertices(graph, 2, 3);
        Graphs.addEdgeWithVertices(graph, 3, 4);
        Graphs.addEdgeWithVertices(graph, 4, 5);
        Graphs.addEdgeWithVertices(graph, 5, 1);
        SeparatorFinder<Integer, DefaultEdge> finder = new SeparatorFinder<>(graph);
        List<Set<Integer>> separators = finder.findSeparators(graph.getEdge(1, 2));
        assertEquals(1, separators.size());
        Set<Integer> separator = separators.get(0);
        assertEquals(2, separator.size());
        assertTrue(separator.containsAll(Arrays.asList(3, 5)));
    }

    /**
     * Test for finding all copies of the same separator
     */
    @Test
    public void testFindSeparators2() {
        Graph<Integer, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        Graphs.addEdgeWithVertices(graph, 1, 2);
        Graphs.addEdgeWithVertices(graph, 2, 3);
        Graphs.addEdgeWithVertices(graph, 3, 4);
        Graphs.addEdgeWithVertices(graph, 3, 5);
        Graphs.addEdgeWithVertices(graph, 3, 6);
        SeparatorFinder<Integer, DefaultEdge> finder = new SeparatorFinder<>(graph);
        List<Set<Integer>> separators = finder.findSeparators(graph.getEdge(1, 2));
        assertEquals(3, separators.size());
        assertEquals(1 , separators.get(0).size());
        assertEquals(separators.get(0), separators.get(1));
        assertEquals(separators.get(1), separators.get(2));
    }

    /**
     * Test for uniqueness of the vertices in the computes separator
     */
    @Test
    public void testFindSeparators3(){
        Graph<Integer, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        Graphs.addEdgeWithVertices(graph, 1, 2);
        Graphs.addEdgeWithVertices(graph, 1, 3);
        Graphs.addEdgeWithVertices(graph, 1, 4);
        Graphs.addEdgeWithVertices(graph, 2, 3);
        Graphs.addEdgeWithVertices(graph, 2, 4);
        Graphs.addEdgeWithVertices(graph, 3, 5);
        Graphs.addEdgeWithVertices(graph, 4, 5);
        Graphs.addEdgeWithVertices(graph, 5, 6);
        SeparatorFinder<Integer, DefaultEdge> finder = new SeparatorFinder<>(graph);
        List<Set<Integer>> separators = finder.findSeparators(graph.getEdge(5, 6));
        assertEquals(1, separators.size());
        Set<Integer> separator = separators.get(0);
        assertEquals(2, separator.size());
        assertTrue(separator.containsAll(Arrays.asList(3, 4)));
    }
}
