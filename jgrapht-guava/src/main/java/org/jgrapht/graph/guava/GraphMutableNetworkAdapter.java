/*
 * (C) Copyright 2018-2018, by Dimitrios Michail and Contributors.
 *
 * JGraphT : a free Java graph-theory library
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
package org.jgrapht.graph.guava;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.util.TypeUtil;

import com.google.common.graph.Graphs;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;

/**
 * A graph adapter class using Guava's {@link MutableNetwork}.
 * 
 * <p>
 * Note that JGraphT's graph interface is the same as Guava's network interface.
 * 
 * @author Dimitrios Michail
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 */
public class GraphMutableNetworkAdapter<V, E>
    extends BaseNetworkAdapter<V, E, MutableNetwork<V, E>>
    implements Graph<V, E>, Cloneable, Serializable
{
    private static final long serialVersionUID = -4058590598220152069L;

    protected static final String GRAPH_IS_UNWEIGHTED = "Graph is unweighted";

    /**
     * Create a new network adapter.
     * 
     * @param network the mutable network
     * @param ef the edge factory of the new graph
     */
    public GraphMutableNetworkAdapter(MutableNetwork<V, E> network, EdgeFactory<V, E> ef)
    {
        super(network, ef);
    }

    /**
     * Create a new network adapter.
     * 
     * @param network the mutable network
     * @param edgeClass class on which to base factory for edges
     */
    public GraphMutableNetworkAdapter(MutableNetwork<V, E> network, Class<? extends E> edgeClass)
    {
        this(network, new ClassBasedEdgeFactory<>(edgeClass));
    }

    @Override
    public E addEdge(V sourceVertex, V targetVertex)
    {
        assertVertexExist(sourceVertex);
        assertVertexExist(targetVertex);

        if (!network.allowsParallelEdges() && containsEdge(sourceVertex, targetVertex)) {
            return null;
        }

        if (!network.allowsSelfLoops() && sourceVertex.equals(targetVertex)) {
            throw new IllegalArgumentException(LOOPS_NOT_ALLOWED);
        }

        E e = edgeFactory.createEdge(sourceVertex, targetVertex);

        if (network.addEdge(sourceVertex, targetVertex, e)) {
            return e;
        }
        return null;
    }

    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, E e)
    {
        if (e == null) {
            throw new NullPointerException();
        }

        assertVertexExist(sourceVertex);
        assertVertexExist(targetVertex);

        if (!network.allowsParallelEdges() && containsEdge(sourceVertex, targetVertex)) {
            return false;
        }

        if (!network.allowsSelfLoops() && sourceVertex.equals(targetVertex)) {
            throw new IllegalArgumentException(LOOPS_NOT_ALLOWED);
        }

        if (network.addEdge(sourceVertex, targetVertex, e)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean addVertex(V v)
    {
        return network.addNode(v);
    }

    @Override
    public E removeEdge(V sourceVertex, V targetVertex)
    {
        E e = getEdge(sourceVertex, targetVertex);

        if (e != null) {
            network.removeEdge(e);
        }

        return e;
    }

    @Override
    public boolean removeEdge(E e)
    {
        return network.removeEdge(e);
    }

    @Override
    public boolean removeVertex(V v)
    {
        return network.removeNode(v);
    }

    @Override
    public void setEdgeWeight(E e, double weight)
    {
        throw new UnsupportedOperationException(GRAPH_IS_UNWEIGHTED);
    }

    /**
     * Returns a shallow copy of this graph instance. Neither edges nor vertices are cloned.
     *
     * @return a shallow copy of this graph.
     *
     * @throws RuntimeException in case the clone is not supported
     *
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone()
    {
        try {
            GraphMutableNetworkAdapter<V, E> newGraph = TypeUtil.uncheckedCast(super.clone());

            newGraph.edgeFactory = this.edgeFactory;
            newGraph.unmodifiableVertexSet = null;
            newGraph.unmodifiableEdgeSet = null;
            newGraph.network = Graphs.copyOf(this.network);

            return newGraph;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private void writeObject(ObjectOutputStream oos)
        throws IOException
    {
        oos.defaultWriteObject();
        oos.writeObject(getType());
        SerializationUtils.writeGraphToStream(this, oos);
    }

    private void readObject(ObjectInputStream ois)
        throws ClassNotFoundException, IOException
    {
        ois.defaultReadObject();

        GraphType type = (GraphType) ois.readObject();
        if (type.isMixed()) {
            throw new IOException("Mixed graphs not yet supported");
        }

        this.network = (type.isDirected() ? NetworkBuilder.directed() : NetworkBuilder.undirected())
            .allowsParallelEdges(type.isAllowingMultipleEdges())
            .allowsSelfLoops(type.isAllowingSelfLoops()).build();

        SerializationUtils.readGraphFromStream(this, ois);
    }

}
