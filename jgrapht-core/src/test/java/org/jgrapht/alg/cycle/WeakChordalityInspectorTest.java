/*
 * (C) Copyright 2018, by Timofey Chudakov and Contributors.
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
package org.jgrapht.alg.cycle;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.util.IntegerVertexFactory;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.generate.NamedGraphGenerator;
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
        generator.generateGraph(graph, new IntegerVertexFactory());
        WeakChordalityInspector<Integer, DefaultEdge> inspector = new WeakChordalityInspector<>(graph);
        assertTrue(inspector.isWeaklyChordal());
    }


    @Test
    public void testIsWeaklyChordal10() {
        Graph<Integer, DefaultEdge> dodecahedron = NamedGraphGenerator.dodecahedronGraph();
        WeakChordalityInspector<Integer, DefaultEdge> inspector = new WeakChordalityInspector<>(dodecahedron);
        assertFalse(inspector.isWeaklyChordal());
    }

    @Test
    public void testIsWeaklyChordal11() {
        Graph<Integer, DefaultEdge> bull = NamedGraphGenerator.bullGraph();
        WeakChordalityInspector<Integer, DefaultEdge> inspector = new WeakChordalityInspector<>(bull);
        assertTrue(inspector.isWeaklyChordal());
    }

    @Test
    public void testIsWeaklyChordal12() {
        Graph<Integer, DefaultEdge> buckyBall = NamedGraphGenerator.buckyBallGraph();
        WeakChordalityInspector<Integer, DefaultEdge> inspector = new WeakChordalityInspector<>(buckyBall);
        assertFalse(inspector.isWeaklyChordal());
    }

    @Test
    public void testIsWeaklyChordal13() {
        Graph<Integer, DefaultEdge> clebsch = NamedGraphGenerator.clebschGraph();
        WeakChordalityInspector<Integer, DefaultEdge> inspector = new WeakChordalityInspector<>(clebsch);
        assertFalse(inspector.isWeaklyChordal());
    }

    @Test
    public void testIsWeaklyChordal14() {
        Graph<Integer, DefaultEdge> grötzsch = NamedGraphGenerator.grötzschGraph();
        WeakChordalityInspector<Integer, DefaultEdge> inspector = new WeakChordalityInspector<>(grötzsch);
        assertFalse(inspector.isWeaklyChordal());
    }

    @Test
    public void testIsWeaklyChordal15() {
        Graph<Integer, DefaultEdge> bidiakis = NamedGraphGenerator.bidiakisCubeGraph();
        WeakChordalityInspector<Integer, DefaultEdge> inspector = new WeakChordalityInspector<>(bidiakis);
        assertFalse(inspector.isWeaklyChordal());
    }

    @Test
    public void testIsWeaklyChordal16() {
        Graph<Integer, DefaultEdge> blanusaFirstSnark = NamedGraphGenerator.blanusaFirstSnarkGraph();
        WeakChordalityInspector<Integer, DefaultEdge> inspector = new WeakChordalityInspector<>(blanusaFirstSnark);
        assertFalse(inspector.isWeaklyChordal());
    }

    @Test
    public void testIsWeaklyChordal17() {
        Graph<Integer, DefaultEdge> doubleStarSnark = NamedGraphGenerator.doubleStarSnarkGraph();
        WeakChordalityInspector<Integer, DefaultEdge> inspector = new WeakChordalityInspector<>(doubleStarSnark);
        assertFalse(inspector.isWeaklyChordal());
    }

    @Test
    public void testIsWeaklyChordal18() {
        Graph<Integer, DefaultEdge> brinkmann = NamedGraphGenerator.brinkmannGraph();
        WeakChordalityInspector<Integer, DefaultEdge> inspector = new WeakChordalityInspector<>(brinkmann);
        assertFalse(inspector.isWeaklyChordal());
    }

    @Test
    public void testIsWeaklyChordal19() {
        Graph<Integer, DefaultEdge> gosset = NamedGraphGenerator.gossetGraph();
        WeakChordalityInspector<Integer, DefaultEdge> inspector = new WeakChordalityInspector<>(gosset);
        assertFalse(inspector.isWeaklyChordal());
    }


    @Test
    public void testIsWeaklyChordal20() {
        Graph<Integer, DefaultEdge> chvatal = NamedGraphGenerator.chvatalGraph();
        WeakChordalityInspector<Integer, DefaultEdge> inspector = new WeakChordalityInspector<>(chvatal);
        assertFalse(inspector.isWeaklyChordal());
    }

    @Test
    public void testIsWeaklyChordal21() {
        Graph<Integer, DefaultEdge> kittell = NamedGraphGenerator.kittellGraph();
        WeakChordalityInspector<Integer, DefaultEdge> inspector = new WeakChordalityInspector<>(kittell);
        assertFalse(inspector.isWeaklyChordal());
    }

    @Test
    public void testIsWeaklyChordal22() {
        Graph<Integer, DefaultEdge> coxeter = NamedGraphGenerator.coxeterGraph();
        WeakChordalityInspector<Integer, DefaultEdge> inspector = new WeakChordalityInspector<>(coxeter);
        assertFalse(inspector.isWeaklyChordal());
    }

    @Test
    public void testIsWeaklyChordal23() {
        Graph<Integer, DefaultEdge> ellinghamHorton78 = NamedGraphGenerator.ellinghamHorton78Graph();
        WeakChordalityInspector<Integer, DefaultEdge> inspector = new WeakChordalityInspector<>(ellinghamHorton78);
        assertFalse(inspector.isWeaklyChordal());
    }

    @Test
    public void testIsWeaklyChordal24() {
        Graph<Integer, DefaultEdge> errera = NamedGraphGenerator.erreraGraph();
        WeakChordalityInspector<Integer, DefaultEdge> inspector = new WeakChordalityInspector<>(errera);
        assertFalse(inspector.isWeaklyChordal());
    }

    @Test
    public void testIsWeaklyChordal25() {
        Graph<Integer, DefaultEdge> folkman = NamedGraphGenerator.folkmanGraph();
        WeakChordalityInspector<Integer, DefaultEdge> inspector = new WeakChordalityInspector<>(folkman);
        assertFalse(inspector.isWeaklyChordal());
    }

    @Test
    public void testIsWeaklyChordal26() {
        Graph<Integer, DefaultEdge> krackhardtKite = NamedGraphGenerator.krackhardtKiteGraph();
        WeakChordalityInspector<Integer, DefaultEdge> inspector = new WeakChordalityInspector<>(krackhardtKite);
        assertTrue(inspector.isWeaklyChordal());
    }
}
