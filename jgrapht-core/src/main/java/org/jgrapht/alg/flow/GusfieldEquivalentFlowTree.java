package org.jgrapht.alg.flow;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
import org.jgrapht.alg.interfaces.MinimumSTCutAlgorithm;
import org.jgrapht.alg.util.ToleranceDoubleComparator;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.*;

/**
 * Implementation of the Gomory Hu minimum cut tree algorithm defined
 * originally by Gomory and Hu in 1961, and revised for simplicity by
 * Gusfield.  The article builds on computing O(n) max flows. The algorithm
 * may compute more than cut tree for the same flow network.
 *
 * @article{gomory1961multi,
 *   title={Multi-terminal network flows},
 *   author={Gomory, Ralph E and Hu, Tien Chung},
 *   journal={Journal of the Society for Industrial \& Applied Mathematics},
 *   volume={9},
 *   number={4},
 *   pages={551--570},
 *   year={1961},
 *   publisher={SIAM}
 * }
 *
 * @article{gusfield1990very,
 *   title={Very simple methods for all pairs network flow analysis},
 *   author={Gusfield, Dan},
 *   journal={SIAM Journal on Computing},
 *   volume={19},
 *   number={1},
 *   pages={143--155},
 *   year={1990},
 *   publisher={SIAM}
 * }
 *
 * @author Joris Kinable
 * @since January 2016
 */
public class GusfieldEquivalentFlowTree<V,E> implements MaximumFlowAlgorithm<V,E>{

    private final Graph<V, E> network;
    private final int N;
//    /* Used to compare floating point values */
//    private final Comparator<Double> comparator;
    private final MinimumSTCutAlgorithm<V, E> minimumSTCutAlgorithm;

    /* Data structures for computations */
    private List<V> vertexList=new ArrayList<>();
    private Map<V, Integer> indexMap=new HashMap<>();
    private int[] p;

    /* Results */
    private double[][] flowMatrix=null;


    private V lastInvokedSource=null;
    private V lastInvokedTarget=null;

    public GusfieldEquivalentFlowTree(SimpleWeightedGraph<V, E> network) {
        this(network, MaximumFlowAlgorithmBase.DEFAULT_EPSILON);
    }

    public GusfieldEquivalentFlowTree(SimpleWeightedGraph<V, E> network, double epsilon) {
        this(network, new PushRelabelMFImpl(network, epsilon));
    }

    public GusfieldEquivalentFlowTree(SimpleWeightedGraph<V, E> network, MinimumSTCutAlgorithm<V,E> minimumSTCutAlgorithm) {
        this.network = network;
        this.N=network.vertexSet().size();
//        this.comparator = new ToleranceDoubleComparator(epsilon);
        this.minimumSTCutAlgorithm=minimumSTCutAlgorithm;
        vertexList.addAll(network.vertexSet());
        for(int i=0; i<vertexList.size(); i++)
            indexMap.put(vertexList.get(i), i);
    }

    private void calculateEquivalentFlowTree(){
        flowMatrix=new double[N][N];
        p=new int[N];

        for(int s=1; s<N; s++){
            int t=p[s];
            double flowValue=minimumSTCutAlgorithm.calculateMinCut(vertexList.get(s),vertexList.get(t));
            Set<V> sourcePartition=minimumSTCutAlgorithm.getSourcePartition(); //Set X in the paper
            for(int i=s; i<N; i++)
                if(sourcePartition.contains(vertexList.get(i)) && p[i]==t)
                    p[i]=s;

            //populate the flow matrix
            flowMatrix[s][t]=flowMatrix[t][s]=flowValue;
            for(int i=0; i<s; i++)
                if(i != t)
                    flowMatrix[s][i]=flowMatrix[i][s]=Math.min(flowMatrix[s][t], flowMatrix[t][i]);
        }
    }

    private SimpleWeightedGraph<V,DefaultWeightedEdge> getEquivalentFlowTree(){
        if(p==null) //Lazy invocation of the algorithm
            this.calculateEquivalentFlowTree();
        SimpleWeightedGraph<V, DefaultWeightedEdge> equivalentFlowTree=new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        Graphs.addAllVertices(equivalentFlowTree, vertexList);
        for(int i=1; i<p.length; i++){
            DefaultWeightedEdge e=equivalentFlowTree.addEdge(vertexList.get(i), vertexList.get(p[i]));
            equivalentFlowTree.setEdgeWeight(e, flowMatrix[i][p[i]]);
        }
        return equivalentFlowTree;
    }

    @Override
    public MaximumFlow<E> getMaximumFlow(V source, V sink) {
        throw new UnsupportedOperationException("Flows calculated via Equivalent Flow trees only provide a maximum flow value, not the exact flow per edge/arc.");
    }

    @Override
    public double calculateMaximumFlow(V source, V sink) {
        if(p==null) //Lazy invocation of the algorithm
            this.calculateEquivalentFlowTree();
        return flowMatrix[indexMap.get(source)][indexMap.get(sink)];
    }

    @Override
    public double getMaximumFlowValue() {
        return calculateMaximumFlow(lastInvokedSource, lastInvokedTarget);
    }

    @Override
    public Map<E, Double> getFlowMap() {
        throw new UnsupportedOperationException("Flows calculated via Equivalent Flow trees only provide a maximum flow value, not the exact flow per edge/arc.");
    }

    @Override
    public V getFlowDirection(E e) {
        throw new UnsupportedOperationException("Flows calculated via Equivalent Flow trees only provide a maximum flow value, not the exact flow per edge/arc.");
    }

    public static void main(String[] args){
        SimpleWeightedGraph<Integer, DefaultWeightedEdge> network=new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        Graphs.addAllVertices(network, Arrays.asList(0,1,2));
        network.addEdge(0,1);
        network.addEdge(1,2);
        network.addEdge(2,0);
        GusfieldEquivalentFlowTree<Integer, DefaultWeightedEdge> gusfieldEquivalentFlowTree=new GusfieldEquivalentFlowTree<Integer, DefaultWeightedEdge>(network);
        for(Integer v1 : network.vertexSet()){
            for(Integer v2 : network.vertexSet()){
                if(v1==v2) continue;
                System.out.println("Max flow "+v1+"-"+v2+": "+gusfieldEquivalentFlowTree.getMaximumFlow(v1, v2));
            }
        }
    }
}
