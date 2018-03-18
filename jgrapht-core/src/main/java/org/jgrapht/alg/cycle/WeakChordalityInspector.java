package org.jgrapht.alg.cycle;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

import java.util.*;

public class WeakChordalityInspector<V, E> {
    private Graph<V, E> graph;
    private Map<V, Integer> vertices;
    private Map<Integer, V> indices;
    private Boolean weaklyChordal;
    private final int n;

    public WeakChordalityInspector(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph);
        if (graph.getType().isDirected()) {
            this.graph = new AsUndirectedGraph<>(graph);
        }
        n = graph.vertexSet().size();
        init();
    }

    public static void main(String[] args) {
        Graph<Integer, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addVertex(4);
        graph.addEdge(1,2);
        graph.addEdge(2,3);
        graph.addEdge(3,4);
        graph.addEdge(1,4);
        graph.addEdge(1,3);
        graph.addEdge(2,4);
        ArrayList<Pair<Integer, Integer>> separator = new ArrayList<>(Arrays.asList(new Pair<>(0, 1), new Pair<>(1, 1), new Pair<>(2, 1), new Pair<>(3, 1)));
        WeakChordalityInspector<Integer, DefaultEdge> inspector = new WeakChordalityInspector<>(graph);
        System.out.println(inspector.computeCoConnectedComponents(separator));
    }

    private void test2(){
        ArrayList<Pair<Integer, Integer>> sep1 = new ArrayList<>(Arrays.asList(new Pair<>(0, 1), new Pair<>(2, 1), new Pair<>(3, 1)));
        ArrayList<Pair<Integer, Integer>> sep2 = new ArrayList<>(Arrays.asList(new Pair<>(0, 1), new Pair<>(1, 1), new Pair<>(3, 1)));
        ArrayList<Pair<Integer, Integer>> sep3 = new ArrayList<>(Arrays.asList(new Pair<>(0, 1), new Pair<>(1, 1)));
        ArrayList<ArrayList<Pair<Integer, Integer>>> separators = new ArrayList<>(Arrays.asList(sep1, sep2, sep3));
        WeakChordalityInspector<Integer, DefaultEdge> inspector = new WeakChordalityInspector<>(new DefaultUndirectedGraph<>(DefaultEdge.class));
        inspector.sortSeparatorsList(separators);
        System.out.println(separators);
    }

    private void test1() {
        Graph<String, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        graph.addVertex("a");
        graph.addVertex("b");
        graph.addVertex("c");
        graph.addVertex("d");
        graph.addVertex("e");
        graph.addVertex("f");
        graph.addVertex("g");
        graph.addVertex("i");
        graph.addVertex("j");
        graph.addVertex("k");
        graph.addEdge("a", "d");
        graph.addEdge("b", "d");
        graph.addEdge("c", "d");
        graph.addEdge("d", "e");
        DefaultEdge edge = graph.addEdge("e", "f");
        graph.addEdge("f", "g");
        graph.addEdge("g", "i");
        graph.addEdge("g", "j");
        WeakChordalityInspector<String, DefaultEdge> inspector = new WeakChordalityInspector<>(graph);
        System.out.println(inspector.computeSeparators(edge));
    }

    private void init() {
        vertices = new HashMap<>(graph.vertexSet().size());
        indices = new HashMap<>(graph.vertexSet().size());
        int i = 0;
        for (V v : graph.vertexSet()) {
            indices.put(i, v);
            vertices.put(v, i++);
        }
        System.out.println(vertices);
        System.out.println(indices);
    }

    /*public boolean isWeaklyChordal() {
        return lazyComputeWeakChordality();
    }*/

    /*private boolean lazyComputeWeakChordality() {
        if (weaklyChordal == null) {
            List<ArrayList<Pair<Integer, Integer>>> separators = new ArrayList<>();
            Set<E> processed = new HashSet<>();
            for (E edge : graph.edgeSet()) {
                if (processed.contains(edge)) {
                    processed.add(edge);
                    separators.addAll(computeSeparators(edge));
                }
            }
            sortSeparatorsList(separators);

            if (separators.size() > 0) {
                int separatorsNum = 1;
                ArrayList<Pair<V, Integer>> original = separators.get(0);
                ArrayList<ArrayList<V>> coConnectedComponents = computeCoConnectedComponents(original);
                for (ArrayList<Pair<V, Integer>> separator : separators) {
                    if (!equalSeparators(original, separator)) {
                        original = separator;
                        ++separatorsNum;
                        if (graph.vertexSet().size() + graph.edgeSet().size() < separatorsNum) {
                            weaklyChordal = false;
                            break;
                        } else {
                            coConnectedComponents = computeCoConnectedComponents(original);
                        }
                    }
                    for (ArrayList<V> coConnectedComponent : coConnectedComponents) {
                        if (!checkCondition(coConnectedComponent, separator)) {
                            weaklyChordal = false;
                            break;
                        }
                    }

                }
            } else {
                weaklyChordal = true;
            }

        }
        return weaklyChordal;
    }*/

    List<ArrayList<Pair<Integer, Integer>>> computeSeparators(E edge) {
        V a = graph.getEdgeSource(edge);
        V b = graph.getEdgeTarget(edge);
        Map<V, Integer> labeling = new HashMap<>(graph.edgesOf(a).size());
        graph.edgesOf(a).forEach(e -> {
            labeling.put(Graphs.getOppositeVertex(graph, e, a), 1);
        });
        graph.edgesOf(b).forEach(e -> {
            V opposite = Graphs.getOppositeVertex(graph, e, b);
            if (labeling.containsKey(opposite)) {
                labeling.put(opposite, 3);
            } else {
                labeling.put(opposite, 2);
            }
        });

        ArrayList<ArrayList<Pair<Integer, Integer>>> separators = new ArrayList<>();
        Map<V, Character> dfsMap = new HashMap<>();
        for (V vertex : graph.vertexSet()) {
            if (labeling.containsKey(vertex)) {
                dfsMap.put(vertex, 'r');
            } else {
                dfsMap.put(vertex, 'w');
            }
        }
        dfsMap.put(a, 'b');
        dfsMap.put(b, 'b');

        for (V vertex : graph.vertexSet()) {
            if (dfsMap.get(vertex) == 'w') {
                ArrayList<Pair<Integer, Integer>> separator = new ArrayList<>();
                dfsVisit(vertex, dfsMap, separator, labeling);
                if (separator.size() > 0) {
                    separators.add(separator);
                    separator.sort(Comparator.comparingInt(Pair::getFirst));
                }
            }
        }

        return separators;
    }

    private void dfsVisit(V vertex, Map<V, Character> map, ArrayList<Pair<Integer, Integer>> separator, Map<V, Integer> labeling) {
        map.put(vertex, 'b');
        graph.edgesOf(vertex).forEach(e -> {
            V opposite = Graphs.getOppositeVertex(graph, e, vertex);
            if (map.get(opposite) == 'w') {
                dfsVisit(opposite, map, separator, labeling);
            } else if (map.get(opposite) == 'r') {
                separator.add(new Pair<>(vertices.get(opposite), labeling.get(opposite)));
            }
        });
    }

    void sortSeparatorsList(List<ArrayList<Pair<Integer, Integer>>> separators) {
        LinkedList<ArrayList<Pair<Integer, Integer>>> bigQueue = new LinkedList<>();
        int k = 0;
        for (ArrayList<Pair<Integer, Integer>> separator : separators) {
            if (separator.size() > k) {
                k = separator.size();
            }
            bigQueue.add(separator);
        }
        separators.clear();
        ArrayList<LinkedList<ArrayList<Pair<Integer, Integer>>>> queues = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            queues.add(new LinkedList<>());
        }
        for (int i = 0; i < k; i++) {
            while (!bigQueue.isEmpty()) {
                ArrayList<Pair<Integer, Integer>> separator = bigQueue.remove();
                if (i >= separator.size()) {
                    separators.add(separator);
                } else {
                    queues.get(separator.get(i).getFirst()).add(separator);
                }
            }
            for (LinkedList<ArrayList<Pair<Integer, Integer>>> queue : queues) {
                bigQueue.addAll(queue);
                queue.clear();
            }
        }
        separators.addAll(bigQueue);
    }

    private boolean compareSeparators(ArrayList<Pair<Integer, Integer>> sep1, ArrayList<Pair<Integer, Integer>> sep2) {
        if (sep1.size() != sep2.size()) {
            return false;
        } else {
            for (int i = 0; i < sep1.size(); i++) {
                if (!sep1.get(i).getFirst().equals(sep2.get(i).getFirst())) {
                    return false;
                }
            }
            return true;
        }
    }

    ArrayList<ArrayList<Integer>> computeCoConnectedComponents(ArrayList<Pair<Integer, Integer>> separator) {
        ArrayList<ArrayList<Integer>> coConnectedComponents = new ArrayList<>();
        ArrayList<Set<Integer>> vList = new ArrayList<>(Collections.nCopies(n, null));
        ArrayList<Integer> vLabels = new ArrayList<>(Collections.nCopies(n, 0));
        Set<Integer> v0 = new HashSet<>(separator.size());
        separator.forEach(pair-> v0.add(pair.getFirst()));
        vList.set(0, v0);
        int minLabel = 0;
        while(v0.size() > 0){
            ArrayList<Integer> coConnectedComponent = new ArrayList<>();
            do{
                while(!vList.get(minLabel).isEmpty()){
                    Integer vertex = vList.get(minLabel).iterator().next();
                    vList.get(minLabel).remove(vertex);
                    coConnectedComponent.add(vertex);
                    vLabels.set(vertex, -1);
                    for(E edge : graph.edgesOf(indices.get(vertex))){
                        Integer opposite = vertices.get(Graphs.getOppositeVertex(graph, edge, indices.get(vertex)));
                        Integer oppLabel = vLabels.get(opposite);
                        if(oppLabel != -1){
                            vList.get(oppLabel).remove(opposite);
                            if(vList.get(oppLabel + 1) == null){
                                vList.set(oppLabel + 1, new HashSet<>());
                            }
                            vList.get(oppLabel + 1).add(opposite);
                            vLabels.set(opposite, oppLabel + 1);
                        }
                    }
                }
                ++minLabel;
            }while(minLabel != coConnectedComponent.size());
            Set<Integer> bucket = vList.get(minLabel);
            for(Integer vertex : bucket){
                vLabels.set(vertex, 0);
                vList.get(0).add(vertex);
            }
            bucket.clear();
            coConnectedComponents.add(coConnectedComponent);
            minLabel = 0;
        }
        return coConnectedComponents;
    }

    private boolean checkCondition(ArrayList<V> connectedComponent, ArrayList<Pair<Integer, Integer>> separator) {
        return false;
    }
}
