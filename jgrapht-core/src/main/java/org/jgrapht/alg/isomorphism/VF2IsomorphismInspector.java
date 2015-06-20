package org.jgrapht.alg.isomorphism;

import java.util.Comparator;
import java.util.Iterator;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.graph.Multigraph;
import org.jgrapht.graph.Pseudograph;


public abstract class VF2IsomorphismInspector<V,E>
    implements IsomorphismInspector<V,E>
{

    protected Graph<V,E> graph1,
                         graph2;

    protected Comparator<V> vertexComparator;
    protected Comparator<E> edgeComparator;

    protected GraphOrdering<V,E> ordering1,
                                 ordering2;


    /**
     * This implementation of the VF2 algorithm does not support graphs with
     * multiple edges.
     * @param graph1 the first graph
     * @param graph2 the second graph
     * @param vertexComparator comparator for semantic equivalence of vertices
     * @param edgeComparator comparator for semantic equivalence of edges
     * @param cacheEdges if true, edges get cached for faster access
     */
    public VF2IsomorphismInspector(
                    Graph<V, E> graph1,
                    Graph<V, E> graph2,
                    Comparator<V> vertexComparator,
                    Comparator<E> edgeComparator,
                    boolean cacheEdges)
    {
        if (graph1 instanceof Multigraph  || graph2 instanceof Multigraph ||
            graph1 instanceof Pseudograph || graph2 instanceof Pseudograph ||
            graph1 instanceof DirectedMultigraph ||
            graph2 instanceof DirectedMultigraph ||
            graph1 instanceof DirectedPseudograph ||
            graph2 instanceof DirectedPseudograph)
            throw new UnsupportedOperationException("graphs with multiple "
                            + "edges are not supported");

        if (graph1 instanceof DirectedGraph &&
                        graph2 instanceof UndirectedGraph ||
            graph1 instanceof UndirectedGraph &&
                        graph2 instanceof DirectedGraph)
            throw new IllegalArgumentException("can not match directed with "
                            + "undirected graphs");

        this.graph1           = graph1;
        this.graph2           = graph2;
        this.vertexComparator = vertexComparator;
        this.edgeComparator   = edgeComparator;
        this.ordering1        = new GraphOrdering<V, E>(graph1, true,
                                                        cacheEdges);
        this.ordering2        = new GraphOrdering<V, E>(graph2, true,
                                                        cacheEdges);
    }

    /**
     * @param graph1 the first graph
     * @param graph2 the second graph
     * @param vertexComparator comparator for semantic equivalence of vertices
     * @param edgeComparator comparator for semantic equivalence of edges
     */
    public VF2IsomorphismInspector(
                    Graph<V,E> graph1,
                    Graph<V,E> graph2,
                    Comparator<V> vertexComparator,
                    Comparator<E> edgeComparator)
    {
        this(graph1,
             graph2,
             vertexComparator,
             edgeComparator,
             true);
    }

    /**
     * @param graph1 the first graph
     * @param graph2 the second graph
     * @param cacheEdges if true, edges get cached for faster access
     */
    public VF2IsomorphismInspector(
                    Graph<V,E> graph1,
                    Graph<V,E> graph2,
                    boolean cacheEdges)
    {
        this(graph1,
             graph2,
             new DefaultComparator<V>(),
             new DefaultComparator<E>(),
             cacheEdges);
    }

    /**
     * @param graph1 the first graph
     * @param graph2 the second graph
     */
    public VF2IsomorphismInspector(
                    Graph<V, E> graph1,
                    Graph<V, E> graph2)
    {
        this(graph1,
             graph2,
             true);
    }


    @Override
    public abstract Iterator<IsomorphicGraphMapping<V, E>> getMappings();

    @Override
    public boolean isomorphismExists() {
        Iterator<IsomorphicGraphMapping<V,E>> iter = getMappings();
        return iter.hasNext();
    }

}
