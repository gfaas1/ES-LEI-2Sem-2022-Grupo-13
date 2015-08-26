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

    class MaximumFlow<V, E> {

        Double value;
        Map<E, Double> flow;

        public MaximumFlow(Double value, Map<E, Double> flow)
        {
            this.value  = value;
            this.flow   = Collections.unmodifiableMap(flow);
        }

        /**
         * Returns value of the maximum-flow for the given network
         * @return value of th maximum-flow
         */
        public Double getValue() {
            return value;
        }

        /**
         * Returns mapping from edge to flow value through this particular edge
         * @return maximum flow
         */
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
