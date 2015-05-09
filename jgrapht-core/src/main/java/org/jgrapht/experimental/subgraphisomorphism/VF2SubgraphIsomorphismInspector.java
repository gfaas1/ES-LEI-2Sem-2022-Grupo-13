/**
 * 
 */
package org.jgrapht.experimental.subgraphisomorphism;

import java.util.Comparator;
import java.util.Stack;

import org.jgrapht.Graph;

/**
 * @author fabian
 *
 */
public class VF2SubgraphIsomorphismInspector<V,E> implements
		SubgraphIsomorphismInspector<SubgraphIsomorphismRelation> {

	private Graph<V,E> graph1,
	                   graph2;
	
	private Comparator<V> vertexComparator;
	private Comparator<E> edgeComparator;
	
	private SubgraphIsomorphismRelation<V,E> nextRelation;
	private Boolean hadOneRelation;
	
	private GraphOrdering<V,E> ordering1,
	                           ordering2;
	
	private Stack<VF2SubState<V,E>> stateStack;
	
	
	public VF2SubgraphIsomorphismInspector(
			Graph<V,E> graph1,
			Graph<V,E> graph2,
			Comparator<V> vertexComparator,
			Comparator<E> edgeComparator)
	{
		this.graph1           = graph1;
		this.graph2           = graph2;
		this.vertexComparator = vertexComparator;
		this.edgeComparator   = edgeComparator;
		this.ordering1        = new GraphOrdering<V,E>(graph1);
		this.ordering2        = new GraphOrdering<V,E>(graph2);
		this.stateStack       = new Stack<VF2SubState<V,E>>();
	}
	
	public VF2SubgraphIsomorphismInspector(
			Graph<V,E> graph1,
			Graph<V,E> graph2)
	{
		this(graph1,
			 graph2,
			 new DefaultComparator<V>(),
			 new DefaultComparator<E>());
	}
	
	
	private SubgraphIsomorphismRelation<V,E> match()
	{
		VF2SubState<V,E> s;
		
		if (stateStack.isEmpty())	{
			s = new VF2SubState<V,E>(
					ordering1,
					ordering2,
					vertexComparator,
					edgeComparator);
		} else {
			stateStack.pop().backtrack();
			s = stateStack.pop();
		}

		// s.resetAddVertexes();
		
		while (true)	{
			while (s.nextPair())	{
				if (s.isFeasiblePair())
				{
					stateStack.push(s);
					s = new VF2SubState<V,E>(s);
					s.addPair();
					
					if (s.isGoal())	{
						stateStack.push(s);
						return s.getCurrentMatching();
					}
					
					s.resetAddVertexes();
				}
			}
			
			if (stateStack.isEmpty())
				return null;

			s.backtrack();
			s = stateStack.pop();
		}
	}
	
	// to be removed..
	private SubgraphIsomorphismRelation<V,E> singleMatch(VF2SubState s)
	{
		if (s.isGoal())
			return s.getCurrentMatching();
		
		s.resetAddVertexes();
		SubgraphIsomorphismRelation<V,E> found = null;
		
		while (found == null && s.nextPair())	{
			if (s.isFeasiblePair())
			{
				VF2SubState s2 = new VF2SubState(s);
				s2.addPair();
				found = singleMatch(s2);
				s2.backtrack();
			}
		}
		
		return found;
	}
	
	private SubgraphIsomorphismRelation<V,E> singleMatch()
	{
		VF2SubState<V,E> s = new VF2SubState<V,E>(
				ordering1,
				ordering2,
				vertexComparator,
				edgeComparator);
		
		return singleMatch(s);
	}
	
	
	private SubgraphIsomorphismRelation<V,E> matchAndCheck()	{
		SubgraphIsomorphismRelation<V,E> rel = match();
		if (rel != null)
			hadOneRelation = true;
		return rel;
	}
	

	@Override
	public boolean hasNext() {
		if (nextRelation != null)
			return true;
		
		return (nextRelation = matchAndCheck()) != null;
	}

	@Override
	public SubgraphIsomorphismRelation next() {
		if (nextRelation != null)	{
			SubgraphIsomorphismRelation<V,E> tmp = nextRelation;
			nextRelation = null;
			return tmp;
		}
		
		//todo: add "throws ..."
		return matchAndCheck();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSubgraphIsomorphic() {
		if (hadOneRelation != null)
			return hadOneRelation;
		
		nextRelation = matchAndCheck();
		if (hadOneRelation == null)
			return hadOneRelation = false;
		
		return true;
	}

}
