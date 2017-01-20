/*
 * (C) Copyright 2017-2017, by Dimitrios Michail and Contributors.
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
package org.jgrapht.alg.color;

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;

/**
 * The Dsatur greedy coloring algorithm.
 * 
 * <p>
 * This is the greedy coloring algorithm using saturation degree ordering. The saturation degree of
 * a vertex is defined as the number of different colors to which it is adjacent. The algorithm
 * selects always the vertex with the largest saturation degree. If multiple vertices have the same
 * maximum saturation degree, a vertex of maximum degree in the uncolored subgraph is selected.
 *
 * See the following paper for details:
 * <ul>
 * <li>D. Brelaz. New methods to color the vertices of a graph. Communications of ACM,
 * 22(4):251–256, 1979.</li>
 * </ul>
 * 
 * <p>
 * Note that the DSatur is not optimal in general, but is optimal for bipartite graphs.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 * 
 * @author Dimitrios Michail
 */
public class SaturationDegreeColoring<V, E>
    implements VertexColoringAlgorithm<V>
{
    private final Graph<V, E> graph;

    /**
     * Construct a new coloring algorithm.
     * 
     * @param graph the input graph
     */
    public SaturationDegreeColoring(Graph<V, E> graph)
    {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Coloring<V> getColoring()
    {
        int n = graph.vertexSet().size();
        int maxColor = -1;
        Map<V, Integer> colors = new HashMap<>(n);
        Map<V, Set<Integer>> adjColors = new HashMap<>(n);

        /*
         * Compute degrees and the maximum degree.
         */
        int maxDegree = 0;
        Map<V, Integer> degree = new HashMap<>(n);
        Map<V, Integer> index = new HashMap<>(n);
        int i = 0;
        for (V v : graph.vertexSet()) {
            int d = graph.edgesOf(v).size();
            degree.put(v, d);
            if (d > maxDegree) {
                maxDegree = d;
            }
            index.put(v, i++);
            adjColors.put(v, new HashSet<>());
        }

        /*
         * Create and fill buckets with saturation degree as index. Each bucket contains vertices
         * ordered by degree in non-increasing order. Therefore, any updates in the degree map for
         * some vertex v must be performed only if v does not belong to any bucket.
         */
        final NavigableSet<V>[] buckets =
            (NavigableSet<V>[]) Array.newInstance(NavigableSet.class, maxDegree + 1);
        Comparator<V> degreeComparator = new DegreeComparator(index, degree);
        for (i = 0; i <= maxDegree; i++) {
            buckets[i] = new TreeSet<>(degreeComparator);
        }
        for (V v : graph.vertexSet()) {
            buckets[0].add(v);
        }

        /*
         * Extract from buckets
         */
        int maxSaturation = 0;
        while (maxSaturation >= 0) {
            NavigableSet<V> b = buckets[maxSaturation];

            if (b.isEmpty()) {
                maxSaturation--;
                continue;
            }

            // find next vertex
            V v = b.pollFirst();

            // find first free color
            Set<Integer> used = adjColors.get(v);
            int candidate = 0;
            while (used.contains(candidate)) {
                candidate++;
            }
            colors.put(v, candidate);
            if (candidate > maxColor) {
                maxColor = candidate;
            }

            // cleanup vertex
            degree.remove(v);
            adjColors.remove(v);

            // update neighbor saturation
            for (E e : graph.edgesOf(v)) {
                V u = Graphs.getOppositeVertex(graph, e, v);

                // if colored skip
                if (colors.containsKey(u)) {
                    continue;
                }

                Set<Integer> otherUsed = adjColors.get(u);

                // remove from saturation bucket
                int sat = otherUsed.size();
                buckets[sat].remove(u);

                // update colored subgraph degree
                degree.put(u, degree.get(u) - 1);

                // re-insert into saturation bucket
                if (!otherUsed.contains(candidate)) {
                    otherUsed.add(candidate);
                    buckets[sat + 1].add(u);

                    if (sat + 1 > maxSaturation) {
                        maxSaturation = sat + 1;
                    }
                } else {
                    buckets[sat].add(u);
                }
            }
        }

        return new ColoringImpl<>(colors, maxColor + 1);
    }

    /**
     * A vertex comparator based on vertex degree. We use lexicographic ordering to distinguish
     * vertices with the same degrees.
     */
    private class DegreeComparator
        implements Comparator<V>
    {
        private Map<V, Integer> index;
        private Map<V, Integer> degree;

        public DegreeComparator(Map<V, Integer> index, Map<V, Integer> degree)
        {
            this.index = index;
            this.degree = degree;
        }

        @Override
        public int compare(V o1, V o2)
        {
            int d1 = degree.get(o1);
            int d2 = degree.get(o2);
            if (d1 > d2) {
                return -1;
            } else if (d2 > d1) {
                return 1;
            } else {
                int i1 = index.get(o1);
                int i2 = index.get(o2);
                if (i1 < i2) {
                    return -1;
                } else if (i1 > i2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }

    }

}
