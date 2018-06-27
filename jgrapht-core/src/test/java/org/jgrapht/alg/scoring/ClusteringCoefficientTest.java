package org.jgrapht.alg.scoring;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Test;

public class ClusteringCoefficientTest {

    @Test
    public void testSmall(){
        Graph<Integer, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

        for (int i = 1; i <= 8; i++) {
            graph.addVertex(i);
        }

        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 3);
        graph.addEdge(2, 4);
        graph.addEdge(3, 4);
        graph.addEdge(4, 5);
        graph.addEdge(4, 6);
        graph.addEdge(5, 7);
        graph.addEdge(6, 7);
        graph.addEdge(2, 8);
        graph.addEdge(8, 5);

        System.out.println(new ClusteringCoefficient<>(graph).getScores());
    }

}