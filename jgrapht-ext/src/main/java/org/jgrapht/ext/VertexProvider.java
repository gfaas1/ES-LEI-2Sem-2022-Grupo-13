/*
 * (C) Copyright 2015-2017, by Wil Selwood and Contributors.
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
package org.jgrapht.ext;

/**
 * Creates a Vertex of type V
 *
 * @param <V> the vertex type
 * @deprecated Use {@link org.jgrapht.io.VertexProvider} instead.
 */
@Deprecated
public interface VertexProvider<V>
    extends org.jgrapht.io.VertexProvider<V>
{
}

// End VertexProvider.java
