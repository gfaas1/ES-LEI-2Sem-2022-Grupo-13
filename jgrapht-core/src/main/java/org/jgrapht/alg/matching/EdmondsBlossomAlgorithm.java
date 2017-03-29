/*
 * (C) Copyright 2017-2017, by Joris Kinable and Contributors.
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
package org.jgrapht.alg.matching;

import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.graph.AsWeightedGraph;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * This implementation supports 4 types of matchings:
 * <ol>
 *     <li>Minimum Cost Perfect Matching: finds a perfect matching of minimum cost.</li>
 *     <li>Maximum Cost Perfect Matching: finds a perfect matching of maximum cost.</li>
 *     <li>Maximum Cost Matching: finds a matching of maximum cost (no guarantees are given in terms of the size of the matching).</li>
 *     <li>Maximum Cardinality Matching: finds a matching of maximum cardinality while ignoring the edge costs.</li>
 * </ol>
 *
 * @author Joris Kinable
 */
public class EdmondsBlossomAlgorithm<V,E> implements MatchingAlgorithm<V, E> {

    public enum Mode{MINCOST_PERFECT_MATCHING, MAXCOST_PERFECT_MATCHING, MAXCOST_MATCHING, MAXCARDINALITY_MATCHING}

    private final Graph<V,E> inputGraph;
    private final Graph<V,E> graph;

    public EdmondsBlossomAlgorithm(Graph<V,E> graph, Mode mode){
        inputGraph=GraphTests.requireUndirected(graph);

        if(mode == Mode.MAXCOST_MATCHING){
            this.graph=graph;
        }else if(mode == Mode.MAXCARDINALITY_MATCHING){
            Map<E, Double> uniformWeightMap=new HashMap<>();
            for(E e : graph.edgeSet())
                uniformWeightMap.put(e, 1.0);
            this.graph=new AsWeightedGraph<>(graph, uniformWeightMap);
        }else if(mode == Mode.MAXCOST_PERFECT_MATCHING){
            if(graph.vertexSet().size()%2==1)
                throw new IllegalArgumentException("A graph with an odd number of vertices does not have a perfect matching.");
            double M=graph.edgeSet().stream().mapToDouble(graph::getEdgeWeight).sum()+1;
            Map<E, Double> weightMap=new HashMap<>();
            for(E e : graph.edgeSet())
                weightMap.put(e, M+graph.getEdgeWeight(e));
            this.graph=new AsWeightedGraph<>(graph, weightMap);
        }else{ //MINCOST_PERFECT_MATCHING
            if(graph.vertexSet().size()%2==1)
                throw new IllegalArgumentException("A graph with an odd number of vertices does not have a perfect matching.");
            double M=graph.edgeSet().stream().mapToDouble(e -> 1.0/graph.getEdgeWeight(e)).sum()+1;
            Map<E, Double> weightMap=new HashMap<>();
            for(E e : graph.edgeSet())
                weightMap.put(e, M+1.0/graph.getEdgeWeight(e));
            this.graph=new AsWeightedGraph<>(graph, weightMap);
        }

    }

    @Override
    public Matching<E> getMatching() {
        return null;
    }
}
