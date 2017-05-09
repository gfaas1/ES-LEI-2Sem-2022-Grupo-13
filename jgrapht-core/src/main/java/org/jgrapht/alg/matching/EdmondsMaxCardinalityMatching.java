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
public class EdmondsMaxCardinalityMatching<V,E> implements MatchingAlgorithm<V, E> {

    public final boolean DEBUG=false;

    /* The graph we are matching on. */
    private final Graph<V,E> graph;
    /* (Heuristic) matching algorithm used to compute an initial feasible solution */
    private MatchingAlgorithm<V,E> initializer;

    /* Ordered list of vertices */
    private List<V> vertices;
    /* Mapping of a vertex to their unique position in the ordered list of vertices */
    private Map<V, Integer> vertexIndexMap;

    /* A matching for the input graph (can be an empty set of edges) */
    private SimpleMatching matching;

    /* Number of matched vertices. */
    private int matchedVertices;


    /* -----Algorithm data structures below---------- */

    /** Storage of the forest, even and odd levels */
    private int[] even, odd;

    /** Special 'nil' vertex. */
    private static final int nil = -1;

    /** Queue of 'even' (exposed) vertices */
    private FixedSizeQueue queue;

    /** Union-Find to store blossoms. */
    private UnionFind<Integer> uf;

    /**
     * For each odd vertex condensed into a blossom, a bridge is defined. Suppose the examination of edge [v,w] causes a blossom to form containing odd vertex x.
     * We define bridge(x) to be [v,w] if x is an ancestor of v before the blossom is formed, or [w,v] if x is an ancestor of w.
     */
    private final Map<Integer, Pair<Integer,Integer>> bridges = new HashMap<>();

    /** Pre-allocated array which stores augmenting paths. */
    private int[]  path;

     /* Pre-allocated bit sets to track paths in the trees. */
    private BitSet vAncestors, wAncestors;


    /**
     * Constructs a new instance of the algorithm. {@link GreedyMaxCardinalityMatching} is used to quickly generate a
     * near optimal initial solution.
     * @param graph graph
     */
    public EdmondsMaxCardinalityMatching(Graph<V,E> graph) {
//        this(graph, null);
        this(graph, new GreedyMaxCardinalityMatching<V, E>(graph, false));
    }

    /**
     * Constructs a new instance of the algorithm.
     * @param graph graph
     * @param initializer heuristic matching algorithm used to quickly generate a (near optimal) initial feasible solution.
     */
    public EdmondsMaxCardinalityMatching(Graph<V,E> graph, MatchingAlgorithm<V,E> initializer) {
        this.graph = GraphTests.requireUndirected(graph);
        this.initializer=initializer;
    }

    /**
     * Prepares the data structures
     */
    private void init(){
        vertices=new ArrayList<>();
        vertices.addAll(graph.vertexSet());
        vertexIndexMap=new HashMap<>();
        for(int i=0; i<vertices.size(); i++)
            vertexIndexMap.put(vertices.get(i), i);
        this.matching = new SimpleMatching(vertices.size());
        this.matchedVertices = 0;
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
    }

    /**
     * Calculates an initial feasible.
     * @param initializer algorithm used to compute the initial matching
     */
    private void warmStart(MatchingAlgorithm<V,E> initializer){
        Matching<V,E> initialSolution=initializer.getMatching();
        for(E e : initialSolution.getEdges()){
            V u=graph.getEdgeSource(e);
            V v=graph.getEdgeTarget(e);
            this.matching.match(vertexIndexMap.get(u), vertexIndexMap.get(v));
        }
        matchedVertices =initialSolution.getEdges().size()*2;
    }


    /**
     * Find an augmenting path an alternate it's matching. If an augmenting path
     * was found then the search must be restarted. If a blossom was detected
     * the blossom is contracted and the search continues.
     *
     * @return an augmenting path was found
     */
    private boolean augment() {

        // reset data structures
        Arrays.fill(even, nil);
        Arrays.fill(odd, nil);
        uf.reset();
        bridges.clear();
        queue.clear();



        for (int root = 0; root < vertices.size(); root++) {
            if(matching.isMatched(root)) //Only grow trees from exposed nodes
                continue;
            even[root] = root;
            queue.enqueue(root);

//            thisTree=new HashSet<>();
//            thisTree.add(root);

            if(DEBUG) System.out.println("\ngrowing path from "+root);

            // queue every isExposed vertex and place in the
            // even level (level = 0)


            // for each 'free' vertex, start a bfs search
            while (!queue.empty()) {
                int v = queue.poll(); //Even vertex

                for (V wOrig : Graphs.neighborListOf(graph, vertices.get(v))) {
                    int w = vertexIndexMap.get(wOrig);

                    if(DEBUG) System.out.println("checking edge ("+v+","+w+")");

                    // the endpoints of the edge are both at even levels in the
                    // forest - this means it is either an augmenting path or
                    // a blossom
                    if (even[uf.find(w)] != nil) { //w is an even vertex
                        // if v and w belong to the same blossom, the edge has been shrunken away and we can ignore it.
                        // if not, we found a new blossom.
                        if (!uf.connected(v, w)) {
//                            if(!thisTree.contains(w)) {
//                                throw new RuntimeException("trying to create blossom with even node from other tree. This tree: "+thisTree);
//                            }
                            blossom(v, w); //Create a new blossom using bridge edge (v,w)
                        }
                    }

                    // add the edge to the forest if is not already and extend
                    // the tree with this isMatched edge
                    else if (odd[w] == nil) { //w is an odd vertex or is an unreached vertex

                        if(matching.isExposed(w)){ //w is unreached: we found an augmenting path
                            if(DEBUG) System.out.println("found augmenting path after adding edge ("+v+","+w+")");
                            augment(v);
                            augment(w);
                            matching.match(v, w);
                            return true;
                        }
                        odd[w] = v;
                        int u = matching.opposite(w);

                        if(DEBUG) System.out.println("growing tree ("+v+","+w+","+u+")");

                        even[u] = w;
//                        if(thisTree.contains(u))
//                            throw new RuntimeException("adding even node again 1");
                        queue.enqueue(u); //CHECK WHETHER u has been added to the queue already?
//                        thisTree.add(u);
                    }
                }
            }
        }

        if(DEBUG) System.out.println("No augmenting path found");

        // no augmenting paths, matching is maximum
        return false;
    }

    /**
     * Creates a new blossom using bridge (v,w). The blossom is an odd cycle. Nodes v and w are both even vertices.
     *
     * @param v endpoint of the bridge
     * @param w another endpoint the bridge
     */
    private void blossom(int v, int w) {
        //Compute the base of the blossom. Let p1, p2 be the paths from the root of the tree to v resp. w. The base vertex
        //is the last vertex p1 and p2 have in common. In a blossom, the base vertex is unique in the sense that it is
        //the only vertex incident to 2 unmatched edges.
        int base=lowestCommonAncestor(v, w);

        if(DEBUG) System.out.println("Found blossom. base: "+base+" bridge: ("+v+","+w+")");
        //Compute resp the left leg (v to base) and right leg (w to base) of the blossom.
        blossomSupports(v, w, base);
        blossomSupports(w, v, base);

        //To complete the blossom, combine the left and the right leg.
        uf.union(v, base);
        uf.union(w, base);

        //Blossoms are efficiently stored in a UnionFind data structure uf. Ideally, uf.find(x) for some vertex x returns
        //the base u of the blossom containing x. However, when uf uses rank compression, it cannot be guaranteed that the vertex
        // returned is indeed the base of the blossom. In fact, it can be any vertex of the blossom containing x. We therefore have to ensure
        //that the predecessor of the blossom's representative is the predecessor of the actual base vertex.
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
    private void blossomSupports(int v, int w, int base) {
        Pair<Integer,Integer> bridge = new Pair<>(v,w);
        v=uf.find(v);
        int u=v;
        while (v != base){
            uf.union(v, u);

            u = even[v]; //odd vertex
            this.bridges.put(u, bridge);
            queue.enqueue(u);
            uf.union(v, u);
            v = uf.find(odd[u]); //even vertex
        }
    }

    private int lowestCommonAncestor(int v, int w)
    {
        vAncestors.clear();
        wAncestors.clear();

        // walk back along the trees filling up 'vAncestors' and 'wAncestors'
        // with the vertices in the tree -  vCurr and wCurr are the 'even' parents
        // from v/w along the tree
        int counter=0;
        while (true) {
            if(counter>vertices.size())
                throw new RuntimeException("counter exceeded2");

            v = parent(vAncestors, v);
            w = parent(wAncestors, w);

            // v and w lead to the same root - we have found a blossom. We
            // travelled all the way down the tree thus vand w) are
            // the base of the blossom
            if (v == w) {
                return v;
            }
            // the current vertex in 'v' can be found in w's ancestors they must
            // share a root - we have found a blossom whose base is 'v'
            else if (wAncestors.get(v)) {
                return v;
            }

            // the current vertex in 'w' can be found in v's ancestors they must
            // share a root, we have found a blossom whose base is 'w'
            else if (vAncestors.get(w)) {
                return w;
            }
            counter++;
        }
    }

    /**
     * Compute the nearest even ancestor of even node v. If v is the root of a tree, then this method returns v itself.
     *
     * @param ancestors temporary set which records
     * @param v      even vertex
     * @return the nearest even ancestor of v
     */
    private int parent(BitSet ancestors, int v) {
        v = uf.find(v);
        ancestors.set(v);
        int parent = uf.find(even[v]);
        if (parent == v)
            return v; // root of tree
        ancestors.set(parent); //NOT NEEDED? We do not need to track odd ancestors?
        return uf.find(odd[parent]);
    }



    /**
     * Construct a path from vertex v to the root of its tree, and use the resulting path to augment the matching.
     *
     * @param v starting vertex (leaf in the tree)
     */
    private void augment(int v) {
        int n = buildPath(path, 0, v, nil);
        for (int i = 2; i < n; i += 2) {
            matching.match(path[i], path[i - 1]);
        }
    }

    /**
     * Builds the path backwards from the specified 'start' vertex to the
     * 'end' vertex. If the path reaches a blossom then the path through the blossom
     * is lifted to the original graph.
     *
     * @param path  path storage
     * @param i     offset (in path)
     * @param start start vertex
     * @param end  end vertex
     * @return the total length of the path.
     */
    private int buildPath(int[] path, int i, int start, int end) {
        while (true) {

            // Lift the path through the blossom. The buildPath method always starts from an even vertex. Vertices which were originally odd become even
            // when they are contracted into a blossom. If we start constructing the path from such an odd vertex, we must 'lift' the path through the blossom.
            // To lift the path through the blossom, we have to walk from odd node u in the direction of the bridge, cross the bridge, and then
            // continue in the direction of the tree root.
            while (odd[start] != nil) {

                Pair<Integer,Integer> bridge = bridges.get(start);

                //From the start vertex u, walk in the direction of the bridge (v,w). The first edge encountered
                //on the path from u to v is always a matched edge. Notice that the path from u to v leads away from the root of the tree. Since we only store
                // pointers in the direction of the root, we have to compute a path from v to u, and reverse the resulting path.
                int j = buildPath(path, i, bridge.getFirst(), start);
                reverse(path, i, j - 1);
                i = j;

                //walk from the other side of the bridge up in the direction of the root.
                start = bridge.getSecond();
            }
            path[i++] = start; //even vertex

            // root of the tree
            if (matching.isExposed(start))
                return i;

            path[i++] = matching.opposite(start); //odd vertex

            // base case
            if (path[i - 1] == end)
                return i;

            start = odd[path[i - 1]]; //even vertex
        }
    }

    @Override
    public Matching<V, E> getMatching() {
        this.init();

        // Continuously augment the matching until augmentation is no longer possible.
        while (matchedVertices < graph.vertexSet().size()-1 && augment()) {
            matchedVertices += 2;
        }

        Set<E> edges=new LinkedHashSet<>();
        for (int vx = 0; vx < vertices.size(); vx++) {
            if(matching.isExposed(vx))
                continue;
            V v =vertices.get(vx);
            V w = vertices.get(matching.opposite(vx));
            edges.add(graph.getEdge(v,w));
        }

        return new MatchingImpl<>(graph, edges, edges.size());
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
     * computed as a by-product of Edmonds algorithm. Consequently, the runtime of this method equals the time required to test for the
     * existence of an augmenting path.
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
        if(augment())
            return false;

        // A side effect of the Edmonds Blossom-Shrinking algorithm is that it computes what is known as the
        // Edmonds-Gallai decomposition of a graph: it decomposes the graph into three disjoint sets of vertices: odd, even, or free.
        // Let D(G) be the set of vertices such that for each v in D(G) there exists a maximum matching missing v. Let A(G) be the set of vertices such that each v in A(G)
        // is a neighbor of D(G), but is not contained in D(G) itself. The set A(G) attains the minimum in the Tutte-Berge Formula. It can be shown that
        // A(G)= {vertices labeled odd in the Edmonds Blossomg-Shrinking algorithm}. Note: we only take odd vertices that are not consumed by blossoms (every blossom is even).
        Set<V> oddVertices= vertexIndexMap.values().stream().filter(vx -> odd[vx] != nil && !bridges.containsKey(vx)).map(vertices::get).collect(Collectors.toSet());
        Set<V> otherVertices=graph.vertexSet().stream().filter(v -> !oddVertices.contains(v)).collect(Collectors.toSet());

        Graph<V,E> subgraph=new AsSubgraph<>(graph, otherVertices, null); //Induced subgraph defined on all vertices which are not odd.
        List<Set<V>> connectedComponents=new ConnectivityInspector<>(subgraph).connectedSets();
        long nrOddCardinalityComponents=connectedComponents.stream().filter(s -> s.size()%2==1).count();

//        System.out.println("matching size: "+matching.getEdges().size()+" tutte: "+((graph.vertexSet().size()+oddVertices.size()-nrOddCardinalityComponents)/2.0));

        return matching.getEdges().size() == (graph.vertexSet().size()+oddVertices.size()-nrOddCardinalityComponents)/2.0;
    }

    /**
     * Simple representation of a matching
     */
    final class SimpleMatching {

        private static final int UNMATCHED = -1;
        private final int[] match;

        private SimpleMatching(int n) {
            this.match = new int[n];
            Arrays.fill(match, UNMATCHED);
        }

        /**
         * Test whether a vertex is matched (i.e. incident to a matched edge).
         */
        boolean isMatched(int v) {
            return match[v] != UNMATCHED;
        }

        /**
         * Test whether a vertex is exposed (i.e. not incident to a matched edge).
         */
        boolean isExposed(int v) {
            return match[v]==UNMATCHED;
        }

        /**
         * For a given vertex v and matched edge (v,w), this function returns vertex w.
         */
        int opposite(int v) {
            assert isMatched(v);
            return match[v];
        }

        /**
         * Add the edge '{u,v}' to the matched edge set.
         */
        void match(int u, int v) {
            match[u] = v;
            match[v] = u;
        }
    }

    /**
     * Efficient implementation of a fixed size queue for integers.
     */
    private static final class FixedSizeQueue{
        private final int[] vs;
        private int i = 0;
        private int n = 0;

        /**
         * Create a queue of size n.
         *
         * @param n size of the queue
         */
        private FixedSizeQueue(int n) {
            vs = new int[n];
        }

        /**
         * Add an element to the queue.
         *
         * @param e element
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
         * @return true if the queue is empty
         */
        boolean empty() {
            return i == n;
        }

        /** Empty the queue. */
        void clear() {
            i = 0;
            n = 0;
        }

        public String toString(){
            String s="";
            for(int j=i; j<n; j++)
                s+=vs[j]+" ";
            return s;
        }
    }

    /** Utility function to reverse part of an array */
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
