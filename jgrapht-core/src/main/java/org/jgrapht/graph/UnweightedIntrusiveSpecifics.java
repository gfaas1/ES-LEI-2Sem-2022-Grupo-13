package org.jgrapht.graph;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.jgrapht.WeightedGraph;
import org.jgrapht.util.TypeUtil;

/**
 * An uniform weights variant of the intrusive specifics.
 * 
 * @author Dimitrios Michail
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 */
class UnweightedIntrusiveSpecifics<V, E>
    implements IntrusiveSpecifics<V, E>
{
    private static final long serialVersionUID = -3143021059895136537L;

    private Map<E, IntrusiveEdge> edgeMap;

    public UnweightedIntrusiveSpecifics()
    {
        this.edgeMap = new LinkedHashMap<>();
    }

    private IntrusiveEdge getIntrusiveEdge(E e)
    {
        if (e instanceof IntrusiveEdge) {
            return (IntrusiveEdge) e;
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
        IntrusiveEdge intrusiveEdge;
        if (e instanceof IntrusiveEdge) {
            intrusiveEdge = (IntrusiveEdge) e;
        } else {
            intrusiveEdge = new IntrusiveEdge();
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
        return WeightedGraph.DEFAULT_EDGE_WEIGHT;
    }

    @Override
    public void setEdgeWeight(E e, double weight)
    {
        throw new UnsupportedOperationException();
    }

}
