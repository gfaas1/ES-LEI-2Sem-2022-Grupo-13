/**
 * 
 */
package org.jgrapht.experimental.subgraphisomorphism;

import static org.junit.Assert.*;

import java.util.*;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Test;

/**
 * @author fabian
 *
 */
public class SubgraphIsomorphismChecker {
	
	@Test
	public void test1()	{
		
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
		
		MatchingChecker m = new MatchingChecker();
		VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> insp =
				new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g1, g2);
		
		assertEquals(true, m.containsAllMatchings(insp, g1, g2));
	}
	
	@Test
	public void test2()	{
		
		/*
		 * ...
		 */
		
		DirectedGraph<Integer, DefaultEdge> g1 =
				new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class),
				                           g2 =
			    new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);

		g1.addVertex(0);
		g1.addVertex(1);
		g1.addVertex(2);
		g1.addVertex(3);
		g1.addVertex(4);
		g1.addVertex(5);
		
		g2.addVertex(0);
		g2.addVertex(1);
		g2.addVertex(2);
		g2.addVertex(3);
		
		g1.addEdge(0, 1);
		g1.addEdge(0, 5);
		g1.addEdge(1, 4);
		g1.addEdge(2, 1);
		g1.addEdge(2, 4);
		g1.addEdge(3, 1);
		g1.addEdge(4, 0);
		g1.addEdge(5, 2);
		g1.addEdge(5, 4);
		
		g2.addEdge(0, 3);
		g2.addEdge(1, 2);
		g2.addEdge(1, 3);
		g2.addEdge(2, 3);
		g2.addEdge(2, 0);
		
		MatchingChecker m = new MatchingChecker();
		VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> insp =
				new VF2SubgraphIsomorphismInspector<Integer, DefaultEdge>(g1, g2);
		
		assertEquals(true, m.containsAllMatchings(insp, g1, g2));
		
	}
	
	
	private class MatchingChecker
	{

		/**
		 * Assuming g1 and g2 have vertexes labeled with 0, 1, ...
		 * No semantic check is done.
		 * 
		 * @param insp the SubgraphIsomorphismInspector
		 * @param g1 first Graph
		 * @param g2 second Graph, smaller or equal to g1
		 * @return
		 */
		public boolean containsAllMatchings(
				VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> insp,
				Graph<Integer, DefaultEdge> g1,
				Graph<Integer, DefaultEdge> g2)
		{
			boolean correct = true;
			ArrayList<SubgraphIsomorphismRelation<Integer, DefaultEdge>> matchings =
					getMatchings(g1, g2);
			
			loop:for (;insp.hasNext();)	{
				SubgraphIsomorphismRelation<Integer, DefaultEdge> rel1 =
						insp.next();
				
				System.out.print("> " + rel1 + " ..");
				
				for (SubgraphIsomorphismRelation<Integer, DefaultEdge> rel2 : matchings)	{
					if (rel1.equals(rel2))	{
						matchings.remove(rel2);
						System.out.println("exists");
						continue loop;
					}
				}
				
				correct = false;
				System.out.println("does not exist!");
			}
			
			if (!matchings.isEmpty())	{
				correct = false;
				
				System.out.println("-- no counterpart for:");
				for (SubgraphIsomorphismRelation<Integer, DefaultEdge> match : matchings)
					System.out.println("  " + match);
			}
			
			if (correct)
				System.out.println("-- ok");
			
			return correct;
		}
		
		/**
		 * Assuming g1 and g2 have vertexes labeled with 0, 1, ...
		 * No semantic check is done.
		 * 
		 * @param g1 first Graph
		 * @param g2 second Graph, smaller or equal to g1
		 * @return
		 */
		public ArrayList<SubgraphIsomorphismRelation<Integer, DefaultEdge>> getMatchings(
				Graph<Integer, DefaultEdge> g1,
				Graph<Integer, DefaultEdge> g2)
		{
			int NULL_NODE = Integer.MAX_VALUE,
			    n1        = g1.vertexSet().size(),
			    n2        = g2.vertexSet().size();
			
			GraphOrdering<Integer, DefaultEdge> g1o =
					new GraphOrdering<Integer, DefaultEdge>(g1),
			                                    g2o =
			        new GraphOrdering<Integer, DefaultEdge>(g2);
			
			ArrayList<ArrayList<Integer>> perms = getPermutations(new boolean[n1], n2);
			
			ArrayList<SubgraphIsomorphismRelation<Integer, DefaultEdge>> rels = 
					new ArrayList<SubgraphIsomorphismRelation<Integer, DefaultEdge>>();
			
			
			loop:for (ArrayList<Integer> perm : perms)	{
				int[] core2 = new int[n2];
				int i = 0;
				for (Integer p : perm)
					core2[i++] = p.intValue();
				
				for (DefaultEdge edge : g2.edgeSet())	{
					Integer u1 = g2.getEdgeSource(edge),
							u2 = g2.getEdgeTarget(edge),
							v1 = core2[u1],
							v2 = core2[u2];
					
					if (!g1.containsEdge(v1, v2))
						continue loop;
				}
				
				int[] core1 = new int[n1];
				Arrays.fill(core1, NULL_NODE);
				
				for (i = 0; i < n2; i++)
					core1[core2[i]] = i;
				
				rels.add(new SubgraphIsomorphismRelation<Integer, DefaultEdge>(
						  g1o, g2o, core1, core2));
			}
			
			return rels;
		}
		
		private ArrayList<ArrayList<Integer>> getPermutations(boolean[] vertexSet, int len)
		{
			ArrayList<ArrayList<Integer>> perms = new ArrayList<ArrayList<Integer>>();
			
			if (len <= 0)	{
				perms.add(new ArrayList<Integer>());
				return perms;
			}
			
			for (int i = 0; i < vertexSet.length; i++)	{
				if (!vertexSet[i])	{
					vertexSet[i] = true;
					ArrayList<ArrayList<Integer>> newPerms = getPermutations(vertexSet, len-1);
					vertexSet[i] = false;
					
					for (ArrayList<Integer> perm : newPerms)
						perm.add(i);
					
					perms.addAll(newPerms);
				}
			}
			
			return perms;
		}
	
	}

}
