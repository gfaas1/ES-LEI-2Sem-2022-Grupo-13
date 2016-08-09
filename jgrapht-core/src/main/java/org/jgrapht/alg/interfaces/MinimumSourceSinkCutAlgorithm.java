package org.jgrapht.alg.interfaces;

import java.util.Set;

/**
 * Created by jkinable on 8/8/16.
 */
public interface MinimumSourceSinkCutAlgorithm<V, E> {
    public double calculateMinCut(V source, V sink);
    public double getCutCapacity();
    public Set<V> getSourcePartition();
    public Set<V> getSinkPartition();
    public Set<E> getCutEdges();

}
