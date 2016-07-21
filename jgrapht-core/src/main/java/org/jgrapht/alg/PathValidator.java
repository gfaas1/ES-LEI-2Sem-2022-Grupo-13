/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2016, by Barak Naveh and Contributors.
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
/* ----------------
 * PathValidator.java
 * ----------------
 * (C) Copyright 2016-, by Assaf Mizrachi and Contributors.
 *
 * Original Author:  Assaf Mizrachi
 * Contributor(s):   
 *
 * $Id$
 *
 * Changes
 * -------
 *
 */
package org.jgrapht.alg;

/**
 * May be used  to provide external path validations in addition to the basic
 * validations done by {@link KShortestPaths} - that the path is from source
 * to target and that it does not contain loops.
 * 
 * @author Assaf Mizrachi
 * @since July, 21, 2016
 *
 */
public interface PathValidator<V, E> {

    /**
     * Checks if an edge can be added to a previous path element.
     * 
     * @param prevPathElement the previous path element
     * @param edge the edge to be added to the path.
     * 
     * @return <code>true</code> if edge can be added, <code>false</code> otherwise.
     */
    public boolean isNotValidPath(AbstractPathElement<V, E> prevPathElement, E edge);
}
