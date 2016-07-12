package org.jgrapht.traverse;

import java.util.Iterator;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EnhancedTestCase;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.VertexFactory;
import org.jgrapht.generate.RingGraphGenerator;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class RandomWalkIteratorTest extends EnhancedTestCase {

	/**
	 * 
	 */
	public void testEmptyGraph() {
		DirectedGraph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
		Iterator<String> iter = new RandomWalkIterator<>(graph);
		assertFalse(iter.hasNext());
	}
	
	/**
	 * 
	 */
	public void testSink() {
		DirectedGraph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);		
		graph.addVertex("123");
		Iterator<String> iter = new RandomWalkIterator<>(graph);
		assertTrue(iter.hasNext());
		assertEquals("123", iter.next());
		assertFalse(iter.hasNext());
	}
	
	/**
	 * 
	 */
	public void testExhausted() {
		UndirectedGraph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
		RingGraphGenerator<String, DefaultEdge> graphGenerator = new RingGraphGenerator<>(10);
		graphGenerator.generateGraph(graph, new VertexFactory<String>() {
			private int index = 1;
			@Override
			public String createVertex() {
				return String.valueOf(index++);
			}
		}, null);
		
		int maxSteps = 4;
		Iterator<String> iter = new RandomWalkIterator<>(graph, "1", false, maxSteps);
		for (int i = 0; i < maxSteps; i++) {
			assertTrue(iter.hasNext());
			assertNotNull(iter.next());
		}
		assertFalse(iter.hasNext());
	}
	
	/**
	 * 
	 */
	public void testDeterministic() {
		DirectedGraph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);	
		int ringSize = 5;
		RingGraphGenerator<String, DefaultEdge> graphGenerator = new RingGraphGenerator<>(ringSize);
		graphGenerator.generateGraph(graph, new VertexFactory<String>() {
			private int index = 0;
			@Override
			public String createVertex() {
				return String.valueOf(index++);
			}
		}, null);
		Iterator<String> iter = new RandomWalkIterator<>(graph, "0", false, 20);
		int step = 0;
		while(iter.hasNext()) {
			step++;
			assertEquals(String.valueOf(step % ringSize), iter.next());
		}
	}
}
