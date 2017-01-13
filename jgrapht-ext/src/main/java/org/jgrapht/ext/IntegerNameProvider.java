/*
 * (C) Copyright 2005-2017, by Charles Fry and Contributors.
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
 * Assigns a unique integer to represent each vertex. Each instance of IntegerNameProvider maintains
 * an internal map between every vertex it has ever seen and the unique integer representing that
 * vertex. As a result it is probably desirable to have a separate instance for each distinct graph.
 *
 * @param <V> the graph vertex type
 *
 * @author Charles Fry
 * 
 * @deprecated in favor of {@link IntegerComponentNameProvider}
 */
@Deprecated
public class IntegerNameProvider<V>
    extends IntegerComponentNameProvider<V>
{
}

// End IntegerNameProvider.java
