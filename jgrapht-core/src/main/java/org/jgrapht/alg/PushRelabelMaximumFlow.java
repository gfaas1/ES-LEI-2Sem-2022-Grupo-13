package org.jgrapht.alg;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
import org.jgrapht.alg.util.ExtensionManager;

import java.util.*;

/**
 * <p><a href="https://en.wikipedia.org/wiki/Push%E2%80%93relabel_maximum_flow_algorithm">
 * Push-relabel maximum flow</a> algorithm designed by Andrew V. Goldberg and
 * Robert Tarjan. Current implementation complexity upper-bound is O(V^3). For more
 * details see: <i>"A new approach to the maximum flow problem"</i> by Andrew V. Goldberg
 * and Robert Tarjan <i>STOC '86: Proceedings of the eighteenth annual ACM symposium on
 * Theory of computing</i></p>
 *
 */
public class PushRelabelMaximumFlow<V, E> implements MaximumFlowAlgorithm<V, E> {

    private static final double EPSILON = 1e-6;

    private DirectedGraph<V, E> network;

    PushRelabelMaximumFlow(DirectedGraph<V, E> network) {
        // TODO: Copy
        this.network = network;
    }

    public static class VertexExtension extends ExtensionManager.BaseExtension {
        private double excess;
        private int label;

        public VertexExtension() { super(); }

        private boolean hasExcess() {
            return excess > 0;
        }
    }

    public static class EdgeExtension extends ExtensionManager.BaseExtension {
        private double flow;
        private double capacity;

        public EdgeExtension() { super(); }

        private boolean hasCapacity() {
            return Math.abs(capacity - flow) > EPSILON;
        }
    }

    ExtensionManager<V, VertexExtension> vXs = new ExtensionManager<V, VertexExtension>(VertexExtension.class);
    ExtensionManager<E, EdgeExtension>   eXs = new ExtensionManager<E, EdgeExtension>(EdgeExtension.class);

    public void initialize(V source, Queue<V> active) {
        for (V v : network.vertexSet()) {
            if (v == source) {
                extendedVertex(v).label  = network.vertexSet().size();
                extendedVertex(v).excess = Double.POSITIVE_INFINITY;
            } else {
                extendedVertex(v).label  = 0;
                extendedVertex(v).excess = 0.0;
            }
        }

        for (E e : new ArrayList<E>(network.edgeSet())) {
            eXs.get(e).capacity = network.getEdgeWeight(e);

            if (network.getEdgeSource(e) == source) {
                V v = network.getEdgeTarget(e);

                //extendedEdge(e).flow     = extendedEdge(e).capacity;
                //extendedVertex(v).excess = extendedEdge(e).flow;
                pushFlowThrough(e, extendedEdge(e).capacity);

                active.offer(v);
            } else {
                eXs.get(e).flow = 0.0;
            }
        }
    }

    @Override
    public MaximumFlow<V, E> buildMaximumFlow(V source, V sink) {
        Queue<V> active = new ArrayDeque<V>();

        initialize(source, active);

        while (!active.isEmpty()) {
            V u = active.poll();

            VertexExtension ux = extendedVertex(u);
            for (;;) {
                for (E e : network.outgoingEdgesOf(u)) {
                    if (isAdmissible(e)) {
                        // NB(kudinkin): Concerns?
                        V v = network.getEdgeTarget(e);
                        if (v != sink)
                            active.offer(v);

                        // Check whether we're rip off the excess
                        if (discharge(e)) {
                            break;
                        }
                    }
                }

                if (ux.hasExcess())
                    relabel(u);
                else
                    break;
            }
        }

        Map<E, Double> maxFlow = new HashMap<E, Double>();

        double maxFlowValue = 0.0;
        for (E e : network.edgeSet()) {
            EdgeExtension ex = extendedEdge(e);
            if (compareFlowTo(ex.flow, 0) == 1) {
                if (network.getEdgeTarget(e) == sink)
                    maxFlowValue += ex.flow;

                maxFlow.put(e, ex.flow);

                // _DBG
                System.out.println(e + " F/CAP: " + ex.flow + "/" + ex.capacity);
            }
        }

        return new VerbatimMaximumFlow<V, E>(maxFlowValue, maxFlow);
    }

    private void relabel(V v) {
        // _DBG
        assert(extendedVertex(v).hasExcess());

        int min = Integer.MAX_VALUE;
        for (E e : network.outgoingEdgesOf(v)) {
            EdgeExtension ex = extendedEdge(e);
            if (ex.hasCapacity()) {
                VertexExtension ux = extendedVertex(network.getEdgeTarget(e));
                if (min > ux.label)
                    min = ux.label;
            }
        }

        // Sanity
        if (min != Integer.MAX_VALUE)
            extendedVertex(v).label = min + 1;
    }

    private boolean discharge(E e) {
        V u = network.getEdgeSource(e);

        EdgeExtension   ex = extendedEdge(e);
        VertexExtension ux = extendedVertex(u);

        pushFlowThrough(e, Math.min(ux.excess, ex.capacity - ex.flow));

        return !ux.hasExcess();
    }

    private void pushFlowThrough(E e, double f) {
        EdgeExtension ex  = extendedEdge(e);

        V u = network.getEdgeSource(e);
        V v = network.getEdgeTarget(e);

        extendedVertex(u).excess -= f;
        extendedVertex(v).excess += f;

        // Check whether there's an inverse edge
        if (network.containsEdge(v, u)) {
            // Inverse edge
            E ie = network.getEdge(v, u);

            EdgeExtension iex = extendedEdge(ie);

            // _DBG
            assert(compareFlowTo(ex.flow, 0.0) == 0 || compareFlowTo(iex.flow, 0.0) == 0);

            if (compareFlowTo(iex.flow, f) == -1) {
                double d = f - iex.flow;

                ex.flow += d;

                iex.flow      = 0;
                iex.capacity += d;
            } else {
                iex.flow -= f;
            }
        } else {
            E ie = network.addEdge(v, u);

            EdgeExtension iex = extendedEdge(ie);

            ex.flow += f;
            iex.capacity = f;
        }

        //if (!ex.hasCapacity())
        //    network.removeEdge(e);
    }

    private int compareFlowTo(double flow, double val) {
        double diff = Math.abs(flow - val);
        if (diff < EPSILON)
            return 0;
        else
            return diff < 0 ? -1 : 1;
    }

    private boolean isAdmissible(E e) {
        EdgeExtension ex = extendedEdge(e);
        return ex.hasCapacity()
            && extendedVertex(network.getEdgeSource(e)).label == extendedVertex(network.getEdgeTarget(e)).label + 1;
    }

    private EdgeExtension extendedEdge(E e) {
        return eXs.get(e);
    }

    private VertexExtension extendedVertex(V s) {
        return vXs.get(s);
    }
}
