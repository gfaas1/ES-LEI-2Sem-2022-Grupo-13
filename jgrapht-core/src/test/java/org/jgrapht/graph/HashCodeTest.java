/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
/* --------------
 * HashCodeTest.java
 * --------------
 * (C) Copyright 2012, by Vladimir Kostyukov and Contributors.
 *
 * Original Author:  Vladimir Kostyukov
 * Contributor(s):   -
 *
 * $Id$
 *
 * Changes
 * -------
 * 22-May-2012 : Initial revision (VK);
 *
 */

package org.jgrapht.graph;

import org.jgrapht.*;

public class HashCodeTest
    extends EnhancedTestCase
{
    //~ Instance fields --------------------------------------------------------

    private String v1 = "v1";
    private String v2 = "v2";
    private String v3 = "v3";
    private String v4 = "v4";

    //~ Constructors -----------------------------------------------------------

    /**
     * @see junit.framework.TestCase#TestCase(java.lang.String)
     */
    public HashCodeTest(String name)
    {
        super(name);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Tests hashCode() method of DefaultDirectedGraph.
     */
    public void testDefaultDirectedGraph()
    {
        DirectedGraph<String, DefaultEdge> g1 =
            new DefaultDirectedGraph<String, DefaultEdge>(
                DefaultEdge.class);
        g1.addVertex(v1);
        g1.addVertex(v2);
        g1.addVertex(v3);
        g1.addVertex(v4);
        g1.addEdge(v1, v2);
        g1.addEdge(v2, v3);
        g1.addEdge(v3, v1);

        DirectedGraph<String, DefaultEdge> g2 = 
             new DefaultDirectedGraph<String, DefaultEdge>(
                 DefaultEdge.class);
        g2.addVertex(v4);
        g2.addVertex(v3);
        g2.addVertex(v2);
        g2.addVertex(v1);
        g2.addEdge(v3, v1);
        g2.addEdge(v2, v3);
        g2.addEdge(v1, v2);

        assertEquals(g1.hashCode(), g2.hashCode());
    }

    /**
     * Tests hashCode() method of SimpleGraph.
     */
    public void testSimpleGraph()
    {
        UndirectedGraph<String, DefaultEdge> g1 =
            new SimpleGraph<String, DefaultEdge>(
                DefaultEdge.class);
        g1.addVertex(v1);
        g1.addVertex(v2);
        g1.addVertex(v3);
        g1.addEdge(v1, v2);
        g1.addEdge(v3, v1);

        UndirectedGraph<String, DefaultEdge> g2 = 
             new SimpleGraph<String, DefaultEdge>(
                 DefaultEdge.class);
        g2.addVertex(v3);
        g2.addVertex(v2);
        g2.addVertex(v1);
        g2.addEdge(v3, v1);
        g2.addEdge(v1, v2);

        assertEquals(g1.hashCode(), g2.hashCode());
    }

    /**
     * Tests hashCode() method for different graphs.
     */
    public void testDifferentGraphs()
    {
        DirectedGraph<String, DefaultEdge> g1 =
            new DefaultDirectedGraph<String, DefaultEdge>(
                DefaultEdge.class);
        g1.addVertex(v1);
        g1.addVertex(v2);
        g1.addVertex(v3);
        g1.addEdge(v1, v2);
        g1.addEdge(v3, v1);

        UndirectedGraph<String, DefaultEdge> g2 = 
             new SimpleGraph<String, DefaultEdge>(
                 DefaultEdge.class);
        g2.addVertex(v3);
        g2.addVertex(v2);
        g2.addVertex(v1);
        g2.addEdge(v3, v1);
        g2.addEdge(v1, v2);

        assertEquals(g1.hashCode(), g2.hashCode());
    }

    /**
     * Tests hashCode() method for graph with non-Intrusive edges.
     */
    public void testGraphsWithNonIntrusiveEdge()
    {
        DirectedGraph<String, String> g1 =
            new DefaultDirectedGraph<String, String>(
                String.class);
        g1.addVertex(v1);
        g1.addVertex(v2);
        g1.addVertex(v3);
        g1.addEdge(v1, v2, v1 + v2);
        g1.addEdge(v3, v1, v3 + v1);

        UndirectedGraph<String, String> g2 = 
             new SimpleGraph<String, String>(
                 String.class);
        g2.addVertex(v3);
        g2.addVertex(v2);
        g2.addVertex(v1);
        g2.addEdge(v3, v1, v3 + v1);
        g2.addEdge(v1, v2, v1 + v2);

        assertEquals(g1.hashCode(), g2.hashCode());
    }

    /**
     * Tests hashCode() method for weighted graphs.
     */
    public void testWeghtedGraphs()
    {
        WeightedGraph<String, DefaultWeightedEdge> g1 =
            new DefaultDirectedWeightedGraph<String, DefaultWeightedEdge>(
                DefaultWeightedEdge.class);
        g1.addVertex(v1);
        g1.addVertex(v2);
        g1.addVertex(v3);
        DefaultWeightedEdge e112 = g1.addEdge(v1, v2);
        DefaultWeightedEdge e131 = g1.addEdge(v3, v1);
        g1.setEdgeWeight(e112, 10.0);
        g1.setEdgeWeight(e131, 20.0);

        WeightedGraph<String, DefaultWeightedEdge> g2 = 
             new SimpleWeightedGraph<String, DefaultWeightedEdge>(
                 DefaultWeightedEdge.class);
        g2.addVertex(v3);
        g2.addVertex(v2);
        g2.addVertex(v1);
        DefaultWeightedEdge e231 = g2.addEdge(v3, v1);
        DefaultWeightedEdge e212 = g2.addEdge(v1, v2);
        g2.setEdgeWeight(e212, 10.0);
        g2.setEdgeWeight(e231, 20.0);

        assertEquals(g1.hashCode(), g2.hashCode());
    }

    /**
     * Tests hashCode() method for pseudo graphs.
     */
    public void testPseudograph() {
        UndirectedGraph<String, DefaultEdge> g1 =
            new Pseudograph<String, DefaultEdge>(DefaultEdge.class);
        g1.addVertex(v1);
        g1.addVertex(v2);
        g1.addVertex(v3);
        g1.addEdge(v1, v2);
        g1.addEdge(v2, v3);
        g1.addEdge(v3, v1);
        g1.addEdge(v1, v2);
        g1.addEdge(v1, v1);

        UndirectedGraph<String, DefaultEdge> g2 =
            new Pseudograph<String, DefaultEdge>(DefaultEdge.class);
        g2.addVertex(v3);
        g2.addVertex(v2);
        g2.addVertex(v1);
        g2.addEdge(v1, v1);
        g2.addEdge(v1, v2);
        g2.addEdge(v3, v1);
        g2.addEdge(v2, v3);
        g2.addEdge(v1, v2);

        assertEquals(g1.hashCode(), g2.hashCode());
    }

    /**
     * Tests hashCode() method for graphs with custom edges.
     */
    public void testGraphsWithCustomEdges() {
        DirectedGraph<String, CustomEdge> g1 =
            new DefaultDirectedGraph<String, CustomEdge>(
                CustomEdge.class);
        g1.addVertex(v1);
        g1.addVertex(v2);
        g1.addVertex(v3);
        g1.addEdge(v1, v2, new CustomEdge("v1-v2"));
        g1.addEdge(v3, v1, new CustomEdge("v3-v1"));

        DirectedGraph<String, CustomEdge> g2 =
            new DefaultDirectedGraph<String, CustomEdge>(
                CustomEdge.class);
        g2.addVertex(v3);
        g2.addVertex(v2);
        g2.addVertex(v1);
        g2.addEdge(v3, v1, new CustomEdge("v3-v1"));
        g2.addEdge(v1, v2, new CustomEdge("v1-v2"));

        assertEquals(g1.hashCode(), g2.hashCode());
    }
 
    /**
     * Custom edge class.
     */
    public static class CustomEdge
        extends DefaultEdge
    {
        private static final long serialVersionUID = 1L;
        private String label;

        public CustomEdge(String label) {
            this.label = label; 
        }

        public int hashCode()
        {
            return label.hashCode();
        }
    }
}
