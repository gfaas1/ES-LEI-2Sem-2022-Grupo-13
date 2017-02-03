/*
 * (C) Copyright 2005-2017, by Christian Soltenborn and Contributors.
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
package org.jgrapht.alg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.interfaces.StrongConnectivityAlgorithm;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.graph.DirectedSubgraph;

/**
 * Base implementation of the strongly connected components algorithm.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Christian Soltenborn
 * @author Christian Hammer
 * @author Dimitrios Michail
 */
abstract class AbstractStrongConnectivityInspector<V, E>
    implements StrongConnectivityAlgorithm<V, E>
{
    protected final DirectedGraph<V, E> graph;
    protected List<Set<V>> stronglyConnectedSets;
    protected List<DirectedSubgraph<V, E>> stronglyConnectedSubgraphs;

    public AbstractStrongConnectivityInspector(DirectedGraph<V, E> graph)
    {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
    }

    @Override
    public DirectedGraph<V, E> getGraph()
    {
        return graph;
    }

    @Override
    public boolean isStronglyConnected()
    {
        return stronglyConnectedSets().size() == 1;
    }

    @Override
    public List<DirectedSubgraph<V, E>> stronglyConnectedSubgraphs()
    {
        if (stronglyConnectedSubgraphs == null) {
            List<Set<V>> sets = stronglyConnectedSets();
            stronglyConnectedSubgraphs = new Vector<>(sets.size());

            for (Set<V> set : sets) {
                stronglyConnectedSubgraphs.add(new DirectedSubgraph<>(graph, set, null));
            }
        }
        return stronglyConnectedSubgraphs;
    }

    @Override
    public DirectedGraph<DirectedSubgraph<V, E>, DefaultEdge> getCondensation()
    {
        List<Set<V>> sets = stronglyConnectedSets();

        DirectedGraph<DirectedSubgraph<V, E>, DefaultEdge> condensation =
            new DirectedPseudograph<>(DefaultEdge.class);
        Map<V, DirectedSubgraph<V, E>> vertexToComponent = new HashMap<>();

        for (Set<V> set : sets) {
            DirectedSubgraph<V, E> component = new DirectedSubgraph<>(graph, set, null);
            condensation.addVertex(component);
            for (V v : set) {
                vertexToComponent.put(v, component);
            }
        }

        for (E e : graph.edgeSet()) {
            V s = graph.getEdgeSource(e);
            DirectedSubgraph<V, E> sComponent = vertexToComponent.get(s);

            V t = graph.getEdgeTarget(e);
            DirectedSubgraph<V, E> tComponent = vertexToComponent.get(t);

            if (sComponent != tComponent) { // reference equal on purpose
                condensation.addEdge(sComponent, tComponent);
            }
        }

        return condensation;
    }

}
