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
import org.jgrapht.traverse.LexicographicalBfsIterator;
import org.jgrapht.traverse.MaximumCardinalityIterator;

import java.util.*;

/**
 * Implementation of the lexicographical breadth-first search algorithm for chordal graph recognition.
 * <p>
 * A <a href="https://en.wikipedia.org/wiki/Chordal_graph">chordal graph</a> is one in which all cycles of four or more vertices have a chord, which is an edge that
 * is not part of the cycle but connects two vertices of the cycle.
 * <p>
 * A graph is chordal iff its the vertices can be arranged into a perfect elimination order. Perfect elimination
 * order isn't unique. Both maximum cardinality search and lexicographical breadth-first search produces this order.
 * As a result, chordality inspection can be performed in two way. Here it is done by default via MCS,
 * because it outperforms LexBFS by a constant factor in practice. Nevertheless, lexicographical breadth-first
 * order can be obtained via {@link ChordalityInspector#getLexicographicalBfsOrder()}.
 * <p>
 * Both lexicographical BFS and maximum cardinality search run in O(|V| + |E|). Checking whether given order is the perfect elimination order
 * via {@link ChordalityInspector#isPerfectEliminationOrder(List)} takes O(|V| + |E|) as well. So,
 * overall time complexity of the method {@link ChordalityInspector#isChordal()} is O(|V| + |E|).
 *
 * @param <V> the graph vertex type.
 * @param <E> the graph edge type.
 * @author Timofey Chudakov
 * @see LexicographicalBfsIterator
 * @see MaximumCardinalityIterator
 */
public class ChordalityInspector<V, E> {
    /**
     * The inspected graph.
     */
    private Graph<V, E> graph;
    /**
     * Contains true if the graph is chordal, otherwise false. Is null before the first call to the
     * {@link ChordalityInspector#isChordal()}.
     */
    private Boolean chordal = null;

    /**
     * Vertices of the {@code graph} in a maximum cardinality order.
     */
    private List<V> mcsOrder;
    /**
     * Vertices of the {@code graph} in a lexicographical breadth-first order.
     */
    private List<V> lexBfsOrder;


    /**
     * Creates a chordality inspector for {@code graph}.
     *
     * @param graph the graph for which a chordality inspector to be created.
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
     * @return true if this graph is chordal, otherwise false.
     */
    public boolean isChordal() {
        if (chordal == null) {
            mcsOrder = mcs();
            chordal = isPerfectEliminationOrder(mcsOrder);
        }
        return chordal;
    }


    /**
     * Lazily computes lexicographical breadth-first order of the inspected graph.
     *
     * @return the vertices of the {@code graph} in the lexicographical breadth-first order.
     */
    public List<V> getLexicographicalBfsOrder() {
        if (lexBfsOrder == null) {
            lexBfsOrder = lexBfs();
        }
        return lexBfsOrder;
    }

    /**
     * Lazily computed maximum cardinality order of the inspected graph.
     *
     * @return the vertices of the {@code graph} in the maximum cardinality order.
     */
    public List<V> getMaximumCardinalityOrder() {
        if (mcsOrder == null) {
            mcsOrder = mcs();
        }
        return mcsOrder;
    }

    /**
     * Computes maximum cardinality order via {@link MaximumCardinalityIterator}.
     *
     * @return the order produced by the {@link MaximumCardinalityIterator}.
     */
    private List<V> mcs() {
        int vertexNum = graph.vertexSet().size();
        List<V> order = new ArrayList<>(vertexNum);
        MaximumCardinalityIterator<V, E> maximumCardinalityIterator = new MaximumCardinalityIterator<>(graph);
        for (int i = 0; i < vertexNum; i++) {
            order.add(maximumCardinalityIterator.next());
        }
        return order;
    }

    /**
     * Computes lexicographical breadth-first order via {@link LexicographicalBfsIterator}.
     *
     * @return the order produced by the {@link LexicographicalBfsIterator}.
     */
    private List<V> lexBfs() {
        int vertexNum = graph.vertexSet().size();
        List<V> order = new ArrayList<>(vertexNum);
        LexicographicalBfsIterator<V, E> iterator = new LexicographicalBfsIterator<>(graph);
        for (int i = 0; i < vertexNum; i++) {
            order.add(iterator.next());
        }
        return order;
    }

    /**
     * Checks whether the vertices in the {@code vertexOrder} are in perfect elimination order with
     * respect to the inspected graph. Returns false, if the inspected graph isn't chordal.
     *
     * @param vertexOrder the sequence of vertices of {@code graph}.
     * @return true if the {@code graph} is chordal and the vertices in {@code vertexOrder} are in
     * perfect elimination order.
     */
    public boolean isPerfectEliminationOrder(List<V> vertexOrder) {
        Set<V> graphVertices = graph.vertexSet();
        if (graphVertices.size() == vertexOrder.size() && graphVertices.containsAll(vertexOrder)) {
            Map<V, Integer> map = new HashMap<>(vertexOrder.size());
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
     * @param vertexOrder the sequence of vertices of {@code graph}.
     * @param map         maps every vertex in {@code graph} to its position in {@code vertexOrder}, is used for
     *                    constant-time lookups.
     * @return true if the {@code graph} is chordal and the vertices in {@code vertexOrder} are in
     * perfect elimination order.
     */
    private boolean isPerfectEliminationOrder(List<V> vertexOrder, Map<V, Integer> map) {
        for (V vertex : vertexOrder) {
            Set<V> predecessors = getPredecessors(map, vertex);
            if (predecessors.size() > 0) {
                V maxPredecessor = Collections.max(predecessors, Comparator.comparingInt(map::get));
                for (V predecessor : predecessors) {
                    if (!predecessor.equals(maxPredecessor) && !graph.containsEdge(predecessor, maxPredecessor)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    /**
     * Returns the predecessors of {@code vertex} in the order defined by {@code map}. More precisely,
     * returns those of {@code vertex}, whose mapped index in {@code map} is less then the index of {@code vertex}.
     *
     * @param map    defines the mapping of vertices in {@code graph} to their indices in order.
     * @param vertex the vertex whose predecessors in order are to be returned.
     * @return the predecessors of {@code vertex} in order defines by {@code map}.
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


