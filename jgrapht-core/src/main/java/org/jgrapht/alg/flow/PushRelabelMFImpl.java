/*
 * (C) Copyright 2015-2018, by Alexey Kudinkin and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package org.jgrapht.alg.flow;

import org.jgrapht.Graph;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.alg.util.extension.ExtensionFactory;

import java.lang.reflect.Array;
import java.util.*;

/**
 * <p>
 * <a href="https://en.wikipedia.org/wiki/Push%E2%80%93relabel_maximum_flow_algorithm"> Push-relabel
 * maximum flow</a> algorithm designed by Andrew V. Goldberg and Robert Tarjan. Current
 * implementation complexity upper-bound is O(V^3). For more details see: <i>"A new approach to the
 * maximum flow problem"</i> by Andrew V. Goldberg and Robert Tarjan <i>STOC '86: Proceedings of the
 * eighteenth annual ACM symposium on Theory of computing</i>
 * </p>
 *
 * <p>
 * This class can also computes minimum s-t cuts. Effectively, to compute a minimum s-t cut, the
 * implementation first computes a minimum s-t flow, after which a BFS is run on the residual graph.
 * </p>
 *
 * Note: even though the algorithm accepts any kind of graph, currently only Simple directed and
 * undirected graphs are supported (and tested!).
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Alexey Kudinkin, Alexandru Valeanu
 */
public class PushRelabelMFImpl<V, E>
        extends MaximumFlowAlgorithmBase<V, E>
{
    // Diagnostic

    private static final boolean DIAGNOSTIC_ENABLED = false;

    private final ExtensionFactory<VertexExtension> vertexExtensionsFactory;
    private final ExtensionFactory<AnnotatedFlowEdge> edgeExtensionsFactory;

    // Label pruning helpers

    private int[] countHeight;

    private Queue<VertexExtension> activeVertices;

    private PushRelabelDiagnostic diagnostic;

    // number of vertices
    private final int N;

    private final VertexExtension[] vertexExtension;

    /**
     * Construct a new push-relabel algorithm.
     *
     * @param network the network
     */
    public PushRelabelMFImpl(Graph<V, E> network)
    {
        this(network, DEFAULT_EPSILON);
    }

    /**
     * Construct a new push-relabel algorithm.
     *
     * @param network the network
     * @param epsilon tolerance used when comparing floating-point values
     */
    @SuppressWarnings("unchecked")
    public PushRelabelMFImpl(Graph<V, E> network, double epsilon)
    {
        super(network, epsilon);

        this.vertexExtensionsFactory = VertexExtension::new;

        this.edgeExtensionsFactory = AnnotatedFlowEdge::new;

        if (DIAGNOSTIC_ENABLED) {
            this.diagnostic = new PushRelabelDiagnostic();
        }

        this.N = network.vertexSet().size();
        this.vertexExtension = (VertexExtension[]) Array.newInstance(VertexExtension.class, N);
    }

    private void enqueue(VertexExtension vx){
        if (!vx.active && vx.hasExcess()){
            vx.active = true;
            activeVertices.add(vx);
        }
    }

    /**
     * Prepares all data structures to start a new invocation of the Maximum Flow or Minimum Cut
     * algorithms
     *
     * @param source source
     * @param sink sink
     */
    void init(V source, V sink)
    {
        super.init(source, sink, vertexExtensionsFactory, edgeExtensionsFactory);

        this.countHeight = new int[2 * N];

        int id = 0;
        for (V v: network.vertexSet()){
            vertexExtension[id] = getVertexExtension(v);
            id++;
        }
    }

    /**
     * Initialization
     *
     * @param source the source
     * @param sink the sink
     * @param active resulting queue with all active vertices
     */
    public void initialize(VertexExtension source, VertexExtension sink, Queue<VertexExtension> active)
    {
        this.activeVertices = active;

        for (int i = 0; i < N; i++) {
            vertexExtension[i].excess = 0;
            vertexExtension[i].height = 0;
            vertexExtension[i].active = false;
        }

        source.height = N;
        source.active = true;
        sink.active = true;

        countHeight[N] = 1;
        countHeight[0] = N - 1;

        for (AnnotatedFlowEdge ex : source.getOutgoing()){
            source.excess += ex.capacity;
            dischargeEdge(ex);
        }
    }

    @Override
    public MaximumFlow<E> getMaximumFlow(V source, V sink)
    {
        this.calculateMaximumFlow(source, sink);
        maxFlow = composeFlow();
        return new MaximumFlowImpl<>(maxFlowValue, maxFlow);
    }

    /**
     * Sets current source to <tt>source</tt>, current sink to <tt>sink</tt>, then calculates
     * maximum flow from <tt>source</tt> to <tt>sink</tt>. Note, that <tt>source</tt> and
     * <tt>sink</tt> must be vertices of the <tt>
     * network</tt> passed to the constructor, and they must be different.
     *
     * @param source source vertex
     * @param sink sink vertex
     * @return the value of the maximum flow
     */
    public double calculateMaximumFlow(V source, V sink)
    {
        init(source, sink);

        this.activeVertices = new ArrayDeque<>(N);
        initialize(getVertexExtension(source), getVertexExtension(sink), this.activeVertices);

        while (!activeVertices.isEmpty()){
            VertexExtension vx = activeVertices.poll();
            vx.active = false;
            discharge(vx);
        }

        // Calculate the max flow that reaches the sink. There may be more efficient ways to do
        // this.
        for (E e : network.edgesOf(sink)) {
            AnnotatedFlowEdge edge = edgeExtensionManager.getExtension(e);
            maxFlowValue += (directedGraph ? edge.flow : edge.flow + edge.getInverse().flow);
        }

        if (DIAGNOSTIC_ENABLED) {
            diagnostic.dump();
        }

        return maxFlowValue;
    }

    private void gapHeuristic(int k){
        for (int i = 0; i < N; i++) {
            if (vertexExtension[i].height >= k){
                countHeight[vertexExtension[i].height]--;
                vertexExtension[i].height = Math.max(vertexExtension[i].height, N + 1);
                countHeight[vertexExtension[i].height]++;

                enqueue(vertexExtension[i]);
            }
        }
    }

    private void dischargeEdge(AnnotatedFlowEdge ex)
    {
        VertexExtension ux = ex.getSource();
        VertexExtension vx = ex.getTarget();
        double delta = Math.min(ux.excess, ex.capacity - ex.flow);

        if (ux.height <= vx.height || comparator.compare(delta, 0.0) <= 0)
            return;

        if (DIAGNOSTIC_ENABLED) {
            diagnostic.incrementDischarges(ex);
        }

        pushFlowThrough(ex, delta);
        enqueue(vx);
    }

    /**
     * Push flow through an edge.
     *
     * @param ex the edge
     * @param f the amount of flow to push through
     */
    protected void pushFlowThrough(AnnotatedFlowEdge ex, double f)
    {
        ex.getSource().excess -= f;
        ex.getTarget().excess += f;

        assert ((ex.getSource().excess >= 0.0) && (ex.getTarget().excess >= 0));

        super.pushFlowThrough(ex, f);
    }

    private void relabel(VertexExtension vx){
        countHeight[vx.height]--;
        vx.height = 2 * N;

        for (AnnotatedFlowEdge ex : vx.getOutgoing()){
            if (ex.hasCapacity()){
                vx.height = Math.min(vx.height, ex.<VertexExtension>getTarget().height + 1);
            }
        }

        countHeight[vx.height]++;
        enqueue(vx);

        if (DIAGNOSTIC_ENABLED){
            diagnostic.incrementRelabels(vx.height, vx.height);
        }
    }

    private void discharge(VertexExtension ux){
        for (int i = 0; ux.hasExcess() && i < ux.getOutgoing().size(); i++) {
            dischargeEdge(ux.getOutgoing().get(i));
        }

        if (ux.hasExcess()){
            if (countHeight[ux.height] == 1)
                gapHeuristic(ux.height);
            else
                relabel(ux);
        }
    }

    private VertexExtension getVertexExtension(V v)
    {
        assert vertexExtensionManager != null;
        return (VertexExtension) vertexExtensionManager.getExtension(v);
    }

    private class PushRelabelDiagnostic
    {
        // Discharges
        Map<Pair<V, V>, Integer> discharges = new HashMap<>();
        long dischargesCounter = 0;

        // Relabels
        Map<Pair<Integer, Integer>, Integer> relabels = new HashMap<>();
        long relabelsCounter = 0;

        private void incrementDischarges(AnnotatedFlowEdge ex)
        {
            Pair<V, V> p = Pair.of(ex.getSource().prototype, ex.getTarget().prototype);
            if (!discharges.containsKey(p)) {
                discharges.put(p, 0);
            }
            discharges.put(p, discharges.get(p) + 1);

            dischargesCounter++;
        }

        private void incrementRelabels(int from, int to)
        {
            Pair<Integer, Integer> p = Pair.of(from, to);
            if (!relabels.containsKey(p)) {
                relabels.put(p, 0);
            }
            relabels.put(p, relabels.get(p) + 1);

            relabelsCounter++;
        }

        void dump()
        {
            Map<Integer, Integer> labels = new HashMap<>();

            for (V v : network.vertexSet()) {
                VertexExtension vx = getVertexExtension(v);

                if (!labels.containsKey(vx.height)) {
                    labels.put(vx.height, 0);
                }

                labels.put(vx.height, labels.get(vx.height) + 1);
            }

            System.out.println("LABELS  ");
            System.out.println("------  ");
            System.out.println(labels);

            List<Map.Entry<Pair<Integer, Integer>, Integer>> relabelsSorted =
                    new ArrayList<>(relabels.entrySet());

            Collections.sort(relabelsSorted, (o1, o2) -> -(o1.getValue() - o2.getValue()));

            System.out.println("RELABELS    ");
            System.out.println("--------    ");
            System.out.println("    Count:  " + relabelsCounter);
            System.out.println("            " + relabelsSorted);

            List<Map.Entry<Pair<V, V>, Integer>> dischargesSorted =
                    new ArrayList<>(discharges.entrySet());

            Collections
                    .sort(dischargesSorted, (one, other) -> -(one.getValue() - other.getValue()));

            System.out.println("DISCHARGES  ");
            System.out.println("----------  ");
            System.out.println("    Count:  " + dischargesCounter);
            System.out.println("            " + dischargesSorted);
        }
    }

    /**
     * Vertex extension for the push-relabel algorithm, which contains an additional height.
     */
    public class VertexExtension extends VertexExtensionBase {
        private int height;
        private boolean active;

        private boolean hasExcess()
        {
            return excess > 0;
        }

        @Override
        public String toString()
        {
            return prototype.toString() + String.format(" { HGT: %d } ", height);
        }
    }

}

// End PushRelabelMFImpl.java
