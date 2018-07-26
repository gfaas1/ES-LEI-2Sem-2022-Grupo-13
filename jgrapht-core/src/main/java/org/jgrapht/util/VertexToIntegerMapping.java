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
package org.jgrapht.util;

import java.util.*;

/**
 * Helper class for building a one-to-one mapping for a collection of vertices to the integer range $[0, n)$
 * where $n$ is the number of unique vertices in the collection.
 *
 * @author Alexandru Valeanu
 *
 * @param <V> the graph vertex type
 */
public class VertexToIntegerMapping<V> {

    private final Map<V, Integer> vertexMap;
    private final List<V> indexList;

    /**
     * Create a new mapping.
     *
     * @param vertices the input collection of vertices
     */
    public VertexToIntegerMapping(Collection<V> vertices){
        Objects.requireNonNull(vertices, "the input collection of vertices cannot be null");

        vertexMap = new HashMap<>(vertices.size());
        indexList = new ArrayList<>(vertices.size());

        for (V v : vertices) {
            if (!vertexMap.containsKey(v)){
                vertexMap.put(v, vertexMap.size());
                indexList.add(v);
            }
        }
    }

    /**
     * Get the vertexMap, a mapping from vertices to integers (i.e. the inverse of {@link this#getIndexList()}).
     *
     * @return a mapping from vertices to integers
     */
    public Map<V, Integer> getVertexMap(){
        return vertexMap;
    }

    /**
     * Get the indexList, a mapping from integers to mapping (i.e. the inverse of {@link this#getVertexMap()} ()}).
     *
     * @return a mapping from vertices to integers
     */
    public List<V> getIndexList(){
        return indexList;
    }
}
