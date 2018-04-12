/*
 * (C) Copyright 2018-2018, by Joris Kinable and Contributors.
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
package org.jgrapht.alg.interfaces;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Algorithm to compute an <a href="http://mathworld.wolfram.com/IndependentVertexSet.html">Independent Set</a> in a graph.
 *
 * @param <V> vertex the graph vertex type
 *
 * @author Joris Kinable
 */
public interface IndependentSetAlgorithm<V> {

    /**
     * Computes an independent set; all vertices are considered to have equal weight.
     *
     * @return a vertex independent set
     */
    IndependentSet<V> getIndependentSet();

    /**
     * A <a href="http://mathworld.wolfram.com/IndependentVertexSet.html">Independent Set</a>
     *
     * @param <V> the vertex type
     */
    interface IndependentSet<V>
            extends Set<V>
    {

        /**
         * Returns the weight of the independent set. When solving a minimum weight independent set
         * problem, the weight returned is the sum of the weights of the vertices in the independent set. When
         * solving the unweighted variant, the cardinality of the independent set is returned instead.
         *
         * @return weight of the independent set
         */
        double getWeight();
    }

    /**
     * Default implementation of a independent set
     *
     * @param <V> the vertex type
     */
    class IndependentSetImpl<V>
            implements IndependentSet<V>
    {
        protected Set<V> independentSet;
        protected double weight;

        public IndependentSetImpl(Set<V> independentSet)
        {
            this.independentSet = independentSet;
            this.weight = independentSet.size();
        }

        public IndependentSetImpl(Set<V> independentSet, double weight)
        {
            this.independentSet = independentSet;
            this.weight = weight;
        }

        /**
         * Set the weight of the independent set
         */
        public void setWeight(){ this.weight=weight;}

        @Override
        public double getWeight()
        {
            return weight;
        }


        @Override
        public String toString()
        {
            String s = "IS(" + this.getWeight() +
                    "): " +
                    this.independentSet.toString();
            return s;
        }

        @Override
        public int size() {
            return independentSet.size();
        }

        @Override
        public boolean isEmpty() {
            return independentSet.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public Iterator<V> iterator() {
            return independentSet.iterator();
        }

        @Override
        public Object[] toArray() {
            return independentSet.toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return independentSet.toArray(a);
        }

        @Override
        public boolean add(V v) {
            return independentSet.add(v);
        }

        @Override
        public boolean remove(Object o) {
            return independentSet.remove(o);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return independentSet.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends V> c) {
            return independentSet.addAll(c);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return independentSet.retainAll(c);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return independentSet.removeAll(c);
        }

        @Override
        public void clear() {
            independentSet.clear();
        }
    }
}
