package org.jgrapht.alg.matching;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

/**
 * Created by jkinable on 5/11/17.
 */
public class HopcroftKarpMaximumCardinalityBipartiteMatching<V,E> implements MatchingAlgorithm<V,E>{

    private final Graph<V,E> graph;
    private final Set<V> partition1;
    private final Set<V> partition2;

    /* Ordered list of vertices */
    private List<V> vertices;
    /* Mapping of a vertex to their unique position in the ordered list of vertices */
    private Map<V, Integer> vertexIndexMap;


    private final int UNMATCHED=-1;
    private int DUMMY;
    private final int INF = Integer.MAX_VALUE;
    private int[] pair;
    private int[] dist;

    public HopcroftKarpMaximumCardinalityBipartiteMatching(Graph<V, E> graph, Set<V> partition1, Set<V> partition2) {
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

    public boolean BFS()
    {
        Queue<Integer> queue = new LinkedList<Integer>();
        for (int v = 0; v < partition1.size(); v++)
            if (pair[v] == UNMATCHED)
            {
                dist[v] = 0;
                queue.add(v);
            }
            else
                dist[v] = INF;

        dist[DUMMY] = INF;

        while (!queue.isEmpty())
        {
            int v = queue.poll();
            if (dist[v] < dist[DUMMY])
                for (V uOrig : Graphs.neighborListOf(graph, vertices.get(v))) {
                    int u=vertexIndexMap.get(uOrig);
                    if (dist[pair[u]] == INF) {
                        dist[pair[u]] = dist[v] + 1;
                        queue.add(pair[u]);
                    }
                }
        }
        return dist[DUMMY] != INF;
    }
    /** Function DFS **/
    public boolean DFS(int v)
    {
        if (v != DUMMY)
        {
            for (V uOrig : Graphs.neighborListOf(graph, vertices.get(v))) {
                int u=vertexIndexMap.get(uOrig);
                if (dist[pair[u]] == dist[v] + 1)
                    if (DFS(pair[u])) {
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
        Arrays.fill(pair, DUMMY);
        dist = new int[vertices.size() + 1];
        DUMMY= vertices.size();
        int matching = 0;
        while (BFS())
            for (int v = 0; v < partition1.size(); v++)
                if (pair[v] == UNMATCHED)
                    if (DFS(v))
                        matching = matching + 1;
        return matching;
    }

    @Override
    public Matching<V,E> getMatching() {
        this.init();

        this.HopcroftKarp();

        Set<E> edges=new HashSet<>();
        for(int i=0; i<vertices.size(); i++){
            if(pair[i] != DUMMY){
                edges.add(graph.getEdge(vertices.get(i), vertices.get(pair[i])));
            }
        }
        return new MatchingImpl<>(graph, edges, edges.size());
    }
}
