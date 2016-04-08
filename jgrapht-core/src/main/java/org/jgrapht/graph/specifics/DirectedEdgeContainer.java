package org.jgrapht.graph.specifics;

import org.jgrapht.graph.EdgeSetFactory;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

/**
 * A container for vertex edges.
 *
 * <p>In this edge container we use array lists to minimize memory toll.
 * However, for high-degree vertices we replace the entire edge container
 * with a direct access subclass (to be implemented).</p>
 *
 * @author Barak Naveh
 */
public class DirectedEdgeContainer<VV, EE>
    implements Serializable
{
    private static final long serialVersionUID = 7494242245729767106L;
    Set<EE> incoming;
    Set<EE> outgoing;
    private transient Set<EE> unmodifiableIncoming = null;
    private transient Set<EE> unmodifiableOutgoing = null;

    DirectedEdgeContainer(EdgeSetFactory<VV, EE> edgeSetFactory,
                          VV vertex)
    {
        incoming = edgeSetFactory.createEdgeSet(vertex);
        outgoing = edgeSetFactory.createEdgeSet(vertex);
    }

    /**
     * A lazy build of unmodifiable incoming edge set.
     *
     * @return
     */
    public Set<EE> getUnmodifiableIncomingEdges()
    {
        if (unmodifiableIncoming == null) {
            unmodifiableIncoming = Collections.unmodifiableSet(incoming);
        }

        return unmodifiableIncoming;
    }

    /**
     * A lazy build of unmodifiable outgoing edge set.
     *
     * @return
     */
    public Set<EE> getUnmodifiableOutgoingEdges()
    {
        if (unmodifiableOutgoing == null) {
            unmodifiableOutgoing = Collections.unmodifiableSet(outgoing);
        }

        return unmodifiableOutgoing;
    }

    /**
     * .
     *
     * @param e
     */
    public void addIncomingEdge(EE e)
    {
        incoming.add(e);
    }

    /**
     * .
     *
     * @param e
     */
    public void addOutgoingEdge(EE e)
    {
        outgoing.add(e);
    }

    /**
     * .
     *
     * @param e
     */
    public void removeIncomingEdge(EE e)
    {
        incoming.remove(e);
    }

    /**
     * .
     *
     * @param e
     */
    public void removeOutgoingEdge(EE e)
    {
        outgoing.remove(e);
    }
}
