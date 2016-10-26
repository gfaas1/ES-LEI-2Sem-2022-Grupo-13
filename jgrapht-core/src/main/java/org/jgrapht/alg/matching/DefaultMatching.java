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

import java.io.Serializable;
import java.util.Set;

import org.jgrapht.alg.interfaces.MatchingAlgorithm.Matching;

/**
 * A default implementation of the matching interface.
 * 
 * @param <E> the graph edge type
 *
 * @author Dimitrios Michail
 */
class DefaultMatching<E>
    implements Matching<E>, Serializable
{
    private static final long serialVersionUID = 4767675421846527768L;

    private Set<E> edges;
    private double weight;

    /**
     * Construct a new instance
     * 
     * @param edges the edges of the matching
     * @param weight the weight of the matching
     */
    public DefaultMatching(Set<E> edges, double weight)
    {
        this.edges = edges;
        this.weight = weight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getWeight()
    {
        return weight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> getEdges()
    {
        return edges;
    }

}
