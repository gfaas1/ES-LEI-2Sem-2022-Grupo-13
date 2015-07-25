package org.jgrapht.alg.flow;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
import org.jgrapht.alg.util.ExtensionManager;
import org.jgrapht.alg.util.ExtensionManager.ExtensionFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MaximumFlowAlgorithmBase<V, E> implements MaximumFlowAlgorithm<V, E> {

    private ExtensionManager<V, ? extends VertexExtensionBase>  vXs;
    private ExtensionManager<E, ? extends EdgeExtensionBase>    eXs;

    /* package */ abstract DirectedGraph<V, E> getNetwork();

    <VE extends VertexExtensionBase, EE extends EdgeExtensionBase>
    void init(ExtensionFactory<VE> vertexExtensionFactory, ExtensionFactory<EE> edgeExtensionFactory) {
        vXs = new ExtensionManager<V, VE>(vertexExtensionFactory);
        eXs = new ExtensionManager<E, EE>(edgeExtensionFactory);
    }

    protected void buildInternal()
    {
        DirectedGraph<V, E> n = getNetwork();

        for (V u : n.vertexSet()) {
            VertexExtensionBase ux = extendedVertex(u);

            ux.prototype = u;

            for (E e : n.outgoingEdgesOf(u)) {
                V v = n.getEdgeTarget(e);

                VertexExtensionBase vx = extendedVertex(v);
                EdgeExtensionBase   ex = extendedEdge(e);

                ex.source    = ux;
                ex.target    = vx;
                ex.capacity  = n.getEdgeWeight(e);
                ex.prototype = e;

                EdgeExtensionBase iex = createInverse(ex);

                ux.getOutgoing().add(ex);
                vx.getOutgoing().add(iex);
            }
        }
    }

    private EdgeExtensionBase createInverse(EdgeExtensionBase ex) {
        EdgeExtensionBase iex = null;

        try {

            iex = eXs.createInstance();

            iex.source = ex.target;
            iex.target = ex.source;

            ex.inverse  = iex;
            iex.inverse = ex;

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return iex;
    }

    // DIE, JAVA, DIE!
    private VertexExtensionBase extendedVertex(V v) {
        return this.<VertexExtensionBase>vertexExtended(v);
    }
    private EdgeExtensionBase   extendedEdge(E e) {
        return this.<EdgeExtensionBase>edgeExtended(e);
    }

    protected <VE extends VertexExtensionBase> VE vertexExtended(V v) {
        return (VE) vXs.get(v);
    }

    protected <EE extends EdgeExtensionBase> EE edgeExtended(E e) {
        return (EE) eXs.get(e);
    }


    /* package */ class VertexExtensionBase extends ExtensionManager.BaseExtension
    {
        private final List<? extends EdgeExtensionBase> outgoing = new ArrayList<EdgeExtensionBase>();

        public <EE extends EdgeExtensionBase> List<EE> getOutgoing() {
            return (List<EE>) outgoing;
        }

        V prototype;

        double excess;
    }

    /* package */ class EdgeExtensionBase extends ExtensionManager.BaseExtension {

        private VertexExtensionBase source;
        private VertexExtensionBase target;

        private EdgeExtensionBase inverse;

        public <VE extends VertexExtensionBase> VE getSource() {
            return (VE) source;
        }

        public void setSource(VertexExtensionBase source) {
            this.source = source;
        }

        public <VE extends VertexExtensionBase> VE getTarget() {
            return (VE) target;
        }

        public void setTarget(VertexExtensionBase target) {
            this.target = target;
        }

        public  <EE extends EdgeExtensionBase> EE getInverse() {
            return (EE) inverse;
        }

        public void setInverse(EdgeExtensionBase inverse) {
            this.inverse = inverse;
        }

        E prototype;

        double capacity;
        double flow;
    }

    protected Map<E, Double> composeFlow() {
        Map<E, Double> maxFlow = new HashMap<E, Double>();
        for (E e : getNetwork().edgeSet()) {
            EdgeExtensionBase ex = extendedEdge(e);
            maxFlow.put(e, ex.flow);

            // _DBG
            System.out.println(e + " F/CAP " + ex.flow + "/" + ex.capacity);
        }

        return maxFlow;
    }
}
