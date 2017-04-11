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
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.util.UnionFind;
import org.jgrapht.graph.AsWeightedGraph;

import java.lang.reflect.Array;
import java.util.*;

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
public class EdmondsBlossomAlgorithmv2<V,E> implements MatchingAlgorithm<V, E> {

    public enum Mode{MINCOST_PERFECT_MATCHING, MAXCOST_PERFECT_MATCHING, MAXCOST_MATCHING, MAXCARDINALITY_MATCHING}

    private enum Label{ODD, EVEN, FREE}

    private final Graph<V,E> inputGraph;
    private final Graph<V,E> graph;

    public EdmondsBlossomAlgorithmv2(Graph<V, E> graph, Mode mode){
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

    /*
    All vertices in the original graph are mapped to a unique integer to simplify the implementation and to improve efficiency.
     */
    private List<V> vertices=new ArrayList<>();
    private Map<V, Integer> vertexIndexMap=new HashMap<>();

    /* The representative cache tracks for each vertex its representative. If the vertex is part of a blossom, then the representative
    of that blossom is returned. Otherwise, the vertex represents itself. Given an edge (u,v), if the two vertices u, v belong to the same blossom,
     in [1] denoted by R(u)=R(v), then the edge (u,v) has been 'shrunken away'. Initially, each vertex represents itself.
     */
    UnionFind<Integer> representative;

    /*
    Arrays representing resp the even and odd nodes forming the alternating trees. A node is even (resp odd) if it is at
    even (resp) odd distance from the root of the tree it belongs to. By definition, the root of a tree is even. All tree
    roots are unmatched nodes. A node is unmatched if no edge in the current matching is incident to that node.
    Similarly, an edge is said to be matched (resp unmatched) if the edge is part (resp not part) of the matching. Each layer
    in an alternating tree consists alternatingly matched and unmatched edges. Edges leaving
    even nodes are unmatched, edges leaving odd nodes are always matched. By definition,
    odd nodes in an alternating tree always have cardinality 2. Each vertex and each edge is part of at most 1 tree.
     Note that the odd and even arrays only contain representatives! odd[v] returns the ancestor u of vertex v in its alternating tree.
     The returned vertex is not necessarily a representative vertex (the returned vertex can be part of a blossom). Therefore, to obtain
     its presentative, invoke representatives.find(). In this example, the edge (u,v) is unmatched, and representative.find(u) must be an even node.
     */
    private int[] even,odd;

    /*
    Vector storing the dual values for each node
     */
    private double[] dualValues;

    /*
    Compact representation of the graph.
     */
    private Map<Integer, Edge>[] adjacencyList;

    /*
    The current matching
     */
    private Matching matching;

    /** Special 'NIL' vertex. */
    private static final int NIL = -1;


    private void init(){
        vertices.addAll(graph.vertexSet());
        for(int i=0; i<vertices.size(); i++)
            vertexIndexMap.put(vertices.get(i), i);

        even=new int[vertices.size()];
        odd=new int[vertices.size()];
        dualValues=new double[vertices.size()];
        Arrays.fill(dualValues, Double.MAX_VALUE);
        representative =new UnionFind<>(vertexIndexMap.values());

        //Build adjacencyList and initialize dual values
        adjacencyList = (Map<Integer, Edge>[]) Array.newInstance(Map.class, vertices.size());
        for(int ux=0; ux<vertices.size(); ux++){
            V u=vertices.get(ux);
            for(E e : graph.edgesOf(u)){
                V v=Graphs.getOppositeVertex(graph, e, u);
                int vx=vertexIndexMap.get(v);
                if(adjacencyList[ux].containsKey(vx))
                    continue;

                double weight=graph.getEdgeWeight(e);
                Edge edge=new Edge(ux, vx, weight);
                adjacencyList[ux].put(vx, edge);
                adjacencyList[vx].put(ux, edge);

                dualValues[ux]=Math.min(dualValues[ux], weight);
                dualValues[vx]=Math.min(dualValues[vx], weight);
            }
        }
        for(int ux=0; ux<vertices.size(); ux++)
            dualValues[ux]/=2;
        matching=new Matching(vertices.size()/2);
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
                    if(u.label== Label.EVEN && v.label== Label.FREE)
                        grow(u, v);
                    else if(v.label== Label.EVEN && u.label== Label.FREE)
                        grow(v, u);

                    //Shrink
                    if(u.label== Label.EVEN && v.label== Label.EVEN && areInSameTree(u,v))
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

    /*
    Let u be a node in the last even layer of tree T, (u,v) be an edge
    such that v is not in V(T) and v is a matched vertex. Let (v,w) be
    the corresponding matched edge. We can then extend tree T with the
    two edges (u,v) and (v,w).
     */
    private void grow(int u, int v){
        odd[v] = u;
        int w = matching.getOpposite(v);
        // add the matched edge (potential though a blossom) if it
        // isn't in the forest already
        if (even[representative.find(w)] == NIL) { //Not sure this if check is needed?
            even[w] = v;
            queue.enqueue(u);
        }
    }

    private void augment(int u, int v){
        //Find the path P1 from node u to its root. In matching M, replace the edges in M \cap P1 by the edges in the symmetric difference between M and P1

        //Find the path P2 from node v to its root. In matching M, replace the edges in M \cap P2 by the edges in the symmetric difference between M and P2

        //Add the edge (u,v) to M, thereby increasing the cardinality of the matching by 1.
        matching.match(u,v);
    }

    private void shrink(int u, int v){

    }

    private void expand(int u){

    }

    private void updateDuals(){

    }


    private class Edge{
        int source;
        int target;
        double weight;

        public Edge(int source, int target, double weight){
            this.source=source;
            this.target=target;
            this.weight=weight;
        }

    }

    private class Matching{
        private static final int UNMATCHED=-1;
        int[] matching;

        Set<Edge> edges=new LinkedHashSet<>();
        double weight=0;

        public Matching(int N){
            matching=new int[N];
            Arrays.fill(matching, UNMATCHED);
        }

        public void match(int u, int v){
            matching[u]=v;
            matching[v]=u;
        }

        public boolean isPerfect(){
            return edges.size()==graph.vertexSet().size()/2.0;
        }

        /**
         * Test whether any of the edges in the matching touches vertex w. (needs optimization)
         * @param v
         * @return
         */
        public boolean testIncidence(int v){
            return matching[v] != UNMATCHED;
        }

        public int getOpposite(int v){
            return matching[v];
        }
    }


    private boolean areInSameTree(int u, int v){

    }
}
