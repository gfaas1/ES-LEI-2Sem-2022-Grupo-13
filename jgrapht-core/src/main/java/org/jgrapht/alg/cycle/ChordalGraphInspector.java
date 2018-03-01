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
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;

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
 * also on the information from this article.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 * @author Timofey Chudakov
 */
public class ChordalGraphInspector<V, E> {

    /**
     * Checks whether the {@code graph} is chordal.
     *
     * @param graph the graph to check, whether it is chordal or not
     * @return true if this graph is chordal, otherwise false
     */
    public boolean isChordal(Graph<V, E> graph) {
        GraphTests.requireUndirected(graph);
        return isPerfectEliminationOrder(graph, getLexicographicalBfsOrder(graph));
    }

    /**
     * Performs lexicographical breadth-first search on the {@code graph} and returns its vertices in the order
     * in which they where visited by the algorithms
     *
     * @param graph the graph to perform algorithm ons
     * @return the vertices of the {@code graph} in the order produced by lexicographical breadth-first search
     */
    public List<V> getLexicographicalBfsOrder(Graph<V, E> graph) {
        Set<V> vertexSet = graph.vertexSet();
        if (vertexSet.size() > 0) {
            List<V> lexBfsOrder = new ArrayList<>(vertexSet.size());
            BucketList<V> bucketList = new BucketList<>(vertexSet);
            while (lexBfsOrder.size() < vertexSet.size()) {
                V vertex = bucketList.poll();
                lexBfsOrder.add(vertex);
                bucketList.updateBuckets(getUnvisitedNeighbours(graph, bucketList, vertex));
            }
            return lexBfsOrder;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Returns yet unvisited by the lexicographical breadth-first search neighbours of the {@code vertex}
     *
     * @param graph      the graph, on which LexBFS is performed
     * @param bucketList data structure, that backs the algorithm up
     * @param vertex     the vertex, whose neighbours are being explored
     * @return neighbours of {@code vertex} which have yet to be visited by lexicographical bfs
     */
    private Set<V> getUnvisitedNeighbours(Graph<V, E> graph, BucketList<V> bucketList, V vertex) {
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
     * Checks whether the vertices in the {@code vertexOrder} are in perfect elimination order.
     * Returns false, if {@code graph} isn't chordal.
     *
     * @param graph       the graph whose vertices are put in {@code vertexOrder}
     * @param vertexOrder the sequence of vertices of {@code graph}
     * @return true if the {@code graph} is chordal and the vertices in {@code vertexOrder} are in
     * perfect elimination order
     */
    public boolean isPerfectEliminationOrder(Graph<V, E> graph, List<V> vertexOrder) {
        Set<V> graphVertices = graph.vertexSet();
        if (graphVertices.size() == vertexOrder.size() && graphVertices.containsAll(vertexOrder)) {
            Map<V, Integer> map = new HashMap<>();
            int i = 0;
            for (V vertex : vertexOrder) {
                map.put(vertex, i);
                ++i;
            }
            return isPerfectEliminationOrder(graph, vertexOrder, map);
        } else {
            return false;
        }
    }

    /**
     * Checks whether the vertices in the {@code vertexOrder} are in perfect elimination order.
     * Returns false, if {@code graph} isn't chordal.
     *
     * @param graph       the graph whose vertices are put in {@code vertexOrder}
     * @param vertexOrder the sequence of vertices of {@code graph}
     * @param map         maps every vertex in {@code graph} to its position in {@code vertexOrder}, is used for constant-time lookups
     * @return true if the {@code graph} is chordal and the vertices in {@code vertexOrder} are in
     * perfect elimination order
     */
    private boolean isPerfectEliminationOrder(Graph<V, E> graph, List<V> vertexOrder, Map<V, Integer> map) {
        for (V vertex : vertexOrder) {
            Set<V> predecessors = getPredecessors(graph, map, vertex);
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
     * Returns the predecessors of {@code vertex} in the order defined by {@code map}. More precisely,
     * returns those of {@code vertex}, whose mapped index in {@code map} is less then index of {@code vertex}
     *
     * @param graph  the graph, whose vertices are ordered in {@code map}
     * @param map    defines the mapping of vertices in {@code graph} to their indices in order
     * @param vertex the vertex whose predecessors in order are to be returned
     * @return the predecessors of {@code vertex} in order defines by {@code map}
     */
    private Set<V> getPredecessors(Graph<V, E> graph, Map<V, Integer> map, V vertex) {
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


