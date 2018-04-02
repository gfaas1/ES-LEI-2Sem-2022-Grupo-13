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

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.AsUndirectedGraph;

import java.util.*;

/**
 * Allows testing weak chordality of a graph. The inspected {@code graph} is specified at the construction time
 * and cannot be modified. When the graph is externally modified, the behaviour of the {@code WeakChordalityInspector}
 * is unpredictable. Note: weak chordality of the graph is computed only once and then the same value is returned.
 * <p>
 * A graph is called <a href="http://www.graphclasses.org/classes/gc_14.html">weakly chordal</a> if it and its
 * complement don't have chordless cycles of length greater then 4. A chord is an edge that is not a part of a
 * cycle but connects two vertices of the cycle. For more information on weakly chordal graph recognition see:
 * Lars Severin Skeide (2002) <a href="http://www.ii.uib.no/~skeide/rec_wcg.pdf"><i>Recognizing weakly chordal
 * graphs</i></a>. Candidate Scientist Thesis in Informatics. Department of Informatics, University of Bergen, Norway.
 * The terminology in this implementation is consistent with one used in the paper.
 * <p>
 * The running time complexity of the algorithm if $\mathcal{O}(|E|^2)$ and the space complexity is also $\mathcal{O}(|E|^2)$
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 * @author Timofey Chudakov
 * @since April 2018
 */
public class WeakChordalityInspector<V, E> {
    /**
     * Vertex number
     */
    private final int n;
    /**
     * Edge number
     */
    private final int m;
    /**
     * The inspected graph
     */
    private Graph<V, E> graph;
    /**
     * Bijective mapping of vertices onto $\left[0,n-1\right]$
     */
    private Map<V, Integer> vertices;
    /**
     * Inverse of the bijective mapping of vertices onto $\left[0,n-1\right]$
     */
    private Map<Integer, V> indices;
    /**
     * Contains true if the graph is weakly chordal, otherwise false. Is null before the first call to the
     * {@link WeakChordalityInspector#isWeaklyChordal()}.
     */
    private Boolean weaklyChordal = null;

    /**
     * Creates a weak chordality inspector for the {@code graph}
     *
     * @param graph the inspected {@code graph}
     */
    public WeakChordalityInspector(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph);
        if (graph.getType().isDirected()) {
            this.graph = new AsUndirectedGraph<>(graph);
        }
        n = graph.vertexSet().size();
        m = graph.edgeSet().size();
        initMappings();
    }

    /**
     * Initializes the mappings of the vertices
     */
    private void initMappings() {
        vertices = new HashMap<>(n);
        indices = new HashMap<>(n);
        int i = 0;
        for (V v : graph.vertexSet()) {
            indices.put(i, v);
            vertices.put(v, i++);
        }
    }

    /**
     * Check whether the inspected {@code graph} is weakly chordal.
     * Note: this value is computed lazily.
     *
     * @return true, if the inspected {@code graph} is weakly chordal, otherwise false.
     */
    public boolean isWeaklyChordal() {
        return lazyComputeWeakChordality();
    }

    /**
     * Lazily tests the weak chordality of the {@code graph} and returns the computed value.
     *
     * @return true, if the inspected {@code graph} is weakly chordal, otherwise false.
     */
    private boolean lazyComputeWeakChordality() {
        if (weaklyChordal == null) {
            ArrayList<ArrayList<Pair<Integer, Integer>>> separators = new ArrayList<>();
            for (E edge : graph.edgeSet()) {
                separators.addAll(computeSeparators(edge));
            }

            if (separators.size() > 0) {
                sortSeparatorsList(separators);
                int separatorsNum = 1;
                ArrayList<Pair<Integer, Integer>> original = separators.get(0);
                ArrayList<ArrayList<Integer>> coConnectedComponents = computeCoConnectedComponents(original);
                for (ArrayList<Pair<Integer, Integer>> separator : separators) {
                    if (!equalSeparators(original, separator)) {
                        original = separator;
                        ++separatorsNum;
                        if (n + m < separatorsNum) {
                            weaklyChordal = false;
                            break;
                        } else {
                            coConnectedComponents = computeCoConnectedComponents(original);
                        }
                    }
                    if (!checkLabels(coConnectedComponents, separator)) {
                        weaklyChordal = false;
                        break;
                    }
                }
            } else {
                weaklyChordal = true;
            }
        }
        return weaklyChordal;
    }

    /**
     * Computes all minimal separators in the neighbourhood of the {@code edge} and returns them.
     * This is done via depth-first search. Following colouring is used: 2 (black) - already visited vertex,
     * 1 (red) - vertex in the neighbourhood of the {@code edge}, 1 (white) - unvisited vertex. The result can
     * contain duplicates.
     *
     * @param edge the edge, whose neighbourhood is being explored
     * @return computed minimal separators in the neighbourhood of the {@code edge}
     */
    private List<ArrayList<Pair<Integer, Integer>>> computeSeparators(E edge) {
        V source = graph.getEdgeSource(edge);
        V target = graph.getEdgeTarget(edge);
        if (source != target) {
            int sourceIndex = vertices.get(source);
            int targetIndex = vertices.get(target);

            ArrayList<Integer> labeling = getLabeling(source, target);
            ArrayList<ArrayList<Pair<Integer, Integer>>> separators = new ArrayList<>();
            ArrayList<ArrayList<ArrayList<Pair<Integer, Integer>>>> vInSeparator =
                    new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                vInSeparator.add(new ArrayList<>());
            }

            //0 - unvisited (white), 1 - neighbour of the edge (red), 2 - visited (black)
            ArrayList<Byte> dfsArr = new ArrayList<>(Collections.nCopies(n, (byte) -1));
            for (V vertex : graph.vertexSet()) {
                int vertexIndex = vertices.get(vertex);
                if (labeling.get(vertexIndex) != null) {
                    dfsArr.set(vertexIndex, (byte) 1);
                } else {
                    dfsArr.set(vertexIndex, (byte) 0);
                }
            }
            dfsArr.set(sourceIndex, (byte) 2);
            dfsArr.set(targetIndex, (byte) 2);

            for (V vertex : graph.vertexSet()) {
                int vertexIndex = vertices.get(vertex);
                if (dfsArr.get(vertexIndex) == 0) {
                    ArrayList<Pair<Integer, Integer>> separator = new ArrayList<>();
                    separators.add(separator);
                    dfsVisit(vertex, dfsArr, separator, vInSeparator);
                }
            }
            for (int vertex = 0; vertex < n; vertex++) {
                ArrayList<ArrayList<Pair<Integer, Integer>>> listOfSeparators = vInSeparator.get(vertex);
                for (ArrayList<Pair<Integer, Integer>> separator : listOfSeparators) {
                    separator.add(new Pair<>(vertex, labeling.get(vertex)));
                }
            }

            return separators;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Visits the {@code vertex}. Adds to the bucket of the {code vertex} in the {@code vInSeparators} a reference
     * to the {@code separator} for all red vertices in the neighbourhood of the {@code vertex}. Recursively visits
     * all white vertices in the neighbourhood.
     *
     * @param vertex        the currently visited vertex
     * @param dfsArr        the depth-first vertex labeling
     * @param separator     the separator, to which all found red vertices will be added
     * @param vInSeparators the list of buckets, which contains references to separators vertices belong to
     */
    private void dfsVisit(V vertex, ArrayList<Byte> dfsArr, ArrayList<Pair<Integer, Integer>> separator,
                          ArrayList<ArrayList<ArrayList<Pair<Integer, Integer>>>> vInSeparators) {
        int vertexIndex = vertices.get(vertex);
        dfsArr.set(vertexIndex, (byte) 2);
        for (E edge : graph.edgesOf(vertex)) {
            V opposite = Graphs.getOppositeVertex(graph, edge, vertex);
            int oppositeIndex = vertices.get(opposite);
            if (dfsArr.get(oppositeIndex) == 0) {
                dfsVisit(opposite, dfsArr, separator, vInSeparators);
            } else if (dfsArr.get(oppositeIndex) == 1) {
                vInSeparators.get(oppositeIndex).add(separator);
            }
        }
    }

    /**
     * Computes the labeling of the neighbourhood of the vertices {@code source} and {@code target}.
     * Vertex from the neighbourhood is labeled with "1" if it sees only {@code source}, "2" is it sees
     * only {@code target}, and "3" if it sees both vertices.
     *
     * @param source the vertex, whose neighbourhood is being labeled
     * @param target another vertex, whose neighbourhood is being labeled
     * @return the computed labeling with the respect to the rule described above
     */
    private ArrayList<Integer> getLabeling(V source, V target) {
        ArrayList<Integer> labeling = new ArrayList<>(Collections.nCopies(n, null));
        for (E edge : graph.edgesOf(source)) {
            labeling.set(vertices.get(Graphs.getOppositeVertex(graph, edge, source)), 1);
        }
        for (E edge : graph.edgesOf(target)) {
            Integer oppositeIndex = vertices.get(Graphs.getOppositeVertex(graph, edge, target));
            if (labeling.get(oppositeIndex) != null) {
                labeling.set(oppositeIndex, 3);
            } else {
                labeling.set(oppositeIndex, 2);
            }
        }
        return labeling;
    }

    /**
     * Sorts the {@code separators} using bucket sort
     *
     * @param separators the list of separators to be sorted
     */
    private void sortSeparatorsList(List<ArrayList<Pair<Integer, Integer>>> separators) {
        Queue<ArrayList<Pair<Integer, Integer>>> mainQueue = new LinkedList<>();
        int maxSeparatorLength = 0;
        for (ArrayList<Pair<Integer, Integer>> separator : separators) {
            if (separator.size() > maxSeparatorLength) {
                maxSeparatorLength = separator.size();
            }
            mainQueue.add(separator);
        }
        separators.clear();
        ArrayList<Queue<ArrayList<Pair<Integer, Integer>>>> queues = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            queues.add(new LinkedList<>());
        }
        for (int i = 0; i < maxSeparatorLength; i++) {
            while (!mainQueue.isEmpty()) {
                ArrayList<Pair<Integer, Integer>> separator = mainQueue.remove();
                if (i >= separator.size()) {
                    separators.add(separator);
                } else {
                    queues.get(separator.get(separator.size() - i - 1).getFirst()).add(separator);
                }
            }
            for (Queue<ArrayList<Pair<Integer, Integer>>> queue : queues) {
                mainQueue.addAll(queue);
                queue.clear();
            }
        }
        separators.addAll(mainQueue);
    }

    /**
     * Compares two separators for equality. Labeling of the vertices in the separators isn't considered
     *
     * @param sep1 first separator
     * @param sep2 second separator
     * @return true, if the separators are equal, false otherwise
     */
    private boolean equalSeparators(ArrayList<Pair<Integer, Integer>> sep1, ArrayList<Pair<Integer, Integer>> sep2) {
        if (sep1.size() != sep2.size()) {
            for (int i = 0; i < sep1.size(); i++) {
                if (!sep2.get(i).getFirst().equals(sep1.get(i).getFirst())) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Computes the connected components of the complement of the graph induces by the vertices of the {@code separator}.
     * They are also called "coconnected components". The running time is $\mathcal{O}(|V| + |E|)$.
     *
     * @param separator the separators, whose coconnected components are computed
     * @return the coconected of the {@code separator}
     */
    private ArrayList<ArrayList<Integer>> computeCoConnectedComponents(ArrayList<Pair<Integer, Integer>> separator) {
        ArrayList<ArrayList<Integer>> coConnectedComponents = new ArrayList<>();

        ArrayList<Set<Integer>> bucketsByLabel = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            bucketsByLabel.add(new HashSet<>());
        }
        ArrayList<Integer> labels = new ArrayList<>(Collections.nCopies(n, -1));
        Set<Integer> unvisited = new HashSet<>(separator.size());
        separator.forEach(pair -> {
            unvisited.add(pair.getFirst());
            labels.set(pair.getFirst(), 0);
        });
        bucketsByLabel.set(0, unvisited);
        int minLabel = 0;

        while (unvisited.size() > 0) {
            ArrayList<Integer> coConnectedComponent = new ArrayList<>();
            do {
                while (!bucketsByLabel.get(minLabel).isEmpty()) {
                    Integer vertex = bucketsByLabel.get(minLabel).iterator().next();
                    bucketsByLabel.get(minLabel).remove(vertex);
                    coConnectedComponent.add(vertex);
                    labels.set(vertex, -1);

                    for (E edge : graph.edgesOf(indices.get(vertex))) {
                        Integer opposite = vertices.get(Graphs.getOppositeVertex(graph, edge, indices.get(vertex)));
                        Integer oppositeLabel = labels.get(opposite);
                        if (oppositeLabel != -1) {
                            putToNextBucket(opposite, oppositeLabel, bucketsByLabel, labels);
                        }
                    }
                }
                ++minLabel;
            } while (minLabel != coConnectedComponent.size());
            reload(bucketsByLabel, labels, minLabel);

            coConnectedComponents.add(coConnectedComponent);
            minLabel = 0;
        }
        return coConnectedComponents;
    }

    /**
     * Moves the {@code vertex} to the next bucket.
     *
     * @param vertex         the vertex to be moved
     * @param vertexLabel    the label of the {@code vertex}
     * @param bucketsByLabel the buckets, in which vertices are stored
     * @param labels         the labels of the vertices
     */
    private void putToNextBucket(Integer vertex, Integer vertexLabel, ArrayList<Set<Integer>> bucketsByLabel, ArrayList<Integer> labels) {
        bucketsByLabel.get(vertexLabel).remove(vertex);
        bucketsByLabel.get(vertexLabel + 1).add(vertex);
        labels.set(vertex, vertexLabel + 1);
    }

    /**
     * Moves all vertices from the bucket with label {@code minLabel} to the bucket with label 0. Clears the
     * bucket with label {@code minLabel}. Updates the labeling accordingly.
     *
     * @param bucketsByLabel the buckets vertices are stored in
     * @param labels         the labels of the vertices
     * @param minLabel       the minimum value of the non-empty bucket
     */
    private void reload(ArrayList<Set<Integer>> bucketsByLabel, ArrayList<Integer> labels, int minLabel) {
        if(minLabel != 0){
            Set<Integer> bucket = bucketsByLabel.get(minLabel);
            for (Integer vertex : bucket) {
                labels.set(vertex, 0);
                bucketsByLabel.get(0).add(vertex);
            }
            bucket.clear();
        }
    }

    /**
     * For a given coconnected component of the {@code separator} checks whether every vertex in it is seen
     * by al least one vertex on the edge that is separated by the {@code separator}
     *
     * @param coConnectedComponents the set of the coconected components of the {@code separator}
     * @param separator             minimal separator of some edge in the {@code graph}
     * @return true if the condition described above holds, false otherwise
     */
    private boolean checkLabels(ArrayList<ArrayList<Integer>> coConnectedComponents, ArrayList<Pair<Integer, Integer>> separator) {
        ArrayList<Integer> vertexLabels = new ArrayList<>(Collections.nCopies(n, null));
        for (Pair<Integer, Integer> vertexAndLabel : separator) {
            vertexLabels.set(vertexAndLabel.getFirst(), vertexAndLabel.getSecond());
        }
        for (ArrayList<Integer> coConnectedComponent : coConnectedComponents) {
            int label = 0;
            for (Integer vertex : coConnectedComponent) {
                if (vertexLabels.get(vertex) != 3) {
                    if (label != 0) {
                        if (label != vertexLabels.get(vertex)) {
                            return false;
                        }
                    } else {
                        label = vertexLabels.get(vertex);
                    }
                }
            }
        }
        return false;
    }
}
