/*
 * (C) Copyright 2018-2018, by Alexandru Valeanu and Contributors.
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
package org.jgrapht.alg.spanning;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;

import java.lang.reflect.Array;
import java.util.*;

/**
 * An implementation of <a href="http://en.wikipedia.org/wiki/Prim's_algorithm"> Prim's
 * algorithm</a> that finds a minimum spanning tree/forest subject to connectivity of the supplied
 * weighted undirected graph. The algorithm was developed by Czech mathematician V. Jarník and later
 * independently by computer scientist Robert C. Prim and rediscovered by E. Dijkstra.
 *
 * This implementation runs in $O(|V|^2)$.
 *
 * If your graph is sparse consider using {@link PrimMinimumSpanningTree}.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Alexandru Valeanu
 */
public class PrimMinimumSpanningTreeDenseGraphs<V, E>
    implements SpanningTreeAlgorithm<E>
{
    private final Graph<V, E> g;

    /**
     * Construct a new instance of the algorithm.
     *
     * @param graph the input graph
     */
    public PrimMinimumSpanningTreeDenseGraphs(Graph<V, E> graph)
    {
        this.g = Objects.requireNonNull(graph, "Graph cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public SpanningTree<E> getSpanningTree(){
        Set<E> minimumSpanningTreeEdgeSet = new HashSet<>(g.vertexSet().size());
        double spanningTreeWeight = 0d;

        final int N = g.vertexSet().size();

        /*
         * Normalize the graph
         *   map each vertex to an integer (using a HashMap)
         *   keep the reverse mapping  (using an ArrayList)
         */
        Map<V, Integer> vertexMap = new HashMap<>();
        List<V> indexList = new ArrayList<>();
        for(V v : g.vertexSet()){
            vertexMap.put(v,vertexMap.size());
            indexList.add(v);
        }

        VertexInfo[] vertices = (VertexInfo[]) Array.newInstance(VertexInfo.class, N);

        for (int i = 0; i < N; i++) {
            vertices[i] = new VertexInfo();
            vertices[i].distance = Double.MAX_VALUE;
        }

        vertices[0].distance = 0;

        for (int step = 0; step < N; step++) {
            int u = -1;

            for (int i = 0; i < N; i++) {
                if (!vertices[i].spanned && (u == -1 || vertices[i].distance < vertices[u].distance))
                    u = i;
            }

            if (u == -1)
                break;


            V root = indexList.get(u);
            vertices[u].spanned = true;

            if (vertices[u].edgeFromParent != null) {
                minimumSpanningTreeEdgeSet.add(vertices[u].edgeFromParent);
                spanningTreeWeight += g.getEdgeWeight(vertices[u].edgeFromParent);
            }

            for (E e : g.edgesOf(root)) {
                V target = g.getEdgeTarget(e);

                if (target.equals(root))
                    target = g.getEdgeSource(e);

                int id = vertexMap.get(target);
                double cost = g.getEdgeWeight(e);

                if (!vertices[id].spanned && vertices[id].distance > cost) {
                    vertices[id].distance = cost;
                    vertices[id].edgeFromParent = e;
                }
            }
        }

        return new SpanningTreeImpl<>(minimumSpanningTreeEdgeSet, spanningTreeWeight);
    }

    private class VertexInfo {
        public boolean spanned;
        public double distance;
        public E edgeFromParent;
    }
}

// End PrimMinimumSpanningTree.java
