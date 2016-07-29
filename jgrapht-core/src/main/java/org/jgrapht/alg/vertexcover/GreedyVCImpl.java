/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
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
/* -----------------
 * GreedyVCImpl.java
 * -----------------
 * (C) Copyright 2003-2008, by Linda Buisman and Contributors.
 *
 * Original Author:  Linda Buisman
 * Contributor(s):   Barak Naveh
 *                   Christian Hammer
 *                   Joris Kinable
 *
 * $Id$
 *
 * Changes
 * -------
 * 06-Nov-2003 : Initial revision (LB);
 * 07-Jun-2005 : Made generic (CH);
 * 28-Jul-2016 : Moved to dedicated package; Added greedy implementation for Weighted VC (JK)
 *
 */
package org.jgrapht.alg.vertexcover;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.interfaces.MinimumWeightedVertexCoverAlgorithm;
import org.jgrapht.alg.util.VertexDegreeComparator;
import org.jgrapht.graph.UndirectedSubgraph;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Greedy algorithm to find a vertex cover for a graph. A vertex cover is a set of
 * vertices that touches all the edges in the graph. The graph's vertex set is a
 * trivial cover. However, a <i>minimal</i> vertex set (or at least an
 * approximation for it) is usually desired. Finding a true minimal vertex cover
 * is an NP-Complete problem. For more on the vertex cover problem, see <a
 * href="http://mathworld.wolfram.com/VertexCover.html">
 * http://mathworld.wolfram.com/VertexCover.html</a>
 *
 * Note: this class supports pseudo-graphs
 *
 * @author Linda Buisman
 * @since Nov 6, 2003
 */
public class GreedyVCImpl<V,E> implements MinimumWeightedVertexCoverAlgorithm<V,E> {

    /**
     * Finds a greedy approximation for a minimal vertex cover of a specified
     * graph. At each iteration, the algorithm picks the vertex with the highest
     * degree and adds it to the cover, until all edges are covered.
     *
     * Note: The worst-case approximation of this greedy algorithm can be as bad as
     * log(n) times the optimum solution!
     *
     *
     * <p>The algorithm works on undirected graphs, but can also work on
     * directed graphs when their edge-directions are ignored. To ignore edge
     * directions you can use {@link org.jgrapht.Graphs#undirectedGraph(Graph)}
     * or {@link org.jgrapht.graph.AsUndirectedGraph}.</p>
     **/
    @Override
    public VertexCover<V> getVertexCover(UndirectedGraph<V,E> graph) {
        // C <-- {}
        Set<V> cover = new LinkedHashSet<>();

        // G' <-- G
        UndirectedGraph<V, E> sg = new UndirectedSubgraph<>(graph, null, null);

        // compare vertices in descending order of degree
        VertexDegreeComparator<V, E> comp = new VertexDegreeComparator<>(sg);

        // while G' != {}
        while (!sg.edgeSet().isEmpty()) {
            // v <-- vertex with maximum degree in G'
            V v = Collections.max(sg.vertexSet(), comp);

            // C <-- C U {v}
            cover.add(v);

            // remove from G' every edge incident on v, and v itself
            sg.removeVertex(v);
        }

        return new VertexCover<>(cover, cover.size());

    }

    /**
     * Finds a greedy solution to the minimum weighted vertex cover problem. At each iteration, the algorithm picks
     * the vertex v with the smallest ratio {@code weight(v)/degree(v)} and adds it to the cover. Next vertex v
     * and all edges incident to it are removed. The process repeats until all vertices are covered.
     * Runtime: O(|E|*log|V|)
     *
     * @param graph input graph
     * @param vertexWeightMap mapping of vertex weights
     * @return greedy solution
     */
    @Override
    public VertexCover<V> getVertexCover(UndirectedGraph<V,E> graph, Map<V, Integer> vertexWeightMap) {
        Set<V> cover=new LinkedHashSet<>();
        int weight=0;
        //Filter out all vertices with degree 0 to prevent division by zero exceptions
        Set<V> vertexSubset=graph.vertexSet().stream().filter(v -> graph.degreeOf(v) > 0).collect(Collectors.toSet());

        UndirectedGraph<V,E> copy= new UndirectedSubgraph<>(graph, vertexSubset, null);
        while(!copy.edgeSet().isEmpty()) { //Keep going until all edges are covered
            V v=Collections.min(copy.vertexSet(),
                    (v1, v2) -> Double.compare(vertexWeightMap.get(v1)/copy.degreeOf(v1), vertexWeightMap.get(v2)/copy.degreeOf(v2)));
            cover.add(v);
            weight+=vertexWeightMap.get(v);

            //Delete v, all edges incident to v, and all vertices u which became isolated
            Set<V> neighbors=new HashSet<>(Graphs.neighborListOf(copy, v));
            copy.removeVertex(v);
            for(V u : neighbors)
                if(u != v && copy.degreeOf(u)==0)
                    copy.removeVertex(u);
        }

        return new VertexCover<>(cover, weight);
    }
}
