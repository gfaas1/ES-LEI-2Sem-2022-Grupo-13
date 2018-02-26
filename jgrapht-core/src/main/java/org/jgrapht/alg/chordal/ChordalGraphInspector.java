package org.jgrapht.alg.chordal;

import org.jgrapht.Graph;
import org.jgrapht.alg.util.Pair;

import java.util.*;

public class ChordalGraphInspector<V, E> {

    public boolean isChordal(Graph<V, E> graph) {
        return isPerfectEliminationOrder(graph, getLexicographicalBfsOrder(graph));
    }

    public boolean isPerfectEliminationOrder(Graph<V, E> graph, List<V> vertexOrder) {
        Set<V> graphVertices = graph.vertexSet();
        if (graphVertices.size() == vertexOrder.size() && graphVertices.containsAll(vertexOrder)) {
            Map<V, Integer> map = new HashMap<>();
            int i = 0;
            for (V vertex : vertexOrder) {
                map.put(vertex, i);
                ++i;
            }
            return isPerfectEliminationOrder(graph, vertexOrder, map);
        } else {
            return false;
        }
    }

    private boolean isPerfectEliminationOrder(Graph<V, E> graph, List<V> order, Map<V, Integer> map) {
        Set<Pair<V, V>> testEdges = new HashSet<>();
        for (V vertex : order) {
            Set<V> predecessors = getPredecessors(graph, map, vertex);
            if (predecessors.size() > 0) {
                V maxPredecessor = Collections.min(predecessors, Comparator.comparingInt(map::get));
                for (V predecessor : predecessors) {
                    if (predecessor.equals(maxPredecessor)) {
                        continue;
                    }
                    testEdges.add(new Pair<>(maxPredecessor, predecessor));
                }
                testEdges.add(new Pair<>(maxPredecessor, vertex));
            }
        }
        for (Pair<V, V> edge : testEdges) {
            if (!graph.containsEdge(edge.getFirst(), edge.getSecond())) {
                return false;
            }
        }
        return true;
    }

    private Set<V> getPredecessors(Graph<V, E> graph, Map<V, Integer> map, V vertex) {
        Set<V> predecessors = new HashSet<>();
        Integer vertexPosition = map.get(vertex);
        Set<E> edges = graph.edgesOf(vertex);
        for (E edge : edges) {
            V source = graph.getEdgeSource(edge);
            V target = graph.getEdgeTarget(edge);
            V dest = source.equals(vertex) ? target : source;
            Integer destPosition = map.get(dest);
            if (destPosition < vertexPosition) {
                predecessors.add(dest);
            }
        }
        return predecessors;
    }

    public List<V> getLexicographicalBfsOrder(Graph<V, E> graph) {
        Set<V> vertexSet = graph.vertexSet();
        if (vertexSet.size() > 0) {
            List<V> lexBfsOrder = new ArrayList<>(vertexSet.size());
            BucketList<V> bucketList = new BucketList<>(vertexSet);
            while (lexBfsOrder.size() < vertexSet.size()) {
                int vertexNum = vertexSet.size() - lexBfsOrder.size();
                V vertex = bucketList.poll();
                lexBfsOrder.add(vertex);
                Set<V> unvisitedNeighbours = getUnvisitedNeighbours(graph, bucketList, vertex);
                for (V unvisitedNeighbour : unvisitedNeighbours) {
                    bucketList.updateLabel(unvisitedNeighbour, vertexNum);
                }
            }
            return lexBfsOrder;
        } else {
            return new ArrayList<>();
        }
    }

    private Set<V> getUnvisitedNeighbours(Graph<V, E> graph, BucketList<V> bucketList, V vertex) {
        Set<V> unmapped = new HashSet<>();
        Set<E> edges = graph.edgesOf(vertex);
        for (E edge : edges) {
            V source = graph.getEdgeSource(edge);
            V target = graph.getEdgeTarget(edge);
            V dest = source.equals(vertex) ? target : source;
            if (bucketList.containsBucketWith(dest)) {
                unmapped.add(dest);
            }
        }
        return unmapped;
    }


}
