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
 * GreedyVCImpl.java
 * -----------------
 * (C) Copyright 2003-2008, by Linda Buisman and Contributors.
 *
 * Original Author:  Joris Kinable
 * Contributor(s):   Barak Naveh
 *                   Christian Hammer
 *                   Linda Buisman
 *
 * $Id$
 *
 * Changes
 * -------
 * 06-Nov-2003 : Initial revision (LB);
 * 07-Jun-2005 : Made generic (CH);
 * 28-Jul-2016 : Moved to dedicated package; Added greedy implementation for Weighted VC (JK)
 * 31-Jul-2016 : Replaced original implementation (O(|E|*|N|)) by Linda Buisman with more efficient (O(|E|*log|N|)) implementation
 *
 */
package org.jgrapht.alg.vertexcover;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.interfaces.MinimumWeightedVertexCoverAlgorithm;

import java.util.*;

/**
 * Greedy algorithm to find a vertex cover for a graph. A vertex cover is a set of
 * vertices that touches all the edges in the graph. The graph's vertex set is a
 * trivial cover. However, a <i>minimal</i> vertex set (or at least an
 * approximation for it) is usually desired. Finding a true minimal vertex cover
 * is an NP-Complete problem. For more on the vertex cover problem, see <a
 * href="http://mathworld.wolfram.com/VertexCover.html">
 * http://mathworld.wolfram.com/VertexCover.html</a>
 *
 * Note: this class supports pseudo-graphs
 * Runtime: O(|E|*log|V|)
 *
 *
 *
 * @author Linda Buisman
 * @since Nov 6, 2003
 */
public class GreedyVCImpl<V,E> implements MinimumWeightedVertexCoverAlgorithm<V,E> {

    private static int vertexCounter=0;

    /**
     * Finds a greedy solution to the minimum weighted vertex cover problem. At each iteration, the algorithm picks
     * the vertex v with the smallest ratio {@code weight(v)/degree(v)} and adds it to the cover. Next vertex v
     * and all edges incident to it are removed. The process repeats until all vertices are covered.
     * Runtime: O(|E|*log|V|)
     *
     * @param graph input graph
     * @param vertexWeightMap mapping of vertex weights
     * @return greedy solution
     */
    @Override
    public VertexCover<V> getVertexCover(UndirectedGraph<V,E> graph, Map<V, Double> vertexWeightMap) {
        Set<V> cover=new LinkedHashSet<>();
        double weight=0;

        //Create working graph: for every vertex, create a ComparableVertex which maintains its own list of neighbors
        Map<V, ComparableVertex> vertexEncapsulationMap=new HashMap<>();
        graph.vertexSet().stream().filter(v -> graph.degreeOf(v) > 0).forEach(v -> vertexEncapsulationMap.put(v, new ComparableVertex(v, vertexWeightMap.get(v))));

        for(E e : graph.edgeSet()){
            V u=graph.getEdgeSource(e);
            ComparableVertex ux=vertexEncapsulationMap.get(u);
            V v=graph.getEdgeTarget(e);
            ComparableVertex vx=vertexEncapsulationMap.get(v);
            ux.addNeighbor(vx);
            vx.addNeighbor(ux);

            assert(ux.neighbors.get(vx) == vx.neighbors.get(ux)): " in an undirected graph, if vx is a neighbor of ux, then ux must be a neighbor of vx";
        }

        TreeSet<ComparableVertex> workingGraph=new TreeSet<>();
        workingGraph.addAll(vertexEncapsulationMap.values());
        assert(workingGraph.size() == graph.vertexSet().size());

        while(!workingGraph.isEmpty()) { //Continue until all edges are covered

            //Find a vertex vx for which W(vx)/degree(vx) is minimal
            ComparableVertex vx = workingGraph.pollFirst();
            assert(workingGraph.parallelStream().allMatch(ux -> vx.getRatio() <= ux.getRatio())) : "vx does not have the smallest ratio among all elements. VX: "+vx+" WorkingGraph: "+workingGraph;

            for(ComparableVertex nx : vx.neighbors.keySet()){

                if(nx ==vx) //Ignore self loops
                    continue;

                workingGraph.remove(nx);

                //Delete vx from nx' neighbor list. Delete nx from the graph and place it back, thereby updating the ordering of the graph
                nx.removeNeighbor(vx);

                if (nx.degree > 0)
                    workingGraph.add(nx);

            }

            //Update cover
            cover.add(vx.v);
            weight+=vertexWeightMap.get(vx.v);
            assert(!workingGraph.parallelStream().anyMatch(ux -> ux.ID==vx.ID)) : "vx should no longer exist in the working graph";
        }

        return new VertexCover<>(cover, weight);
    }

    public class ComparableVertex implements Comparable<ComparableVertex>{
        /** original vertex **/
        public final V v;

        /** weight of the vertex **/
        public double weight;

        /** unique id, used to guarantee that compareTo never returns 0 **/
        public final int ID;

        /** degree of this vertex **/
        private int degree=0;

        /** Map of neighbors, and a count of the number of edges to this neighbor **/
        public Map<ComparableVertex, Integer> neighbors;

        public ComparableVertex(V v, double weight){
            this.ID=vertexCounter++;
            this.v=v;
            this.weight=weight;
            neighbors=new LinkedHashMap<>();
        }

        public void addNeighbor(ComparableVertex v){
            if(!neighbors.containsKey(v))
                neighbors.put(v,1);
            else
                neighbors.put(v, neighbors.get(v) + 1);
            degree++;

            assert(neighbors.values().stream().mapToInt(Integer::intValue).sum() == degree);
        }

        public void removeNeighbor(ComparableVertex v){
            degree-=neighbors.get(v);
            neighbors.remove(v);
        }

        public double getRatio(){
            return weight/degree;
        }

        @Override
        public int compareTo(ComparableVertex other) {
            if(this.ID == other.ID) //Same vertex
                return 0;
            int result=Double.compare(this.getRatio(), other.getRatio());
            if(result == 0 ) //If vertices have the same value, resolve tie by an ID comparison
                return Integer.compare(this.ID, other.ID);
            else
                return result;
        }

        @Override
        public int hashCode(){
            return ID;
        }

        @Override
        public boolean equals(Object o){
            if(this==o)
                return true;
            else if(!(o instanceof ClarksonTwoApproxVCImpl.ComparableVertex))
                return false;
            ComparableVertex other=(ComparableVertex)o;
            return this.ID==other.ID;
        }

        @Override
        public String toString(){
            return "v"+ID+"("+degree+")";
        }
    }
}
