/*
 * (C) Copyright 2013-2018, by Alexey Kudinkin and Contributors.
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
package org.jgrapht.alg.spanning;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;

import java.util.*;

/**
 * An implementation of <a href="http://en.wikipedia.org/wiki/Prim's_algorithm"> Prim's
 * algorithm</a> that finds a minimum spanning tree/forest subject to connectivity of the supplied
 * weighted undirected graph. The algorithm was developed by Czech mathematician V. Jarník and later
 * independently by computer scientist Robert C. Prim and rediscovered by E. Dijkstra.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Alexey Kudinkin, Alexandru Valeanu
 * @since Mar 5, 2013
 */
public class PrimMinimumSpanningTree<V, E>
    implements SpanningTreeAlgorithm<E>
{
    private final Graph<V, E> g;

    /**
     * Construct a new instance of the algorithm.
     * 
     * @param graph the input graph
     */
    public PrimMinimumSpanningTree(Graph<V, E> graph)
    {
        this.g = Objects.requireNonNull(graph, "Graph cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpanningTree<E> getSpanningTree()
    {
        Set<E> minimumSpanningTreeEdgeSet = new HashSet<>(g.vertexSet().size());
        double spanningTreeWeight = 0d;

        Set<V> unspanned = new HashSet<>(g.vertexSet());

        while (!unspanned.isEmpty()) {
            Iterator<V> ri = unspanned.iterator();

            V root = ri.next();

            ri.remove();

            // Edges crossing the cut C = (S, V \ S), where S is set of
            // already spanned vertices

            PriorityQueue<E> dangling = new PriorityQueue<>(
                g.edgeSet().size(),
                    Comparator.comparingDouble(g::getEdgeWeight));

            dangling.addAll(g.edgesOf(root));

            for (E next; (next = dangling.poll()) != null;) {
                V s, t = unspanned.contains(s = g.getEdgeSource(next)) ? s : g.getEdgeTarget(next);

                // Decayed edges aren't removed from priority-queue so that
                // having them just ignored being encountered through min-max
                // traversal
                if (!unspanned.contains(t)) {
                    continue;
                }

                minimumSpanningTreeEdgeSet.add(next);
                spanningTreeWeight += g.getEdgeWeight(next);

                unspanned.remove(t);

                for (E e : g.edgesOf(t)) {
                    if (unspanned.contains(
                        g.getEdgeSource(e).equals(t) ? g.getEdgeTarget(e) : g.getEdgeSource(e)))
                    {
                        dangling.add(e);
                    }
                }
            }
        }

        return new SpanningTreeImpl<>(minimumSpanningTreeEdgeSet, spanningTreeWeight);
    }


    /**
     * Computes a spanning tree in $O(|V|^2)$ time.
     *
     * Note: This method is only recommended for dense graphs.
     *
     * @return a spanning tree
     */
    @SuppressWarnings("unchecked")
    public SpanningTree<E> getSpanningTreeDense(){
        Set<E> minimumSpanningTreeEdgeSet = new HashSet<>(g.vertexSet().size());
        double spanningTreeWeight = 0d;

        final int N = g.vertexSet().size();

        /*
         * Normalize the graph
         *   map each vertex to an integer (using a HashMap)
         *   keep the reverse mapping  (using an ArrayList)
         */
        Map<V, Integer> vertexMap = new HashMap<>();
        List<V> indexList = new ArrayList<>();
        for(V v : g.vertexSet()){
            vertexMap.put(v,vertexMap.size());
            indexList.add(v);
        }

        boolean[] spanned = new boolean[N];
        double[] distance = new double[N];
        E[] edgeFromParent = (E[]) new Object[N];

        Arrays.fill(distance, Double.MAX_VALUE);
        distance[0] = 0;

        for (int step = 0; step < N; step++) {
            int u = -1;

            for (int i = 0; i < N; i++) {
                if (!spanned[i] && (u == -1 || distance[i] < distance[u]))
                    u = i;
            }

            if (u == -1)
                break;


            V root = indexList.get(u);
            spanned[u] = true;

            if (edgeFromParent[u] != null) {
                minimumSpanningTreeEdgeSet.add(edgeFromParent[u]);
                spanningTreeWeight += g.getEdgeWeight(edgeFromParent[u]);
            }

            for (E e : g.edgesOf(root)) {
                V target = g.getEdgeTarget(e);

                if (target.equals(root))
                    target = g.getEdgeSource(e);

                int id = vertexMap.get(target);
                double cost = g.getEdgeWeight(e);

                if (!spanned[id] && distance[id] > cost) {
                    distance[id] = cost;
                    edgeFromParent[id] = e;
                }
            }
        }

        return new SpanningTreeImpl<>(minimumSpanningTreeEdgeSet, spanningTreeWeight);
    }
}

// End PrimMinimumSpanningTree.java
