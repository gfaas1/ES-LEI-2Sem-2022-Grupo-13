/*
 * (C) Copyright 2018-2018, by Christoph Grüne, Daniel Mock, Oliver Feith and Contributors.
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
package org.jgrapht.alg.color;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;

import java.io.Serializable;
import java.util.*;

/**
 * Implementation of the color refinement algorithm that finds the coarsest stable coloring of a graph based on a given
 * coloring <code>alpha</code> as described in
 * C. Berkholz, P. Bonsma, and M. Grohe.  Tight lower and upper bounds for the complexity of canonical
 * colour refinement. Theory of Computing Systems,doi:10.1007/s00224-016-9686-0, 2016 (color refinement)
 * The complexity of this algorithm is O(|V| + |E| log |V|).
 *
 * @param <V> the vertex type
 * @param <E> the edge type
 *
 * @author Christoph Grüne
 * @author Daniel Mock
 * @author Oliver Feith
 */
public class ColorRefinementAlgorithm<V, E> implements VertexColoringAlgorithm<V>, Serializable {

    private static final long serialVersionUID = -987646758624545630L;

    /**
     * The input graph
     */
    protected final Graph<V, E> graph;

    private Coloring<V> alpha;

    private Integer k;

    /**
     * Construct a new coloring algorithm.
     *
     * @param graph the input graph
     * @param alpha the coloring on the graph to be refined
     */
    public ColorRefinementAlgorithm(Graph<V, E> graph, Coloring<V> alpha) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        this.alpha = Objects.requireNonNull(alpha, "alpha cannot be null");
        if(!isAlphaConsistent(alpha, graph)) {
            throw new IllegalArgumentException("alpha is not a valid surjective l-coloring for the given graph.");
        }
    }

    /**
     * Construct a new coloring algorithm.
     *
     * @param graph the input graph
     */
    public ColorRefinementAlgorithm(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        this.alpha = getDefaultAlpha(graph.vertexSet());
    }

    /**
     * calculates a canonical surjective k-coloring of the given graph such that the classes of the coloring form the coarsest stable partition that refines alpha.
     *
     * @return the calculated coloring
     */
    @Override
    public Coloring<V> getColoring() {
        Integer n = graph.vertexSet().size(); // the size of the graph
        k = alpha.getNumberColors(); // number of colors used

        // mapping from all colors to their classes
        HashMap<Integer, List<V>> C = new HashMap<>(n + 1);
        // mapping from color to their classes, whereby every vertex in the classes has colorDegree(v) >= 1
        HashMap<Integer, List<V>> A = new HashMap<>(n + 1);

        int[] maxColorDegree = new int[n + 1]; // mapping from color to its maximum color degree
        int[] minColorDegree = new int[n + 1]; // mapping from color to its minimum color degree
        Map<V, Integer> colorDegree = new HashMap<>(); // mapping from vertex to the color degree (number of neighbors with different colors) of the vertex
        Map<V, Integer> coloring = new HashMap<>(); // stores the coloring (that is returned in the end)

        for(int c = 1; c <= n; ++c) {
            C.put(c, new ArrayList<>()); // init color classes
            A.put(c, new ArrayList<>()); // init color classes with colorDegree(v) >= 1
            // the maximum color degree is already initialised with 0
        }
        for(V v : graph.vertexSet()) {
            C.get(alpha.getColors().get(v)).add(v); // init the color classes corresponding to the given coloring alpha
            colorDegree.put(v, 0); // init the color degree for every vertex with 0
            coloring.put(v, alpha.getColors().get(v)); // assign a color to every vertex (for initialization)
        }
        Deque<Integer> refineStack = getSortedStack(alpha); // get an ascendingly sorted stack of all colors that are predefined by alpha
        ArrayList<Integer> adjacentColors = new ArrayList<>(n); // list of all colors that have at least one vertex with colorDegree >= 1

        while(!refineStack.isEmpty()) {
            Integer r = refineStack.pop(); // analyze the next color

            //calculate number of neighbours of v of color r, and calculate maximum and minimum degree of all colors
            calculateColorDegree(r, coloring, C, A, adjacentColors, maxColorDegree, minColorDegree, colorDegree);

            //calculate new partition of the colors and update the color classes correspondingly
            calculateColorPartition(coloring, C, A, refineStack, adjacentColors, maxColorDegree, minColorDegree, colorDegree);

            //reset attributes for new iteration such that the invariants are still correct
            Iterator<Integer> adjColorIterator = adjacentColors.iterator();
            
            while(adjColorIterator.hasNext()) {
                Integer c = adjColorIterator.next();
                
                for(V v : A.get(c)) {
                    colorDegree.put(v, 0);
                }
                maxColorDegree[c] = 0;
                A.put(c, new ArrayList<>());
                adjColorIterator.remove();
            }
        }

        return new ColoringImpl<>(coloring, coloring.size());
    }

    /**
     * Helper method for getColoring().
     * calculates the color degree for every vertex and the maximum and minimum color degree for every color.
     *
     * @param r refining color (current color in the iteration)
     * @param color the color mapping
     * @param C the mapping from all colors to their classes
     * @param A the mapping from all colors to their classes with colorDegree(v) >= 1
     * @param adjacentColors the list of all colors that have at least one vertex with colorDegree >= 1
     * @param maxColorDegree the mapping from color to its maximum color degree
     * @param minColorDegree the mapping from color to its minimum color degree
     * @param colorDegree the mapping from vertex to the color degree (number of neighbors with different colors) of the vertex
     */
    private void calculateColorDegree(Integer r, Map<V, Integer> color, HashMap<Integer, List<V>> C, HashMap<Integer, List<V>> A, ArrayList<Integer> adjacentColors, int[] maxColorDegree, int[] minColorDegree, Map<V, Integer> colorDegree) {
        for(V v : C.get(r)) {
            Set<E> incomingEdges = graph.incomingEdgesOf(v); // get all incident edges to get all adjacent vertices
            
            Set<V> inNeighborhood = new HashSet<V>();
            
            for(E e: incomingEdges) {
                // add the source vertices to the set -- however, this notion is unclear for undirected graphs, hence we need to test both edge endpoints and take the one not equal to v
                V target = graph.getEdgeTarget(e);
                if(!target.equals(v)) {
                    inNeighborhood.add(target);
                } else {
                    inNeighborhood.add(graph.getEdgeSource(e));
                }
            }
            
            for(V w : inNeighborhood) { // go through all vertices in the in-neighborhood
                colorDegree.put(w, colorDegree.get(w) + 1); // increase color degree of the current vertex
                if(colorDegree.get(w) == 1) { // add vertex to A if color degree of exactly 1 is reached
                    A.get(color.get(w)).add(w);
                }
                if(!adjacentColors.contains(color.get(w))) { // add vertex to Color_adj only if it is not already contained in Color_adj
                    adjacentColors.add(color.get(w));
                }
                if(colorDegree.get(w) > maxColorDegree[color.get(w)]) { // update maxColorDegree for color(w) if maximum color degree has increased
                    maxColorDegree[color.get(w)] = colorDegree.get(w);
                }
            }
        }

        for(Integer c : adjacentColors) { // go through all colors, which have at least one vertex with colorDegree >= 1, to update minColorDegree
            if(C.get(c).size() != A.get(c).size()) { // if there is a vertex with colorDegree(v) = 0 < 1, set minimum color degree to 0
                minColorDegree[c] = 0;
            } else {
                minColorDegree[c] = maxColorDegree[c]; // set minColorDegree(c) to maxColorDegree before iterating over all vertices
                for(V v : A.get(c)) { // update minColorDegree by iterating over all vertices
                    if (colorDegree.get(v) < minColorDegree[c]) { // if there is a vertex v with lower color degree, update
                        minColorDegree[c] = colorDegree.get(v);
                    }
                } // now minColorDegree is correctly computed for color c
            }
        }
    }

    /**
     * Helper method for getColoring().
     * Partition the colors that do not have the same minimum and maximum color degree. That is, these colors are not completely refined.
     *
     * @param color the color mapping
     * @param C the mapping from all colors to their classes
     * @param A the mapping from all colors to their classes with colorDegree(v) >= 1
     * @param refineStack the stack containing all colors that have to be refined
     * @param adjacentColors the list of all colors that have at least one vertex with colorDegree >= 1
     * @param maxColorDegree the mapping from color to its maximum color degree
     * @param minColorDegree the mapping from color to its minimum color degree
     * @param colorDegree the mapping from vertex to the color degree (number of neighbors with different colors) of the vertex
     */
    private void calculateColorPartition(Map<V, Integer> color, HashMap<Integer, List<V>> C, HashMap<Integer, List<V>> A, Deque<Integer> refineStack, ArrayList<Integer> adjacentColors, int[] maxColorDegree, int[] minColorDegree, Map<V, Integer> colorDegree) {
        ArrayList<Integer> Colors_split = new ArrayList<>(); // subset of adjacentColors that will be split up into different color classes
        for(Integer c : adjacentColors) {
            if(minColorDegree[c] < maxColorDegree[c]) { // colors have to be refined as the vertices with that color do not have the same color degree
                Colors_split.add(c);
            }
        }
        Colors_split.sort(Comparator.comparingInt(o -> o)); // sort list because the colors have to be considered in canonical order
        for(Integer s : Colors_split) { // split the colors for all colors in Colors_split
            splitUpColor(s, color, C, A, refineStack, maxColorDegree, minColorDegree, colorDegree);
        }
    }

    /**
     * Helper method for getColoring().
     *
     * @param s the color to split the color class for
     * @param color the color mapping
     * @param C the mapping from all colors to their classes
     * @param A the mapping from all colors to their classes with colorDegree(v) >= 1
     * @param refineStack the stack containing all colors that have to be refined
     * @param maxColorDegree the mapping from color to its maximum color degree
     * @param minColorDegree the mapping from color to its minimum color degree
     * @param colorDegree the mapping from vertex to the color degree (number of neighbors with different colors) of the vertex
     */
    private void splitUpColor(Integer s, Map<V, Integer> color, HashMap<Integer, List<V>> C, HashMap<Integer, List<V>> A, Deque<Integer> refineStack, int[] maxColorDegree, int[] minColorDegree, Map<V, Integer> colorDegree) {
        Map<Integer, Integer> numColorDegree = new HashMap<>(); // mapping from the color degree to the number of vertices with that color degree
        Map<Integer, Integer> f = new HashMap<>(); // mapping from color degrees that occur in S to newly introduced colors or to color s
        boolean instack; // helper variable that stores if a color is already in the stack refineStack

        int currentMaxColorDegree = maxColorDegree[s]; // maxColorDegree_ is the maximum color degree of color s (the color to split the color class for)

        for(int i = 1; i <= currentMaxColorDegree; ++i) { // initialize numColorDegree
            numColorDegree.put(i, 0);
        }
        numColorDegree.put(0, C.get(s).size() - A.get(s).size()); // add the number of vertices with color degree 0 to numColorDegree;
        // remember C maps all colors to their classes and A only to vertices in that class with colorDegree(v) >= 1

        for(V v : A.get(s)) { // iterate over all vertices and update numColorDegree correspondingly
            numColorDegree.put(colorDegree.get(v), numColorDegree.get(colorDegree.get(v)) + 1);
        }
        int b = 0; // helper variable storing the index with the maximum number of vertices with the corresponding color degree
        for(int i = 1; i <= currentMaxColorDegree; ++i) { // find b as defined above
            if(numColorDegree.get(i) > numColorDegree.get(b)) {
                b = i;
            }
        }
        instack = refineStack.contains(s); // is s already in stack refineStack?

        // add new colors to the stack refineStack, which have to be refined further, corresponding to the calculations before
        // calculate the mapping f.
        addColorsToRefineStackAndComputeF(s, currentMaxColorDegree, minColorDegree, refineStack, numColorDegree, f, instack, b);

        for(V v : A.get(s)) { // update C and color for all vertices in the color class of s corresponding to the calculated f
            if(!f.get(colorDegree.get(v)).equals(s)) { // f assigns v a new color
                C.get(s).remove(v); // remove v from s
                C.get(f.get(colorDegree.get(v))).add(v); // add v to the new color class
                color.replace(v, f.get(colorDegree.get(v))); // give v the new color
            }
        }
    }

    /**
     * Helper method for getColoring().
     * adds all colors to refineStack which have to be refined further and constructs the mapping f.
     *
     * @param s the current color
     * @param currentMaxColorDegree maximum color degree of s
     * @param minColorDegree the mapping from color to its minimum color degree
     * @param refineStack the stack refineStack that administrates all colors tobe refined
     * @param numColorDegree mapping from the color degree to the number of vertices with that color degree
     * @param f mapping from color degrees that occur in S to newly introduced colors or to color s
     * @param isSInStack contains whether color s is already in stack
     * @param b index with the maximum number of vertices with the corresponding color degree
     */
    private void addColorsToRefineStackAndComputeF(Integer s, Integer currentMaxColorDegree, int[] minColorDegree, Deque<Integer> refineStack, Map<Integer, Integer> numColorDegree, Map<Integer, Integer> f, boolean isSInStack, int b) {
        for(int i = 0; i <= currentMaxColorDegree; ++i) { // go through all indices (color degrees) of numColorDegree
            if(numColorDegree.get(i) >= 1) { // if there is a vertex with color degree i
                if(i == minColorDegree[s]) { // i is the minimum color degree of s
                    f.put(i, s); // colors with minimum color degree keep color s
                    if(!isSInStack && b != i) { // push s on the stack if it is not in the stack and i is not the index with the maximum number of vertices with the corresponding color degree
                        refineStack.push(f.get(i));
                    }
                } else { // i is not the minimum color degree of s
                    k++; // we add a new color so we have to increase the number of colors
                    f.put(i, k); // we add the new color to f for the index i
                    if(isSInStack || i != b) { // push s on the stack if it is in the stack and i is not the index with the maximum number of vertices with the corresponding color degree
                        refineStack.push(f.get(i));
                    }
                }
            }
        }
    }

    /**
     * checks whether alpha is a valid surjective l-coloring for the given graph
     *
     * @param alpha the surjective l-coloring to be checked
     * @param graph the graph that is colored by alpha
     * @return whether alpha is a valid surjective l-coloring for the given graph
     */
    private boolean isAlphaConsistent(Coloring<V> alpha, Graph<V, E> graph) {

        // check if the coloring is restricted to the graph,
        // i.e. there are exactly as many vertices in the graph as in the coloring
        if(alpha.getColors().size() != graph.vertexSet().size()) {
            return false;
        }

        // check surjectivity, i.e. are the colors in the set {1, ..., maximumColor} used?
        if(alpha.getColorClasses().size() != alpha.getNumberColors()) {
            return false;
        }

        for(V v : graph.vertexSet()) {
            // ensure that the key set of alpha and the vertex set of the graph actually coincide
            if(!alpha.getColors().containsKey(v)) {
                return false;
            }

            // ensure the colors lie in in the set {1, ..., maximumColor}
            Integer currentColor = alpha.getColors().get(v);
            if (currentColor > alpha.getNumberColors() || currentColor < 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * returns a coloring such that all vertices have color 1.
     *
     * @param vertices the vertices that should be colored
     * @return the all-1 coloring
     */
    private static <V> Coloring<V> getDefaultAlpha(Set<V> vertices) {
        Map<V, Integer> alpha = new HashMap<>();
        for(V v : vertices) {
            alpha.put(v, 1);
        }
        return new ColoringImpl<>(alpha, 1);
    }

    /**
     * returns a canonically sorted stack of all colors of alpha. It is important that alpha is consistent
     *
     * @param alpha the surjective l-coloring
     * @return a canonically sorted stack of all colors of alpha
     */
    private Deque<Integer> getSortedStack(Coloring<V> alpha) {
        int numberColors = alpha.getNumberColors();
        // We use an ArrayDeque since it is fast, can be initialized with the correct size
        // and because Stack is kind of deprecated
        Deque<Integer> stack = new ArrayDeque<>(graph.vertexSet().size());
        for(int i = numberColors; i > 0; --i) {
            stack.push(i);
        }
        return stack;
    }

}
