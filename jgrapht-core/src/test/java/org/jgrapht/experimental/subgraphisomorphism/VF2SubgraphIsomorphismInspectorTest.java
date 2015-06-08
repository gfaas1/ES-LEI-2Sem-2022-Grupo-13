/**
 * 
 */
package org.jgrapht.experimental.subgraphisomorphism;

import static org.junit.Assert.*;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Fabian Späh
 *
 */
public class VF2SubgraphIsomorphismInspectorTest {

	@Test
	public void testSingleMatching() {
		/*
		 *   v1 ---> v2 <---> v3 ---> v4     v5
		 *   
		 */
		DirectedGraph<String, DefaultEdge> g1 =
            new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		
		String v1 = "v1",
			   v2 = "v2",
			   v3 = "v3",
			   v4 = "v4",
			   v5 = "v5";
		
		g1.addVertex(v1);
		g1.addVertex(v2);
		g1.addVertex(v3);
		g1.addVertex(v4);
		g1.addVertex(v5);
		
		g1.addEdge(v1, v2);
		g1.addEdge(v2, v3);
		g1.addEdge(v3, v2);
		g1.addEdge(v3, v4);
		
		
		/*
		 *   v6 <---> v7 <--- v8
		 *   
		 */
		DirectedGraph<String, DefaultEdge> g2 =
			new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		
		String v6 = "v6",
			   v7 = "v7",
			   v8 = "v8";
		
		g2.addVertex(v6);
		g2.addVertex(v7);
		g2.addVertex(v8);
		
		g2.addEdge(v7, v6);
		g2.addEdge(v8, v7);
		g2.addEdge(v6, v7);
		
		
		
		VF2SubgraphIsomorphismInspector<String, DefaultEdge> vf2 =
			new VF2SubgraphIsomorphismInspector<String, DefaultEdge>(g1, g2);
		
		SubgraphIsomorphismRelation<String, DefaultEdge> rel = vf2.next();
		assertEquals("[v1=v8 v2=v7 v3=v6 v4=~~ v5=~~]", rel.toString());
	}
	
	/**
	 * Tests three edge cases: Both graphs empty, second graph empty and first
	 * graph empty.
	 */
	@Test
	public void testEdgeCases()    {
	    /*
	     * Tests with empty graphs
	     */
	    
	    DirectedGraph<String, DefaultEdge> g1 =
            new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class),
                                           g2 =
            new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
	    
	    VF2SubgraphIsomorphismInspector<String, DefaultEdge> vf2 =
            new VF2SubgraphIsomorphismInspector<String, DefaultEdge>(g1, g2);
	    
	    assertEquals("[]", vf2.next().toString());

	    
        
        DirectedGraph<Integer, DefaultEdge> g3 =
            new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class),
                                           g4 =
            new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);
        
        g3.addVertex(1);
        g3.addVertex(2);
        g3.addVertex(3);
        g3.addVertex(4);
        
        g3.addEdge(1, 2);
        g3.addEdge(2, 3);
        g3.addEdge(3, 4);
        
        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf3 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g3, g4);
        
        assertEquals("[1=~~ 2=~~ 3=~~ 4=~~]", vf3.next().toString());

        
        
        DirectedGraph<Integer, DefaultEdge> g5 =
            new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class),
                                           g6 =
            new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);
        
        g6.addVertex(1);
        g6.addVertex(2);
        g6.addVertex(3);
        g6.addVertex(4);
        
        g6.addEdge(1, 2);
        g6.addEdge(2, 3);
        g6.addEdge(3, 4);
        
        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf4 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g5, g6);
        
        assertEquals(false, vf4.hasNext());
	}
	
	@Rule
	public ExpectedException thrown  = ExpectedException.none();
	
	@Test
	public void testExceptions()   {
	    DirectedGraph<Integer, DefaultEdge> g1 = 
            new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);
	    
	    UndirectedGraph<Integer, DefaultEdge> g2 =
            new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
	    
	    thrown.expect(IllegalArgumentException.class);
	    @SuppressWarnings("unused")
        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf2 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g1, g2);
	}

    @Test
    public void testExhaustive() {
        
        /*
         * 
         *      0   3
         *      |  /|        0 2
         * g1 = | 2 |   g2 = |/
         *      |/  |        1
         *      1   4
         */
        
        SimpleGraph<Integer, DefaultEdge> g1 =
                new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class),
                                         g2 = 
                new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        g1.addVertex(0);
        g1.addVertex(1);
        g1.addVertex(2);
        g1.addVertex(3);
        g1.addVertex(4);
        
        g2.addVertex(0);
        g2.addVertex(1);
        g2.addVertex(2);
        
        g1.addEdge(0, 1);
        g1.addEdge(1, 2);
        g1.addEdge(2, 3);
        g1.addEdge(3, 4);

        g2.addEdge(0, 1);
        g2.addEdge(1, 2);
        
        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf2 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g1, g2);
        
        assertEquals(true,
            SubgraphIsomorphismTestUtils.containsAllMatchings(vf2, g1, g2));

        
        
        /*
         * g3 = ...   g4 = ...
         * 
         */
        
        DirectedGraph<Integer, DefaultEdge> g3 =
            new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class),
                                            g4 =
            new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);

        g3.addVertex(0);
        g3.addVertex(1);
        g3.addVertex(2);
        g3.addVertex(3);
        g3.addVertex(4);
        g3.addVertex(5);
        
        g4.addVertex(0);
        g4.addVertex(1);
        g4.addVertex(2);
        g4.addVertex(3);
        
        g3.addEdge(0, 1);
        g3.addEdge(0, 5);
        g3.addEdge(1, 4);
        g3.addEdge(2, 1);
        g3.addEdge(2, 4);
        g3.addEdge(3, 1);
        g3.addEdge(4, 0);
        g3.addEdge(5, 2);
        g3.addEdge(5, 4);
        
        g4.addEdge(0, 3);
        g4.addEdge(1, 2);
        g4.addEdge(1, 3);
        g4.addEdge(2, 3);
        g4.addEdge(2, 0);
        
        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf3 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g3, g4);
        
        assertEquals(true,
            SubgraphIsomorphismTestUtils.containsAllMatchings(vf3, g3, g4));
        
        
        /*
         *      1----0        0---2
         *      |             |  /
         * g5 = |        g6 = | /
         *      |             |/
         *      2----3        1
         */
        
        SimpleGraph<Integer, DefaultEdge> g5 =
            new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class),
                                          g6 =
            new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
        
        g5.addVertex(0);
        g5.addVertex(1);
        g5.addVertex(2);
        g5.addVertex(3);
        
        g6.addVertex(0);
        g6.addVertex(1);
        g6.addVertex(2);

        g5.addEdge(0, 1);
        g5.addEdge(1, 2);
        g5.addEdge(2, 3);

        g6.addEdge(0, 1);
        g6.addEdge(1, 2);
        g6.addEdge(2, 0);
        
        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf4 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g5, g6);
        
        assertEquals(true,
            SubgraphIsomorphismTestUtils.containsAllMatchings(vf4, g5, g6));
    }
    
    /**
     * Tests if a all given matchings are correct (on some random graphs).
     */
    @Test
    public void testRandomGraphs() {
        Random rnd = new Random();
        rnd.setSeed(54321);
        
        for (int i = 1; i < 50; i++)    {
            int vertexCount    = 2 + rnd.nextInt(i),
                edgeCount      = vertexCount + 
                    rnd.nextInt(vertexCount * (vertexCount - 1)) / 2,
                subVertexCount = 1 + rnd.nextInt(vertexCount);
            
            DirectedGraph<Integer, DefaultEdge> g1 =
                SubgraphIsomorphismTestUtils.randomGraph(vertexCount,
                                edgeCount, i),
                                                g2 =
                SubgraphIsomorphismTestUtils.randomSubgraph(g1, subVertexCount,
                                i);
            
            VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf2 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g1,
                                g2);

            SubgraphIsomorphismTestUtils.showLog(i + ": " + vertexCount +
                            "v, " + edgeCount + "e ");
            
            while (vf2.hasNext())   {
                assertEquals(true,
                    SubgraphIsomorphismTestUtils.isCorrectMatching(vf2.next(),
                                    g1, g2));
                SubgraphIsomorphismTestUtils.showLog(".");
            }
            SubgraphIsomorphismTestUtils.showLog("\n");
        }
    }
    
    /**
     * Tests if all given matchings are correct and if every matching is found
     * (on random graphs).
     */
    @Test
    public void testRandomGraphsExhaustive() {
        Random rnd = new Random();
        rnd.setSeed(12345);
        
        for (int i = 1; i < 100; i++)    {
            int vertexCount    = 3 + rnd.nextInt(5),
                edgeCount      = rnd.nextInt(vertexCount * (vertexCount - 1)),
                subVertexCount = 2 + rnd.nextInt(vertexCount),
                subEdgeCount   = rnd.nextInt(subVertexCount * 
                                    (subVertexCount - 1));
            
            DirectedGraph<Integer, DefaultEdge> g1 =
                SubgraphIsomorphismTestUtils.randomGraph(vertexCount,
                                edgeCount, i),
                                                g2 =
                SubgraphIsomorphismTestUtils.randomGraph(subVertexCount,
                                subEdgeCount, i);
            
            VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf2 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g1,
                                g2);

            SubgraphIsomorphismTestUtils.showLog(i + ": " + vertexCount +
                            "v, " + edgeCount + "e ....\n");

            assertEquals(true,
                SubgraphIsomorphismTestUtils.containsAllMatchings(vf2, g1, g2));
        }
    }
}
