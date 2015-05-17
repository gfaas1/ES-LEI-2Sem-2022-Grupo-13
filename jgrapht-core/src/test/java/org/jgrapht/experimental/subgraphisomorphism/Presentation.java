/**
 * 
 */
package org.jgrapht.experimental.subgraphisomorphism;

import static org.junit.Assert.*;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Test;

/**
 * @author fabian
 *
 */
public class Presentation {

    @Test
    public void test() {
        DirectedGraph<String, DefaultEdge> g1 =
            new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class),
                                           g2 =
            new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

        String v1 = "v1",
               v2 = "v2",
               v3 = "v3",
               v4 = "v4",
               v5 = "v5",
               v6 = "v6",
               
               w1 = "w1",
               w2 = "w2",
               w3 = "w3",
               w4 = "w4";
        
        g1.addVertex(v1);
        g1.addVertex(v2);
        g1.addVertex(v3);
        g1.addVertex(v4);
        g1.addVertex(v5);
        g1.addVertex(v6);
        
        g2.addVertex(w1);
        g2.addVertex(w2);
        g2.addVertex(w3);
        g2.addVertex(w4);
        
        g1.addEdge(v1, v2);
        g1.addEdge(v1, v6);
        g1.addEdge(v2, v5);
        g1.addEdge(v3, v2);
        g1.addEdge(v3, v5);
        g1.addEdge(v4, v2);
        g1.addEdge(v5, v1);
        g1.addEdge(v6, v3);
        g1.addEdge(v6, v5);
        
        g2.addEdge(w1, w4);
        g2.addEdge(w2, w3);
        g2.addEdge(w2, w4);
        g2.addEdge(w3, w4);
        g2.addEdge(w3, w1);
        
        SubgraphIsomorphismInspector<SubgraphIsomorphismRelation<String, DefaultEdge>>
            insp = new VF2SubgraphIsomorphismInspector<String, DefaultEdge>(g1, g2);
        
        GraphSubgraphMapping<String, DefaultEdge> map = insp.next();
        System.out.println(map);
        assertEquals("[v1=~~ v2=w1 v3=w3 v4=~~ v5=w4 v6=w2]", map.toString());
    }

}
