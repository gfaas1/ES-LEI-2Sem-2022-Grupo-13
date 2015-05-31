package org.jgrapht.experimental.subgraphisomorphism;

import java.util.Comparator;

import org.jgrapht.Graph;


/**
 * @author fabian
 *
 * @param <V> the type of the vertices
 * @param <E> the type of the edges
 */
public class VF2IsomorphismInspector<V,E> extends
                VF2SubgraphIsomorphismInspector<V,E> {

    public VF2IsomorphismInspector(
                    Graph<V, E> graph1,
                    Graph<V, E> graph2,
                    Comparator<V> vertexComparator,
                    Comparator<E> edgeComparator)
    {
        super(graph1, graph2, vertexComparator, edgeComparator);
    }

    public VF2IsomorphismInspector(
                    Graph<V, E> graph1,
                    Graph<V, E> graph2)
    {
        super(graph1, graph2);
    }

    @Override
    protected SubgraphIsomorphismRelation<V, E> match() {
        VF2SubState<V, E> s;

        if (stateStack.isEmpty()) {
            s = new VF2State<V, E>(ordering1, ordering2,
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
}
