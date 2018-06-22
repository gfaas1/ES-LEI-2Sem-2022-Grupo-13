/*
 * (C) Copyright 2018, by X and Contributors.
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
package org.jgrapht.graph;

import java.io.Serializable;

import org.jgrapht.Graph;

/**
 * Provides a weighted view on a graph
 *
 * Algorithms designed for weighted graphs should also work on unweighted graphs. This class emulates an weighted
 * graph based on a unweighted one by handling the storage of edge weights internally and passing all other operations
 * on the underlying graph. As a consequence, the edges returned are the edges of the original graph.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 */
public class AsWeightedGraph<V, E>
    extends GraphDelegator<V, E>
    implements Serializable, Graph<V, E> {
}
