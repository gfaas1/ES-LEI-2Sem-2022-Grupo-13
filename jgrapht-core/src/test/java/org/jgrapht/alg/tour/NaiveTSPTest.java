package org.jgrapht.alg.tour;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.junit.Test;

import static org.jgrapht.alg.tour.TwoApproxMetricTSPTest.assertHamiltonian;
import static org.junit.Assert.assertEquals;

/**
 * @author Alexandru Valeanu
 */
public class NaiveTSPTest
{
     static Graph<String, DefaultWeightedEdge> directedGraph(){
        SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> g =
                new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        g.addVertex("0");
        g.addVertex("1");
        g.addVertex("2");
        g.addVertex("3");
        g.addVertex("4");

        g.setEdgeWeight(g.addEdge("0", "1"), 9d);
        g.setEdgeWeight(g.addEdge("0", "3"), 8d);
        g.setEdgeWeight(g.addEdge("1", "0"), 7d);
        g.setEdgeWeight(g.addEdge("1", "2"), 1d);
        g.setEdgeWeight(g.addEdge("1", "4"), 3d);
        g.setEdgeWeight(g.addEdge("2", "0"), 5d);
        g.setEdgeWeight(g.addEdge("2", "4"), 4d);
        g.setEdgeWeight(g.addEdge("3", "2"), 6d);
        g.setEdgeWeight(g.addEdge("4", "3"), 7d);
        g.setEdgeWeight(g.addEdge("4", "1"), 1d);

        return g;
    }

    static Graph<String, DefaultWeightedEdge> directedGraph2(){
        SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> g =
                new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        g.addVertex("0");
        g.addVertex("1");
        g.addVertex("2");
        g.addVertex("3");

        g.setEdgeWeight(g.addEdge("1", "3"), 578985d);
        g.setEdgeWeight(g.addEdge("1", "2"), 316670d);
        g.setEdgeWeight(g.addEdge("2", "3"), 121118d);
        g.setEdgeWeight(g.addEdge("3", "2"), 585978d);
        g.setEdgeWeight(g.addEdge("0", "1"), 220022d);
        g.setEdgeWeight(g.addEdge("2", "1"), 62190d);
        g.setEdgeWeight(g.addEdge("0", "3"), 599952d);
        g.setEdgeWeight(g.addEdge("3", "1"), 540561d);
        g.setEdgeWeight(g.addEdge("0", "2"), 960850d);
        g.setEdgeWeight(g.addEdge("2", "0"), 781797d);

        return g;
    }

    static Graph<String, DefaultWeightedEdge> undirectedGraph(){
        SimpleWeightedGraph<String, DefaultWeightedEdge> g =
                new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        g.addVertex("1");
        g.addVertex("2");
        g.addVertex("3");
        g.addVertex("4");

        g.setEdgeWeight(g.addEdge("1", "2"), 10d);
        g.setEdgeWeight(g.addEdge("1", "3"), 15d);
        g.setEdgeWeight(g.addEdge("1", "4"), 20d);
        g.setEdgeWeight(g.addEdge("2", "3"), 35d);
        g.setEdgeWeight(g.addEdge("2", "4"), 25d);
        g.setEdgeWeight(g.addEdge("3", "4"), 30d);

        return g;
    }

    static Graph<String, DefaultWeightedEdge> symmetric4CitiesGraph(){
        SimpleWeightedGraph<String, DefaultWeightedEdge> g =
                new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        g.addVertex("A");
        g.addVertex("B");
        g.addVertex("C");
        g.addVertex("D");
        g.setEdgeWeight(g.addEdge("A", "B"), 20d);
        g.setEdgeWeight(g.addEdge("A", "C"), 42d);
        g.setEdgeWeight(g.addEdge("A", "D"), 35d);
        g.setEdgeWeight(g.addEdge("B", "C"), 30d);
        g.setEdgeWeight(g.addEdge("B", "D"), 34d);
        g.setEdgeWeight(g.addEdge("C", "D"), 12d);

        return g;
    }

    @Test
    public void testDirectedGraph()
    {
        Graph<String, DefaultWeightedEdge> g = directedGraph();

        GraphPath<String, DefaultWeightedEdge> tour =
                new NaiveTSP<String, DefaultWeightedEdge>().getTour(g);

        assertHamiltonian(g, tour);
        assertEquals(tour.getWeight(), 26d, 1e-9);
    }

    @Test
    public void testDirectedGraph2()
    {
        Graph<String, DefaultWeightedEdge> g = directedGraph2();

        GraphPath<String, DefaultWeightedEdge> tour =
                new NaiveTSP<String, DefaultWeightedEdge>().getTour(g);

        assertHamiltonian(g, tour);
        assertEquals(tour.getWeight(), 2166782d, 1e-9);
    }

    @Test
    public void testUndirectedGraph(){
        Graph<String, DefaultWeightedEdge> g = undirectedGraph();

        GraphPath<String, DefaultWeightedEdge> tour =
                new NaiveTSP<String, DefaultWeightedEdge>().getTour(g);

        assertHamiltonian(g, tour);
        assertEquals(tour.getWeight(), 80d, 1e-9);
    }

    @Test
    public void testWikiExampleSymmetric4Cities()
    {
        Graph<String, DefaultWeightedEdge> g = symmetric4CitiesGraph();

        GraphPath<String, DefaultWeightedEdge> tour =
                new NaiveTSP<String, DefaultWeightedEdge>().getTour(g);

        assertHamiltonian(g, tour);
        assertEquals(tour.getWeight(), 97d, 1e-9);
    }

}