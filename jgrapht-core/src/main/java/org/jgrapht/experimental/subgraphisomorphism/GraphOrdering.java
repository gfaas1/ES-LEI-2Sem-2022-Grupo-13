package org.jgrapht.experimental.subgraphisomorphism;

import org.jgrapht.Graph;
import org.jgrapht.DirectedGraph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;


public class GraphOrdering<V,E> {

	private Graph<V,E> graph;

	private Map<V, Integer> mapVertexToOrder;
	private Map<Integer, V> mapOrderToVertex;
	private int vertexCount;
	
	
	public GraphOrdering(Graph<V,E> graph)
	{
		this.graph            = graph;
		
		mapVertexToOrder = new HashMap<V, Integer>();
		mapOrderToVertex = new HashMap<Integer, V>();
		
		Integer i = 0;
		for(V vertex : graph.vertexSet())	{
			mapVertexToOrder.put(vertex, i);
			mapOrderToVertex.put(i++, vertex);	
		}
		
		vertexCount = i.intValue();
	}
	
	
	public int getVertexCount()	{
		return this.vertexCount;
	}
	
	public int[] getOutEdges(int vertexOrder)	{
		V v            = mapOrderToVertex.get(new Integer(vertexOrder));
		Set<E> edgeSet = new HashSet<E>();
		
		if (graph instanceof DirectedGraph<?,?>)
			edgeSet = ((DirectedGraph<V,E>) graph).outgoingEdgesOf(v);
		else
			edgeSet = graph.edgesOf(v);
		
		int[] vertexArray = new int[edgeSet.size()];
		int i = 0;
		
		for(E edge : edgeSet)	{
			V source = graph.getEdgeSource(edge),
			  target = graph.getEdgeTarget(edge);
			vertexArray[i++] = mapVertexToOrder.get(
					source == v ? target : source);
		}
		
		return vertexArray;
	}
	
	public int[] getInEdges(int vertexOrder)	{
		V v            = mapOrderToVertex.get(new Integer(vertexOrder));
		Set<E> edgeSet = new HashSet<E>();
		
		if (graph instanceof DirectedGraph<?,?>)
			edgeSet = ((DirectedGraph<V,E>) graph).incomingEdgesOf(v);
		else
			edgeSet = graph.edgesOf(v);
		
		int[] vertexArray = new int[edgeSet.size()];
		int i = 0;
		
		for(E edge : edgeSet)	{
			V source = graph.getEdgeSource(edge),
			  target = graph.getEdgeTarget(edge);
			vertexArray[i++] = mapVertexToOrder.get(
					source == v ? target : source);
		}
		
		return vertexArray;
	}
	
	public boolean hasEdge(int v1Order, int v2Order)	{
		V v1 = mapOrderToVertex.get(new Integer(v1Order)),
		  v2 = mapOrderToVertex.get(new Integer(v2Order));
		
		return graph.containsEdge(v1, v2);
	}
	
	public V getVertex(int vertexOrder)	{
		return mapOrderToVertex.get(new Integer(vertexOrder));
	}
	
	public E getEdge(int v1Order, int v2Order)	{
		V v1 = mapOrderToVertex.get(new Integer(v1Order)),
		  v2 = mapOrderToVertex.get(new Integer(v2Order));
		
		// this may be problematic on multigraphs..
		return graph.getEdge(v1, v2);
	}
	
	// experimental methods.. (for use in TestCases/...)
	
	public int getVertexOrder(V v)	{
		return mapVertexToOrder.get(v).intValue();
	}
	
	public int[] getEdgeOrder(E e)	{
		V v1 = graph.getEdgeSource(e),
		  v2 = graph.getEdgeTarget(e);
		
		int[] edge = new int[2];
		edge[0] = mapVertexToOrder.get(v1);
		edge[1] = mapVertexToOrder.get(v2);
		
		return edge;
	}
	
	public Graph<V,E> getGraph()	{
		return graph;
	}
	
}
