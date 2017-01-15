/*
 * (C) Copyright 2012-2017, by Alejandro Ramon Lopez del Huerto and Contributors.
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
package org.jgrapht.alg.matching;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.MatchingAlgorithm.*;
import org.jgrapht.graph.*;

import junit.framework.*;

/**
 * .
 *
 * @author Alejandro R. Lopez del Huerto
 * @since Jan 24, 2012
 */
public final class EdmondsBlossomShrinkingTest
    extends TestCase
{
    public void testOne()
    {
        // create an undirected graph
        UndirectedGraph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);

        Integer v1 = 1;
        Integer v2 = 2;
        Integer v3 = 3;
        Integer v4 = 4;

        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);

        DefaultEdge e12 = g.addEdge(v1, v2);
        DefaultEdge e34 = g.addEdge(v3, v4);

        // compute max match
        EdmondsBlossomShrinking<Integer, DefaultEdge> matcher = new EdmondsBlossomShrinking<>(g);
        Matching<DefaultEdge> match = matcher.computeMatching();
        assertEquals(2, match.getEdges().size());
        assertTrue(match.getEdges().contains(e12));
        assertTrue(match.getEdges().contains(e34));
    }

    public void testCrash()
    {
        UndirectedGraph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);

        Integer v1 = 1;
        Integer v2 = 2;
        Integer v3 = 3;
        Integer v4 = 4;
        Integer v5 = 5;

        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addVertex(v5);

        DefaultEdge e12 = g.addEdge(v1, v2);
        DefaultEdge e34 = g.addEdge(v3, v4);

        EdmondsBlossomShrinking<Integer, DefaultEdge> matcher = new EdmondsBlossomShrinking<>(g);

        Matching<DefaultEdge> match = matcher.computeMatching();

        assertEquals(2, match.getEdges().size());

        assertTrue(match.getEdges().contains(e12));
        assertTrue(match.getEdges().contains(e34));
    }

    public void testCrash2()
    {
        UndirectedGraph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);

        Integer vs[] = new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 };

        for (int i = 1; i < 14; ++i) {
            g.addVertex(vs[i]);
        }

        DefaultEdge e12 = g.addEdge(vs[1], vs[2]);
        DefaultEdge e34 = g.addEdge(vs[3], vs[4]);
        DefaultEdge e56 = g.addEdge(vs[5], vs[6]);
        DefaultEdge e78 = g.addEdge(vs[7], vs[8]);
        DefaultEdge e910 = g.addEdge(vs[9], vs[10]);
        DefaultEdge e1112 = g.addEdge(vs[11], vs[12]);

        EdmondsBlossomShrinking<Integer, DefaultEdge> matcher = new EdmondsBlossomShrinking<>(g);

        Matching<DefaultEdge> match = matcher.computeMatching();

        assertEquals(6, match.getEdges().size());

        assertTrue(match.getEdges().contains(e12));
        assertTrue(match.getEdges().contains(e34));
        assertTrue(match.getEdges().contains(e56));
        assertTrue(match.getEdges().contains(e78));
        assertTrue(match.getEdges().contains(e910));
        assertTrue(match.getEdges().contains(e1112));
    }
}
