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

//TODO: Remove need to inverse paths
//TODO: do not exhaustively iterate over all exposed nodes to find a max non-perfect matching. Instead, shrink the graph
//TODO: track exposed nodes
//TODO: remove unnecessary code
//TODO: add comments to code and proper references

import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.alg.util.UnionFind;
import org.jgrapht.graph.AsSubgraph;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Maximum matching in general graphs using Edmond's Blossom Algorithm. This
 * implementation was adapted D Eppstein's python code (<a
 * href="http://www.ics.uci.edu/~eppstein/PADS/CardinalityMatching.py">src</a>)
 * which provides efficient tree traversal and handling of blossoms. The
 * implementation may be quite daunting as a general introduction to the ideas.
 * Personally I found <a href="http://www.keithschwarz.com/interesting/">Keith
 * Schwarz</a> version very informative when starting to understand the
 * workings. <p/>
 *
 * An asymptotically better algorithm is described by Micali and Vazirani (1980)
 * and is similar to bipartite matching (<a href="http://en.wikipedia.org/wiki/Hopcroft%E2%80%93Karp_algorithm">Hopkroft-Karp</a>)
 * where by multiple augmenting paths are discovered at once. In general though
 * this version is very fast - particularly if given an existing matching to
 * start from. Even the very simple ArbitraryMatching eliminates many
 * loop iterations particularly at the start when all length 1 augmenting paths
 * are discovered.
 *
 * @author Joris Kinable
 * @see <a href="http://en.wikipedia.org/wiki/Blossom_algorithm">Blossom
 *      algorithm, Wikipedia</a>
 * @see <a href="http://en.wikipedia.org/wiki/Hopcroft%E2%80%93Karp_algorithm">Hopkroft-Karp,
 *      Wikipedia</a>
 * @see <a href="http://research.microsoft.com/apps/video/dl.aspx?id=171055">Presentation
 *      from Vazirani on his and Micali O(|E| * sqrt(|V|)) algorithm</a>
 */
public class EdmondsMaxCardinalityMatchingBaseLineComparison<V,E> implements MatchingAlgorithm<V, E> {

    /* The graph we are matching on. */
    private final Graph<V,E> graph;
    /* (Heuristic) matching algorithm used to compute an initial solution */
    private MatchingAlgorithm<V,E> initializer;

    /*
    All vertices in the original graph are mapped to a unique integer to simplify the implementation and to improve efficiency.
     */
    private List<V> vertices;
    private Map<V, Integer> vertexIndexMap;

    /** The current matching. */
    private MatchingArray matching;

/* Algorithm data structures below. */

    /** Storage of the forest, even and odd levels */
    private int[] even, odd;

    /** Special 'nil' vertex. */
    private static final int nil = -1;

    /** Queue of 'even' (free) vertices to start paths from. */
    private FixedSizeQueue queue;

    /** Union-Find to store blossoms. */
    private UnionFind<Integer> uf;

    /**
     * Map stores the bridges of the blossom - indexed by with support
     * vertices.
     */
    private final Map<Integer, Pair<Integer,Integer>> bridges = new HashMap<>();

    /** Temporary array to fill with path information. */
    private int[]  path;

    /**
     * Temporary bit sets when walking down 'trees' to check for
     * paths/blossoms.
     */
    private BitSet vAncestors, wAncestors;

    /** Number of isMatched vertices. */
    private int nMatched;


    public EdmondsMaxCardinalityMatchingBaseLineComparison(Graph<V, E> graph) {
        this(graph, null);
//        this(graph, new GreedyMaxCardinalityMatching<V, E>(graph, false));
    }

    public EdmondsMaxCardinalityMatchingBaseLineComparison(Graph<V, E> graph, MatchingAlgorithm<V, E> initializer) {
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
        this.matching = new MatchingArray(vertices.size());
        if(initializer != null)
            this.warmStart(initializer);

        this.even = new int[vertices.size()];
        this.odd = new int[vertices.size()];

        this.queue = new FixedSizeQueue(vertices.size());
        this.uf = new UnionFind<>(vertexIndexMap.values());

        // tmp storage of paths in the algorithm
        path = new int[vertices.size()];
        vAncestors = new BitSet(vertices.size());
        wAncestors = new BitSet(vertices.size());

        this.nMatched = 0;
    }

    /**
     * Provide an algorithm which computes an initial solution. The algorithm is usually a heuristic.
     * @param initializer algorithm used to compute an initial matching
     */
    private void warmStart(MatchingAlgorithm<V,E> initializer){
        Matching<V,E> initialSolution=initializer.getMatching();
        System.out.println("warmstart: "+initialSolution.getWeight());
        for(E e : initialSolution.getEdges()){
            V u=graph.getEdgeSource(e);
            V v=graph.getEdgeTarget(e);
            Integer ux=vertexIndexMap.get(u);
            Integer vx=vertexIndexMap.get(v);
            this.matching.match(ux, vx);
        }
        nMatched=initialSolution.getEdges().size()*2;
    }

    /**
     * Find an augmenting path an alternate it's matching. If an augmenting path
     * was found then the search must be restarted. If a blossom was detected
     * the blossom is contracted and the search continues.
     *
     * @return an augmenting path was found
     */
    private boolean augment() {

        //If the matching is perfect, or if it leaves only one node exposed in a graph with an odd number of vertices, we cannot augment.
        if(nMatched >= graph.vertexSet().size()-1)
            return false;

        // reset data structures
        Arrays.fill(even, nil);
        Arrays.fill(odd, nil);
        uf.reset();
        bridges.clear();
        queue.clear();

        // queue every isExposed vertex and place in the
        // even level (level = 0)
        for (int v = 0; v < vertices.size(); v++) {
            if (matching.isExposed(v)) {
//                System.out.println("exposed: "+vertices.get(v));
                even[v] = v;
                queue.enqueue(v);
            }
        }

        // for each 'free' vertex, start a bfs search
        while (!queue.empty()) {
            int vx = queue.poll();
            V v=vertices.get(vx);

            for (V w : Graphs.neighborListOf(graph, v)) {
                int wx = vertexIndexMap.get(w);

                // the endpoints of the edge are both at even levels in the
                // forest - this means it is either an augmenting path or
                // a blossom
                if (even[uf.find(wx)] != nil) {
                    if (check(vx, wx))
                        return true;
                }

                // add the edge to the forest if is not already and extend
                // the tree with this isMatched edge
                else if (odd[wx] == nil) {
                    odd[wx] = vx;
//                    System.out.println("odd: "+wx);
                    int u = matching.other(wx);
                    // add the isMatched edge (potential though a blossom) if it
                    // isn't in the forest already
                    if (even[uf.find(u)] == nil) {
                        even[u] = wx;
                        queue.enqueue(u);
                    }
                }
            }
        }

        // no augmenting paths, matching is maximum
        return false;
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
        //The matching is maximum if it is perfect, or if it leaves only one node exposed in a graph with an odd number of vertices
        if(matching.getEdges().size()*2 >= graph.vertexSet().size()-1)
            return true;

        this.init(); //Reset data structures and use the provided matching as a starting point
        for(E e : matching.getEdges()){
            V u=graph.getEdgeSource(e);
            V v=graph.getEdgeTarget(e);
            Integer ux=vertexIndexMap.get(u);
            Integer vx=vertexIndexMap.get(v);
            this.matching.match(ux, vx);
        }
        //Search for an augmenting path. If one is found, then clearly the matching is not maximum
        boolean foundAugmentingPath=augment();
        if(foundAugmentingPath)
            return false;

        //A side effect of the Edmonds Blossom-Shrinking algorithm is that it computes what is known as the
        // Edmonds-Gallai decomposition of a graph: it decomposes the graph into three disjoint sets of vertices: odd, even, or free.
        // The odd set achieves the minimum in the Tutte-Berge Formula. Note: we only take odd vertices that are not consumed by blossoms (every blossom is even).
        Set<V> oddVertices= vertexIndexMap.values().stream().filter(vx -> odd[vx] != nil && !bridges.containsKey(vx)).map(vertices::get).collect(Collectors.toSet());
        Set<V> otherVertices=graph.vertexSet().stream().filter(v -> !oddVertices.contains(v)).collect(Collectors.toSet());

        Graph<V,E> subgraph=new AsSubgraph<>(graph, otherVertices, null); //Induced subgraph defined on all vertices which are not odd.
        List<Set<V>> connectedComponents=new ConnectivityInspector<>(subgraph).connectedSets();
        long nrOddCardinalityComponents=connectedComponents.stream().filter(s -> s.size()%2==1).count();
        return matching.getEdges().size() == (graph.vertexSet().size()+oddVertices.size()-nrOddCardinalityComponents)/2.0;
    }

    /**
     * An edge was found which connects two 'even' vertices in the forest. If
     * the vertices have the same root we have a blossom otherwise we have
     * identified an augmenting path. This method checks for these cases and
     * responds accordingly. <p/>
     *
     * If an augmenting path was found - then it's edges are alternated and the
     * method returns true. Otherwise if a blossom was found - it is contracted
     * and the search continues.
     *
     * @param v endpoint of an edge
     * @param w another endpoint of an edge
     * @return a path was augmented
     */
    private boolean check(int v, int w) {

        // self-loop (within blossom) ignored
        if (uf.find(v).equals(uf.find(w)))
            return false;

        vAncestors.clear();
        wAncestors.clear();
        int vCurr = v;
        int wCurr = w;

        // walk back along the trees filling up 'vAncestors' and 'wAncestors'
        // with the vertices in the tree -  vCurr and wCurr are the 'even' parents
        // from v/w along the tree
        while (true) {

            vCurr = parent(vAncestors, vCurr);
            wCurr = parent(wAncestors, wCurr);

            // v and w lead to the same root - we have found a blossom. We
            // travelled all the way down the tree thus vCurr (and wCurr) are
            // the base of the blossom
            if (vCurr == wCurr) {
                blossom(v, w, vCurr);
                return false;
            }

            // we are at the root of each tree and the roots are different, we
            // have found and augmenting path
            if (uf.find(even[vCurr]) == vCurr && uf.find(even[wCurr]) == wCurr) {
                augment(v);
                augment(w);
                matching.match(v, w);
                return true;
            }

            // the current vertex in 'v' can be found in w's ancestors they must
            // share a root - we have found a blossom whose base is 'vCurr'
            if (wAncestors.get(vCurr)) {
                blossom(v, w, vCurr);
                return false;
            }

            // the current vertex in 'w' can be found in v's ancestors they must
            // share a root, we have found a blossom whose base is 'wCurr'
            if (vAncestors.get(wCurr)) {
                blossom(v, w, wCurr);
                return false;
            }
        }
    }

    /**
     * Access the next ancestor in a tree of the forest. Note we go back two
     * places at once as we only need check 'even' vertices.
     *
     * @param ancestors temporary set which fills up the path we traversed
     * @param curr      the current even vertex in the tree
     * @return the next 'even' vertex
     */
    private int parent(BitSet ancestors, int curr) {
        curr = uf.find(curr);
        ancestors.set(curr);
        int parent = uf.find(even[curr]);
        if (parent == curr)
            return curr; // root of tree
        ancestors.set(parent);
        return uf.find(odd[parent]);
    }

    /**
     * Create a new blossom for the specified 'bridge' edge.
     *
     * @param v    adjacent to w
     * @param w    adjacent to v
     * @param base connected to the stem (common ancestor of v and w)
     */
    private void blossom(int v, int w, int base) {
        base = uf.find(base);
        int[] supports1 = blossomSupports(v, w, base);
        int[] supports2 = blossomSupports(w, v, base);

        for (int i = 0; i < supports1.length; i++)
            uf.union(supports1[i], supports1[0]);
        for (int i = 0; i < supports2.length; i++)
            uf.union(supports2[i], supports2[0]);

        even[uf.find(base)] = even[base];
    }

    /**
     * Creates the blossom 'supports' for the specified blossom 'bridge' edge
     * (v, w). We travel down each side to the base of the blossom ('base')
     * collapsing vertices and point any 'odd' vertices to the correct 'bridge'
     * edge. We do this by indexing the birdie to each vertex in the 'bridges'
     * map.
     *
     * @param v    an endpoint of the blossom bridge
     * @param w    another endpoint of the blossom bridge
     * @param base the base of the blossom
     */
    private int[] blossomSupports(int v, int w, int base) {

        int n = 0;
        path[n++] = uf.find(v);
        Pair<Integer,Integer> b = new Pair<>(v,w);
        while (path[n - 1] != base) {
            int u = even[path[n - 1]];
            path[n++] = u;
            this.bridges.put(u, b);
            // contracting the blossom allows us to continue searching from odd
            // vertices (any odd vertices are now even - part of the blossom set)
            queue.enqueue(u);
            path[n++] = uf.find(odd[u]);
        }

        return Arrays.copyOf(path, n);
    }

    /**
     * Augment all ancestors in the tree of vertex 'v'.
     *
     * @param v the leaf to augment from
     */
    private void augment(int v) {
        int n = buildPath(path, 0, v, nil);
        for (int i = 2; i < n; i += 2) {
            matching.match(path[i], path[i - 1]);
        }
    }

    /**
     * Builds the path backwards from the specified 'start' vertex until the
     * 'goal'. If the path reaches a blossom then the path through the blossom
     * is lifted to the original graph.
     *
     * @param path  path storage
     * @param i     offset (in path)
     * @param start start vertex
     * @param goal  end vertex
     * @return the number of items set to the path[].
     */
    private int buildPath(int[] path, int i, int start, int goal) {
        while (true) {

            // lift the path through the contracted blossom
            while (odd[start] != nil) {

                Pair<Integer,Integer> bridge = bridges.get(start);

                // add to the path from the bridge down to where 'start'
                // is - we need to reverse it as we travel 'up' the blossom
                // and then...
                int j = buildPath(path, i, bridge.getFirst(), start);
                reverse(path, i, j - 1);
                i = j;

                // ... we travel down the other side of the bridge
                start = bridge.getSecond();
            }
            path[i++] = start;

            // root of the tree
            if (matching.isExposed(start))
                return i;

            path[i++] = matching.other(start);

            // end of recursive
            if (path[i - 1] == goal)
                return i;

            start = odd[path[i - 1]];
        }
    }

    @Override
    public Matching<V, E> getMatching() {
        this.init();

        // continuously augment while we find new paths, each
        // path increases the matching cardinality by 2
        while (augment()) {
            nMatched += 2;
        }

        Set<E> edges=new LinkedHashSet<E>();
        for (int vx = 0; vx < matching.match.length; vx++) {
            V v =vertices.get(vx);
            int wx = matching.match[vx];
            if (wx > vx && matching.match[wx] == vx) {
                V w =vertices.get(wx);
                edges.add(graph.getEdge(v,w));
            }
        }

        return new MatchingImpl<V, E>(graph, edges, edges.size());
    }

    final class MatchingArray {

        /** Indicates an isExposed vertex. */
        private static final int UNMATCHED = -1;

        /** Storage of which each vertex is isMatched with. */
        private final int[] match;

        /**
         * Create a matching of the given size.
         *
         * @param n number of items
         */
        private MatchingArray(int n) {
            this.match = new int[n];
            Arrays.fill(match, UNMATCHED);
        }

        boolean matched(int v) {
            return !isExposed(v);
        }

        /**
         * Is the vertex v 'isExposed'.
         *
         * @param v a vertex
         * @return the vertex has no matching
         */
        boolean isExposed(int v) {
            int w = match[v];
            return w < 0 || match[w] != v;
        }

        /**
         * Access the vertex isMatched with 'v'.
         *
         * @param v a vertex
         * @return isMatched vertex
         * @throws IllegalArgumentException the vertex is currently isExposed
         */
        int other(int v) {
            if (isExposed(v))
                throw new IllegalArgumentException(v + " is not matched");
            return match[v];
        }

        /**
         * Add the edge '{u,v}' to the isMatched edge set. Any existing matches for
         * 'u' or 'v' are removed from the isMatched set.
         *
         * @param u a vertex
         * @param v another vertex
         */
        void match(int u, int v) {
            // set the new match, don't need to update existing - we only provide
            // access to bidirectional mappings
            match[u] = v;
            match[v] = u;
        }

    }

    /**
     * Utility class provides a fixed size queue. Enough space is allocated for
     * every vertex in the graph. Any new vertices are added at the 'end' index
     * and 'polling' a vertex advances the 'start'.
     */
    private static final class FixedSizeQueue {
        private final int[] vs;
        private int i = 0;
        private int n = 0;

        /**
         * Create a queue of size 'n'.
         *
         * @param n size of the queue
         */
        private FixedSizeQueue(int n) {
            vs = new int[n];
        }

        /**
         * Add an element to the queue.
         *
         * @param e
         */
        void enqueue(int e) {
            vs[n++] = e;
        }

        /**
         * Poll the first element from the queue.
         *
         * @return the first element.
         */
        int poll() {
            return vs[i++];
        }

        /**
         * Check if the queue has any items.
         *
         * @return the queue is empty
         */
        boolean empty() {
            return i == n;
        }

        /** Reset the queue. */
        void clear() {
            i = 0;
            n = 0;
        }
    }

    /** Utility to reverse a section of a fixed size array */
    static void reverse(int[] path, int i, int j) {
        while (i < j) {
            int tmp = path[i];
            path[i] = path[j];
            path[j] = tmp;
            i++;
            j--;
        }
    }
}
