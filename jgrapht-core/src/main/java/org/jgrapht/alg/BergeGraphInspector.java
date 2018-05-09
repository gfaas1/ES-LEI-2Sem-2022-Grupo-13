/*
 * (C) Copyright 2016-2018, by Philipp S. Kaesgen and Contributors.
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
package org.jgrapht.alg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.GraphTests;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.generate.ComplementGraphGenerator;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.graph.Multigraph;
import org.jgrapht.graph.SimpleGraph;



/**
 * <p>Tests whether a graph is <a href="http://mathworld.wolfram.com/PerfectGraph.html">perfect</a>. 
 * A perfect graph, also known as a Berge graph, is a graph $G$ such that for every induced subgraph of $G$, 
 * the clique number $\chi(G)$ equals the chromatic number $\omega(G)$, i.e.,  $\omega(G)=\chi(G)$. Another 
 * characterization of perfect graphs is given by the Strong Perfect Graph Theorem 
 * [M. Chudnovsky, N. Robertson, P. Seymour, R. Thomas. The strong perfect graph theorem Annals of Mathematics, vol 164(1): pp. 51–230, 2006]: 
 * A graph $G$ is perfect if neither $G$ nor its complement $\overline{G}$ have an odd hole. 
 * A hole in $G$ is an induced subgraph of $G$ that is a cycle of length at least four, and it is odd or even if it 
 * has odd (or even, respectively) length.
 * <p>
 * Some special <a href="http://graphclasses.org/classes/gc_56.html">classes</a> of graphs are are known to be perfect, 
 * e.g. Bipartite graphs and Chordal graphs. Testing whether a graph is resp. Bipartite or Chordal can be done efficiently 
 * using {@link GraphTests#isBipartite} or {@link org.jgrapht.alg.cycle.ChordalityInspector}.
 * <p>
 * The implementation of this class is based on the paper: M. Chudnovsky, G. Cornuejols, X. Liu, P. Seymour, and K. Vuskovic. Recognizing Berge Graphs. Combinatorica 25(2): 143--186, 2003.
 * <p>Special Thanks to Maria Chudnovsky for her kind help.
 * 
 * <p>The runtime complexity of this implementation is $O(|V|^9|)$. This implementation is far more efficient than 
 * simplistically testing whether graph  $G$ or its complement $\overline{G}$ have an odd cycle, because testing 
 * whether one graph can be found as an induced subgraph of another is 
 * <a href="https://en.wikipedia.org/wiki/Induced_subgraph_isomorphism_problem">known</a> to be NP-hard.
 * 
 * @author Philipp S. Kaesgen (pkaesgen@freenet.de)
 * @since 2018
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 */
public class BergeGraphInspector<V,E>{
    /**
     * Checks whether two paths which both intersect in the vertex m have another common vertex
     * @param g A Graph
     * @param s1 A Vertex
     * @param s2 A Vertex
     * @param m A Vertex
     * @return whether there is an edge except for m
     */
    protected boolean haveNoEdgeDisregardingM(Graph<V,E> g,GraphPath<V,E> s1, GraphPath<V,E> s2,V m){
        for (V v1 : s1.getVertexList())
            if (v1 != m)
                for (V v2 : s2.getVertexList())
                    if (v2 != m&&!g.containsEdge(v1,v2))
                        return true;
                        
        return false;
    }
    
    
    /**
     * Lists the vertices which are covered by two paths
     * @param g A Graph
     * @param p1 A Path in g
     * @param p2 A Path in g
     * @return Set of vertices covered by both p1 and p2 
     */
    protected Set<V> intersectGraphPaths(Graph<V,E> g,GraphPath<V,E> p1, GraphPath<V,E> p2){
        Set<V> res = new HashSet<V>();
        res.addAll(p1.getVertexList());
        res.retainAll(p2.getVertexList());
        return res;
    }
    
    /**
     * Assembles a GraphPath of the Paths S and T. Required for the Pyramid Checker
     * @param g A Graph
     * @param S A Path in g
     * @param T A Path in g
     * @param M A set of vertices in g
     * @param m A vertex
     * @param b1 A base vertex
     * @param b2 A base vertex
     * @param b3 A base vertex
     * @param s1 A vertex
     * @param s2 A vertex
     * @param s3 A vertex
     * @return The conjunct path of S and T
     */
    protected GraphPath<V,E> P(Graph<V,E> g,GraphPath<V,E> S, GraphPath<V,E> T, Set<V> M, V m, V b1, V b2, V b3, V s1, V s2, V s3){
        if (S!=null&&T!=null){
            if (s1==b1){
                if (b1==m){
                    List<E> edgeList = new LinkedList<E>();
                    edgeList.add(g.getEdge(s1, b1));
                    return new GraphWalk<V,E>(g,s1,b1,edgeList,g.getEdgeWeight(edgeList.get(0)));
                }
                else{
                    return null;
                }
            }
            else{
                if (b1!=m){
                    M.add(b1);
                    Set<V> intersection=intersectGraphPaths(g,S,T);
                    M.remove(b1);
                    if (!(g.containsEdge(m, b2)||g.containsEdge(m,b3)||g.containsEdge(m,s2)||g.containsEdge(m,s3))&&
                            !S.getEdgeList().isEmpty()&&!T.getEdgeList().isEmpty()&&
                            intersection.size()==1&&intersection.contains(m)
                            &&haveNoEdgeDisregardingM(g,S, T, m)
                            ){
                        List<E> edgeList =new LinkedList<E>();
                        edgeList.addAll(S.getEdgeList());
                        edgeList.addAll(T.getEdgeList());
                        double weight = 0;
                        for (E e : edgeList) weight+=g.getEdgeWeight(e);
                        return new GraphWalk<V,E>(g, b1, s1, edgeList, weight);
                    }
                    else{
                        return null;
                    }
                }
                else{
                    return null;
                }
            }
        }
        else{
            return null;
        }
    }
    
    
    
    /**
     * Checks whether a graph contains a pyramid. Running time: O(|V(g)|^9)
     * @param g Graph
     * @return Either it finds a pyramid (and hence an odd hole) in g, or it determines that g contains no pyramid
     */
    protected boolean containsPyramid(Graph<V,E> g){
        /*
         * A pyramid looks like this:
         * 
         *    b2-(T2)-m2-(S2)-s2
         *   / |                \
         * b1---(T1)-m1-(S1)-s1--a
         *   \ |                /
         *    b3-(T3)-m3-(S3)-s3
         *    
         *    Note that b1, b2, and b3 are connected and all names in parentheses are paths
         * 
         */
        Set<Set<V>> visitedTriangles = new HashSet<Set<V>>();
        for (E e1 : g.edgeSet()){
            V b1= g.getEdgeSource(e1), b2= g.getEdgeTarget(e1);
            if (b1==b2) continue;
            for (E e2 : g.edgesOf(b1)){
                V b3 = g.getEdgeSource(e2);
                if (b3==b1)
                    b3 = g.getEdgeTarget(e2);
                if (b3==b1||b3==b2||!g.containsEdge(b2,b3)) continue;
                    
                //Triangle detected for the pyramid base
                Set<V> triangles = new HashSet<V>();
                triangles.add(b1);
                triangles.add(b2);
                triangles.add(b3);
                if (visitedTriangles.contains(triangles)){
                    continue;
                }
                visitedTriangles.add(triangles);
                
                for (V aCandidate : g.vertexSet()){
                    if (
                            aCandidate==b1||aCandidate==b2||aCandidate==b3||
                            //a is adjacent to at most one of b1,b2,b3
                            g.containsEdge(aCandidate,b1)&&g.containsEdge(aCandidate,b2)||
                            g.containsEdge(aCandidate,b2)&&g.containsEdge(aCandidate,b3)||
                            g.containsEdge(aCandidate,b1)&&g.containsEdge(aCandidate,b3)){
                        continue;
                    }
                    
                    //aCandidate could now be the top of the pyramid
                    for (E e4 : g.edgesOf(aCandidate)){
                        V s1 = g.getEdgeSource(e4);
                        if (s1==aCandidate)
                            s1 = g.getEdgeTarget(e4);
                        if (s1==b2||s1==b3||s1!=b1&&(g.containsEdge(s1,b2)||g.containsEdge(s1,b3))){
                            continue;
                        }
                        
                        for (E e5 : g.edgesOf(aCandidate)){
                            V s2 = g.getEdgeSource(e5);
                            if (s2==aCandidate)
                                s2 = g.getEdgeTarget(e5);
                            if (g.containsEdge(s1,s2)||s1==s2||s2==b1||s2==b3||s2!=b2&&(g.containsEdge(s2,b1)||g.containsEdge(s2,b3))){
                                continue;
                            }
                            
                            for (E e6 : g.edgesOf(aCandidate)){
                                V s3 = g.getEdgeSource(e6);
                                if (s3==aCandidate)
                                    s3 = g.getEdgeTarget(e6);
                                if (g.containsEdge(s3,s2)||s1==s3||s3==s2||g.containsEdge(s1,s3)||s3==b1||s3==b2||s3!=b3&&(g.containsEdge(s3,b1)||g.containsEdge(s3,b2))){
                                    continue;
                                }
                                
                                //s1, s2, s3 could now be the closest vertices to the top vertex of the pyramid
                                Set<V> M = new HashSet<V>(),M1 = new HashSet<V>(),M2 = new HashSet<V>(),M3 = new HashSet<V>();
                                M.addAll(g.vertexSet());
                                M.remove(b1);
                                M.remove(b2);
                                M.remove(b3);
                                M.remove(s1);
                                M.remove(s2);
                                M.remove(s3);
                                M1.addAll(M);
                                M2.addAll(M);
                                M3.addAll(M);
                                M1.add(b1);
                                M2.add(b2);
                                M3.add(b3);
                                
                                Map<V,GraphPath<V, E>>     S1=new HashMap<V,GraphPath<V,E>>(),
                                                        S2=new HashMap<V,GraphPath<V,E>>(),
                                                        S3=new HashMap<V,GraphPath<V,E>>(),
                                                        T1=new HashMap<V,GraphPath<V,E>>(),
                                                        T2=new HashMap<V,GraphPath<V,E>>(),
                                                        T3=new HashMap<V,GraphPath<V,E>>();

                                //find paths which could be the edges of the pyramid
                                for (V m1 : M){
                                    Set<V> validInterior = new HashSet<V>();
                                    validInterior.addAll(M);
                                    Set<V> toRemove = new HashSet<V>();
                                    for (V i : validInterior){
                                        if (g.containsEdge(i,b2)||g.containsEdge(i,s2)||g.containsEdge(i,b3)||g.containsEdge(i,s3)){
                                            toRemove.add(i);
                                        }
                                    }
                                    validInterior.removeAll(toRemove);    
                                    validInterior.add(s1);
                                    Graph<V,E> subg = new AsSubgraph<V,E>(g,validInterior,null);
                                    if (subg.containsVertex(s1)&&subg.containsVertex(m1)){
                                        S1.put(m1,new DijkstraShortestPath<V, E>(subg).getPath(s1,m1));
                                        validInterior.remove(s1);
                                        validInterior.add(b1);
                                        subg = new AsSubgraph<V,E>(g,validInterior,null);
                                        if (subg.containsVertex(b1)&&subg.containsVertex(m1)){
                                            T1.put(m1, new DijkstraShortestPath<V, E>(subg).getPath(b1,m1));
                                        }
                                        else {
                                            S1.remove(m1);
                                        }
                                    }
                                }
                                for (V m2 : M){
                                    Set<V> validInterior = new HashSet<V>();
                                    validInterior.addAll(M);
                                    Set<V> toRemove = new HashSet<V>();
                                    for (V i : validInterior){
                                        if (g.containsEdge(i,b1)||g.containsEdge(i,s1)||g.containsEdge(i,b3)||g.containsEdge(i,s3)){
                                            toRemove.add(i);
                                        }
                                    }
                                    validInterior.removeAll(toRemove);
                                    validInterior.add(s2);
                                    Graph<V,E> subg = new AsSubgraph<V,E>(g,validInterior,null);
                                    if (subg.containsVertex(s2)&&subg.containsVertex(m2)){
                                        S2.put(m2,new DijkstraShortestPath<V, E>(subg).getPath(s2,m2));
                                        validInterior.remove(s2);
                                        validInterior.add(b2);
                                        subg = new AsSubgraph<V,E>(g,validInterior,null);
                                        if (subg.containsVertex(b2)&&subg.containsVertex(m2)){
                                            T2.put(m2,new DijkstraShortestPath<V, E>(subg).getPath(b2,m2));
                                        }
                                        else {
                                            S2.remove(m2);
                                        }
                                    }
                                }
                                for (V m3 : M){
                                    Set<V> validInterior = new HashSet<V>();
                                    validInterior.addAll(M);
                                    Set<V> toRemove = new HashSet<V>();
                                    for (V i : validInterior){
                                        if (g.containsEdge(i,b1)||g.containsEdge(i,s1)||g.containsEdge(i,b2)||g.containsEdge(i,s2)){
                                            toRemove.add(i);
                                        }
                                    }
                                    validInterior.removeAll(toRemove);
                                    validInterior.add(s3);
                                    Graph<V,E> subg = new AsSubgraph<V,E>(g,validInterior,null);
                                    if (subg.containsVertex(s3)&&subg.containsVertex(m3)){
                                        S3.put(m3,new DijkstraShortestPath<V, E>(subg).getPath(s3,m3));
                                        validInterior.remove(s3);
                                        validInterior.add(b3);
                                        subg = new AsSubgraph<V,E>(g,validInterior,null);
                                        if (subg.containsVertex(b3)&&subg.containsVertex(m3)){
                                            T3.put(m3,new DijkstraShortestPath<V, E>(subg).getPath(b3,m3));
                                        }
                                        else {
                                            S3.remove(m3);
                                        }
                                    }
                                }
                                
                                //Check if all edges of a pyramid are valid
                                for (V m1 : S1.keySet()){
                                    GraphPath<V,E> P1 = P(g,S1.get(m1),T1.get(m1),M,m1,b1,b2,b3,s1,s2,s3);
                                    if (P1!=null){
                                        for (V m2 : S2.keySet()){
                                            GraphPath<V,E> P2 = P(g,S2.get(m2),T2.get(m2),M,m2,b2,b1,b3,s2,s1,s3);
                                            if (P2!=null){
                                                for (V m3 : S3.keySet()){
                                                    GraphPath<V,E> P3 = P(g,S3.get(m3),T3.get(m3),M,m3,b3,b1,b2,s3,s1,s2);
                                                    if (P3!=null){
                                                        return true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                
                                }
                                
                                
                            }
                            
                        }
                        
                    }
                            
                        
                    
                }
            }
        }
        
        return false;
    }
    
    /**
     * Finds all Components of a set F contained in V(g)
     * @param g A graph
     * @param F A vertex subset of g
     * @return Components of F in g
     */
    protected List<Set<V>> findAllComponents(Graph<V,E> g, Set<V> F){
        return new ConnectivityInspector<V,E>(new AsSubgraph<>(g,F)).connectedSets();
    }
    
    /**
     * Checks whether a graph contains a Jewel. Running time: O(|V(g)|^6)
     * @param g Graph
     * @return Decides whether there is a jewel in g
     */
    protected boolean containsJewel(Graph<V,E> g){
        for (V v2 : g.vertexSet()){
            for (V v3 : g.vertexSet()){
                if (v2==v3||!g.containsEdge(v2,v3)) continue;
                for (V v5 : g.vertexSet()){
                    if (v2==v5||v3==v5) continue;
                    
                    Set<V> F = new HashSet<V>();
                    for (V f : g.vertexSet()){
                        if (f==v2||f==v3||f==v5||g.containsEdge(f,v2)||g.containsEdge(f,v3)||g.containsEdge(f,v5)) continue;
                        F.add(f);
                    }
                    
                    List<Set<V>> componentsOfF = findAllComponents(g, F);
                    
                    Set<V> X1 = new HashSet<V>();
                    for (V x1 : g.vertexSet()){
                        if (x1==v2||x1==v3||x1==v5||!g.containsEdge(x1,v2)||!g.containsEdge(x1,v5)||g.containsEdge(x1,v3)) continue;
                        X1.add(x1);
                    }
                    Set<V> X2 = new HashSet<V>();
                    for (V x2 : g.vertexSet()){
                        if (x2==v2||x2==v3||x2==v5||g.containsEdge(x2,v2)||!g.containsEdge(x2,v5)||!g.containsEdge(x2,v3)) continue;
                        X2.add(x2);
                    }
                    
                    for (V v1 : X1){
                        for (V v4 : X2){
                            if (v1==v4||g.containsEdge(v1,v4)) continue;
                            for (Set<V> FPrime : componentsOfF){
                                if (hasANeighbour(g, FPrime, v1)&&hasANeighbour(g, FPrime, v4)){
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Checks whether a graph contains a clean shortest odd hole. Running time: O(|V(g)|^4)
     * @param g Graph containing no pyramid or jewel
     * @return Decides whether g contains a clean shortest odd hole
     */
    protected boolean containsCleanShortestOddHole(Graph<V,E> g){
        /*
         * Find 3 Paths which are an uneven odd hole when conjunct
         */
        for (V u : g.vertexSet()){
            for (V v : g.vertexSet()){
                if (u==v||g.containsEdge(u,v)) continue;
                
                GraphPath<V, E> puv = new DijkstraShortestPath<V, E>(g).getPath( u, v);
                if (puv==null) continue;
                
                for (V w : g.vertexSet()){
                    if (w==u||w==v||g.containsEdge(w,u)||g.containsEdge(w,v)) continue;
                    GraphPath<V,E> pvw = new DijkstraShortestPath<V,E>(g).getPath(v, w);
                    if (pvw==null) continue;
                    GraphPath<V,E> pwu = new DijkstraShortestPath<V,E>(g).getPath(w, u);
                    if (pwu==null) continue;
                    Set<V> set = new HashSet<V>();
                    set.addAll(puv.getVertexList());
                    set.addAll(pvw.getVertexList());
                    set.addAll(pwu.getVertexList());
                    Graph<V,E> subg = new AsSubgraph<>(g,set);
                    //Look for holes with more than 6 edges and uneven length
                    if (set.size()<7||subg.vertexSet().size()!=set.size()||subg.edgeSet().size()!=subg.vertexSet().size()||subg.vertexSet().size()%2==0) continue;
                    boolean isCircle=true;
                    for (V t : subg.vertexSet()){
                        //if in an induced subgraph a vertex has not 2 edges, it cannot be an odd hole
                        if (subg.edgesOf(t).size()!=2){ 
                            isCircle=false;
                            break;
                        }
                    }
                    if (!isCircle) continue;
                    
                    return true;
                    
                }
                
            }
        }
        return false;
    }
    
    /**
     * Returns a path in g from start to end avoiding the vertices in X
     * @param g A Graph
     * @param start start vertex
     * @param end end vertex
     * @param X set of vertices which should not be in the graph
     * @return A Path in G\X
     */
    protected GraphPath<V, E> getPathAvoidingX(Graph<V, E> g, V start, V end, Set<V> X){
        Set<V> vertexSet = new HashSet<V>();
        vertexSet.addAll(g.vertexSet());
        vertexSet.removeAll(X);
        vertexSet.add(start);
        vertexSet.add(end);
        Graph<V,E> subg = new AsSubgraph<V,E>(g,vertexSet,null);
        return new DijkstraShortestPath<V, E>(subg).getPath(start,end);
    }
    
    /**
     * Checks whether the vertex set of a graph without a vertex set X contains a shortest odd hole. Running time: O(|V(g)|^4)
     * @param g Graph containing neither pyramid nor jewel
     * @param X Subset of V(g) and a possible Cleaner for an odd hole
     * @return Determines whether g has an odd hole such that X is a near-cleaner for it
     */
    protected boolean containsShortestOddHole(Graph<V,E> g,Set<V> X){
        for (V y1 : g.vertexSet()){
            if (X.contains(y1)) continue;
            
            for(E e13 : g.edgeSet()){
                V x1 = g.getEdgeSource(e13);
                V x3 = g.getEdgeTarget(e13);
                if (x1==x3||x1==y1||x3==y1) continue;
                
                for (E e32 : g.edgesOf(x3)){
                    V x2 = g.getEdgeTarget(e32);
                    if (x2==x3){
                        x2 = g.getEdgeSource(e32);
                    }
                    if (x2==x3||x2==x1||x2==y1||g.containsEdge(x2,x1)) continue;
                    
                    GraphPath<V, E> rx1y1 = getPathAvoidingX(g, x1, y1, X);
                    GraphPath<V, E> rx2y1 = getPathAvoidingX(g, x2, y1, X);
                    
                    double n;
                    if (rx1y1==null||rx2y1==null) continue;
                    
                    V y2 = null;
                    for (V y2Candidate : rx2y1.getVertexList()){
                        if (g.containsEdge(y1,y2Candidate)&&y2Candidate!=x1&&y2Candidate!=x2&&y2Candidate!=x3){
                            y2=y2Candidate;
                            break;
                        }
                    }
                    if (y2==null) continue;
                    
                    GraphPath<V, E> rx3y1 = getPathAvoidingX(g, x3, y1, X);
                    GraphPath<V, E> rx3y2 = getPathAvoidingX(g, x3, y2, X);
                    GraphPath<V, E> rx1y2 = getPathAvoidingX(g, x1, y2, X);
                    if (rx3y1!=null&&rx3y2!=null&&rx1y2!=null&& rx2y1.getLength()==(n=rx1y1.getLength()+1) && n==rx1y2.getLength() && rx3y1.getLength()>=n && rx3y2.getLength()>=n){
                        return true;
                    }
                    
                }
            }
        }
        return false;
    }
    
    /**
     * Checks whether a clean shortest odd hole is in g or whether X is a cleaner for an amenable shortest odd hole
     * @param g A graph, containing no pyramid or jewel
     * @param X A subset X of V(g) and a possible Cleaner for an odd hole
     * @return Returns whether g has an odd hole or there is no shortest odd hole in C such that X is a near-cleaner for C.
     */
    protected boolean routine1(Graph<V,E> g,Set<V> X){
        return containsCleanShortestOddHole(g)||containsShortestOddHole(g, X);    
    }
    
    
    /**
     * Checks whether a graph has a configuration of type T1. A configuration of type T1 in g is a hole of length 5
     * @param g A Graph
     * @return whether g contains a configuration of Type T1 (5-cycle)
     */
    protected boolean hasConfigurationType1(Graph<V,E> g){
        for (V v1 : g.vertexSet()){
            Set<V> temp = new ConnectivityInspector<V, E>(g).connectedSetOf(v1);
            for (V v2 : temp){
                if (v1==v2||!g.containsEdge(v1,v2)) continue;
                for (V v3 : temp){
                    if (v3==v1||v3==v2||!g.containsEdge(v2,v3)||g.containsEdge(v1,v3)) continue;
                    for (V v4 : temp){
                        if (v4==v1||v4==v2||v4==v3||g.containsEdge(v1,v4)||g.containsEdge(v2,v4)||!g.containsEdge(v3,v4)) continue;
                        for (V v5 : temp){
                            if (v5==v1||v5==v2||v5==v3||v5==v4||g.containsEdge(v2,v5)||g.containsEdge(v3,v5)||!g.containsEdge(v1,v5)||!g.containsEdge(v4,v5)) continue;
                            return true;
                        }
                    }
                }
            }
        }
        
        
        return false;
    }
    
    
    /**
     * A vertex y is X-complete if y contained in V(g)\X is adjacent to every vertex in X.
     * @param g A Graph
     * @param y Vertex whose X-completeness is to assess
     * @param X Set of vertices
     * @return whether y is X-complete
     */
    protected boolean isYXComplete(Graph<V,E> g, V y,Set<V> X){
        if (g.vertexSet().contains(y)&&!X.contains(y)){
            for (V x : X){
                if (!g.containsEdge(y,x)){
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    
    
    /**
     * Returns all anticomponents of a graph and a vertex set. 
     * @param g A Graph
     * @param Y A set of vertices
     * @return List of anticomponents of Y in g
     */
    protected List<Set<V>> findAllAnticomponentsOfY(Graph<V,E> g, Set<V> Y){
        Graph<V,E> target;
        if (g.getType().isSimple()) target = new SimpleGraph<>(g.getVertexSupplier(),g.getEdgeSupplier(),g.getType().isWeighted());
        else target = new Multigraph<>(g.getVertexSupplier(),g.getEdgeSupplier(),g.getType().isWeighted());
        new ComplementGraphGenerator<>(g).generateGraph(target);
        
        return findAllComponents(target, Y);
    }
    
    /**
     * <p>Checks whether a graph is of configuration type T2. A configuration of type T2 in g is a sequence v1,v2,v3,v4,P,X such that:</p>
     * <ul>
     * <li> v1-v2-v3-v4 is a path of g</li>
     * <li> X is an anticomponent of the set of all {v1,v2,v4}-complete vertices</li>
     * <li> P is a path in G\(X+{v2,v3}) between v1,v4, and no vertex in P*, i.e. P's interior, is X-complete or adjacent to v2 or adjacent to v3</li>
     * </ul>
     * An example is the complement graph of a cycle-7-graph
     * @param g A Graph
     * @return whether g contains a configuration of Type T2
     */
    protected boolean hasConfigurationType2(Graph<V,E> g){
        for (V v1 : g.vertexSet()){
            for (V v2 : g.vertexSet()){
                if (v1==v2||!g.containsEdge(v1,v2)) continue;
                
                for (V v3 : g.vertexSet()){
                    if (v3==v2||v1==v3||g.containsEdge(v1,v3)||!g.containsEdge(v2,v3)) continue;
                    
                    for (V v4 : g.vertexSet()){
                        if (v4==v1||v4==v2||v4==v3||g.containsEdge(v4,v2)||g.containsEdge(v4,v1)||!g.containsEdge(v3,v4)) continue;
                        
                        Set<V> temp = new HashSet<V>();
                        temp.add(v1);
                        temp.add(v2);
                        temp.add(v4);
                        Set<V> Y = new HashSet<V>();
                        for (V y : g.vertexSet()){
                            if (isYXComplete(g, y, temp)){
                                Y.add(y);
                            }
                        }
                        List<Set<V>> anticomponentsOfY = findAllAnticomponentsOfY(g, Y);
                        for (Set<V> X : anticomponentsOfY){
                            Set<V> v2v3 = new HashSet<V>();
                            v2v3.addAll(g.vertexSet());
                            v2v3.remove(v2);
                            v2v3.remove(v3);
                            v2v3.removeAll(X);
                            if (!v2v3.contains(v1)||!v2v3.contains(v4))continue;
                            
                            GraphPath<V, E> Path = new DijkstraShortestPath<V,E>(new AsSubgraph<>(g,v2v3)).getPath(v1, v4);
                            if (Path==null) continue;
                            List<V> P =Path.getVertexList();
                            if (!P.contains(v1)||!P.contains(v4)) continue;
                            
                            boolean cont = true;
                            for (V p : P){
                                if (p!=v1&&p!=v4&&(g.containsEdge(p,v2)||g.containsEdge(p,v3)||isYXComplete(g,p,X))) {
                                    cont=false; 
                                    break;
                                }
                            }
                            if (cont){
                                return true;
                                
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Reports whether v has at least one neighbour in set
     * @param g A Graph
     * @param set A set of vertices
     * @param v A vertex
     * @return whether v has at least one neighbour in set
     */
    protected boolean hasANeighbour(Graph<V,E> g, Set<V> set, V v){
        for (V s : set){
            if (g.containsEdge(s,v)){
                return true;
            }
        }
        return false;
    }
    
    /**
     * For each anticomponent X, find the maximal connected subset F' containing v5 with the 
     * properties that v1,v2 have no neighbours in F' and no vertex of F'\v5 is X-complete
     * @param g A Graph
     * @param X A set of vertices
     * @param v1 A vertex
     * @param v2 A vertex
     * @param v5 A Vertex
     * @return The maximal connected vertex subset containing v5, no neighbours of v1 and v2, and no X-complete vertex except v5
     */
    protected Set<V> findMaximalConnectedSubset(Graph<V,E> g, Set<V> X, V v1, V v2, V v5){
        Set<V> FPrime = new ConnectivityInspector<V,E>(g).connectedSetOf(v5);
        Set<V> toBeRemoved = new HashSet<V>();
        for (V f : FPrime){
            if (f!=v5&&isYXComplete(g, f, X)||v1==f||v2==f||g.containsEdge(v1,f)||g.containsEdge(v2,f)){
                toBeRemoved.add(f);
            }
        }
        FPrime.removeAll(toBeRemoved);
        return FPrime;
    }
    
    /**
     * Reports whether a vertex has at least one nonneighbour in X
     * @param g A Graph
     * @param v A Vertex
     * @param X A set of vertices
     * @return whether v has a nonneighbour in X
     */
    protected boolean hasANonneighbourInX(Graph<V,E> g, V v, Set<V> X){
        for (V x : X){
            if (!g.containsEdge(v,x)){
                return true;
            }
        }
        return false;
    }
    
    /**
     * <p>Checks whether a graph is of configuration type T3. A configuration of type T3 in g is a sequence v1,...,v6,P,X such that</p>
     * <ul>
     * <li> v1,...,v6 are distinct vertices of g</li>
     * <li> v1v2,v3v4,v2v3,v3v5,v4v6 are edges, and v1v3,v2v4,v1v5,v2v5,v1v6,v2v6,v4v5 are non-edges</li>
     * <li> X is an anticomponent of the set of all {v1,v2,v5}-complete vertices, and v3,v4 are not X-complete</li>
     * <li> P is a path of g\(X+{v1,v2,v3,v4}) between v5,v6, and no vertex in P* is X-complete or adjacent to v1 or adjacent to v2</li>
     * <li> if v5v6 is an edge then v6 is not X-complete</li>
     * </ul>
     * @param g A Graph
     * @return whether g contains a configuration of Type T3
     */
    protected boolean hasConfigurationType3(Graph<V,E> g){
        for (V v1 : g.vertexSet()){
            for (V v2 : g.vertexSet()){
                if (v1==v2||!g.containsEdge(v1,v2)) continue;
                for (V v5 : g.vertexSet()){
                    if (v1==v5||v2==v5||g.containsEdge(v1,v5)||g.containsEdge(v2,v5)) continue;
                    Set<V> triple = new HashSet<V>();
                    triple.add(v1);
                    triple.add(v2);
                    triple.add(v5);
                    Set<V> Y = new HashSet<V>();
                    for (V y : g.vertexSet()){
                        if (isYXComplete(g,y,triple)){
                            Y.add(y);
                        }
                    }
                    List<Set<V>> anticomponents = findAllAnticomponentsOfY(g, Y);
                    for (Set<V> X : anticomponents){
                        Set<V> FPrime = findMaximalConnectedSubset(g, X, v1, v2, v5);
                        Set<V> F = new HashSet<V>();
                        F.addAll(FPrime);
                        for (V x : X){
                            if (!g.containsEdge(x,v1)&&!g.containsEdge(x,v2)&&!g.containsEdge(x,v5)&&hasANeighbour(g,FPrime,x))
                                F.add(x);
                        }
                        
                        for (V v4 : g.vertexSet()){
                            if (v4==v1||v4==v2||v4==v5||g.containsEdge(v2,v4)||g.containsEdge(v5,v4)||!g.containsEdge(v1,v4)||
                                    !hasANeighbour(g, F, v4)||
                                    !hasANonneighbourInX(g, v4, X)||isYXComplete(g, v4, X)) continue;
                            
                            for (V v3 : g.vertexSet()){
                                if (v3==v1||v3==v2||v3==v4||v3==v5||!g.containsEdge(v2,v3)||!g.containsEdge(v3,v4)||!g.containsEdge(v5,v3)||g.containsEdge(v1,v3)||!hasANonneighbourInX(g, v3, X)||isYXComplete(g, v3, X)) continue;
                                for (V v6 : F){
                                    if (v6==v1||v6==v2||v6==v3||v6==v4||v6==v5||!g.containsEdge(v4,v6)||g.containsEdge(v1,v6)||g.containsEdge(v2,v6)||g.containsEdge(v5,v6)&&!isYXComplete(g, v6, X)) continue;
                                    Set<V> verticesForPv5v6 = new HashSet<V>();
                                    verticesForPv5v6.addAll(FPrime);
                                    verticesForPv5v6.add(v5);
                                    verticesForPv5v6.add(v6);
                                    verticesForPv5v6.remove(v1);
                                    verticesForPv5v6.remove(v2);
                                    verticesForPv5v6.remove(v3);
                                    verticesForPv5v6.remove(v4);
        
                                    if (new ConnectivityInspector<V,E>(new AsSubgraph<V,E>(g,verticesForPv5v6)).pathExists(v6, v5)){
                                        return true;
                                    }
                                
                                }
                                
                                
                            }
                            
                        
                        }
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * If true, the graph is not Berge. Checks whether g contains a Pyramid, Jewel, configuration type 1, 2 or 3.
     * @param g A Graph
     * @return whether g contains a pyramid, a jewel, a T1, a T2, or a T3
     */
    protected boolean routine2(Graph<V,E> g){
        return containsPyramid(g)||containsJewel(g)||hasConfigurationType1(g)||hasConfigurationType2(g)||hasConfigurationType3(g);
    }
    
    /**
     * N(a,b) is the set of all {a,b}-complete vertices
     * @param g A Graph
     * @param a A Vertex
     * @param b A Vertex
     * @return The set of all {a,b}-complete vertices
     */
    protected Set<V> N(Graph<V,E> g, V a, V b){
        Set<V> res = new HashSet<V>();
        Set<V> ab = new HashSet<V>();
        ab.add(a);
        ab.add(b);
        for (V c : g.vertexSet()){
            if (isYXComplete(g, c, ab)) res.add(c);
        }
        return res;
    }
    
    /**
     * r(a,b,c) is the cardinality of the largest anticomponent of N(a,b) that contains a nonneighbour of c (or 0, if c is N(a,b)-complete)
     * @param g a Graph
     * @param Nab The set of all {a,b}-complete vertices
     * @param c A vertex
     * @return The cardinality of the largest anticomponent of N(a,b) that contains a nonneighbour of c (or 0, if c is N(a,b)-complete)
     */
    protected int r(Graph<V,E> g, Set<V> Nab, V c){
        if (isYXComplete(g,c,Nab)) return 0;
        List<Set<V>> anticomponents = findAllAnticomponentsOfY(g, Nab);
        int res = 0;
        for (Set<V> set : anticomponents){
            if (!hasANonneighbourInX(g, c, set)) continue;
            if (set.size()>res) res=set.size();
        }
        return res;
    }
    
    /**
     * Y(a,b,c) is the union of all anticomponents of N(a,b) that have cardinality strictly greater than r(a,b,c)
     * @param g A graph
     * @param Nab The set of all {a,b}-complete vertices
     * @param c A vertex
     * @return A Set of vertices with cardinality greater r(a,b,c)
     */
    protected Set<V> Y(Graph<V,E> g, Set<V> Nab, V c){
        int cutoff = r(g,Nab,c);
        List<Set<V>> anticomponents = findAllAnticomponentsOfY(g, Nab);
        Set<V> res = new HashSet<V>();
        for (Set<V> anticomponent : anticomponents){
            if (anticomponent.size()>cutoff){
                res.addAll(anticomponent);
            }
        }
        return res;
    }
    
    /**
     * W(a,b,c) is the anticomponent of N(a,b)+{c} that contains c
     * @param g A graph
     * @param Nab The set of all {a,b}-complete vertices
     * @param c A vertex
     * @return The anticomponent of N(a,b)+{c} containing c
     */
    protected Set<V> W(Graph<V,E> g, Set<V> Nab, V c){
        Set<V> temp = new HashSet<V>();
        temp.addAll(Nab);
        temp.add(c);
        List<Set<V>> anticomponents = findAllAnticomponentsOfY(g, temp);
        for (Set<V> anticomponent : anticomponents)
            if (anticomponent.contains(c))
                return anticomponent;
        return null;
    }
    
    /**
     * Z(a,b,c) is the set of all (Y(a,b,c)+W(a,b,c))-complete vertices
     * @param g A graph
     * @param Nab The set of all {a,b}-complete vertices
     * @param c A vertex
     * @return A set of vertices
     */
    protected Set<V> Z(Graph<V,E> g, Set<V> Nab, V c){
        Set<V> temp = new HashSet<V>();
        temp.addAll(Y(g,Nab,c));
        temp.addAll(W(g,Nab,c));
        Set<V> res = new HashSet<V>();
        for (V it : g.vertexSet()){
            if (isYXComplete(g, it, temp))
                res.add(it);
        }
        return res;
    }
    
    /**
     * X(a,b,c)=Y(a,b,c)+Z(a,b,c)
     * @param g A graph
     * @param Nab The set of all {a,b}-complete vertices
     * @param c A vertex
     * @return The union of Y(a,b,c) and Z(a,b,c)
     */
    protected Set<V> X(Graph<V,E> g, Set<V> Nab, V c){
        Set<V> res = new HashSet<V>();
        res.addAll(Y(g,Nab,c));
        res.addAll(Z(g,Nab,c));
        return res;
    }
    
    /**
     * A triple (a,b,c) of vertices is relevant if a,b are distinct and nonadjacent, and c is not contained in N(a,b) (possibly
     * c is contained in {a,b}).
     * @param g A graph
     * @param a A vertex
     * @param b A vertex
     * @param c A vertex
     * @return Assessement whether a,b,c is a relevant triple
     */
    protected boolean isTripleRelevant(Graph<V,E> g, V a, V b, V c){
        return a!=b&&!g.containsEdge(a,b)&&!N(g,a,b).contains(c);
    }
    
    
    /**
     * Returns a set of vertex sets that may be near-cleaners for an amenable hole in g.
     * @param g A graph
     * @return possible near-cleaners
     */
    protected Set<Set<V>> routine3(Graph<V,E> g){
        Set<Set<V>> NuvList = new HashSet<Set<V>>();
        for (V u : g.vertexSet()){
            for (V v : g.vertexSet()){
                if (u==v||!g.containsEdge(u,v)) continue;
                NuvList.add(N(g,u,v));
            }
        }
        
        Set<Set<V>> tripleList = new HashSet<Set<V>>();
        for (V a : g.vertexSet()){
            for (V b : g.vertexSet()){
                if (a==b||g.containsEdge(a,b)) continue;
                Set<V> Nab = N(g,a,b);
                for (V c : g.vertexSet()){
                    if (isTripleRelevant(g,a,b,c)){
                        tripleList.add(X(g,Nab,c));
                    }
                }
            }
        }
        Set<Set<V>> res = new HashSet<Set<V>>();
        for (Set<V> Nuv : NuvList){
            for (Set<V> triple : tripleList){
                Set<V> temp = new HashSet<V>();
                temp.addAll(Nuv);
                temp.addAll(triple);
                res.add(temp);
            }
        }
        return res;
    }
    
    
    /**
     * Performs the Berge Recognition Algorithm.
     * <p> First this algorithm is used to test whether g or its complement contain a jewel, a pyramid or a configuration of type
     * 1, 2 or 3. If so, it is output that g is not Berge. If not, then every shortest odd hole in g is amenable. This asserted, the near-cleaner subsets
     * of V(g) are determined. For each of them in turn it is checked, if this subset is a near-cleaner and, thus, if there is an odd hole. If 
     * an odd hole is found, this checker will output that g is not Berge. If no odd hole is found, all near-cleaners for the complement graph are determined
     * and it will be proceeded as before. If again no odd hole is detected, g is Berge.
     * @param g A graph
     * @return whether g is Berge and, thus, perfect
     */
    public boolean isBerge(Graph<V,E> g){
        GraphTests.requireDirectedOrUndirected(g);
        Graph<V,E> complementGraph;
        if (g.getType().isSimple()) complementGraph = new SimpleGraph<V,E>(g.getVertexSupplier(),g.getEdgeSupplier(),g.getType().isWeighted());
        else complementGraph = new Multigraph<V,E>(g.getVertexSupplier(),g.getEdgeSupplier(),g.getType().isWeighted());
        new ComplementGraphGenerator<V,E>(g).generateGraph(complementGraph);
        
        if (routine2(g)||routine2(complementGraph)) {
            return false;
        }
        
        for (Set<V> it : routine3(g)){
            if (routine1(g,it)) {
                return false;
            }
        }
        
        for (Set<V> it : routine3(complementGraph)){
            if (routine1(complementGraph,it)){
                return false;
            }
        }
        
        return true;
        
    }
    
    

}
