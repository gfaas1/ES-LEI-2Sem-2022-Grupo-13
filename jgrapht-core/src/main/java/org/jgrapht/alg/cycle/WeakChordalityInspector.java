package org.jgrapht.alg.cycle;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.AsUndirectedGraph;

import java.util.*;

public class WeakChordalityInspector<V, E> {
    private final int n;
    private final int m;
    private Graph<V, E> graph;
    private Map<V, Integer> vertices;
    private Map<Integer, V> indices;
    private Boolean weaklyChordal;

    public WeakChordalityInspector(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph);
        if (graph.getType().isDirected()) {
            this.graph = new AsUndirectedGraph<>(graph);
        }
        n = graph.vertexSet().size();
        m = graph.edgeSet().size();
        init();
    }

    private void init() {
        vertices = new HashMap<>(n);
        indices = new HashMap<>(n);
        int i = 0;
        for (V v : graph.vertexSet()) {
            indices.put(i, v);
            vertices.put(v, i++);
        }
    }

    public boolean isWeaklyChordal() {
        return lazyComputeWeakChordality();
    }

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

    List<ArrayList<Pair<Integer, Integer>>> computeSeparators(E edge) {
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

    private void putToNextBucket(Integer vertex, Integer vertexLabel, ArrayList<Set<Integer>> bucketsByLabel, ArrayList<Integer> labels) {
        bucketsByLabel.get(vertexLabel).remove(vertex);
        bucketsByLabel.get(vertexLabel + 1).add(vertex);
        labels.set(vertex, vertexLabel + 1);
    }

    private void reload(ArrayList<Set<Integer>> bucketsByLabel, ArrayList<Integer> labels, int minLabel) {
        Set<Integer> bucket = bucketsByLabel.get(minLabel);
        for (Integer vertex : bucket) {
            labels.set(vertex, 0);
            bucketsByLabel.get(0).add(vertex);
        }
        bucket.clear();
    }

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
