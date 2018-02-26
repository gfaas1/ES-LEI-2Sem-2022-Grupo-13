package org.jgrapht.util;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Create a synchronized (thread-safe) Set backed by the specified Set. In order to guarantee
 * serial access, it is critical that <strong>all</strong> access to the backing Set is
 * accomplished through the returned Set.
 *
 * <p>
 * The difference between this class and Collections.SynchronizedSet is that it will happen on the
 * backing Set's copy which is unmodifiable containing all set's elements when traversing it via
 * <code>Iterator</code>, <code>Spliterator</code>, <code>Stream</code> or
 * <code>ParallelStream</code>. Which means that all operations through those methods
 * will <strong>not</strong> change the backing Set.
 * </p>
 *
 * <p>
 * When <code>iterator()</code>, <code>spliterator()</code>, <code>stream()</code> or
 * <code>parallelStream()</code> is called. It will firstly check if need to create the backing
 * set's copy which is unmodifiable, if needed, a <code>LinkedHashSet</code> containing all backing
 * set's elements at this moment will be created to support those operation. If the backing set is
 * modified not by the wrapper, user need to call <code>modified()</code> method manually to inform
 * those method of creating the backing set's copy is needed.
 * </p>
 *
 * <p>
 * The created set's hashCode is equal to the backing set's hashCode. And the created set is equal
 * to anther set if they are the same set or the backing set is equal to another set.
 * </p>
 *
 * <p>
 * The created set will be serializable if the backing set is serializable.
 * </p>
 *
 *
 * @param <E> the class of the objects in the set
 *
 * @author CHEN Kui
 * @since Feb 23, 2018
 */

public class AsSynchronizedIterateOnCopySet<E>
    implements Set<E>, Serializable
{
    private static final long serialVersionUID = -102323563687847936L;

    // Backing set.
    private Set<E> set;

    // Backing set's copy which is unmodifiable. It means the copy set need to be update if the set
    // is null.
    private transient Set<E> copy;

    // Object on which to synchronize
    final private Object mutex;

    /**
     * Constructor for AsSynchronizedIterateOnCopySet.
     * @param s the backing graph.
     */
    public AsSynchronizedIterateOnCopySet(Set<E> s)
    {
        set = Objects.requireNonNull(s, "s must not be null");
        copy = null;
        mutex = this;
    }

    /**
     * Constructor for AsSynchronizedIterateOnCopySet.
     * @param s the backing graph.
     * @param _mutex the object on which to synchronize.
     */
    public AsSynchronizedIterateOnCopySet(Set<E> s, Object _mutex)
    {
        set = Objects.requireNonNull(s, "s must not be null");
        copy = null;
        mutex = _mutex;
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
     * Returns an iterator over the elements in the backing set's copy which is unmodifiable. The
     * elements are returned in the same order of the backing set.
     *
     * @return an iterator over the elements in the backing set's copy which is unmodifiable.
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
        synchronized (mutex) {
            if (set.add(e)) {
                modified();
                return true;
            }
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(Object o)
    {
        synchronized (mutex) {
            if (set.remove(o)) {
                modified();
                return true;
            }
            return false;
        }
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
        synchronized (mutex) {
            try {
                if (set.addAll(c)) {
                    modified();
                    return true;
                }
                return false;
            } catch (UnsupportedOperationException e) {
                throw e;
            } catch (Exception e) {
                modified();
                throw e;
            }

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean retainAll(Collection<?> c)
    {
        synchronized (mutex) {
            try {
                if (set.retainAll(c)) {
                    modified();
                    return true;
                }
                return false;
            } catch (UnsupportedOperationException e) {
                throw e;
            } catch (Exception e) {
                modified();
                throw e;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAll(Collection<?> c)
    {
        synchronized (mutex) {
            try {
                if (set.removeAll(c)) {
                    modified();
                    return true;
                }
                return false;
            } catch (UnsupportedOperationException e) {
                throw e;
            } catch (Exception e) {
                modified();
                throw e;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear()
    {
        synchronized (mutex) {
            set.clear();
            modified();
        }
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
        synchronized (mutex) {
            try {
                if (set.removeIf(filter)) {
                    modified();
                    return true;
                }
                return false;
            } catch (UnsupportedOperationException e) {
                throw e;
            } catch (Exception e) {
                modified();
                throw e;
            }
        }
    }

    /**
     * Creates a <Code>Spliterator</code> over the elements in the set's copy which is unmodifiable.
     *
     * @return a  <code>Spliterator</code> over the elements in the backing set's copy which is
     * unmodifiable.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Spliterator<E> spliterator()
    {
        return getCopy().spliterator();
    }

    /**
     * Return a sequential <code>Stream</code> with the backing set's copy which is unmodifiable as
     * its source.
     * @return a sequential <code>Stream</code> with the backing set's copy which is unmodifiable as
     * its source.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Stream<E> stream() {
        return getCopy().stream();
    }

    /**
     * Return a possibly parallel <code>Stream</code> with the backing set's copy which is
     * unmodifiable as its source.
     * @return a possibly parallel <code>Stream</code> with the backing set's copy which is
     * unmodifiable as its source.
     * It is allowable for this method to return a sequential stream.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Stream<E> parallelStream() {
        return getCopy().parallelStream();
    }

    /**
     * Compares the specified object with this set for equality.
     * @param o object to be compared for equality with this set.
     * @return <code>true</code> if o and this set is the same object or o is equal to the
     * backing object, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
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
    public int hashCode() {
        synchronized (mutex) {
            return set.hashCode();
        }
    }

    /**
     * Return the backing set's toString result.
     * @return the backing set's toString result.
     */
    @Override
    public String toString() {
        synchronized (mutex) {
            return set.toString();
        }
    }

    /**
     * Get the backing set's copy which is unmodifiable.
     * @return the backing set's copy which is unmodifiable.
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
    public void modified() {
        copy = null;
    }
}
