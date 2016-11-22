package org.jgrapht.alg.flow;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
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
 * @author Mads Jensen
 * @since January 2016
 */
public class GusfieldEquivalentFlowTree<V,E> implements MaximumFlowAlgorithm<V,E>{

    private final Graph<V, E> network;
    /* Used to compare floating point values */
    private final Comparator<Double> comparator;

    /* Data structures for computations */
    List<V> vertexList=new ArrayList<>();
    Map<V, Integer> indexMap=new HashMap<>();
    int[] p;

    /* Results */
    double[][] flowMatrix=null;


    private V lastInvokedSource=null;
    private V lastInvokedTarget=null;

    public GusfieldEquivalentFlowTree(SimpleWeightedGraph<V, E> network) {
        this(network, MaximumFlowAlgorithmBase.DEFAULT_EPSILON);
    }

    public GusfieldEquivalentFlowTree(SimpleWeightedGraph<V, E> network, double epsilon) {
        this.network = network;
        this.comparator = new ToleranceDoubleComparator(epsilon);
        vertexList.addAll(network.vertexSet());
        for(int i=0; i<vertexList.size(); i++)
            indexMap.put(vertexList.get(i), i);
    }

    private void calculateEquivalentFlowTree(){

    }

    private void populateFlowMatrix(){
        if(p==null)
            this.calculateEquivalentFlowTree();
        flowMatrix=new double[network.vertexSet().size()][network.vertexSet().size()];
    }

    private SimpleWeightedGraph<V,DefaultWeightedEdge> getEquivalentFlowTree(){
        if(p==null)
            this.calculateEquivalentFlowTree();
        SimpleWeightedGraph<V, DefaultWeightedEdge> equivalentFlowTree=new SimpleWeightedGraph<V, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        Graphs.addAllVertices(equivalentFlowTree, vertexList);

        return equivalentFlowTree;
    }

    @Override
    public MaximumFlow<E> getMaximumFlow(V source, V sink) {
        throw new UnsupportedOperationException("Flows calculated via Equivalent Flow trees only provide a maximum flow value, not the exact flow per edge/arc.");
    }

    @Override
    public double calculateMaximumFlow(V source, V sink) {
        if(flowMatrix==null)
            this.populateFlowMatrix();
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
}
