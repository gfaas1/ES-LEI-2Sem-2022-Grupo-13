/*
 * (C) Copyright 2013-2016, by Alexey Kudinkin and Contributors.
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
package org.jgrapht.alg.interfaces;

import java.util.Set;

/**
 * Allows to derive a <a href="http://en.wikipedia.org/wiki/Matching_(graph_theory)">matching</a> of
 * a given graph.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 */
public interface MatchingAlgorithm<V, E>
{
    /**
     * Default tolerance used by algorithms comparing floating point values.
     */
    double DEFAULT_EPSILON = 1e-9;

    /*
     * TODO after next release: Rename computeMatching() to getMatching() and deprecate
     * computeMatching().
     */

    /**
     * Returns set of edges making up the matching
     * 
     * @return a matching
     * @deprecated Use {@link #computeMatching()} instead.
     */
    @Deprecated
    default Set<E> getMatching()
    {
        return computeMatching().getEdges();
    }

    /**
     * Compute a matching for a given graph.
     * 
     * @return a matching
     */
    Matching<E> computeMatching();

    /**
     * A graph matching.
     *
     * @param <E> the graph edge type
     */
    interface Matching<E>
    {
        /**
         * Returns the weight of the matching.
         *
         * @return the weight of the matching
         */
        double getWeight();

        /**
         * Get the edges of the matching.
         *
         * @return the edges of the matching
         */
        Set<E> getEdges();
    }

}

// End MatchingAlgorithm.java
