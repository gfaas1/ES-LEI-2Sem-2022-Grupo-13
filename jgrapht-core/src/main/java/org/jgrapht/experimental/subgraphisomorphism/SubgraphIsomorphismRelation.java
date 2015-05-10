/**
 * 
 */
package org.jgrapht.experimental.subgraphisomorphism;

import java.util.TreeSet;

/**
 * @author fabian
 *
 */
public class SubgraphIsomorphismRelation<V,E> implements GraphSubgraphMapping<V, E> {
	
	GraphOrdering<V,E> g1,
	                   g2;
	
	int[] core1,
	      core2;
	

	public SubgraphIsomorphismRelation(
			GraphOrdering<V,E> g1,
			GraphOrdering<V,E> g2,
			int[] core1,
			int[] core2)
	{
		this.g1 = g1;
		this.g2 = g2;
		this.core1 = core1.clone();
		this.core2 = core2.clone();
	}
	
	
	@Override
	public V getVertexCorrespondence(V v, boolean forward) {
		int vOrdering, uOrdering;
		V u;
		
		if (forward)	{
			vOrdering = g1.getVertexOrder(v);
			uOrdering = core1[vOrdering];
			u = g2.getVertex(uOrdering);
		} else {
			vOrdering = g2.getVertexOrder(v);
			uOrdering = core2[vOrdering];
			u = g1.getVertex(uOrdering);
		}
		
		return u;
	}

	@Override
	public E getEdgeCorrespondence(E e, boolean forward) {
		E e2;
		
		if (forward)	{
			int[] eOrder = g1.getEdgeOrder(e);
			e2 = g2.getEdge(core1[eOrder[0]], core1[eOrder[1]]);
		} else {
			int[] eOrder = g2.getEdgeOrder(e);
			e2 = g1.getEdge(core2[eOrder[0]], core2[eOrder[1]]);
		}
		
		return e2;
	}

	public boolean hasVertexCorrespondence(V v)	{
		return getVertexCorrespondence(v, true) != null;
	}

	public boolean hasEdgeCorrespondence(E e)	{
		return getEdgeCorrespondence(e, true) != null;
	}
	
	
	public String toString()	{
		String str = "[";
		
		TreeSet<V> vertexSet = new TreeSet<V>(g1.getGraph().vertexSet());
		for (V v : vertexSet)
			str += v.toString() + "=" +
					(hasVertexCorrespondence(v) ?
							getVertexCorrespondence(v, true) : "~~") + " ";
		return str.substring(0, str.length()-1) + "]";			
	}

}
