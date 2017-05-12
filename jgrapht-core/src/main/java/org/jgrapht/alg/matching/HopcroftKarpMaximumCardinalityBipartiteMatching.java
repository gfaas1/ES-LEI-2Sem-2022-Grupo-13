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
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;

import java.util.*;

/**
 * Implementation of the well-known Hopcroft Karp algorithm to compute a matching of maximum cardinality in a bipartite graph.
 * To compute a maximum cardinality matching in general (non-bipartite) graphs, use {@link EdmondsMaximumCardinalityMatching} instead.
 * The algorithm runs in O(|E|*√|V|) time.
 *
 * <p>
 * The original algorithm is described in: Hopcroft, John E.; Karp, Richard M. (1973), "An n5/2 algorithm for
 * maximum matchings in bipartite graphs", SIAM Journal on Computing 2 (4): 225–231,
 * doi:10.1137/0202019 A coarse overview of the algorithm is given in:
 * <a href="http://en.wikipedia.org/wiki/Hopcroft-Karp_algorithm">http://en.wikipedia.org/wiki/Hopcroft-Karp_algorithm</a>
 *
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Joris Kinable
 */
public class HopcroftKarpMaximumCardinalityBipartiteMatching<V,E> implements MatchingAlgorithm<V,E>{

    private final Graph<V,E> graph;
    private final Set<V> partition1;
    private final Set<V> partition2;

    /* Ordered list of vertices */
    private List<V> vertices;
    /* Mapping of a vertex to their unique position in the ordered list of vertices */
    private Map<V, Integer> vertexIndexMap;

    /* Number of matched vertices i partition 1. */
    private int matchedVertices;

    /* Dummy vertex */
    private final int DUMMY = 0;
    /* Infinity */
    private final int INF = Integer.MAX_VALUE;
    
    /* Array keeping track of the matching. */
    private int[] matching;
    /* Distance array. Used to compute shoretest augmenting paths */
    private int[] dist;

    /* queue used for breadth first search */
    private FixedSizeQueue queue;

    /**
     * Constructs a new instance of the Hopcroft Karp bipartite matching algorithm
     * @param graph input graph
     * @param partition1 the first partition of vertices
     * @param partition2 the second partition of vertices
     */
    public HopcroftKarpMaximumCardinalityBipartiteMatching(Graph<V, E> graph, Set<V> partition1, Set<V> partition2) {
        this.graph=graph;

        //Ensure that partition1 is smaller or equal in size compared to partition 2
        if(partition1.size() <= partition2.size()) {
            this.partition1 = partition1;
            this.partition2 = partition2;
        }else{ //else, swap
            this.partition1 = partition2;
            this.partition2 = partition1;
        }
    }

    private void init() {
        vertices = new ArrayList<>();
        vertices.add(null);
        vertices.addAll(partition1);
        vertices.addAll(partition2);
        vertexIndexMap = new HashMap<>();
        for (int i = 0; i < vertices.size(); i++)
            vertexIndexMap.put(vertices.get(i), i);

        matching = new int[vertices.size() + 1];
        dist = new int[partition1.size()+1];
        queue = new FixedSizeQueue(vertices.size());
    }

    /**
     * Greedily compute an initial feasible matching
     */
    private void warmStart()
    {
        for (V uOrig : partition1) {
            int u=vertexIndexMap.get(uOrig);

            for (V vOrig : Graphs.neighborListOf(graph, uOrig)) {
                int v=vertexIndexMap.get(vOrig);
                if(matching[v]== DUMMY){
                    matching[v] = u;
                    matching[u] = v;
                    matchedVertices++;
                    break;
                }
            }
        }
    }

    /**
     * BFS function which finds the shortest augmenting path. The length of the shortest augmenting path is stored in
     * dist[DUMMY].
     * @return true if an augmenting path was found, false otherwise
     */
    private boolean bfs()
    {
//        System.out.println("BFS");
        queue.clear();

        for (int u = 1; u <= partition1.size(); u++)
            if (matching[u] == DUMMY){ //Add all unmatched vertices to the queue and set their distance to 0
                dist[u] = 0;
                queue.enqueue(u);
            }else //Set distance of all matched vertices to INF
                dist[u] = INF;
        dist[DUMMY] = INF;
//        System.out.println("init:\n\tmatching: "+Arrays.toString(matching)+"\n\tdist: "+Arrays.toString(dist));

        while (!queue.empty()){
            int u = queue.poll();
            if (dist[u] < dist[DUMMY])
                for (V vOrig : Graphs.neighborListOf(graph, vertices.get(u))) {
                    int v=vertexIndexMap.get(vOrig);
//                    System.out.println("processing edge (v,u): ("+u+","+v+")");
                    if (dist[matching[v]] == INF) {
                        dist[matching[v]] = dist[u] + 1;
                        queue.enqueue(matching[v]);
                    }
                }
        }
//        System.out.println("bfs finished:\n\tmatching: "+Arrays.toString(matching)+"\n\tdist: "+Arrays.toString(dist));
//        System.out.println("BFS returning: "+(dist[DUMMY] != INF));
        return dist[DUMMY] != INF; //Return true if an augmenting path is found
    }

    /**
     * Find all vertex disjoint augmenting paths of length dist[DUMMY].
     * @param u vertex from which the DFS is started
     * @return true if an augmenting path from vertex u was found, false otherwise
     */
    private boolean dfs(int u)
    {
//        System.out.println("DFS from vertex: "+u);
        if (u != DUMMY){
            for (V vOrig : Graphs.neighborListOf(graph, vertices.get(u))) {
                int v = vertexIndexMap.get(vOrig);
                if (dist[matching[v]] == dist[u] + 1)
                    if (dfs(matching[v])) {
                        matching[v] = u;
                        matching[u] = v;
                        matchedVertices++;
                        return true;
                    }
            }

            dist[u] = INF;
            return false;
        }
        return true;
    }

    @Override
    public Matching<V,E> getMatching() {
        this.init();
        this.warmStart();

        while (matchedVertices < partition1.size() && bfs()) {
            //Greedily search for vertex disjoint augmenting paths
            for (int v = 1; v <= partition1.size() && matchedVertices < partition1.size(); v++)
                if (matching[v] == DUMMY) //v is unmatched
                    dfs(v);
        }
        assert matchedVertices <= partition1.size();

        Set<E> edges=new HashSet<>();
        for(int i=0; i<vertices.size(); i++){
            if(matching[i] != DUMMY){
                edges.add(graph.getEdge(vertices.get(i), vertices.get(matching[i])));
            }
        }
        return new MatchingImpl<>(graph, edges, edges.size());
    }

    /**
     * Efficient implementation of a fixed size queue for integers.
     */
    private static final class FixedSizeQueue
    {
        private final int[] vs;
        private int i = 0;
        private int n = 0;

        /**
         * Create a queue of size n.
         *
         * @param n size of the queue
         */
        private FixedSizeQueue(int n)
        {
            vs = new int[n];
        }

        /**
         * Add an element to the queue.
         *
         * @param e element
         */
        void enqueue(int e)
        {
            vs[n++] = e;
        }

        /**
         * Poll the first element from the queue.
         *
         * @return the first element.
         */
        int poll()
        {
            return vs[i++];
        }

        /**
         * Check if the queue has any items.
         *
         * @return true if the queue is empty
         */
        boolean empty()
        {
            return i == n;
        }

        /** Empty the queue. */
        void clear()
        {
            i = 0;
            n = 0;
        }

        public String toString()
        {
            String s = "";
            for (int j = i; j < n; j++)
                s += vs[j] + " ";
            return s;
        }
    }
}
