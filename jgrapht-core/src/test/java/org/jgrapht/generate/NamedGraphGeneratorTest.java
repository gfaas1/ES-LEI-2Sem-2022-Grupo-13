/*
 * (C) Copyright 2017-2017, by Joris Kinable and Contributors.
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
package org.jgrapht.generate;

import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for NamedGraphGenerator
 *
 * @author Joris Kinable
 */
public class NamedGraphGeneratorTest {

    @Test
    public void testDoyleGraph(){
        Graph<Integer, DefaultEdge> g=NamedGraphGenerator.doyleGraph();
        this.validateBasics(g, 27, 54, 3, 3, 5);
        assertTrue(GraphTests.isEulerian(g));
    }

    @Test
    public void testPetersenGraph(){
        Graph<Integer, DefaultEdge> g=NamedGraphGenerator.petersenGraph();
        this.validateBasics(g, 10, 15, 2, 2, 5);
        assertTrue(GraphTests.isCubic(g));
    }

    @Test
    public void testDürerGraphGraph(){
        Graph<Integer, DefaultEdge> g=NamedGraphGenerator.petersenGraph();
        this.validateBasics(g, 12, 18, 3, 4, 3);
        assertTrue(GraphTests.isCubic(g));
    }

    @Test
    public void testDodecahedronGraphGraph(){
        Graph<Integer, DefaultEdge> g=NamedGraphGenerator.dodecahedronGraph();
        this.validateBasics(g, 20, 30, 5, 5, 5);
        assertTrue(GraphTests.isCubic(g));
    }

    @Test
    public void testDesarguesGraphGraph(){
        Graph<Integer, DefaultEdge> g=NamedGraphGenerator.desarguesGraph();
        this.validateBasics(g, 20, 30, 5, 5, 6);
        assertTrue(GraphTests.isCubic(g));
        assertTrue(GraphTests.isBipartite(g));
    }

    @Test
    public void testNauruGraphGraph(){
        Graph<Integer, DefaultEdge> g=NamedGraphGenerator.nauruGraph();
        this.validateBasics(g, 24, 36, 4, 4, 6);
        assertTrue(GraphTests.isCubic(g));
        assertTrue(GraphTests.isBipartite(g));
    }

    @Test
    public void testMöbiusKantorGraph(){
        Graph<Integer, DefaultEdge> g=NamedGraphGenerator.möbiusKantorGraph();
        this.validateBasics(g, 16, 24, 4, 4, 6);
        assertTrue(GraphTests.isCubic(g));
        assertTrue(GraphTests.isBipartite(g));
    }

    @Test
    public void testBullGraph(){
        Graph<Integer, DefaultEdge> g=NamedGraphGenerator.bullGraph();
        this.validateBasics(g, 5, 5, 2, 3, 3);
    }

    @Test
    public void testButterflyGraph(){
        Graph<Integer, DefaultEdge> g=NamedGraphGenerator.butterflyGraph();
        this.validateBasics(g, 5, 6, 1, 2, 3);
        assertTrue(GraphTests.isEulerian(g));
    }

    @Test
    public void testClawGraph(){
        Graph<Integer, DefaultEdge> g=NamedGraphGenerator.clawGraph();
        this.validateBasics(g, 4, 3, 1, 3, Integer.MAX_VALUE);
        assertTrue(GraphTests.isBipartite(g));
    }

    @Test
    public void testBuckyBallGraph(){
        Graph<Integer, DefaultEdge> g=NamedGraphGenerator.buckyBallGraph();
        this.validateBasics(g, 60, 90, 9, 9, 5);
        assertTrue(GraphTests.isCubic(g));
    }

    @Test
    public void testClebschGraph(){
        Graph<Integer, DefaultEdge> g=NamedGraphGenerator.clebschGraph();
        this.validateBasics(g, 16, 40, 2, 2, 4);
    }

    @Test
    public void testGrötzschGraph(){
        Graph<Integer, DefaultEdge> g=NamedGraphGenerator.grötzschGraph();
        this.validateBasics(g, 11, 20, 2, 2, 4);
    }

    @Test
    public void testBidiakisCubeGraph(){
        Graph<Integer, DefaultEdge> g=NamedGraphGenerator.bidiakisCubeGraph();
        this.validateBasics(g, 12, 18, 3, 3, 4);
        assertTrue(GraphTests.isCubic(g));
    }

    @Test
    public void testBlanusaFirstSnarkGraphGraph(){
        Graph<Integer, DefaultEdge> g=NamedGraphGenerator.blanusaFirstSnarkGraph();
        this.validateBasics(g, 18, 27, 4, 4, 5);
        assertTrue(GraphTests.isCubic(g));
    }

    @Test
    public void testBlanusaSecondSnarkGraphGraph(){
        Graph<Integer, DefaultEdge> g=NamedGraphGenerator.blanusaSecondSnarkGraph();
        this.validateBasics(g, 18, 27, 4, 4, 5);
        assertTrue(GraphTests.isCubic(g));
    }

    @Test
    public void testDoubleStarSnarkGraphGraph(){
        Graph<Integer, DefaultEdge> g=NamedGraphGenerator.doubleStarSnarkGraph();
        this.validateBasics(g, 30, 45, 4, 4, 6);
    }

    @Test
    public void testBrinkmannGraphGraph(){
        Graph<Integer, DefaultEdge> g=NamedGraphGenerator.brinkmannGraph();
        this.validateBasics(g, 21, 42, 3, 3, 5);
        assertTrue(GraphTests.isEulerian(g));
    }

    @Test
    public void testGossetGraphGraph(){
        Graph<Integer, DefaultEdge> g=NamedGraphGenerator.gossetGraph();
        this.validateBasics(g, 56, 756, 3, 3, 3);
    }

    @Test
    public void testChvatalGraphGraph(){
        Graph<Integer, DefaultEdge> g=NamedGraphGenerator.chvatalGraph();
        this.validateBasics(g, 12, 24, 2, 2, 4);
        assertTrue(GraphTests.isEulerian(g));
    }

    private <V,E> void validateBasics(Graph<V, E> g, int vertices, int edges, int radius, int diameter, int girt){
        assertEquals(vertices, g.vertexSet().size());
        assertEquals(edges, g.edgeSet().size());
        GraphDistanceMetrics gdm = new GraphDistanceMetrics();
        assertEquals(radius, gdm.getRadius());
        assertEquals(diameter, gdm.getDiameter());
        assertEquals(girt, gdm.getGirth());
    }
}
