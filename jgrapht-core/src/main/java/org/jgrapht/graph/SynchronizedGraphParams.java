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

/**
 * The SynchronizedGraph Params describes the properties of a AsSynchronizedGraph to be created such
 * as whether it uses cache.
 *
 * @author CHEN Kui
 * @since Mar 9, 2018
 */
public class SynchronizedGraphParams
{
    private boolean cacheEnable;

    /**
     * Constructor for SynchronizedGraphParams with cacheDisable.
     */
    public SynchronizedGraphParams()
    {
        cacheEnable = false;
    }

    /**
     * Make the AsSynchronizedGraph to be created <strong>not</strong> use cache for <code>edgesOf</code>,
     * <code>incomingEdgesOf</code> and <code>outgoingEdgesOf</code> methods.
     *
     * @return the SynchronizedGraphParams
     */
    public SynchronizedGraphParams cacheDisable()
    {
        cacheEnable = false;
        return this;
    }

    /**
     * Make the AsSynchronizedGraph to be created use cache for <code>edgesOf</code>,
     * <code>incomingEdgesOf</code> and <code>outgoingEdgesOf</code> methods.
     *
     * @return the SynchronizedGraphParams
     */
    public SynchronizedGraphParams cacheEnable()
    {
        cacheEnable = true;
        return this;
    }

    /**
     * Return whether a cache will be used for AsSynchronizedGraph to be created.
     *
     * @return <tt>true</tt> if cache will be used, <tt>false</tt> if cache will not be use
     */
    public boolean isCacheEnable() {
        return cacheEnable;
    }
}
