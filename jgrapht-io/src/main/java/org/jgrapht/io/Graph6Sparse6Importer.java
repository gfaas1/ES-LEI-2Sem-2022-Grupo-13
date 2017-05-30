/*
 * (C) Copyright 2017-2017, by Joris Kinable and Contributors.
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
package org.jgrapht.io;

import org.jgrapht.Graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Importer which reads graphs in graph6 or sparse6 format. A description of the format can be found
 * <a href="https://users.cecs.anu.edu.au/~bdm/data/formats.txt">here</a>. graph6 and sparse6 are formats for storing
 * undirected graphs in a compact manner, using only printable ASCII characters. Files in these formats have text type
 * and contain one line per graph. graph6 is suitable for small graphs, or large dense graphs. sparse6 is more
 * space-efficient for large sparse graphs. Typically, files storing graph6 graphs have the 'g6' extension. Similarly,
 * files storing sparse6 graphs have a 's6' file extension.
 *
 * @author Joris Kinable
 */
public class Graph6Sparse6Importer<V,E> extends AbstractBaseImporter<V,E> implements GraphImporter<V,E>{

    private final double defaultWeight;

    // ~ Constructors ----------------------------------------------------------

    /**
     * Construct a new DIMACSImporter
     *
     * @param vertexProvider provider for the generation of vertices. Must not be null.
     * @param edgeProvider provider for the generation of edges. Must not be null.
     * @param defaultWeight default edge weight
     */
    public Graph6Sparse6Importer(
            VertexProvider<V> vertexProvider, EdgeProvider<V, E> edgeProvider, double defaultWeight)
    {
        super(vertexProvider, edgeProvider);
        this.defaultWeight = defaultWeight;
    }

    /**
     * Construct a new DIMACSImporter
     *
     * @param vertexProvider provider for the generation of vertices. Must not be null.
     * @param edgeProvider provider for the generation of edges. Must not be null.
     */
    public Graph6Sparse6Importer(VertexProvider<V> vertexProvider, EdgeProvider<V, E> edgeProvider)
    {
        this(vertexProvider, edgeProvider, Graph.DEFAULT_EDGE_WEIGHT);
    }

    @Override
    public void importGraph(Graph<V, E> g, Reader input) throws ImportException {
        // convert to buffered
        BufferedReader in;
        if (input instanceof BufferedReader) {
            in = (BufferedReader) input;
        } else {
            in = new BufferedReader(input);
        }

        String g6="";
        try {
            g6=in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(g6.isEmpty())
            throw new ImportException("Failed to read graph");

        if(g6.startsWith(":"))
            this.readSparse6(g, g6.substring(1, g6.length()));
        else if(g6.startsWith(">>sparse6<<:"))
            this.readSparse6(g, g6.substring(12, g6.length()));
        else if(g6.startsWith(">>graph6<<"))
            this.readGraph6(g, g6.substring(10, g6.length()));
        else //assume the graph is in Graph6 format
            this.readGraph6(g, g6);
    }

    private void readGraph6(Graph<V, E> g, String g6) throws ImportException {
        bytes = g6.getBytes();
        validateInput();
        byteIndex = bitIndex = 0;

        //Number of vertices n
        final int n = getNumberOfVertices();
        System.out.println("number of vertices: "+n);

        //Add vertices to the graph
        Map<Integer, V> map = new HashMap<>();
        for (int i = 0; i < n; i++) {
            V vertex = vertexProvider.buildVertex(""+i, new HashMap<String, String>());
            map.put(i, vertex);
            g.addVertex(vertex);
        }

        for (int j = 1; j < n; j++) {
            for (int i = 0; i < j; i++) {
                int bit = getBits(1);
                if (bit == 1) {

                    V from= map.get(i);
                    V to = map.get(j);
                    String label = "e_" + i + "_" + j;
                    E e = edgeProvider.buildEdge(from, to, label, new HashMap<String, String>());
                    g.addEdge(from, to, e);

                    if (g.getType().isWeighted())
                        g.setEdgeWeight(e, defaultWeight);
                }
            }
        }
    }

    private void readSparse6(Graph<V, E> g, String s6) throws ImportException {
        bytes = s6.getBytes();
        validateInput();
        byteIndex = bitIndex = 0;

        //Number of vertices n
        final int n = getNumberOfVertices();
        System.out.println("number of vertices: "+n);

        //Add vertices to the graph
        Map<Integer, V> map = new HashMap<>();
        for (int i = 0; i < n; i++) {
            V vertex = vertexProvider.buildVertex(""+i, new HashMap<String, String>());
            map.put(i, vertex);
            g.addVertex(vertex);
        }

        //number of bits needed to represent n-1 in binary
        int k = (int) Math.ceil(Math.log(n) / Math.log(2));
        //Current vertex
        int v = 0;

        //The remaining bytes encode a sequence b[0] x[0] b[1] x[1] b[2] x[2] ... b[m] x[m]
        //Read blocks. In decoding, an incomplete (b,x) pair at the end is discarded.
        while (hasBits(1 + k)) {
            int b = getBits(1); //Read x[i]
            int x = getBits(k); //Read b[i]

            if(b==1)
                v++;

            if (v >= n) //Ignore the last bit, this is just padding
                break;

            if (x > v)
                v = x;
            else {
                V from= map.get(x);
                V to = map.get(v);
                String label = "e_" + x + "_" + v;
                E e = edgeProvider.buildEdge(from, to, label, new HashMap<String, String>());
                g.addEdge(from, to, e);

                if (g.getType().isWeighted())
                    g.setEdgeWeight(e, defaultWeight);
            }
        }
    }

    private byte[] bytes;
    private int byteIndex, bitIndex=0;

    private void validateInput() throws ImportException {
        for(byte b : bytes)
            if(b < 63 || b > 126)
                throw new ImportException("Graph string seems to be corrupt. Illegal character detected");
    }

    private int getNumberOfVertices() throws ImportException {
        //Determine whether the number of vertices is encoded in 1, 4 or 8 bytes.
        if(bytes.length > 8 && bytes[0] == 126 && bytes[1]==126) {
            byteIndex+=2; //Strip the first 2 garbage bytes
            return getBits(36);
        }else if(bytes.length > 4 && bytes[0] == 126) {
            byteIndex++; //Strip the first garbage byte
            return getBits(18);
        }else
            return getBits(6);
    }


    /**
     * Check whether there is another block of k bits data available
     * @param k bits
     * @return true if a data block of k bits is available
     */
    private boolean hasBits(int k) {
        return (byteIndex + (bitIndex + k - 1) / 6) < bytes.length;
    }


    /**
     * Converts the next k bits of data to an integer
     * @param k number of bits
     * @return the next k bits of data represented by an integer
     */
    private int getBits(int k) throws ImportException {
        int value=0;
        //Read minimum{bits we need, remaining bits in current byte}
        if(bitIndex > 0 || k < 6){
            int x=Math.min(k, 6-bitIndex);
                int mask = (1 << x) - 1;
                int y = (bytes[byteIndex]-63) >> (6 - bitIndex - x);
                y &= mask;
                value = (value << k) + y;
                k -= x;
                bitIndex = bitIndex + x;
                if(bitIndex == 6){
                    byteIndex++;
                    bitIndex=0;
                }
        }

        //Read blocks of 6 bits at a time
        int blocks=k/6;
        for (int j = 0; j < blocks; j++) {
            value = (value << 6) + bytes[byteIndex]-63;
            byteIndex++;
            k -= 6;
        }

        //Read remaining bits
        if(k>0){
            int y=bytes[byteIndex]-63;
            y=y >> (6-k);
            value=(value << k) + y;
            bitIndex=k;
        }

        return value;
    }

    private String getBitString(int i){
        return String.format("%8s", Integer.toBinaryString(i & 0xFF)).replace(' ', '0');
    }
}
