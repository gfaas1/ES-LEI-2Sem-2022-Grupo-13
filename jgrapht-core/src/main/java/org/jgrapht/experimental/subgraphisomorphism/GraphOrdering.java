package org.jgrapht.experimental.subgraphisomorphism;

import org.jgrapht.Graph;
import org.jgrapht.DirectedGraph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 
 * This class represents the order on the graph vertices. There are also some
 * helper-functions for receiving outgoing/incoming edges, etc.
 * 
 * @author Fabian Späh
 *
 * @param <V> the type of the vertices
 * @param <E> the type of the edges
 */

public class GraphOrdering<V, E> {

    private Graph<V, E>     graph;

    private Map<V, Integer> mapVertexToOrder;
    private Object[]        mapOrderToVertex;
    private int             vertexCount;

    private int[][]         outgoingEdges;
    private int[][]         incomingEdges;

    /**
     * @param graph the graph to be ordered
     * @param orderByDegree should the vertices be ordered by their degree. This
     *        speeds up the VF2 algorithm.
     */
    public GraphOrdering(Graph<V, E> graph, boolean orderByDegree) {
        this.graph = graph;

        Set<V> vertexSet = graph.vertexSet();
        vertexCount      = vertexSet.size();
        mapVertexToOrder = new HashMap<V, Integer>();
        mapOrderToVertex = new Object[vertexCount];

        outgoingEdges    = new int[vertexCount][];
        incomingEdges    = new int[vertexCount][];

        Integer i = 0;
        for (V vertex : vertexSet) {
            mapVertexToOrder.put(vertex, i);
            mapOrderToVertex[i] = vertex;

            outgoingEdges[i]   = null;
            incomingEdges[i++] = null;
        }

        // TODO: orderByDegree
    }

    /**
     * @param graph the graph to be ordered
     */
    public GraphOrdering(Graph<V, E> graph) {
        this(graph, false);
    }

    /**
     * @return returns the number of vertices in the graph.
     */
    public int getVertexCount() {
        return this.vertexCount;
    }

    /**
     * @param vertexNumber the number which identifies the vertex v in this
     *        order.
     * @return the identifying numbers of all vertices which are connected to v
     *         by an edge outgoing from v.
     */
    public int[] getOutEdges(int vertexNumber) {
        if (outgoingEdges[vertexNumber] != null)
            return outgoingEdges[vertexNumber];

        V v = getVertex(vertexNumber);
        Set<E> edgeSet = null;

        if (graph instanceof DirectedGraph<?, ?>)
            edgeSet = ((DirectedGraph<V, E>) graph).outgoingEdgesOf(v);
        else
            edgeSet = graph.edgesOf(v);

        int[] vertexArray = new int[edgeSet.size()];
        int i = 0;

        for (E edge : edgeSet) {
            V source = graph.getEdgeSource(edge),
              target = graph.getEdgeTarget(edge);
            vertexArray[i++] =
                mapVertexToOrder.get(source == v ? target : source);
        }

        return outgoingEdges[vertexNumber] = vertexArray;
    }

    /**
     * @param vertexNumber the number which identifies the vertex v in this
     *        order.
     * @return the identifying numbers of all vertices which are connected to v
     *         by an edge incoming to v.
     */
    public int[] getInEdges(int vertexNumber) {
        if (incomingEdges[vertexNumber] != null)
            return incomingEdges[vertexNumber];

        V v            = getVertex(vertexNumber);
        Set<E> edgeSet = null;

        if (graph instanceof DirectedGraph<?, ?>)
            edgeSet = ((DirectedGraph<V, E>) graph).incomingEdgesOf(v);
        else
            edgeSet = graph.edgesOf(v);

        int[] vertexArray = new int[edgeSet.size()];
        int i             = 0;

        for (E edge : edgeSet) {
            V source = graph.getEdgeSource(edge),
              target = graph.getEdgeTarget(edge);
            vertexArray[i++] =
                mapVertexToOrder.get(source == v ? target : source);
        }

        return incomingEdges[vertexNumber] = vertexArray;
    }

    /**
     * @param v1Number the number of the first vertex v1
     * @param v2Number the number of the second vertex v2
     * @return exists the edge from v1 to v2
     */
    public boolean hasEdge(int v1Number, int v2Number) {
        V v1 = getVertex(v1Number),
          v2 = getVertex(v2Number);

        return graph.containsEdge(v1, v2);
    }

    /**
     * be careful: there's no check for a invalid vertexNumber
     * 
     * @param vertexNumber the number identifying the vertex v
     * @return v
     */
    @SuppressWarnings("unchecked")
    public V getVertex(int vertexNumber) {
        return (V) mapOrderToVertex[vertexNumber];
    }

    /**
     * this implementation may lead to problems on multigraphs, because only
     * one of possibly more edges is returned.
     * 
     * @param v1Number the number identifying the vertex v1
     * @param v2Number the number identifying the vertex v2
     * @return the edge from v1 to v2
     */
    public E getEdge(int v1Number, int v2Number) {
        V v1 = getVertex(v1Number), v2 = getVertex(v2Number);

        return graph.getEdge(v1, v2);
    }

    // experimental methods.. (for use in TestCases/...)

    public int getVertexNumber(V v) {
        return mapVertexToOrder.get(v).intValue();
    }

    public int[] getEdgeNumbers(E e) {
        V v1 = graph.getEdgeSource(e), v2 = graph.getEdgeTarget(e);

        int[] edge = new int[2];
        edge[0] = mapVertexToOrder.get(v1);
        edge[1] = mapVertexToOrder.get(v2);

        return edge;
    }

    public Graph<V, E> getGraph() {
        return graph;
    }

}
