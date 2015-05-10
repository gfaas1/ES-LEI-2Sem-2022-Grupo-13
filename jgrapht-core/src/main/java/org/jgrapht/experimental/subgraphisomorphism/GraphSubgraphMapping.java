/**
 * 
 */
package org.jgrapht.experimental.subgraphisomorphism;

import org.jgrapht.GraphMapping;

/**
 * @author fabian
 *
 */
public interface GraphSubgraphMapping<V,E> extends GraphMapping<V, E> {

	public boolean hasVertexCorrespondence(V v);
	
	public boolean hasEdgeCorrespondence(E e);
	
}
