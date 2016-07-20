/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
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
/* -------------------------
* BidirectionalDijkstraShortestPath.java
* -------------------------
* (C) Copyright 2016, by Dimitrios Michail and Contributors.
*
* Original Author:  Dimitrios Michail
* Contributor(s):   - 
*
* $Id$
*
* Changes
* -------
* 19-July-2016: Initial revision (DM);
*
*/
package org.jgrapht.alg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.util.FibonacciHeap;
import org.jgrapht.util.FibonacciHeapNode;

/**
 * A bidirectional version of Dijkstra's algorithm.
 * 
 * <p>
 * See the Wikipedia article for details and references about
 * <a href="https://en.wikipedia.org/wiki/Bidirectional_search">bidirectional
 * search</a>. This technique does not change the worst-case behavior of the
 * algorithm but reduces, in some cases, the number of visited vertices in
 * practice. This implementation alternatively constructs forward and reverse
 * paths from the source and target vertices respectively.
 * </p>
 * 
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @see DijkstraShortestPath
 *
 * @author Dimitrios Michail
 * @since July 2016
 */
public final class BidirectionalDijkstraShortestPath<V, E>
{

    private final GraphPath<V, E> path;

    /**
     * Creates the instance and executes the bidirectional Dijkstra shortest
     * path algorithm. An instance is only good for a single search; after
     * construction, it can be accessed to retrieve information about the found
     * path.
     *
     * @param graph the input graph
     * @param startVertex the vertex at which the path should start
     * @param endVertex the vertex at which the path should end
     */
    public BidirectionalDijkstraShortestPath(
        Graph<V, E> graph,
        V startVertex,
        V endVertex)
    {
        this(graph, startVertex, endVertex, Double.POSITIVE_INFINITY);
    }

    /**
     * Creates the instance and executes the bidirectional Dijkstra shortest
     * path algorithm. An instance is only good for a single search; after
     * construction, it can be accessed to retrieve information about the found
     * path.
     *
     * @param graph the input graph
     * @param startVertex the vertex at which the path should start
     * @param endVertex the vertex at which the path should end
     * @param radius limit on weighted path length, or Double.POSITIVE_INFINITY
     *        for unbounded search
     */
    public BidirectionalDijkstraShortestPath(
        Graph<V, E> graph,
        V startVertex,
        V endVertex,
        double radius)
    {
        if (graph == null) {
            throw new IllegalArgumentException("Input graph cannot be null");
        }
        if (startVertex == null || !graph.containsVertex(startVertex)) {
            throw new IllegalArgumentException(
                "Invalid graph vertex as source");
        }
        if (endVertex == null || !graph.containsVertex(endVertex)) {
            throw new IllegalArgumentException(
                "Invalid graph vertex as target");
        }
        if (radius < 0.0) {
            throw new IllegalArgumentException("Radius must be non-negative");
        }

        this.path = new BidirectionalDijkstraShortestPathDetails<V, E>(
            graph,
            startVertex,
            endVertex,
            radius).run();
    }

    /**
     * Return the edges making up the path.
     *
     * @return List of edges, or null if no path exists
     */
    public List<E> getPathEdgeList()
    {
        if (path == null) {
            return null;
        } else {
            return path.getEdgeList();
        }
    }

    /**
     * Return the path found.
     *
     * @return path representation, or null if no path exists
     */
    public GraphPath<V, E> getPath()
    {
        return path;
    }

    /**
     * Return the weighted length of the path found.
     *
     * @return path length, or Double.POSITIVE_INFINITY if no path exists
     */
    public double getPathLength()
    {
        if (path == null) {
            return Double.POSITIVE_INFINITY;
        } else {
            return path.getWeight();
        }
    }

}

/**
 * The implementation details
 */
class BidirectionalDijkstraShortestPathDetails<V, E>
{
    private final SearchFrontier<V, E> forwardFrontier;
    private final SearchFrontier<V, E> reversedFrontier;

    private final V source;
    private final V target;
    private final double radius;

    public BidirectionalDijkstraShortestPathDetails(
        Graph<V, E> graph,
        V source,
        V target,
        double radius)
    {
        this.forwardFrontier = new SearchFrontier<V, E>(graph);
        if (graph instanceof DirectedGraph) {
            this.reversedFrontier = new SearchFrontier<V, E>(
                new EdgeReversedGraph<>(((DirectedGraph<V, E>) graph)));
        } else {
            this.reversedFrontier = new SearchFrontier<V, E>(graph);
        }
        this.source = source;
        this.target = target;
        this.radius = radius;
    }

    public GraphPath<V, E> run()
    {
        // handle special case if source equals target
        if (source.equals(target)) {
            return new GraphWalk<>(
                forwardFrontier.graph,
                source,
                target,
                Arrays.asList(source),
                new ArrayList<>(),
                0d);
        }

        assert !source.equals(target);

        // initialize both frontiers
        forwardFrontier.updateDistance(source, null, 0d);
        reversedFrontier.updateDistance(target, null, 0d);

        // initialize best path
        double bestPath = Double.POSITIVE_INFINITY;
        V bestPathCommonVertex = null;

        SearchFrontier<V, E> frontier = forwardFrontier;
        SearchFrontier<V, E> otherFrontier = reversedFrontier;

        while (true) {
            // stopping condition
            if (frontier.heap.isEmpty() || otherFrontier.heap.isEmpty()
                || frontier.heap.min().getKey()
                    + otherFrontier.heap.min().getKey() >= bestPath)
            {
                break;
            }

            // frontier scan
            FibonacciHeapNode<QueueEntry<V, E>> vNode = frontier.heap
                .removeMin();
            V v = vNode.getData().v;

            for (E e : frontier.specifics.edgesOf(v)) {
                double vDistance = vNode.getKey();
                V u = Graphs.getOppositeVertex(frontier.graph, e, v);
                frontier.updateDistance(
                    u,
                    e,
                    vDistance + frontier.graph.getEdgeWeight(e));

                // check if u has also been found in other frontier
                FibonacciHeapNode<QueueEntry<V, E>> uOtherNode = otherFrontier.seen
                    .get(u);
                if (uOtherNode != null) {
                    double pathDistance = vDistance
                        + frontier.graph.getEdgeWeight(e) + uOtherNode.getKey();
                    if (pathDistance < bestPath) {
                        bestPath = pathDistance;
                        bestPathCommonVertex = u;
                    }
                }

            }

            // swap frontiers
            SearchFrontier<V, E> tmpFrontier = frontier;
            frontier = otherFrontier;
            otherFrontier = tmpFrontier;

        }

        // create path if found
        if (Double.isFinite(bestPath) && bestPath <= radius) {
            return createPath(bestPath, bestPathCommonVertex);
        }

        return null;
    }

    private GraphPath<V, E> createPath(double weight, V commonVertex)
    {
        LinkedList<E> edgeList = new LinkedList<>();
        LinkedList<V> vertexList = new LinkedList<>();

        // add common vertex
        vertexList.add(commonVertex);

        // traverse forward path
        V v = commonVertex;
        while (true) {
            FibonacciHeapNode<QueueEntry<V, E>> node = forwardFrontier.seen
                .get(v);
            E e = node.getData().e;

            if (e == null) {
                break;
            }

            edgeList.addFirst(e);
            v = Graphs.getOppositeVertex(forwardFrontier.graph, e, v);
            vertexList.addFirst(v);
        }

        // traverse reverse path
        v = commonVertex;
        while (true) {
            FibonacciHeapNode<QueueEntry<V, E>> node = reversedFrontier.seen
                .get(v);
            E e = node.getData().e;

            if (e == null) {
                break;
            }

            edgeList.addLast(e);
            v = Graphs.getOppositeVertex(reversedFrontier.graph, e, v);
            vertexList.addLast(v);
        }

        return new GraphWalk<>(
            forwardFrontier.graph,
            source,
            target,
            vertexList,
            edgeList,
            weight);
    }

}

/**
 * Helper class to maintain the search frontier
 */
class SearchFrontier<V, E>
{
    final Graph<V, E> graph;
    final Specifics<V, E> specifics;

    final FibonacciHeap<QueueEntry<V, E>> heap;
    final Map<V, FibonacciHeapNode<QueueEntry<V, E>>> seen;

    public SearchFrontier(Graph<V, E> graph)
    {
        this.graph = graph;
        if (graph instanceof DirectedGraph) {
            this.specifics = new DirectedSpecifics<>(
                (DirectedGraph<V, E>) graph);
        } else {
            this.specifics = new UndirectedSpecifics<>(graph);
        }
        this.heap = new FibonacciHeap<>();
        this.seen = new HashMap<>();
    }

    public void updateDistance(V v, E e, double distance)
    {
        FibonacciHeapNode<QueueEntry<V, E>> node = seen.get(v);
        if (node == null) {
            node = new FibonacciHeapNode<>(new QueueEntry<>(e, v));
            heap.insert(node, distance);
            seen.put(v, node);
        } else {
            if (distance < node.getKey()) {
                heap.decreaseKey(node, distance);
                node.getData().e = e;
            }
        }
    }
}

/**
 * Provides unified interface for operations that are different in directed
 * graphs and in undirected graphs.
 */
abstract class Specifics<VV, EE>
{

    /**
     * Returns the edges outgoing from the specified vertex in case of directed
     * graph, and the edge touching the specified vertex in case of undirected
     * graph.
     *
     * @param vertex the vertex whose outgoing edges are to be returned.
     *
     * @return the edges outgoing from the specified vertex in case of directed
     *         graph, and the edge touching the specified vertex in case of
     *         undirected graph.
     */
    public abstract Set<? extends EE> edgesOf(VV vertex);
}

/**
 * An implementation of {@link Specifics} for a directed graph.
 */
class DirectedSpecifics<VV, EE>
    extends Specifics<VV, EE>
{

    private DirectedGraph<VV, EE> graph;

    /**
     * Creates a new DirectedSpecifics object.
     *
     * @param g the graph for which this specifics object to be created.
     */
    public DirectedSpecifics(DirectedGraph<VV, EE> g)
    {
        graph = g;
    }

    /**
     * @see CrossComponentIterator.Specifics#edgesOf(Object)
     */
    @Override
    public Set<? extends EE> edgesOf(VV vertex)
    {
        return graph.outgoingEdgesOf(vertex);
    }
}

/**
 * An implementation of {@link Specifics} in which edge direction (if any) is
 * ignored.
 */
class UndirectedSpecifics<VV, EE>
    extends Specifics<VV, EE>
{

    private Graph<VV, EE> graph;

    /**
     * Creates a new UndirectedSpecifics object.
     *
     * @param g the graph for which this specifics object to be created.
     */
    public UndirectedSpecifics(Graph<VV, EE> g)
    {
        graph = g;
    }

    /**
     * @see CrossComponentIterator.Specifics#edgesOf(Object)
     */
    @Override
    public Set<EE> edgesOf(VV vertex)
    {
        return graph.edgesOf(vertex);
    }
}

/**
 * Private data to associate with each entry in the priority queue.
 */
class QueueEntry<V, E>
{
    E e;
    V v;

    QueueEntry()
    {
    }

    public QueueEntry(E e, V v)
    {
        this.e = e;
        this.v = v;
    }
}

// End BidirectionalDijkstraShortestPath.java
