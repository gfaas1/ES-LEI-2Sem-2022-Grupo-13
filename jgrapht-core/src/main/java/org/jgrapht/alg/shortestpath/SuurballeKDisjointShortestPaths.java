/*
 * (C) Copyright 2018-2018, by Assaf Mizrachi and Contributors.
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
package org.jgrapht.alg.shortestpath;

import java.util.*;
import java.util.stream.*;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.*;
import org.jgrapht.graph.*;

/**
 * An implementation of Suurballe algorithm for finding K edge-<em>disjoint</em> shortest paths.
 * The algorithm determines the k disjoint shortest simple paths in increasing order of
 * weight.
 *
 * <p>
 * The algorithm is running k sequential Dijkstra iterations to find the shortest path at each step.
 * Hence, yielding a complexity of k*O(Dijkstra).
 * 
 * <p>
 * For further reference see <a href="https://en.wikipedia.org/wiki/Suurballe%27s_algorithm">
 * Wikipedia page </a>
 * <ul>
 * <li>
 * Suurballe, J. W.; Tarjan, R. E. (1984), A quick method for finding shortest pairs of disjoint paths.
 * </ul>
 * 
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 * 
 * @author Assaf Mizrachi
 * @since February 12, 2018
 */
public class SuurballeKDisjointShortestPaths<V, E> implements KShortestPathAlgorithm<V, E> {
    /**
     * Graph on which shortest paths are searched.
     */
    private Graph<V, E> workingGraph;
    
    private final Graph<V, E> graph;

    private List<List<E>> pathList;

    private int nPaths;
    
    private Set<E> overlappingEdges;

    /**
     * Creates an object to calculate k disjoint shortest paths between the start
     * vertex and others vertices.
     *
     * @param graph
     *            graph on which shortest paths are searched.
     * @param nPaths
     *            number of disjoint paths between the start vertex and an end
     *            vertex.
     *
     * @throws IllegalArgumentException
     *             if nPaths is negative or 0.
     * @throws IllegalArgumentException 
     *             if the graph is null.
     * @throws IllegalArgumentException 
     *             if the graph is undirected.
     */
    public SuurballeKDisjointShortestPaths(Graph<V, E> graph, int nPaths) {
                         
        if (nPaths <= 0) {
            throw new IllegalArgumentException("Number of paths must be positive");
        }

        GraphTests.requireDirected(graph);
        if (! GraphTests.isSimple(graph)) {
            throw new IllegalArgumentException("Graph must be simple");
        }
        this.graph = graph;               
        this.nPaths = nPaths;
    }
    
    /**
     * Returns the k shortest simple paths in increasing order of weight.
     *
     * @param startVertex source vertex of the calculated paths.
     * @param endVertex target vertex of the calculated paths.
     *
     * @return list of disjoint paths between the start vertex and the end vertex
     * 
     * @throws IllegalArgumentException if the graph does not contain the startVertex or the
     *         endVertex
     * @throws IllegalArgumentException if the startVertex and the endVertex are the same vertices
     * @throws IllegalArgumentException if the startVertex or the endVertex is null
     */
    @Override
    public List<GraphPath<V, E>> getPaths(V startVertex, V endVertex)
    {
        if (endVertex == null) {
            throw new IllegalArgumentException("endVertex is null");
        }
        if (startVertex == null) {
            throw new IllegalArgumentException("startVertex is null");
        }
        if (endVertex.equals(startVertex)) {
            throw new IllegalArgumentException("The end vertex is the same as the start vertex!");
        }
        if (! graph.vertexSet().contains(startVertex)) {
            throw new IllegalArgumentException("graph must contain the start vertex!");
        }
        if (! graph.vertexSet().contains(endVertex)) {
            throw new IllegalArgumentException("graph must contain the end vertex!");
        }
        
        if (graph.getType().isWeighted()) {
            this.workingGraph = new DefaultDirectedWeightedGraph<>(graph.getEdgeFactory());
        } else {
            this.workingGraph = new AsWeightedGraph<>(graph, new HashMap<>());
        }
        
        Graphs.addAllVertices(workingGraph, graph.vertexSet());
        V source, target;
        E edge;
        for (E e : graph.edgeSet()) {
            source = workingGraph.getEdgeSource(e);
            target = workingGraph.getEdgeTarget(e);
            edge = workingGraph.addEdge(source, target);
            workingGraph.setEdgeWeight(edge, graph.getEdgeWeight(e));
        }

        GraphPath<V, E> currentPath;
        this.pathList = new ArrayList<>();
        DijkstraShortestPath<V, E> dijkstraShortestPaths;
        ShortestPathAlgorithm.SingleSourcePaths<V, E> singleSourcePaths = null;
        
        for (int cPath = 1; cPath <= this.nPaths; cPath++) {
            if (cPath > 1) {
                prepare(this.pathList.get(cPath - 2), singleSourcePaths);
            }                       
            dijkstraShortestPaths = new DijkstraShortestPath<>(workingGraph);
            singleSourcePaths = dijkstraShortestPaths.getPaths(startVertex);
            currentPath = singleSourcePaths.getPath(endVertex);
            if (currentPath != null) {
                pathList.add(currentPath.getEdgeList());
            } else {
                break;
            }
        }

        return pathList.size() > 0 ? resolvePaths(startVertex, endVertex) : Collections.emptyList();
    }
    
    /**
     * Prepares the graph for a search of the next path:
     * Replacing the edges of the previous path with reversed edges
     * with negative weight
     * @param singleSourcePaths 
     * 
     * @param cPath the number of the next path to search 
     */
    private void prepare(List<E> previousPath, SingleSourcePaths<V, E> singleSourcePaths) {
        
        V source, target;
        E reversedEdge;
        
        for (E edge : this.workingGraph.edgeSet()) {
            source = workingGraph.getEdgeSource(edge);
            target = workingGraph.getEdgeTarget(edge);
            double modifiedWeight = this.workingGraph.getEdgeWeight(edge)
                - singleSourcePaths.getWeight(target) + singleSourcePaths.getWeight(source);
            
            this.workingGraph.setEdgeWeight(edge, modifiedWeight);
        }
        
        for (E originalEdge : previousPath) {
            source = workingGraph.getEdgeSource(originalEdge);
            target = workingGraph.getEdgeTarget(originalEdge);
            
            double zeroWeight = workingGraph.getEdgeWeight(originalEdge);
            if (zeroWeight != 0) {
                throw new IllegalStateException("Expected zero weight edge along the path");
            }                       
            workingGraph.removeEdge(originalEdge); 
            reversedEdge = workingGraph.getEdge(target, source);
            if (reversedEdge != null) {
                workingGraph.removeEdge(reversedEdge);                
            }
            reversedEdge = workingGraph.addEdge(target, source);
            workingGraph.setEdgeWeight(reversedEdge, zeroWeight);
        }
    }
    
    /**
     * At the end of the search we have list of intermediate paths - not necessarily
     * disjoint and may contain reversed edges. Here we go over all, removing overlapping
     * edges and merging them to valid paths (from start to end). Finally, we sort
     * them according to their weight.
     * 
     * @param endVertex the end vertex
     * 
     * @return sorted list of disjoint paths from start vertex to end vertex.
     */
    private List<GraphPath<V, E>> resolvePaths(V startVertex, V endVertex) {
        //first we need to remove overlapping edges.        
        findOverlappingEdges();
        
        //now we might be left with path fragments (not necessarily leading from start to end).
        //We need to merge them to valid paths.
        List<GraphPath<V, E>> paths = mergePaths(startVertex, endVertex);
        
        //sort paths by overall weight (ascending)
        Collections.sort(paths, (o1, o2) -> Double.compare(o1.getWeight(), o2.getWeight()));        
        return paths;
    }
    
    /**
     * After removing overlapping edges, each path is not necessarily connecting
     * start to end vertex. Here we connect the path fragments to valid paths
     * (from start to end).
     * 
     * @param endVertex the end vertex
     * 
     * @return list of disjoint paths from start to end.
     */    
    private List<GraphPath<V, E>> mergePaths(V startVertex, V endVertex)
    {
        List<ArrayDeque<E>> pathsQueueList = new ArrayList<ArrayDeque<E>>(this.pathList.size());
        this.pathList.forEach(path -> pathsQueueList.add(new ArrayDeque<>()));
        ArrayDeque<E> allEdges = new ArrayDeque<>(flatPathListOrdered());
        while (! allEdges.isEmpty()) {
            E edge = allEdges.pop();
            if (this.overlappingEdges.contains(edge)) {
                continue;
            }
            for (ArrayDeque<E> path : pathsQueueList) {
                if (path.isEmpty()) {
                    path.add(edge);
                    break;
                }

                if (this.workingGraph
                    .getEdgeSource(edge).equals(this.workingGraph.getEdgeTarget(path.peekLast())))
                {
                    path.add(edge);
                    break;
                }
            }
        }

        return pathsQueueList
            .stream()
            .map(pathQueue -> createGraphPath(new ArrayList<>(pathQueue), startVertex, endVertex))
            .collect(Collectors.toList());
    }
    
    /**
     * Flattens pathList to list of edges ordered (ascending) according to their
     * distance (i.e. number of hops) from the source.
     * 
     * @return list of all paths edges.
     */
    private List<E> flatPathListOrdered() {
        List<E> flatListOrdered = new ArrayList<>();
        int maxSize = 0;
        for (List<E> list : this.pathList) {
            if (list.size() > maxSize) {
                maxSize = list.size();
            }
        }
        
        for (int i = 0; i < maxSize; i++) {
            for (List<E> list : this.pathList) {
                if (i < list.size()) {
                    flatListOrdered.add(list.get(i));
                }
            }
        }
        return flatListOrdered;
    }
    
    /**
     * Iterating over all paths to removes overlapping edges (contained
     * in more than single path). At the end of this method, each path
     * contains unique edges but not necessarily connecting the start
     * to end vertex.
     * 
     */
    private void findOverlappingEdges() {
        Iterator<E> path1Iter, path2Iter;
        E e1, e2;
        boolean found;
        this.overlappingEdges = new HashSet<>();
        //removing overlapping edges
        for (int i = 0; i < pathList.size(); i++) {
            List<E> path1 = pathList.get(i);
            path1Iter = path1.iterator();
            while (path1Iter.hasNext()) {
                e1 = path1Iter.next();
                found = false;
                for (int j = i + 1; j < pathList.size(); j++) {
                    List<E> path2 = pathList.get(j);
                    path2Iter = path2.iterator();
                    while (path2Iter.hasNext()) {
                        e2 = path2Iter.next();
                        //graph is directed, checking both options.
                        if ((workingGraph.getEdgeSource(e1).equals(workingGraph.getEdgeSource(e2)) &&
                            workingGraph.getEdgeTarget(e1).equals(workingGraph.getEdgeTarget(e2))) ||
                                
                                (workingGraph.getEdgeSource(e1).equals(workingGraph.getEdgeTarget(e2)) &&
                                    workingGraph.getEdgeTarget(e1).equals(workingGraph.getEdgeSource(e2)))) {
                            found = true;
                            this.overlappingEdges.add(e2);
                        }
                    }
                }
                if (found) {
                    this.overlappingEdges.add(e1);
                }
            }
        }
        
    }
    
    private GraphPath<V, E> createGraphPath(List<E> edgeList, V startVertex, V endVertex) {
        double weight = 0;
        List<E> originalGraphEdgeList = new ArrayList<>(edgeList.size());
        for (E edge : edgeList) {
            E originalGraphEdge =  this.graph.getEdge(this.graph.getEdgeSource(edge), this.graph.getEdgeTarget(edge));
            originalGraphEdgeList.add(originalGraphEdge);
            weight += this.graph.getEdgeWeight(originalGraphEdge);
        }
        return new GraphWalk<>(this.graph, startVertex, endVertex, originalGraphEdgeList, weight);
    }
    
}
