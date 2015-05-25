package org.jgrapht.experimental.subgraphisomorphism;

import java.util.Iterator;

/**
 * @author Fabian Späh
 *
 * @param <E> the type of all mappings to be iterated over.
 */

public interface SubgraphIsomorphismInspector<T> extends Iterator<T>
{

    public boolean isSubgraphIsomorphic();

}
