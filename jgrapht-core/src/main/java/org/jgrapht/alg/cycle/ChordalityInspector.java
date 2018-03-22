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
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.traverse.GraphIterator;
import org.jgrapht.traverse.LexBreadthFirstIterator;
import org.jgrapht.traverse.MaximumCardinalityIterator;

import java.util.*;

/**
 * Allows testing chordality of a graph. The inspected {@code graph} is specified at construction time
 * and cannot be modified. Currently chordality of a graph is tested via {@link MaximumCardinalityIterator}
 * by default. When {@link IterationOrder#LEX_BFS} is specified as a second constructor parameter, this
 * {@code ChordalityInspector} uses {@link LexBreadthFirstIterator} to compute perfect elimination order.
 * <p>
 * A <a href="https://en.wikipedia.org/wiki/Chordal_graph">chordal graph</a> is one in which all cycles of
 * four or more vertices have a chord, which is an edge that is not part of the cycle but connects two vertices
 * of the cycle.
 * <p>
 * A graph is chordal iff its the vertices can be arranged into a perfect elimination order. More than one perfect
 * elimination order may exist for a given graph. Either maximum cardinality search or lexicographical breadth-first
 * search can be used to produce such an order.
 * <p>
 * Both lexicographical BFS and maximum cardinality search run in $\mathcal{O}(|V| + |E|)$. Checking whether given order
 * is the perfect elimination order via {@link ChordalityInspector#isPerfectEliminationOrder(List)} takes
 * $\mathcal{O}(|V| + |E|)$ as well. Finding  hole of the graph, if it isn't chordal, takes O(|V| + |E|).
 * So, overall time complexity of the method {@link ChordalityInspector#isChordal()} is $\mathcal{O}(|V| + |E|)$.
 *
 * @param <V> the graph vertex type.
 * @param <E> the graph edge type.
 * @author Timofey Chudakov
 * @see LexBreadthFirstIterator
 * @see MaximumCardinalityIterator
 * @since March 2018
 */
public class ChordalityInspector<V, E> implements VertexColoringAlgorithm<V> {
    /**
     * Stores the type of iterator used by this {@code ChordalityInspector}.
     */
    private final IterationOrder iterationOrder;
    /**
     * The inspected graph.
     */
    private Graph<V, E> graph;
    /**
     * Contains true if the graph is chordal, otherwise false. Is null before the first call to the
     * {@link ChordalityInspector#isChordal()}.
     */
    private Boolean chordal = null;
    /**
     * Order produced by {@code orderIterator}.
     */
    private List<V> order;
    /**
     * Iterator used for producing perfect elimination order.
     */
    private GraphIterator<V, E> orderIterator;

    /**
     * A chordless cycle of the inspected {@code graph}.
     */
    private GraphPath<V, E> hole;

    /**
     * A coloring of the inspected {@code graph}.
     */
    private Coloring<V> coloring;

    /**
     * A maximum independent set of the inspected {@code graph}.
     */
    private Set<V> maximumIndependentSet;

    /**
     * List of all maximal cliques of the inspected {@code graph}.
     */
    private Set<V> maximumClique;

    /**
     * Creates a chordality inspector for {@code graph}, which uses {@link MaximumCardinalityIterator}
     * as a default iterator.
     *
     * @param graph the graph for which a chordality inspector to be created.
     */
    public ChordalityInspector(Graph<V, E> graph) {
        this(graph, IterationOrder.MCS);
    }

    /**
     * Creates a chordality inspector for {@code graph}, which uses an iterator defined by the second
     * parameter as an internal iterator.
     *
     * @param graph          the graph for which a chordality inspector is to be created.
     * @param iterationOrder the constant, which defines iterator to be used by this {@code ChordalityInspector}.
     */
    public ChordalityInspector(Graph<V, E> graph, IterationOrder iterationOrder) {
        this.graph = Objects.requireNonNull(graph);
        this.iterationOrder = iterationOrder;
        this.hole = null;
        if (graph.getType().isDirected()) {
            this.graph = new AsUndirectedGraph<>(graph);
        }
        if (iterationOrder == IterationOrder.MCS) {
            this.orderIterator = new MaximumCardinalityIterator<>(graph);
        } else {
            this.orderIterator = new LexBreadthFirstIterator<>(graph);
        }
    }

    /**
     * Checks whether the inspected graph is chordal.
     *
     * @return true if this graph is chordal, otherwise false.
     */
    public boolean isChordal() {
        if (chordal == null) {
            order = lazyComputeOrder();
            chordal = isPerfectEliminationOrder(order, true);
        }
        return chordal;
    }

    /**
     * Checks whether the chordality of the {@code graph} has been computed already.
     *
     * @return true, if the chordality of the graph has been computed already, false otherwise.
     */
    public boolean isComputed() {
        return chordal != null;
    }

    /**
     * Returns the computed vertex order. In the case where inspected graph is chordal, returned order
     * is a perfect elimination order.
     *
     * @return computed vertex order.
     */
    public List<V> getSearchOrder() {
        return lazyComputeOrder();
    }

    /**
     * Returns some chordless cycle on 4 or more vertices of the {@code graph}.
     * If the {@code graph} is chordal, returns null.
     *
     * @return chordless cycle of the {@code graph} if the graph isn't chordal, null otherwise.
     */
    public GraphPath<V, E> getHole() {
        if (!isComputed()) {
            isChordal();
        }
        return hole;
    }

    /**
     * Checks whether the vertices in the {@code vertexOrder} are in perfect elimination order with
     * respect to the inspected graph. Returns false, if the inspected graph isn't chordal.
     *
     * @param vertexOrder the sequence of vertices of the {@code graph}.
     * @return true if the {@code graph} is chordal and the vertices in {@code vertexOrder} are in
     * perfect elimination order, otherwise false.
     */
    public boolean isPerfectEliminationOrder(List<V> vertexOrder) {
        return isPerfectEliminationOrder(vertexOrder, false);
    }

    /**
     * Returns a coloring of the inspected {@code graph}. If the graph isn't
     * chordal, returns null.
     *
     * @return a coloring of the {@code graph} if it is chordal, null otherwise.
     */
    @Override
    public Coloring<V> getColoring() {
        return lazyComputeColoring();
    }

    /**
     * Returns a maximum independent set of the inspected {@code graph}.
     * If the graph isn't chordal, returns null.
     *
     * @return a maximum independent set of the {@code graph} if it is chordal, false otherwise.
     */
    public Set<V> getMaximumIndependentSet() {
        return lazyComputeMaximumIndependentSet();
    }

    /**
     * Returns a maximum clique of the inspected {@code graph}. If the graph isn't chordal,
     * returns null.
     *
     * @return a maximum clique of the {@code graph} if it is chordal, null otherwise.
     */
    public Set<V> getMaximumClique() {
        return lazyComputeMaximumClique();
    }

    /**
     * Computes vertex order via {@code orderIterator}.
     *
     * @return computed order.
     */
    private List<V> lazyComputeOrder() {
        if (order == null) {
            int vertexNum = graph.vertexSet().size();
            order = new ArrayList<>(vertexNum);
            for (int i = 0; i < vertexNum; i++) {
                order.add(orderIterator.next());
            }
        }
        return order;
    }

    /**
     * Lazily computes the coloring of the graph. Returns null if the graph isn't chordal.
     *
     * @return coloring of the graph if it is chordal, null otherwise.
     */
    private Coloring<V> lazyComputeColoring() {
        if (coloring == null) {
            if (!isComputed()) {
                isChordal();
            }
            if (chordal) {
                Map<V, Integer> vertexColoring = new HashMap<>(order.size());
                Map<V, Integer> vertexInOrder = getVertexInOrder(order);
                for (V vertex : order) {
                    vertexColoring.put(vertex, getPredecessors(vertexInOrder, vertex).stream().map(vertexColoring::get)
                            .max(Integer::compareTo).orElse(-1) + 1);
                }
                int maxColor = order.stream().map(vertexColoring::get).max(Integer::compareTo).orElse(-1) + 1;
                coloring = new ColoringImpl<>(vertexColoring, maxColor);
                return coloring;
            } else {
                return null;
            }
        } else {
            return coloring;
        }
    }

    /**
     * Lazily computes a maximum independent set of the inspected {@code graph}.
     * If the graph isn't chordal, returns null.
     *
     * @return a maximum independent set of the {@code graph} if it is chordal, false otherwise.
     */
    private Set<V> lazyComputeMaximumIndependentSet() {
        if (maximumIndependentSet == null) {
            if (!isComputed()) {
                isChordal();
            }
            if (chordal) {
                Set<V> restricted = new HashSet<>();
                Set<V> independent = new HashSet<>();
                ListIterator<V> reverse = order.listIterator(order.size());
                while (reverse.hasPrevious()) {
                    V previous = reverse.previous();
                    if (!restricted.contains(previous)) {
                        independent.add(previous);
                        for (E edge : graph.edgesOf(previous)) {
                            V opposite = Graphs.getOppositeVertex(graph, edge, previous);
                            if (!previous.equals(opposite)) {
                                restricted.add(opposite);
                            }
                        }
                    }
                }
                return maximumIndependentSet = independent;
            } else {
                return null;
            }
        } else {
            return maximumIndependentSet;
        }
    }

    /**
     * Lazily computes some maximum clique of the {@code graph}. Returns null if the graph isn't chordal.
     *
     * @return @return a maximum clique of the {@code graph} if it is chordal, null otherwise.
     */
    private Set<V> lazyComputeMaximumClique() {
        if (maximumClique == null) {
            if(!isComputed()){
                isChordal();
            }
            if (chordal) {
                lazyComputeColoring();
                Map<V, Integer> vertexInOrder = getVertexInOrder(order);
                Map.Entry<V, Integer> maxEntry = coloring.getColors().entrySet().stream().max(
                        Comparator.comparing(Map.Entry::getValue)).orElse(null);
                if (maxEntry == null) {
                    return new HashSet<>();
                } else {
                    maximumClique = getPredecessors(vertexInOrder, maxEntry.getKey());
                    maximumClique.add(maxEntry.getKey());
                    return maximumClique;
                }
            } else {
                return null;
            }
        } else {
            return maximumClique;
        }
    }

    /**
     * Checks whether the vertices in the {@code vertexOrder} are in perfect elimination order with
     * respect to the inspected graph. Returns false, if the inspected graph isn't chordal. If
     * {@code computeHole} is true and the inspected graph isn't chordal, computes some chordless
     * cycle in the inspected {@code graph}.
     *
     * @param vertexOrder the sequence of vertices of the {@code graph}.
     * @param computeHole tells whether to compute the hole if the graph isn't chordal.
     * @return true if the {@code graph} is chordal and the vertices in {@code vertexOrder} are in
     * perfect elimination order, otherwise false.
     */
    private boolean isPerfectEliminationOrder(List<V> vertexOrder, boolean computeHole) {
        Set<V> graphVertices = graph.vertexSet();
        if (graphVertices.size() == vertexOrder.size() && graphVertices.containsAll(vertexOrder)) {
            return isPerfectEliminationOrder(vertexOrder, getVertexInOrder(vertexOrder), computeHole);
        } else {
            return false;
        }
    }

    /**
     * Returns a map containing vertices from the {@code vertexOrder} mapped to their
     * indices in {@code vertexOrder}.
     *
     * @param vertexOrder a list with vertices.
     * @return a mapping of vertices from {@code vertexOrder} to their indices in {@code vertexOrder}.
     */
    private Map<V, Integer> getVertexInOrder(List<V> vertexOrder) {
        Map<V, Integer> vertexInOrder = new HashMap<>(vertexOrder.size());
        int i = 0;
        for (V vertex : vertexOrder) {
            vertexInOrder.put(vertex, i++);
        }
        return vertexInOrder;
    }

    /**
     * Checks whether the vertices in the {@code vertexOrder} are in perfect elimination order.
     * Returns false, if the inspected graph isn't chordal.
     *
     * @param vertexOrder   the sequence of vertices of {@code graph}.
     * @param vertexInOrder maps every vertex in {@code graph} to its position in {@code vertexOrder}.
     * @param computeHole   tells whether to compute the hole if the graph isn't chordal.
     * @return true if the {@code graph} is chordal and the vertices in {@code vertexOrder} are in
     * perfect elimination order.
     */
    private boolean isPerfectEliminationOrder(List<V> vertexOrder, Map<V, Integer> vertexInOrder, boolean computeHole) {
        for (V vertex : vertexOrder) {
            Set<V> predecessors = getPredecessors(vertexInOrder, vertex);
            if (predecessors.size() > 0) {
                V maxPredecessor = Collections.max(predecessors, Comparator.comparingInt(vertexInOrder::get));
                for (V predecessor : predecessors) {
                    if (!predecessor.equals(maxPredecessor) && !graph.containsEdge(predecessor, maxPredecessor)) {
                        if (computeHole) {
                            int position = vertexInOrder.get(vertex);
                            Set<V> subgraph = new HashSet<>(position + 1);
                            for (int i = 0; i <= position; i++) {
                                subgraph.add(order.get(i));
                            }
                            findHole(predecessor, vertex, maxPredecessor, subgraph);
                        }
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Computes a chordless cycle from the vertices of {@code subgraph} of the inspected {@code graph}
     * with vertices {@code a}, {@code b} and {@code c} on this cycle (there must be no edge between
     * {@code a} and {@code c}.
     *
     * @param a        vertex that belongs to the cycle
     * @param b        vertex that belongs to the cycle
     * @param c        vertex that belongs to the cycle
     * @param subgraph vertices that define a subgraph of the {@code graph}, which
     *                 contains chordless cycle.
     */
    private void findHole(V a, V b, V c, Set<V> subgraph) {
        List<V> cycle = new ArrayList<>(Arrays.asList(a, b, c));
        Map<V, Boolean> visited = new HashMap<>(subgraph.size());
        for (V vertex : subgraph) {
            visited.put(vertex, false);
        }

        findCycle(cycle, visited, a, b, c);
        cycle = minimizeCycle(cycle);
        hole = new GraphWalk<>(graph, cycle, 0);
    }

    /**
     * Computes some cycle in the graph on the vertices from the domain of the map {@code visited}.
     * More precisely, finds some path from {@code middle} to {@code finish}.
     * The vertex {@code middle} isn't the endpoint of any chord in this cycle.
     *
     * @param cycle   already computed part of the cycle
     * @param visited the map that defines which vertex has been visited by this method
     * @param finish  the last vertex in the cycle.
     * @param middle  the vertex, which must be adjacent onl
     * @param current currently examined vertex.
     */
    private void findCycle(List<V> cycle, Map<V, Boolean> visited, V finish, V middle, V current) {
        visited.put(current, true);
        for (E edge : graph.edgesOf(current)) {
            V opposite = Graphs.getOppositeVertex(graph, edge, current);
            if ((visited.containsKey(opposite) && !visited.get(opposite) && !graph.containsEdge(opposite, middle)
                    && !opposite.equals(middle)) || opposite.equals(finish)) {
                cycle.add(opposite);
                if (opposite.equals(finish)) {
                    return;
                }
                findCycle(cycle, visited, finish, middle, opposite);
                if (cycle.get(cycle.size() - 1).equals(finish)) {
                    return;
                } else {
                    cycle.remove(cycle.size() - 1);
                }
            }
        }
    }

    /**
     * Minimizes the cycle represented by the list {@code cycle}. More precisely it
     * retains first 2 vertices and finds a chordless cycle starting from the third vertex.
     *
     * @param cycle vertices of the graph that represent the cycle.
     * @return a chordless cycle
     */
    private List<V> minimizeCycle(List<V> cycle) {
        Set<V> cycleVertices = new HashSet<>(cycle);
        cycleVertices.remove(cycle.get(1));
        List<V> minimized = new ArrayList<>();
        minimized.add(cycle.get(0));
        minimized.add(cycle.get(1));
        for (int i = 2; i < cycle.size() - 1; ) {
            V vertex = cycle.get(i);
            minimized.add(vertex);
            cycleVertices.remove(vertex);
            Set<V> forward = new HashSet<>();
            for (E edge : graph.edgesOf(vertex)) {
                V opposite = Graphs.getOppositeVertex(graph, edge, vertex);
                if (cycleVertices.contains(opposite)) {
                    forward.add(opposite);
                }
            }
            for (V forwardVertex : forward) {
                if (cycleVertices.contains(forwardVertex)) {
                    do {
                        cycleVertices.remove(cycle.get(i));
                        i++;
                    } while (i < cycle.size() && !cycle.get(i).equals(forwardVertex));
                }
            }
        }
        minimized.add(cycle.get(cycle.size() - 1));
        return minimized;
    }

    /**
     * Returns the predecessors of {@code vertex} in the order defined by {@code map}. More precisely,
     * returns those of {@code vertex}, whose mapped index in {@code map} is less then the index of {@code vertex}.
     *
     * @param vertexInOrder defines the mapping of vertices in {@code graph} to their indices in order.
     * @param vertex        the vertex whose predecessors in order are to be returned.
     * @return the predecessors of {@code vertex} in order defines by {@code map}.
     */
    private Set<V> getPredecessors(Map<V, Integer> vertexInOrder, V vertex) {
        Set<V> predecessors = new HashSet<>();
        Integer vertexPosition = vertexInOrder.get(vertex);
        Set<E> edges = graph.edgesOf(vertex);
        for (E edge : edges) {
            V oppositeVertex = Graphs.getOppositeVertex(graph, edge, vertex);
            Integer destPosition = vertexInOrder.get(oppositeVertex);
            if (destPosition < vertexPosition) {
                predecessors.add(oppositeVertex);
            }
        }
        return predecessors;
    }

    /**
     * Returns the type of iterator used in this {@code ChordalityInspector}
     *
     * @return the type of iterator used in this {@code ChordalityInspector}
     */
    public IterationOrder getIterationOrder() {
        return iterationOrder;
    }

    /**
     * Specifies internal iterator type.
     */
    public enum IterationOrder {
        MCS, LEX_BFS,
    }
}


