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
package org.jgrapht.alg.cycle;

import java.util.*;

/**
 * Data structure for performing lexicographical breadth-first search.
 * Allows to add and retrieve vertices from buckets, update their labels
 *
 * @param <V> the graph vertex type
 * @author Timofey Chudakov
 * @see ChordalGraphInspector
 * @since 1.8
 */
class BucketList<V> {
    /**
     * Bucket with the vertices that have lexicographically largest label assigned to them
     */
    private Bucket head;
    /**
     * Map for mapping vertices to buckets they are currently in.
     * Is used for finding the bucket of the vertex in constant time
     */
    private Map<V, Bucket> bucketMap;

    /**
     * Creates a <code>BucketList</code> with a single bucket with all specified {@code vertices} in that bucket
     *
     * @param vertices the vertices of the graph, that should be stored in a {@code head} bucket
     */
    BucketList(Collection<V> vertices) {
        head = new Bucket(vertices);
        bucketMap = new HashMap<>();
        for (V vertex : vertices) {
            bucketMap.put(vertex, head);
        }
    }

    /**
     * Checks whether there exists a bucket with the specified <code>vertex</code>
     *
     * @param vertex the vertex whose presence in some <code>Bucket</code> in this <code>BucketList</code> is checked
     * @return <tt>true</tt> if there exists a bucket with <code>vertex</code> in it, otherwise <tt>false</tt>.
     */
    public boolean containsBucketWith(V vertex) {
        return bucketMap.containsKey(vertex);
    }

    /**
     * Retrieves element from head bucket by invoking {@link Bucket#poll()}.
     * <p>
     * Removes the head bucket if it becomes empty after the operation.
     *
     * @return vertex returned by {@link Bucket#poll()} invoked on head bucket
     */
    public V poll() {
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
     * For every bucket B in this {@code BucketList}, which contains vertices from the set {@code vertices},
     * creates a new {@code Bucket} B' and moves all vertices from B to B', that at the same time contained in {@code vertices}
     * Bucket B' becomes previous to B. For every such {@code Bucket} B only one {@code Bucket} B' is created.
     * If some bucket B becomes empty after this operation, it is remove from the data structure.
     *
     * @param vertices the vertices, that should be moved to new Buckets
     */
    public void updateBuckets(Set<V> vertices) {
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
     * Plays the role of the container of vertices. All vertices stored in bucket have identical label.
     * <p>
     * Encapsulates operations of addition and removal of vertices from the bucket, removal of a bucket from the data
     * structure.
     */
    private class Bucket {
        /**
         * Reference of the bucket with lexicographically smaller label
         */
        private Bucket next;
        /**
         * Reference of the bucket with lexicographically larger label
         */
        private Bucket prev;
        /**
         * Set of vertices currently stored in this bucket
         */
        private Set<V> vertices;

        /**
         * Creates a new bucket with all {@code vertices} and an empty label.
         * Is used to create the first bucket with all vertices of a graph
         *
         * @param vertices vertices to store in this bucket
         */
        Bucket(Collection<V> vertices) {
            this.vertices = new HashSet<>(vertices);
        }

        Bucket(V vertex) {
            this.vertices = new HashSet<>();
            vertices.add(vertex);
        }

        /**
         * Removes the {@code vertex} from this bucket
         *
         * @param vertex the vertex to remove
         * @return true if the {@code vertex} was removed from this bucket, otherwise false.
         */
        public boolean removeVertex(V vertex) {
            return vertices.remove(vertex);
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
         * Inserts this bucket in the data structure before the {@code bucket}
         *
         * @param bucket the bucket, that will be the next to this bucket
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
         * Adds the {@code vertex} to this bucket
         *
         * @param vertex the vertex to add
         */
        public void addVertex(V vertex) {
            vertices.add(vertex);
        }

        /**
         * Retrieves one vertex from this bucket
         *
         * @return vertex, that was removed from this bucket, null if the bucket was empty.
         */
        public V poll() {
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
        public boolean isEmpty() {
            return vertices.size() == 0;
        }
    }
}
