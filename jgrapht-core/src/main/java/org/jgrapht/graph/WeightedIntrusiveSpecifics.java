package org.jgrapht.graph;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.jgrapht.util.TypeUtil;

/**
 * A weighted variant of the intrusive specifics.
 * 
 * @author Dimitrios Michail
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 */
class WeightedIntrusiveSpecifics<V, E>
    implements IntrusiveSpecifics<V, E>
{
    private static final long serialVersionUID = -3043688536993767461L;

    private Map<E, IntrusiveWeightedEdge> edgeMap;

    public WeightedIntrusiveSpecifics()
    {
        this.edgeMap = new LinkedHashMap<>();
    }

    private IntrusiveWeightedEdge getIntrusiveEdge(E e)
    {
        if (e instanceof IntrusiveWeightedEdge) {
            return (IntrusiveWeightedEdge) e;
        }
        return edgeMap.get(e);
    }

    @Override
    public boolean containsEdge(E e)
    {
        return edgeMap.containsKey(e);
    }

    @Override
    public Set<E> getEdgeSet()
    {
        return edgeMap.keySet();
    }

    @Override
    public void remove(E e)
    {
        edgeMap.remove(e);
    }

    @Override
    public void add(E e, V sourceVertex, V targetVertex)
    {
        IntrusiveWeightedEdge intrusiveEdge;
        if (e instanceof IntrusiveWeightedEdge) {
            intrusiveEdge = (IntrusiveWeightedEdge) e;
        } else {
            intrusiveEdge = new IntrusiveWeightedEdge();
        }
        intrusiveEdge.source = sourceVertex;
        intrusiveEdge.target = targetVertex;
        edgeMap.put(e, intrusiveEdge);
    }

    @Override
    public V getEdgeSource(E e)
    {
        IntrusiveEdge ie = getIntrusiveEdge(e);
        if (ie == null) {
            throw new IllegalArgumentException("no such edge in graph: " + e.toString());
        }
        return TypeUtil.uncheckedCast(ie.source, null);
    }

    @Override
    public V getEdgeTarget(E e)
    {
        IntrusiveEdge ie = getIntrusiveEdge(e);
        if (ie == null) {
            throw new IllegalArgumentException("no such edge in graph: " + e.toString());
        }
        return TypeUtil.uncheckedCast(ie.target, null);
    }

    @Override
    public double getEdgeWeight(E e)
    {
        IntrusiveWeightedEdge ie = getIntrusiveEdge(e);
        if (ie == null) {
            throw new IllegalArgumentException("no such edge in graph: " + e.toString());
        }
        return ie.weight;
    }

    @Override
    public void setEdgeWeight(E e, double weight)
    {
        IntrusiveWeightedEdge ie = getIntrusiveEdge(e);
        if (ie == null) {
            throw new IllegalArgumentException("no such edge in graph: " + e.toString());
        }
        ie.weight = weight;
    }

}
