package org.jgrapht.alg.cycle;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SeparatorFinderTest {

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
        assertEquals(separators.size(), 1);
        Set<Integer> separator = separators.get(0);
        assertEquals(separator.size(), 2);
        assertTrue(separator.containsAll(Arrays.asList(3, 5)));
    }

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
        assertEquals(separators.size(), 3);
        assertEquals(separators.get(0).size() , 1);
        assertEquals(separators.get(0), separators.get(1));
        assertEquals(separators.get(1), separators.get(2));
    }

    @Test
    public void testFindSeparators3() {

    }

    @Test
    public void testFindSeparators4() {

    }

    @Test
    public void testFindSeparators5() {

    }


}
