
package org.jgrapht.experimental.subgraphisomorphism;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Stack;

import org.jgrapht.Graph;

/**
 * Implements the VF2 subgraph isomorphism algorithm.
 * 
 * @author Fabian Späh
 *
 * @param <V> the type of the vertices
 * @param <E> the type of the edges
 */
public class VF2SubgraphIsomorphismInspector<V, E>
    implements SubgraphIsomorphismInspector<SubgraphIsomorphismRelation<V, E>>
{

    protected Graph<V, E> graph1,
                          graph2;

    protected Comparator<V> vertexComparator;
    protected Comparator<E> edgeComparator;

    protected SubgraphIsomorphismRelation<V, E> nextRelation;
    protected Boolean                           hadOneRelation;

    protected GraphOrdering<V, E> ordering1,
                                  ordering2;

    protected Stack<VF2SubState<V, E>> stateStack;

    /**
     * @param graph1 the first graph
     * @param graph2 the possible subgraph of graph1
     * @param vertexComparator comparator for semantic equivalence of vertices
     * @param edgeComparator comparator for semantic equivalence of edges
     */
    public VF2SubgraphIsomorphismInspector(
                    Graph<V, E> graph1,
                    Graph<V, E> graph2,
                    Comparator<V> vertexComparator,
                    Comparator<E> edgeComparator)
    {
        this.graph1           = graph1;
        this.graph2           = graph2;
        this.vertexComparator = vertexComparator;
        this.edgeComparator   = edgeComparator;
        this.ordering1        = new GraphOrdering<V, E>(graph1, true);
        this.ordering2        = new GraphOrdering<V, E>(graph2, true);
        this.stateStack       = new Stack<VF2SubState<V, E>>();
    }

    /**
     * @param graph1 the first graph
     * @param graph2 the possible subgraph of graph1
     */
    public VF2SubgraphIsomorphismInspector(
                    Graph<V, E> graph1,
                    Graph<V, E> graph2)
    {
        this(graph1,
             graph2,
             new DefaultComparator<V>(),
             new DefaultComparator<E>());
    }

    /**
     * This function moves over all matchings between graph1 and graph2. It
     * changes the state of the whole inspector.
     * 
     * @return null or one matching between graph1 and graph2
     */
    protected SubgraphIsomorphismRelation<V, E> match() {
        VF2SubState<V, E> s;

        if (stateStack.isEmpty()) {
            s = new VF2SubState<V, E>(ordering1, ordering2,
                            vertexComparator, edgeComparator);
            
            if (graph2.vertexSet().isEmpty())
                return hadOneRelation != null ? null : s.getCurrentMatching();
        } else {
            stateStack.pop().backtrack();
            s = stateStack.pop();
        }
        

        while (true) {
            while (s.nextPair()) {
                if (s.isFeasiblePair()) {
                    stateStack.push(s);
                    s = new VF2SubState<V, E>(s);
                    s.addPair();

                    if (s.isGoal()) {
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

    protected SubgraphIsomorphismRelation<V, E> matchAndCheck() {
        SubgraphIsomorphismRelation<V, E> rel = match();
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
    public SubgraphIsomorphismRelation<V, E> next() {
        if (nextRelation != null) {
            SubgraphIsomorphismRelation<V, E> tmp = nextRelation;
            nextRelation = null;
            return tmp;
        }

        SubgraphIsomorphismRelation<V, E> rel = matchAndCheck();
        if (rel == null)
            throw new NoSuchElementException();
        return rel;
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
