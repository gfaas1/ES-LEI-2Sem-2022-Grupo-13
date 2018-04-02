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
package org.jgrapht.graph;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.jgrapht.Graph;

/**
 * Helper class for serialization of graphs.
 * 
 * <p>
 * This is an internal implementation, should be kept package private.
 * 
 * @author Dimitrios Michail
 */
class SerializationUtils
{

    /**
     * Write the vertices and edges of a graph to an object stream.
     * 
     * @param graph the graph
     * @param oos the object stream
     * @throws IOException in case any I/O error occurs
     */
    static <V, E> void writeGraphToStream(Graph<V, E> graph, ObjectOutputStream oos)
        throws IOException
    {
        // write vertices
        int n = graph.vertexSet().size();
        oos.writeInt(n);
        for (V v : graph.vertexSet()) {
            oos.writeObject(v);
        }

        // write edges
        int m = graph.edgeSet().size();
        oos.writeInt(m);
        for (E e : graph.edgeSet()) {
            oos.writeObject(graph.getEdgeSource(e));
            oos.writeObject(graph.getEdgeTarget(e));
            oos.writeObject(e);
        }
    }

    /**
     * Read the vertices and edges of a graph from an object stream.
     * 
     * @param graph the graph
     * @param ois the input stream
     * @throws ClassNotFoundException if the class of some object in the stream cannot be found
     * @throws IOException in case any I/O error occurs
     */
    @SuppressWarnings("unchecked")
    static <V, E> void readGraphFromStream(Graph<V, E> graph, ObjectInputStream ois)
        throws ClassNotFoundException, IOException
    {
        // read vertices
        int n = ois.readInt();
        for (int i = 0; i < n; i++) {
            V v = (V) ois.readObject();
            graph.addVertex(v);
        }

        // read edges
        int m = ois.readInt();
        for (int i = 0; i < m; i++) {
            V s = (V) ois.readObject();
            V t = (V) ois.readObject();
            E e = (E) ois.readObject();
            graph.addEdge(s, t, e);
        }
    }

}
