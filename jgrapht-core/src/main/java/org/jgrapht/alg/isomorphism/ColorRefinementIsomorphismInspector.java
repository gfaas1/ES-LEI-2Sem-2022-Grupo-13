package org.jgrapht.alg.isomorphism;

import org.jgrapht.GraphMapping;

import java.util.Iterator;

/**
 * @param <V> the type of the vertices
 * @param <E> the type of the edges
 */
public class ColorRefinementIsomorphismInspector<V, E> implements IsomorphismInspector<V, E> {

    /**
     *
     *
     * @return
     * @throws IllegalStateException
     */
    @Override
    public Iterator<GraphMapping<V, E>> getMappings() throws IllegalStateException {
        throw new IllegalStateException("ColorRefinement does not calculate a mapping. It only can decide whether the graphs are not isomorphic.");
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isomorphismExists() {
        return false;
    }
}
