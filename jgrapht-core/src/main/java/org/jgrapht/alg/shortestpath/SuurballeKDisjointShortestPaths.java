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
 * weight. Only directed simple graphs are allowed.
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
 * @since March 28, 2018
 */
public class SuurballeKDisjointShortestPaths<V, E> implements KShortestPathAlgorithm<V, E> {
    /**
     * Graph on which shortest paths are searched.
     */
    private Graph<V, E> workingGraph;

    private List<List<E>> pathList;

    private Set<E> overlappingEdges;
    
    private HashMap<E, Double> weightMap;
    
    private Graph<V, E> originalGraph;

    /**
     * Creates an object to calculate $k$ disjoint shortest paths between the start vertex and
     * others vertices.
     *
     * @param graph graph on which shortest paths are searched.
     *
     * @throws IllegalArgumentException if the graph is null.
     * @throws IllegalArgumentException if the graph is undirected.
     */
    public SuurballeKDisjointShortestPaths(Graph<V, E> graph) {
                         
        this.originalGraph = graph;
        GraphTests.requireDirected(graph);
        if (!GraphTests.isSimple(graph)) {
            throw new IllegalArgumentException("Graph must be simple");
        }           
        //Assuring all weights modifications are not applied to original graph                
        this.weightMap = new HashMap<>();
        this.workingGraph = new AsWeightedGraph<>(new DefaultDirectedGraph<>(
            this.originalGraph.getVertexSupplier(), this.originalGraph.getEdgeSupplier(), false), weightMap);
        
        Graphs.addGraph(workingGraph, this.originalGraph);
    }
    
    /**
     * Returns the $k$ shortest simple paths in increasing order of weight.
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
    public List<GraphPath<V, E>> getPaths(V startVertex, V endVertex, int k)
    {        
        if (k <= 0) {
            throw new IllegalArgumentException("Number of paths must be positive");
        }
        if (endVertex == null) {
            throw new IllegalArgumentException("endVertex is null");
        }
        if (startVertex == null) {
            throw new IllegalArgumentException("startVertex is null");
        }
        if (endVertex.equals(startVertex)) {
            throw new IllegalArgumentException("The end vertex is the same as the start vertex!");
        }
        if (!workingGraph.vertexSet().contains(startVertex)) {
            throw new IllegalArgumentException("graph must contain the start vertex!");
        }
        if (!workingGraph.vertexSet().contains(endVertex)) {
            throw new IllegalArgumentException("graph must contain the end vertex!");
        }
        
        //original edge weights may have changed due to previous calls
        if (this.originalGraph.getType().isWeighted()) {            
            this.originalGraph.edgeSet().forEach(e -> {
                this.weightMap.put(e, this.originalGraph.getEdgeWeight(e));
            });
        }        

        GraphPath<V, E> currentPath;
        this.pathList = new ArrayList<>();
        DijkstraShortestPath<V, E> dijkstraShortestPath;
        ShortestPathAlgorithm.SingleSourcePaths<V, E> singleSourcePaths = null;

        for (int cPath = 1; cPath <= k; cPath++) {
            if (cPath > 1) {
                prepare(this.pathList.get(cPath - 2), singleSourcePaths);
            }
            dijkstraShortestPath = new DijkstraShortestPath<>(workingGraph);
            singleSourcePaths = dijkstraShortestPath.getPaths(startVertex);
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
     * Prepares the graph for a search of the next path: Replacing the edges of the previous path
     * with reversed edges with negative weight
     * 
     * @param previousPath shortest path found on previous round.
     */
    private void prepare(List<E> previousPath, SingleSourcePaths<V, E> singleSourcePaths)
    {
        
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
     * At the end of the search we have list of intermediate paths - not necessarily disjoint and
     * may contain reversed edges. Here we go over all, removing overlapping edges and merging them
     * to valid paths (from start to end). Finally, we sort them according to their weight.
     * 
     * @param startVertex the start vertex
     * @param endVertex the end vertex
     * 
     * @return sorted list of disjoint paths from start vertex to end vertex.
     */
    private List<GraphPath<V, E>> resolvePaths(V startVertex, V endVertex)
    {
        // first we need to remove overlapping edges.
        findOverlappingEdges();

        // now we might be left with path fragments (not necessarily leading from start to end).
        // We need to merge them to valid paths.
        List<GraphPath<V, E>> paths = buildPaths(startVertex, endVertex);

        // sort paths by overall weight (ascending)
        Collections.sort(paths, Comparator.comparingDouble(GraphPath::getWeight));
        return paths;
    }

    /**
     * After removing overlapping edges, each path is not necessarily connecting start to end
     * vertex. Here we connect the path fragments to valid paths (from start to end).
     * 
     * @param startVertex the start vertex
     * @param endVertex the end vertex
     * 
     * @return list of disjoint paths from start to end.
     */
    private List<GraphPath<V, E>> buildPaths(V startVertex, V endVertex)
    {
        List<List<E>> paths = new ArrayList<>();
        Map<V, ArrayDeque<E>> sourceToEdgeLookup = new HashMap<>();
        Set<E> nonOverlappingEdges = pathList
            .stream().flatMap(List::stream).filter(e -> !this.overlappingEdges.contains(e))
            .collect(Collectors.toSet());

        for (E e : nonOverlappingEdges) {
            V u = workingGraph.getEdgeSource(e);
            if (u.equals(startVertex)) { // start of a new path
                List<E> path = new ArrayList<>();
                path.add(e);
                paths.add(path);
            } else { // some edge which is part of a path
                if (!sourceToEdgeLookup.containsKey(u)) {
                    sourceToEdgeLookup.put(u, new ArrayDeque<>());
                }
                sourceToEdgeLookup.get(u).add(e);
            }
        }

        // Build the paths using the lookup table
        for (List<E> path : paths) {
            V v = workingGraph.getEdgeTarget(path.get(0));
            while (!v.equals(endVertex)) {
                E e = sourceToEdgeLookup.get(v).poll();
                path.add(e);
                v = workingGraph.getEdgeTarget(e);
            }
        }
        
        return paths
            .stream().map(path -> createGraphPath(new ArrayList<>(path), startVertex, endVertex))
            .collect(Collectors.toList());
    }

    /**
     * Iterating over all paths to removes overlapping edges (contained in more than single path).
     * At the end of this method, each path contains unique edges but not necessarily connecting the
     * start to end vertex.
     * 
     */
    private void findOverlappingEdges()
    {
        boolean found;
        this.overlappingEdges = new HashSet<>();
        V sourceE1, targetE1;
        V sourceE2, targetE2;
        // removing overlapping edges
        for (int i = 0; i < pathList.size() - 1; i++) {
            for (E e1 : pathList.get(i)) {
                sourceE1 = workingGraph.getEdgeSource(e1);
                targetE1 = workingGraph.getEdgeTarget(e1);
                found = false;
                for (int j = i + 1; j < pathList.size(); j++) {
                    for (E e2 : pathList.get(j)) {
                        sourceE2 = workingGraph.getEdgeSource(e2);
                        targetE2 = workingGraph.getEdgeTarget(e2);
                        // graph is directed, checking both options.
                        if ((sourceE1.equals(sourceE2) && targetE1.equals(targetE2))
                            || (sourceE1.equals(targetE2) && targetE1.equals(sourceE2)))
                        {
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

    private GraphPath<V, E> createGraphPath(List<E> edgeList, V startVertex, V endVertex)
    {
        double weight = 0;
        for (E edge : edgeList) {
            weight += originalGraph.getEdgeWeight(edge);
        }
        return new GraphWalk<>(originalGraph, startVertex, endVertex, edgeList, weight);
    }
    
}
