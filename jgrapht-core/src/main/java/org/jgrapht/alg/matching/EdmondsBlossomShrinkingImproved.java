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

import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.util.UnionFind;
import org.jgrapht.util.ArrayUnenforcedSet;

import java.util.*;

/**
 * An implementation of Edmonds Blossom Shrinking algorithm for constructing maximum matchings on
 * graphs. The algorithm runs in time O(V^4).
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Alejandro R. Lopez del Huerto
 * @since Jan 24, 2012
 */
public class EdmondsBlossomShrinkingImproved<V, E>
    implements MatchingAlgorithm<V, E>
{
    private final Graph<V, E> graph;

    /*
   All vertices in the original graph are mapped to a unique integer to simplify the implementation and to improve efficiency.
    */
    private List<V> vertices;
    private Map<V, Integer> vertexIndexMap;


    private final MatchingAlgorithm<V,E> initializer;
    private Map<Integer, Integer> match;
    private Map<Integer, Integer> path;

    /** Union-Find to store blossoms. */
    private UnionFind<Integer> uf;

    /** Special 'nil' vertex. */
    private static final int nil = -1;

    private Map<Integer, Integer> contracted;

    /**
     * Construct an instance of the Edmonds blossom shrinking algorithm.
     *
     * @param graph the input graph
     * @throws IllegalArgumentException if the graph is not undirected
     */
    public EdmondsBlossomShrinkingImproved(Graph<V, E> graph)
    {
        //this(graph, new GreedyMaxCardinalityMatching<V, E>(graph, false));
        this(graph, null);
    }

    public EdmondsBlossomShrinkingImproved(Graph<V, E> graph, MatchingAlgorithm<V,E> initializer)
    {
        this.graph = GraphTests.requireUndirected(graph);
        this.initializer=initializer;

        vertices=new ArrayList<>();
        vertices.addAll(graph.vertexSet());
        vertexIndexMap=new HashMap<>();
        for(int i=0; i<vertices.size(); i++)
            vertexIndexMap.put(vertices.get(i), i);
    }

    private void warmStart(MatchingAlgorithm<V,E> initializer){
        Matching<V,E> initialSolution=initializer.getMatching();
        System.out.println("warmstart: "+initialSolution.getWeight());
        for(E e : initialSolution.getEdges()){
            V u=graph.getEdgeSource(e);
            V v=graph.getEdgeTarget(e);
            match.put(vertexIndexMap.get(u), vertexIndexMap.get(v));
            match.put(vertexIndexMap.get(v), vertexIndexMap.get(u));
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
        uf = new UnionFind<>(vertexIndexMap.values());

        if(initializer != null)
            this.warmStart(initializer);

        for (int i : vertexIndexMap.values()) {
            // Any augmenting path should start with _exposed_ vertex
            // (vertex may not escape match-set being added once)
            if (!match.containsKey(i)) {
                // Match is maximal iff graph G contains no more augmenting paths
                int v = findPath(i);
                if(v != nil) {
                    while (v != nil) {
                        int pv = path.get(v);
                        int ppv = match.getOrDefault(pv, nil);//match.get(pv);
                        match.put(v, pv);
                        match.put(pv, v);
                        v = ppv;
                    }
                }
            }
        }

        Set<Integer> seen = new HashSet<>();
        vertexIndexMap.values().stream().filter(v -> !seen.contains(v) && match.containsKey(v)).forEach(
            v -> {
                seen.add(v);
                seen.add(match.get(v));
                edges.add(graph.getEdge(vertices.get(v), vertices.get(match.get(v))));
            });

        return new MatchingImpl<>(graph, edges, edges.size());
    }

    private int findPath(int root)
    {
        System.out.println("find path");
        Set<Integer> used = new HashSet<>();
        Queue<Integer> q = new ArrayDeque<>();

        // Expand graph back from its contracted state
        path.clear();
        contracted.clear();
        uf.clear();

//        vertexIndexMap.values().forEach(vertex -> contracted.put(vertex, vertex));

        used.add(root);
        q.add(root);

        while (!q.isEmpty()) {
            int v = q.remove();
            System.out.println("remove q");

            for (V w : Graphs.neighborListOf(graph, vertices.get(v))) {
                int to = vertexIndexMap.get(w);

//                if (contracted.get(v) == contracted.get(to) || (match.containsKey(v) && to == match.get(v)))
//                    continue;
                System.out.println("point 1");
                if (uf.find(v).equals(uf.find(to)) || (match.containsKey(v) && to == match.get(v)))
                    continue;

                System.out.println("point 2");

                // Check whether we've hit a 'blossom'
                if ((to == root)
                    || ((match.containsKey(to)) && (path.containsKey(match.get(to)))))
                {
                    System.out.println("start lca");
                    int stem = lowestCommonAncestor(v, to, root);
                    System.out.println("end lca");

                    Set<Integer> blossom = new HashSet<>();

                    System.out.println("point 3a");
                    markPath(v, to, stem, blossom);
                    markPath(to, v, stem, blossom);

                    System.out.println("start1");
                    vertexIndexMap.values().stream()
                        .filter(
//                            i -> blossom.contains(contracted.get(i)))
                                i -> blossom.contains(uf.find(i)))
                        .forEach(i -> {
                            contracted.put(i, stem);
                            uf.union(stem, i);
                            if (!used.contains(i)) {
                                used.add(i);
                                q.add(i);
                            }
                        });
                    System.out.println("end1");

                    // Check whether we've had hit a loop (of even length (!) presumably)
                } else if (!path.containsKey(to)) {
                    System.out.println("point 3b");
                    path.put(to, v);

                    if (!match.containsKey(to)) {
                        return to;
                    }

                    to = match.get(to);

                    used.add(to);
                    q.add(to);
                }
                System.out.println("point 4");
            }
        }
        return nil;
    }

    private void markPath(int v, int child, int stem, Set<Integer> blossom)
    {
        System.out.println("start mark path");
//        while (!contracted.get(v).equals(stem)) {
        while (uf.find(v)!= stem) {
            //blossom.add(contracted.get(v));
            blossom.add(uf.find(v));
//            blossom.add(contracted.get(match.get(v)));
            blossom.add(uf.find(match.get(v)));
            path.put(v, child);
            child = match.get(v);
            v = path.get(child);
        }
        System.out.println("end mark path");
    }

    private int lowestCommonAncestor(int a, int b, int root)
    {
        BitSet seen=new BitSet(vertices.size());
        for (;;) {
            //a = contracted.get(a);
            a = uf.find(a);
//            System.out.println("lc a: "+a+" expected: "+contracted.get(a));
            seen.set(a);
            if (!match.containsKey(a)) //We've reached the root of the tree
                break;
            a = path.get(match.get(a));
        }
        for (;;) {
//            b = contracted.get(b);
            b=uf.find(b);
            if (seen.get(b))
                return b;
            b = path.get(match.get(b));
        }
    }
}

// End EdmondsBlossomShrinking.java
