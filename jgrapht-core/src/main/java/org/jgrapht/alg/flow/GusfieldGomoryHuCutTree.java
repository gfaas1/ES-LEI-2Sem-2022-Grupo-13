/*
 * (C) Copyright 2016-2016, by Joris Kinable and Contributors.
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
package org.jgrapht.alg.flow;

import org.jgrapht.Graphs;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
import org.jgrapht.alg.interfaces.MinimumSTCutAlgorithm;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.*;

/**
 * This class computes an Gomory-Hu tree (GHT) using the algorithm proposed by Dan Gusfield. For a definition of GHTs, refer to:
 * <i>Gomory, R., Hu, T. Multi-terminal network flows. Journal of the Socieity for Industrial & Applied mathematics, 9(4), p551-570, 1961.</i>
 * GHTs can be used to efficiently query the maximum flows and minimum cuts for all pairs of vertices. The algorithm is described in:
 * <i>Gusfiel, D, Very simple methods for all pairs network flow analysis. SIAM Journal on Computing, 19(1), p142-155, 1990</i><br>
 * In an undirected graph, there exist n(n-1)/2 different vertex pairs. This class computes the maximum flow/minimum cut between each of these
 * pairs efficiently by performing exactly (n-1) minimum s-t cut computations. If your application needs fewer than (n-1) flow values/cuts,
 * consider computing the maximum flows/minimum cuts manually through {@link MaximumFlowAlgorithm}/{@link MinimumSTCutAlgorithm}.
 *
 *
 * <p>The runtime complexity of this class is O((V-1)Q), where Q is the runtime complexity of the algorithm used to compute
 * s-t cuts in the graph. By default, this class uses the {@link PushRelabelMFImpl} implementation to calculate minimum s-t cuts.
 * This class has a runtime complexity of O(V^3), resulting in a O(V^4) runtime complexity for the overal algorithm.
 *
 *
 * <p>Note: this class performs calculations in a lazy manner. The GHT is not calculated until the first invocation of
 * {@link GusfieldGomoryHuCutTree#calculateMaximumFlow(Object, Object)} or {@link GusfieldGomoryHuCutTree#getGomoryHuTree()}.
 * Moreover, this class <em>only</em> calculates the value of the maximum flow between a source-destination pair; it does not calculate
 * the corresponding flow per edge. If you need to know the exact flow through an edge, use one of the alternative {@link MaximumFlowAlgorithm} implementations.
 *
 * <p>In contrast to an Equivalent Flow Tree ({@link GusfieldGomoryHuCutTree}), Gomory-Hu trees also provide all minimum cuts for all pairs of vertices!
 *
 * <p>This class does not support changes to the underlying graph. Results of this class are undefined when the graph is changes after constructing an instance of this class.
 *
 * @author Joris Kinable
 * @since January 2016
 */
public class GusfieldGomoryHuCutTree<V,E> implements MaximumFlowAlgorithm<V,E>, MinimumSTCutAlgorithm<V,E>{

    private final SimpleWeightedGraph<V, E> network;
    /* Number of vertices in the graph */
    private final int N;
    /* Algorithm used to computed the Maximum s-t flows */
    private final MinimumSTCutAlgorithm<V, E> minimumSTCutAlgorithm;

    /* Data structures for computations */
    private List<V> vertexList=new ArrayList<>();
    private Map<V, Integer> indexMap=new HashMap<>();
    private int[] p; //See vector p in the paper description
    private double[] fl; //See vector fl in the paper description
    private int[] neighbors;

    /* Matrix containing the flow values for every s-t pair */
    private double[][] flowMatrix=null;
    /* Matrix which stores the cheapest edge on the unique path from s to t in a Gomory-Hu tree. Caching these edges
    improves the performance of the minimum cut methods.
     */
    private int[][][] cheapestEdgeMatrix;

    private V lastInvokedSource=null;
    private V lastInvokedTarget=null;

    /**
     * Constructs a new GusfieldEquivalentFlowTree instance.
     * @param network input graph
     */
    public GusfieldGomoryHuCutTree(SimpleWeightedGraph<V, E> network) {
        this(network, MaximumFlowAlgorithmBase.DEFAULT_EPSILON);
    }

    /**
     * Constructs a new GusfieldEquivalentFlowTree instance.
     * @param network input graph
     * @param epsilon precision
     */
    public GusfieldGomoryHuCutTree(SimpleWeightedGraph<V, E> network, double epsilon) {
        this(network, new PushRelabelMFImpl<>(network, epsilon));
    }

    /**
     * Constructs a new GusfieldEquivalentFlowTree instance.
     * @param network input graph
     * @param minimumSTCutAlgorithm algorithm used to compute the minimum s-t cuts
     */
    public GusfieldGomoryHuCutTree(SimpleWeightedGraph<V, E> network, MinimumSTCutAlgorithm<V,E> minimumSTCutAlgorithm) {
        this.network=network;
        this.N=network.vertexSet().size();
        this.minimumSTCutAlgorithm=minimumSTCutAlgorithm;
        vertexList.addAll(network.vertexSet());
        for(int i=0; i<vertexList.size(); i++)
            indexMap.put(vertexList.get(i), i);
    }

    /**
     * Runs the algorithm
     */
    private void calculateEquivalentFlowTree(){
        flowMatrix=new double[N][N];
        cheapestEdgeMatrix=new int[N][N][2];
        p=new int[N];
        fl=new double[N];
        System.out.println("Init p: "+Arrays.toString(p));
//        neighbors=new int[N];

        for(int s=1; s<N; s++){
            int t=p[s];
//            neighbors[s]=t;
            double flowValue=minimumSTCutAlgorithm.calculateMinCut(vertexList.get(s),vertexList.get(t));
            Set<V> sourcePartition=minimumSTCutAlgorithm.getSourcePartition(); //Set X in the paper
            fl[s]=flowValue;

            for(int i=0; i<N; i++)
                if(i!=s && sourcePartition.contains(vertexList.get(i)) && p[i]==t)
                    p[i]=s;
            if(sourcePartition.contains(vertexList.get(p[t]))){
                p[s]=p[t];
                p[t]=s;
                fl[s]=fl[t];
                fl[t]=flowValue;
            }

            //populate the flow matrix
            flowMatrix[s][t]=flowMatrix[t][s]=flowValue;
            cheapestEdgeMatrix[s][t]=cheapestEdgeMatrix[t][s]=new int[]{s, t};

            System.out.println("Calculated flow between: "+s+"-"+t);
            for(int i=0; i<s; i++)
                if(i != t) {
                    if(flowMatrix[s][t]< flowMatrix[t][i]){
                        flowMatrix[s][i] = flowMatrix[i][s]=flowMatrix[s][t];
                        cheapestEdgeMatrix[s][i][0]=cheapestEdgeMatrix[i][s][0]=cheapestEdgeMatrix[s][t][0];
                        cheapestEdgeMatrix[s][i][1]=cheapestEdgeMatrix[i][s][1]=cheapestEdgeMatrix[s][t][1];
//                        cheapestEdgeMatrix[s][i][0]=cheapestEdgeMatrix[i][s][0]=s;
//                        cheapestEdgeMatrix[s][i][1]=cheapestEdgeMatrix[i][s][1]=t;
                    }else{
                        flowMatrix[s][i] = flowMatrix[i][s] = flowMatrix[t][i];
                        cheapestEdgeMatrix[s][i][0]=cheapestEdgeMatrix[i][s][0]=cheapestEdgeMatrix[t][i][0];
                        cheapestEdgeMatrix[s][i][1]=cheapestEdgeMatrix[i][s][1]=cheapestEdgeMatrix[t][i][1];
//                        cheapestEdgeMatrix[s][i][0]=cheapestEdgeMatrix[i][s][0]=t;
//                        cheapestEdgeMatrix[s][i][1]=cheapestEdgeMatrix[i][s][1]=i;
                    }

//                    flowMatrix[s][i] = flowMatrix[i][s] = Math.min(flowMatrix[s][t], flowMatrix[t][i]);
                }
        }
        //TEMP
        System.out.println("Final p: "+Arrays.toString(p));
        for(int i=0; i<N-1; i++){
            for(int j=i+1; j<N; j++){
                System.out.println("Shortest edge from "+vertexList.get(i)+" to "+vertexList.get(j)+" is: ("+vertexList.get(cheapestEdgeMatrix[i][j][0])+","+vertexList.get(cheapestEdgeMatrix[i][j][1])+")");
            }
        }

        //END TEMP

    }

    /**
     * Returns the Gomory-Hu Tree as an actual tree (graph). Note that this tree is not necessary unique.
     * @return Gomory-Hu Tree
     */
    private SimpleWeightedGraph<V,DefaultWeightedEdge> getGomoryHuTree(){
        if(p==null) //Lazy invocation of the algorithm
            this.calculateEquivalentFlowTree();
        SimpleWeightedGraph<V, DefaultWeightedEdge> equivalentFlowTree=new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        Graphs.addAllVertices(equivalentFlowTree, vertexList);
        System.out.println("p: "+Arrays.toString(p));
        for(int i=1; i<N; i++){
            DefaultWeightedEdge e=equivalentFlowTree.addEdge(vertexList.get(i), vertexList.get(p[i]));
            equivalentFlowTree.setEdgeWeight(e, fl[i]);
        }
        return equivalentFlowTree;
    }


    /* ================== Maximum Flow ================== */

    /**
     * Unsupported operation
     * @param source source of the flow inside the network
     * @param sink sink of the flow inside the network
     *
     * @return nothing
     */
    @Override
    public MaximumFlow<E> getMaximumFlow(V source, V sink) {
        throw new UnsupportedOperationException("Flows calculated via Equivalent Flow trees only provide a maximum flow value, not the exact flow per edge/arc.");
    }

    /**
     * Returns the Maximum flow between source and sink. The algorithm is only executed once; successive invocations of this method will return in O(1) time.
     * @param source source vertex
     * @param sink sink vertex
     * @return the Maximum flow between source and sink.
     */
    @Override
    public double calculateMaximumFlow(V source, V sink) {
        assert indexMap.containsKey(source) && indexMap.containsKey(sink);

        lastInvokedSource=source;
        lastInvokedTarget=sink;

        if(p==null) //Lazy invocation of the algorithm
            this.calculateEquivalentFlowTree();
        return flowMatrix[indexMap.get(source)][indexMap.get(sink)];
    }

    /**
     * Returns maximum flow value, that was calculated during last <tt>
     * calculateMaximumFlow</tt> call.
     * @return maximum flow value
     */
    @Override
    public double getMaximumFlowValue() {
        return calculateMaximumFlow(lastInvokedSource, lastInvokedTarget);
    }

    /**
     * Unsupported operation
     * @return nothing
     */
    @Override
    public Map<E, Double> getFlowMap() {
        throw new UnsupportedOperationException("Flows calculated via Equivalent Flow trees only provide a maximum flow value, not the exact flow per edge/arc.");
    }

    /**
     * Unsupported operation
     * @param e edge
     * @return nothing
     */
    @Override
    public V getFlowDirection(E e) {
        throw new UnsupportedOperationException("Flows calculated via Equivalent Flow trees only provide a maximum flow value, not the exact flow per edge/arc.");
    }


    /* ================== Minimum Cut ================== */

    @Override
    public double calculateMinCut(V source, V sink) {
        return calculateMaximumFlow(source, sink);
    }

    @Override
    public double getCutCapacity() {
        return calculateMinCut(lastInvokedSource, lastInvokedTarget);
    }

    @Override
    public Set<V> getSourcePartition() {
        SimpleWeightedGraph<V,DefaultWeightedEdge> gomoryHuTree=this.getGomoryHuTree();
        System.out.println("Last invoked source: "+lastInvokedSource);
        System.out.println("Last invoked target: "+lastInvokedTarget);
        //Calulate the cheapest edge from s to t in the Gomory-Hu tree
        int sourceIndex=cheapestEdgeMatrix[indexMap.get(lastInvokedSource)][indexMap.get(lastInvokedTarget)][0];
        int targetIndex=cheapestEdgeMatrix[indexMap.get(lastInvokedSource)][indexMap.get(lastInvokedTarget)][1];
        DefaultWeightedEdge cheapestEdge=gomoryHuTree.getEdge(vertexList.get(sourceIndex), vertexList.get(targetIndex));
        System.out.println("Cheapest edge: "+cheapestEdge+" source cheapest edge: "+vertexList.get(sourceIndex)+" target cheapest edge: "+vertexList.get(targetIndex)+" source index: "+sourceIndex+" target index: "+targetIndex);

        //Remove the selected edge from the gomoryHuTree graph. The resulting graph consists of 2 components
        getGomoryHuTree().removeEdge(cheapestEdge);

        //Return the vertices in the component with the source vertex
        return new ConnectivityInspector<>(gomoryHuTree).connectedSetOf(lastInvokedSource);
    }

    @Override
    public Set<V> getSinkPartition() {
        Set<V> sinkPartition=new LinkedHashSet<>(network.vertexSet());
        sinkPartition.removeAll(this.getSourcePartition());
        return sinkPartition;
    }

    @Override
    public Set<E> getCutEdges() {
        Set<E> cutEdges=new LinkedHashSet<>();
        Set<V> sourcePartion=this.getSourcePartition();
        for(E e : network.edgeSet()){
            V source=network.getEdgeSource(e);
            V sink=network.getEdgeTarget(e);
            if(sourcePartion.contains(source) ^ sourcePartion.contains(sink))
                cutEdges.add(e);
        }
        return cutEdges;
    }



    public static void main(String[] args){
//        SimpleWeightedGraph<Integer, DefaultWeightedEdge> network=new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
//        Graphs.addAllVertices(network, Arrays.asList(0,1,2));
//        Graphs.addEdge(network, 0, 1, 3);
//        Graphs.addEdge(network, 1, 2, 4);
//        Graphs.addEdge(network, 0, 2, 7);
//        GusfieldGomoryHuCutTree<Integer, DefaultWeightedEdge> gusfieldGomoryHuCutTree=new GusfieldGomoryHuCutTree<Integer, DefaultWeightedEdge>(network);
//        for(Integer v1 : network.vertexSet()){
//            for(Integer v2 : network.vertexSet()){
//                if(v1==v2) continue;
//                System.out.println("Max flow "+v1+"-"+v2+": "+gusfieldGomoryHuCutTree.calculateMaximumFlow(v1, v2));
//            }
//        }
//        System.out.println("Gomory-Hu tree: "+gusfieldGomoryHuCutTree.getGomoryHuTree());

//        SimpleWeightedGraph<Integer, DefaultWeightedEdge> network=new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
//        Graphs.addAllVertices(network, Arrays.asList(1,2,3,4,5,6));
//        Graphs.addEdge(network, 1, 2, 1);
//        Graphs.addEdge(network, 3, 4, 1);
//        Graphs.addEdge(network, 5, 6, 1);
//        Graphs.addEdge(network, 5, 1, 1);
//        Graphs.addEdge(network, 1, 3, 1);
//        Graphs.addEdge(network, 6, 2, 1);
//        Graphs.addEdge(network, 2, 4, 1);
//        GusfieldGomoryHuCutTree<Integer, DefaultWeightedEdge> gusfieldGomoryHuCutTree=new GusfieldGomoryHuCutTree<Integer, DefaultWeightedEdge>(network);
//        for(int i=1; i<6; i++){
//            for(int j=i+1; j<7; j++){
//                System.out.println("Max flow "+i+"-"+j+": "+gusfieldGomoryHuCutTree.calculateMaximumFlow(i, j));
//            }
//        }
//        System.out.println("Gomory-Hu tree: "+gusfieldGomoryHuCutTree.getGomoryHuTree());

        //Example wikipedia
        SimpleWeightedGraph<Integer, DefaultWeightedEdge> network=new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        Graphs.addAllVertices(network, Arrays.asList(0,1,2,3,4,5));
        Graphs.addEdge(network, 0, 1, 1);
        Graphs.addEdge(network, 0, 2, 7);
        Graphs.addEdge(network, 1, 2, 1);
        Graphs.addEdge(network, 1, 3, 3);
        Graphs.addEdge(network, 1, 4, 2);
        Graphs.addEdge(network, 2, 4, 4);
        Graphs.addEdge(network, 3, 4, 1);
        Graphs.addEdge(network, 3, 5, 6);
        Graphs.addEdge(network, 4, 5, 2);
        GusfieldGomoryHuCutTree<Integer, DefaultWeightedEdge> gusfieldGomoryHuCutTree=new GusfieldGomoryHuCutTree<Integer, DefaultWeightedEdge>(network);
        for(int i=0; i<5; i++){
            for(int j=i+1; j<6; j++){
                System.out.println("Max flow "+i+"-"+j+": "+gusfieldGomoryHuCutTree.calculateMaximumFlow(i, j));
            }
        }
        System.out.println("Gomory-Hu tree: "+gusfieldGomoryHuCutTree.getGomoryHuTree());
        gusfieldGomoryHuCutTree.calculateMinCut(0,5);
        System.out.println("Source parition (0-5): "+gusfieldGomoryHuCutTree.getSourcePartition());
        System.out.println("Sink parition (0-5): "+gusfieldGomoryHuCutTree.getSinkPartition());

    }
}
