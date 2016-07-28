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
 * GreedyVertexCover.java
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
 * 28-Jul-2016 : Moved to dedicated package (JK)
 *
 */
package org.jgrapht.alg.vertexCover;

import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.interfaces.VertexCoverAlgorithm;
import org.jgrapht.alg.util.VertexDegreeComparator;
import org.jgrapht.graph.UndirectedSubgraph;

import java.util.*;

/**
 * Greedy algorithm to find a vertex cover for a graph. A vertex cover is a set of
 * vertices that touches all the edges in the graph. The graph's vertex set is a
 * trivial cover. However, a <i>minimal</i> vertex set (or at least an
 * approximation for it) is usually desired. Finding a true minimal vertex cover
 * is an NP-Complete problem. For more on the vertex cover problem, see <a
 * href="http://mathworld.wolfram.com/VertexCover.html">
 * http://mathworld.wolfram.com/VertexCover.html</a>
 *
 * @author Linda Buisman
 * @since Nov 6, 2003
 */
public class GreedyVertexCover<V,E> implements VertexCoverAlgorithm<V,E> {

    private final UndirectedGraph<V,E> graph;

    public GreedyVertexCover(UndirectedGraph<V,E> graph){
        this.graph=graph;
    }

    /**
     * Finds a greedy approximation for a minimal vertex cover of a specified
     * graph. At each iteration, the algorithm picks the vertex with the highest
     * degree and adds it to the cover, until all edges are covered.
     *
     * Note: every invocation of this method will recompute the cover!
     *
     * <p>The algorithm works on undirected graphs, but can also work on
     * directed graphs when their edge-directions are ignored. To ignore edge
     * directions you can use {@link org.jgrapht.Graphs#undirectedGraph(Graph)}
     * or {@link org.jgrapht.graph.AsUndirectedGraph}.</p>
     **/
    @Override
    public VertexCover<V> getVertexCover() {
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
     * TODO: requires implementation. Consider Clarkson's or Bar-Yehuda and Even's Greedy algorithms, e.g.: https://www.cs.umd.edu/class/spring2011/cmsc651/vc.pdf
     * @param vertexWeightMap
     * @return
     */
    @Override
    public VertexCover<V> getVertexCover(Map<V, Integer> vertexWeightMap) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
