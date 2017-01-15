/*
 * (C) Copyright 2003-2017, by John V Sichi, Dimitrios Michail and Contributors.
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
package org.jgrapht.alg.shortestpath;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.*;
import org.jgrapht.alg.util.*;
import org.jgrapht.util.*;

/**
 * A light-weight version of the closest-first iterator for a directed or undirected graphs. For
 * this iterator to work correctly the graph must not be modified during iteration. Currently there
 * are no means to ensure that, nor to fail-fast. The results of such modifications are undefined.
 * 
 * <p>
 * The metric for <i>closest</i> here is the weighted path length from a start vertex, i.e.
 * Graph.getEdgeWeight(Edge) is summed to calculate path length. Negative edge weights will result
 * in an IllegalArgumentException. Optionally, path length may be bounded by a finite radius.
 * 
 * <p>
 * NOTE: This is an internal iterator for use in shortest paths algorithms. For an iterator that is
 * suitable to return to the users see {@link org.jgrapht.traverse.ClosestFirstIterator}. This
 * implementation is must faster since it does not support graph traversal listeners nor
 * disconnected components.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author John V. Sichi
 * @author Dimitrios Michail
 */
class DijkstraClosestFirstIterator<V, E>
    implements Iterator<V>
{
    private final Graph<V, E> graph;
    private final V source;
    private final double radius;
    private final Specifics specifics;
    private final FibonacciHeap<QueueEntry> heap;
    private final Map<V, FibonacciHeapNode<QueueEntry>> seen;

    /**
     * Creates a new iterator for the specified graph. Iteration will start at the specified start
     * vertex and will be limited to the connected component that includes that vertex.
     * 
     * @param graph the graph to be iterated.
     * @param source the source vertex
     */
    public DijkstraClosestFirstIterator(Graph<V, E> graph, V source)
    {
        this(graph, source, Double.POSITIVE_INFINITY);
    }

    /**
     * Creates a new radius-bounded iterator for the specified graph. Iteration will start at the
     * specified start vertex and will be limited to the subset of the connected component which
     * includes that vertex and is reachable via paths of weighted length less than or equal to the
     * specified radius.
     *
     * @param graph the graph
     * @param source the source vertex
     * @param radius limit on weighted path length, or Double.POSITIVE_INFINITY for unbounded search
     */
    public DijkstraClosestFirstIterator(Graph<V, E> graph, V source, double radius)
    {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        this.source = Objects.requireNonNull(source, "Sourve vertex cannot be null");
        if (radius < 0.0) {
            throw new IllegalArgumentException("Radius must be non-negative");
        }
        this.radius = radius;
        if (graph instanceof DirectedGraph) {
            this.specifics = new DirectedSpecifics((DirectedGraph<V, E>) graph);
        } else {
            this.specifics = new UndirectedSpecifics(graph);
        }
        this.heap = new FibonacciHeap<>();
        this.seen = new HashMap<>();

        // initialize with source vertex
        updateDistance(source, null, 0d);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext()
    {
        if (heap.isEmpty()) {
            return false;
        }
        FibonacciHeapNode<QueueEntry> vNode = heap.min();
        double vDistance = vNode.getKey();
        if (radius < vDistance) {
            heap.clear();
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V next()
    {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        // settle next node
        FibonacciHeapNode<QueueEntry> vNode = heap.removeMin();
        V v = vNode.getData().v;
        double vDistance = vNode.getKey();

        // relax edges
        for (E e : specifics.edgesOf(v)) {
            V u = Graphs.getOppositeVertex(graph, e, v);
            double eWeight = graph.getEdgeWeight(e);
            if (eWeight < 0.0) {
                throw new IllegalArgumentException("Negative edge weight not allowed");
            }
            updateDistance(u, e, vDistance + eWeight);
        }

        return v;
    }

    /**
     * Return the paths computed by this iterator. Only the paths to vertices which are already
     * returned by the iterator will be shortest paths. Additional paths to vertices which are not
     * yet returned (settled) by the iterator might be included with the following properties: the
     * distance will be an upper bound on the actual shortest path and the distance will be inside
     * the radius of the search.
     * 
     * @return the single source paths
     */
    public SingleSourcePaths<V, E> getPaths()
    {
        Map<V, Pair<Double, E>> distanceAndPredecessorMap = new HashMap<>();

        for (FibonacciHeapNode<QueueEntry> vNode : seen.values()) {
            double vDistance = vNode.getKey();
            if (radius < vDistance) {
                continue;
            }
            V v = vNode.getData().v;
            distanceAndPredecessorMap.put(v, Pair.of(vDistance, vNode.getData().e));
        }

        return new TreeSingleSourcePathsImpl<>(graph, source, distanceAndPredecessorMap);
    }

    private void updateDistance(V v, E e, double distance)
    {
        FibonacciHeapNode<QueueEntry> node = seen.get(v);
        if (node == null) {
            node = new FibonacciHeapNode<>(new QueueEntry(e, v));
            heap.insert(node, distance);
            seen.put(v, node);
        } else {
            if (distance < node.getKey()) {
                heap.decreaseKey(node, distance);
                node.getData().e = e;
            }
        }
    }

    abstract class Specifics
    {
        public abstract Set<? extends E> edgesOf(V vertex);
    }

    class DirectedSpecifics
        extends Specifics
    {

        private DirectedGraph<V, E> graph;

        public DirectedSpecifics(DirectedGraph<V, E> g)
        {
            graph = g;
        }

        @Override
        public Set<? extends E> edgesOf(V vertex)
        {
            return graph.outgoingEdgesOf(vertex);
        }
    }

    class UndirectedSpecifics
        extends Specifics
    {

        private Graph<V, E> graph;

        public UndirectedSpecifics(Graph<V, E> g)
        {
            graph = g;
        }

        @Override
        public Set<E> edgesOf(V vertex)
        {
            return graph.edgesOf(vertex);
        }
    }

    class QueueEntry
    {
        E e;
        V v;

        public QueueEntry(E e, V v)
        {
            this.e = e;
            this.v = v;
        }
    }
}
