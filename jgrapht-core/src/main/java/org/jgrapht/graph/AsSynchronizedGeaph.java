package org.jgrapht.graph;
import java.io.*;
import java.lang.*;
import java.util.*;
import org.jgrapht.*;
import org.jgrapht.util.*;

/**
 * Create a synchronized (thread-safe) Graph backed by the specified Graph. In order to guarantee
 * serial access, it is critical that <strong>all</strong> access to the backing Graph is
 * accomplished through the returned Graph.
 *
 * <p>
 * It is imperative that the user manually synchronize on {@link EdgeFactory} when creating edges.
 * Failure to follow this advice may result in non-deterministic behavior.
 * </p>
 *
 * <p>
 * The Graph guarantee that all operations on the returned Set does not affect the backing Graph.
 * For <code>edgeSet</code> and <code>vertexSet</code> method, the Graph will return
 * {@link AsSynchronizedIterateOnCopySet} wrapping backing Graph's backing Set. For other
 * Set-returned method, the Graph will return a new {@link java.util.LinkedHashSet} wrapped in
 * <code>java.util.Collections.UnmodifiableSet</code> containing all elements from Set returned by backing Graph.
 * </p>
 *
 * <p>
 * This graph does <i>not</i> pass the hashCode and equals operations through to the backing graph,
 * but relies on <tt>Object</tt>'s <tt>equals</tt> and <tt>hashCode</tt> methods. This graph will be
 * serializable if the backing graph is serializable.
 * </p>
 *
 * <p>
 * The created set will be serializable if the backing set is serializable.
 * </p>
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author CHEN Kui
 * @since Feb 23, 2018
 *
 */

public class AsSynchronizedGeaph<V, E>
    extends GraphDelegator<V, E>
    implements Graph<V, E>, Serializable
{
    private static final long serialVersionUID = 5144561442831050752L;


    // Object on which to synchronize
    private final Object mutex;

    // A set encapsulating backing vertexSet.
    private transient AsSynchronizedIterateOnCopySet<V> allVerticesSet = null;

    // A set encapsulating backing edgeSet.
    private transient AsSynchronizedIterateOnCopySet<E> allEdgesSet = null;

    // A map caching for incomingEdges operation.
    private transient Map<V, Set<E>> incomingEdgesMap = new HashMap<>();

    // A map caching for outgoingEdges operation.
    private transient Map<V, Set<E>> outgoingEdgesMap = new HashMap<>();

    // A map caching for edgesOf operation.
    private transient Map<V, Set<E>> edgesOfMap = new HashMap<>();


    /**
     * Constructor for AsSynchronizedGeaph.
     *
     * @param g the backing graph (the delegate).
     */
    public AsSynchronizedGeaph(Graph<V, E> g)
    {
        super(g);
        this.mutex = this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> getAllEdges(V sourceVertex, V targetVertex)
    {
        synchronized (mutex) {
            return
                copySet(super.getAllEdges(sourceVertex, targetVertex));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E getEdge(V sourceVertex, V targetVertex)
    {
        synchronized (mutex) {
            return super.getEdge(sourceVertex, targetVertex);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public E addEdge(V sourceVertex, V targetVertex)
    {
        synchronized (mutex) {
            E e = super.addEdge(sourceVertex, targetVertex);
            if (e != null) {
                edgeModified(sourceVertex, targetVertex);
            }
            return e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, E e)
    {
        synchronized (mutex) {
             if (super.addEdge(sourceVertex, targetVertex, e)) {
                 edgeModified(sourceVertex, targetVertex);
                 return true;
             }
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addVertex(V v)
    {
        synchronized (mutex) {
            if (super.addVertex(v)) {
                vertexSetModified();
                return true;
            }
            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsEdge(E e)
    {
        synchronized (mutex) {
            return super.containsEdge(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsVertex(V v)
    {
        synchronized (mutex) {
            return super.containsVertex(v);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int degreeOf(V vertex)
    {
        synchronized (mutex) {
            return super.degreeOf(vertex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> edgeSet()
    {
        synchronized (mutex) {
            return getAllEdgesSet();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> edgesOf(V vertex)
    {
        synchronized (mutex) {
            Set<E> st = edgesOfMap.get(vertex);
            if (st != null)
                return st;
            st = copySet(super.edgesOf(vertex));
            edgesOfMap.put(vertex, st);
            return st;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int inDegreeOf(V vertex)
    {
        synchronized (mutex) {
            return super.inDegreeOf(vertex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> incomingEdgesOf(V vertex)
    {
        synchronized (mutex) {
            Set<E> st = incomingEdgesMap.get(vertex);
            if (st != null) return st;
            st = copySet(super.incomingEdgesOf(vertex));
            incomingEdgesMap.put(vertex, st);
            return st;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int outDegreeOf(V vertex)
    {
        synchronized (mutex) {
            return super.outDegreeOf(vertex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> outgoingEdgesOf(V vertex)
    {
        synchronized (mutex) {
            Set<E> st = outgoingEdgesMap.get(vertex);
            if (st != null) return st;
            st = copySet(super.outgoingEdgesOf(vertex));
            outgoingEdgesMap.put(vertex, st);
            return st;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeEdge(E e)
    {
        synchronized (mutex) {
            V source = super.getEdgeSource(e);
            V target = super.getEdgeTarget(e);
            if (super.removeEdge(e)) {
                edgeModified(source, target);
                return true;
            }
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E removeEdge(V sourceVertex, V targetVertex)
    {
        synchronized (mutex) {
            E e = super.removeEdge(sourceVertex, targetVertex);
            if (e != null) {
                edgeModified(sourceVertex, targetVertex);
            }
            return e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeVertex(V v)
    {
        synchronized (mutex) {
            if (super.removeVertex(v)) {
                vertexSetModified();
                edgeSetModified();
                incomingEdgesMap.clear();
                outgoingEdgesMap.clear();
                edgesOfMap.clear();
                return true;
            }
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        synchronized (mutex) {
            return super.toString();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<V> vertexSet()
    {
        synchronized (mutex) {
            return getAllVerticesSet();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V getEdgeSource(E e)
    {
        synchronized (mutex) {
            return super.getEdgeSource(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V getEdgeTarget(E e)
    {
        synchronized (mutex) {
            return super.getEdgeTarget(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getEdgeWeight(E e)
    {
        synchronized (mutex) {
            return super.getEdgeWeight(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEdgeWeight(E e, double weight)
    {
        synchronized (mutex) {
            super.setEdgeWeight(e, weight);
        }
    }


    /**
     * Create a copy of the set.
     *
     * @param set the set to be copied.
     *
     * @return a copy of the set
     */
    private <C> Set<C> copySet(Set<C> set)
    {
        return Collections.unmodifiableSet(new LinkedHashSet<>(set));
    }

    /**
     * Clear the copies which the edge to be added or removed can effect.
     *
     * @param sourceVertex source vertex of the modified edge.
     * @param targetVertex target vertex of the modified edge.
     */
    private void edgeModified(V sourceVertex, V targetVertex)
    {
        outgoingEdgesMap.remove(sourceVertex);
        incomingEdgesMap.remove(targetVertex);
        edgesOfMap.remove(sourceVertex);
        edgesOfMap.remove(targetVertex);
        edgeSetModified();
        if (!super.getType().isDirected()) {
            outgoingEdgesMap.remove(targetVertex);
            incomingEdgesMap.remove(sourceVertex);
        }
    }

    /**
     * Get backing backing graph's vertexSet wrapped in AsSynchronizedIterateOnCopySet.
     *
     * @return backing backing graph's vertexSet wrapped in AsSynchronizedIterateOnCopySet.
     */
    private Set<V> getAllVerticesSet() {
        if (allVerticesSet != null) {
            return allVerticesSet;
        }
        return allVerticesSet = new AsSynchronizedIterateOnCopySet<>(super.vertexSet(), mutex);
    }

    /**
     * Get backing backing graph's edgeSet wrapped in AsSynchronizedIterateOnCopySet.
     *
     * @return backing backing graph's edgeSet wrapped in AsSynchronizedIterateOnCopySet.
     */
    private Set<E> getAllEdgesSet() {
        if (allEdgesSet != null) {
            return allEdgesSet;
        }
        return allEdgesSet = new AsSynchronizedIterateOnCopySet<>(super.edgeSet(), mutex);
    }

    /**
     * Inform allVerticesSet that the backing data has be modified.
     */
    private void vertexSetModified()
    {
        if (allVerticesSet != null)
            allVerticesSet.modified();
    }

    private void edgeSetModified()
    {
        if (allEdgesSet != null)
            allEdgesSet.modified();
    }

}

// End AsSynchronizedGraph.java
