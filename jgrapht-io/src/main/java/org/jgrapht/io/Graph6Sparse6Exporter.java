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
import org.jgrapht.GraphTests;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Exporter which exports graphs in graph6 or sparse6 format. A description of the format can be found
 * <a href="https://users.cecs.anu.edu.au/~bdm/data/formats.txt">here</a>. graph6 and sparse6 are formats for storing
 * undirected graphs in a compact manner, using only printable ASCII characters. Files in these formats have text format
 * and contain one line per graph. graph6 is suitable for small graphs, or large dense graphs. sparse6 is more
 * space-efficient for large sparse graphs. Typically, files storing graph6 graphs have the 'g6' extension. Similarly,
 * files storing sparse6 graphs have a 's6' file extension. sparse6 graphs support loops and multiple edges, graph6
 * graphs do not.
 * <p>
 * In particular, the length of a Graph6 string representation of a graph depends only on the number of vertices.
 * However, this also means that graphs with few edges take as much space as graphs with many edges. On the other
 * hand, Sparse6 is a variable length format which can use dramatically less space for sparse graphs but can have
 * a much larger storage size for dense graphs.
 *
 * @author Joris Kinable
 */
public class Graph6Sparse6Exporter<V,E>
        implements GraphExporter<V, E>{

    public enum Format{GRAPH6, SPARSE6};

    private Format format;

    private ByteArrayOutputStream byteArrayOutputStream;

    /**
     * The default format used by the exporter.
     */
    //TODO: instead of selecting a default format, the exporter should choose the most efficient format to export a particular graph.
    public static final Format DEFAULT_GRAPH6SPARSE6_FORMAT = Format.GRAPH6;

    /**
     * Constructs a new exporter with a given vertex ID provider.
     *
     */
    public Graph6Sparse6Exporter()
    {
        this(DEFAULT_GRAPH6SPARSE6_FORMAT);
    }

    /**
     * Constructs a new exporter with a given vertex ID provider.
     *
     * @param format the format to use
     */
    public Graph6Sparse6Exporter(Format format)
    {
        this.format = Objects.requireNonNull(format, "Format cannot be null");
    }

    @Override
    public void exportGraph(Graph<V, E> g, Writer writer) throws ExportException {
        GraphTests.requireUndirected(g);
        if(format == Format.GRAPH6 && (g.getType().isMultigraph() || g.getType().isPseudograph()))
            System.out.println("WARNING: your input graph supports parallel edges, but your selected output format (graph6) does not! Parallel edges will be ignored!");

        //Map all vertices to a unique integer
        List<V> vertices = new ArrayList<>(g.vertexSet());

        byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            if (format == Format.SPARSE6)
                writeSparse6(g, vertices);
            else
                writeGraph6(g, vertices);
        }catch (IOException e){
            e.printStackTrace();
        }

        String g6="";
        try {
            g6=byteArrayOutputStream.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        PrintWriter out = new PrintWriter(writer);
        out.println(g6);
        out.flush();
    }

    private void writeSparse6(Graph<V, E> g, List<V> vertices) throws IOException {
        int[][] edges=new int[g.edgeSet().size()][2];
        int index=0;
        for(int i=0; i<vertices.size()-1; i++){
            for(int j=i; j<vertices.size(); j++){
                if(g.containsEdge(vertices.get(i), vertices.get(j))) {
                    edges[index][0] = i;
                    edges[index][1] = j;
                    index++;
                }
            }
        }

        //sparse6 format always starts with ":"
        byteArrayOutputStream.write(":".getBytes());
        writeNumberOfVertices(vertices.size());
        //number of bits needed to represent n-1 in binary
        int k = (int) Math.ceil(Math.log(vertices.size()) / Math.log(2));

        int m=0;
        int v=0;
        while(m < edges.length){
            if(edges[m][1] > v+1){
                writeBit(true);
                writeIntInKBits(edges[m][1], k);
                v = edges[m][1];
            }else if(edges[m][1] == v + 1){
                writeBit(true);
                writeIntInKBits(edges[m][0], k);
                v++;
                m++;
            }else{
                writeBit(false);
                writeIntInKBits(edges[m][0], k);
                m++;
            }
        }
        //Pad right hand side with '1's to fill the last byte. This may not be the 'correct' way of padding as
        //described in the sparse6 format descr, but I couldn't make sense of the description. This seems to work fine.
        System.out.println("bitindex before padding: "+bitIndex);
        int padding=6-bitIndex;
        for(int i=0; i<padding; i++) {
            System.out.println("pad. bitindex: "+bitIndex);
            writeBit(true);
        }
        writeByte(); //pash the last byte

    }

    private void writeGraph6(Graph<V, E> g, List<V> vertices) throws IOException {
        writeNumberOfVertices(vertices.size());
        //Write the upper triangle of the adjacency matrix of G as a bit vector x of length n(n-1)/2,
        //using the ordering (0,1),(0,2),(1,2),(0,3),(1,3),(2,3),...,(n-1,n).
        for(int i=0; i<vertices.size()-1; i++)
            for(int j=i+1; j<vertices.size(); j++)
                writeBit(g.containsEdge(vertices.get(i), vertices.get(j)));
        writeByte(); //Finish writing the last byte
    }

    private void writeNumberOfVertices(int n) throws IOException {
        assert n >= 0;
        if(n <= 62)
            byteArrayOutputStream.write(n+63);
        else if(n <= 258047){
            //write number in 4 bytes
            byteArrayOutputStream.write(126);
            byte[] bytes = ByteBuffer.allocate(3).putInt(n).array();
            for(int i=0; i<bytes.length; i++)
                bytes[i]+=+63;
            byteArrayOutputStream.write(bytes);
        }else{
            //write number in 8 bytes
            byteArrayOutputStream.write(126);
            byteArrayOutputStream.write(126);
            byte[] bytes = ByteBuffer.allocate(6).putInt(n).array();
            for(int i=0; i<bytes.length; i++)
                bytes[i]+=+63;
            byteArrayOutputStream.write(bytes);
        }
    }

    private byte currentByte;
    private int bitIndex;

    private void writeIntInKBits(int number, int k){
        for(int i=k-1; i>=0; i--)
            writeBit((number & (1 << i)) != 0);
    }
    private void writeBit(boolean bit){
        if(bitIndex == 6)
            writeByte();
        if(bit)
            currentByte |= 1 << (5-bitIndex);
        bitIndex++;
    }

    private void writeByte(){
        byteArrayOutputStream.write(currentByte+63);
        currentByte=0;
        bitIndex=0;
        System.out.println("push");
    }
}
