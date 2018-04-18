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

/**
 * Allows obtaining all minimal <a href="https://en.wikipedia.org/wiki/Vertex_separator">vertex separators</a>
 * in the neighborhood of some edge in the {@code graph}. A separator is called minimal, if no its proper subset
 * is a separator in the graph.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 */
public class SeparatorFinder<V, E> {
    /**
     * The graph the search is performed on
     */
    private Graph<V, E> graph;

    /**
     * Constructs a new SeparatorFinder for the specified {@code graph}
     *
     * @param graph the graph vertex separators are to be searched in
     */
    public SeparatorFinder(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph);
    }

    /**
     * Computes and returns all minimal separators in the neighborhood of the {@code edge} in
     * the {@code graph}. The result may contain duplicate separators.
     * <p>
     * Returns null if the {@code graph} doesn't contain the specified {@code edge}
     *
     * @param edge the edge, whose neighborhood is being explored
     * @return the list of all minimal separators in the neighborhood of the {@code edge}.
     * The resulted list may contain duplicates.
     */
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
                    // possible to find one more separator
                    Set<V> separator = getSeparator(vertex, dfsMap);
                    if (!separator.isEmpty()) {
                        separators.add(separator);
                    }
                }
            }

            return separators;
        } else {
            return null;
        }
    }

    /**
     * Performs iterative depth-first search starting from the {@code startVertex}. Adds every
     * encountered red vertex to the resulting separator. Doesn't process red vertices. Marks
     * all white vertices with black color.
     *
     * @param startVertex the vertex to start depth-first traversal from
     * @param dfsMap      the depth-first vertex labeling
     * @return the computed separator, which consists of all encountered red vertices
     */
    private Set<V> getSeparator(V startVertex, Map<V, Byte> dfsMap) {
        LinkedList<V> stack = new LinkedList<>();
        Set<V> separator = new HashSet<>();
        stack.add(startVertex);

        while (!stack.isEmpty()) {
            V currentVertex = stack.removeLast();
            if (dfsMap.get(currentVertex) == 0) {
                dfsMap.put(currentVertex, (byte) 2);
                for (E edge : graph.edgesOf(currentVertex)) {
                    V opposite = Graphs.getOppositeVertex(graph, edge, currentVertex);
                    if (dfsMap.get(opposite) == 0) {
                        stack.add(opposite);
                    } else if (dfsMap.get(opposite) == 1) {
                        separator.add(opposite); // found red vertex, which belongs to the separator
                    }
                }
            }
        }

        return separator;
    }
}
