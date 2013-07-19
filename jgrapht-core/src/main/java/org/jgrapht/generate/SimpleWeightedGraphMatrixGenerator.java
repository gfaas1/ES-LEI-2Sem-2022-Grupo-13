package org.jgrapht.generate;


import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.List;

public class SimpleWeightedGraphMatrixGenerator<V, E> extends WeightedGraphGenerator<V, E> {

    protected List<V> vertices;

    ///////////////////////////////////////////////////////////////////////////////////////////////

    public static int[] range(final int from, final int to) {
        int[] range = new int[to - from];
        for (int i=from; i < to; ++i) {
            range[i - from] = i;
        }
        return range;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public SimpleWeightedGraph<V, E> build() {

        if (weights == null)
            throw new IllegalArgumentException("Graph may not be constructed without weight-matrix specified");

        SimpleWeightedGraph<V, E> target;

        if (edgeFactory != null)
            target = new SimpleWeightedGraph<V, E>(edgeFactory);
        else if (edgeClass != null)
            target = new SimpleWeightedGraph<V, E>(edgeClass);
        else
            throw new IllegalArgumentException("Graph may not be constructed only with edge-factory or edge-class specified");

        if (vertices == null)
            throw new IllegalArgumentException("Graph may not be constructed without vertex-set specified");

        assert vertices.size() == weights.length;

        for (V vertex : vertices) {
            target.addVertex(vertex);
        }

        for (int i=0; i < vertices.size(); ++i) {

            assert vertices.size() == weights[i].length;

            for (int j=0; j < vertices.size(); ++j) {
                if (i != j) {
                    target.setEdgeWeight(
                        target.addEdge(vertices.get(i), vertices.get(j)),
                        weights[i][j]
                    );
                }
            }
        }

        return target;

    }

    public SimpleWeightedGraphMatrixGenerator<V, E> vertices(List<V> vertices) {
        this.vertices = vertices;
        return this;
    }

}
