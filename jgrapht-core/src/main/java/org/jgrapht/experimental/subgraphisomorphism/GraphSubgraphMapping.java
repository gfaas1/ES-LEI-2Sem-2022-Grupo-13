
package org.jgrapht.experimental.subgraphisomorphism;

import org.jgrapht.GraphMapping;

/**
 * An mapping between a graph and one of its subgraphs
 * 
 * @author Fabian Späh
 *
 * @param <V> the type of the vertices
 * @param <E> the type of the edges
 */

public interface GraphSubgraphMapping<V, E> extends GraphMapping<V, E>
{

    /**
     * @param v
     * @return is there a corresponding vertex to v in the subgraph
     */
    public boolean hasVertexCorrespondence(V v);

    /**
     * @param e
     * @return is there a corresponding edge to e in the subgraph
     */
    public boolean hasEdgeCorrespondence(E e);

}
