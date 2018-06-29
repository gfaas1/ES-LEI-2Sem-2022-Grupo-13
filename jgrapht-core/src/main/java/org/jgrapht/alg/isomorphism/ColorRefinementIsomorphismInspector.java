/*
 * (C) Copyright 2018-2018, by Christoph Grüne and Contributors.
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
package org.jgrapht.alg.isomorphism;

import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;
import org.jgrapht.Graphs;
import org.jgrapht.alg.color.ColorRefinementAlgorithm;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm.Coloring;
import org.jgrapht.traverse.BreadthFirstIterator;

import java.io.Serializable;
import java.util.*;

/**
 * Implementation of the color refinement algorithm isomorphism test using its feature of detecting
 * <a href="http://mathworld.wolfram.com/GraphIsomorphism.html">isomorphism between two graphs</a>
 * as described in
 * C. Berkholz, P. Bonsma, and M. Grohe.  Tight lower and upper bounds for the complexity of canonical
 * colour refinement. Theory of Computing Systems,doi:10.1007/s00224-016-9686-0, 2016 (color refinement)
 * The complexity of this algorithm is O(|V| + |E| log |V|).
 *
 * @param <V> the type of the vertices
 * @param <E> the type of the edges
 *
 * @author Christoph Grüne
 */
public class ColorRefinementIsomorphismInspector<V, E> extends RefinementAbstractIsomorphismInspector<V, E> implements Serializable {

    private static final long serialVersionUID = -4798546523147487442L;

    /**
     * The isomorphism that is calculated by this color refinement isomorphism inspector
     */
    private GraphMapping<V, E> isomorphicGraphMapping;

    /**
     * contains whether the graphs are isomorphic or not.
     * If we cannot decide whether they are isomorphic the value will be not present.
     */
    private Boolean isIsomorphic;
    /**
     * contains whether the two graphs produce a discrete coloring.
     * Then, we can decide whether the graphs are isomorphic.
     */
    private boolean isColoringDiscrete;
    /**
     * contains whether the two graphs are forests. Forests can be identified to be isomorphic or not.
     */
    private boolean isForest;

    /**
     * contains whether the isomorphism test is executed to ensure that every operation is defined all the time
     */
    private boolean isomorphismTestExecuted;

    /**
     * Constructor for a isomorphism inspector based on color refinement. It checks whether <code>graph1</code> and
     * <code>graph2</code> are isomorphic.
     *
     * @param graph1 the first graph
     * @param graph2 the second graph
     */
    public ColorRefinementIsomorphismInspector(Graph<V, E> graph1, Graph<V, E> graph2) {
        super(graph1, graph2);
        this.isomorphicGraphMapping = null;
        this.isColoringDiscrete = false;
        this.isomorphismTestExecuted = false;
        this.isForest = false;
    }

    /**
     * returns the mapping if the calculated coarse coloring returned by color refinement is discrete.
     * Otherwise it returns null.
     *
     * @return the mapping of the graph isomorphism if the coloring is discrete, otherwise null.
     */
    @Override
    public Iterator<GraphMapping<V, E>> getMappings() throws IllegalStateException {
        if(!isomorphismTestExecuted) {
            try {
                isomorphismExists();
            } catch(IsomorphismUndecidableException ignored) {

            }
        }
        if(isIsomorphic != null && isIsomorphic) {
            ArrayList<GraphMapping<V, E>> iteratorList = new ArrayList<>(1);
            iteratorList.add(isomorphicGraphMapping);
            return iteratorList.iterator();
        } else {
            return null;
        }
    }

    /**
     * Check if an isomorphism exists.
     *
     * @return Optional.of(true) if there is an isomorphism, Optional.of(false) if there is no isomorphism,
     * Optional.empty if ut cannot be decided whether there is an isomorphism or not.
     */
    @Override
    public boolean isomorphismExists() throws IsomorphismUndecidableException {
        if(isomorphismTestExecuted) {
            if(isIsomorphic != null) {
                return isIsomorphic;
            } else {
                throw new IsomorphismUndecidableException();
            }
        }

        if(graph1.vertexSet().size() != graph2.vertexSet().size()) {
            return false;
        }

        ColorRefinementAlgorithm<V, E> colorRefinementAlgorithm1 = new ColorRefinementAlgorithm<>(graph1);
        ColorRefinementAlgorithm<V, E> colorRefinementAlgorithm2 = new ColorRefinementAlgorithm<>(graph2);

        Coloring<V> coloring1 = colorRefinementAlgorithm1.getColoring(); // execute color refinement for graph1
        Coloring<V> coloring2 = colorRefinementAlgorithm2.getColoring(); // execute color refinement for graph2

        isIsomorphic = coarseColoringAreEqual(coloring1, coloring2);
        return isIsomorphic;
    }

    /**
     * returns whether the coarse colorings of the two given graphs are discrete if the colorings are the same.
     * Otherwise, we do not computed if the coloring is discrete, hence, this method is undefined.
     *
     * @return if the both colorings are discrete if they are the same on both graphs.
     */
    public boolean isColoringDiscrete() {
        if(!isomorphismTestExecuted) {
            try {
                isomorphismExists();
            } catch(IsomorphismUndecidableException ignored) {

            }
        }
        return isColoringDiscrete;
    }

    /**
     * returns whether the two given graphs are forests if an isomorphism exists and the coloring is not discrete,
     * because if the coloring is discrete, it does not have to be calculated if the graphs are forests.
     * Otherwise, we do not compute if the graphs are forests, hence, this method is undefined.
     *
     * @return if the both colorings are discrete if they are the same on both graphs.
     */
    public boolean isForest() {
        if(!isomorphismTestExecuted) {
            try {
                isomorphismExists();
            } catch (IsomorphismUndecidableException ignored) {

            }
        }
        return isForest;
    }

    /**
     * checks whether two coarse colorings are equal. Furthermore, it sets <code>isColoringDiscrete</code> to true iff the colorings are discrete.
     *
     * @param coloring1 the first coarse coloring
     * @param coloring2 the second coarse coloring
     * @return if the given coarse colorings are equal
     */
    private boolean coarseColoringAreEqual(Coloring<V> coloring1, Coloring<V> coloring2) throws IsomorphismUndecidableException {
        if(coloring1.getNumberColors() != coloring2.getNumberColors()) {
            return false;
        }

        List<Set<V>> colorClasses1 = coloring1.getColorClasses();
        List<Set<V>> colorClasses2 = coloring2.getColorClasses();

        if(colorClasses1.size() != colorClasses2.size()) {
            return false;
        }

        sortColorClasses(colorClasses1, coloring1);
        sortColorClasses(colorClasses2, coloring2);

        Iterator<Set<V>> it1 = colorClasses1.iterator();
        Iterator<Set<V>> it2 = colorClasses2.iterator();

        // check the color classes
        while(it1.hasNext() && it2.hasNext()) {
            Set<V> cur1 = it1.next();
            Set<V> cur2 = it2.next();

            if(cur1.size() != cur2.size()) { // check if the size for the current color class are the same for both graphs.
                return false;
            }
            if(cur1.iterator().hasNext()) { // safety check whether the color class is not empty.
                if(!coloring1.getColors().get(cur1.iterator().next()).equals(coloring2.getColors().get(cur2.iterator().next()))) { // check if the color are not the same (works as colors are integers).
                    return false; // colors are not the same -> graphs are not isomorphic.
                }
            }
        }

        if(!it1.hasNext() && !it2.hasNext()) { // no more color classes for both colorings, that is, the graphs have the same coloring.
            if(coloring1.getColorClasses().size() == graph1.vertexSet().size() && coloring2.getColorClasses().size() == graph2.vertexSet().size()) { // check if the colorings are discrete, that is, the color mapping is injective.
                isColoringDiscrete = true;
                calculateGraphMapping(coloring1, coloring2);
                return true;
            } else if(isForest(graph1) && isForest(graph2)) {
                isForest = true;
                calculateGraphMapping(coloring1, coloring2);
                return true;
            }
            isIsomorphic = null;
            throw new IsomorphismUndecidableException("Color refinement cannot decide whether the two graphs are isomorphic or not.");
        } else { // just a safety check. The program should not go into that branch as we checked that the size of the sets of all color classes is the same. Nevertheless, the graphs are not isomorphic if this case occurs.
            return false;
        }
    }

    /**
     * sorts a list of color classes by the size and the color (integer representation of the color) and
     *
     * @param colorClasses the list of the color classes
     * @param coloring the coloring
     */
    private void sortColorClasses(List<Set<V>> colorClasses, Coloring<V> coloring) {
        colorClasses.sort((o1, o2) -> {
            if(o1.size() == o2.size()) {
                Iterator it1 = o1.iterator();
                Iterator it2 = o2.iterator();
                if(!it1.hasNext() || !it2.hasNext()) {
                    return Integer.compare(o1.size(), o2.size());
                }
                return coloring.getColors().get(it1.next()).compareTo(coloring.getColors().get(it2.next()));
            }
            return Integer.compare(o1.size(), o2.size());
        });
    }

    /**
     * calculates the graph isomorphism as GraphMapping and assigns it to attribute <code>isomorphicGraphMapping</code>
     *
     * @param coloring1 the discrete vertex coloring of graph1
     * @param coloring2 the discrete vertex coloring of graph2
     */
    private void calculateGraphMapping(Coloring<V> coloring1, Coloring<V> coloring2) {
        GraphOrdering<V, E> graphOrdering1 = new GraphOrdering<>(graph1);
        GraphOrdering<V, E> graphOrdering2 = new GraphOrdering<>(graph2);

        int[] core1 = new int[graph1.vertexSet().size()];
        int[] core2 = new int[graph2.vertexSet().size()];

        Iterator<Set<V>> setIterator1 = coloring1.getColorClasses().iterator();
        Iterator<Set<V>> setIterator2 = coloring2.getColorClasses().iterator();

        // we only have to check one iterator as the color classes have the same size
        while(setIterator1.hasNext()) {
            Iterator<V> vertexIterator1 = setIterator1.next().iterator();
            Iterator<V> vertexIterator2 = setIterator2.next().iterator();

            while(vertexIterator1.hasNext()) {
                V v1 = vertexIterator1.next();
                V v2 = vertexIterator2.next();

                int numberOfV1 = graphOrdering1.getVertexNumber(v1);
                int numberOfV2 = graphOrdering2.getVertexNumber(v2);

                core1[numberOfV1] = numberOfV2;
                core2[numberOfV2] = numberOfV1;
            }
        }

        isomorphicGraphMapping = new IsomorphicGraphMapping<>(graphOrdering1, graphOrdering2, core1, core2);
    }

    /**
     * Checks whether the given <code>graph</code> is a forest
     *
     * @param graph the graph to test for being a forest
     * @return true, if <code>graph</code> is a forest; false, otherwise
     */
    private boolean isForest(Graph<V, E> graph) {
        BreadthFirstIterator<V, E> breadthFirstIterator = new BreadthFirstIterator<>(graph);
        Set<V> visitedVertices = new HashSet<>();

        while (breadthFirstIterator.hasNext()) {
            V current = breadthFirstIterator.next();
            if(visitedVertices.contains(current)) {
                return false;
            }
            for(E e : graph.outgoingEdgesOf(current)) {
                V v = Graphs.getOppositeVertex(graph, e, current);
                if(!v.equals(breadthFirstIterator.getParent(current)) && visitedVertices.contains(v)) {
                    return false;
                }
            }
            visitedVertices.add(current);
        }
        return true;
    }
}
