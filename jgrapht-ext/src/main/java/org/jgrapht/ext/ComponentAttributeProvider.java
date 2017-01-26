/*
 * (C) Copyright 2010-2017, by John Sichi and Contributors.
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
 * Provides display attributes for vertices and/or edges in a graph.
 *
 * @param <T> the type for which attributes are provided for
 *
 * @author John Sichi
 * @deprecated Use {@link org.jgrapht.io.ComponentAttributeProvider} instead.
 */
@Deprecated
public interface ComponentAttributeProvider<T>
    extends org.jgrapht.io.ComponentAttributeProvider<T>
{
}

// End ComponentAttributeProvider.java
