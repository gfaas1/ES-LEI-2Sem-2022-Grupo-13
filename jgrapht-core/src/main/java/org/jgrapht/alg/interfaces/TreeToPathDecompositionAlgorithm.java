/*
 * (C) Copyright 2018-2018, by Alexandru Valeanu and Contributors.
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

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * An algorithm which computes a decomposition into disjoint paths for a given tree/forest
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 */
public interface TreeToPathDecompositionAlgorithm<V, E> {
    /**
     * Computes a path decomposition.
     *
     * @return a path decomposition
     */
    PathDecomposition<V, E> getPathDecomposition();

    /**
     * A path decomposition.
     *
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     */
    interface PathDecomposition<V, E> {
        /**
         * Set of edges of the path decomposition.
         * 
         * @return edge set of the path decomposition
         */
        Set<E> getEdges();

        /**
         * List of disjoint vertex paths of the decomposition
         *
         * @return list of vertex paths
         */
        List<List<V>> getPaths();

        /**
         * @return number of paths in the decomposition
         */
        default int numberOfPaths(){
            return getPaths().size();
        }
    }

    /**
     * Default implementation of the path decomposition interface.
     *
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     */
    class PathDecompositionImpl<V, E> implements PathDecomposition<V, E>, Serializable {

        private static final long serialVersionUID = -5745023840678523568L;
        private final Set<E> edges;
        private final List<List<V>> paths;

        /**
         * Construct a new spanning tree.
         *
         * @param edges the edges
         * @param paths the vertex paths
         */
        public PathDecompositionImpl(Set<E> edges, List<List<V>> paths) {
            this.edges = edges;
            this.paths = paths;
        }

        @Override
        public Set<E> getEdges() {
            return edges;
        }

        @Override
        public List<List<V>> getPaths() {
            return paths;
        }

        @Override
        public String toString() {
            return "Path-Decomposition [edges=" + edges + "," + "paths=" + getPaths() + "]";
        }
    }

}
