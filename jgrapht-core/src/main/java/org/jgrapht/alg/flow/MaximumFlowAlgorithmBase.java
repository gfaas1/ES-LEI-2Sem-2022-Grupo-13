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

    ExtensionManager<V, ? extends VertexExtensionBase>  vXs;
    ExtensionManager<E, ? extends EdgeExtensionBase>    eXs;

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
            VertexExtensionBase ux = vertexExtended0(u);

            ux.prototype = u;

            for (E e : n.outgoingEdgesOf(u)) {
                V v = n.getEdgeTarget(e);

                VertexExtensionBase vx = vertexExtended0(v);
                EdgeExtensionBase   ex = edgeExtended0(e);

                ex.source   = ux;
                ex.target   = vx;
                ex.capacity = n.getEdgeWeight(e);

                EdgeExtensionBase iex = createInverse(ex);

                ux.outgoing.add(ex);
                vx.outgoing.add(iex);
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
    protected VertexExtensionBase vertexExtended0(V v) {
        return this.<VertexExtensionBase>vertexExtended(v);
    }

    // DIE, JAVA, DIE!
    protected EdgeExtensionBase edgeExtended0(E e) {
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
        final List<EdgeExtensionBase> outgoing;

        V prototype;

        double excess;

        VertexExtensionBase() {
            this(null);
        }

        VertexExtensionBase(V prototype)
        {
            this.prototype  = prototype;
            this.outgoing   = new ArrayList<EdgeExtensionBase>();
        }
    }

    /* package */ class EdgeExtensionBase extends ExtensionManager.BaseExtension {

        E prototype;

        VertexExtensionBase source;
        VertexExtensionBase target;

        double capacity;
        double flow;

        EdgeExtensionBase inverse;

        EdgeExtensionBase() {
            this(null, null, 0, null);
        }

        EdgeExtensionBase(VertexExtensionBase source,
                          VertexExtensionBase target,
                          double capacity,
                          E prototype)
        {
            this.source     = source;
            this.target     = target;
            this.capacity   = capacity;
            this.prototype  = prototype;
        }
    }

    protected Map<E, Double> composeFlow() {
        Map<E, Double> maxFlow = new HashMap<E, Double>();
        for (E e : getNetwork().edgeSet()) {
            maxFlow.put(e, edgeExtended0(e).flow);
        }

        return maxFlow;
    }
}
