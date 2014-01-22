package org.jgrapht.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

public class SimpleGraphPath<V, E>
    implements GraphPath<V, E>
{

    private SimpleGraph<V, E> graph;
    private List<V> vertices;

    public SimpleGraphPath(SimpleGraph<V, E> simpleGraph, List<V> vertices)
    {
        this.graph = simpleGraph;
        this.vertices = vertices;       
    }

    @Override
    public SimpleGraph<V, E> getGraph()
    {
        return this.graph;
    }

    @Override
    public V getStartVertex()
    {
        return this.vertices.get(0);
    }

    @Override
    public V getEndVertex()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<E> getEdgeList()
    {
        List<E> result = new ArrayList<E>();
        for (int i = 0; i < getVertexList().size() - 1; i++) {
            result.add(this.getGraph().getEdge(
                getVertexList().get(i),
                getVertexList().get(i + 1)));
        }
        return result;
    }

    public List<V> getVertexList()
    {
        return vertices;
    }

    @Override
    public double getWeight()
    {
        // TODO Auto-generated method stub
        return 0;
    }

}
