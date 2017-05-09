/*
 * (C) Copyright 2012-2017, by Alejandro Ramon Lopez del Huerto and Contributors.
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
package org.jgrapht.alg.matching;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.util.*;

/**
 * An implementation of Edmonds Blossom Shrinking algorithm for constructing maximum matchings on
 * graphs. The algorithm runs in time O(V^4).
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Alejandro R. Lopez del Huerto
 * @since Jan 24, 2012
 * @deprecated In favor of the more efficient {@link EdmondsMaximumCardinalityMatching} implementation.
 */
@Deprecated
public class EdmondsBlossomShrinking<V, E>
    implements MatchingAlgorithm<V, E>
{
    private final Graph<V, E> graph;
    private final MatchingAlgorithm<V,E> initializer;
    private Map<V, V> match;
    private Map<V, V> path;
    private Map<V, V> contracted;

    /**
     * Construct an instance of the Edmonds blossom shrinking algorithm.
     * 
     * @param graph the input graph
     * @throws IllegalArgumentException if the graph is not undirected
     */
    public EdmondsBlossomShrinking(Graph<V, E> graph)
    {
        this(graph, null);
    }

    public EdmondsBlossomShrinking(Graph<V, E> graph, MatchingAlgorithm<V,E> initializer)
    {
        this.graph = GraphTests.requireUndirected(graph);
        this.initializer=initializer;
    }

    private void warmStart(MatchingAlgorithm<V,E> initializer){
        Matching<V,E> initialSolution=initializer.getMatching();
        System.out.println("warmstart: "+initialSolution.getWeight());
        for(E e : initialSolution.getEdges()){
            V u=graph.getEdgeSource(e);
            V v=graph.getEdgeTarget(e);
            match.put(u, v);
            match.put(v, u);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Matching<V, E> getMatching()
    {
        Set<E> edges = new ArrayUnenforcedSet<>();
        match = new HashMap<>();
        path = new HashMap<>();
        contracted = new HashMap<>();

        if(initializer != null)
            this.warmStart(initializer);

        for (V i : graph.vertexSet()) {
            // Any augmenting path should start with _exposed_ vertex
            // (vertex may not escape match-set being added once)
            if (!match.containsKey(i)) {
                // Match is maximal iff graph G contains no more augmenting paths
                V v = findPath(i);
                while (v != null) {
                    V pv = path.get(v);
                    V ppv = match.get(pv);
                    match.put(v, pv);
                    match.put(pv, v);
                    v = ppv;
                }
            }
        }

        Set<V> seen = new HashSet<>();
        graph.vertexSet().stream().filter(v -> !seen.contains(v) && match.containsKey(v)).forEach(
            v -> {
                seen.add(v);
                seen.add(match.get(v));
                edges.add(graph.getEdge(v, match.get(v)));
            });

        return new MatchingImpl<>(graph, edges, edges.size());
    }

    private V findPath(V root)
    {
        Set<V> used = new HashSet<>();
        Queue<V> q = new ArrayDeque<>();

        // Expand graph back from its contracted state
        path.clear();
        contracted.clear();

        graph.vertexSet().forEach(vertex -> contracted.put(vertex, vertex));

        used.add(root);
        q.add(root);

        while (!q.isEmpty()) {
            V v = q.remove();

            for (V to : Graphs.neighborListOf(graph, v)) {

                if ((contracted.get(v).equals(contracted.get(to))) || to.equals(match.get(v)))
                    continue;

                // Check whether we've hit a 'blossom'
                if ((to.equals(root))
                    || ((match.containsKey(to)) && (path.containsKey(match.get(to)))))
                {
                    V stem = lowestCommonAncestor(v, to);

                    Set<V> blossom = new HashSet<>();

                    markPath(v, to, stem, blossom);
                    markPath(to, v, stem, blossom);

                    graph
                        .vertexSet().stream()
                        .filter(
                            i -> blossom.contains(contracted.get(i)))
                        .forEach(i -> {
                            contracted.put(i, stem);
                            if (!used.contains(i)) {
                                used.add(i);
                                q.add(i);
                            }
                        });

                    // Check whether we've had hit a loop (of even length (!) presumably)
                } else if (!path.containsKey(to)) {
                    path.put(to, v);

                    if (!match.containsKey(to)) {
                        return to;
                    }

                    to = match.get(to);

                    used.add(to);
                    q.add(to);
                }
            }
        }
        return null;
    }

    private void markPath(V v, V child, V stem, Set<V> blossom)
    {
        while (!contracted.get(v).equals(stem)) {
            blossom.add(contracted.get(v));
            blossom.add(contracted.get(match.get(v)));
            path.put(v, child);
            child = match.get(v);
            v = path.get(child);
        }
    }

    private V lowestCommonAncestor(V a, V b)
    {
        Set<V> seen = new HashSet<>();
        for (;;) {
            a = contracted.get(a);
            seen.add(a);
            if (!match.containsKey(a)) //We've reached the root of the tree
                break;
            a = path.get(match.get(a));
        }
        for (;;) {
            b = contracted.get(b);
            if (seen.contains(b))
                return b;
            b = path.get(match.get(b));
        }
    }
}

// End EdmondsBlossomShrinking.java
