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
 * Algorithm to compute a <a href="http://mathworld.wolfram.com/Clique.html">Clique</a> in a graph.
 *
 * @param <V> vertex the graph vertex type
 *
 * @author Joris Kinable
 */
public interface CliqueAlgorithm<V> {

    /**
     * Computes a clique.
     *
     * @return a clique
     */
    Clique<V> getClique();

    /**
     * A <a href="http://mathworld.wolfram.com/Clique.html">Clique</a>
     *
     * @param <V> the vertex type
     */
    interface Clique<V>
            extends Set<V>
    {

        /**
         * Returns the weight of the clique. When solving a maximum weight clique
         * problem, the weight returned is the sum of the weights of the vertices in the clique. When
         * solving the unweighted variant, the cardinality of the clique is returned instead.
         *
         * @return weight of the independent set
         */
        double getWeight();
    }

    /**
     * Default implementation of a clique
     *
     * @param <V> the vertex type
     */
    class CliqueImpl<V>
            implements Clique<V>
    {
        protected Set<V> clique;
        protected double weight;

        public CliqueImpl(Set<V> clique)
        {
            this.clique = clique;
            this.weight = clique.size();
        }

        public CliqueImpl(Set<V> clique, double weight)
        {
            this.clique = clique;
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
            String s = "clique(" + this.getWeight() +
                    "): " +
                    this.clique.toString();
            return s;
        }

        @Override
        public int size() {
            return clique.size();
        }

        @Override
        public boolean isEmpty() {
            return clique.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public Iterator<V> iterator() {
            return clique.iterator();
        }

        @Override
        public Object[] toArray() {
            return clique.toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return clique.toArray(a);
        }

        @Override
        public boolean add(V v) {
            return clique.add(v);
        }

        @Override
        public boolean remove(Object o) {
            return clique.remove(o);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return clique.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends V> c) {
            return clique.addAll(c);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return clique.retainAll(c);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return clique.removeAll(c);
        }

        @Override
        public void clear() {
            clique.clear();
        }
    }
}
