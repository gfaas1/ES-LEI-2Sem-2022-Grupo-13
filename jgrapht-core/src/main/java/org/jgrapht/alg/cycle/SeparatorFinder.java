/*
 * (C) Copyright 2018-2018, by Timofey Chudakov and Contributors.
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
package org.jgrapht.alg.cycle;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;

import java.util.*;

public class SeparatorFinder<V, E> {
    private Graph<V, E> graph;

    public SeparatorFinder(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph);
    }

    public List<Set<V>> findSeparators(E edge) {
        if (graph.containsEdge(edge)) {
            List<Set<V>> separators = new ArrayList<>();
            V source = graph.getEdgeSource(edge);
            V target = graph.getEdgeTarget(edge);
            Set<V> neighborhood = Graphs.neighborhoodSetOf(graph, edge);
            Map<V, Byte> dfsMap = new HashMap<>(graph.vertexSet().size());

            //0 - unvisited (white), 1 - neighbor of the edge (red), 2 - visited (black)
            for (V vertex : graph.vertexSet()) {
                if (neighborhood.contains(vertex)) {
                    dfsMap.put(vertex, (byte) 1);
                } else {
                    dfsMap.put(vertex, (byte) 0);
                }
            }
            dfsMap.put(source, (byte) 2);
            dfsMap.put(target, (byte) 2);

            for (V vertex : graph.vertexSet()) {
                if (dfsMap.get(vertex) == 0) {
                    Set<V> separator = new HashSet<>();
                    separators.add(separator);
                    dfsVisit(vertex, dfsMap, separator);
                }
            }

            return separators;
        } else {
            return null;
        }
    }

    /**
     * Visits the {@code vertex}. Adds every encountered red vertex to the separator
     *
     * @param vertex the currently visited vertex
     * @param dfsMap the depth-first vertex labeling
     * @param separator the separator, to which all found red vertices are added
     */
    private void dfsVisit(V vertex, Map<V, Byte> dfsMap, Set<V> separator) {
        dfsMap.put(vertex, (byte) 2);

        for(V neighbor : Graphs.neighborListOf(graph, vertex)){
            if(dfsMap.get(neighbor) == 0){
                dfsVisit(neighbor, dfsMap, separator);
            }else if(dfsMap.get(neighbor) == 1){
                separator.add(neighbor);
            }
        }
    }
}
