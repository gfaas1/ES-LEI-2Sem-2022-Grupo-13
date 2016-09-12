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
package org.jgrapht.alg.matching;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.WeightedMatchingAlgorithm;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Unit tests for the PathGrowingWeightedMatching algorithm
 * 
 * @author Dimitrios Michail
 */
public class PathGrowingWeightedMatchingTest
    extends BasePathGrowingWeightedMatchingTest
{

    @Override
    public WeightedMatchingAlgorithm<Integer, DefaultWeightedEdge> getApproximationAlgorithm(
        Graph<Integer, DefaultWeightedEdge> graph)
    {
        return new PathGrowingWeightedMatching<>(graph);
    };

}

// End PathGrowingWeightedMatchingTest.java
