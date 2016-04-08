package org.jgrapht.graph.specifics;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.EdgeSetFactory;
import org.jgrapht.util.ArrayUnenforcedSet;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * .
 *
 * @author Barak Naveh
 */
public class UndirectedSpecifics<V,E>
    extends Specifics<V,E>
    implements Serializable
{
    private static final long serialVersionUID = 6494588405178655873L;
    private static final String NOT_IN_UNDIRECTED_GRAPH =
        "no such operation in an undirected graph";

    private AbstractBaseGraph<V,E> abstractBaseGraph;
    private Map<V, UndirectedEdgeContainer<V, E>> vertexMapUndirected;
    protected EdgeSetFactory<V, E> edgeSetFactory;

    public UndirectedSpecifics(AbstractBaseGraph<V,E> abstractBaseGraph)
    {
        this(abstractBaseGraph, new LinkedHashMap<>());
    }

    public UndirectedSpecifics(AbstractBaseGraph<V,E> abstractBaseGraph,
                               Map<V, UndirectedEdgeContainer<V, E>> vertexMap)
    {
        this.abstractBaseGraph = abstractBaseGraph;
        this.vertexMapUndirected = vertexMap;
        this.edgeSetFactory=abstractBaseGraph.getEdgeSetFactory();
    }

    @Override public void addVertex(V v)
    {
        // add with a lazy edge container entry
        vertexMapUndirected.put(v, null);
    }

    @Override public Set<V> getVertexSet()
    {
        return vertexMapUndirected.keySet();
    }

    /**
     * @see Graph#getAllEdges(Object, Object)
     */
    @Override public Set<E> getAllEdges(V sourceVertex, V targetVertex)
    {
        Set<E> edges = null;

        if (abstractBaseGraph.containsVertex(sourceVertex)
            && abstractBaseGraph.containsVertex(targetVertex))
        {
            edges = new ArrayUnenforcedSet<>();

            for (E e : getEdgeContainer(sourceVertex).vertexEdges) {
                boolean equal =
                        isEqualsStraightOrInverted(
                                sourceVertex,
                                targetVertex,
                                e);

                if (equal) {
                    edges.add(e);
                }
            }
        }

        return edges;
    }

    /**
     * @see Graph#getEdge(Object, Object)
     */
    @Override public E getEdge(V sourceVertex, V targetVertex)
    {
        if (abstractBaseGraph.containsVertex(sourceVertex)
            && abstractBaseGraph.containsVertex(targetVertex))
        {

            for (E e : getEdgeContainer(sourceVertex).vertexEdges) {
                boolean equal =
                        isEqualsStraightOrInverted(
                                sourceVertex,
                                targetVertex,
                                e);

                if (equal) {
                    return e;
                }
            }
        }

        return null;
    }

    private boolean isEqualsStraightOrInverted(
        Object sourceVertex,
        Object targetVertex,
        E e)
    {
        boolean equalStraight =
            sourceVertex.equals(abstractBaseGraph.getEdgeSource(e))
            && targetVertex.equals(abstractBaseGraph.getEdgeTarget(e));

        boolean equalInverted =
            sourceVertex.equals(abstractBaseGraph.getEdgeTarget(e))
            && targetVertex.equals(abstractBaseGraph.getEdgeSource(e));
        return equalStraight || equalInverted;
    }

    @Override public void addEdgeToTouchingVertices(E e)
    {
        V source = abstractBaseGraph.getEdgeSource(e);
        V target = abstractBaseGraph.getEdgeTarget(e);

        getEdgeContainer(source).addEdge(e);

        if (!source.equals(target)) {
            getEdgeContainer(target).addEdge(e);
        }
    }

    @Override public int degreeOf(V vertex)
    {
        if (abstractBaseGraph.isAllowingLoops()) { // then we must count, and add loops twice

            int degree = 0;
            Set<E> edges = getEdgeContainer(vertex).vertexEdges;

            for (E e : edges) {
                if (abstractBaseGraph.getEdgeSource(e).equals(abstractBaseGraph.getEdgeTarget(e))) {
                    degree += 2;
                } else {
                    degree += 1;
                }
            }

            return degree;
        } else {
            return getEdgeContainer(vertex).edgeCount();
        }
    }

    /**
     * @see Graph#edgesOf(Object)
     */
    @Override public Set<E> edgesOf(V vertex)
    {
        return getEdgeContainer(vertex).getUnmodifiableVertexEdges();
    }

    /**
     * @see DirectedGraph#inDegreeOf(Object)
     */
    @Override public int inDegreeOf(V vertex)
    {
        throw new UnsupportedOperationException(NOT_IN_UNDIRECTED_GRAPH);
    }

    /**
     * @see DirectedGraph#incomingEdgesOf(Object)
     */
    @Override public Set<E> incomingEdgesOf(V vertex)
    {
        throw new UnsupportedOperationException(NOT_IN_UNDIRECTED_GRAPH);
    }

    /**
     * @see DirectedGraph#outDegreeOf(Object)
     */
    @Override public int outDegreeOf(V vertex)
    {
        throw new UnsupportedOperationException(NOT_IN_UNDIRECTED_GRAPH);
    }

    /**
     * @see DirectedGraph#outgoingEdgesOf(Object)
     */
    @Override public Set<E> outgoingEdgesOf(V vertex)
    {
        throw new UnsupportedOperationException(NOT_IN_UNDIRECTED_GRAPH);
    }

    @Override public void removeEdgeFromTouchingVertices(E e)
    {
        V source = abstractBaseGraph.getEdgeSource(e);
        V target = abstractBaseGraph.getEdgeTarget(e);

        getEdgeContainer(source).removeEdge(e);

        if (!source.equals(target)) {
            getEdgeContainer(target).removeEdge(e);
        }
    }

    /**
     * A lazy build of edge container for specified vertex.
     *
     * @param vertex a vertex in this graph.
     *
     * @return EdgeContainer
     */
    private UndirectedEdgeContainer<V, E> getEdgeContainer(V vertex)
    {
        //abstractBaseGraph.assertVertexExist(vertex); //JK: I don't think we need this here. This should have been verified upstream

        UndirectedEdgeContainer<V, E> ec = vertexMapUndirected.get(vertex);

        if (ec == null) {
            ec = new UndirectedEdgeContainer<>(
                    edgeSetFactory,
                    vertex);
            vertexMapUndirected.put(vertex, ec);
        }

        return ec;
    }
}
