package org.jgrapht.alg.matching;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;

import java.util.*;

/**
 * Created by jkinable on 5/11/17.
 */
public class HopcroftKarpMaximumCardinalityBipartiteMatchingBackup<V,E> implements MatchingAlgorithm<V,E>{

    private final Graph<V,E> graph;
    private final Set<V> partition1;
    private final Set<V> partition2;

    /* Ordered list of vertices */
    private List<V> vertices;
    /* Mapping of a vertex to their unique position in the ordered list of vertices */
    private Map<V, Integer> vertexIndexMap;


    private final int NIL = 0;
    private final int INF = Integer.MAX_VALUE;
    private int[] pair;
    private int[] dist;

    public HopcroftKarpMaximumCardinalityBipartiteMatchingBackup(Graph<V, E> graph, Set<V> partition1, Set<V> partition2) {
        this.graph=graph;
        this.partition1=partition1;
        this.partition2=partition2;
    }

    private void init() {
        vertices = new ArrayList<>();
        vertices.addAll(partition1);
        vertices.addAll(partition2);
        vertexIndexMap = new HashMap<>();
        for (int i = 0; i < vertices.size(); i++)
            vertexIndexMap.put(vertices.get(i), i);
    }

    

    /** Function bfs **/
    public boolean bfs()
    {
        System.out.println("BFS");
        Queue<Integer> queue = new LinkedList<>();
        for (int v = 1; v <= partition1.size(); ++v)
            if (pair[v] == NIL){ //Add all unmatched vertices to the queue and set their distance to 0
                dist[v] = 0;
                queue.add(v);
            }else //Set distance of all matched vertices to INF
                dist[v] = INF;
        dist[NIL] = INF;
        System.out.println("init:\n\tpair: "+Arrays.toString(pair)+"\n\tdist: "+Arrays.toString(dist));

        while (!queue.isEmpty()){
            int v = queue.poll();
            if (dist[v] < dist[NIL])
                for (V uOrig : Graphs.neighborListOf(graph, vertices.get(v-1))) {
                    int u=vertexIndexMap.get(uOrig)+1;
                    System.out.println("processing edge (v,u): ("+v+","+u+")");
                    if (dist[pair[u]] == INF) {
                        dist[pair[u]] = dist[v] + 1;
                        queue.add(pair[u]);
                    }
                }
        }
        System.out.println("bfs finished:\n\tpair: "+Arrays.toString(pair)+"\n\tdist: "+Arrays.toString(dist));
        System.out.println("BFS returning: "+(dist[NIL] != INF));
        return dist[NIL] != INF; //Return true if an augmenting path is found
    }
    /** Function dfs **/
    public boolean dfs(int v)
    {
        System.out.println("DFS from vertex: "+v);
        if (v != NIL){
            for (V uOrig : Graphs.neighborListOf(graph, vertices.get(v-1))) {
                int u = vertexIndexMap.get(uOrig)+1;
                if (dist[pair[u]] == dist[v] + 1)
                    if (dfs(pair[u])) {
                        pair[u] = v;
                        pair[v] = u;
                        return true;
                    }
            }

            dist[v] = INF;
            return false;
        }
        return true;
    }
    /** Function to get maximum matching **/
    public int HopcroftKarp()
    {
        pair = new int[vertices.size() + 1];
        dist = new int[vertices.size() + 1];
        int matching = 0;
        while (bfs())
            for (int v = 1; v <= partition1.size(); ++v)
                if (pair[v] == NIL)
                    if (dfs(v))
                        matching = matching + 1;
        return matching;
    }

    @Override
    public Matching<V,E> getMatching() {
        this.init();

        this.HopcroftKarp();

        Set<E> edges=new HashSet<>();
        for(int i=0; i<vertices.size(); i++){
            if(pair[i+1] != NIL){
                edges.add(graph.getEdge(vertices.get(i), vertices.get(pair[i+1]-1)));
            }
        }
        return new MatchingImpl<>(graph, edges, edges.size());
    }
}
