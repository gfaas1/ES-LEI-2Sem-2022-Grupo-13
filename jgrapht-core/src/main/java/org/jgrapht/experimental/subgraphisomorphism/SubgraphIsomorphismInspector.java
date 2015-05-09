package org.jgrapht.experimental.subgraphisomorphism;

import java.util.Iterator;

public interface SubgraphIsomorphismInspector<E>
	extends Iterator<E>
{
	
	public boolean isSubgraphIsomorphic();
	
}
