package org.jgrapht.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jgrapht.EnhancedTestCase;
import org.jgrapht.GraphPath;

public class SimpleGraphPathTest
    extends EnhancedTestCase
{
    private SimpleGraph<String, DefaultEdge> graph;
    private GraphPath<String, DefaultEdge> path;
    private List<String> pathVertices;

    @Override
    public void setUp()
    {
        graph = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");
        graph.addVertex("v4");
        graph.addEdge("v1", "v2");
        graph.addEdge("v1", "v3");
        graph.addEdge("v3", "v4");
        graph.addEdge("v2", "v4");

        pathVertices = new ArrayList<String>();
        pathVertices.add("v1");
        pathVertices.add("v2");
        pathVertices.add("v4");
        
        path = new SimpleGraphPath<String, DefaultEdge>(graph, pathVertices);
    }

    public void testEdgeList()
    {
        DefaultEdge[] expectedEdges = {
            graph.getEdge("v1", "v2"),
            graph.getEdge("v2", "v4") };
        assertEquals(Arrays.asList(expectedEdges), path.getEdgeList());
    }
    
    public void testInvalidVertexList() {
        List<String> invalidPath = new ArrayList<String>();
        invalidPath.add("v1");
        invalidPath.add("v2");
        invalidPath.add("v3");
        try {
            new SimpleGraphPath<String, DefaultEdge>(graph, invalidPath);
        } catch (IllegalArgumentException e) {
            assertTrue();
        }
    }
    
    public void testStartVertex() {
        assertTrue(path.getStartVertex().equals("v1"));
    }
    
    public void testEndVertex() {
        assertTrue(path.getEndVertex().equals("v4"));
    }
    
}
