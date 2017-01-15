/*
 * (C) Copyright 2008-2017, by Andrew Newell and Contributors.
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

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.cycle.*;
import org.jgrapht.graph.*;

/**
 * This algorithm will check whether a graph is Eulerian (hence it contains an
 * <a href="http://mathworld.wolfram.com/EulerianCircuit.html">Eulerian circuit</a>). Also, if a
 * graph is Eulerian, the caller can obtain a list of vertices making up the Eulerian circuit. An
 * Eulerian circuit is a circuit which traverses each edge exactly once.
 *
 * @author Andrew Newell
 * @since Dec 21, 2008
 * @deprecated use {@link HierholzerEulerianCycle} instead
 */
@Deprecated
public abstract class EulerianCircuit
{

    /**
     * Test whether a graph is Eulerian. An undirected graph is Eulerian if it is connected and each
     * vertex has an even degree.
     *
     * @param graph the input graph
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     *
     * @return true if the graph is Eulerian, false otherwise
     */
    public static <V, E> boolean isEulerian(UndirectedGraph<V, E> graph)
    {
        return new HierholzerEulerianCycle<V, E>().isEulerian(graph);
    }

    /**
     * This method will return a list of vertices which represents the Eulerian circuit of the
     * graph.
     *
     * @param g The graph to find an Eulerian circuit
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     *
     * @return null if no Eulerian circuit exists, or a list of vertices representing the Eulerian
     *         circuit if one does exist
     */
    public static <V, E> List<V> getEulerianCircuitVertices(UndirectedGraph<V, E> g)
    {
        // If the graph is not Eulerian then just return a null since no
        // Eulerian circuit exists
        if (!isEulerian(g)) {
            return null;
        }

        // The circuit will be represented by a linked list
        List<V> path = new LinkedList<>();
        UndirectedGraph<V, E> sg = new UndirectedSubgraph<>(g, null, null);
        path.add(sg.vertexSet().iterator().next());

        // Algorithm for finding an Eulerian circuit Basically this will find an
        // arbitrary circuit, then it will find another arbitrary circuit until
        // every edge has been traversed
        while (sg.edgeSet().size() > 0) {
            V v = null;

            // Find a vertex which has an edge that hasn't been traversed yet,
            // and keep its index position in the circuit list
            int index = 0;
            for (Iterator<V> iter = path.iterator(); iter.hasNext(); index++) {
                v = iter.next();
                if (sg.degreeOf(v) > 0) {
                    break;
                }
            }

            // Finds an arbitrary circuit of the current vertex and
            // appends this into the circuit list
            while (sg.degreeOf(v) > 0) {
                for (V temp : sg.vertexSet()) {
                    if (sg.containsEdge(v, temp)) {
                        path.add(index, temp);
                        sg.removeEdge(v, temp);
                        v = temp;
                        break;
                    }
                }
            }
        }
        return path;
    }
}

// End EulerianCircuit.java
