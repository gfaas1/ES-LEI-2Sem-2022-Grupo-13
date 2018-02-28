package org.jgrapht.alg.chordal;

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
public class BucketList<V> {
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
     * Creates a <code>BucketList</code> with a single bucket with all specified <code>vertices</code> in that bucket
     *
     * @param vertices the graph vertex type
     */
    public BucketList(Collection<V> vertices) {
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
     *
     * <p>Removes the head bucket if it becomes empty after the operation.</p>
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
     * Updates the label of the {@code vertex} and puts it to the appropriate bucket
     *
     * <p>Retrieves the {@code vertex} from the bucket is was previously in and adds it
     * to the bucket whose label is a concatenation of the label of the {@code vertex}
     * and {@code lastSelectedVertex}. Creates new bucket is needed. Removes initial bucket if it
     * is empty after this operation.</p>
     *
     * @param vertex             the vertex whose label has to be updated
     * @param lastSelectedVertex the index to append to the {@code label} of {@code vertex}
     *
     */
    public void updateLabel(V vertex, int lastSelectedVertex) {
        Bucket vertexBucket = bucketMap.get(vertex);
        if (vertexBucket.prev == null ||
                !vertexBucket.prev.equalLabel(vertexBucket.label, lastSelectedVertex)) {
            Bucket newBucket = new Bucket(vertexBucket.label, lastSelectedVertex);
            newBucket.addVertex(vertex);
            bucketMap.put(vertex, newBucket);
            newBucket.insertBefore(vertexBucket);
            if (head == vertexBucket) {
                head = newBucket;
            }
        } else {
            vertexBucket.prev.addVertex(vertex);
            bucketMap.put(vertex, vertexBucket.prev);
        }
        vertexBucket.removeVertex(vertex);
        if (vertexBucket.isEmpty()) {
            if (vertexBucket == head) {
                head = vertexBucket.next;
            }
            vertexBucket.removeSelf();
        }
    }

    /**
     * Plays the role of a bucket with vertices in the lexicographical breadth-first search. Encapsulates operations
     * of addition and removal of vertices to the bucket, removing a bucket from the data structure and comparing its
     * label with
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
         * Label of this bucket. Represented as an array of indices of all neighbours of vertices in this bucket,
         * that have already been visited and now aren't stored in any bucket in this data structure.
         */
        private int[] label;
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
            this.label = new int[0];
            this.vertices = new HashSet<>(vertices);
        }

        /**
         * Creates an empty bucket with a label represented as the concatenation of
         * {@code baseLabel} and {@code lastSelectedVertex}. Used to create new buckets
         * at the time of the lexical being in process.
         *
         * @param baseLabel          first part of the final label
         * @param lastSelectedVertex last part of the final label
         */
        Bucket(int[] baseLabel, int lastSelectedVertex) {
            this.label = new int[baseLabel.length + 1];
            System.arraycopy(baseLabel, 0, label, 0, baseLabel.length);
            label[label.length - 1] = lastSelectedVertex;
            this.vertices = new HashSet<>();
        }

        @Override
        public String toString() {
            return "Bucket{" + "label=" + Arrays.toString(label) +
                    ", vertices=" + vertices +
                    '}';
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
         * Compares the label of this bucket with the concatenation of the {@code baseLabel}
         * and {@code lastSelectedVertex}.
         *
         * @param baseLabel          first part of the label
         * @param lastSelectedVertex last element of the label
         * @return true if the label of this bucket is equal to the concatenation of {@code baseLabel}
         * and {@code lastSelectedVertex}, otherwise false.
         */
        boolean equalLabel(int[] baseLabel, int lastSelectedVertex) {
            if (baseLabel.length != label.length - 1) {
                return false;
            } else {
                for (int i = 0; i < baseLabel.length; i++) {
                    if (baseLabel[i] != label[i]) {
                        return false;
                    }
                }
                return lastSelectedVertex == label[baseLabel.length];
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

        public Bucket getNext() {
            return next;
        }

        public void setNext(Bucket next) {
            this.next = next;
        }

    }
}
