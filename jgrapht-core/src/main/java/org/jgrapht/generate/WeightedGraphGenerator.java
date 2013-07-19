package org.jgrapht.generate;

import org.jgrapht.EdgeFactory;
import org.jgrapht.WeightedGraph;
import org.jgrapht.generate.interfaces.GraphBuilder;

public abstract class WeightedGraphGenerator<V, E> implements GraphBuilder<V, E> {

    protected Class<? extends E> edgeClass;

    protected EdgeFactory<V, E> edgeFactory;

    protected double[][] weights;

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public abstract WeightedGraph<V, E> build();

    public WeightedGraphGenerator<V, E> edgeFactory(EdgeFactory<V, E> edgeFactory) {
        this.edgeFactory = edgeFactory;
        return this;
    }

    public WeightedGraphGenerator<V, E> edgeClass(Class<? extends E> edgeClass) {
        this.edgeClass = edgeClass;
        return this;
    }

    public WeightedGraphGenerator<V, E> weights(double[][] weights) {
        this.weights = weights;
        return this;
    }

}
