/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
/* -----------------
 * MaximumFlowAlgorithmPerformanceTest.java
 * -----------------
 * (C) Copyright 2015-2015, by Barak Naveh and Contributors.
 *
 * Original Author:  Barak Naveh
 * Contributor(s):
 *
 * $Id$
 *
 * Changes
 * -------
 */
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
            unmodifiableVertexEdges =Collections.unmodifiableSet(vertexEdges);
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
