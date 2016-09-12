/*
 * (C) Copyright 2007-2016, by France Telecom and Contributors.
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

import org.jgrapht.*;


/**
 * An undirected graph that is a {@link MaskSubgraph} on another graph.
 *
 * @author Guillaume Boulmier
 * @since July 5, 2007
 */
public class UndirectedMaskSubgraph<V, E>
    extends MaskSubgraph<V, E>
    implements UndirectedGraph<V, E>
{
    public UndirectedMaskSubgraph(
        UndirectedGraph<V, E> base,
        MaskFunctor<V, E> mask)
    {
        super(base, mask);
    }
}

// End UndirectedMaskSubgraph.java
