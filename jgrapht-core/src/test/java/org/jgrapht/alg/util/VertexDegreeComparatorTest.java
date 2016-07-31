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
 * Created by jkinable on 7/31/16.
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
