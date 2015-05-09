/**
 * 
 */
package org.jgrapht.experimental.subgraphisomorphism;

import static org.junit.Assert.*;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.junit.Test;

/**
 * @author fabian
 *
 */
public class VF2SubgraphIsomorphismInspectorTest {

	@Test
	public void test1() {
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
	
	@Test
	public void test2() {
		Random rnd = new Random();
		rnd.setSeed(99999);
		
		for (int i = 1; i < 50; i++)	{
			int vertexCount = 2 + rnd.nextInt(i),
				edgeCount = rnd.nextInt(vertexCount * (vertexCount - 1)),
				subVertexCount = 1 + rnd.nextInt(vertexCount);

			System.out.print(i + ": " + vertexCount + "v, " + edgeCount + "e ");
			assertEquals(true, singleTest(vertexCount, edgeCount, subVertexCount, i));
		}
	}
	
	
	private boolean singleTest(
			int vertexCount,
			int edgeCount,
			int subVertexCount,
			long seed)
	{
		System.out.print(".");
		DirectedGraph<String, DefaultEdge> g1 = randomGraph(vertexCount, edgeCount, seed);
		SubgraphWithString sgws = randomSubgraph(g1, subVertexCount, seed);
		
		VF2SubgraphIsomorphismInspector<String, DefaultEdge> vf2 =
				new VF2SubgraphIsomorphismInspector<String, DefaultEdge>(g1, sgws.graph);
		
		System.out.print(".");
		boolean isCorrect = vf2.hasNext();
		
		for (;vf2.hasNext();)
		{
			isCorrect = isCorrect && isCorrectMatching(g1, sgws.graph, vf2.next());
			System.out.print(".");
		}
		System.out.println("");
		
		return isCorrect;
	}
	
	private boolean isCorrectMatching(
			DirectedGraph<String, DefaultEdge> g1,
			DirectedGraph<String, DefaultEdge> g2,
			SubgraphIsomorphismRelation<String, DefaultEdge> rel)
	{
		Set<String> vertexSet = g2.vertexSet();
		
		for (String u1 : vertexSet)	{
			String v1 = rel.getVertexCorrespondence(u1, false);
			
			for (String u2 : vertexSet)	{
				if (u1 == u2)
					continue;
				
				String v2 = rel.getVertexCorrespondence(u2, false);
				
				if (v1 == v2)
					return false;
				
				if (g1.containsEdge(v1, v2) != g2.containsEdge(u1, u2))
					return false;
			}
		}
		
		return true;
	}
	
	private DirectedGraph<String, DefaultEdge> randomGraph(int vertexCount, int edgeCount, long seed)	{
		String[] vertexes = new String[vertexCount];
		DirectedGraph<String, DefaultEdge> g =
				new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		
		for (int i = 0; i < vertexCount; i++)
			g.addVertex(vertexes[i] = "v" + i);
		
		Random rnd = new Random();
		rnd.setSeed(seed);
		
		for (int i = 0; i < edgeCount;)	{
			String source = vertexes[rnd.nextInt(vertexCount)],
				   target = vertexes[rnd.nextInt(vertexCount)];
			
			if (source != target && !g.containsEdge(source, target))	{
				g.addEdge(source, target);
				i++;
			}
		}
		
		return g;
	}
	
	private SubgraphWithString randomSubgraph(
				DirectedGraph<String, DefaultEdge> g1,
				int vertexCount,
				long seed)
	{
		Map<String, String> map = new HashMap<String, String>();
		DirectedGraph<String, DefaultEdge> g2 =
				new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		Set<String> vertexSet = g1.vertexSet();
		int n = vertexSet.size();
		
		Random rnd = new Random();
		rnd.setSeed(seed);
		
		for (int i = 0; i < vertexCount;)	{
			for (String v : vertexSet)	{
				if (rnd.nextInt(n) == 0 && !map.containsKey(v))	{
					String u = "u" + (i++);
					g2.addVertex(u);
					map.put(v, u);
				}
			}
		}
		
		for (DefaultEdge e : g1.edgeSet())	{
			String v1 = g1.getEdgeSource(e),
				   v2 = g1.getEdgeTarget(e);
			if (map.containsKey(v1) && map.containsKey(v2))	{
				String u1 = map.get(v1),
					   u2 = map.get(v2);
				g2.addEdge(u1, u2);
			}
		}
		
		
		String str = "[";
		
		TreeSet<String> vertexTree = new TreeSet<String>(vertexSet);
		for (String v : vertexTree)	{
			str += v.toString() + "=";
			if (map.containsKey(v))	{
				str += map.get(v);
			} else {
				str += "~~";
			}
			str += " ";
		}
		
		str = str.substring(0, str.length()-1) + "]";	
		
		
		return new SubgraphWithString(g2, str);
	}
	
	
	private class SubgraphWithString
	{
		public DirectedGraph<String, DefaultEdge>graph;
		       String str;
		
		public SubgraphWithString(
				DirectedGraph<String, DefaultEdge> graph,
				String str)
		{
			this.graph = graph;
			this.str = str;
		}
	}

}
