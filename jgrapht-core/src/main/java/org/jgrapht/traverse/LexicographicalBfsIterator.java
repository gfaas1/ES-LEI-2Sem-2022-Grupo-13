/*
 * (C) Copyright 2018, by Timofey Chudakov and Contributors.
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
package org.jgrapht.traverse;

import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;

import java.util.*;

/**
 * A lexicographical breadth-first iterator for an undirected graph.
 * <p>
 * Every vertex has an implicit label (they aren't used explicitly in order to reduce time and
 * memory complexity). When some vertex is returned by this iterator, its index is the number of
 * vertices in this graph minus number of already returned vertices. For a given vertex v its label
 * is a concatenation of indices of already returned vertices, that were also its neighbours, with
 * some separator between them. For example, 7#4#3 is a valid vertex label.
 * <p>
 * Iterator chooses vertex with lexicographically largest label and returns it. It breaks ties
 * arbitrarily. For more information on lexicographical BFS see the following papers: <a
 * href="https://pdfs.semanticscholar.org/d4b5/a492f781f23a30773841ec79c46d2ec2eb9c.pdf">
 * <i>Lexicographic Breadth First Search – A Survey</i></a> and <a
 * href="http://www.cse.iitd.ac.in/~naveen/courses/CSL851/uwaterloo.pdf"><i>CS 762: Graph-theoretic
 * algorithms. Lecture notes of a graduate course. University of Waterloo</i></a>.
 * <p>
 * For this iterator to work correctly the graph must not be modified during iteration. Currently
 * there are no means to ensure that, nor to fail-fast. The results of such modifications are
 * undefined.
 *
 * @param <V> the graph vertex type.
 * @param <E> the graph edge type.
 * @author Timofey Chudakov
 * @since 1.8
 */
public class LexicographicalBfsIterator<V, E> extends AbstractGraphIterator<V, E> {

    /**
     * Reference to the {@code BucketList} that contains unvisited vertices.
     */
    private BucketList bucketList;
    /**
     * Number of unvisited vertices.
     */
    private int remainingVertices;

    /**
     * Creates new lexicographical breadth-first iterator for {@code graph}.
     *
     * @param graph the graph to be iterated.
     */
    public LexicographicalBfsIterator(Graph<V, E> graph) {
        super(graph);
        GraphTests.requireUndirected(graph);
        bucketList = new BucketList(graph.vertexSet());
        remainingVertices = graph.vertexSet().size();
    }

    /**
     * Checks whether there exist unvisited vertices.
     *
     * @return true if there exist unvisited vertices.
     */
    @Override
    public boolean hasNext() {
        return remainingVertices > 0;
    }

    /**
     * Returns a vertex with the lexicographically largest label, breaking ties arbitrarily.
     * Recomputes cardinalities of its unvisited neighbours.
     *
     * @return vertex with the lexicographically largest label.
     */
    @Override
    public V next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        V vertex = bucketList.poll();
        bucketList.updateBuckets(getUnvisitedNeighbours(vertex));
        --remainingVertices;
        return vertex;
    }

    /**
     * Computes and returns neighbours of {@code vertex} which haven't been visited by this iterator.
     *
     * @param vertex the vertex, whose neighbours are being explored.
     * @return neighbours of {@code vertex} which have yet to be visited by this iterator.
     */
    private Set<V> getUnvisitedNeighbours(V vertex) {
        Set<V> unmapped = new HashSet<>();
        Set<E> edges = graph.edgesOf(vertex);
        for (E edge : edges) {
            V oppositeVertex = Graphs.getOppositeVertex(graph, edge, vertex);
            if (bucketList.containsBucketWith(oppositeVertex)) {
                unmapped.add(oppositeVertex);
            }
        }
        return unmapped;
    }

    /**
     * Data structure for performing lexicographical breadth-first search. Allows to add and retrieve
     * vertices from buckets, update their buckets after a new vertex has been added to the LexBFS
     * order. Labels aren't used explicitly, which results in time and space optimization.
     *
     * @author Timofey Chudakov
     * @since 1.8
     */
    class BucketList {
        /**
         * Bucket with the vertices that have lexicographically largest label.
         */
        private Bucket head;
        /**
         * Map for mapping vertices to buckets they are currently in. Is used for finding the bucket of
         * the vertex in constant time.
         */
        private Map<V, Bucket> bucketMap;

        /**
         * Creates a <code>BucketList</code> with a single bucket with all specified {@code vertices} in
         * that bucket.
         *
         * @param vertices the vertices of the graph, that should be stored in a {@code head} bucket.
         */
        BucketList(Collection<V> vertices) {
            head = new Bucket(vertices);
            bucketMap = new HashMap<>(vertices.size());
            for (V vertex : vertices) {
                bucketMap.put(vertex, head);
            }
        }

        /**
         * Checks whether there exists a bucket with the specified <code>vertex</code>.
         *
         * @param vertex the vertex whose presence in some {@code Bucket} in this {@code BucketList} is
         *               checked.
         * @return <tt>true</tt> if there exists a bucket with {@code vertex} in it, otherwise
         * <tt>false</tt>.
         */
        boolean containsBucketWith(V vertex) {
            return bucketMap.containsKey(vertex);
        }

        /**
         * Retrieves element from the head bucket by invoking {@link Bucket#poll()}.
         * <p>
         * <p>Removes the head bucket if it becomes empty after the operation.
         *
         * @return vertex returned by {@link Bucket#poll()} invoked on head bucket.
         */
        V poll() {
            V res = head.poll();
            bucketMap.remove(res);
            if (head.isEmpty()) {
                head = head.next;
                if (head != null) {
                    head.prev = null;
                }
            }
            return res;
        }

        /**
         * For every bucket B in this {@code BucketList}, which contains vertices from the set {@code
         * vertices}, creates a new {@code Bucket} B' and moves vertices from B to B' according to the
         * following rule: $B' = B\cap vertices$ and $B = B\backslash B'$. For every such {@code Bucket} B only one {@code Bucket}
         * B' is created. If some bucket B becomes empty after this operation, it is removed from the
         * data structure.
         *
         * @param vertices the vertices, that should be moved to new buckets.
         */
        void updateBuckets(Set<V> vertices) {
            Set<Bucket> visitedBuckets = new HashSet<>();
            for (V vertex : vertices) {
                Bucket bucket = bucketMap.get(vertex);
                if (visitedBuckets.contains(bucket)) {
                    bucket.prev.addVertex(vertex);
                    bucketMap.put(vertex, bucket.prev);
                } else {
                    visitedBuckets.add(bucket);
                    Bucket newBucket = new Bucket(vertex);
                    newBucket.insertBefore(bucket);
                    bucketMap.put(vertex, newBucket);
                    if (head == bucket) {
                        head = newBucket;
                    }
                }
                bucket.removeVertex(vertex);
                if (bucket.isEmpty()) {
                    visitedBuckets.remove(bucket);
                    bucket.removeSelf();
                }
            }
        }

        /**
         * Plays the role of the container of vertices. All vertices stored in bucket have identical
         * label. Labels aren't used explicitly.
         * <p>
         * <p>Encapsulates operations of addition and removal of vertices from the bucket, removal of a
         * bucket from the data structure.
         */
        private class Bucket {
            /**
             * Reference of the bucket with lexicographically smaller label.
             */
            private Bucket next;
            /**
             * Reference of the bucket with lexicographically larger label.
             */
            private Bucket prev;
            /**
             * Set of vertices currently stored in this bucket.
             */
            private Set<V> vertices;

            /**
             * Creates a new bucket with all {@code vertices} stored in it.
             *
             * @param vertices vertices to store in this bucket.
             */
            Bucket(Collection<V> vertices) {
                this.vertices = new HashSet<>(vertices);
            }

            /**
             * Creates a new Bucket with a single {@code vertex} in it.
             *
             * @param vertex the vertex to store in this bucket.
             */
            Bucket(V vertex) {
                this.vertices = new HashSet<>();
                vertices.add(vertex);
            }

            /**
             * Removes the {@code vertex} from this bucket.
             *
             * @param vertex the vertex to remove.
             */
            void removeVertex(V vertex) {
                vertices.remove(vertex);
            }

            /**
             * Removes this bucket from the data structure.
             */
            void removeSelf() {
                if (next != null) {
                    next.prev = prev;
                }
                if (prev != null) {
                    prev.next = next;
                }
            }

            /**
             * Inserts this bucket in the data structure before the {@code bucket}.
             *
             * @param bucket the bucket, that will be the next to this bucket.
             */
            void insertBefore(Bucket bucket) {
                this.next = bucket;
                if (bucket != null) {
                    this.prev = bucket.prev;
                    if (bucket.prev != null) {
                        bucket.prev.next = this;
                    }
                    bucket.prev = this;
                } else {
                    this.prev = null;
                }
            }

            /**
             * Adds the {@code vertex} to this bucket.
             *
             * @param vertex the vertex to add.
             */
            void addVertex(V vertex) {
                vertices.add(vertex);
            }

            /**
             * Retrieves one vertex from this bucket.
             *
             * @return vertex, that was removed from this bucket, null if the bucket was empty.
             */
            V poll() {
                if (vertices.isEmpty()) {
                    return null;
                } else {
                    V vertex = vertices.iterator().next();
                    vertices.remove(vertex);
                    return vertex;
                }
            }

            /**
             * Checks whether this bucket is empty.
             *
             * @return <tt>true</tt> if this bucket doesn't contain any elements, otherwise false.
             */
            boolean isEmpty() {
                return vertices.size() == 0;
            }
        }
    }
}
