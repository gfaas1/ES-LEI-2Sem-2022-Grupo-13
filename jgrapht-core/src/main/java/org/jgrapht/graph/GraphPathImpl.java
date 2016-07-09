/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2009, by Barak Naveh and Contributors.
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
/* ----------------
 * GraphPathImpl.java
 * ----------------
 * (C) Copyright 2009-2009, by John V. Sichi and Contributors.
 *
 * Original Author:  John V. Sichi
 * Contributor(s):   Joris Kinable
 *
 * $Id$
 *
 * Changes
 * -------
 * 03-Jul-2009 : Initial revision (JVS);
 *
 */
package org.jgrapht.graph;

import java.util.*;

import org.jgrapht.*;


/**
 * GraphPathImpl is a default implementation of {@link GraphPath}.
 *
 * @author John Sichi
 * @version $Id$
 */
public class GraphPathImpl<V, E>
    implements GraphPath<V, E>
{
    private Graph<V, E> graph;

    private List<V> vertexList;
    private List<E> edgeList;

    private V startVertex;

    private V endVertex;

    private double weight;

    public GraphPathImpl(
        Graph<V, E> graph,
        V startVertex,
        V endVertex,
        List<E> edgeList,
        double weight)
    {
        this(graph, startVertex, endVertex, null, edgeList, weight);
    }

    public GraphPathImpl(
            Graph<V, E> graph,
            List<V> vertexList,
            double weight)
    {
        this(graph,
                (vertexList.isEmpty() ? null : vertexList.get(0)),
                (vertexList.isEmpty() ? null : vertexList.get(vertexList.size()-1)),
                vertexList,
                null,
                weight);
    }

    public GraphPathImpl(
            Graph<V, E> graph,
            V startVertex,
            V endVertex,
            List<V> vertexList,
            List<E> edgeList,
            double weight)
    {
        if(vertexList == null && edgeList == null)
            throw new IllegalArgumentException("Vertex list and edge list cannot both be null!");
        
        this.graph = graph;
        this.startVertex = startVertex;
        this.endVertex = endVertex;
        this.vertexList=vertexList;
        this.edgeList = edgeList;
        this.weight = weight;
    }

    // implement GraphPath
    @Override public Graph<V, E> getGraph()
    {
        return graph;
    }

    // implement GraphPath
    @Override public V getStartVertex()
    {
        return startVertex;
    }

    // implement GraphPath
    @Override public V getEndVertex()
    {
        return endVertex;
    }

    // implement GraphPath
    @Override public List<E> getEdgeList()
    {
        return (edgeList != null ? edgeList : GraphPath.super.getEdgeList());
    }

    // implement GraphPath
    @Override public List<V> getVertexList()
    {
        return (vertexList != null ? vertexList : GraphPath.super.getVertexList());
    }

    // implement GraphPath
    @Override public double getWeight()
    {
        return weight;
    }

    // override Object
    @Override public String toString()
    {
        return edgeList.toString();
    }
}

// End GraphPathImpl.java
