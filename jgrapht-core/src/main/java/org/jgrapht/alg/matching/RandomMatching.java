package org.jgrapht.alg.matching;

import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by jkinable on 4/25/17.
 */
public class RandomMatching<V,E> implements MatchingAlgorithm<V,E>{

    private final Graph<V,E> graph;

    public RandomMatching(Graph<V, E> graph) {
        this.graph = GraphTests.requireUndirected(graph);
    }


    @Override
    public Matching<V, E> getMatching() {
        Set<V> matched=new HashSet<>();
        Set<E> edges=new LinkedHashSet<E>();
        for(V v : graph.vertexSet()){
            if(matched.contains(v))
                continue;

            for(E edge : graph.edgesOf(v)){
                V w = Graphs.getOppositeVertex(graph, edge, v);
                if(!matched.contains(w)) {
                    edges.add(edge);
                    matched.add(v);
                    matched.add(w);
                    break;
                }
            }
        }

        return new MatchingImpl<>(graph, edges, edges.stream().mapToDouble(graph::getEdgeWeight).sum());
    }
}
