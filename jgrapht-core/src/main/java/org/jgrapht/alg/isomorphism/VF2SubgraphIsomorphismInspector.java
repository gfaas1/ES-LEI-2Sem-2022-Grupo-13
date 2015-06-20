package org.jgrapht.alg.isomorphism;

import java.util.Comparator;

import org.jgrapht.Graph;


public class VF2SubgraphIsomorphismInspector<V,E>
    extends VF2IsomorphismInspector<V,E>
{

    public VF2SubgraphIsomorphismInspector(
                    Graph<V,E> graph1,
                    Graph<V,E> graph2,
                    Comparator<V> vertexComparator,
                    Comparator<E> edgeComparator)
    {
        super(graph1, graph2, vertexComparator, edgeComparator);
    }

    public VF2SubgraphIsomorphismInspector(
                    Graph<V, E> graph1,
                    Graph<V, E> graph2)
    {
        super(graph1, graph2);
    }


    @Override
    public VF2SubgraphMappingIterator<V, E> getMappings() {
        return new VF2SubgraphMappingIterator<V,E>(ordering1, ordering2,
                        vertexComparator, edgeComparator);
    }

}
