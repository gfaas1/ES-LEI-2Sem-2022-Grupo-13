/*
 * (C) Copyright 2003-2017, by Barak Naveh, Dimitrios Michail and Contributors.
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
package org.jgrapht;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;

import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.KosarajuStrongConnectivityInspector;
import org.jgrapht.alg.cycle.HierholzerEulerianCycle;
import org.jgrapht.graph.AbstractBaseGraph;

/**
 * A collection of utilities to test for various graph properties.
 * 
 * @author Barak Naveh
 * @author Dimitrios Michail
 */
public abstract class GraphTests
{

    /**
     * Test whether a graph is empty. An empty graph on n nodes consists of n isolated vertices with
     * no edges.
     * 
     * @param graph the input graph
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     * @return true if the graph is empty, false otherwise
     */
    public static <V, E> boolean isEmpty(Graph<V, E> graph)
    {
        Objects.requireNonNull(graph, "Graph cannot be null");
        return graph.edgeSet().isEmpty();
    }

    /**
     * Check if a graph is simple. A graph is simple if it has no self-loops and multiple edges.
     * 
     * @param graph a graph
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     * @return true if a graph is simple, false otherwise
     */
    public static <V, E> boolean isSimple(Graph<V, E> graph)
    {
        Objects.requireNonNull(graph, "Graph cannot be null");
        if (graph instanceof AbstractBaseGraph<?, ?>) {
            AbstractBaseGraph<V, E> abg = (AbstractBaseGraph<V, E>) graph;
            if (!abg.isAllowingLoops() && !abg.isAllowingMultipleEdges()) {
                return true;
            }
        }
        // no luck, we have to check
        boolean isDirected = graph instanceof DirectedGraph<?, ?>;
        for (V v : graph.vertexSet()) {
            Iterable<E> edgesOf;
            if (isDirected) {
                edgesOf = ((DirectedGraph<V, E>) graph).outgoingEdgesOf(v);
            } else {
                edgesOf = graph.edgesOf(v);
            }
            Set<V> neighbors = new HashSet<>();
            for (E e : edgesOf) {
                V u = Graphs.getOppositeVertex(graph, e, v);
                if (u.equals(v) || !neighbors.add(u)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Test whether a graph is complete. A complete undirected graph is a simple graph in which
     * every pair of distinct vertices is connected by a unique edge. A complete directed graph is a
     * directed graph in which every pair of distinct vertices is connected by a pair of unique
     * edges (one in each direction).
     * 
     * @param graph the input graph
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     * @return true if the graph is complete, false otherwise
     */
    public static <V, E> boolean isComplete(Graph<V, E> graph)
    {
        Objects.requireNonNull(graph, "Graph cannot be null");
        int n = graph.vertexSet().size();
        int allEdges;
        if (graph instanceof DirectedGraph<?, ?>) {
            allEdges = Math.multiplyExact(n, n - 1);
        } else if (graph instanceof UndirectedGraph<?, ?>) {
            if (n % 2 == 0) {
                allEdges = Math.multiplyExact(n / 2, n - 1);
            } else {
                allEdges = Math.multiplyExact(n, (n - 1) / 2);
            }
        } else {
            throw new IllegalArgumentException("Graph must be directed or undirected");
        }
        return graph.edgeSet().size() == allEdges && isSimple(graph);
    }

    /**
     * Test whether an undirected graph is connected.
     * 
     * <p>
     * This method does not performing any caching, instead recomputes everything from scratch. In
     * case more control is required use {@link ConnectivityInspector} directly.
     *
     * @param graph the input graph
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     * @return true if the graph is connected, false otherwise
     * @see ConnectivityInspector
     */
    public static <V, E> boolean isConnected(UndirectedGraph<V, E> graph)
    {
        Objects.requireNonNull(graph, "Graph cannot be null");
        return new ConnectivityInspector<>(graph).isGraphConnected();
    }

    /**
     * Test whether a directed graph is weakly connected.
     * 
     * <p>
     * This method does not performing any caching, instead recomputes everything from scratch. In
     * case more control is required use {@link ConnectivityInspector} directly.
     *
     * @param graph the input graph
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     * @return true if the graph is weakly connected, false otherwise
     * @see ConnectivityInspector
     */
    public static <V, E> boolean isWeaklyConnected(DirectedGraph<V, E> graph)
    {
        Objects.requireNonNull(graph, "Graph cannot be null");
        return new ConnectivityInspector<>(graph).isGraphConnected();
    }

    /**
     * Test whether a directed graph is strongly connected.
     * 
     * <p>
     * This method does not performing any caching, instead recomputes everything from scratch. In
     * case more control is required use {@link KosarajuStrongConnectivityInspector} directly.
     *
     * @param graph the input graph
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     * @return true if the graph is strongly connected, false otherwise
     * @see KosarajuStrongConnectivityInspector
     */
    public static <V, E> boolean isStronglyConnected(DirectedGraph<V, E> graph)
    {
        Objects.requireNonNull(graph, "Graph cannot be null");
        return new KosarajuStrongConnectivityInspector<>(graph).isStronglyConnected();
    }

    /**
     * Test whether an undirected graph is a tree.
     *
     * @param graph the input graph
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     * @return true if the graph is tree, false otherwise
     */
    public static <V, E> boolean isTree(UndirectedGraph<V, E> graph)
    {
        return (graph.edgeSet().size() == (graph.vertexSet().size() - 1)) && isConnected(graph);
    }

    /**
     * Test whether a graph is bipartite.
     * 
     * @param graph the input graph
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     * @return true if the graph is bipartite, false otherwise
     */
    public static <V, E> boolean isBipartite(Graph<V, E> graph)
    {
        if (isEmpty(graph)) {
            return true;
        }
        try {
            // at most n^2/4 edges
            if (Math.multiplyExact(4, graph.edgeSet().size()) > Math
                .multiplyExact(graph.vertexSet().size(), graph.vertexSet().size()))
            {
                return false;
            }
        } catch (ArithmeticException e) {
            // ignore
        }

        Set<V> unknown = new HashSet<>(graph.vertexSet());
        Set<V> odd = new HashSet<>();
        Deque<V> queue = new LinkedList<>();

        while (!unknown.isEmpty()) {
            if (queue.isEmpty()) {
                queue.add(unknown.iterator().next());
            }

            V v = queue.removeFirst();
            unknown.remove(v);

            for (E e : graph.edgesOf(v)) {
                V n = Graphs.getOppositeVertex(graph, e, v);
                if (unknown.contains(n)) {
                    queue.add(n);
                    if (!odd.contains(v)) {
                        odd.add(n);
                    }
                } else if (!(odd.contains(v) ^ odd.contains(n))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Test whether a partition of the vertices into two sets is a bipartite partition.
     * 
     * @param graph the input graph
     * @param firstPartition the first vertices partition
     * @param secondPartition the second vertices partition
     * @return true if the partition is a bipartite partition, false otherwise
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     */
    public static <V, E> boolean isBipartitePartition(
        Graph<V, E> graph, Set<? extends V> firstPartition, Set<? extends V> secondPartition)
    {
        Objects.requireNonNull(graph, "Graph cannot be null");
        Objects.requireNonNull(firstPartition, "First partition cannot be null");
        Objects.requireNonNull(secondPartition, "Second partition cannot be null");

        if (graph.vertexSet().size() != firstPartition.size() + secondPartition.size()) {
            return false;
        }

        for (V v : graph.vertexSet()) {
            Set<? extends V> otherPartition;
            if (firstPartition.contains(v)) {
                otherPartition = secondPartition;
            } else if (secondPartition.contains(v)) {
                otherPartition = firstPartition;
            } else {
                // v does not belong to any of the two partitions
                return false;
            }

            for (E e : graph.edgesOf(v)) {
                V other = Graphs.getOppositeVertex(graph, e, v);
                if (!otherPartition.contains(other)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Test whether a graph is Eulerian. An undirected graph is Eulerian if it is connected and each
     * vertex has an even degree. A directed graph is Eulerian if it is strongly connected and each
     * vertex has the same incoming and outgoing degree. Test whether a graph is Eulerian. An
     * <a href="http://mathworld.wolfram.com/EulerianGraph.html">Eulerian graph</a> is a graph
     * containing an <a href="http://mathworld.wolfram.com/EulerianCycle.html">Eulerian cycle</a>.
     *
     * @param graph the input graph
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     *
     * @return true if the graph is Eulerian, false otherwise
     * @see HierholzerEulerianCycle#isEulerian(Graph)
     */
    public static <V, E> boolean isEulerian(Graph<V, E> graph)
    {
        Objects.requireNonNull(graph, "Graph cannot be null");
        return new HierholzerEulerianCycle<V, E>().isEulerian(graph);
    }

}

// End GraphTests.java
