package org.jgrapht.alg.interfaces;

import java.util.Collections;
import java.util.Map;

/**
 * Allows to derive <a
 * href="https://en.wikipedia.org/wiki/Maximum_flow_problem">maximum-flow</a> from
 * the supplied <a href="https://en.wikipedia.org/wiki/Flow_network">flow network</a>
 *
 * @param <V> vertex concept type
 * @param <E> edge concept type
 */
public interface MaximumFlowAlgorithm<V, E> {

    interface MaximumFlow<V, E> {

        /**
         * Returns value of the maximum-flow for the given network
         * @return value of th maximum-flow
         */
        Double getValue();

        /**
         * Returns mapping from edge to flow value through this particular edge
         * @return maximum flow
         */
        Map<E, Double> getFlow();
    }

    class VerbatimMaximumFlow<V, E> implements MaximumFlow<V, E> {

        Double value;
        Map<E, Double> flow;

        public VerbatimMaximumFlow(Double value, Map<E, Double> flow)
        {
            this.value  = value;
            this.flow   = Collections.unmodifiableMap(flow);
        }

        @Override
        public Double getValue() {
            return value;
        }

        @Override
        public Map<E, Double> getFlow() {
            return flow;
        }
    }

    /**
     * Builds maximum flow for the supplied network flow, for the supplied ${source} and ${sink}
     * @return maximum flow
     * @param source source of the flow inside the network
     * @param sink sink of the flow inside the network
     */
    MaximumFlow<V, E> buildMaximumFlow(V source, V sink);
}
