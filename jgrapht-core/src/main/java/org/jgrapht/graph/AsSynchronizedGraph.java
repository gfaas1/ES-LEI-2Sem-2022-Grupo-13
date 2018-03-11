/*
 * (C) Copyright 2018-2018, by CHEN Kui and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package org.jgrapht.graph;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import org.jgrapht.*;

/**
 * Create a synchronized (thread-safe) Graph backed by the specified Graph. In order to guarantee
 * serial access, it is critical that <strong>all</strong> access to the backing Graph is
 * accomplished through the created Graph.
 *
 * <p>
 * Users need to manually synchronize on {@link EdgeFactory} if creating an edge needs to access
 * shared resources. Failure to follow this advice may result in non-deterministic behavior.
 * </p>
 *
 * <p>
 * For all methods returning a Set, the Graph guarantees that all operations on the returned Set do
 * not affect the backing Graph. For <code>edgeSet</code> and <code>vertexSet</code> methods, the
 * returned Set is backed by the underlying graph, but when a traversal over the set is started via
 * a method such as iterator(), a snapshot of the underlying Set is copied for iteration purposes.
 * For <code>edgesOf</code>, <code>incomingEdgesOf</code> and <code>outgoingEdgesOf</code> methods,
 * the returned Set is a unmodifiable copy of the result produced by the underlying Graph. Users can
 * control whether those copies should be cached; caching may significantly increase memory
 * requirements. If users decide to cache those copies and the backing graph's changes don't affect
 * them, those copies will be returned the next time the method is called. If the backing graph's
 * changes affect them, they will be removed from cache and re-created the next time the method is
 * called. If users decide to not cache those copies, the graph will create ephemeral copies every
 * time the method is called. For other methods returning a Set, the Set is just the backing Graph's
 * return.
 * </p>

 * <p>
 * If users want to get something more about the graph through the vertex or edge may be removed by
 * other threads, fail with an {@link IllegalArgumentException} will be caused. Therefore, calling
 * the remove methods concurrently with a typical algorithm is likely to cause the algorithm to fail
 * with an {@link IllegalArgumentException}. So really the main concurrent read/write use case is
 * add-only.
 * </p>
 *
 * <p>
 * The created Graph's hashCode is equal to the backing set's hashCode. And the created Graph is equal
 * to another Graph if they are the same Graph or the backing Graph is equal to the other Graph.
 * </p>
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author CHEN Kui
 * @since Feb 23, 2018
 */
public class AsSynchronizedGraph<V, E>
    extends GraphDelegator<V, E>
    implements Graph<V, E>, Serializable
{
    private static final long serialVersionUID = 5144561442831050752L;

    // Object on which to synchronize
    private final Object mutex;

    // A set encapsulating backing vertexSet.
    private transient CopyOnDemandSet<V> allVerticesSet;

    // A set encapsulating backing edgeSet.
    private transient CopyOnDemandSet<E> allEdgesSet;

    private CacheStrategy<V, E> cacheStrategy;

    /**
     * Constructor for AsSynchronizedGraph with strategy of not caching the copies for
     * <code>edgesOf</code>, <code>incomingEdgesOf</code> and <code>outgoingEdgesOf</code> methods.
     *
     * @param g the backing graph (the delegate).
     */
    public AsSynchronizedGraph(Graph<V, E> g)
    {
        this(g, false);
    }

    /**
     * Constructor for AsSynchronizedGraph with specified cache strategy for <code>edgesOf</code>,
     * <code>incomingEdgesOf</code> and <code>outgoingEdgesOf</code> methods.
     *
     * @param g the backing graph (the delegate).
     * @param cacheEnabled a flag whether to use cache for those methods, if <tt>true</tt>, cache
     *        will be used for those methods, otherwise cache will not be used.
     */
    private AsSynchronizedGraph(Graph<V, E> g, boolean cacheEnabled)
    {
        super(g);
        mutex = this;
        if (cacheEnabled)
            cacheStrategy = new CacheAccess();
        else
            cacheStrategy = new NoCache();
        allEdgesSet = new CopyOnDemandSet<>(super.edgeSet(), mutex);
        allVerticesSet = new CopyOnDemandSet<>(super.vertexSet(), mutex);
    }

    /**
     * Constructor for AsSynchronizedGraph with specified properties.
     *
     * @param g the backing graph (the delegate)
     * @param params specified properties for AsSynchronizedGraph to be created
     */
    public AsSynchronizedGraph(Graph<V, E> g, SynchronizedGraphParams params)
    {
        this(g, params.isCacheEnable());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> getAllEdges(V sourceVertex, V targetVertex)
    {
        synchronized (mutex) {
            return
                super.getAllEdges(sourceVertex, targetVertex);
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
            E e = cacheStrategy.addEdge(sourceVertex, targetVertex);
            if (e != null)
                edgeSetModified();
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
             if (cacheStrategy.addEdge(sourceVertex, targetVertex, e)) {
                 edgeSetModified();
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
    public boolean containsEdge(V sourceVertex, V targetVertex)
    {
        synchronized (mutex) {
            return super.containsEdge(sourceVertex, targetVertex);
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
            return allEdgesSet;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> edgesOf(V vertex)
    {
        synchronized (mutex) {
            return cacheStrategy.edgesOf(vertex);
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
            return cacheStrategy.incomingEdgesOf(vertex);
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
            return cacheStrategy.outgoingEdgesOf(vertex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAllEdges(Collection<? extends E> edges)
    {
        synchronized (mutex) {
            return super.removeAllEdges(edges);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> removeAllEdges(V sourceVertex, V targetVertex)
    {
        synchronized (mutex) {
            return super.removeAllEdges(sourceVertex, targetVertex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAllVertices(Collection<? extends V> vertices)
    {
        synchronized (mutex) {
            return super.removeAllVertices(vertices);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeEdge(E e)
    {
        synchronized (mutex) {
            if (cacheStrategy.removeEdge(e)) {
                edgeSetModified();
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
            E e = cacheStrategy.removeEdge(sourceVertex, targetVertex);
            if (e != null)
                edgeSetModified();
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
            if (cacheStrategy.removeVertex(v)) {
                edgeSetModified();
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
            return allVerticesSet;
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
     * Return whether the graph uses cache for <code>edgesOf</code>, <code>incomingEdgesOf</code> and
     * <code>outgoingEdgesOf</code> methods.
     * @return <tt>true</tt> if cache is in use, <tt>false</tt> if cache is not in use.
     */
    public boolean isCacheEnabled()
    {
        synchronized (mutex) {
            return cacheStrategy.isCacheEnabled();
        }
    }

    /**
     * Set the cache strategy for <code>edgesOf</code>, <code>incomingEdgesOf</code> and
     * <code>outgoingEdgesOf</code> methods.
     *
     * @param cacheEnabled a flag whether to use cache for those methods, if <tt>true</tt>, cache
     *        will be used for those methods, otherwise cache will not be used.
     * @return the AsSynchronizedGraph
     */
    public AsSynchronizedGraph<V, E> setCache(boolean cacheEnabled)
    {
        synchronized (mutex) {
            if (cacheEnabled == isCacheEnabled())
                return this;
            if (cacheEnabled)
                cacheStrategy = new CacheAccess();
            else
                cacheStrategy = new NoCache();
            return this;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        synchronized (mutex) {
            return getDelegate().hashCode();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        synchronized (this) {
            return getDelegate().equals(o);
        }
    }

    /**
     * Create a unmodifiable copy of the set.
     *
     * @param set the set to be copied.
     *
     * @return a unmodifiable copy of the set
     */
    private <C> Set<C> copySet(Set<C> set)
    {
        return Collections.unmodifiableSet(new LinkedHashSet<>(set));
    }

    /**
     * Inform allVerticesSet that the backing data has been modified.
     */
    private void vertexSetModified()
    {
        allVerticesSet.modified();
    }

    /**
     * Inform allEdgesSet that the backing data has been modified.
     */
    private void edgeSetModified()
    {
        allEdgesSet.modified();
    }

    /**
     * Create a synchronized (thread-safe) and unmodifiable Set backed by the specified Set. In order
     * to guarantee serial access, it is critical that <strong>all</strong> access to the backing
     * Set is accomplished through the created Set.
     *
     * <p>
     * When a traversal over the set is started via a method such as iterator(), a snapshot of the
     * underlying set is copied for iteration purposes.
     * </p>
     *
     * <p>
     * The created Set's hashCode is equal to the backing Set's hashCode. And the created Set is equal
     * to another set if they are the same Set or the backing Set is equal to the other Set.
     * </p>
     *
     * <p>
     * The created set will be serializable if the backing set is serializable.
     * </p>
     *
     * @param <E> the class of the objects in the set
     *
     * @author CHEN Kui
     * @since Feb 23, 2018
     */
    private static class CopyOnDemandSet<E>
            implements Set<E>, Serializable
    {
        private static final long serialVersionUID = -102323563687847936L;

        // Backing set.
        private Set<E> set;

        // Backing set's unmodifiable copy. If null, needs to be recomputed on next access.
        private transient Set<E> copy;

        // A set encapsulating backing vertexSet.
        final Object mutex;

        private static final String UNMODIFIABLE = "this set is unmodifiable";

        /**
         * Constructor for CopyOnDemandSet.
         * @param s the backing set.
         * @param mutex object on which to synchronize.
         */
        private CopyOnDemandSet(Set<E> s, Object mutex)
        {
            set = Objects.requireNonNull(s, "s must not be null");
            copy = null;
            this.mutex = mutex;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int size()
        {
            synchronized (mutex) {
                return set.size();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isEmpty()
        {
            synchronized (mutex) {
                return set.isEmpty();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean contains(Object o)
        {
            synchronized (mutex) {
                return set.contains(o);
            }
        }

        /**
         * Returns an iterator over the elements in the backing set's unmodifiable copy. The elements
         * are returned in the same order of the backing set.
         *
         * @return an iterator over the elements in the backing set's unmodifiable copy.
         */
        @Override
        public Iterator<E> iterator()
        {
            return getCopy().iterator();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object[] toArray()
        {
            synchronized (mutex) {
                return set.toArray();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <T> T[] toArray(T[] a)
        {
            synchronized (mutex) {
                return set.toArray(a);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean add(E e)
        {
            throw new UnsupportedOperationException(UNMODIFIABLE);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean remove(Object o)
        {
            throw new UnsupportedOperationException(UNMODIFIABLE);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean containsAll(Collection<?> c)
        {
            synchronized (mutex) {
                return set.containsAll(c);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean addAll(Collection<? extends E> c)
        {
            throw new UnsupportedOperationException(UNMODIFIABLE);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean retainAll(Collection<?> c)
        {
            throw new UnsupportedOperationException(UNMODIFIABLE);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean removeAll(Collection<?> c)
        {
            throw new UnsupportedOperationException(UNMODIFIABLE);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void clear()
        {
            throw new UnsupportedOperationException(UNMODIFIABLE);
        }

        /**
         * {@inheritDoc}
         */
        // Override default methods in Collection
        @Override
        public void forEach(Consumer<? super E> action)
        {
            synchronized (mutex) {
                set.forEach(action);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean removeIf(Predicate<? super E> filter)
        {
            throw new UnsupportedOperationException(UNMODIFIABLE);
        }

        /**
         * Creates a <Code>Spliterator</code> over the elements in the set's unmodifiable copy.
         *
         * @return a  <code>Spliterator</code> over the elements in the backing set's unmodifiable copy.
         */
        @Override
        public Spliterator<E> spliterator()
        {
            return getCopy().spliterator();
        }

        /**
         * Return a sequential <code>Stream</code> with the backing set's unmodifiable copy as its
         * source.
         * @return a sequential <code>Stream</code> with the backing set's unmodifiable copy as its
         * source.
         */
        @Override
        public Stream<E> stream()
        {
            return getCopy().stream();
        }

        /**
         * Return a possibly parallel <code>Stream</code> with the backing set's unmodifiable copy as
         * its source.
         * @return a possibly parallel <code>Stream</code> with the backing set's unmodifiable copy
         * as its source.
         */
        @Override
        public Stream<E> parallelStream()
        {
            return getCopy().parallelStream();
        }

        /**
         * Compares the specified object with this set for equality.
         * @param o object to be compared for equality with this set.
         * @return <code>true</code> if o and this set are the same object or o is equal to the
         * backing object, false otherwise.
         */
        @Override
        public boolean equals(Object o)
        {
            if (this == o)
                return true;
            synchronized (mutex) {
                return set.equals(o);
            }
        }

        /**
         * Return the backing set's hashcode.
         * @return the backing set's hashcode.
         */
        @Override
        public int hashCode()
        {
            synchronized (mutex) {
                return set.hashCode();
            }
        }

        /**
         * Return the backing set's toString result.
         * @return the backing set's toString result.
         */
        @Override
        public String toString()
        {
            synchronized (mutex) {
                return set.toString();
            }
        }

        /**
         * Get the backing set's unmodifiable copy.
         * @return the backing set's unmodifiable copy.
         */
        private Set<E> getCopy()
        {
            synchronized (mutex) {
                if (copy != null)
                    return copy;
                return Collections.unmodifiableSet(new LinkedHashSet<>(set));
            }
        }

        /**
         * If the backing set is modified, call this method to let this set knows the backing set's
         * copy need to update.
         */
        private void modified()
        {
            synchronized (mutex) {
                copy = null;
            }
        }
    }

    /**
     * An interface for cache strategy of AsSynchronizedGraph's <code>edgesOf</code>,
     * <code>incomingEdgesOf</code> and <code>outgoingEdgesOf</code> methods.
     */
    private interface CacheStrategy<V, E>
    {
        /**
         * Add an edge into AsSynchronizedGraph's backing graph.
         */
        E addEdge(V sourceVertex, V targetVertex);

        /**
         * Add an edge into AsSynchronizedGraph's backing graph.
         */
        boolean addEdge(V sourceVertex, V targetVertex, E e);

        /**
         * Get all edges touching the specified vertex in AsSynchronizedGraph's backing graph.
         */
        Set<E> edgesOf(V vertex);

        /**
         * Get a set of all edges in AsSynchronizedGraph's backing graph incoming into the specified
         * vertex.
         */
        Set<E> incomingEdgesOf(V vertex);

        /**
         * Get a set of all edges in AsSynchronizedGraph's backing graph outgoing from the specified
         * vertex.
         */
        Set<E> outgoingEdgesOf(V vertex);

        /**
         * Remove the specified edge from AsSynchronizedGraph's backing graph.
         */
        boolean removeEdge(E e);

        /**
         * Remove an edge from AsSynchronizedGraph's backing graph.
         */
        E removeEdge(V sourceVertex, V targetVertex);

        /**
         * Remove the specified vertex from AsSynchronizedGraph's backing graph.
         */
        boolean removeVertex(V v);

        /**
         * Return whether the graph uses cache for <code>edgesOf</code>, <code>incomingEdgesOf</code>
         * and <code>outgoingEdgesOf</code> methods.
         * @return <tt>true</tt> if cache is in use, <tt>false</tt> if cache is not in use.
         */
        boolean isCacheEnabled();
    }

    /**
     * Don't use cache for AsSynchronizedGraph's <code>edgesOf</code>, <code>incomingEdgesOf</code>
     * and <code>outgoingEdgesOf</code> methods.
     */
    private class NoCache
        implements CacheStrategy<V, E>, Serializable
    {
        private static final long serialVersionUID = 19246150051213471L;

        /**
         * {@inheritDoc}
         */
        @Override
        public E addEdge(V sourceVertex, V targetVertex)
        {
            return AsSynchronizedGraph.super.addEdge(sourceVertex, targetVertex);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean addEdge(V sourceVertex, V targetVertex, E e)
        {
            return AsSynchronizedGraph.super.addEdge(sourceVertex, targetVertex, e);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Set<E> edgesOf(V vertex)
        {
            return copySet(AsSynchronizedGraph.super.edgesOf(vertex));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Set<E> incomingEdgesOf(V vertex)
        {
            return copySet(AsSynchronizedGraph.super.incomingEdgesOf(vertex));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Set<E> outgoingEdgesOf(V vertex)
        {
            return copySet(AsSynchronizedGraph.super.outgoingEdgesOf(vertex));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean removeEdge(E e)
        {
            return AsSynchronizedGraph.super.removeEdge(e);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public E removeEdge(V sourceVertex, V targetVertex)
        {
            return AsSynchronizedGraph.super.removeEdge(sourceVertex, targetVertex);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean removeVertex(V v)
        {
            return AsSynchronizedGraph.super.removeVertex(v);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isCacheEnabled()
        {
            return false;
        }
    }

    /**
     * Use cache for AsSynchronizedGraph's <code>edgesOf</code>, <code>incomingEdgesOf</code>
     * and <code>outgoingEdgesOf</code> methods.
     */
    private class CacheAccess
        implements CacheStrategy<V, E>, Serializable
    {
        private static final long serialVersionUID = -18262921841829294L;

        // A map caching for incomingEdges operation.
        private transient Map<V, Set<E>> incomingEdgesMap = new HashMap<>();

        // A map caching for outgoingEdges operation.
        private transient Map<V, Set<E>> outgoingEdgesMap = new HashMap<>();

        // A map caching for edgesOf operation.
        private transient Map<V, Set<E>> edgesOfMap = new HashMap<>();

        /**
         * {@inheritDoc}
         */
        @Override
        public E addEdge(V sourceVertex, V targetVertex)
        {
            E e = AsSynchronizedGraph.super.addEdge(sourceVertex, targetVertex);
            if (e != null)
                edgeModified(sourceVertex, targetVertex);
            return e;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean addEdge(V sourceVertex, V targetVertex, E e)
        {
            if (AsSynchronizedGraph.super.addEdge(sourceVertex, targetVertex, e)) {
                edgeModified(sourceVertex, targetVertex);
                return true;
            }
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Set<E> edgesOf(V vertex)
        {
            Set<E> s = edgesOfMap.get(vertex);
            if (s != null)
                return s;
            s = copySet(AsSynchronizedGraph.super.edgesOf(vertex));
            edgesOfMap.put(vertex, s);
            return s;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Set<E> incomingEdgesOf(V vertex)
        {
            Set<E> s = incomingEdgesMap.get(vertex);
            if (s != null)
                return s;
            s = copySet(AsSynchronizedGraph.super.incomingEdgesOf(vertex));
            incomingEdgesMap.put(vertex, s);
            return s;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Set<E> outgoingEdgesOf(V vertex)
        {
            Set<E> s = outgoingEdgesMap.get(vertex);
            if (s != null)
                return s;
            s = copySet(AsSynchronizedGraph.super.outgoingEdgesOf(vertex));
            outgoingEdgesMap.put(vertex, s);
            return s;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean removeEdge(E e)
        {
            V sourceVertex = getEdgeSource(e);
            V targetVertex = getEdgeTarget(e);
            if (AsSynchronizedGraph.super.removeEdge(e)) {
                edgeModified(sourceVertex, targetVertex);
                return true;
            }
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public E removeEdge(V sourceVertex, V targetVertex)
        {
            E e = AsSynchronizedGraph.super.removeEdge(sourceVertex, targetVertex);
            if (e != null)
                edgeModified(sourceVertex, targetVertex);
            return e;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean removeVertex(V v)
        {
            if (AsSynchronizedGraph.super.removeVertex(v)) {
                edgesOfMap.clear();
                incomingEdgesMap.clear();
                outgoingEdgesMap.clear();
                return true;
            }
            return false;
        }

        /**
         * Clear the copies which the edge to be added or removed can affect.
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
            if (!AsSynchronizedGraph.super.getType().isDirected()) {
                outgoingEdgesMap.remove(targetVertex);
                incomingEdgesMap.remove(sourceVertex);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isCacheEnabled()
        {
            return true;
        }
    }
}

// End AsSynchronizedGraph.java
