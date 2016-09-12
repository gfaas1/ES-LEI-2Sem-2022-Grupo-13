/*
 * (C) Copyright 2005-2016, by Assaf Lehr and Contributors.
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

/**
 * Math Utilities. Currently contains the following:
 *
 * <ol>
 * <li>factorial(int N) - caclulate the factorial of N (aka N!)</li>
 * </ol>
 *
 * @author Assaf
 * @since May 30, 2005
 */
public class MathUtil
{
    public static long factorial(int N)
    {
        long multi = 1;
        for (int i = 1; i <= N; i++) {
            multi = multi * i;
        }
        return multi;
    }
}

// End MathUtil.java
