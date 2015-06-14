package org.jgrapht.alg.isomorphism;

import java.util.Iterator;


/**
 * @param <V> the type of the vertices
 * @param <E> the type of the edges
 */
public interface IsomorphismInspector<V,E>
{

    public Iterator<IsomorphicGraphMapping<V,E>> getMappings();

    public boolean isomorphismExists();

}
