package org.jgrapht.alg.chordal;

import java.util.*;

public class BucketList<V> {
    private Bucket head;
    private Map<V, Bucket> bucketMap;


    public BucketList(Collection<V> vertices) {
        head = new Bucket(vertices);
        bucketMap = new HashMap<>();
        for (V vertex : vertices) {
            bucketMap.put(vertex, head);
        }
    }

    public boolean containsBucketWith(V vertex) {
        return bucketMap.containsKey(vertex);
    }

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

    public void updateLabel(V vertex, int lastSelected) {
        Bucket vertexBucket = bucketMap.get(vertex);
        if (vertexBucket.prev == null ||
                !vertexBucket.prev.equalLabel(vertexBucket.label, lastSelected)) {
            Bucket newBucket = new Bucket(vertexBucket.label, lastSelected);
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

    private class Bucket {
        private Bucket next;
        private Bucket prev;
        private int[] label;
        private Set<V> vertices;

        Bucket(int[] baseLabel, int lastSelected) {
            this.label = new int[baseLabel.length + 1];
            System.arraycopy(baseLabel, 0, label, 0, baseLabel.length);
            label[label.length - 1] = lastSelected;
            this.vertices = new HashSet<>();
        }

        Bucket() {
            this.label = new int[0];
            this.vertices = new HashSet<>();
        }

        Bucket(Collection<V> vertices) {
            this.label = new int[0];
            this.vertices = new HashSet<>(vertices);
        }

        @Override
        public String toString() {
            return "Bucket{" + "label=" + Arrays.toString(label) +
                    ", vertices=" + vertices +
                    '}';
        }

        void removeSelf() {
            if (next != null) {
                next.prev = prev;
            }
            if (prev != null) {
                prev.next = next;
            }
        }

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

        public void setNext(Bucket next) {
            this.next = next;
        }

        public void addVertex(V vertex) {
            vertices.add(vertex);
        }

        public void setPrev(Bucket prev) {
            this.prev = prev;
        }

        boolean equalLabel(int[] baseLabel, int lastSelected) {
            if (baseLabel.length != label.length - 1) {
                return false;
            } else {
                for (int i = 0; i < baseLabel.length; i++) {
                    if (baseLabel[i] != label[i]) {
                        return false;
                    }
                }
                return lastSelected == label[baseLabel.length];
            }
        }


        public boolean isEmpty() {
            return vertices.size() == 0;
        }

        public V poll() {
            V vertex = vertices.iterator().next();
            vertices.remove(vertex);
            return vertex;
        }

        public boolean removeVertex(V vertex) {
            return vertices.remove(vertex);
        }

        public boolean hasVertices() {
            return !vertices.isEmpty();
        }
    }
}
