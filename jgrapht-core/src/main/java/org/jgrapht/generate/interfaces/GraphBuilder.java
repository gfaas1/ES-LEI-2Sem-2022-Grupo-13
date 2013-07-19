package org.jgrapht.generate.interfaces;

import org.jgrapht.Graph;

public interface GraphBuilder<V, E> {

    /**
     * Builds graph according to the configuration specified through the builder
     *
     * @return      target graph matching configuration specified
     */
    public Graph<V, E> build();

}
