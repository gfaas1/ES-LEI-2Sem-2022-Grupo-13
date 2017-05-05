/*
 * (C) Copyright 2012-2017, by Alejandro Ramon Lopez del Huerto and Contributors.
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
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.util.UnionFind;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.util.ArrayUnenforcedSet;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An implementation of Edmonds Blossom Shrinking algorithm for constructing maximum matchings on
 * graphs. The algorithm runs in time O(V^4).
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Alejandro R. Lopez del Huerto
 * @since Jan 24, 2012
 */
public class EdmondsBlossomShrinkingImproved3<V, E>
    implements MatchingAlgorithm<V, E>
{
    /* Input graph */
    private final Graph<V, E> graph;

    /* All vertices in the original graph are mapped to a unique integer to simplify the implementation and to improve efficiency. */
    /* Ordered list of vertices */
    private List<V> vertices;
    /* Map which maps each vertex to its unique position in the ordered list of vertices */
    private Map<V, Integer> vertexIndexMap;

    /* algorithm used to quickly calculate an initial feasible matching. */
    private final MatchingAlgorithm<V,E> initializer;


    /* ---------------- Internal data structures ------------------ */


    private Map<Integer, Integer> match;
    /* Map defining the predecessors of the odd nodes */
    private Map<Integer, Integer> predOdd;
    private Map<Integer, Integer> predEven;

    /* Union-Find to represent pseudo nodes. A pseudonode groups together a number of vertices in the original graph. The cardinality of a pseudo node is always odd.
    * If the cardinality of a pseudo node is 3 or larger, it is called a blossom.
    * */
    private UnionFind<Integer> uf;

    /** Special 'nil' vertex. */
    private static final int nil = -1;

    /**
     * Construct an instance of the Edmonds blossom shrinking algorithm.
     *
     * @param graph the input graph
     * @throws IllegalArgumentException if the graph is not undirected
     */
    public EdmondsBlossomShrinkingImproved3(Graph<V, E> graph)
    {
//        this(graph, new GreedyMaxCardinalityMatching<V, E>(graph, false));
        this(graph, null);
    }

    public EdmondsBlossomShrinkingImproved3(Graph<V, E> graph, MatchingAlgorithm<V, E> initializer)
    {
        this.graph = GraphTests.requireUndirected(graph);
        this.initializer=initializer;
    }

    /**
     * Prepare the data structures
     */
    private void init(){

        vertices=new ArrayList<>();
        vertices.addAll(graph.vertexSet());
        vertexIndexMap=new HashMap<>();
        for(int i=0; i<vertices.size(); i++)
            vertexIndexMap.put(vertices.get(i), i);

        match = new HashMap<>();
        predOdd = new HashMap<>();
        predEven = new HashMap<>();
        uf = new UnionFind<>(vertexIndexMap.values());

        if(initializer != null)
            this.warmStart(initializer);
    }

    private void warmStart(MatchingAlgorithm<V,E> initializer){
        Matching<V,E> initialSolution=initializer.getMatching();
        for(E e : initialSolution.getEdges()){
            V u=graph.getEdgeSource(e);
            V v=graph.getEdgeTarget(e);
            match.put(vertexIndexMap.get(u), vertexIndexMap.get(v));
            match.put(vertexIndexMap.get(v), vertexIndexMap.get(u));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Matching<V, E> getMatching()
    {
        Set<E> edges = new ArrayUnenforcedSet<>();
        this.init();

        for (int v : vertexIndexMap.values()) {

            //The matching is maximum if it is perfect, or if it leaves only one node exposed in a graph with an odd number of vertices
//            if(match.size() >= vertices.size()-1)
//                break;

            System.out.println("\ngrowing path from "+v);

            // Any augmenting predOdd should start with _exposed_ vertex
            // (vertex may not escape match-set being added once)
            if (!match.containsKey(v)) {
                // Match is maximal iff graph G contains no more augmenting paths
                int w = findAugmentingPath(v);
                if(w != nil) {
//                    System.out.println("found augmenting path ending in: "+w);
//                    System.out.println("predOdd: "+predOdd);
//                    System.out.println("predEven: "+predEven);
                    while (w != nil) {
                        int pv = predOdd.get(w);
//                        System.out.println("pv: "+pv);
                        //int ppv = match.getOrDefault(pv, nil);//match.get(pv);
                        int ppv = match.getOrDefault(pv, nil);
//                        int ppv = predEven.get(pv);
//                        System.out.println("ppv: "+ppv);
//                        System.out.println("matched edge: ("+w+","+pv+")");
                        match.put(w, pv);
                        match.put(pv, w);
                        w = ppv;
                        if(pv==ppv)
                            break;
                    }
                }
            }else{
//                System.out.println("already matched. skipping: "+v);
            }
        }

        Set<Integer> seen = new HashSet<>();
        vertexIndexMap.values().stream().filter(v -> !seen.contains(v) && match.containsKey(v)).forEach(
            v -> {
                seen.add(v);
                seen.add(match.get(v));
                edges.add(graph.getEdge(vertices.get(v), vertices.get(match.get(v))));
            });

        return new MatchingImpl<>(graph, edges, edges.size());
    }

    /**
     * Find an augmenting path.
     * @param root starting vertex of the augmenting path
     * @return the ending vertex of the augmenting path, or nil if none is found.
     */
    private int findAugmentingPath(int root)
    {
//        System.out.println("find path");
        Set<Integer> used = new HashSet<>();
        Queue<Integer> q = new ArrayDeque<>();

        predOdd.clear();
        predEven.clear();
        uf.reset();
        predEven.put(root, root);
//        System.out.println("uf: "+uf);


        used.add(root);
        q.add(root);

        while (!q.isEmpty()) {
            int v = q.remove();

            for (V neighbor : Graphs.neighborListOf(graph, vertices.get(v))) {
                int w = vertexIndexMap.get(neighbor);

//                if (contracted.get(v) == contracted.get(to) || (match.containsKey(v) && to == match.get(v)))
//                    continue;
                System.out.println("checking edge ("+v+","+w+")");

                //If edge (v,w) is part of a blossom, it is shrunken away and we can ignore it.
                //If w is odd, we can ignore the edge as well.
                if (uf.connected(v, w) /*||
                        (predEven.containsKey(v) && w == predEven.get(v))*/) { //I think this could be: predOdd.containsKey(w)
                    System.out.println("ignore edge");
                    continue;


                // Check whether we encountered a blossom. A blossom can only exist if w is an even vertex
                }/*else if ((w == root)
                    || ((predEven.containsKey(w)) && (predOdd.containsKey(predEven.get(w)))))*/
                else if(predEven.containsKey(uf.find(w)))//if(predEven.containsKey(w))
                {
                    System.out.println("found blossom. Search base");
                    int base = lowestCommonAncestor(v, w);
                    System.out.println("stem: "+base);

                    Set<Integer> blossom = new HashSet<>();
                    markPath(v, w, base, blossom);
                    markPath(w, v, base, blossom);
                    System.out.println("blossom: "+blossom);

                    vertexIndexMap.values().stream()
                        .filter(
//                            i -> blossom.contains(contracted.get(i)))
                                i -> blossom.contains(uf.find(i)))
                        .forEach(i -> {
                            //uf.union(i, base);
                            uf.union(base, i);
                            if (!used.contains(i)) {
                                used.add(i);
                                q.add(i); //check whether this indeed only adds the ODD vertices in the blossom back to the queue
                            }
                        });
                    System.out.println("uf: "+uf);
                    predEven.put(uf.find(base), predEven.get(base));
                    //even[uf.find(base)] = even[base];

//                    System.out.println("predOdd: "+predOdd);
//                    System.out.println("predEven: " + predEven);

                // If w is an odd vertex, we ignore edge (w,v). Adding this edge would lead to an even length cycle.
                // Only cycles of odd length (blossoms) are allowed.
                } else if (!predOdd.containsKey(w)) {
                    predOdd.put(w, v);

                    //We found an augmenting path from root to w.
                    if (!match.containsKey(w)) {
                        System.out.println("found augmenting path after adding edge ("+v+","+w+")");
                        return w;
                    }

                    System.out.println("growing tree ("+v+","+w+","+match.get(w)+")");

                    //Perform a grow step
                    int x = match.get(w);
                    predEven.put(x, w);
                    used.add(x);
                    q.add(x);



//                    System.out.println("predOdd: "+predOdd);
//                    System.out.println("predEven: "+predEven);

                }
            }
        }
        System.out.println("No augmenting path found");
        return nil;
    }

    private void markPath(int v, int child, int base, Set<Integer> blossom)
    {
//        System.out.println("markPath. v: "+v+" child: "+child);

//        while (!contracted.get(v).equals(stem)) {
        while (uf.find(v)!= base) {
            blossom.add(uf.find(v));
            blossom.add(uf.find(match.get(v)));
            predOdd.put(v, child);
//            System.out.println("predOdd[" + v + "]=" + child);
            child = match.get(v);
//            System.out.println("predEven["+child+"]="+v);
            v = predOdd.get(child);
        }
    }

    private int lowestCommonAncestor(int v, int w)
    {
//        System.out.println("lca. v: "+v+" w: "+w);
//        System.out.println("predOdd: "+predOdd);
//        System.out.println("predEven: "+predEven);
        BitSet seen=new BitSet(vertices.size());
        for (;;) {
            v = uf.find(v);
            seen.set(v);
//            System.out.println("seen.add: "+v);
            int parent = uf.find(predEven.get(v)); //If not matched, then we've reached the root of the tree
//            int parent = predEven.get(v); //If not matched, then we've reached the root of the tree
            if(parent == v)
                break; //root of tree
            v= predOdd.get(parent);

//            if (!match.containsKey(v)) //We've reached the root of the tree
//                break;
//            v = predOdd.get(match.get(v));
        }
        for (;;) {
//            b = contracted.get(b);
            w=uf.find(w);
//            System.out.println("uf.find(w): "+w);
            if (seen.get(w))
                return w;
            w = predOdd.get(predEven.get(w));
        }
    }

    /**
     * Checks whether the given matching is of maximum cardinality. A matching m is maximum if there does not exist a different matching
     * m' in the graph which is of larger cardinality. This method is solely intended for verification purposes. Any matching returned
     * by the {@link #getMatching()} method in this class is guaranteed to be maximum.
     * <p>
     * To attest whether the matching is maximum, we use the Tutte-Berge Formula
     * which provides a tight bound on the cardinality of the matching. The Tutte-Berge Formula states:
     * 2 * m(G) = min_{X} ( |V(G)| + |X| - o(G-X) ), where m(G) is the size of the matching, X a subset of vertices,
     * G-X the induced graph on vertex set V(G)\X, and o(G) the number of connected components of odd cardinality in graph G.<br>
     * Note: to compute this bound, we do not iterate over all possible subsets X (this would be too expensive). Instead, X is
     * computed as a by-product of Edmonds algorithm. Consequently, the runtime of this method equals the runtime of one iteration
     * of Edmonds algorithm.
     * @param matching matching
     * @return true if the matching is maximum, false otherwise.
     */
    public boolean isMaximumMatching(Matching<V,E> matching){

        System.out.println("Matching: "+matching);
        System.out.println("graph: "+graph);

        //The matching is maximum if it is perfect, or if it leaves only one node exposed in a graph with an odd number of vertices
        if(matching.getEdges().size()*2 >= graph.vertexSet().size()-1)
            return true;

        this.init(); //Reset data structures and use the provided matching as a starting point
        for(E e : matching.getEdges()){
            V u=graph.getEdgeSource(e);
            V v=graph.getEdgeTarget(e);
            Integer ux=vertexIndexMap.get(u);
            Integer vx=vertexIndexMap.get(v);
            match.put(ux, vx);
            match.put(vx, ux);
        }

        // A side effect of the Edmonds Blossom-Shrinking algorithm is that it computes what is known as the
        // Edmonds-Gallai decomposition of a graph: it decomposes the graph into three disjoint sets of vertices: odd, even, or free.
        // Let D(G) be the set of vertices such that for each v in D(G) there exists a maximum matching missing v. Let A(G) be the set of vertices such that each v in A(G)
        // is a neighbor of D(G), but is not contained in D(G) itself. The set A(G) attains the minimum in the Tutte-Berge Formula. It can be shown that
        // A(G)= {vertices labeled odd in the Edmonds Blossomg-Shrinking algorithm}. Note: we only take odd vertices that are not consumed by blossoms (every blossom is even).

        //Choose an arbitrary unmatched vertex from each connected component. Try to find an augmenting path, starting from this vertex. If one is found, then clearly the matching is not maximum.
        List<Set<V>> connectedComponents=new ConnectivityInspector<>(graph).connectedSets();
        Set<V> oddVertices=new HashSet<>();
        for(Set<V> component : connectedComponents){
//            System.out.println("component: "+component);

            Iterator<V> it=component.iterator();
            V v =null;
            //Find unmatched vertex
            while (it.hasNext() && v==null){
                V w =it.next();
                if(!match.containsKey(w))
                    v=w;
            }
            if(v==null) //no unmatched vertices found in this component
                continue;

            System.out.println("1 pass edmonds. Start: "+vertexIndexMap.get(v));
            int endVertexAugmentingPath=findAugmentingPath(vertexIndexMap.get(v));
            //The matching is not maximum if an augmenting path was found
            if(endVertexAugmentingPath != nil) {
//                System.out.println("found augmenting path");
                return false;
            }

            //Record the odd vertices. Only consider odd vertices which are not part of some blossom.
            for(V w :component){
                int wx=vertexIndexMap.get(w);
//                System.out.println("wx: "+wx+" predOdd.containsKey(wx): "+predOdd.containsKey(wx)+" !uf.connected(wx, predOdd.get(wx)): "+!uf.connected(wx, predOdd.get(wx)));
                //If an odd vertex v is part of a blossom, than so must his even parent w. We simply check whether vertex
                //v and w are part of the same pseudo node. If so, then it must be a blossom.
                if(predOdd.containsKey(wx) && !uf.connected(wx, predOdd.get(wx))) {
                    oddVertices.add(w);
                }
            }

//            System.out.println("predODD: "+predOdd);
//            System.out.println("predeven: "+predEven);
//            System.out.println("uf: "+uf);
//            System.out.println("odd vertices: "+oddVertices);
        }


//        int v=vertexIndexMap.get(graph.vertexSet().stream().filter(u -> !matching.isMatched(u)).findAny().get());
//        int endVertexAugmentingPath=findAugmentingPath(v);
//        if(endVertexAugmentingPath != nil)
//            return false;

        System.out.println("checking tutte");
        System.out.println("predODD: "+predOdd);
        System.out.println("predeven: "+predEven);
        System.out.println("uf: "+uf);
        System.out.println("odd vertices: "+oddVertices);
//        Set<V> oddVertices = vertexIndexMap.values().stream().filter(w -> predOdd.containsKey(w) ).map(vertices::get).collect(Collectors.toSet());


        //Set<V> oddVertices= vertexIndexMap.values().stream().filter(vx -> odd[vx] != nil && !bridges.containsKey(vx)).map(vertices::get).collect(Collectors.toSet());
        Set<V> otherVertices=graph.vertexSet().stream().filter(w -> !oddVertices.contains(w)).collect(Collectors.toSet());

        Graph<V,E> subgraph=new AsSubgraph<>(graph, otherVertices, null); //Induced subgraph defined on all vertices which are not odd.
        List<Set<V>> connectedComponentsSubgraph=new ConnectivityInspector<>(subgraph).connectedSets();
        long nrOddCardinalityComponents=connectedComponentsSubgraph.stream().filter(s -> s.size()%2==1).count();

        System.out.println("matching size: "+matching.getEdges().size()+" tutte: "+((graph.vertexSet().size()+oddVertices.size()-nrOddCardinalityComponents)/2.0));
        return matching.getEdges().size() == (graph.vertexSet().size()+oddVertices.size()-nrOddCardinalityComponents)/2.0;
    }

}

// End EdmondsBlossomShrinking.java
