package org.jgrapht.alg.isomorphism;

import org.jgrapht.GraphMapping;

import java.util.Iterator;

/**
 *
 * @param <V> the type of the vertices
 * @param <E> the type of the edges
 */
public class IndividualizationRefinementIsomorphismInspector<V, E> implements IsomorphismInspector<V, E> {

    /**
     *
     * @return
     */
    @Override
    public Iterator<GraphMapping<V, E>> getMappings() {
        return null;
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
