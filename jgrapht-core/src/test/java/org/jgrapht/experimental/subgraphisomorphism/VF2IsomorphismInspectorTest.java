package org.jgrapht.experimental.subgraphisomorphism;

import static org.junit.Assert.*;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Test;

public class VF2IsomorphismInspectorTest {

    @Test
    public void test() {
        /*
         *   v1-----v2
         *    \    /
         *     \  /
         *      v3
         */
        SimpleGraph<String, DefaultEdge> g1 =
            new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
        
        String v1 = "v1",
               v2 = "v2",
               v3 = "v3";
        
        g1.addVertex(v1);
        g1.addVertex(v2);
        g1.addVertex(v3);
        
        g1.addEdge(v1, v2);
        g1.addEdge(v2, v3);
        g1.addEdge(v3, v1);
        
        
        VF2IsomorphismInspector<String, DefaultEdge> vf2 =
            new VF2IsomorphismInspector<String, DefaultEdge>(g1, g1);
        
        SubgraphIsomorphismRelation<String, DefaultEdge> rel = vf2.next();
        assertEquals("[v1=v1 v2=v2 v3=v3]", rel.toString());
        
        
        
        /*
         *   1 ---> 2 <--- 3
         */
        DefaultDirectedGraph<Integer, DefaultEdge> g2 =
            new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);
        
        g2.addVertex(1);
        g2.addVertex(2);
        g2.addVertex(3);
        
        g2.addEdge(1, 2);
        g2.addEdge(3, 2);
        
        
        VF2IsomorphismInspector<Integer, DefaultEdge> vf3 =
            new VF2IsomorphismInspector<Integer, DefaultEdge>(g2, g2);
        assertEquals("[1=1 2=2 3=3]", vf3.next().toString());
        assertEquals("[1=3 2=2 3=1]", vf3.next().toString());
        
    }

}
