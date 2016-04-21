/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2009, by Barak Naveh and Contributors.
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
/* -------------------------
 * DOTUtilsTest.java
 * -------------------------
 * (C) Copyright 2003-2016, by Christoph Zauner and Contributors
 *
 * Original Author:  Christoph Zauner
 *
 * $Id$
 *
 * Changes
 * -------
 *
 */
package org.jgrapht.ext;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * @author Christoph Zauner
 */
public class DOTUtilsTest
{

    // @Rule
    public TestRule watcher = new TestWatcher()
    {
        @Override
        protected void starting(Description description)
        {
            System.out.println(
                "\n+++ Test: " + description.getMethodName() + " +++\n");
        }
    };

    //@formatter:off
    /**
     * Graph to convert to a String:
     *
     *             +--> C
     *             |
     * A +--> B +--+
     *             |
     *             +--> D
     */
    //@formatter:on
    @Test
    public void testConvertGraphToDotString()
    {

        DirectedGraph<String, DefaultEdge> graph = new DefaultDirectedGraph<String, DefaultEdge>(
            DefaultEdge.class);

        String a = "A";
        String b = "B";
        String c = "C";
        String d = "D";

        graph.addVertex(a);
        graph.addVertex(b);
        graph.addVertex(c);
        graph.addVertex(d);

        graph.addEdge(a, b);
        graph.addEdge(b, c);
        graph.addEdge(b, d);

        // System.out.println("Input:\t\t" + graph);

        //@formatter:off
        String expectedGraphAsDotString =
                "digraph G {"            +
                  "  1 [ label=\"A\" ];" +
                  "  2 [ label=\"B\" ];" +
                  "  3 [ label=\"C\" ];" +
                  "  4 [ label=\"D\" ];" +
                  "  1 -> 2;"            +
                  "  2 -> 3;"            +
                  "  2 -> 4;"            +
                "}";
        //@formatter:on

        String graphAsDotString = DOTUtils.convertGraphToDotString(graph)
            .replaceAll("(\\r|\\n)", "");

        // System.out.println("\nOutput:\t\t" + graphAsDotString);
        // System.out.println("Expected:\t" + expectedGraphAsDotString);

        Assert.assertEquals(expectedGraphAsDotString, graphAsDotString);
    }
}

// End DOTUtilsTest.java
