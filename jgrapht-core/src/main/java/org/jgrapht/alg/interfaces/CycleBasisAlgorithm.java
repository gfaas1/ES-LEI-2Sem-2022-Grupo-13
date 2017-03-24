/*
 * (C) Copyright 2016-2017, by Dimitrios Michail and Contributors.
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

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Allows to derive an undirected <a href="https://en.wikipedia.org/wiki/Cycle_basis">cycle
 * basis</a> of a given graph.
 * 
 * <p>
 * Note that undirected cycle bases are defined for both undirected and directed graphs. For a
 * discussion of different kinds of cycle bases in graphs see the following paper.
 * <ul>
 * <li>Christian Liebchen, and Romeo Rizzi. Classes of Cycle Bases. Discrete Applied Mathematics,
 * 155(3), 337-355, 2007.</li>
 * </ul>
 *
 * @param <V> vertex the graph vertex type
 * @param <E> edge the graph edge type
 * 
 * @author Dimitrios Michail
 * @since October 2016
 */
public interface CycleBasisAlgorithm<V, E>
{
    /**
     * Return a list of cycles forming an undirected cycle basis of a graph.
     * 
     * @return an undirected cycle basis
     */
    CycleBasis<V, E> getCycleBasis();

    /**
     * An undirected cycle basis.
     * 
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     */
    interface CycleBasis<V, E>
    {
        /**
         * Return the set of cycles of the cycle basis.
         * 
         * @return the set of cycles of the cycle basis
         */
        Set<List<E>> getCycles();

        /**
         * Get the length of the cycle basis. The length of the cycle basis is the sum of the
         * lengths of its cycles. The length of a cycle is the total number of edges of the cycle.
         * 
         * @return the length of the cycles basis
         */
        int getLength();

        /**
         * Get the weight of the cycle basis. The weight of the cycle basis is the sum of the
         * weights of its cycles. The weight of a cycle is the sum of the weights of its edges.
         * 
         * @return the length of the cycles basis
         */
        double getWeight();
    }

    /**
     * Default implementation of the undirected cycle basis interface.
     *
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     */
    class CycleBasisImpl<V, E>
        implements CycleBasis<V, E>, Serializable
    {
        private static final long serialVersionUID = -1420882459022219505L;

        private Set<List<E>> cycles;
        private int length;
        private double weight;

        /**
         * Construct a new instance.
         */
        public CycleBasisImpl()
        {
            this.cycles = Collections.emptySet();
            this.length = 0;
            this.weight = 0d;
        }

        /**
         * Construct a new instance.
         * 
         * @param cycles the cycles of the basis
         * @param length the length of the cycle basis
         * @param weight the weight of the cycle basis
         */
        public CycleBasisImpl(Set<List<E>> cycles, int length, double weight)
        {
            this.cycles = Collections.unmodifiableSet(cycles);
            this.length = length;
            this.weight = weight;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Set<List<E>> getCycles()
        {
            return cycles;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getLength()
        {
            return length;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public double getWeight()
        {
            return weight;
        }

    }

}

// End UndirectedCycleBasisAlgorithm.java
