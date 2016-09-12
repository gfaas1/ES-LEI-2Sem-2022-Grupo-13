/*
 * (C) Copyright 2006-2016, by John V Sichi and Contributors.
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
package org.jgrapht.util;

import java.util.*;


/**
 * Helper for efficiently representing small sets whose elements are known to be
 * unique by construction, implying we don't need to enforce the uniqueness
 * property in the data structure itself. Use with caution.
 *
 * <p>Note that for equals/hashCode, the class implements the Set behavior
 * (unordered), not the list behavior (ordered); the fact that it subclasses
 * ArrayList should be considered an implementation detail.
 *
 * @author John V. Sichi
 */
public class ArrayUnenforcedSet<E>
    extends ArrayList<E>
    implements Set<E>
{
    private static final long serialVersionUID = -7413250161201811238L;

    public ArrayUnenforcedSet()
    {
        super();
    }

    public ArrayUnenforcedSet(Collection<? extends E> c)
    {
        super(c);
    }

    public ArrayUnenforcedSet(int n)
    {
        super(n);
    }

    @Override public boolean equals(Object o)
    {
        return new SetForEquality().equals(o);
    }

    @Override public int hashCode()
    {
        return new SetForEquality().hashCode();
    }

    /**
     * Multiple inheritance helper.
     */
    private class SetForEquality
        extends AbstractSet<E>
    {
        @Override public Iterator<E> iterator()
        {
            return ArrayUnenforcedSet.this.iterator();
        }

        @Override public int size()
        {
            return ArrayUnenforcedSet.this.size();
        }
    }
}

// End ArrayUnenforcedSet.java
