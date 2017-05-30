package org.jgrapht.generate;

import org.jgrapht.Graph;
import org.jgrapht.VertexFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
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

    //-------------Holt Graph-----------//
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


    //-------------Klein7RegularGraph-----------//

//    public static Graph<Integer, DefaultEdge> klein7RegularGraph(){
//        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
//        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateKlein7RegularGraph(g);
//        return g;
//    }
//
//    public <V,E> void generateKlein7RegularGraph(Graph<V,E> targetGraph){
//        GraphImporter<V, E> importer = new Graph6Sparse6Importer<>(
//                (l, a) -> vertexFactory.createVertex(), (f, t, l, a) -> targetGraph.getEdgeFactory().createEdge(f, t));
//        String g6 = "ZBXzr|}^z~TTitjLth|dmkrmsl|if}TmbJMhrJX]YfFyTbmsseztKTvyhDvw\n";
//        Reader reader=new InputStreamReader(new ByteArrayInputStream(g6.getBytes(StandardCharsets.UTF_8)), "UTF-8");
//        importer.importGraph(targetGraph, reader);
//    }

    //--------------Helper methods-----------------/
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
