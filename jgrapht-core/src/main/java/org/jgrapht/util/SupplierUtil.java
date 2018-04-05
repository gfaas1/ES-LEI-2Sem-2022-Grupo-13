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
package org.jgrapht.util;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * Helper class for suppliers.
 *
 * @author Dimitrios Michail
 */
public class SupplierUtil
{

    /**
     * Create a supplier from a class which calls the default constructor.
     * 
     * @param clazz the class
     * @return the supplier
     * @param <T> the type of results supplied by this supplier 
     */
    public static <T> Supplier<T> createSupplier(Class<? extends T> clazz)
    {
        return (Supplier<T> & Serializable) () -> {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
                throw new RuntimeException("Supplier failed", ex);
            }
        };
    }

    /**
     * Create an integer supplier which returns a sequence starting from zero.
     * 
     * @return an integer supplier
     */
    public static Supplier<Integer> createIntegerSupplier()
    {
        return new IntegerSupplier(0);
    }

    /**
     * Create an integer supplier which returns a sequence starting from a specific numbers.
     * 
     * @param start where to start the sequence
     * @return an integer supplier
     */
    public static Supplier<Integer> createIntegerSupplier(int start)
    {
        return new IntegerSupplier(start);
    }

    /**
     * Create a string supplier which returns unique strings. The returns strings are 
     * simply integers starting from zero.
     * 
     * @return a string supplier
     */
    public static Supplier<String> createStringSupplier()
    {
        return new StringSupplier(0);
    }
    
    /**
     * Create a string supplier which returns unique strings. The returns strings are 
     * simply integers starting from start.
     * 
     * @param start where to start the sequence 
     * @return a string supplier
     */
    public static Supplier<String> createStringSupplier(int start)
    {
        return new StringSupplier(start);
    }

    private static class IntegerSupplier
        implements Supplier<Integer>, Serializable
    {
        private static final long serialVersionUID = -4714266728630636497L;
        
        private int i = 0;

        public IntegerSupplier(int start)
        {
            this.i = start;
        }

        @Override
        public Integer get()
        {
            return i++;
        }
    }

    private static class StringSupplier
        implements Supplier<String>, Serializable
    {
        private static final long serialVersionUID = -5025488316341437260L;
        
        private int i = 0;
        
        public StringSupplier(int start)
        {
            this.i = start;
        }

        @Override
        public String get()
        {
            return String.valueOf(i++);
        }
    }

}
