package org.jgrapht.graph.specifics;

import java.io.Serializable;
import java.util.Set;

/**
 * .
 *
 * @author Barak Naveh
 */
public abstract class Specifics<V, E>
    implements Serializable
{
    private static final long serialVersionUID = 785196247314761183L;

    public abstract void addVertex(V vertex);

    public abstract Set<V> getVertexSet();

    /**
     * .
     *
     * @param sourceVertex
     * @param targetVertex
     *
     * @return
     */
    public abstract Set<E> getAllEdges(V sourceVertex,
        V targetVertex);

    /**
     * .
     *
     * @param sourceVertex
     * @param targetVertex
     *
     * @return
     */
    public abstract E getEdge(V sourceVertex, V targetVertex);

    /**
     * Adds the specified edge to the edge containers of its source and
     * target vertices.
     *
     * @param e
     */
    public abstract void addEdgeToTouchingVertices(E e);

    /**
     * .
     *
     * @param vertex
     *
     * @return
     */
    public abstract int degreeOf(V vertex);

    /**
     * .
     *
     * @param vertex
     *
     * @return
     */
    public abstract Set<E> edgesOf(V vertex);

    /**
     * .
     *
     * @param vertex
     *
     * @return
     */
    public abstract int inDegreeOf(V vertex);

    /**
     * .
     *
     * @param vertex
     *
     * @return
     */
    public abstract Set<E> incomingEdgesOf(V vertex);

    /**
     * .
     *
     * @param vertex
     *
     * @return
     */
    public abstract int outDegreeOf(V vertex);

    /**
     * .
     *
     * @param vertex
     *
     * @return
     */
    public abstract Set<E> outgoingEdgesOf(V vertex);

    /**
     * Removes the specified edge from the edge containers of its source and
     * target vertices.
     *
     * @param e
     */
    public abstract void removeEdgeFromTouchingVertices(E e);
}
