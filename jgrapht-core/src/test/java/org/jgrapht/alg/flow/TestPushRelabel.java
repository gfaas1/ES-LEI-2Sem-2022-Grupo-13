package org.jgrapht.alg.flow;

import junit.framework.TestCase;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

/**
 * Created by vernemat on 23/05/2017.
 */
public class TestPushRelabel extends TestCase {

    public void testPushRelabelWithNonIdenticalNode() {
        SimpleDirectedGraph<String,DefaultEdge> g1 = new SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge.class) ;

        g1.addVertex("v0");
        g1.addVertex("v1");
        g1.addVertex("v2");
        g1.addVertex("v3");
        g1.addVertex("v4");
        g1.addEdge("v0","v2");
        g1.addEdge("v3","v4");
        g1.addEdge("v1","v0");
        g1.addEdge("v0","v4");
        g1.addEdge("v0","v1");
        g1.addEdge("v2","v1");

        MaximumFlowAlgorithm<String, DefaultEdge> mf1 = new PushRelabelMFImpl(g1);
        String sourceFlow = "v" + new String("v3").substring(1) ;
        String sinkFlow = "v0" ;
        double flow = mf1.calculateMaximumFlow(sourceFlow,sinkFlow);
        assertEquals(0.0, flow);
    }
}
