/*
 * (C) Copyright 2018-2018, by Dimitrios Michail and Contributors.
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
package org.jgrapht.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.*;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.util.SupplierUtil;
import org.junit.*;

/**
 * @author Dimitrios Michail
 */
public class SimpleGraphMLImporterTest
{

    private static final String NL = System.getProperty("line.separator");

    @Test
    public void testUndirectedUnweighted()
        throws ImportException
    {
        // @formatter:off
        String input = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL + 
            "<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"" + NL +  
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + NL +
            "xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns " + 
            "http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">" + NL + 
            "<graph id=\"G\" edgedefault=\"undirected\">" + NL + 
            "<edge source=\"2\" target=\"3\"/>" + NL + 
            "<node id=\"1\"/>" + NL +
            "<node id=\"2\"/>" + NL + 
            "<node id=\"3\"/>" + NL +  
            "<edge source=\"1\" target=\"2\"/>" + NL + 
            "<edge source=\"3\" target=\"1\"/>"+ NL + 
            "</graph>" + NL + 
            "</graphml>";
        // @formatter:on

        Graph<String,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().weighted(false).allowingMultipleEdges(true).allowingSelfLoops(true)
                .vertexSupplier(SupplierUtil.createStringSupplier())
                .edgeSupplier(SupplierUtil.createDefaultEdgeSupplier()).buildGraph();

        new SimpleGraphMLImporter<String, DefaultEdge>()
            .importGraph(g, new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        assertEquals(3, g.vertexSet().size());
        assertEquals(3, g.edgeSet().size());
        assertTrue(g.containsVertex("0"));
        assertTrue(g.containsVertex("1"));
        assertTrue(g.containsVertex("2"));
        assertTrue(g.containsEdge("0", "1"));
        assertTrue(g.containsEdge("1", "2"));
        assertTrue(g.containsEdge("2", "0"));
    }

    @Test
    public void testUndirectedUnweightedWithConsumers()
        throws ImportException
    {
        // @formatter:off
        String input = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL + 
            "<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"" + NL +  
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + NL +
            "xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns " + 
            "http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">" + NL + 
            "<graph id=\"G\" edgedefault=\"undirected\">" + NL + 
            "<edge source=\"v\" target=\"x\"/>" + NL + 
            "<node id=\"u\"/>" + NL +
            "<node id=\"v\"/>" + NL + 
            "<node id=\"x\"/>" + NL +  
            "<edge source=\"u\" target=\"v\"/>" + NL + 
            "<edge source=\"x\" target=\"u\"/>"+ NL + 
            "</graph>" + NL + 
            "</graphml>";
        // @formatter:on

        Graph<String,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().weighted(false).allowingMultipleEdges(true).allowingSelfLoops(true)
                .vertexSupplier(SupplierUtil.createStringSupplier())
                .edgeSupplier(SupplierUtil.createDefaultEdgeSupplier()).buildGraph();

        Map<Pair<String, String>, Attribute> vertexAttrs = new HashMap<>();
        Map<Pair<DefaultEdge, String>, Attribute> edgeAttrs = new HashMap<>();

        new SimpleGraphMLImporter<String, DefaultEdge>((k, v) -> {
            vertexAttrs.put(k, v);
        }, (k, v) -> {
            edgeAttrs.put(k, v);
        }).importGraph(g, new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        // check graph
        assertEquals(3, g.vertexSet().size());
        assertEquals(3, g.edgeSet().size());
        assertTrue(g.containsVertex("0"));
        assertTrue(g.containsVertex("1"));
        assertTrue(g.containsVertex("2"));
        assertTrue(g.containsEdge("0", "1"));
        assertTrue(g.containsEdge("1", "2"));
        assertTrue(g.containsEdge("2", "0"));

        // check collected attributes
        assertEquals(vertexAttrs.get(Pair.of("0", "id")), DefaultAttribute.createAttribute("v"));
        assertEquals(vertexAttrs.get(Pair.of("1", "id")), DefaultAttribute.createAttribute("x"));
        assertEquals(vertexAttrs.get(Pair.of("2", "id")), DefaultAttribute.createAttribute("u"));
        assertEquals(
            edgeAttrs.get(Pair.of(g.getEdge("0", "1"), "source")),
            DefaultAttribute.createAttribute("v"));
        assertEquals(
            edgeAttrs.get(Pair.of(g.getEdge("0", "1"), "target")),
            DefaultAttribute.createAttribute("x"));
        assertEquals(
            edgeAttrs.get(Pair.of(g.getEdge("1", "2"), "source")),
            DefaultAttribute.createAttribute("x"));
        assertEquals(
            edgeAttrs.get(Pair.of(g.getEdge("1", "2"), "target")),
            DefaultAttribute.createAttribute("u"));
        assertEquals(
            edgeAttrs.get(Pair.of(g.getEdge("2", "0"), "source")),
            DefaultAttribute.createAttribute("u"));
        assertEquals(
            edgeAttrs.get(Pair.of(g.getEdge("2", "0"), "target")),
            DefaultAttribute.createAttribute("v"));
    }

    @Test
    public void testValidate()
        throws ImportException
    {
        // @formatter:off
        String input = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL + 
            "<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"" + NL +  
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + NL +
            "xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns " + 
            "http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">" + NL + 
            "<graph id=\"G\" edgedefault=\"undirected\">" + NL + 
            "<nOde id=\"1\"/>" + NL +
            "<node id=\"2\"/>" + NL + 
            "<myedge source=\"1\" target=\"2\"/>" + NL + 
            "</graph>" + NL + 
            "</graphml>";
        // @formatter:on

        try {
            Graph<String,
                DefaultEdge> g = GraphTypeBuilder
                    .undirected().weighted(false).allowingMultipleEdges(true)
                    .allowingSelfLoops(true).vertexSupplier(SupplierUtil.createStringSupplier())
                    .edgeSupplier(SupplierUtil.createDefaultEdgeSupplier()).buildGraph();

            Map<Pair<String, String>, Attribute> vertexAttrs = new HashMap<>();
            Map<Pair<DefaultEdge, String>, Attribute> edgeAttrs = new HashMap<>();

            new SimpleGraphMLImporter<String, DefaultEdge>()
                .importGraph(g, new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            fail("No!");
        } catch (ImportException e) {
        }
    }

}
