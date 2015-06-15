package org.jgrapht.alg.isomorphism;

import static org.junit.Assert.*;

import java.lang.Object;
import java.util.*;

import com.sun.javaws.exceptions.InvalidArgumentException;
import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.omg.CORBA.*;


public class VF2SubgraphIsomorphismInspectorTest {


    @Rule
    public ExpectedException thrown  = ExpectedException.none();


    /**
     * Tests graph types: In case of invalid graph types or invalid
     * combination of graph arguments UnsupportedOperationException or
     * InvalidArgumentException is expected
     */
    @Test
    public void testGraphTypes()    {

        DirectedGraph<Integer, DefaultEdge> dg1 =
                new DefaultDirectedGraph<Integer, DefaultEdge>
                        (DefaultEdge.class);

        dg1.addVertex(1);
        dg1.addVertex(2);

        dg1.addEdge(1, 2);


        SimpleGraph<Integer, DefaultEdge> sg1 =
                new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        sg1.addVertex(1);
        sg1.addVertex(2);

        sg1.addEdge(1, 2);


        Multigraph<Integer, DefaultEdge> mg1 =
                new Multigraph<Integer, DefaultEdge>(DefaultEdge.class);

        mg1.addVertex(1);
        mg1.addVertex(2);

        mg1.addEdge(1, 2);


        Pseudograph<Integer, DefaultEdge> pg1 =
                new Pseudograph<Integer, DefaultEdge>(DefaultEdge.class);

        pg1.addVertex(1);
        pg1.addVertex(2);

        pg1.addEdge(1, 2);

        /* GT-1 */
        thrown.expect(UnsupportedOperationException.class);
        @SuppressWarnings("unused")
        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> gt1 =
                new VF2SubgraphIsomorphismInspector<Integer,DefaultEdge>
                        (mg1,mg1);

        /* GT-2 */
        thrown.expect(UnsupportedOperationException.class);
        @SuppressWarnings("unused")
        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> gt2 =
                    new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                            (pg1,pg1);

        /* GT-3 */
        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> gt3 =
           new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                       (sg1,sg1);
        assertEquals("[1=1 2=2]", gt3.getMappings().next().toString());

        /* GT-4 */
        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> gt4 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                        (dg1,dg1);
        assertEquals("[1=1 2=2]", gt4.getMappings().next().toString());

        /* GT-5 */
        thrown.expect(UnsupportedOperationException.class);
        @SuppressWarnings("unused")
        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> gt5 =
                    new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                            (sg1,mg1);

        /* GT-6 */
        thrown.expect(UnsupportedOperationException.class);
        @SuppressWarnings("unused")
        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> gt6 =
                    new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                            (sg1,pg1);

        /* GT-7 */
        thrown.expect(UnsupportedOperationException.class);
        @SuppressWarnings("unused")
        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> gt7 =
                    new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                            (dg1,mg1);

        /* GT-8 */
        thrown.expect(UnsupportedOperationException.class);
        @SuppressWarnings("unused")
        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> gt8 =
                    new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                            (dg1,pg1);

        /* GT-9 */
        thrown.expect(UnsupportedOperationException.class);
        @SuppressWarnings("unused")
        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> gt9 =
              new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                            (pg1,mg1);

        /* GT-10 */
        thrown.expect(IllegalArgumentException.class);
        @SuppressWarnings("unused")
        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> gt10 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(sg1, dg1);

    }


    /**
    * Tests edge cases on simple graphs
    */
    @Test
    public void testEdgeCasesSimpleGraph()    {

        /* ECS-1: graph and subgraph empty */

        SimpleGraph<Integer, DefaultEdge> sg0v =
            new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class),
                                          sg0v2 =
            new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs1 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                    (sg0v, sg0v2);

        assertEquals("[]", vfs1.getMappings().next().toString());


        /* ECS-2: graph non-empty, subgraph empty */

        SimpleGraph<Integer, DefaultEdge> sg4v3e =
            new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        sg4v3e.addVertex(1);
        sg4v3e.addVertex(2);
        sg4v3e.addVertex(3);
        sg4v3e.addVertex(4);

        sg4v3e.addEdge(1, 2);
        sg4v3e.addEdge(3, 2);
        sg4v3e.addEdge(3, 4);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs2 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                    (sg4v3e, sg0v);

        assertEquals("[1=~~ 2=~~ 3=~~ 4=~~]",
                        vfs2.getMappings().next().toString());


        /* ECS-3: graph empty, subgraph non-empty */

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs3 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                    (sg0v, sg4v3e);

        assertEquals(false, vfs3.isomorphismExists());


        /* ECS-4: graph non-empty, subgraph single vertex */

        SimpleGraph<Integer, DefaultEdge> sg1v =
                new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        sg1v.addVertex(5);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs4 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                    (sg4v3e, sg1v);

        Iterator<IsomorphicGraphMapping<Integer, DefaultEdge>> iter =
            vfs4.getMappings();

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


        /* ECS-5: graph empty, subgraph single vertex */

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs5 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                    (sg0v, sg1v);

        assertEquals(false, vfs5.isomorphismExists());


        /* ECS-6: subgraph with vertices, but no edges */

        SimpleGraph<Integer, DefaultEdge> sg3v0e =
                new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        sg3v0e.addVertex(5);
        sg3v0e.addVertex(6);
        sg3v0e.addVertex(7);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs6 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                    (sg4v3e, sg3v0e);

        assertEquals(false, vfs6.isomorphismExists());

        /* ECS-7: graph and subgraph with vertices, but no edges */

        SimpleGraph<Integer, DefaultEdge> sg2v0e =
                new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        sg2v0e.addVertex(1);
        sg2v0e.addVertex(2);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs7 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                        (sg3v0e, sg2v0e);

        Iterator<IsomorphicGraphMapping<Integer, DefaultEdge>> iter7 =
                vfs7.getMappings();

        Set<String> mappings7 =
                new HashSet<String>(Arrays.asList("[5=1 6=2 7=~~]",
                        "[5=1 6=~~ 7=2]",
                        "[5=2 6=1 7=~~]",
                        "[5=~~ 6=1 7=2]",
                        "[5=2 6=~~ 7=1]",
                        "[5=~~ 6=2 7=1]"));
        assertEquals(true, mappings7.remove(iter7.next().toString()));
        assertEquals(true, mappings7.remove(iter7.next().toString()));
        assertEquals(true, mappings7.remove(iter7.next().toString()));
        assertEquals(true, mappings7.remove(iter7.next().toString()));
        assertEquals(true, mappings7.remove(iter7.next().toString()));
        assertEquals(true, mappings7.remove(iter7.next().toString()));
        assertEquals(false, iter7.hasNext());


        /* ECS-8: graph no edges, subgraph contains single edge */

        SimpleGraph<Integer, DefaultEdge> sg2v1e =
            new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        sg2v1e.addVertex(5);
        sg2v1e.addVertex(6);

        sg2v1e.addEdge(5, 6);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs8 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                    (sg3v0e, sg2v1e);

        assertEquals(false, vfs8.isomorphismExists());


        /* ECS-9: complete graphs of different size,
        * graph smaller than subgraph*/

        SimpleGraph<Integer, DefaultEdge> sg5c =
                    new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        sg5c.addVertex(0);
        sg5c.addVertex(1);
        sg5c.addVertex(2);
        sg5c.addVertex(3);
        sg5c.addVertex(4);

        sg5c.addEdge(0, 1);
        sg5c.addEdge(0, 2);
        sg5c.addEdge(0, 3);
        sg5c.addEdge(0, 4);
        sg5c.addEdge(1, 2);
        sg5c.addEdge(1, 3);
        sg5c.addEdge(1, 4);
        sg5c.addEdge(2, 3);
        sg5c.addEdge(2, 4);
        sg5c.addEdge(3, 4);

        SimpleGraph<Integer, DefaultEdge> sg4c =
                new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        sg4c.addVertex(0);
        sg4c.addVertex(1);
        sg4c.addVertex(2);
        sg4c.addVertex(3);

        sg4c.addEdge(0, 1);
        sg4c.addEdge(0, 2);
        sg4c.addEdge(0, 3);
        sg4c.addEdge(1, 2);
        sg4c.addEdge(1, 3);
        sg4c.addEdge(2, 3);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs9 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                        (sg4c, sg5c);

        assertEquals(false, vfs9.isomorphismExists());

        /* ECS-10: complete graphs of different size,
        * graph bigger than subgraph*/

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs10 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                    (sg5c, sg4c);

        // first mapping found
        assertEquals("[0=0 1=1 2=2 3=3 4=~~]",
        vfs10.getMappings().next().toString());


        /* ECS-11: isomorphic graphs */

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs11 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                        (sg4v3e, sg4v3e);

        // first mapping found
        assertEquals("[1=1 2=2 3=3 4=4]",
                vfs11.getMappings().next().toString());
        /* assertEquals(true,
            SubgraphIsomorphismTestUtils.containsAllMatchings(vfs11,
                    sg4v3e, sg4v3e)); */

        /* ECS-12: not connected graphs of different size */
        SimpleGraph<Integer, DefaultEdge> sg6v4enc =
                new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        sg6v4enc.addVertex(0);
        sg6v4enc.addVertex(1);
        sg6v4enc.addVertex(2);
        sg6v4enc.addVertex(3);
        sg6v4enc.addVertex(4);
        sg6v4enc.addVertex(5);

        sg6v4enc.addEdge(1, 2);
        sg6v4enc.addEdge(2, 3);
        sg6v4enc.addEdge(3, 1);
        sg6v4enc.addEdge(4, 5);

        SimpleGraph<Integer, DefaultEdge> sg5v4enc =
                new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        sg5v4enc.addVertex(6);
        sg5v4enc.addVertex(7);
        sg5v4enc.addVertex(8);
        sg5v4enc.addVertex(9);
        sg5v4enc.addVertex(10);

        sg5v4enc.addEdge(7, 6);
        sg5v4enc.addEdge(9, 8);
        sg5v4enc.addEdge(10, 9);
        sg5v4enc.addEdge(8, 10);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vfs12 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                        (sg6v4enc, sg5v4enc);

        // first mapping found
        assertEquals("[0=~~ 1=8 2=9 3=10 4=6 5=7]",
                vfs12.getMappings().next().toString());


    }


    /* Tests edge cases on directed graphs
     */
    @Test
    public void testEdgeCasesDirectedGraph()    {

        /* ECD-1: graph and subgraph empty */

        DirectedGraph<Integer, DefaultEdge> dg0v =
                new DefaultDirectedGraph<Integer, DefaultEdge>
                    (DefaultEdge.class),
                                            dg0v2 =
                new DefaultDirectedGraph<Integer, DefaultEdge>
                    (DefaultEdge.class);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf1 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                    (dg0v, dg0v2);

        assertEquals("[]", vf1.getMappings().next().toString());


        /* ECD-2: graph non-empty, subgraph empty */

        DirectedGraph<Integer, DefaultEdge> dg4v3e =
                new DefaultDirectedGraph<Integer, DefaultEdge>
                        (DefaultEdge.class);

        dg4v3e.addVertex(1);
        dg4v3e.addVertex(2);
        dg4v3e.addVertex(3);
        dg4v3e.addVertex(4);

        dg4v3e.addEdge(1, 2);
        dg4v3e.addEdge(3, 2);
        dg4v3e.addEdge(3, 4);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf2 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                        (dg4v3e, dg0v);

        assertEquals("[1=~~ 2=~~ 3=~~ 4=~~]",
                vf2.getMappings().next().toString());


        /* ECD-3: graph empty, subgraph non-empty */

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf3 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                        (dg0v, dg4v3e);

        assertEquals(false, vf3.isomorphismExists());


        /* ECD-4: graph non-empty, subgraph single vertex */

        DirectedGraph<Integer, DefaultEdge> dg1v =
                new DefaultDirectedGraph<Integer, DefaultEdge>
                        (DefaultEdge.class);

        dg1v.addVertex(5);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf4 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                        (dg4v3e, dg1v);

        Iterator<IsomorphicGraphMapping<Integer, DefaultEdge>> iter4 =
                vf4.getMappings();

        Set<String> mappings =
                new HashSet<String>(Arrays.asList("[1=5 2=~~ 3=~~ 4=~~]",
                        "[1=~~ 2=5 3=~~ 4=~~]",
                        "[1=~~ 2=~~ 3=5 4=~~]",
                        "[1=~~ 2=~~ 3=~~ 4=5]"));
        assertEquals(true, mappings.remove(iter4.next().toString()));
        assertEquals(true, mappings.remove(iter4.next().toString()));
        assertEquals(true, mappings.remove(iter4.next().toString()));
        assertEquals(true, mappings.remove(iter4.next().toString()));
        assertEquals(false, iter4.hasNext());


        /* ECD-5: graph empty, subgraph single vertex */

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf5 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                        (dg0v, dg1v);

        assertEquals(false, vf5.isomorphismExists());


        /* ECD-6: subgraph with vertices, but no edges */

        DirectedGraph<Integer, DefaultEdge> dg3v0e =
                new DefaultDirectedGraph<Integer, DefaultEdge>
                        (DefaultEdge.class);

        dg3v0e.addVertex(5);
        dg3v0e.addVertex(6);
        dg3v0e.addVertex(7);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf6 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                        (dg4v3e, dg3v0e);

        assertEquals(false, vf6.isomorphismExists());

        /* ECD-7: graph and subgraph with vertices, but no edges */

        DirectedGraph<Integer, DefaultEdge> dg2v0e =
                new DefaultDirectedGraph<Integer, DefaultEdge>
                        (DefaultEdge.class);

        dg2v0e.addVertex(1);
        dg2v0e.addVertex(2);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf7 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                        (dg3v0e, dg2v0e);

        Iterator<IsomorphicGraphMapping<Integer, DefaultEdge>> iter7 =
                vf7.getMappings();

        Set<String> mappings7 =
                new HashSet<String>(Arrays.asList("[5=1 6=2 7=~~]",
                        "[5=1 6=~~ 7=2]",
                        "[5=2 6=1 7=~~]",
                        "[5=~~ 6=1 7=2]",
                        "[5=2 6=~~ 7=1]",
                        "[5=~~ 6=2 7=1]"));
        assertEquals(true, mappings7.remove(iter7.next().toString()));
        assertEquals(true, mappings7.remove(iter7.next().toString()));
        assertEquals(true, mappings7.remove(iter7.next().toString()));
        assertEquals(true, mappings7.remove(iter7.next().toString()));
        assertEquals(true, mappings7.remove(iter7.next().toString()));
        assertEquals(true, mappings7.remove(iter7.next().toString()));
        assertEquals(false, iter7.hasNext());


        /* ECD-8: graph no edges, subgraph contains single edge */

        DirectedGraph<Integer, DefaultEdge> dg2v1e =
                new DefaultDirectedGraph<Integer, DefaultEdge>
                        (DefaultEdge.class);

        dg2v1e.addVertex(5);
        dg2v1e.addVertex(6);

        dg2v1e.addEdge(5, 6);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf8 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                        (dg3v0e, dg2v1e);

        assertEquals(false, vf8.isomorphismExists());


        /* ECD-9: complete graphs of different size,
        * graph smaller than subgraph*/

        DirectedGraph<Integer, DefaultEdge> dg5c =
                new DefaultDirectedGraph<Integer, DefaultEdge>
                        (DefaultEdge.class);

        dg5c.addVertex(0);
        dg5c.addVertex(1);
        dg5c.addVertex(2);
        dg5c.addVertex(3);
        dg5c.addVertex(4);

        dg5c.addEdge(0, 1);
        dg5c.addEdge(0, 2);
        dg5c.addEdge(0, 3);
        dg5c.addEdge(0, 4);
        dg5c.addEdge(1, 2);
        dg5c.addEdge(1, 3);
        dg5c.addEdge(1, 4);
        dg5c.addEdge(2, 3);
        dg5c.addEdge(2, 4);
        dg5c.addEdge(3, 4);

        DirectedGraph<Integer, DefaultEdge> dg4c =
                new DefaultDirectedGraph<Integer, DefaultEdge>
                        (DefaultEdge.class);

        dg4c.addVertex(0);
        dg4c.addVertex(1);
        dg4c.addVertex(2);
        dg4c.addVertex(3);

        dg4c.addEdge(0, 1);
        dg4c.addEdge(0, 2);
        dg4c.addEdge(0, 3);
        dg4c.addEdge(1, 2);
        dg4c.addEdge(1, 3);
        dg4c.addEdge(2, 3);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf9 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                        (dg4c, dg5c);

        assertEquals(false, vf9.isomorphismExists());

        /* ECD-10: complete graphs of different size,
        * graph bigger than subgraph*/

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf10 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                        (dg5c, dg4c);

        // first mapping found
        assertEquals("[0=0 1=1 2=2 3=3 4=~~]",
                vf10.getMappings().next().toString());


        /* ECD-11: isomorphic graphs */

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf11 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                        (dg4v3e, dg4v3e);

        // first mapping found
        assertEquals("[1=1 2=2 3=3 4=4]",
                vf11.getMappings().next().toString());
        /* assertEquals(true,
            SubgraphIsomorphismTestUtils.containsAllMatchings(vfs11,
                    sg4v3e, sg4v3e)); */

        /* ECD-12: not connected graphs of different size */
        DirectedGraph<Integer, DefaultEdge> dg6v4enc =
                new DefaultDirectedGraph<Integer, DefaultEdge>
                        (DefaultEdge.class);

        dg6v4enc.addVertex(0);
        dg6v4enc.addVertex(1);
        dg6v4enc.addVertex(2);
        dg6v4enc.addVertex(3);
        dg6v4enc.addVertex(4);
        dg6v4enc.addVertex(5);

        dg6v4enc.addEdge(1, 2);
        dg6v4enc.addEdge(2, 3);
        dg6v4enc.addEdge(3, 1);
        dg6v4enc.addEdge(4, 5);

        DirectedGraph<Integer, DefaultEdge> dg5v4enc =
                new DefaultDirectedGraph<Integer, DefaultEdge>
                        (DefaultEdge.class);

        dg5v4enc.addVertex(6);
        dg5v4enc.addVertex(7);
        dg5v4enc.addVertex(8);
        dg5v4enc.addVertex(9);
        dg5v4enc.addVertex(10);

        dg5v4enc.addEdge(7, 6);
        dg5v4enc.addEdge(9, 8);
        dg5v4enc.addEdge(10, 9);
        dg5v4enc.addEdge(8, 10);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf12 =
                new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>
                        (dg6v4enc, dg5v4enc);

        // first mapping found
        assertEquals("[0=~~ 1=8 2=10 3=9 4=7 5=6]",
                vf12.getMappings().next().toString());

    }



    @Test
    public void testExhaustive() {

        /* DET-1:
         *
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



        /* DET-2: (example from VF2 presentation)
         *
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


        /* DET-3:
         *
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

    /** RG-1:
     * Tests if a all matchings are correct (on some random graphs).
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

            for (Iterator<IsomorphicGraphMapping<Integer, DefaultEdge>>
                    mappings = vf2.getMappings(); mappings.hasNext();) {
                assertEquals(true,
                    SubgraphIsomorphismTestUtils.isCorrectMatching(
                                    mappings.next(), g1, g2));
                SubgraphIsomorphismTestUtils.showLog(".");
            }
            SubgraphIsomorphismTestUtils.showLog("\n");
        }
    }


    /** RG-2:
     * Tests if all matchings are correct and if every matching is found
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
                SubgraphIsomorphismTestUtils.containsAllMatchings
                        (vf2, g1, g2));
        }
    }

    /* HG:
    * meaasures time needed to check a pair of huge random graphs
    *
    * */
    @Test
    public void testHugeGraph() {
        int n = 1000;
        long time = System.currentTimeMillis();

        DirectedGraph<Integer, DefaultEdge> g1 =
            SubgraphIsomorphismTestUtils.randomGraph(n, n*n/50, 12345),
                                            g2 =
            SubgraphIsomorphismTestUtils.randomSubgraph(g1, n/2, 54321);

        VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf2 =
            new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g1, g2);

        assertEquals(true, vf2.isomorphismExists());

        System.out.println("|V1| = " + g1.vertexSet().size() + 
                         ", |E1| = " + g1.edgeSet().size() + 
                         ", |V2| = " + g2.vertexSet().size() + 
                         ", |E2| = " + g2.edgeSet().size() +
                         " - " + (System.currentTimeMillis() - time) + "ms");
    }
}
