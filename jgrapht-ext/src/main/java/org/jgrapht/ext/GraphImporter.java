/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
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
/* ------------------
 * GraphImporter.java
 * ------------------
 * (C) Copyright 2016-2016, by Dimitrios Michail and Contributors.
 *
 * Original Author:  Dimitrios Michail
 *
 * Changes
 * -------
 * 17-Aug-2016 : Initial Version (DM);
 *
 */
package org.jgrapht.ext;

import java.io.InputStream;
import java.io.Reader;

import org.jgrapht.Graph;

/**
 * Interface for graph importers
 */
public interface GraphImporter<V, E>
{

    /**
     * Import a graph
     * 
     * @param in the input stream
     * @param g the graph
     * @throws ImportException in case any error occurs
     */
    void read(InputStream in, Graph<V, E> g)
        throws ImportException;

    /**
     * Import a graph
     * 
     * @param in the input reader
     * @param g the graph
     * @throws ImportException in case any error occurs
     */
    void read(Reader in, Graph<V, E> g)
        throws ImportException;

}

// End GraphImporter.java