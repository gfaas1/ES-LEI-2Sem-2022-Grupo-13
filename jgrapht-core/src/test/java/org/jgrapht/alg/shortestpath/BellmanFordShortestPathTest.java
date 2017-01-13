/*
 * (C) Copyright 2006-2017, by John V Sichi and Contributors.
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
package org.jgrapht.alg.shortestpath;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.*;
import org.jgrapht.graph.*;

/**
 * .
 *
 * @author John V. Sichi
 */
public class BellmanFordShortestPathTest
    extends ShortestPathTestCase
{
    // ~ Methods ----------------------------------------------------------------

    /**
     * .
     */
    public void testConstructor()
    {
        SingleSourcePaths<String, DefaultWeightedEdge> tree;
        Graph<String, DefaultWeightedEdge> g = create();

        tree = new BellmanFordShortestPath<>(g).getPaths(V3);

        // find best path with no constraint on number of hops
        assertEquals(
            Arrays.asList(new DefaultEdge[] { e13, e12, e24, e45 }),
            tree.getPath(V5).getEdgeList());
        assertEquals(15.0, tree.getPath(V5).getWeight(), 0);

        // find best path within 2 hops (less than optimal)
        tree = new BellmanFordShortestPath<>(g, 2).getPaths(V3);
        assertEquals(Arrays.asList(new DefaultEdge[] { e34, e45 }), tree.getPath(V5).getEdgeList());
        assertEquals(25.0, tree.getPath(V5).getWeight(), 0);

        // find best path within 1 hop (doesn't exist!)
        tree = new BellmanFordShortestPath<>(g, 1).getPaths(V3);
        assertNull(tree.getPath(V5));
    }

    @Override
    protected List<DefaultWeightedEdge> findPathBetween(
        Graph<String, DefaultWeightedEdge> g, String src, String dest)
    {
        return new BellmanFordShortestPath<>(g).getPaths(src).getPath(dest).getEdgeList();
    }

    public void testWithNegativeEdges()
    {
        Graph<String, DefaultWeightedEdge> g = createWithBias(true);

        List<DefaultWeightedEdge> path;

        path = findPathBetween(g, V1, V4);
        assertEquals(Arrays.asList(new DefaultEdge[] { e13, e34 }), path);

        path = findPathBetween(g, V1, V5);
        assertEquals(Arrays.asList(new DefaultEdge[] { e15 }), path);
    }
}

// End BellmanFordShortestPathTest.java
