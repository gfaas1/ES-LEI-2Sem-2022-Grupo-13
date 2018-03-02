/*
 * (C) Copyright 2018, by Timofey Chudakov and Contributors.
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
import org.jgrapht.graph.AsUndirectedGraph;

import java.util.*;

/**
 * Implementation of the lexicographical breadth-first search algorithm for chordal graph recognition.
 * <p>
 * A chordal graph is one in which all cycles of four or more vertices have a chord, which is an edge that
 * is not part of the cycle but connects two vertices of the cycle.
 * <p>
 * For more information on the topic see the following
 * <a href="http://www.cse.iitd.ac.in/~naveen/courses/CSL851/uwaterloo.pdf">article</a>:
 * <i>"CS 762: Graph-theoretic algorithms.
 * Lecture notes of a graduate course. University of Waterloo. Fall 1999, Winter 2002, Winter 2004."</i>
 * <p>
 * Terminology in this implementation is consistent with the one in the article. The implementation is based
 * also on the information from this article. Nevertheless, there is one important difference: in this implementation
 * vertex labels aren't used directly. Instead, the fact that the bucket, which results from moving vertices from particular
 * bucket to a new one, should be placed right before the initial bucket, is exploited. This results in time
 * and space optimization. This operation is handled by {@link BucketList}
 * <p>
 * Lexicographical BFS runs in O(|V| + |E|). Checking whether given order is the perfect elimination order
 * via {@link ChordalityInspector#isPerfectEliminationOrder(List)} takes O(|V| + |E|) as well. So,
 * overall time complexity of method {@link ChordalityInspector#isChordal()} is O(|V| + |E|).
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 * @author Timofey Chudakov
 * @see BucketList
 */
public class ChordalityInspector<V, E> {
    /**
     * The inspected graph
     */
    private Graph<V, E> graph;
    /**
     * Vertices of {@code graph} in order returned by {@link ChordalityInspector#getLexicographicalBfsOrder()}
     */
    private List<V> lexBfsOrder;

    /**
     * Creates a chordality inspector for {@code graph}
     *
     * @param graph the graph for which a chordality inspector to be created
     */
    public ChordalityInspector(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph);
        if (graph.getType().isDirected()) {
            this.graph = new AsUndirectedGraph<>(graph);
        }
    }

    /**
     * Checks whether the inspected graph is chordal. Note: Every time this method is invoked, LexBFS order is recomputed.
     *
     * @return true if this graph is chordal, otherwise false
     */
    public boolean isChordal() {
        this.lexBfsOrder = recomputeLexicographicalBfsOrder();
        return isPerfectEliminationOrder(lexBfsOrder);
    }

    /**
     * Either computes LexBFS order, if it hasn't yet been computed, or returns the most recently computed
     * LexBFS order.
     *
     * @return the vertices of the {@code graph} in the order, that was computed most recently
     */
    public List<V> getLexicographicalBfsOrder() {
        if (lexBfsOrder == null) {
            this.lexBfsOrder = recomputeLexicographicalBfsOrder();
        }
        return lexBfsOrder;
    }

    /**
     * Checks whether the vertices in the {@code vertexOrder} are in perfect elimination order with
     * respect to the inspected graph. Returns false, if the inspected graph isn't chordal.
     *
     * @param vertexOrder the sequence of vertices of {@code graph}
     * @return true if the {@code graph} is chordal and the vertices in {@code vertexOrder} are in
     * perfect elimination order
     */
    public boolean isPerfectEliminationOrder(List<V> vertexOrder) {
        Set<V> graphVertices = graph.vertexSet();
        if (graphVertices.size() == vertexOrder.size() && graphVertices.containsAll(vertexOrder)) {
            Map<V, Integer> map = new HashMap<>();
            int i = 0;
            for (V vertex : vertexOrder) {
                map.put(vertex, i);
                ++i;
            }
            return isPerfectEliminationOrder(vertexOrder, map);
        } else {
            return false;
        }
    }

    /**
     * Checks whether the vertices in the {@code vertexOrder} are in perfect elimination order.
     * Returns false, if the inspected graph isn't chordal.
     *
     * @param vertexOrder the sequence of vertices of {@code graph}
     * @param map         maps every vertex in {@code graph} to its position in {@code vertexOrder}, is used for constant-time lookups
     * @return true if the {@code graph} is chordal and the vertices in {@code vertexOrder} are in
     * perfect elimination order
     */
    private boolean isPerfectEliminationOrder(List<V> vertexOrder, Map<V, Integer> map) {
        for (V vertex : vertexOrder) {
            Set<V> predecessors = getPredecessors(map, vertex);
            if (predecessors.size() > 0) {
                V maxPredecessor = Collections.max(predecessors, Comparator.comparingInt(map::get));
                for (V predecessor : predecessors) {
                    if (predecessor.equals(maxPredecessor)) {
                        continue;
                    }
                    if (!graph.containsEdge(predecessor, maxPredecessor)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Invalidates previously computed LexBFS order and computes it again. After the invocation
     * of this method the method {@link ChordalityInspector#getLexicographicalBfsOrder()} will return
     * newly computed LexBFS order.
     *
     * @return computed LexBFS order
     */
    private List<V> recomputeLexicographicalBfsOrder() {
        Set<V> vertexSet = graph.vertexSet();
        if (vertexSet.size() > 0) {
            List<V> lexBfsOrder = new ArrayList<>(vertexSet.size());
            BucketList<V> bucketList = new BucketList<>(vertexSet);
            for (int i = 0; i < vertexSet.size(); i++) {
                V vertex = bucketList.poll();
                lexBfsOrder.add(vertex);
                bucketList.updateBuckets(getUnvisitedNeighbours(bucketList, vertex));
            }
            return lexBfsOrder;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Returns yet unvisited by the lexicographical breadth-first search neighbours of the {@code vertex}
     * in the inspected graph
     *
     * @param bucketList data structure, that backs the algorithm up
     * @param vertex     the vertex, whose neighbours are being explored
     * @return neighbours of {@code vertex} which have yet to be visited by lexicographical BFS
     */
    private Set<V> getUnvisitedNeighbours(BucketList<V> bucketList, V vertex) {
        Set<V> unmapped = new HashSet<>();
        Set<E> edges = graph.edgesOf(vertex);
        for (E edge : edges) {
            V oppositeVertex = Graphs.getOppositeVertex(graph, edge, vertex);
            if (bucketList.containsBucketWith(oppositeVertex)) {
                unmapped.add(oppositeVertex);
            }
        }
        return unmapped;
    }

    /**
     * Returns the predecessors of {@code vertex} in the order defined by {@code map}. More precisely,
     * returns those of {@code vertex}, whose mapped index in {@code map} is less then the index of {@code vertex}
     *
     * @param map    defines the mapping of vertices in {@code graph} to their indices in order
     * @param vertex the vertex whose predecessors in order are to be returned
     * @return the predecessors of {@code vertex} in order defines by {@code map}
     */
    private Set<V> getPredecessors(Map<V, Integer> map, V vertex) {
        Set<V> predecessors = new HashSet<>();
        Integer vertexPosition = map.get(vertex);
        Set<E> edges = graph.edgesOf(vertex);
        for (E edge : edges) {
            V oppositeVertex = Graphs.getOppositeVertex(graph, edge, vertex);
            Integer destPosition = map.get(oppositeVertex);
            if (destPosition < vertexPosition) {
                predecessors.add(oppositeVertex);
            }
        }
        return predecessors;
    }
    
}


