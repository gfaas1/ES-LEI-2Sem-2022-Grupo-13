package org.jgrapht.experimental.subgraphisomorphism;

import java.util.Iterator;

/**
 * @author Fabian Späh
 *
 * @param <E> the type of all mappings to be iterated over.
 */

public interface SubgraphIsomorphismInspector<E> extends Iterator<E>
{

    public boolean isSubgraphIsomorphic();

}
