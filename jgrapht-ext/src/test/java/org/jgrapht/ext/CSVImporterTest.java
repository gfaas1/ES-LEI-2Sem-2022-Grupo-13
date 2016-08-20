/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
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
/* ------------------------------
 * CSVImporterTest.java
 * ------------------------------
 * (C) Copyright 2016-2016, by Dimitrios Michail and Contributors.
 *
 * Original Author: Dimitrios Michail
 *
 * Changes
 * -------
 * 19-August-2016 : Initial revision (DM);
 *
 */
package org.jgrapht.ext;

import java.io.StringReader;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.graph.DirectedWeightedPseudograph;
import org.jgrapht.graph.Pseudograph;
import org.jgrapht.graph.WeightedPseudograph;

import junit.framework.TestCase;

/**
 * 
 * @author Dimitrios Michail
 */
public class CSVImporterTest
    extends TestCase
{

    public <E> Graph<String, E> readGraph(
        String input,
        CSVImporter.Format format,
        Character delimiter,
        Class<? extends E> edgeClass,
        boolean directed,
        boolean weighted)
        throws ImportException
    {
        Graph<String, E> g;
        if (directed) {
            if (weighted) {
                g = new DirectedWeightedPseudograph<String, E>(edgeClass);
            } else {
                g = new DirectedPseudograph<String, E>(edgeClass);
            }
        } else {
            if (weighted) {
                g = new WeightedPseudograph<String, E>(edgeClass);
            } else {
                g = new Pseudograph<String, E>(edgeClass);
            }
        }

        VertexProvider<String> vp = new VertexProvider<String>()
        {
            @Override
            public String buildVertex(
                String label,
                Map<String, String> attributes)
            {
                return label;
            }
        };

        EdgeProvider<String, E> ep = new EdgeProvider<String, E>()
        {

            @Override
            public E buildEdge(
                String from,
                String to,
                String label,
                Map<String, String> attributes)
            {
                return g.getEdgeFactory().createEdge(from, to);
            }

        };

        CSVImporter<String, E> importer = new CSVImporter<>(
            format,
            delimiter,
            vp,
            ep);
        importer.read(g, new StringReader(input));

        return g;
    }

    public void testEdgeListDirectedUnweighted()
        throws ImportException
    {
        // @formatter:off
        String input = "1,2\n"
                     + "2,3\n"
                     + "3,4\n"
                     + "4,1\n";
        // @formatter:on

        Graph<String, DefaultEdge> g = readGraph(
            input,
            CSVImporter.Format.EDGE_LIST,
            ',',
            DefaultEdge.class,
            true,
            false);

        assertEquals(4, g.vertexSet().size());
        assertEquals(4, g.edgeSet().size());
        assertTrue(g.containsVertex("1"));
        assertTrue(g.containsVertex("2"));
        assertTrue(g.containsVertex("3"));
        assertTrue(g.containsVertex("4"));
        assertTrue(g.containsEdge("1", "2"));
        assertTrue(g.containsEdge("2", "3"));
        assertTrue(g.containsEdge("3", "4"));
        assertTrue(g.containsEdge("4", "1"));
    }

    public void testEdgeListDirectedUnweightedWithSemicolon()
        throws ImportException
    {
        // @formatter:off
        String input = "1;2\n"
                     + "2;3\n"
                     + "3;4\n"
                     + "4;1\n";
        // @formatter:on

        Graph<String, DefaultEdge> g = readGraph(
            input,
            CSVImporter.Format.EDGE_LIST,
            ';',
            DefaultEdge.class,
            true,
            false);

        assertEquals(4, g.vertexSet().size());
        assertEquals(4, g.edgeSet().size());
        assertTrue(g.containsVertex("1"));
        assertTrue(g.containsVertex("2"));
        assertTrue(g.containsVertex("3"));
        assertTrue(g.containsVertex("4"));
        assertTrue(g.containsEdge("1", "2"));
        assertTrue(g.containsEdge("2", "3"));
        assertTrue(g.containsEdge("3", "4"));
        assertTrue(g.containsEdge("4", "1"));
    }

    public void testAdjacencyListDirectedUnweightedWithSemicolon()
        throws ImportException
    {
        // @formatter:off
        String input = "1;2;3;4\n"
                     + "2;3\n"
                     + "3;4;5;6\n"
                     + "4;1;5;6\n";
        // @formatter:on

        Graph<String, DefaultEdge> g = readGraph(
            input,
            CSVImporter.Format.ADJACENCY_LIST,
            ';',
            DefaultEdge.class,
            true,
            false);

        assertEquals(6, g.vertexSet().size());
        assertEquals(10, g.edgeSet().size());
        assertTrue(g.containsVertex("1"));
        assertTrue(g.containsVertex("2"));
        assertTrue(g.containsVertex("3"));
        assertTrue(g.containsVertex("4"));
        assertTrue(g.containsVertex("5"));
        assertTrue(g.containsVertex("6"));
        assertTrue(g.containsEdge("1", "2"));
        assertTrue(g.containsEdge("1", "3"));
        assertTrue(g.containsEdge("1", "4"));
        assertTrue(g.containsEdge("2", "3"));
        assertTrue(g.containsEdge("3", "4"));
        assertTrue(g.containsEdge("3", "5"));
        assertTrue(g.containsEdge("3", "6"));
        assertTrue(g.containsEdge("4", "1"));
        assertTrue(g.containsEdge("4", "5"));
        assertTrue(g.containsEdge("4", "6"));
    }
    
    public void testEdgeListWithStringsDirectedUnweightedWithSemicolon()
        throws ImportException
    {
        // @formatter:off
        String input = "'john doe';fred\n"
                     + "fred;\"fred\n\"\"21\"\"\"\n"
                     + "\"fred\n\"\"21\"\"\";\"who;;\"\n"
                     + "\"who;;\";'john doe'\n";
        // @formatter:on

        Graph<String, DefaultEdge> g = readGraph(
            input,
            CSVImporter.Format.EDGE_LIST,
            ';',
            DefaultEdge.class,
            true,
            false);

        assertEquals(4, g.vertexSet().size());
        assertEquals(4, g.edgeSet().size());
        assertTrue(g.containsVertex("'john doe'"));
        assertTrue(g.containsVertex("fred"));
        assertTrue(g.containsVertex("fred\n\"21\""));
        assertTrue(g.containsVertex("who;;"));
        assertTrue(g.containsEdge("'john doe'", "fred"));
        assertTrue(g.containsEdge("fred", "fred\n\"21\""));
        assertTrue(g.containsEdge("fred\n\"21\"", "who;;"));
        assertTrue(g.containsEdge("who;;", "'john doe'"));
    }

}
