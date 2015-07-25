package org.jgrapht.alg.flow;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.util.ExtensionManager.ExtensionFactory;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;

/**
 * <p><a href="https://en.wikipedia.org/wiki/Push%E2%80%93relabel_maximum_flow_algorithm">
 * Push-relabel maximum flow</a> algorithm designed by Andrew V. Goldberg and
 * Robert Tarjan. Current implementation complexity upper-bound is O(V^3). For more
 * details see: <i>"A new approach to the maximum flow problem"</i> by Andrew V. Goldberg
 * and Robert Tarjan <i>STOC '86: Proceedings of the eighteenth annual ACM symposium on
 * Theory of computing</i></p>
 *
 */
public class PushRelabelMaximumFlow<V, E> extends MaximumFlowAlgorithmBase<V,E> {

    private static final double EPSILON = 1e-6;

    private DirectedGraph<V, E> network;

    public PushRelabelMaximumFlow(DirectedGraph<V, E> network) {
        this.network = network;

        init(
            new ExtensionFactory<VertexExtension>() {
                @Override
                public VertexExtension create() {
                    return PushRelabelMaximumFlow.this.new VertexExtension();
                }
            },
            new ExtensionFactory<EdgeExtension>() {
                @Override
                public EdgeExtension create() {
                    return PushRelabelMaximumFlow.this.new EdgeExtension();
                }
            }
        );
    }

    @Override
    DirectedGraph<V, E> getNetwork() {
        return network;
    }

    public class VertexExtension extends VertexExtensionBase {
        private int label;

        private boolean hasExcess() {
            return excess > 0;
        }
    }

    public class EdgeExtension extends EdgeExtensionBase {

        private boolean hasCapacity() {
            return Math.abs(capacity - flow) > EPSILON;
        }
    }

    public void initialize(VertexExtension source, Queue<VertexExtension> active) {
        source.label    = network.vertexSet().size();
        source.excess   = Double.POSITIVE_INFINITY;

        for (V v : network.vertexSet()) {
            if (v == source.prototype) {
                // NOP
            } else {
                extendedVertex(v).label = 0;
            }
        }

        for (EdgeExtension ex : source.<EdgeExtension>getOutgoing()) {
            pushFlowThrough(ex, ex.capacity);
            active.offer(ex.<VertexExtension>getTarget());
        }
    }

    @Override
    public MaximumFlow<V, E> buildMaximumFlow(V source, V sink) {
        Queue<VertexExtension> active = new ArrayDeque<VertexExtension>();

        buildInternal();

        VertexExtension sourceX = extendedVertex(source);
        VertexExtension sinkX   = extendedVertex(sink);

        initialize(sourceX, active);

        while (!active.isEmpty()) {
            VertexExtension ux = active.poll();
            for (;;) {
                for (EdgeExtension ex : ux.<EdgeExtension>getOutgoing()) {
                    if (isAdmissible(ex)) {
                        // NB(kudinkin): Concerns?
                        if (ex.getTarget() != sinkX && ex.getTarget() != sourceX)
                            active.offer(ex.<VertexExtension>getTarget());

                        // Check whether we're rip off the excess
                        if (discharge(ex)) {
                            break;
                        }
                    }
                }

                if (ux.hasExcess())
                    relabel(ux);
                else
                    break;
            }
        }

        Map<E, Double> maxFlow = composeFlow();

        double maxFlowValue = 0.0;
        for (E e : network.incomingEdgesOf(sink)) {
            maxFlowValue += maxFlow.get(e);
        }

        return new VerbatimMaximumFlow<V, E>(maxFlowValue, maxFlow);
    }

    private void relabel(VertexExtension vx) {
        // _DBG
        assert(vx.hasExcess());

        int min = Integer.MAX_VALUE;
        for (EdgeExtension ex : vx.<EdgeExtension>getOutgoing()) {
            if (ex.hasCapacity()) {
                VertexExtension ux = ex.getTarget();
                if (min > ux.label)
                    min = ux.label;
            }
        }

        // Sanity
        if (min != Integer.MAX_VALUE) {
            vx.label = min + 1;
        }
    }

    private boolean discharge(EdgeExtension ex) {
        VertexExtension ux = ex.getSource();
        pushFlowThrough(ex, Math.min(ux.excess, ex.capacity - ex.flow));
        return !ux.hasExcess();
    }

    private void pushFlowThrough(EdgeExtension ex, double f) {
        ex.getSource().excess -= f;
        ex.getTarget().excess += f;

        // Check whether there's an inverse edge
//        if (network.containsEdge(v, u)) {
            // Inverse edge
            EdgeExtension iex = ex.getInverse();

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
//        } else {
//            E ie = network.addEdge(v, u);
//
//            EdgeExtension iex = extendedEdge(ie);
//
//            ex.flow += f;
//            iex.capacity = f;
//        }

        //if (!ex.hasCapacity())
        //    network.removeEdge(ex);
    }

    private int compareFlowTo(double flow, double val) {
        double diff = flow - val;
        if (Math.abs(diff) < EPSILON)
            return 0;
        else
            return diff < 0 ? -1 : 1;
    }

    private boolean isAdmissible(EdgeExtension e) {
        return e.hasCapacity()
            && e.<VertexExtension>getSource().label == e.<VertexExtension>getTarget().label + 1;
    }

    private EdgeExtension extendedEdge(E e) {
        return this.edgeExtended(e);
    }

    private VertexExtension extendedVertex(V v) {
        return this.vertexExtended(v);
    }
}
