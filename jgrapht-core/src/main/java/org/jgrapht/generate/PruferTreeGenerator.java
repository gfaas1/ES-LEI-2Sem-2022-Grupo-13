/*
 * (C) Copyright 2018-2018, by Alexandru Valeanu and Contributors.
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
import org.jgrapht.GraphTests;

import java.util.*;

/**
 * Generates a random tree using Prüfer sequences.
 * 
 * <p>
 *  A Prüfer sequence of length $n$ is randomly generated and converted into the corresponding tree.
 * </p>
 *
 * <p>
 *  This implementation is inspired by "X. Wang, L. Wang and Y. Wu, "An Optimal Algorithm for Prufer Codes," Journal
 *  of Software Engineering and Applications, Vol. 2 No. 2, 2009, pp. 111-115. doi: 10.4236/jsea.2009.22016."
 *  and has a running time of $O(n)$.
 * </p>
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Alexandru Valeanu
 */
public class PruferTreeGenerator<V, E> implements GraphGenerator<V, E, V> {

    // number of vertices
    private int n;

    // random number generator
    private Random rng;

    /**
     * Construct a new PruferTreeGenerator.
     *
     * @param n number of vertices to be generated
     * @throws IllegalArgumentException if {@code n} is &le; 0
     */
    public PruferTreeGenerator(int n) {
        this(n, new Random());
    }

    /**
     * Construct a new PruferTreeGenerator.
     *
     * @param n number of vertices to be generated
     * @param seed seed for the random number generator
     * @throws IllegalArgumentException if {@code n} is &le; 0
     */
    public PruferTreeGenerator(int n, long seed) {
        this(n, new Random(seed));
    }

    /**
     * Construct a new PruferTreeGenerator
     *
     * @param n number of vertices to be generated
     * @param rng the random number generator to use
     * @throws IllegalArgumentException if {@code n} is &le; 0
     * @throws NullPointerException if {@code rng} is {@code null}
     */
    public PruferTreeGenerator(int n, Random rng) {
        if (n <= 0){
            throw new IllegalArgumentException("n must be greater than 0");
        }

        this.n = n;
        this.rng = Objects.requireNonNull(rng, "Random number generator cannot be null");
    }

    /**
     * Generates a tree.
     *
     * Note: All existing vertices and edges of the target graph will be removed.
     *
     * @param target the target graph
     * @param resultMap not used by this generator, can be null
     * @throws NullPointerException if {@code target} is {@code null}
     * @throws IllegalArgumentException if {@code target} is not undirected
     */
    @Override
    public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
        GraphTests.requireUndirected(target);

        // remove old vertices and edges
        target.removeAllVertices(new HashSet<>(target.vertexSet()));

        // base case
        if (n == 1){
            if (target.addVertex() == null) {
                throw new IllegalArgumentException("Invalid vertex supplier");
            }

            return;
        }

        List<V> vertexList = new ArrayList<>(n);

        // add vertices
        for (int i = 0; i < n; i++) {
            V newVertex = target.addVertex();

            if (newVertex == null) {
                throw new IllegalArgumentException("Invalid vertex supplier");
            }

            vertexList.add(newVertex);
        }

        // degree stores the remaining degree (plus one) for each node. The
        // degree of a node in the decoded tree is one more than the number
        // of times it appears in the code.
        int[] degree = new int[n];
        Arrays.fill(degree, 1);

        int[] pruferSeq = new int[n - 2];
        for (int i = 0; i < n - 2; i++) {
            pruferSeq[i] = rng.nextInt(n);
            ++degree[pruferSeq[i]];
        }

        int index = -1, x = -1;

        for (int k = 0; k < n; k++){
            if (degree[k] == 1){
                index = x = k;
                break;
            }
        }

        assert index != -1;

        // set of nodes without a parent
        Set<V> orphans = new HashSet<>(target.vertexSet());

        for (int i = 0; i < n - 2; i++){
            int y = pruferSeq[i];
            orphans.remove(vertexList.get(x));
            target.addEdge(vertexList.get(x), vertexList.get(y));
            --degree[y];

            if (y < index && degree[y] == 1){
                x = y;
            }
            else{
                for (int k = index + 1; k < n; k++) {
                    if (degree[k] == 1){
                        index = x = k;
                        break;
                    }
                }
            }
        }

        assert orphans.size() == 2;
        Iterator<V> iterator = orphans.iterator();
        V u = iterator.next();
        V v = iterator.next();
        target.addEdge(u, v);
    }
}
