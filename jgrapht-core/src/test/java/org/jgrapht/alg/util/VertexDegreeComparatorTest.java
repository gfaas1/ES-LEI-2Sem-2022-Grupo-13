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
/* -----------------
 * VertexDegreeComparatorTest.java
 * -----------------
 * (C) Copyright 2016, by Joris Kinable and Contributors.
 *
 * Original Author:  Joris Kinable
 * Contributor(s):   -
 *
 * $Id$
 *
 * Changes
 * -------
 * 28-Jul-2016 : Initial revision (JK);
 *
 */
package org.jgrapht.alg.util;

import junit.framework.TestCase;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.VertexFactory;
import org.jgrapht.generate.RandomGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Unit tests for VertexDegreeComparator
 *
 * @author Joris Kinable
 */
public class VertexDegreeComparatorTest extends TestCase {

    protected static final int TEST_REPEATS = 20;

    private RandomGraphGenerator<Integer, DefaultEdge> randomGraphGenerator;

    public VertexDegreeComparatorTest(){
        randomGraphGenerator=new RandomGraphGenerator<>(100, 1000, 0);
    }

    public void testVertexDegreeComparator(){
        for(int repeat=0; repeat<TEST_REPEATS; repeat++) {
            UndirectedGraph<Integer, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
            randomGraphGenerator.generateGraph(graph, new IntegerVertexFactory(), new HashMap<>());
            List<Integer> vertices = new ArrayList<>(graph.vertexSet());
            //Sort in ascending vertex degree
            Collections.sort(vertices, new VertexDegreeComparator<>(graph, VertexDegreeComparator.Order.ASCENDING));
            for (int i = 0; i < vertices.size() - 1; i++)
                assertTrue(graph.degreeOf(vertices.get(i)) <= graph.degreeOf(vertices.get(i + 1)));

            //Sort in descending vertex degree
            Collections.sort(vertices, new VertexDegreeComparator<>(graph, VertexDegreeComparator.Order.DESCENDING));
            for (int i = 0; i < vertices.size() - 1; i++)
                assertTrue(graph.degreeOf(vertices.get(i)) >= graph.degreeOf(vertices.get(i + 1)));
        }

    }


    public class IntegerVertexFactory implements VertexFactory<Integer>{
        private int vertices=0;

        @Override
        public Integer createVertex() {
            return vertices++;
        }
    }
}
