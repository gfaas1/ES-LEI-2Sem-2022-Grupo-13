/*
 * (C) Copyright 2018-2018, by Dimitrios Michail and Contributors.
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
package org.jgrapht.graph.guava;

import java.io.Serializable;
import java.util.function.ToDoubleFunction;

import com.google.common.graph.ImmutableValueGraph;

/**
 * A graph adapter class using Guava's {@link ImmutableValueGraph} specialized with double values.
 *
 * @author Dimitrios Michail
 *
 * @param <V> the graph vertex type
 */
public class ImmutableDoubleValueGraphAdapter<V>
    extends ImmutableValueGraphAdapter<V, Double>
{
    private static final long serialVersionUID = 8730006126353129360L;

    /**
     * Create a new adapter.
     * 
     * @param valueGraph the value graph
     */
    public ImmutableDoubleValueGraphAdapter(ImmutableValueGraph<V, Double> valueGraph)
    {
        super(valueGraph, (ToDoubleFunction<Double> & Serializable) x -> x);
    }

}
