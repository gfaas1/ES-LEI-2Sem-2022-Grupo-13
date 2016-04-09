package org.jgrapht.graph.specifics;

import org.jgrapht.Graph;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.util.ArrayUnenforcedSet;
import org.jgrapht.util.VertexPair;

import java.io.Serializable;
import java.util.*;

/**
 * Fast implementation of DirectedSpecifics. This class uses additional data structures to improve the performance of methods which depend
 * on edge retrievals, e.g. getEdge(V u, V v), containsEdge(V u, V v),addEdge(V u, V v). A disadvantage is an increate is memory consumption.
 * If memory utilization is an issue, use a {@link DirectedSpecifics} instead.
 *
 * @author Joris Kinable
 */
public class FastLookupDirectedSpecifics<V,E>
    extends DirectedSpecifics<V,E>
    implements Serializable
{
    private static final long serialVersionUID = 4089085208843722263L;

    /* Maps a pair of vertices <u,v> to an set of edges {(u,v)}. In case of a multigraph, all edges which touch both u,v are included in the set */
    protected Map<VertexPair<V>, ArrayUnenforcedSet<E>> touchingVerticesToEdgeMap;

    public FastLookupDirectedSpecifics(AbstractBaseGraph<V, E> abstractBaseGraph)
    {
        this(abstractBaseGraph, new LinkedHashMap<>());
    }

    public FastLookupDirectedSpecifics(AbstractBaseGraph<V, E> abstractBaseGraph, Map<V, DirectedEdgeContainer<V, E>> vertexMap)
    {
        super(abstractBaseGraph, vertexMap);
        this.touchingVerticesToEdgeMap=new HashMap<>();
    }


    /**
     * @see Graph#getAllEdges(Object, Object)
     */
    @Override public Set<E> getAllEdges(V sourceVertex, V targetVertex)
    {
        if (abstractBaseGraph.containsVertex(sourceVertex)&& abstractBaseGraph.containsVertex(targetVertex)) {
            Set<E> edges = touchingVerticesToEdgeMap.get(new VertexPair<>(sourceVertex, targetVertex));
            return edges == null ? Collections.emptySet() : new ArrayUnenforcedSet<>(edges);
        }else{
            return null;
        }
    }

    /**
     * @see Graph#getEdge(Object, Object)
     */
    @Override public E getEdge(V sourceVertex, V targetVertex)
    {
        List<E> edges = touchingVerticesToEdgeMap.get(new VertexPair<>(sourceVertex, targetVertex));
        if(edges==null || edges.isEmpty())
            return null;
        else
            return edges.get(0);
    }

    @Override public void addEdgeToTouchingVertices(E e)
    {
        V source = abstractBaseGraph.getEdgeSource(e);
        V target = abstractBaseGraph.getEdgeTarget(e);

        getEdgeContainer(source).addOutgoingEdge(e);
        getEdgeContainer(target).addIncomingEdge(e);

        VertexPair<V> vertexPair=new VertexPair<>(source, target);
        if(!touchingVerticesToEdgeMap.containsKey(vertexPair))
            touchingVerticesToEdgeMap.put(vertexPair, new ArrayUnenforcedSet<>());
        touchingVerticesToEdgeMap.get(vertexPair).add(e);
    }


    @Override public void removeEdgeFromTouchingVertices(E e)
    {
        V source = abstractBaseGraph.getEdgeSource(e);
        V target = abstractBaseGraph.getEdgeTarget(e);

        getEdgeContainer(source).removeOutgoingEdge(e);
        getEdgeContainer(target).removeIncomingEdge(e);

        //Remove the edge from the touchingVerticesToEdgeMap. If there are no more remaining edges for a pair
        //of touching vertices, remove the pair from the map.
        VertexPair<V> vertexPair=new VertexPair<>(source, target);
        if(touchingVerticesToEdgeMap.containsKey(vertexPair)){
            touchingVerticesToEdgeMap.get(vertexPair).remove(e);
            if(touchingVerticesToEdgeMap.get(vertexPair).isEmpty())
                touchingVerticesToEdgeMap.remove(vertexPair);
        }
    }

}
