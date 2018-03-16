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
 * The SynchronizedGraph Params describes the properties of a synchronized graph being created such
 * as whether it uses cache and fair mode.
 *
 * @author CHEN Kui
 * @since Mar 9, 2018
 */
public class SynchronizedGraphParams
{
    private boolean cacheEnable;
    private boolean fair;

    /**
     * Constructor for SynchronizedGraphParams with cacheDisable and non-fair mode.
     */
    public SynchronizedGraphParams()
    {
        cacheEnable = false;
        fair = false;
    }

    /**
     * Request a synchronized graph using non-fair mode without caching.
     *
     * @return the SynchronizedGraphParams
     */
    public SynchronizedGraphParams cacheDisable()
    {
        cacheEnable = false;
        fair = false;
        return this;
    }

    /**
     * Request a synchronized graph with caching.
     *
     * @return the SynchronizedGraphParams
     */
    public SynchronizedGraphParams cacheEnable()
    {
        cacheEnable = true;
        return this;
    }

    /**
     * Return whether a cache will be used for the synchronized graph being created.
     *
     * @return <tt>true</tt> if cache will be used, <tt>false</tt> if cache will not be use
     */
    public boolean isCacheEnable() {
        return cacheEnable;
    }

    /**
     * Request a synchronized graph with fair mode.
     * @return the SynchronizedGraphParams
     */
    public SynchronizedGraphParams setFair()
    {
        fair = true;
        return this;
    }

    /**
     * Request a synchronized graph with non-fair mode.
     * @return the SynchronizedGraphParams
     */
    public SynchronizedGraphParams setNonfair()
    {
        fair = false;
        return this;
    }

    /**
     *
     * @return <tt>true</tt> if constructed as fair mode, <tt>false</tt> if non-fair
     */
    public boolean isFair()
    {
        return fair;
    }
}
