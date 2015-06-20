package org.jgrapht.alg.isomorphism;

import java.util.Comparator;

import org.jgrapht.Graph;


public class VF2GraphIsomorphismInspector<V,E>
    extends VF2IsomorphismInspector<V,E>
{

    public VF2GraphIsomorphismInspector(
                    Graph<V,E> graph1,
                    Graph<V,E> graph2,
                    Comparator<V> vertexComparator,
                    Comparator<E> edgeComparator)
    {
        super(graph1, graph2, vertexComparator, edgeComparator);
    }

    public VF2GraphIsomorphismInspector(
                    Graph<V, E> graph1,
                    Graph<V, E> graph2)
    {
        super(graph1, graph2);
    }


    @Override
    public VF2GraphMappingIterator<V, E> getMappings() {
        return new VF2GraphMappingIterator<V,E>(ordering1, ordering2,
                        vertexComparator, edgeComparator);
    }

}
