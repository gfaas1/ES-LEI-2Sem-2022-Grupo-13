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
    /* Map defining the predecessors of the odd nodes */
    private Map<Integer, Integer> predOdd;
    private Map<Integer, Integer> predEven;

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
        predOdd = new HashMap<>();
        predEven = new HashMap<>();
        contracted = new HashMap<>();
        uf = new UnionFind<>(vertexIndexMap.values());

        if(initializer != null)
            this.warmStart(initializer);

        for (int v : vertexIndexMap.values()) {

            //If the matching matches |V|-1 vertices, we are done
//            if(match.size() >= vertices.size()-1)
//                break;

            System.out.println("\ngrowing path from "+v);
            // Any augmenting predOdd should start with _exposed_ vertex
            // (vertex may not escape match-set being added once)
            if (!match.containsKey(v)) {
                // Match is maximal iff graph G contains no more augmenting paths
                int w = findPath(v);
                if(w != nil) {
                    System.out.println("found augmenting path ending in: "+w);
                    System.out.println("predOdd: "+predOdd);
                    System.out.println("predEven: "+predEven);
                    while (w != nil) {
                        int pv = predOdd.get(w);
                        System.out.println("pv: "+pv);
                        //int ppv = match.getOrDefault(pv, nil);//match.get(pv);
                        int ppv = predEven.get(pv);
                        System.out.println("ppv: "+ppv);
                        System.out.println("matched edge: ("+w+","+pv+")");
                        match.put(w, pv);
                        match.put(pv, w);
                        w = ppv;
                        if(pv==ppv)
                            break;
                    }
                }
            }else{
                System.out.println("already matched. skipping: "+v);
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
        predOdd.clear();
        predEven.clear();
        contracted.clear();
        uf.reset();
        predEven.put(root, root);
        System.out.println("uf: "+uf);

//        vertexIndexMap.values().forEach(vertex -> contracted.put(vertex, vertex));

        used.add(root);
        q.add(root);

        while (!q.isEmpty()) {
            int v = q.remove();

            for (V neighbor : Graphs.neighborListOf(graph, vertices.get(v))) {
                int w = vertexIndexMap.get(neighbor);

//                if (contracted.get(v) == contracted.get(to) || (match.containsKey(v) && to == match.get(v)))
//                    continue;
                System.out.println("checking edge ("+v+","+w+")");
                //If edge (v,w) is part of a blossom, it is shrunken away and we can ignore it.
                //If w is odd, we can ignore the edge as well.
                if (uf.connected(v, w) || (predEven.containsKey(v) && w == predEven.get(v))) { //I think this could be: predOdd.containsKey(w)
                    System.out.println("ignore edge");
                    continue;
                }

                // Check whether we've hit a 'blossom'
                if ((w == root)
                    || ((predEven.containsKey(w)) && (predOdd.containsKey(predEven.get(w)))))
                {
                    System.out.println("found blossom. Search stem");
                    int stem = lowestCommonAncestor(v, w, root);
                    System.out.println("stem: "+stem);

                    Set<Integer> blossom = new HashSet<>();
                    markPath(v, w, stem, blossom);
                    markPath(w, v, stem, blossom);
                    System.out.println("blossom: "+blossom);

                    vertexIndexMap.values().stream()
                        .filter(
//                            i -> blossom.contains(contracted.get(i)))
                                i -> blossom.contains(uf.find(i)))
                        .forEach(i -> {
//                            contracted.put(i, stem);
                            //uf.union(i, stem);
                            uf.union(stem, i);
                            if (!used.contains(i)) {
                                used.add(i);
                                q.add(i);
                            }
                        });
                    System.out.println("uf: "+uf);
//                    predEven.put(uf.find(stem), predEven.get(stem));
                    //even[uf.find(base)] = even[base];

                    System.out.println("predOdd: "+predOdd);
                    System.out.println("predEven: "+predEven);

                    // Check whether we've had hit a loop (of even length (!) presumably)
                } else if (!predOdd.containsKey(w)) {
                    predOdd.put(w, v);

                    if (!match.containsKey(w)) {
                        System.out.println("found augmenting path after adding edge ("+v+","+w+")");
                        return w;
                    }

                    System.out.println("growing tree ("+v+","+w+","+match.get(w)+")");
                    int x = match.get(w);
                    predEven.put(x, w);

                    System.out.println("predOdd: "+predOdd);
                    System.out.println("predEven: "+predEven);

                    used.add(x);
                    q.add(x);
                }
            }
        }
        System.out.println("No augmenting path found");
        return nil;
    }

    private void markPath(int v, int child, int stem, Set<Integer> blossom)
    {
        System.out.println("markPath. v: "+v+" child: "+child);

//        while (!contracted.get(v).equals(stem)) {
        while (uf.find(v)!= stem) {
            blossom.add(uf.find(v));
            blossom.add(uf.find(match.get(v)));
            predOdd.put(v, child);
            System.out.println("predOdd["+v+"]="+child);
            child = match.get(v);
            predEven.put(child, v);
            System.out.println("predEven["+child+"]="+v);
            v = predOdd.get(child);
        }
    }

//    private int lowestCommonAncestor(int v, int w, int root)
//    {
//        BitSet seen=new BitSet(vertices.size());
//        for (;;) {
//            //a = contracted.get(a);
//            v = uf.find(v);
////            System.out.println("lc a: "+a+" expected: "+contracted.get(a));
//            seen.set(v);
//            if (!match.containsKey(v)) //We've reached the root of the tree
//                break;
//            v = predOdd.get(match.get(v));
//        }
//        for (;;) {
////            b = contracted.get(b);
//            w=uf.find(w);
//            if (seen.get(w))
//                return w;
//            w = predOdd.get(match.get(w));
//        }
//    }

    private int lowestCommonAncestor(int v, int w, int root)
    {
        System.out.println("lca. v: "+v+" w: "+w);
        BitSet seen=new BitSet(vertices.size());
        for (;;) {
            //a = contracted.get(a);
            v = uf.find(v);
//            System.out.println("lc a: "+a+" expected: "+contracted.get(a));
            seen.set(v);
            System.out.println("seen.add: "+v);
            int parent = uf.find(predEven.get(v)); //If not matched, then we've reached the root of the tree
            if(parent == v)
                break; //root of tree
            v= predOdd.get(parent);

//            if (!match.containsKey(v)) //We've reached the root of the tree
//                break;
//            v = predOdd.get(match.get(v));
        }
//        System.out.println("w: "+w);
        for (;;) {
//            b = contracted.get(b);
            w=uf.find(w);
//            System.out.println("uf.find(w): "+w);
            if (seen.get(w))
                return w;
            w = predOdd.get(predEven.get(w));
        }
    }


//    private int parent(BitSet ancestors, int curr) {
//        curr = uf.find(curr);
//        ancestors.set(curr);
//        int parent = uf.find(even[curr]);
//        if (parent == curr)
//            return curr; // root of tree
//        ancestors.set(parent);
//        return uf.find(odd[parent]);
//    }
}

// End EdmondsBlossomShrinking.java
