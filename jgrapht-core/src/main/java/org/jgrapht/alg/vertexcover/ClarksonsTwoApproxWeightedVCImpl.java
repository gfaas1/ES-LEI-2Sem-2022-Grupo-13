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
 * ClarksonsTwoApproxWeightedVCImpl.java
 * -----------------
 * (C) Copyright 2016, by Joris Kinable and Contributors.
 *
 * Original Author:  Joris Kinable
 * Contributor(s):   -
 *
 * $Id$
 *
 * Changes
 * -------
 * 28-Jul-2016 : Initial revision (JK);
 *
 */
package org.jgrapht.alg.vertexcover;

import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.interfaces.MinimumWeightedVertexCoverAlgorithm;
import org.jgrapht.graph.UndirectedSubgraph;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the 2-opt algorithm for a minimum weighted vertex cover by
 * Clarkson, Kenneth L. "A modification of the greedy algorithm for vertex cover." Information Processing Letters 16.1 (1983): 23-25.
 *
 * @author Joris Kinable
 *
 */
public class ClarksonsTwoApproxWeightedVCImpl<V,E> implements MinimumWeightedVertexCoverAlgorithm<V,E> {

    @Override
    public VertexCover<V> getVertexCover(UndirectedGraph<V,E> graph, Map<V, Integer> vertexWeightMap) {
        Set<V> cover=new LinkedHashSet<V>();
        //Filter out all vertices with degree 0 to prevent division by zero exceptions
        Set<V> vertexSubset=graph.vertexSet().stream().filter(v -> graph.degreeOf(v) > 0).collect(Collectors.toSet());
        UndirectedGraph<V,E> copy= new UndirectedSubgraph<>(graph, vertexSubset, null);
        Map<V, Double> W=new HashMap<>();
        for(V v : graph.vertexSet()){
            W.put(v, 1.0* vertexWeightMap.get(v));
        }
        while(!copy.edgeSet().isEmpty()){ //Keep going until all edges are covered

            Set<V> markedForDeletion=new HashSet<>();

            //Find a vertex v for which W(v)/degree(v) is minimal
            V v=Collections.min(copy.vertexSet(),
                    (v1, v2) -> Double.compare(W.get(v1)/copy.degreeOf(v1), W.get(v2)/copy.degreeOf(v2)));

            //Update weights
            double ratio=W.get(v)/copy.degreeOf(v);
            for(E e : copy.edgesOf(v)){
                V u = Graphs.getOppositeVertex(copy, e, v);
                W.put(u, W.get(u)-ratio);

                if(copy.degreeOf(u)== 1)
                    markedForDeletion.add(u);
            }
            W.put(v, 0.0);

            //Update cover
            cover.add(v);
            markedForDeletion.add(v);

            //Remove nextCandidate and all vertices with degree 0
            copy.removeAllVertices(markedForDeletion);
        }

        int weight=cover.stream().mapToInt(vertexWeightMap::get).sum();
        return new VertexCover<>(cover, weight);
    }

}
