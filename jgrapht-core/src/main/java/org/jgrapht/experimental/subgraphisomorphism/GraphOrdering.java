package org.jgrapht.experimental.subgraphisomorphism;

import org.jgrapht.Graph;
import org.jgrapht.DirectedGraph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class GraphOrdering<V,E> {

	private Graph<V,E> graph;

	private Map<V, Integer> mapVertexToOrder;
	private Object[]        mapOrderToVertex;
	private int             vertexCount;
	
	private int[][]         outgoingEdges;
	private int[][]         incomingEdges;
	
	
	public GraphOrdering(Graph<V,E> graph, boolean orderByDegree)
	{
		this.graph            = graph;
		
		Set<V> vertexSet = graph.vertexSet();
		vertexCount = vertexSet.size();
		mapVertexToOrder = new HashMap<V, Integer>();
		mapOrderToVertex = new Object[vertexCount];
		
		outgoingEdges    = new int[vertexCount][];
		incomingEdges    = new int[vertexCount][];
		
		Integer i = 0;
		for(V vertex : vertexSet)	{
			mapVertexToOrder.put(vertex, i);
			mapOrderToVertex[i] = vertex;
			
			outgoingEdges[i]   = null;
			incomingEdges[i++] = null;
		}
		
		// todo: orderByDegree
	}
	
	public GraphOrdering(Graph<V,E> graph)	{
		this(graph, false);
	}
	
	
	public int getVertexCount()	{
		return this.vertexCount;
	}
	
	public int[] getOutEdges(int vertexOrder)	{
		if (outgoingEdges[vertexOrder] != null)
			return outgoingEdges[vertexOrder];
		
		V v            = getVertex(vertexOrder);
		Set<E> edgeSet = null;
		
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
		
		return outgoingEdges[vertexOrder] = vertexArray;
	}
	
	public int[] getInEdges(int vertexOrder)	{
		if (incomingEdges[vertexOrder] != null)
			return incomingEdges[vertexOrder];
		
		V v            = getVertex(vertexOrder);
		Set<E> edgeSet = null;
		
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
		
		return incomingEdges[vertexOrder] = vertexArray;
	}
	
	public boolean hasEdge(int v1Order, int v2Order)	{
		V v1 = getVertex(v1Order),
		  v2 = getVertex(v2Order);
		
		return graph.containsEdge(v1, v2);
	}
	
	// be careful: there's no check for NULL_NODE
	@SuppressWarnings("unchecked")
	public V getVertex(int vertexOrder)	{
		return (V) mapOrderToVertex[vertexOrder];
	}
	
	public E getEdge(int v1Order, int v2Order)	{
		V v1 = getVertex(v1Order),
		  v2 = getVertex(v2Order);
		
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
