/*
 * (C) Copyright 2017-2017, by Dimitrios Michail and Contributors.
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
package org.jgrapht;

/**
 * An interface for a directed graph whose edges have non-uniform weights.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Dimitrios Michail
 */
public interface DirectedWeightedGraph<V, E>
    extends DirectedGraph<V, E>, WeightedGraph<V, E>
{
}
