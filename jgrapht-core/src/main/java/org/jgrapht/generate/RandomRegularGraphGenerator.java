/*
 * (C) Copyright 2018, by Emilio Cruciani and Contributors.
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

package org.jgrapht.generate;

import org.jgrapht.Graph;
import org.jgrapht.VertexFactory;

import java.util.*;

/**
 * Generate a random $d$-regular undirected graph with $n$ vertices.
 * A regular graph is a graph where each vertex has the same degree, i.e. the same number of neighbors.
 *
 * <p>
 * The algorithm for the simple case, proposed in [SW99] and extending the one for the non-simple case,
 * runs in expected $\mathcal{O}(nd^2)$ time.
 * It has been proved in [KV03] to sample from the space of random d-regular graphs
 * in a way which is asymptotically uniform at random when $d = \mathcal{O}(n^{1/3 - \epsilon})$.
 *
 * <p>
 * [KV03] Kim, Jeong Han, and Van H. Vu.
 * "Generating random regular graphs."
 * Proceedings of the thirty-fifth annual ACM symposium on Theory of computing. ACM, 2003.
 *
 * [SW99] Steger, Angelika, and Nicholas C. Wormald.
 * "Generating random regular graphs quickly."
 * Combinatorics, Probability and Computing 8.4 (1999): 377-396.
 *
 * @author Emilio Cruciani
 * @since March 2018
 *
 * @param <V> graph node type
 * @param <E> graph edge type
 */
public class RandomRegularGraphGenerator<V, E> implements GraphGenerator<V, E, V> {

    private final int n;
    private final int d;

    /**
     * Construct a new RandomRegularGraphGenerator.
     *
     * @param n number of nodes
     * @param d degree of nodes
     * @throws IllegalArgumentException if number of nodes is negative
     * @throws IllegalArgumentException if degree is negative
     * @throws IllegalArgumentException if degree is greater than number of nodes
     * @throws IllegalArgumentException if the value "n * k" is odd
     */
    public RandomRegularGraphGenerator(int n, int d) {
        if (n < 0) {
            throw new IllegalArgumentException("number of nodes must be non-negative");
        }
        if (d < 0) {
            throw new IllegalArgumentException("degree of nodes must be non-negative");
        }
        if (d > n) {
            throw new IllegalArgumentException("degree of nodes must be smaller than or equal to number of nodes");
        }
        if ((n * d) % 2 != 0) {
            throw new IllegalArgumentException("value 'n * k' must be even");
        }
        this.n = n;
        this.d = d;
    }

    /**
     * Generate a random regular graph.
     *
     * @param target target graph
     * @param vertexFactory vertex factory
     * @param resultMap result map
     * @throws IllegalArgumentException if target is directed
     * @throws IllegalArgumentException if "d == n" and graph is simple
     */
    @Override
    public void generateGraph(Graph<V, E> target, VertexFactory<V> vertexFactory, Map<String, V> resultMap) {

        // directed/mixed case
        if (!target.getType().isUndirected()) {
            throw new IllegalArgumentException("target graph must be undirected");
        }

        // simple case
        if (target.getType().isSimple()) {
            // no nodes or zero degree case
            if (this.n == 0 || this.d == 0) {
                EmptyGraphGenerator<V, E> emptyGraphGenerator = new EmptyGraphGenerator<>(this.n);
                emptyGraphGenerator.generateGraph(target, vertexFactory, resultMap);
            }

            else if (this.d == this.n) {
                throw new IllegalArgumentException("target graph must be simple if 'd==n'");
            }

            // complete case
            else if (this.d == (this.n - 1)) {
                CompleteGraphGenerator<V, E> completeGraphGenerator = new CompleteGraphGenerator<>(this.n);
                completeGraphGenerator.generateGraph(target, vertexFactory, resultMap);
            }

            // general case
            else {
                generateSimpleRegularGraph(target, vertexFactory);
            }
        }

        // non-simple case
        else {
            generateNonSimpleRegularGraph(target, vertexFactory);
        }
    }


    private boolean isDRegular(Graph<V, E> target) {
        for (V v : target.vertexSet()) {
            if (target.degreeOf(v) != this.d) {
                return false;
            }
        }
        return true;
    }


    private void generateSimpleRegularGraph(Graph<V, E> target, VertexFactory<V> vertexFactory) {
        // integers to vertices
        List<V> vertices = new ArrayList<>();
        for (int i = 0; i < this.n; i++) {
            V vertex = vertexFactory.createVertex();
            vertices.add(vertex);
            target.addVertex(vertex);
        }

        while (!isDRegular(target)) {
            // initialize target graph
            target.removeAllEdges(new HashSet<>(target.edgeSet()));

            // set of candidate edges
            Set<Map.Entry<V, V>> S = new HashSet<>();
            for (int i = 0; i < this.n; i++) {
                for (int j = i + 1; j < this.n; j++) {
                    V u = vertices.get(i);
                    V v = vertices.get(j);
                    S.add(new AbstractMap.SimpleImmutableEntry<>(u, v));
                }
            }

            Set<Map.Entry<V, V>> toRemoveFromS = new HashSet<>();
            while (!S.isEmpty()) {
                // needed to normalize probabilities in [0,1]
                double norm = 0.0;
                for (Map.Entry<V, V> edge : S) {
                    V u = edge.getKey();
                    V v = edge.getValue();
                    norm += (this.d - target.degreeOf(u)) * (this.d - target.degreeOf(v));
                }

                double r = Math.random();
                double c = 0.0;  // cumulative probability
                for (Map.Entry<V, V> edge : S) {
                    V u = edge.getKey();
                    V v = edge.getValue();

                    // probability of picking edge (u, v)
                    double p = (this.d - target.degreeOf(u)) * (this.d - target.degreeOf(v)) / norm;

                    c += p;
                    if (c >= r) {
                        target.addEdge(u, v);

                        // select edges to remove from S
                        // cannot remove directly while iterating
                        toRemoveFromS.add(edge);
                        if (target.degreeOf(u) == this.d) {
                            for (Map.Entry<V, V> e : S) {
                                if (e.getKey().equals(u) || e.getValue().equals(u)) {
                                    toRemoveFromS.add(e);
                                }
                            }
                        }
                        if (target.degreeOf(v) == this.d) {
                            for (Map.Entry<V, V> e : S) {
                                if (e.getKey().equals(v) || e.getValue().equals(v)) {
                                    toRemoveFromS.add(e);
                                }
                            }
                        }
                        break;
                    }
                }

                // update S
                S.removeAll(toRemoveFromS);
                toRemoveFromS.clear();
            }
        }
    }


    private void generateNonSimpleRegularGraph(Graph<V, E> target, VertexFactory<V> vertexFactory) {
        List<V> vertices = new ArrayList<>(this.n * this.d);
        for (int i = 0; i < this.n; i++) {
            V vertex = vertexFactory.createVertex();
            target.addVertex(vertex);
            for (int j = 0; j < this.d; j++) {
                vertices.add(vertex);
            }
        }

        Collections.shuffle(vertices);
        for (int i = 0; i < (this.n * this.d)/2; i++) {
            V u = vertices.get(2*i);
            V v = vertices.get(2*i + 1);
            target.addEdge(u, v);
        }
    }

}
