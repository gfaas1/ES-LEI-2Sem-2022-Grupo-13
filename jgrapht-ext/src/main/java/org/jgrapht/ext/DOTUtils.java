/*
 * (C) Copyright 2003-2017, by Christoph Zauner and Contributors.
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
package org.jgrapht.ext;

import java.io.*;
import java.util.regex.*;

import org.jgrapht.*;

/**
 * Class with DOT format related utilities.
 * 
 * @author Christoph Zauner
 */
public class DOTUtils
{
    /** Keyword for representing strict graphs. */
    static final String DONT_ALLOW_MULTIPLE_EDGES_KEYWORD = "strict";
    /** Keyword for directed graphs. */
    static final String DIRECTED_GRAPH_KEYWORD = "digraph";
    /** Keyword for undirected graphs. */
    static final String UNDIRECTED_GRAPH_KEYWORD = "graph";
    /** Edge operation for directed graphs. */
    static final String DIRECTED_GRAPH_EDGEOP = "->";
    /** Edge operation for undirected graphs. */
    static final String UNDIRECTED_GRAPH_EDGEOP = "--";

    // patterns for IDs
    private static final Pattern ALPHA_DIG = Pattern.compile("[a-zA-Z]+([\\w_]*)?");
    private static final Pattern DOUBLE_QUOTE = Pattern.compile("\".*\"");
    private static final Pattern DOT_NUMBER = Pattern.compile("[-]?([.][0-9]+|[0-9]+([.][0-9]*)?)");
    private static final Pattern HTML = Pattern.compile("<.*>");

    /**
     * Convert a graph into a string in DOT format.
     * 
     * @param graph the input graph
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     * @return a {@link String} representation in DOT format of the given graph
     * @deprecated in favor of implementing the current functionality directly by the user
     */
    @Deprecated
    public static <V, E> String convertGraphToDotString(Graph<V, E> graph)
    {
        StringWriter outputWriter = new StringWriter();
        new DOTExporter<V, E>(new IntegerComponentNameProvider<V>(),
            // vertex name provider
            new StringComponentNameProvider<V>(),
            // edge label provider
            null).exportGraph(graph, outputWriter);

        return outputWriter.toString();
    }

    /**
     * Test if the ID candidate is a valid ID.
     *
     * @param idCandidate the ID candidate.
     *
     * @return <code>true</code> if it is valid; <code>false</code> otherwise.
     */
    static boolean isValidID(String idCandidate)
    {
        return ALPHA_DIG.matcher(idCandidate).matches()
            || DOUBLE_QUOTE.matcher(idCandidate).matches()
            || DOT_NUMBER.matcher(idCandidate).matches() || HTML.matcher(idCandidate).matches();
    }

}

// End DOTUtils.java
