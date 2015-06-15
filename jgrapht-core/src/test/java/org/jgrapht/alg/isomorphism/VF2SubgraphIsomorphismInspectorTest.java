package org.jgrapht.alg.isomorphism;

import static org.junit.Assert.*;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


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
        
        GraphMapping<String, DefaultEdge> rel = vf2.getMappings().next();
        assertEquals("[v1=v8 v2=v7 v3=v6 v4=~~ v5=~~]", rel.toString());
    }
    
    /**
     * Tests edge cases: Both graphs empty, second graph empty and first
     * graph empty, graph with single node, graph with nodes but no edges
     */
    @Test
    public void testEdgeCasesDirectedGraph() {
        /* graph and subgraph empty */

        DirectedGraph<String, DefaultEdge> g1 =
            new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class),
                                           g2 =
            new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

        VF2SubgraphIsomorphismInspector<String, DefaultEdge> vf2 =
            new VF2SubgraphIsomorphismInspector<String, DefaultEdge>(g1, g2);

        assertEquals("[]", vf2.getMappings().next().toString());


        /* graph non-empty, subgraph empty */

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

        assertEquals("[1=~~ 2=~~ 3=~~ 4=~~]",
                        vf3.getMappings().next().toString());


        /* graph empty, subgraph non-empty */

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf4 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g4, g3);

        assertEquals(false, vf4.isomorphismExists());


        /* graph non-empty, subgraph single vertex */

        DirectedGraph<Integer, DefaultEdge> g5 =
            new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);

        g5.addVertex(5);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf5 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g3, g5);
        
        Iterator<IsomorphicGraphMapping<Integer, DefaultEdge>> iter =
            vf5.getMappings();

        Set<String> mappings = 
            new HashSet<String>(Arrays.asList("[1=5 2=~~ 3=~~ 4=~~]",
                                              "[1=~~ 2=5 3=~~ 4=~~]",
                                              "[1=~~ 2=~~ 3=5 4=~~]",
                                              "[1=~~ 2=~~ 3=~~ 4=5]"));
        assertEquals(true, mappings.remove(iter.next().toString()));
        assertEquals(true, mappings.remove(iter.next().toString()));
        assertEquals(true, mappings.remove(iter.next().toString()));
        assertEquals(true, mappings.remove(iter.next().toString()));
        assertEquals(false, iter.hasNext());


        /* graph empty, subgraph single vertex */

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf5b =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g4, g5);

        assertEquals(false, vf5b.isomorphismExists());


        /* subgraph with vertices, but no edges */

        DirectedGraph<Integer, DefaultEdge> g6 =
            new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);

        g6.addVertex(5);
        g6.addVertex(6);
        g6.addVertex(7);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf6 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g4, g6);
        
        assertEquals(false, vf6.isomorphismExists());


        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf6b =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g3, g6);

        assertEquals(false, vf6b.isomorphismExists());


        /* graph no edges, subgraph contains edge */

        DirectedGraph<Integer, DefaultEdge> g7 =
            new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);

        g7.addVertex(5);
        g7.addVertex(6);

        g7.addEdge(5, 6);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf7 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g6, g7);

        assertEquals(false, vf7.isomorphismExists());


        /* complete graphs of different size */

        DirectedGraph<Integer, DefaultEdge> g8 =
            new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);

        g8.addVertex(0);
        g8.addVertex(1);
        g8.addVertex(2);
        g8.addVertex(3);
        g8.addVertex(4);

        g8.addEdge(0,1);
        g8.addEdge(0,2);
        g8.addEdge(0,3);
        g8.addEdge(0,4);
        g8.addEdge(1,2);
        g8.addEdge(1,3);
        g8.addEdge(1,4);
        g8.addEdge(2,3);
        g8.addEdge(2,4);
        g8.addEdge(3,4);

        DirectedGraph<Integer, DefaultEdge> g9 =
            new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);

        g9.addVertex(0);
        g9.addVertex(1);
        g9.addVertex(2);
        g9.addVertex(3);

        g9.addEdge(0,1);
        g9.addEdge(0,2);
        g9.addEdge(0,3);
        g9.addEdge(1,2);
        g9.addEdge(1,3);
        g9.addEdge(2,3);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf8 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g8, g9);

        SubgraphIsomorphismTestUtils.allMatchingsCorrect(vf8, g8, g9);

        
        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf9 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g9, g8);

        assertEquals(false, vf9.isomorphismExists());


        /* complete graphs of same size */
        
        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf10 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g8, g8);

        SubgraphIsomorphismTestUtils.allMatchingsCorrect(vf10, g8, g8);


        /* complete graphs (??) of different size */
        DirectedGraph<Integer, DefaultEdge> g11 =
            new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);

        g11.addVertex(0);
        g11.addVertex(1);
        g11.addVertex(2);
        g11.addVertex(3);
        g11.addVertex(4);
        g11.addVertex(5);

        g11.addEdge(1,2);
        g11.addEdge(2,3);
        g11.addEdge(3,1);
        g11.addEdge(4, 5);

        DirectedGraph<Integer, DefaultEdge> g12 =
            new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);

        g12.addVertex(6);
        g12.addVertex(7);
        g12.addVertex(8);
        g12.addVertex(9);
        g12.addVertex(10);

        g12.addEdge(7,6);
        g12.addEdge(9,8);
        g12.addEdge(10,9);
        g12.addEdge(8,10);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf13 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g11, g12);

        // is this the only possible mapping?
        assertEquals("[0=~~ 1=8 2=10 3=9 4=7 5=6]",
                        vf13.getMappings().next().toString());
    }

    /**
    * Tests edge cases: Both graphs empty, second graph empty and first
    * graph empty, graph with single node, graph with nodes but no edges
    */
    @Test
    public void testEdgeCasesSimpleGraph()    {
        /* graph and subgraph empty */

        SimpleGraph<Integer, DefaultEdge> sg1 =
            new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class),
                                          sg2 =
            new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs2 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(sg1, sg2);

        assertEquals("[]", vfs2.getMappings().next().toString());


        /* graph non-empty, subgraph empty */

        SimpleGraph<Integer, DefaultEdge> sg3 =
            new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class),
                                          sg4 =
            new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        sg3.addVertex(1);
        sg3.addVertex(2);
        sg3.addVertex(3);
        sg3.addVertex(4);

        sg3.addEdge(1, 2);
        sg3.addEdge(3, 2);
        sg3.addEdge(3, 4);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs3 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(sg3, sg4);

        assertEquals("[1=~~ 2=~~ 3=~~ 4=~~]",
                        vfs3.getMappings().next().toString());


        /* graph empty, subgraph non-empty */

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs4 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(sg4, sg3);

        assertEquals(false, vfs4.isomorphismExists());


        /* graph non-empty, subgraph single vertex */

        SimpleGraph<Integer, DefaultEdge> sg5 =
                new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        sg5.addVertex(5);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs5 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(sg3, sg5);
        
        Iterator<IsomorphicGraphMapping<Integer, DefaultEdge>> iter =
            vfs5.getMappings();

        Set<String> mappings = 
            new HashSet<String>(Arrays.asList("[1=5 2=~~ 3=~~ 4=~~]",
                                              "[1=~~ 2=5 3=~~ 4=~~]",
                                              "[1=~~ 2=~~ 3=5 4=~~]",
                                              "[1=~~ 2=~~ 3=~~ 4=5]"));
        assertEquals(true, mappings.remove(iter.next().toString()));
        assertEquals(true, mappings.remove(iter.next().toString()));
        assertEquals(true, mappings.remove(iter.next().toString()));
        assertEquals(true, mappings.remove(iter.next().toString()));
        assertEquals(false, iter.hasNext());


        /* graph empty, subgraph single vertex */

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs5b =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(sg4, sg5);

        assertEquals(false, vfs5b.isomorphismExists());


        /* subgraph with vertices, but no edges */

        SimpleGraph<Integer, DefaultEdge> sg6 =
                new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        sg6.addVertex(5);
        sg6.addVertex(6);
        sg6.addVertex(7);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs6 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(sg4, sg6);

        assertEquals(false, vfs6.isomorphismExists());


        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs6b =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(sg3, sg6);

        assertEquals(false, vfs6b.isomorphismExists());


        /* graph no edges, subgraph contains edge */

        SimpleGraph<Integer, DefaultEdge> sg7 =
            new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        sg7.addVertex(5);
        sg7.addVertex(6);

        sg7.addEdge(5, 6);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs7 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(sg6, sg7);

        assertEquals(false, vfs7.isomorphismExists());


        /* complete graphs of different size */

        SimpleGraph<Integer, DefaultEdge> sg8 =
                    new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        sg8.addVertex(0);
        sg8.addVertex(1);
        sg8.addVertex(2);
        sg8.addVertex(3);
        sg8.addVertex(4);

        sg8.addEdge(0,1);
        sg8.addEdge(0,2);
        sg8.addEdge(0,3);
        sg8.addEdge(0,4);
        sg8.addEdge(1,2);
        sg8.addEdge(1,3);
        sg8.addEdge(1,4);
        sg8.addEdge(2,3);
        sg8.addEdge(2,4);
        sg8.addEdge(3,4);

        SimpleGraph<Integer, DefaultEdge> sg9 =
                new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        sg9.addVertex(0);
        sg9.addVertex(1);
        sg9.addVertex(2);
        sg9.addVertex(3);

        sg9.addEdge(0,1);
        sg9.addEdge(0,2);
        sg9.addEdge(0,3);
        sg9.addEdge(1,2);
        sg9.addEdge(1,3);
        sg9.addEdge(2,3);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs8 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(sg8, sg9);

        assertEquals(true, 
            SubgraphIsomorphismTestUtils.containsAllMatchings(vfs8, sg8, sg9));


        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs9 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(sg9, sg8);

        assertEquals(false, vfs9.isomorphismExists());


        /* complete graphs of same size */

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs10 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(sg8, sg8);

        assertEquals(true,
            SubgraphIsomorphismTestUtils.containsAllMatchings(vfs10, sg8, sg8));
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
            
            for (Iterator<IsomorphicGraphMapping<Integer, DefaultEdge>> mappings =
                            vf2.getMappings(); mappings.hasNext();)    {
                assertEquals(true,
                    SubgraphIsomorphismTestUtils.isCorrectMatching(
                                    mappings.next(), g1, g2));
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
    
    @Test
    public void testHugeGraph() {
        int n = 700;
        long time = System.currentTimeMillis();
        
        DirectedGraph<Integer, DefaultEdge> g1 =
            SubgraphIsomorphismTestUtils.randomGraph(n, n*n/50, 12345),
                                            g2 =
            SubgraphIsomorphismTestUtils.randomSubgraph(g1, n/2, 54321);
        
        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf2 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g1, g2);
        
        assertEquals(true, vf2.isomorphismExists());
        
        SubgraphIsomorphismTestUtils.showLog(
                        "|V1| = " + g1.vertexSet().size() + 
                      ", |E1| = " + g1.edgeSet().size() + 
                      ", |V2| = " + g2.vertexSet().size() + 
                      ", |E2| = " + g2.edgeSet().size() +
                      " - " + (System.currentTimeMillis() - time) + "ms");
    }
    
    @Test
    public void testSemanticCheck() {
        /*
         *       a---<3>---b
         *       |         |
         * g1 = <4>       <1>   g2 = A---<6>---b---<5>---B
         *       |         |
         *       A---<2>---B
         */
        SimpleGraph<String, Integer> g1 =
            new SimpleGraph<String, Integer>(Integer.class),
                                     g2 =
            new SimpleGraph<String, Integer>(Integer.class);
                                          
        g1.addVertex("a");
        g1.addVertex("b");
        g1.addVertex("A");
        g1.addVertex("B");
        
        g1.addEdge("a", "b", 3);
        g1.addEdge("b", "B", 1);
        g1.addEdge("B", "A", 2);
        g1.addEdge("A", "a", 4);

        g2.addVertex("A");
        g2.addVertex("b");
        g2.addVertex("B");
        
        g2.addEdge("A", "b", 6);
        g2.addEdge("b", "B", 5);
        
        VF2SubgraphIsomorphismInspector<String, Integer> vf2 =
            new VF2SubgraphIsomorphismInspector<String, Integer>(g1, g2,
                            new VertexComp(),
                            new EdgeComp());
        
        Iterator<IsomorphicGraphMapping<String, Integer>> iter =
            vf2.getMappings();

        assertEquals("[A=A B=b a=~~ b=B]", iter.next().toString());
        assertEquals(false, iter.hasNext());
    }
    
    private class VertexComp implements Comparator<String>  {
        @Override
        public int compare(String o1, String o2) {
            if (o1.toLowerCase().equals(o2.toLowerCase()))
                return 0;
            else
                return 1;
        }
    }
    
    private class EdgeComp implements Comparator<Integer>   {
        @Override
        public int compare(Integer o1, Integer o2) {
            return (o1 % 2) - (o2 % 2);
        }
    }
}
