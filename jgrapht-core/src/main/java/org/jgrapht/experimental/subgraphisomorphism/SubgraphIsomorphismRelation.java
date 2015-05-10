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
	
	private static int NULL_NODE;
	
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
		NULL_NODE = Integer.MAX_VALUE;
		
		this.g1 = g1;
		this.g2 = g2;
		this.core1 = core1.clone();
		this.core2 = core2.clone();
	}
	
	
	@Override
	public V getVertexCorrespondence(V v, boolean forward) {
		GraphOrdering<V,E> firstGraph, secondGraph;
		int[] core;
		
		if (forward)	{
			firstGraph  = g1;
			secondGraph = g2;
			core        = core1;
		} else {
			firstGraph  = g2;
			secondGraph = g1;
			core        = core2;
		}
		
		int vOrdering = firstGraph.getVertexOrder(v),
			uOrdering = core[vOrdering];
		
		if (uOrdering == NULL_NODE)
			return null;
		
		return secondGraph.getVertex(uOrdering);
	}

	@Override
	public E getEdgeCorrespondence(E e, boolean forward) {
		GraphOrdering<V,E> firstGraph, secondGraph;
		int[] core;
		
		if (forward)	{
			firstGraph  = g1;
			secondGraph = g2;
			core        = core1;
		} else {
			firstGraph  = g2;
			secondGraph = g1;
			core        = core2;
		}
		
		int[] eOrder = firstGraph.getEdgeOrder(e);
		if (core[eOrder[0]] == NULL_NODE || core[eOrder[1]] == NULL_NODE)
			return null;
		
		return secondGraph.getEdge(core[eOrder[0]], core[eOrder[1]]);
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
