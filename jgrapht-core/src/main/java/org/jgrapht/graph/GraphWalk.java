/*
 * (C) Copyright 2016-2017, by Joris Kinable and Contributors.
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

import java.util.*;

import org.jgrapht.*;

/**
 * A walk in a graph is an alternating sequence of vertices and edges, starting and ending at a
 * vertex, in which each edge is adjacent in the sequence to its two endpoints. More precisely, a
 * walk is a connected sequence of vertices and edges in a graph
 * {@code v0, e0, v1, e1, v2,....vk-1, ek-1, vk}, such that for {@code 1<=i<=k<}, the edge
 * {@code e_i} has endpoints {@code v_(i-1)} and {@code v_i}. The class makes no assumptions with
 * respect to the shape of the walk: edges may be repeated, and the start and end point of the walk
 * may be different.
 *
 * <p>
 * See <a href="http://mathworld.wolfram.com/Walk.html">http://mathworld.wolfram.com/Walk.html</a>
 * GraphWalk is the default implementation of {@link GraphPath}.
 *
 * <p>
 * Two special cases exist:
 * <ol>
 * <li>A GraphWalk consisting of a single vertex v. In this case, the edge list is empty (the length of the path equals 0),
 * the vertex list contains a single vertex v, and the start and end vertex equal v.</li>
 * <li>An empty Graphwalk. In this case, both the edge and vertex lists are empty, and the start and end vertex are null.</li>
 *</ol>
 *
 * <p>
 * This class is implemented as a light-weight data structure; this class does not verify whether
 * the sequence of edges or the sequence of vertices provided during construction forms an actual
 * walk. It is the responsibility of the invoking class to provide correct input data.
 *
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Joris Kinable
 * 
 */
public class GraphWalk<V, E>
    implements GraphPath<V, E>
{
    protected Graph<V, E> graph;

    protected List<V> vertexList;
    protected List<E> edgeList;

    protected V startVertex;

    protected V endVertex;

    protected double weight;

    /**
     * Creates a walk defined by a sequence of edges. A walk defined by its edges can be specified
     * for non-simple graphs. Edge repetition is permitted, the start and end point points (v0 and
     * vk) can be different.
     *
     * @param graph the graph
     * @param startVertex the starting vertex
     * @param endVertex the last vertex of the path
     * @param edgeList the list of edges of the path
     * @param weight the total weight of the path
     */
    public GraphWalk(Graph<V, E> graph, V startVertex, V endVertex, List<E> edgeList, double weight)
    {
        this(graph, startVertex, endVertex, null, edgeList, weight);
    }

    /**
     * Creates a walk defined by a sequence of vertices. Note that the input graph must be simple,
     * otherwise the vertex sequence does not necessarily define a unique path. Furthermore, all
     * vertices must be pairwise adjacent.
     * 
     * @param graph the graph
     * @param vertexList the list of vertices of the path
     * @param weight the total weight of the path
     */
    public GraphWalk(Graph<V, E> graph, List<V> vertexList, double weight)
    {
        this(
            graph, (vertexList.isEmpty() ? null : vertexList.get(0)),
            (vertexList.isEmpty() ? null : vertexList.get(vertexList.size() - 1)), vertexList, null,
            weight);
    }

    /**
     * Creates a walk defined by both a sequence of edges and a sequence of vertices. Note that both
     * the sequence of edges and the sequence of vertices must describe the same path! This is not
     * verified during the construction of the walk. This constructor makes it possible to store
     * both a vertex and an edge view of the same walk, thereby saving computational overhead when
     * switching from one to the other.
     *
     * @param graph the graph
     * @param startVertex the starting vertex
     * @param endVertex the last vertex of the path
     * @param vertexList the list of vertices of the path
     * @param edgeList the list of edges of the path
     * @param weight the total weight of the path
     */
    public GraphWalk(
        Graph<V, E> graph, V startVertex, V endVertex, List<V> vertexList, List<E> edgeList,
        double weight)
    {
        if (vertexList == null && edgeList == null)
            throw new IllegalArgumentException("Vertex list and edge list cannot both be null!");
        if(startVertex ==null ^ endVertex == null)
            throw new IllegalArgumentException("Either the start and end vertices must both be null, or they must both be not null (one of them is null)");

        this.graph = graph;
        this.startVertex = startVertex;
        this.endVertex = endVertex;
        this.vertexList = vertexList;
        this.edgeList = edgeList;
        this.weight = weight;
    }

    @Override
    public Graph<V, E> getGraph()
    {
        return graph;
    }

    @Override
    public V getStartVertex()
    {
        return startVertex;
    }

    @Override
    public V getEndVertex()
    {
        return endVertex;
    }

    @Override
    public List<E> getEdgeList()
    {
        return (edgeList != null ? edgeList : GraphPath.super.getEdgeList());
    }

    @Override
    public List<V> getVertexList()
    {
        return (vertexList != null ? vertexList : GraphPath.super.getVertexList());
    }

    @Override
    public double getWeight()
    {
        return weight;
    }

    @Override
    public int getLength()
    {
        if (edgeList != null)
            return edgeList.size();
        else if (vertexList != null && !vertexList.isEmpty())
            return vertexList.size() - 1;
        else
            return 0;
    }

    @Override
    public String toString()
    {
        if (vertexList != null)
            return vertexList.toString();
        else
            return edgeList.toString();
    }

    @Override
    public boolean equals(Object o){
        if(o==null || !(o instanceof GraphWalk))
            return false;
        else if(this==o)
            return true;
        GraphWalk<V,E> other=(GraphWalk<V,E>) o;
        return this.startVertex.equals(other.getStartVertex()) && this.endVertex.equals(other.getEndVertex()) &&
            this.getEdgeList().equals(other.getEdgeList()); //Quite expensive if both paths are expressed as vertex lists
    }

    /**
     * Reverses the direction of the path. This method does NOT verify whether the path is feasible wrt the input graph.
     * In case of directed/mixed graphs, the arc directions will be reversed. An error is thrown if reversing an arc (u,v) is impossible
     * because arc (v,u) is not present in the graph.
     */
    @TODO: it would be better if these methods are not void, but return a new GraphWalk (use arraylists)
    @TODO: create an iterator which walks over the path
    public void reverse(){
        V temp=this.startVertex;
        this.startVertex=this.endVertex;
        this.endVertex=temp;

        if (vertexList != null)
            Collections.reverse(vertexList);

        if(graph.getType().isUndirected() && edgeList != null) {
            Collections.reverse(edgeList);
        }else{
            this.weight=0; //Update weights since we will be using different edges
            if(this.edgeList instanceof ArrayList){
                for(int i=edgeList.size()-1; i>=0; i--){
                    E e = edgeList.get(i);
                    V u = graph.getEdgeSource(e);
                    V v = graph.getEdgeTarget(e);
                    edgeList.set(i, graph.getEdge(v,u));
                    this.weight+=graph.getEdgeWeight(e);
                }
            }else {
                ListIterator<E> listIterator = this.edgeList.listIterator(edgeList.size());
                while (listIterator.hasPrevious()) {
                    E e = listIterator.previous();
                    V u = graph.getEdgeSource(e);
                    V v = graph.getEdgeTarget(e);
                    listIterator.remove();
                    listIterator.add(graph.getEdge(v, u));
                    this.weight+=graph.getEdgeWeight(e);
                }
            }
        }
    }

    /**
     * Concatenates the specified GraphPath to the end of this GraphPath. This action can only be performed if the end vertex of this
     * GraphPath is the same as the start vertex of the extending GraphPath
     * @param extension GraphPath used for the concatenation.
     */
    public void concat(GraphPath<V,E> extension){
        if(!this.endVertex.equals(extension.getStartVertex()))
            throw new IllegalArgumentException("This path can only be extended by another path if the end vertex of the orginal path and the start vertex of the extension are equal.");
        if(vertexList != null) {
            List<V> vertexListExtension = extension.getVertexList();
            if(!vertexListExtension.isEmpty())
                vertexList.addAll(vertexListExtension.subList(1, vertexListExtension.size()));
        }
        if(edgeList != null)
            edgeList.addAll(extension.getEdgeList());
        this.endVertex=extension.getEndVertex();
        this.weight+=extension.getWeight();
    }

    /**
     *
     * @param beginIndex
     */
    public void subPath(int beginIndex){
        if(edgeList != null){
            if(edgeList.size() < beginIndex)
                throw new IndexOutOfBoundsException("BeginIndex cannot be larger than the number of edges in the path");
            Iterator<E> it=edgeList.iterator();
            for( int i=0; i<beginIndex; i++){
                E edge=it.next();
                this.weight-= graph.getEdgeWeight(edge);
                it.remove();
            }
            if(vertexList!=null)
                vertexList=vertexList.subList(beginIndex, vertexList.size());
        }else{
            if(vertexList.size() < beginIndex+1)
                throw new IndexOutOfBoundsException("BeginIndex cannot be larger than the number of vertices in the path -1");
            for( int i=0; i<beginIndex; i++){
                E edge=graph.getEdge(vertexList.get(i), vertexList.get(i+1));
                this.weight-= graph.getEdgeWeight(edge);
            }
            vertexList=vertexList.subList(beginIndex, vertexList.size());
        }
    }

    public void subPath(int beginIndex, int endIndex){
        this.subPath(beginIndex);
    }

    /**
     * Returns true if the path is an empty path, that is, a path with startVertex=endVertex=null and with an empty vertex and edge list.
     * @return Returns true if the path is an empty path.
     */
    public boolean isEmpty(){
        return startVertex==null;
    }

    /**
     * Convenience method which verifies whether the given path is feasible wrt the input graph and forms an actual path.
     * @throws InvalidGraphWalkException if the path is invalid
     */
    public void verify() throws InvalidGraphWalkException{

        if(isEmpty()) //Empty path
            return;

        List<V> vertices=this.getVertexList();
        List<E> edges=this.getEdgeList();

        //Verify that the path is an actual path in the graph
        if(edges.size()+1 != vertices.size())
            throw new InvalidGraphWalkException("VertexList and edgeList do not correspond to the same path (cardinality of vertexList +1 must equal the cardinality of the edgeList)");

        //Check start and end vertex
        if(!startVertex.equals(vertices.get(0)))
            throw new InvalidGraphWalkException("The start vertex must be the first vertex in the vertex list");
        if(!endVertex.equals(vertices.get(vertices.size()-1)))
            throw new InvalidGraphWalkException("The end vertex must be the last vertex in the vertex list");

        //All vertices and edges in the path must be contained in the graph
        if(!graph.vertexSet().containsAll(vertices))
            throw new InvalidGraphWalkException("Not all vertices in the path are contained in the graph");

        if(!graph.edgeSet().containsAll(edges))
            throw new InvalidGraphWalkException("Not all edges in the path are contained in the graph");

        //Verify that the sequence of vertices and edges forms an actual path in the graph.
        for(int i=0; i<vertexList.size()-1; i++){
            V u=vertices.get(i);
            V v=vertices.get(i+1);
            E edge=edges.get(i);

            if(graph.getType().isDirected()){ //Directed graph
                if(!graph.getEdgeSource(edge).equals(u) || graph.getEdgeTarget(edge).equals(v))
                    throw new InvalidGraphWalkException("VertexList and edgeList do not form a feasible path");
            }else{ //Undirected or mixed
                if(!Graphs.getOppositeVertex(graph, edge, u).equals(v))
                    throw new InvalidGraphWalkException("VertexList and edgeList do not form a feasible path");
            }
        }
    }

    /**
     * Convenience method which creates an empty walk.
     * @param graph input graph
     * @param <V> vertex type
     * @param <E> edge type
     * @return an empty walk
     */
    public static <V,E> GraphWalk<V,E> emptyWalk(Graph<V,E> graph){
        return new GraphWalk<>(graph, null, null, Collections.emptyList(), Collections.emptyList(), 0.0);
    }

    /**
     * Convenience method which creates a walk consisting of a single vertex.
     * @param graph input graph
     * @param v single vertex
     * @param <V> vertex type
     * @param <E> edge type
     * @return an empty walk
     */
    public static <V,E> GraphWalk<V,E> singletonWalk(Graph<V,E> graph, V v){
        return new GraphWalk<>(graph, v, v, Collections.singletonList(v), Collections.emptyList(), 0.0);
    }


    protected class InvalidGraphWalkException extends Exception{

        public InvalidGraphWalkException(String message){ super(message);}

    }
}

// End GraphPathImpl.java
