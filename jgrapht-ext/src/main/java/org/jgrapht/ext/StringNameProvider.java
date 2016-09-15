/*
 * (C) Copyright 2005-2016, by Charles Fry and Contributors.
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
 * Generates vertex names by invoking {@link #toString()} on them. This assumes that the vertex's
 * {@link #toString()} method returns a unique String representation for each vertex.
 *
 * @author Charles Fry
 */
public class StringNameProvider<V>
    implements VertexNameProvider<V>
{
    public StringNameProvider()
    {
    }

    /**
     * Returns the String representation of the unique integer representing a vertex.
     *
     * @param vertex the vertex to be named
     *
     * @return the name of
     */
    @Override
    public String getVertexName(V vertex)
    {
        return vertex.toString();
    }
}

// End StringNameProvider.java
