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
import org.jgrapht.Graphs;
import org.jgrapht.alg.flow.PushRelabelMFImpl;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import java.util.*;

/**
 * @author Joris Kinable
 */
public class FlowBasedMaximumCardinalityBipartiteMatching<V,E> implements MatchingAlgorithm<V,E> {

    private final Graph<V,E> graph;
    private final Set<V> partition1;
    private final Set<V> partition2;

    private Graph<Integer, DefaultEdge> workingGraph;
    private final int source=-1;
    private final int sink=-2;

    /* Ordered list of vertices */
    private List<V> vertices;
    /* Mapping of a vertex to their unique position in the ordered list of vertices */
    private Map<V, Integer> vertexIndexMap;

    public FlowBasedMaximumCardinalityBipartiteMatching(Graph<V, E> graph, Set<V> partition1, Set<V> partition2) {
        this.graph = GraphTests.requireUndirected(graph);
        this.partition1=partition1;
        this.partition2=partition2;
    }

    private void init(){
        vertices = new ArrayList<>();
        vertices.addAll(partition1);
        vertices.addAll(partition2);
        vertexIndexMap = new HashMap<>();
        for (int i = 0; i < vertices.size(); i++)
            vertexIndexMap.put(vertices.get(i), i);

        workingGraph=new SimpleDirectedGraph<>(DefaultEdge.class);
        workingGraph.addVertex(source);
        workingGraph.addVertex(sink);
        Graphs.addAllVertices(workingGraph, vertexIndexMap.values());

        //Connect source vertex to all vertices from partition one
        for(V v : partition1)
            workingGraph.addEdge(source, vertexIndexMap.get(v));

        //Connect all vertices from partition two to the sink vertex
        for(V v : partition2)
            workingGraph.addEdge(vertexIndexMap.get(v), sink);

        //Add all edges from the bipartite graph
        for(E e : graph.edgeSet()){
            V u=graph.getEdgeSource(e);
            V v=graph.getEdgeTarget(e);

            int ux=vertexIndexMap.get(u);
            int vx=vertexIndexMap.get(v);

            if(ux != vx) { //ignore self loops
                if(ux > vx){ //ensure that all edges point from the source partition to the sink partition
                    int swap=ux;
                    ux=vx;
                    vx=swap;
                }
                workingGraph.addEdge(ux, vx);
            }
        }
    }

    @Override
    public Matching<V, E> getMatching() {

        this.init();

        //Compute max flow from source to sink
        MaximumFlowAlgorithm<Integer, DefaultEdge> maximumFlowAlgorithm=new PushRelabelMFImpl<>(workingGraph);
        MaximumFlowAlgorithm.MaximumFlow<DefaultEdge> maximumFlow=maximumFlowAlgorithm.getMaximumFlow(source, sink);
        Map<DefaultEdge, Double> flow=maximumFlow.getFlow();

        Set<E> matchedEdges=new LinkedHashSet<>();
        //All edges with non-zero flow (excluding edges incident to the source/sink vertex) are matched edges
        for(DefaultEdge e : flow.keySet()){
            if(flow.get(e)==0)
                continue;
            Integer u=workingGraph.getEdgeSource(e);
            Integer v=workingGraph.getEdgeTarget(e);
            if(u != source && v != sink)
                matchedEdges.add(graph.getEdge(vertices.get(u), vertices.get(v)));
        }

        return new MatchingImpl<>(graph, matchedEdges, matchedEdges.size());
    }
}
