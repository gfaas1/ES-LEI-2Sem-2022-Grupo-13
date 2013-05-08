package org.jgrapht.graph.builder;

import org.jgrapht.EdgeFactory;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.builder.interfaces.GraphBuilder;

public abstract class WeightedGraphBuilder<V, E> implements GraphBuilder<V, E> {

    protected Class<? extends E> edgeClass;

    protected EdgeFactory<V, E> edgeFactory;

    protected double[][] weights;

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public abstract WeightedGraph<V, E> build();

    public WeightedGraphBuilder<V, E> edgeFactory(EdgeFactory<V, E> edgeFactory) {
        this.edgeFactory = edgeFactory;
        return this;
    }

    public WeightedGraphBuilder<V, E> edgeClass(Class<? extends E> edgeClass) {
        this.edgeClass = edgeClass;
        return this;
    }

    public WeightedGraphBuilder<V, E> weights(double[][] weights) {
        this.weights = weights;
        return this;
    }

}
