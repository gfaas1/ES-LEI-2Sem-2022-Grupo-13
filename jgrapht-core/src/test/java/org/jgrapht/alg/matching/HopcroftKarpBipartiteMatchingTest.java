/*
 * (C) Copyright 2012-2017, by Joris Kinable and Contributors.
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

import java.util.*;
import java.util.stream.*;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.interfaces.MatchingAlgorithm.*;
import org.jgrapht.graph.*;

import junit.framework.*;

/**
 * Unit test for the HopcroftKarpBipartiteMatching class
 * 
 * @author Joris Kinable
 *
 */
@Deprecated
public class HopcroftKarpBipartiteMatchingTest
    extends MaximumCardinalityBipartiteMatchingTest
{

    @Override
    public MatchingAlgorithm<Integer, DefaultEdge> getMatchingAlgorithm(Graph<Integer, DefaultEdge> graph, Set<Integer> partition1, Set<Integer> partition2) {
        return new HopcroftKarpBipartiteMatching<>(graph, partition1, partition2);
    }
}
