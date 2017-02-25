/*
 * (C) Copyright 2009-2017, by Ilya Razenshteyn and Contributors.
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
import org.jgrapht.util.*;

/**
 * A union of directed graphs.
 * 
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 * 
 * @author Ilya Razenshteyn
 */
public class DirectedGraphUnion<V, E>
    extends GraphUnion<V, E, DirectedGraph<V, E>>
    implements DirectedGraph<V, E>
{
    private static final long serialVersionUID = -740199233080172450L;

    /**
     * Construct a new directed graph union.
     * 
     * @param g1 the first graph
     * @param g2 the second graph
     * @param operator the weight combiner (policy for edge weight calculation)
     */
    public DirectedGraphUnion(
        DirectedGraph<V, E> g1, DirectedGraph<V, E> g2, WeightCombiner operator)
    {
        super(g1, g2, operator);
    }

    /**
     * Construct a new directed graph union. The union will use the {@link WeightCombiner#SUM}
     * weight combiner.
     * 
     * @param g1 the first graph
     * @param g2 the second graph
     */
    public DirectedGraphUnion(DirectedGraph<V, E> g1, DirectedGraph<V, E> g2)
    {
        this(g1, g2, WeightCombiner.SUM);
    }
}

// End DirectedGraphUnion.java
