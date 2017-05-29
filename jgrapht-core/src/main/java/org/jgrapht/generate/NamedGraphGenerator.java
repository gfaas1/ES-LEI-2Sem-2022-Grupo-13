package org.jgrapht.generate;

import org.jgrapht.Graph;
import org.jgrapht.VertexFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jkinable on 5/25/17.
 */
public class NamedGraphGenerator<V,E> {

    private VertexFactory<V> vertexFactory;
    private Map<Integer, V> vertexMap;

    public NamedGraphGenerator(VertexFactory<V> vertexFactory){
        this.vertexFactory=vertexFactory;
    }

    public static Graph<Integer, DefaultEdge> holtGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateHoltGraph(g);
        return g;
    }
    public void generateHoltGraph(Graph<V,E> targetGraph){
        vertexMap=new HashMap<>();
        for(int i=0; i<9; i++)
            for(int j=0; j<3; j++)
                addEdge(targetGraph, i, j);

    }

    private void addEdge(Graph<V,E> targetGraph, int i, int j){
        System.out.println("adding: ("+i+","+j);
        if(!vertexMap.containsKey(i)) {
            V v=vertexFactory.createVertex();
            vertexMap.put(i, v);
            targetGraph.addVertex(v);
        }
        if(!vertexMap.containsKey(j)) {
            V v=vertexFactory.createVertex();
            vertexMap.put(j, v);
            targetGraph.addVertex(v);
        }
        targetGraph.addEdge(vertexMap.get(i), vertexMap.get(j));
    }

    public static class IntegerVertexFactory implements VertexFactory<Integer>
    {
        private int counter = 0;

        @Override
        public Integer createVertex()
        {
            return counter++;
        }

    }

    public static void main(String[] args){
        NamedGraphGenerator.holtGraph();
    }
}
