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
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * A matching in a graph G(V,E) is a set M of edges such that no node of G is incident with more than one edge in M.
 * The weight of a matching is defined as the sum of the weights of the edges in M. A matching has at most 1/2|V| edges.
 * A node v in G is covered by matching M, if M contains an edge incident to v. A matching is perfect if all nodes are
 * covered. By definition, a perfect matching consists of exactly 1/2|V| edges.
 *
 * This algorithm solves the matching problems for general graphs (simple, undirected, weighted or unweighted graphs).
 * If the input graph is bipartite (see {@link GraphTests#isBipartite(Graph)} for details) use
 * {@link HopcroftKarpBipartiteMatching} instead.
 *
 * <p>
 * This implementation can solve 4 different matching problems:
 * <ol>
 *     <li>Minimum Cost Perfect Matching: find a perfect matching of minimum cost.</li>
 *     <li>Maximum Cost Perfect Matching: find a perfect matching of maximum cost.</li>
 *     <li>Maximum Cost Matching: find a matching of maximum cost (no guarantees are given in terms of the size of the matching).</li>
 *     <li>Maximum Cardinality Matching: find a matching of maximum cardinality (ignores the edge costs).</li>
 * </ol>
 *
 * @author Joris Kinable
 */
public class EdmondsBlossomAlgorithm<V,E> implements MatchingAlgorithm<V, E> {

    public enum Mode{MINCOST_PERFECT_MATCHING, MAXCOST_PERFECT_MATCHING, MAXCOST_MATCHING, MAXCARDINALITY_MATCHING}

    private enum Label{ODD, EVEN, FREE}

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

    private Map<V, PseudoNode> pseudoNodeMap=new HashMap<V, PseudoNode>();
    Graph<PseudoNode, Edge> pseudoNodeGraph;

    private void init(){
        //Create pseudonode graph
        pseudoNodeGraph=new SimpleWeightedGraph<PseudoNode, Edge>(Edge.class);
        for(V v : graph.vertexSet()){
            PseudoNode pn=new PseudoNode();
            pseudoNodeGraph.addVertex(pn);
            pseudoNodeMap.put(v, pn);
        }
        for(E e : graph.edgeSet()){
            V source=graph.getEdgeSource(e);
            V target=graph.getEdgeTarget(e);
            Edge edge = pseudoNodeGraph.addEdge(pseudoNodeMap.get(source), pseudoNodeMap.get(target));
            pseudoNodeGraph.setEdgeWeight(edge, graph.getEdgeWeight(e));
        }

        //Initialize dual values
        for(PseudoNode ps : pseudoNodeGraph.vertexSet()){
            double minWeight=Double.MAX_VALUE;
            for(Edge e : pseudoNodeGraph.outgoingEdgesOf(ps))
                minWeight=Math.min(minWeight, pseudoNodeGraph.getEdgeWeight(e));
            ps.dualValue=minWeight/2.0;
        }
    }

    private void run(){
        while(true){
            //primal updates
            for(Edge e : pseudoNodeGraph.edgeSet()){
                PseudoNode u=pseudoNodeGraph.getEdgeSource(e);
                PseudoNode v=pseudoNodeGraph.getEdgeTarget(e);

                double reducedCost=pseudoNodeGraph.getEdgeWeight(e)-u.dualValue-v.dualValue;
                if(reducedCost == 0){ //Tight edges
                    //Augment
                    if(u.label == Label.EVEN && v.label == Label.EVEN && !areInSameTree(u, v))
                        augment(u, v);

                    //Grow
                    if(u.label==Label.EVEN && v.label==Label.FREE)
                        grow(u, v);
                    else if(v.label==Label.EVEN && u.label==Label.FREE)
                        grow(v, u);

                    //Shrink
                    if(u.label==Label.EVEN && v.label==Label.EVEN && areInSameTree(u,v))
                        shrink(u, v);


                }
                //Expand
                if(u.isBlossom() && u.label == Label.ODD && u.dualValue==0.0)
                    expand(u);
                else if(v.isBlossom() && v.label == Label.ODD && v.dualValue==0.0)
                    expand(v);

            }

            //dual updates
            boolean hasPerfectMatching=true;
            for(Edge e : pseudoNodeGraph.edgeSet()) {
                PseudoNode u = pseudoNodeGraph.getEdgeSource(e);
                PseudoNode v = pseudoNodeGraph.getEdgeTarget(e);
                if(!(u.label == Label.EVEN && v.label== Label.ODD)){
                    hasPerfectMatching=true;
                    break;
                }
                if(!(v.label == Label.EVEN && u.label== Label.ODD)){
                    hasPerfectMatching=true;
                    break;
                }
            }
            if(!hasPerfectMatching) //No perfect matching exists in the graph!
                break;
            else
                updateDuals();

        }
    }

    @Override
    public Matching<E> getMatching() {
        return null;
    }

    private void grow(PseudoNode u, PseudoNode v){

    }
    private void augment(PseudoNode u, PseudoNode v){

    }
    private void shrink(PseudoNode u, PseudoNode v){

    }
    private void expand(PseudoNode blossom){

    }

    private void updateDuals(){

    }

    private class PseudoNode{
        Label label= Label.FREE; //odd, even or exposed
        PseudoNode treeRoot=null; //Root of the tree the node belongs to
        double dualValue=0; //Dual value of the node (y_u)

        public PseudoNode(){

        }

        public boolean isBlossom(){
            return false;
        }
    }

    private class Edge{

    }

    private class BMatching{
        Set<Edge> edges=new LinkedHashSet<>();
        double weight=0;

        public boolean isPerfect(){
            return edges.size()==graph.vertexSet().size()/2.0;
        }
    }


    private boolean areInSameTree(PseudoNode n1, PseudoNode n2){

    }
}
