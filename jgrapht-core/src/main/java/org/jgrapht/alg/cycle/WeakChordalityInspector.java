/*
 * (C) Copyright 2018-2018, by Timofey Chudakov and Contributors.
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
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.generate.ComplementGraphGenerator;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.graph.Pseudograph;

import java.util.*;

/**
 * Tests whether a graph is <a href="http://www.graphclasses.org/classes/gc_14.html">weakly chordal</a>.
 * Weakly chordal graphs are also known as weakly triangulated graphs. Triangulated in the context of
 * chordality has a different meaning than triangulated in the context of planarity, where it refers
 * to a maximal planar graph, see: <a href="http://mathworld.wolfram.com/TriangulatedGraph.html">
 * http://mathworld.wolfram.com/TriangulatedGraph.html</a>
 * <p>
 * The following definitions are equivalent:
 * <ol>
 * <li> A graph is weakly chordal if it is (<a href="http://mathworld.wolfram.com/GraphAntihole.html">
 * anti-hole</a>,<a href="http://mathworld.wolfram.com/GraphHole.html">hole</a>)-free. In other words,
 * a graph is weakly chordal if it and its complement do not have
 * <a href="http://mathworld.wolfram.com/ChordlessCycle.html">chordless cycles</a> of length greater than 4.</li>
 * <li> A 2-pair in a graph is a pair of non-adjacent vertices $x$, $y$ such that every chordless path has
 * exactly two edges. A graph is weakly chordal if every connected
 * <a href="https://en.wikipedia.org/wiki/Induced_subgraph">induced subgraph</a> $H$ that is not a complete
 * graph, contains a 2-pair.</li>
 * </ol>
 * Chordal and weakly chordal graphs are <a href="http://mathworld.wolfram.com/PerfectGraph.html">perfect</a>.<br>
 * For more details, refer to: Hayward, R.B. Weakly triangulated graphs, Journal of Combinatorial Theory, Series B,
 * vol 39, Issue 3, pp 200-208, 1985.
 * <p>
 * The implementation in this class is based on: Lars Severin Skeide (2002)
 * <a href="http://www.ii.uib.no/~skeide/rec_wcg.pdf"><i>Recognizing weakly chordal graphs</i></a>.
 * Candidate Scientist Thesis in Informatics. Department of Informatics, University of Bergen, Norway.
 * The terminology used in this implementation is consistent with the one used in this thesis.
 * <p>
 * Both the runtime complexity and space complexity of the algorithm implemented in this class is $\mathcal{O}(|E|^2)$.<br>
 * The inspected {@code graph} is specified at the construction time and cannot be modified.
 * When the graph is externally modified, the behavior of the {@code WeakChordalityInspector} is undefined.
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
     * For finding minimal separators
     */
    private SeparatorFinder<V, E> separatorFinder;
    /**
     * Contains a hole or an anti-hole in the graph, if is isn't weakly chordal
     */
    private GraphPath<V, E> certificate;

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
        separatorFinder = new SeparatorFinder<>(graph);
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

    private boolean isComputed() {
        return weaklyChordal != null;
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

    public GraphPath<V, E> getCertificate() {
        lazyComputeWeakChordality();
        return certificate;
    }

    /**
     * Lazily tests the weak chordality of the {@code graph} and returns the computed value.
     *
     * @return true, if the inspected {@code graph} is weakly chordal, otherwise false.
     */
    private boolean lazyComputeWeakChordality() {
        if (weaklyChordal == null) {
            List<Pair<List<Pair<Integer, Integer>>, E>> globalSeparatorList = computeGlobalSeparatorList();

            if (globalSeparatorList.size() > 0) {
                Pair<Integer, Integer> pair;
                sortSeparatorsList(globalSeparatorList);
                int separatorsNum = 1;
                List<Pair<Integer, Integer>> original = globalSeparatorList.get(0).getFirst();
                List<List<Integer>> coConnectedComponents = computeCoConnectedComponents(graph, original);

                for (Pair<List<Pair<Integer, Integer>>, E> separator : globalSeparatorList) {
                    if (!equalSeparators(original, separator.getFirst())) {
                        original = separator.getFirst();
                        ++separatorsNum;
                        if (n + m < separatorsNum) {
                            return weaklyChordal = false;
                        } else {
                            coConnectedComponents = computeCoConnectedComponents(graph, original);
                        }
                    }
                    if ((pair = checkLabels(coConnectedComponents, separator.getFirst())) != null) {
                        E holeFormer = separator.getSecond();
                        V source = graph.getEdgeSource(holeFormer);
                        V target = graph.getEdgeTarget(holeFormer);

                        V sourceInSeparator = indices.get(pair.getFirst());
                        V targetInSeparator = indices.get(pair.getSecond());

                        if (!graph.containsEdge(source, sourceInSeparator)) {
                            V t = sourceInSeparator;
                            sourceInSeparator = targetInSeparator;
                            targetInSeparator = t;
                        }
                        if (graph.containsEdge(sourceInSeparator, targetInSeparator)) {
                            findAntiHole(sourceInSeparator, source, target, targetInSeparator);
                        } else {
                            findHole(sourceInSeparator, source, target, targetInSeparator);
                        }
                        return weaklyChordal = false;
                    }
                }

                return weaklyChordal = true;
            } else {

                return weaklyChordal = true;
            }
        }
        return weaklyChordal;
    }

    private List<Pair<List<Pair<Integer, Integer>>, E>> computeGlobalSeparatorList() {
        List<Pair<List<Pair<Integer, Integer>>, E>> globalSeparatorList = new ArrayList<>();
        for (E edge : graph.edgeSet()) {
            V source = graph.getEdgeSource(edge);
            V target = graph.getEdgeTarget(edge);
            if (source != target) {
                List<Set<V>> edgeSeparators = separatorFinder.findSeparators(edge);
                globalSeparatorList.addAll(reformatSeparatorList(edgeSeparators, edge));
            }
        }
        return globalSeparatorList;
    }

    /**
     * Computes all minimal separators in the neighborhood of the {@code edge} and returns them.
     * This is done via depth-first search. Following coloring is used: 2 (black) - already visited vertex,
     * 1 (red) - vertex in the neighborhood of the {@code edge}, 1 (white) - unvisited vertex. The result can
     * contain duplicates.
     *
     * @param edge the edge, whose neighborhood is being explored
     * @return computed minimal separators in the neighborhood of the {@code edge}
     */
    private List<Pair<List<Pair<Integer, Integer>>, E>> reformatSeparatorList(List<Set<V>> separators, E edge) {
        List<Integer> labeling = getLabeling(edge);
        List<Pair<List<Pair<Integer, Integer>>, E>> reformattedSeparators = new ArrayList<>();
        List<List<List<Pair<Integer, Integer>>>> vInSeparator =
                new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            vInSeparator.add(new ArrayList<>());
        }

        for (Set<V> computedSeparator : separators) {
            List<Pair<Integer, Integer>> reformattedSeparator = new ArrayList<>(computedSeparator.size());
            reformattedSeparators.add(new Pair<>(reformattedSeparator, edge));
            for (V vertex : computedSeparator) {
                int vertexIndex = vertices.get(vertex);
                vInSeparator.get(vertexIndex).add(reformattedSeparator);
            }
        }

        for (int vertex = 0; vertex < n; vertex++) {
            List<List<Pair<Integer, Integer>>> listOfSeparators = vInSeparator.get(vertex);
            for (List<Pair<Integer, Integer>> separator : listOfSeparators) {
                separator.add(new Pair<>(vertex, labeling.get(vertex)));
            }
        }

        return reformattedSeparators;

    }

    /**
     * Computes the labeling of the neighborhood of the vertices {@code source} and {@code target}.
     * Vertex from the neighborhood is labeled with "1" if it sees only {@code source}, "2" is it sees
     * only {@code target}, and "3" if it sees both vertices.
     *
     * @param edge
     * @return the computed labeling with the respect to the rule described above
     */
    private List<Integer> getLabeling(E edge) {
        V source = graph.getEdgeSource(edge);
        V target = graph.getEdgeTarget(edge);
        List<Integer> labeling = new ArrayList<>(Collections.nCopies(n, null));
        for (E sourceEdge : graph.edgesOf(source)) {
            labeling.set(vertices.get(Graphs.getOppositeVertex(graph, sourceEdge, source)), 1);
        }
        for (E targetEdge : graph.edgesOf(target)) {
            Integer oppositeIndex = vertices.get(Graphs.getOppositeVertex(graph, targetEdge, target));
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
    private void sortSeparatorsList(List<Pair<List<Pair<Integer, Integer>>, E>> separators) {
        Queue<Pair<List<Pair<Integer, Integer>>, E>> mainQueue = new LinkedList<>();
        int maxSeparatorLength = 0;
        for (Pair<List<Pair<Integer, Integer>>, E> separator : separators) {
            if (separator.getFirst().size() > maxSeparatorLength) {
                maxSeparatorLength = separator.getFirst().size();
            }
            mainQueue.add(separator);
        }
        separators.clear();
        List<Queue<Pair<List<Pair<Integer, Integer>>, E>>> queues = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            queues.add(new LinkedList<>());
        }
        for (int i = 0; i < maxSeparatorLength; i++) {
            while (!mainQueue.isEmpty()) {
                Pair<List<Pair<Integer, Integer>>, E> separator = mainQueue.remove();
                if (i >= separator.getFirst().size()) {
                    separators.add(separator);
                } else {
                    queues.get(separator.getFirst().get(separator.getFirst().size() - i - 1).getFirst()).add(separator);
                }
            }
            for (Queue<Pair<List<Pair<Integer, Integer>>, E>> queue : queues) {
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
    private boolean equalSeparators(List<Pair<Integer, Integer>> sep1, List<Pair<Integer, Integer>> sep2) {
        if (sep1.size() == sep2.size()) {
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
    private List<List<Integer>> computeCoConnectedComponents(Graph<V, E> graph, List<Pair<Integer, Integer>> separator) {
        List<List<Integer>> coConnectedComponents = new ArrayList<>();

        List<Set<Integer>> bucketsByLabel = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            bucketsByLabel.add(new HashSet<>());
        }
        List<Integer> labels = new ArrayList<>(Collections.nCopies(n, -1));
        Set<Integer> unvisited = new HashSet<>(separator.size());
        separator.forEach(pair -> {
            unvisited.add(pair.getFirst());
            labels.set(pair.getFirst(), 0);
        });
        bucketsByLabel.set(0, unvisited);
        int minLabel = 0;

        while (unvisited.size() > 0) {
            List<Integer> coConnectedComponent = new ArrayList<>();
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
    private void putToNextBucket(Integer vertex, Integer vertexLabel, List<Set<Integer>> bucketsByLabel, List<Integer> labels) {
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
    private void reload(List<Set<Integer>> bucketsByLabel, List<Integer> labels, int minLabel) {
        if (minLabel != 0) {
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
    private Pair<Integer, Integer> checkLabels(List<List<Integer>> coConnectedComponents, List<Pair<Integer, Integer>> separator) {
        List<Integer> vertexLabels = new ArrayList<>(Collections.nCopies(n, null));
        for (Pair<Integer, Integer> vertexAndLabel : separator) {
            vertexLabels.set(vertexAndLabel.getFirst(), vertexAndLabel.getSecond());
        }
        for (List<Integer> coConnectedComponent : coConnectedComponents) {
            int label = 0;
            Integer labelVertex = null;
            for (Integer vertex : coConnectedComponent) {
                if (vertexLabels.get(vertex) != 3) {
                    if (label != 0) {
                        if (label != vertexLabels.get(vertex)) {
                            return new Pair<>(labelVertex, vertex);
                        }
                    } else {
                        label = vertexLabels.get(vertex);
                        labelVertex = vertex;
                    }
                }
            }
        }
        return null;
    }

    private void findHole(V sourceInSeparator, V source, V target, V targetInSeparator) {
        this.certificate = findHole(graph, sourceInSeparator, source, target, targetInSeparator);
    }

    private void findAntiHole(V sourceInSeparator, V source, V target, V targetInSeparator) {
        ComplementGraphGenerator<V, E> generator = new ComplementGraphGenerator<>(graph, false);
        Graph<V, E> complement = new Pseudograph<>(graph.getEdgeFactory());
        generator.generateGraph(complement);

        E cycleFormer = complement.getEdge(source, targetInSeparator);
        V cycleSource = graph.getEdgeSource(cycleFormer);
        V cycleTarget = graph.getEdgeTarget(cycleFormer);

        SeparatorFinder<V, E> complementSeparatorFinder = new SeparatorFinder<>(complement);
        List<Set<V>> separators = complementSeparatorFinder.findSeparators(cycleFormer);
        List<Pair<List<Pair<Integer, Integer>>, E>> reformatted = reformatSeparatorList(separators, cycleFormer);

        sortSeparatorsList(reformatted);

        List<Pair<Integer, Integer>> original = reformatted.get(0).getFirst();
        List<List<Integer>> coConnectedComponents = computeCoConnectedComponents(complement, original);

        Pair<Integer, Integer> pair;
        for (Pair<List<Pair<Integer, Integer>>, E> separator : reformatted) {
            if (!equalSeparators(separator.getFirst(), original)) {
                original = separator.getFirst();
                coConnectedComponents = computeCoConnectedComponents(complement, separator.getFirst());
            }
            if ((pair = checkLabels(coConnectedComponents, separator.getFirst())) != null) {
                V cycleSourceInSeparator = indices.get(pair.getFirst());
                V cycleTargetInSeparator = indices.get(pair.getSecond());
                if (!complement.containsEdge(cycleSourceInSeparator, cycleSource)) {
                    V t = cycleSourceInSeparator;
                    cycleSourceInSeparator = cycleTargetInSeparator;
                    cycleTargetInSeparator = t;
                }
                this.certificate = findHole(complement, cycleSourceInSeparator, cycleSource,
                        cycleTarget, cycleTargetInSeparator);
                return;
            }
        }
    }

    private GraphPath<V, E> findHole(Graph<V, E> graph, V sourceInSeparator, V source, V target, V targetInSeparator) {
        Map<V, Boolean> visited = new HashMap<>(graph.vertexSet().size());
        for (V vertex : graph.vertexSet()) {
            visited.put(vertex, false);
        }
        visited.put(targetInSeparator, true);
        visited.put(target, true);
        visited.put(source, true);

        List<V> cycle = new ArrayList<>(Arrays.asList(targetInSeparator, target, source, sourceInSeparator));
        dfsVisit(sourceInSeparator, visited, cycle, graph, targetInSeparator, target, source);
        cycle = minimizeCycle(graph, cycle, target, targetInSeparator, source, sourceInSeparator);

        return new GraphWalk<>(graph, cycle, 0);
    }

    public void dfsVisit(V current, Map<V, Boolean> visited, List<V> cycle, Graph<V, E> graph, V tarInSep, V tar, V sour) {
        visited.put(current, true);
        if (graph.containsEdge(current, tarInSep)) {
            cycle.add(tarInSep);
            return;
        }
        for (V vertex : Graphs.neighborListOf(graph, current)) {
            if (!visited.get(vertex) && !graph.containsEdge(sour, vertex) && !graph.containsEdge(tar, vertex)) {
                cycle.add(vertex);
                dfsVisit(vertex, visited, cycle, graph, tarInSep, tar, sour);
                if (cycle.get(cycle.size() - 1).equals(tarInSep)) {
                    return;
                } else {
                    cycle.remove(cycle.size() - 1);
                }
            }
        }
    }

    public List<V> minimizeCycle(Graph<V, E> graph, List<V> cycle, V tar, V tarInSep, V sour, V sourInSep) {
        List<V> minimizedCycle = new ArrayList<>(Arrays.asList(tarInSep, tar, sour));
        Set<V> forwardVertices = new HashSet<>(cycle);
        forwardVertices.remove(tar);
        forwardVertices.remove(sour);
        forwardVertices.remove(sourInSep);

        for (int i = 3; i < cycle.size() - 1; ) {
            V current = cycle.get(i);
            minimizedCycle.add(current);
            forwardVertices.remove(current);

            Set<V> currentForward = new HashSet<>();
            for (V neighbor : Graphs.neighborListOf(graph, current)) {
                if (forwardVertices.contains(neighbor)) {
                    currentForward.add(neighbor);
                }
            }

            for (V forwardVertex : currentForward) {
                if (forwardVertices.contains(forwardVertex)) {
                    do {
                        forwardVertices.remove(cycle.get(i));
                        i++;
                    } while (i < cycle.size() && !cycle.get(i).equals(forwardVertex));
                }
            }
        }
        minimizedCycle.add(tarInSep);
        return minimizedCycle;
    }
}
