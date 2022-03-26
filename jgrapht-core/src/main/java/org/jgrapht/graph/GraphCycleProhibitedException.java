/*
 * (C) Copyright 2021-2021, by Magnus Gunnarsson and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * See the CONTRIBUTORS.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the
 * GNU Lesser General Public License v2.1 or later
 * which is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1-standalone.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR LGPL-2.1-or-later
 */
package org.jgrapht.graph;

/**
 * Exception indicating that the vertexes supplied to {@link DirectedAcyclicGraph} would cause a
 * cycle.
 * 
 * @author EnderCrypt (Magnus Gunnarsson)
 */
public class GraphCycleProhibitedException
    extends
    IllegalArgumentException
{
    private static final long serialVersionUID = 2440845437318796595L;

    public GraphCycleProhibitedException()
    {
        super("Edge would induce a cycle");
    }
}
