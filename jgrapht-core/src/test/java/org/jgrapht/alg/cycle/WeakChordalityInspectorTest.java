package org.jgrapht.alg.cycle;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.VertexFactory;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.Pseudograph;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WeakChordalityInspectorTest {

    /**
     * Test on empty graph
     */
    @Test
    public void testIsWeaklyChordal1() {
        Graph<Integer, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        WeakChordalityInspector<Integer, DefaultEdge> inspector = new WeakChordalityInspector<>(graph);
        assertTrue(inspector.isWeaklyChordal());
    }

    /**
     * Test on small chordal graph
     */
    @Test
    public void testIsWeaklyChordal2() {
        Graph<Integer, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        Graphs.addEdgeWithVertices(graph, 1, 2);
        Graphs.addEdgeWithVertices(graph, 1, 3);
        Graphs.addEdgeWithVertices(graph, 2, 3);
        Graphs.addEdgeWithVertices(graph, 2, 4);
        Graphs.addEdgeWithVertices(graph, 3, 4);
        WeakChordalityInspector<Integer, DefaultEdge> inspector = new WeakChordalityInspector<>(graph);
        assertTrue(inspector.isWeaklyChordal());
    }

    /**
     * Test on small weakly chordal graph
     */
    @Test
    public void testIsWeaklyChordal3() {
        Graph<Integer, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        Graphs.addEdgeWithVertices(graph, 1, 2);
        Graphs.addEdgeWithVertices(graph, 1, 3);
        Graphs.addEdgeWithVertices(graph, 2, 4);
        Graphs.addEdgeWithVertices(graph, 3, 4);
        WeakChordalityInspector<Integer, DefaultEdge> inspector = new WeakChordalityInspector<>(graph);
        assertTrue(inspector.isWeaklyChordal());
    }

    /**
     * Test on hole
     */
    @Test
    public void testIsWeaklyChordal4() {
        Graph<Integer, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        Graphs.addEdgeWithVertices(graph, 1, 2);
        Graphs.addEdgeWithVertices(graph, 2, 3);
        Graphs.addEdgeWithVertices(graph, 3, 4);
        Graphs.addEdgeWithVertices(graph, 4, 5);
        Graphs.addEdgeWithVertices(graph, 5, 1);
        WeakChordalityInspector<Integer, DefaultEdge> inspector = new WeakChordalityInspector<>(graph);
        assertFalse(inspector.isWeaklyChordal());
    }

    /**
     * Test on anti hole
     */
    @Test
    public void testIsWeaklyChordal5() {
        Graph<Integer, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        Graphs.addEdgeWithVertices(graph, 1, 3);
        Graphs.addEdgeWithVertices(graph, 1, 4);
        Graphs.addEdgeWithVertices(graph, 1, 5);
        Graphs.addEdgeWithVertices(graph, 1, 6);
        Graphs.addEdgeWithVertices(graph, 2, 4);
        Graphs.addEdgeWithVertices(graph, 2, 5);
        Graphs.addEdgeWithVertices(graph, 2, 6);
        Graphs.addEdgeWithVertices(graph, 2, 7);
        Graphs.addEdgeWithVertices(graph, 3, 5);
        Graphs.addEdgeWithVertices(graph, 3, 6);
        Graphs.addEdgeWithVertices(graph, 3, 7);
        Graphs.addEdgeWithVertices(graph, 4, 6);
        Graphs.addEdgeWithVertices(graph, 4, 7);
        Graphs.addEdgeWithVertices(graph, 5, 7);
        WeakChordalityInspector<Integer, DefaultEdge> inspector = new WeakChordalityInspector<>(graph);
        assertFalse(inspector.isWeaklyChordal());
    }

    /**
     * Test on weakly chordal pseudograph
     */
    @Test
    public void testIsWeaklyChordal6() {
        Graph<Integer, DefaultEdge> graph = new Pseudograph<>(DefaultEdge.class);
        Graphs.addEdgeWithVertices(graph, 1, 1);
        Graphs.addEdgeWithVertices(graph, 1, 1);
        Graphs.addEdgeWithVertices(graph, 1, 2);
        Graphs.addEdgeWithVertices(graph, 1, 2);
        Graphs.addEdgeWithVertices(graph, 1, 2);
        Graphs.addEdgeWithVertices(graph, 1, 3);
        Graphs.addEdgeWithVertices(graph, 2, 4);
        Graphs.addEdgeWithVertices(graph, 2, 4);
        Graphs.addEdgeWithVertices(graph, 2, 4);
        Graphs.addEdgeWithVertices(graph, 3, 4);
        Graphs.addEdgeWithVertices(graph, 4, 4);
        Graphs.addEdgeWithVertices(graph, 4, 4);
        Graphs.addEdgeWithVertices(graph, 4, 4);
        WeakChordalityInspector<Integer, DefaultEdge> inspector = new WeakChordalityInspector<>(graph);
        assertTrue(inspector.isWeaklyChordal());
    }

    /**
     * Test on big not weakly chordal graph
     */
    @Test
    public void testIsWeaklyChordal7() {
        Graph<Integer, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        Graphs.addEdgeWithVertices(graph, 1, 2);
        Graphs.addEdgeWithVertices(graph, 1, 3);
        Graphs.addEdgeWithVertices(graph, 2, 4);
        Graphs.addEdgeWithVertices(graph, 2, 7);
        Graphs.addEdgeWithVertices(graph, 2, 8);
        Graphs.addEdgeWithVertices(graph, 2, 10);
        Graphs.addEdgeWithVertices(graph, 2, 5);
        Graphs.addEdgeWithVertices(graph, 3, 5);
        Graphs.addEdgeWithVertices(graph, 3, 6);
        Graphs.addEdgeWithVertices(graph, 4, 7);
        Graphs.addEdgeWithVertices(graph, 5, 8);
        Graphs.addEdgeWithVertices(graph, 5, 9);
        Graphs.addEdgeWithVertices(graph, 5, 6);
        Graphs.addEdgeWithVertices(graph, 6, 9);
        Graphs.addEdgeWithVertices(graph, 7, 8);
        Graphs.addEdgeWithVertices(graph, 7, 10);
        Graphs.addEdgeWithVertices(graph, 8, 9);
        Graphs.addEdgeWithVertices(graph, 8, 10);
        Graphs.addEdgeWithVertices(graph, 9, 10);
        WeakChordalityInspector<Integer, DefaultEdge> inspector = new WeakChordalityInspector<>(graph);
        assertFalse(inspector.isWeaklyChordal());
    }

    /**
     * Test on big chordless cycle
     */
    @Test
    public void testIsWeaklyChordal8() {
        Graph<Integer, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        int bound = 100;
        for (int i = 0; i < bound; i++) {
            Graphs.addEdgeWithVertices(graph, i, i + 1);
        }
        Graphs.addEdgeWithVertices(graph, 0, bound);
        WeakChordalityInspector<Integer, DefaultEdge> inspector = new WeakChordalityInspector<>(graph);
        assertFalse(inspector.isWeaklyChordal());
    }

    /**
     * Test on big complete graph
     */
    @Test
    public void testIsWeaklyChordal9() {
        Graph<Integer, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        CompleteGraphGenerator<Integer, DefaultEdge> generator = new CompleteGraphGenerator<>(50);
        generator.generateGraph(graph, new VertexFactory<Integer>() {
            private int number = 0;

            @Override
            public Integer createVertex() {
                return number++;
            }
        });
        WeakChordalityInspector<Integer, DefaultEdge> inspector = new WeakChordalityInspector<>(graph);
        assertTrue(inspector.isWeaklyChordal());
    }
}
