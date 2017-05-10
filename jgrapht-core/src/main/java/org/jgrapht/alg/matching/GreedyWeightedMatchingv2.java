/*
 * (C) Copyright 2016-2017, by Dimitrios Michail and Contributors.
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
import org.jgrapht.VertexFactory;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.util.ToleranceDoubleComparator;
import org.jgrapht.generate.GnmRandomGraphGenerator;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.*;

/**
 * The greedy algorithm for computing a maximum weight matching in an arbitrary graph. The algorithm
 * is a 1/2-approximation algorithm and runs in O(n + m log n) where n is the number of vertices and
 * m is the number of edges of the graph. This implementation accepts directed and undirected graphs
 * which may contain self-loops and multiple edges. There is no assumption on the edge weights, i.e.
 * they can also be negative or zero.
 * 
 * <p>
 * The greedy algorithm is the algorithm that first orders the edge set in non-increasing order of
 * weights and then greedily constructs a maximal cardinality matching out of the edges with
 * positive weight. A maximal cardinality matching (not to be confused with maximum cardinality) is
 * a matching that cannot be increased in cardinality without removing an edge first.
 *
 * <p>
 * For more information about approximation algorithms for the maximum weight matching problem in
 * arbitrary graphs see:
 * <ul>
 * <li>R. Preis, Linear Time 1/2-Approximation Algorithm for Maximum Weighted Matching in General
 * Graphs. Symposium on Theoretical Aspects of Computer Science, 259-269, 1999.</li>
 * <li>D.E. Drake, S. Hougardy, A Simple Approximation Algorithm for the Weighted Matching Problem,
 * Information Processing Letters 85, 211-213, 2003.</li>
 * </ul>
 * 
 * @see PathGrowingWeightedMatching
 * 
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 * 
 * @author Dimitrios Michail
 * @since September 2016
 */
public class GreedyWeightedMatchingv2<V, E>
    implements MatchingAlgorithm<V, E>
{
    private final Graph<V, E> graph;
    private final Comparator<Double> comparator;

    /**
     * Create and execute a new instance of the greedy maximum weight matching algorithm. Floating
     * point values are compared using {@link #DEFAULT_EPSILON} tolerance.
     *
     * @param graph the input graph
     */
    public GreedyWeightedMatchingv2(Graph<V, E> graph)
    {
        this(graph, DEFAULT_EPSILON);
    }

    /**
     * Create and execute a new instance of the greedy maximum weight matching algorithm.
     *
     * @param graph the input graph
     * @param epsilon tolerance when comparing floating point values
     */
    public GreedyWeightedMatchingv2(Graph<V, E> graph, double epsilon)
    {
        if (graph == null) {
            throw new IllegalArgumentException("Input graph cannot be null");
        }
        this.graph = graph;
        this.comparator = new ToleranceDoubleComparator(epsilon);
    }

    /**
     * Get a matching that is a 1/2-approximation of the maximum weighted matching.
     * 
     * @return a matching
     */
    @Override
    public Matching<V, E> getMatching()
    {
        // sort edges in decreasing order of weight
        // (the lambda uses e1 and e2 in the reverse order on purpose)
        List<E> allEdges = new ArrayList<>(graph.edgeSet());
        Collections.sort(
            allEdges,
            (e1, e2) -> {
                double degreeE1=graph.degreeOf(graph.getEdgeSource(e1))+graph.degreeOf(graph.getEdgeTarget(e1));
                double degreeE2=graph.degreeOf(graph.getEdgeSource(e2))+graph.degreeOf(graph.getEdgeTarget(e2));
                return comparator.compare(graph.getEdgeWeight(e2)/degreeE2, graph.getEdgeWeight(e1)/degreeE1);
            });

        double matchingWeight = 0d;
        Set<E> matching = new HashSet<>();
        Set<V> matchedVertices = new HashSet<>();

        // find maximal matching
        for (E e : allEdges) {
            double edgeWeight = graph.getEdgeWeight(e);
            V s = graph.getEdgeSource(e);
            V t = graph.getEdgeTarget(e);
            if (!s.equals(t) && comparator.compare(edgeWeight, 0d) > 0) {
                if (!matchedVertices.contains(s) && !matchedVertices.contains(t)) {
                    matching.add(e);
                    matchedVertices.add(s);
                    matchedVertices.add(t);
                    matchingWeight += edgeWeight;
                }
            }
        }

        // return matching
        return new MatchingImpl<>(graph, matching, matchingWeight);
    }

    public static void main(String[] args){

        int v2Equalsv1=0;
        int v2IsWorse=0;
        int v2IsBetter=0;

        Random random=new Random(1);
        int vertices=200;

        for(int k=0; k<10000; k++) {
            int edges=random.nextInt(maxEdges(vertices)/2)+5;
            System.out.println("k: "+k+" v: "+vertices+" e: "+edges);
            GraphGenerator<Integer, DefaultWeightedEdge, Integer> generator = new GnmRandomGraphGenerator<>(vertices, edges, 0);
            IntegerVertexFactory vertexFactory = new IntegerVertexFactory();

            long t3=System.currentTimeMillis();
            Graph<Integer, DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
            generator.generateGraph(graph, vertexFactory, null);
            for(DefaultWeightedEdge e : graph.edgeSet())
                graph.setEdgeWeight(e, random.nextInt(100));
            t3=System.currentTimeMillis()-t3;

            GreedyWeightedMatching<Integer, DefaultWeightedEdge> v1=new GreedyWeightedMatching<>(graph);
            long t1=System.currentTimeMillis();
            Matching<Integer, DefaultWeightedEdge> m1=v1.getMatching();
            t1=System.currentTimeMillis()-t1;
            double w1=m1.getWeight();
            GreedyWeightedMatchingv2<Integer, DefaultWeightedEdge> v2=new GreedyWeightedMatchingv2<>(graph);
            long t2=System.currentTimeMillis();
            Matching<Integer, DefaultWeightedEdge> m2=v2.getMatching();
            t2=System.currentTimeMillis()-t2;
            double w2=m2.getWeight();
            System.out.println("t1: "+t1+" t2: "+t2+" t3: "+t3);

            if(w1==w2)
                v2Equalsv1++;
            else if(w2 > w1)
                v2IsBetter++;
            else
                v2IsWorse++;

        }

        System.out.println("v2IsBetter: "+v2IsBetter+" v2IsWorse: "+v2IsWorse+" v2Equalsv1: "+v2Equalsv1);

    }

    private static int maxEdges(int n){
        if (n % 2 == 0) {
            return Math.multiplyExact(n / 2, n - 1);
        } else {
            return Math.multiplyExact(n, (n - 1) / 2);
        }
    }

    public static class IntegerVertexFactory implements VertexFactory<Integer>{

        int id=0;

        @Override
        public Integer createVertex() {
            return id++;
        }
    }

}

// End GreedyWeightedMatching.java
