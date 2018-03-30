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
    private final Random rng;

    /**
     * Construct a new RandomRegularGraphGenerator.
     *
     * @param n number of nodes
     * @param d degree of nodes
     * @throws IllegalArgumentException if number of nodes is negative
     * @throws IllegalArgumentException if degree is negative
     * @throws IllegalArgumentException if degree is greater than number of nodes
     * @throws IllegalArgumentException if the value "n * d" is odd
     */
    public RandomRegularGraphGenerator(int n, int d) {
        this(n, d, new Random());
    }

    /**
     * Construct a new RandomRegularGraphGenerator.
     *
     * @param n number of nodes
     * @param d degree of nodes
     * @param seed seed for the random number generator
     * @throws IllegalArgumentException if number of nodes is negative
     * @throws IllegalArgumentException if degree is negative
     * @throws IllegalArgumentException if degree is greater than number of nodes
     * @throws IllegalArgumentException if the value "n * d" is odd
     */
    public RandomRegularGraphGenerator(int n, int d, long seed) {
        this(n, d, new Random(seed));
    }

    /**
     * Construct a new RandomRegularGraphGenerator.
     *
     * @param n number of nodes
     * @param d degree of nodes
     * @param rng the random number generator to use
     * @throws IllegalArgumentException if number of nodes is negative
     * @throws IllegalArgumentException if degree is negative
     * @throws IllegalArgumentException if degree is greater than number of nodes
     * @throws IllegalArgumentException if the value "n * d" is odd
     */
    public RandomRegularGraphGenerator(int n, int d, Random rng) {
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
            throw new IllegalArgumentException("value 'n * d' must be even");
        }
        this.n = n;
        this.d = d;
        this.rng = rng;
    }

    /**
     * Generate a random regular graph.
     *
     * @param target target graph
     * @param vertexFactory vertex factory
     * @param resultMap result map
     * @throws IllegalArgumentException if target is directed
     * @throws IllegalArgumentException if "n == d" and graph is simple
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
                throw new IllegalArgumentException("target graph must be simple if 'n == d'");
            }

            // complete case
            else if (this.d == (this.n - 1)) {
                CompleteGraphGenerator<V, E> completeGraphGenerator = new CompleteGraphGenerator<>(this.n);
                completeGraphGenerator.generateGraph(target, vertexFactory, resultMap);
            }

            // general case
            else {
                generateSimpleRegularGraphAlgorithm1(target, vertexFactory);
                // alternative (slower) method presented in [SW99]
                // generateSimpleRegularGraphAlgorithm2(target, vertexFactory);
            }
        }

        // non-simple case
        else {
            generateNonSimpleRegularGraph(target, vertexFactory);
        }
    }

    // auxiliary method to check if there are remaining suitable edges
    private boolean suitable(Set<Map.Entry<Integer, Integer>> edges, Map<Integer, Integer> potentialEdges) {
        if (potentialEdges.isEmpty()) {
            return true;
        }
        Object[] keys = potentialEdges.keySet().toArray();
        Arrays.sort(keys);

        for (int i = 0; i < keys.length; i++) {
            int s2 = (int) keys[i];
            for (int j = 0; j < i; j++) {
                int s1 = (int) keys[j];
                Map.Entry<Integer, Integer> e = new AbstractMap.SimpleImmutableEntry<>(s1, s2);
                if (!edges.contains(e)) {
                    return true;
                }
            }
        }
        return false;
    }

    // auxiliary method to manage simple case
    private void generateSimpleRegularGraphAlgorithm1(Graph<V, E> target, VertexFactory<V> vertexFactory) {
        // integers to vertices
        List<V> vertices = new ArrayList<>(this.n);
        for (int i = 0; i < this.n; i++) {
            V vertex = vertexFactory.createVertex();
            vertices.add(vertex);
            target.addVertex(vertex);
        }

        // set of final edges to add to target graph
        Set<Map.Entry<Integer, Integer>> edges = new HashSet<>(this.n * this.d);
        do {
            List<Integer> stubs = new ArrayList<>(this.n * this.d);
            for (int i = 0; i < this.n * this.d; i++) {
                stubs.add(i % this.n);
            }

            while (!stubs.isEmpty()) {
                Map<Integer, Integer> potentialEdges = new HashMap<>();
                Collections.shuffle(stubs, this.rng);

                for (int i = 0; i < stubs.size() - 1; i += 2) {
                    int s1 = stubs.get(i);
                    int s2 = stubs.get(i + 1);
                    // s1 < s2 has to be true
                    if (s1 > s2) {
                        int temp = s1;
                        s1 = s2;
                        s2 = temp;
                    }

                    Map.Entry<Integer, Integer> edge = new AbstractMap.SimpleImmutableEntry<>(s1, s2);
                    if (s1 != s2 && !edges.contains(edge)) {
                        edges.add(edge);
                    }
                    else {
                        potentialEdges.put(s1, potentialEdges.getOrDefault(s1, 0) + 1);
                        potentialEdges.put(s2, potentialEdges.getOrDefault(s2, 0) + 1);
                    }
                }

                if (!suitable(edges, potentialEdges)) {
                    edges.clear();
                    break;
                }

                stubs.clear();
                for (Map.Entry<Integer, Integer> e : potentialEdges.entrySet()) {
                    int node = e.getKey();
                    int potential = e.getValue();
                    for (int i = 0; i < potential; i++) {
                        stubs.add(node);
                    }
                }
            }

        } while (edges.isEmpty());

        // add edges to target
        for (Map.Entry<Integer, Integer> e : edges) {
            target.addEdge(vertices.get(e.getKey()), vertices.get(e.getValue()));
        }
    }


    // auxiliary method to check regularity
    private boolean isDRegular(Graph<V, E> target) {
        for (V v : target.vertexSet()) {
            if (target.degreeOf(v) != this.d) {
                return false;
            }
        }
        return true;
    }

    // auxiliary method to manage simple case
    private void generateSimpleRegularGraphAlgorithm2(Graph<V, E> target, VertexFactory<V> vertexFactory) {
        // integers to vertices
        List<V> vertices = new ArrayList<>(this.n);
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

                double r = rng.nextDouble();
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


    // auxiliary method to manage non-simple case
    private void generateNonSimpleRegularGraph(Graph<V, E> target, VertexFactory<V> vertexFactory) {
        List<V> vertices = new ArrayList<>(this.n * this.d);
        for (int i = 0; i < this.n; i++) {
            V vertex = vertexFactory.createVertex();
            target.addVertex(vertex);
            for (int j = 0; j < this.d; j++) {
                vertices.add(vertex);
            }
        }

        Collections.shuffle(vertices, this.rng);
        for (int i = 0; i < (this.n * this.d)/2; i++) {
            V u = vertices.get(2*i);
            V v = vertices.get(2*i + 1);
            target.addEdge(u, v);
        }
    }

}
