package org.jgrapht.graph.specifics;

import org.jgrapht.graph.EdgeSetFactory;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

/**
 * A container of for vertex edges.
 *
 * <p>In this edge container we use array lists to minimize memory toll.
 * However, for high-degree vertices we replace the entire edge container
 * with a direct access subclass (to be implemented).</p>
 *
 * @author Barak Naveh
 */
public class UndirectedEdgeContainer<VV, EE>
    implements Serializable
{
    private static final long serialVersionUID = -6623207588411170010L;
    Set<EE> vertexEdges;
    private transient Set<EE> unmodifiableVertexEdges = null;

    UndirectedEdgeContainer(
            EdgeSetFactory<VV, EE> edgeSetFactory,
            VV vertex)
    {
        vertexEdges = edgeSetFactory.createEdgeSet(vertex);
    }

    /**
     * A lazy build of unmodifiable list of vertex edges
     *
     * @return
     */
    public Set<EE> getUnmodifiableVertexEdges()
    {
        if (unmodifiableVertexEdges == null) {
            unmodifiableVertexEdges =
                Collections.unmodifiableSet(vertexEdges);
        }

        return unmodifiableVertexEdges;
    }

    /**
     * .
     *
     * @param e
     */
    public void addEdge(EE e)
    {
        vertexEdges.add(e);
    }

    /**
     * .
     *
     * @return
     */
    public int edgeCount()
    {
        return vertexEdges.size();
    }

    /**
     * .
     *
     * @param e
     */
    public void removeEdge(EE e)
    {
        vertexEdges.remove(e);
    }
}
