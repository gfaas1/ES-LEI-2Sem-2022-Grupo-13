/*
 * (C) Copyright 2016-2016, by Dimitrios Michail and Contributors.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.SingleSourcePaths;
import org.jgrapht.generate.GnpRandomGraphGenerator;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.graph.IntegerVertexFactory;
import org.junit.Test;

/**
 * @author Dimitrios Michail
 */
public class DefaultAllPairsPathsTest
{

    @Test
    public void test()
    {
        int n = 50;
        DirectedPseudograph<Integer, DefaultWeightedEdge> g =
            new DirectedPseudograph<>(DefaultWeightedEdge.class);
        GraphGenerator<Integer, DefaultWeightedEdge, Integer> gen =
            new GnpRandomGraphGenerator<>(n, 0.7);
        gen.generateGraph(g, new IntegerVertexFactory(), null);

        List<SingleSourcePaths<Integer, DefaultWeightedEdge>> p = new ArrayList<>();
        Map<Integer, SingleSourcePaths<Integer, DefaultWeightedEdge>> map = new HashMap<>();
        for (int i = 1; i < n; i++) {
            SingleSourcePaths<Integer, DefaultWeightedEdge> path =
                new DijkstraShortestPath<>(g).getPaths(i);
            p.add(path);
            map.put(i, path);
        }

        DefaultAllPairsPaths<Integer, DefaultWeightedEdge> paths =
            new DefaultAllPairsPaths<>(g, map);

        for (int i = 1; i < n; i++) {
            for (int j = 1; j < n; j++) {
                assertEquals(
                    new DijkstraShortestPath<>(g).getPath(i, j).getEdgeList(),
                    paths.getPath(i, j).getEdgeList());
            }
        }
        assertEquals(0d, paths.getWeight(0, 0), 1e-9);
        assertTrue(paths.getPath(0, 0).getEdgeList().isEmpty());
        assertTrue(paths.getPath(0, 0).getVertexList().size() == 1);
        assertEquals(Double.POSITIVE_INFINITY, paths.getWeight(0, 1), 1e-9);
        assertTrue(paths.getPath(0, 1).getEdgeList().isEmpty());
    }

}
