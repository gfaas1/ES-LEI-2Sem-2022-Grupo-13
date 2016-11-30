/*
 * (C) Copyright 2016-2016, by Joris Kinable and Contributors.
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
import org.jgrapht.GraphTests;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.interfaces.MinimumSTCutAlgorithm;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementation of the algorithm by Padberg and Rao to compute Odd Minimum Cut-Sets. Let G=(V,E)
 * be an undirected, simple weighted graph, where all edge weights are positive. Let T &sub; V with
 * |T| even, be a set of vertices that are labelled <i>odd</i>. A cut-set (U:V-U) is called odd if
 * |T &cap; U| is an odd number. Let c(U:V-U) be the weight of the cut, that is, the sum of weights
 * of the edges which have exactly one endpoint in U and one endpoint in V-U. The problem of finding
 * an odd minimum cut-set in G is stated as follows: Find W &sube; V such that
 * c(W:V-W)=min{c(U:V-U)|U&sube; V, |T &cap; U| is odd}.
 *
 * <p>
 * The algorithm has been published in: Padberg, M. Rao, M. Odd Minimum Cut-Sets and b-Matchings.
 * Mathematics of Operations Research, 7(1), p67-80, 1982. A more concise description is published
 * in: Letchford, A. Reinelt, G. Theis, D. Odd minimum cut-sets and b-matchings revisited. SIAM
 * Journal of Discrete Mathematics, 22(4), p1480-1487, 2008.
 *
 * <p>
 * The runtime complexity of this algorithm is dominated by the runtime complexity of the algorithm used
 * to compute A Gomory-Hu tree on graph G. Consequently, the runtime complexity of this class is O(V^4).
 *
 * <p>
 * This class does not support changes to the underlying graph. The behavior of this class is
 * undefined when the graph is modified after instantiating this class.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Joris Kinable
 */
public class PadbergRaoOddMinimumCutset<V, E>
{

    /* Input graph */
    private final Graph<V, E> network;
    /* Set of vertices which are labeled 'odd' (set T in the paper) */
    private Set<V> oddVertices;
    /* Algorithm used to calculate the Gomory-Hu Cut-tree */
    private final GusfieldGomoryHuCutTree<V, E> gusfieldGomoryHuCutTreeAlgorithm;
    /* The Gomory-Hu tree */
    private SimpleWeightedGraph<V, DefaultWeightedEdge> gomoryHuTree;

    /* Weight of the minimum odd cut-set */
    private double minimumCutWeight = Double.MAX_VALUE;
    /* Source partition constituting the minimum odd cut-set */
    private Set<V> sourcePartitionMinimumCut;

    /**
     * Creates a new instance of the PadbergRaoOddMinimumCutset algorithm.
     * 
     * @param network input graph
     */
    public PadbergRaoOddMinimumCutset(Graph<V, E> network)
    {
        this(network, MaximumFlowAlgorithmBase.DEFAULT_EPSILON);
    }

    /**
     * Creates a new instance of the PadbergRaoOddMinimumCutset algorithm.
     * 
     * @param network input graph
     * @param epsilon tolerance
     */
    public PadbergRaoOddMinimumCutset(Graph<V, E> network, double epsilon)
    {
        this(network, new PushRelabelMFImpl<>(network, epsilon));
    }

    /**
     * Creates a new instance of the PadbergRaoOddMinimumCutset algorithm.
     * 
     * @param network input graph
     * @param minimumSTCutAlgorithm algorithm used to calculate the Gomory-Hu tree
     */
    public PadbergRaoOddMinimumCutset(
        Graph<V, E> network, MinimumSTCutAlgorithm<V, E> minimumSTCutAlgorithm)
    {
        if(!(network instanceof UndirectedGraph))
            throw new IllegalArgumentException("Graph must be undirected");
        this.network = network;
        gusfieldGomoryHuCutTreeAlgorithm =
            new GusfieldGomoryHuCutTree<>(network, minimumSTCutAlgorithm);
    }

    /**
     * Calculates the minimum odd cut. The implementation follows Algorithm 1 in the paper Odd
     * minimum cut sets and b-matchings revisited by Adam Letchford, Gerhard Reinelt and Dirk Theis.
     * The original algorithm runs on a compressed Gomory-Hu tree: a cut-tree with the odd vertices
     * as terminal vertices. This tree has |T|-1 edges as opposed to |V|-1 for a Gomory-Hu tree
     * defined on the input graph G. This compression step can however be skipped. If you want to
     * run the original algorithm in the paper (default), set the parameter
     * <code>useTreeCompression</code> to true. Alternatively, experiment which setting of this
     * parameter produces the fastest results. Both settings are guaranteed to find the optimal cut.
     * Experiments on random graphs showed that setting <code>useTreeCompression</code> to true was
     * on average a bit faster, but there were some cases where setting
     * <code>useTreeCompression</code> to false yielded significantly faster results.
     *
     * @param oddVertices Set of vertices which are labeled 'odd'. Note that the number of vertices
     *        in this set must be even!
     * @param useTreeCompression parameter indicating whether tree compression should be used
     *        (default: true).
     * @return weight of the minimum odd cut.
     */
    public double calculateMinCut(Set<V> oddVertices, boolean useTreeCompression)
    {
        if (useTreeCompression)
            return calculateMinCutWithTreeCompression(oddVertices);
        else
            return calculateMinCutWithoutTreeCompression(oddVertices);
    }

    /**
     * Modified implementation of the algorithm proposed in Odd Minimum Cut-sets and b-matchings by
     * Padberg and Rao. The optimal cut is directly computed on the Gomory-Hu tree computed for
     * graph G. This approach iterates efficiently over all possible cuts of the graph (there are
     * |V| such cuts).
     * 
     * @param oddVertices Set of vertices which are labeled 'odd'. Note that the number of vertices
     *        in this set must be even!
     * @return weight of the minimum odd cut.
     */
    private double calculateMinCutWithoutTreeCompression(Set<V> oddVertices)
    {
        minimumCutWeight = Double.MAX_VALUE;
        this.oddVertices = oddVertices;
        gomoryHuTree = gusfieldGomoryHuCutTreeAlgorithm.getGomoryHuTree();
        Set<DefaultWeightedEdge> edges = new LinkedHashSet<>(gomoryHuTree.edgeSet());
        for (DefaultWeightedEdge edge : edges) {
            V source = gomoryHuTree.getEdgeSource(edge);
            V target = gomoryHuTree.getEdgeTarget(edge);
            double edgeWeight = gomoryHuTree.getEdgeWeight(edge);
            gomoryHuTree.removeEdge(edge); // Temporarily remove edge
            Set<V> sourcePartition =
                new ConnectivityInspector<>(gomoryHuTree).connectedSetOf(source);
            if (edgeWeight < minimumCutWeight
                && PadbergRaoOddMinimumCutset.isOddSet(sourcePartition, oddVertices))
            { // If the source partition forms an odd cutset, check whether the cut isn't better
              // than the one we already found.
                minimumCutWeight = edgeWeight;
                sourcePartitionMinimumCut = sourcePartition;
            }
            gomoryHuTree.addEdge(source, target, edge); // Place edge back
        }
        return minimumCutWeight;
    }

    /**
     * Implementation of the algorithm proposed in Odd Minimum Cut-sets and b-matchings by Padberg
     * and Rao. The algorithm first compresses the Gomory-Hu tree, thereby creating a cut-tree using
     * the odd vertices as terminal vertices.
     * 
     * @param oddVertices Set of vertices which are labeled 'odd'. Note that the number of vertices
     *        in this set must be even!
     * @return weight of the minimum odd cut.
     */
    private double calculateMinCutWithTreeCompression(Set<V> oddVertices)
    {
        minimumCutWeight = Double.MAX_VALUE;
        this.oddVertices = oddVertices;

        if (oddVertices.size() % 2 == 1)
            throw new IllegalArgumentException("There needs to be an even number of odd vertices");
        assert network.vertexSet().containsAll(oddVertices); // All odd vertices must be contained
                                                             // in the graph
        assert oddVertices.size() % 2 == 0; // Number of odd vertices needs to be even
        assert network.edgeSet().stream().filter(e -> network.getEdgeWeight(e) < 0).count() == 0; // All
                                                                                                  // edge
                                                                                                  // weights
                                                                                                  // need
                                                                                                  // to
                                                                                                  // be
                                                                                                  // positive

        // Build a Gomory-Hu tree on the entire graph
        gomoryHuTree = gusfieldGomoryHuCutTreeAlgorithm.getGomoryHuTree();

        // Optional: Compress the graph (computations could be performed on the original Gomory-Hu
        // tree)
        SimpleWeightedGraph<Cluster, DefaultWeightedEdge> compositeTree =
            this.createCompositeTree();

        // For each edge in the composite tree, remove the edge and evaluate the resulting cut
        Set<DefaultWeightedEdge> edgeSet = new LinkedHashSet<>(compositeTree.edgeSet());
        for (DefaultWeightedEdge edge : edgeSet) {
            Cluster n1 = compositeTree.getEdgeSource(edge);
            Cluster n2 = compositeTree.getEdgeTarget(edge);
            double cutWeight = compositeTree.getEdgeWeight(edge);

            // Remove the edge from the graph, thereby creating 2 partitions representing a cut
            compositeTree.removeEdge(edge);
            // Evaluate the cut
            this.evaluateCut(compositeTree, cutWeight);
            // Restore the tree by adding the edge back to the graph
            compositeTree.addEdge(n1, n2, edge);
        }

        return minimumCutWeight;
    }

    /**
     * Creates a compressed Gomory-Hu tree consisting of 'cluster' nodes. Each cluster node contains
     * exactly one odd node, and zero or more even nodes. In the paper, this graph is defined as a
     * cut-tree on the original graph G with terminal vertex set T.
     * 
     * @return Gomory-Hu cut tree with terminal vertex set T
     */
    private SimpleWeightedGraph<Cluster, DefaultWeightedEdge> createCompositeTree()
    {
        SimpleWeightedGraph<Cluster, DefaultWeightedEdge> compressedTree =
            new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        // Initialize
        // Choose 2 random odd nodes (there are certainly 2 odd nodes since the number of odd nodes
        // in the graph must be even).
        Iterator<V> oddNodeIterator = oddVertices.iterator();
        V oddNode1 = oddNodeIterator.next();
        V oddNode2 = oddNodeIterator.next();
        // Find cheapest edge in the Gomory-Hu tree separating those two vertices
        List<DefaultWeightedEdge> pathEdges =
            DijkstraShortestPath.findPathBetween(gomoryHuTree, oddNode1, oddNode2);
        DefaultWeightedEdge cheapestEdge =
            pathEdges.stream().min(Comparator.comparing(gomoryHuTree::getEdgeWeight)).orElseThrow(
                () -> new RuntimeException("path should not be empty?!"));
        V clusterHead1 = gomoryHuTree.getEdgeSource(cheapestEdge);
        V clusterHead2 = gomoryHuTree.getEdgeTarget(cheapestEdge);
        // Remove the selected edge from the gomoryHuTree graph. The resulting graph consists of 2
        // components
        gomoryHuTree.removeEdge(cheapestEdge);

        // Create 2 new clusters
        ConnectivityInspector<V, DefaultWeightedEdge> connectivityInspector =
            new ConnectivityInspector<>(gomoryHuTree);
        Set<V> nodeSet1 = connectivityInspector.connectedSetOf(clusterHead1);
        Set<V> nodeSet2 = connectivityInspector.connectedSetOf(clusterHead2);
        Cluster cluster1 = new Cluster(clusterHead1, nodeSet1);
        Cluster cluster2 = new Cluster(clusterHead2, nodeSet2);
        compressedTree.addVertex(cluster1);
        compressedTree.addVertex(cluster2);
        compressedTree.addEdge(cluster1, cluster2, cheapestEdge);

        Queue<Cluster> openNodes = new LinkedList<>();
        if (cluster1.oddNodeSubset.size() > 1) // Place the node back onto the queue if it contains
                                               // multiple odd nodes
            openNodes.add(cluster1);
        if (cluster2.oddNodeSubset.size() > 1) // Place the node back onto the queue if it contains
                                               // multiple odd nodes
            openNodes.add(cluster2);

        while (!openNodes.isEmpty()) {
            Cluster parent = openNodes.poll();
            Cluster child = this.splitNode(compressedTree, parent);

            if (parent.oddNodeSubset.size() > 1) // Place the node back onto the queue if it
                                                 // contains multiple odd nodes
                openNodes.add(parent);
            if (child.oddNodeSubset.size() > 1) // Place the child onto the queue
                openNodes.add(child);
        }
        return compressedTree;
    }

    /**
     * Procedure which takes a cluster (parent) with 2 or more odd nodes and splits off a new
     * cluster. The new cluster contains at least one odd node. As a result, the parent cluster has
     * at least one less
     * 
     * @param compressedTree gomory-hu tree on the terminal vertex T
     * @param parent cluster with two or more odd nodes
     * @return a new cluster split-off from the parent node, with 1 or more odd nodes
     */
    private Cluster splitNode(
        SimpleWeightedGraph<Cluster, DefaultWeightedEdge> compressedTree, Cluster parent)
    {
        assert parent.oddNodeSubset.size() >= 2;

        // Choose 2 random odd nodes (there are certainly 2 odd nodes as this is guaranteed by the
        // invoking function).
        Iterator<V> oddNodeIterator = parent.oddNodeSubset.iterator();
        V oddNode1 = oddNodeIterator.next();
        V oddNode2 = oddNodeIterator.next();
        // Find cheapest edge in the Gomory-Hu tree separating those two vertices
        List<DefaultWeightedEdge> pathEdges =
            DijkstraShortestPath.findPathBetween(gomoryHuTree, oddNode1, oddNode2);
        DefaultWeightedEdge cheapestEdge =
            pathEdges.stream().min(Comparator.comparing(gomoryHuTree::getEdgeWeight)).orElseThrow(
                () -> new RuntimeException("path should not be empty?!"));
        V edgeSource = gomoryHuTree.getEdgeSource(cheapestEdge);
        V edgeTarget = gomoryHuTree.getEdgeTarget(cheapestEdge);

        // Remove the selected edge from the gomoryHuTree graph. This splits the component the edge
        // was part of into two separate components.
        gomoryHuTree.removeEdge(cheapestEdge);

        // Find the vertices in each of the components
        ConnectivityInspector<V, DefaultWeightedEdge> connectivityInspector =
            new ConnectivityInspector<>(gomoryHuTree);
        Set<V> sourcePartition = connectivityInspector.connectedSetOf(edgeSource);
        Set<V> targetPartition = connectivityInspector.connectedSetOf(edgeTarget);

        // One of the components contains the cluster head of the parent. This component is used to
        // update the parent, the other component becomes a new cluster.
        Cluster child;
        if (sourcePartition.contains(parent.clusterHead)) {
            parent.retainAll(sourcePartition);
            child = new Cluster(edgeTarget, targetPartition);
        } else {
            parent.retainAll(targetPartition);
            child = new Cluster(edgeSource, sourcePartition);
        }

        compressedTree.addVertex(child);
        compressedTree.addEdge(parent, child, cheapestEdge);

        return child;
    }

    /**
     * Function which evaluates the value (weight) of a cut. If the cut is better than the best cut
     * observed thus far, we update the incumbent.
     * 
     * @param forestGraph graph consisting of two trees representing the cut
     * @param cutWeight weight of the cut
     */
    private void evaluateCut(
        SimpleWeightedGraph<Cluster, DefaultWeightedEdge> forestGraph, double cutWeight)
    {
        // Check whether the proposed cut can improve uppon the best cut found thus far. If not,
        // return.
        if (minimumCutWeight < cutWeight)
            return;

        ConnectivityInspector<Cluster, DefaultWeightedEdge> connectivityInspector =
            new ConnectivityInspector<>(forestGraph);
        // Get the nodes in one of the two trees in the graph
        Cluster node = forestGraph.vertexSet().iterator().next(); // Get a random starting node;
        Set<Cluster> compositePartition = connectivityInspector.connectedSetOf(node);

        // Test whether we have found a parition with an odd number of odd nodes (each composite
        // node contains exactly one odd node)
        if (compositePartition.size() % 2 == 1) {
            this.minimumCutWeight = cutWeight;
            this.sourcePartitionMinimumCut = new LinkedHashSet<>();
            for (Cluster nodeInPartition : compositePartition) {
                sourcePartitionMinimumCut.add(nodeInPartition.clusterHead);
                sourcePartitionMinimumCut.addAll(nodeInPartition.nodes);
            }
        }
    }

    /**
     * Convenience method which test whether the given set contains an odd number of odd-labeled
     * nodes.
     * 
     * @param <V> vertex type
     * @param vertices input set
     * @param oddVertices subset of vertices which are labeled odd
     * @return true if the given set contains an odd number of odd-labeled nodes.
     */
    public static <V> boolean isOddSet(Set<V> vertices, Set<V> oddVertices)
    {
        return vertices.stream().filter(oddVertices::contains).count() % 2 == 1;
    }

    /**
     * Returns partition W of the cut obtained after the last invocation of
     * {@link #calculateMinCut(Set, boolean)}
     *
     * @return partition W
     */
    public Set<V> getSourcePartition()
    {
        return sourcePartitionMinimumCut;
    }

    /**
     * Returns partition V-W of the cut obtained after the last invocation of
     * {@link #calculateMinCut(Set, boolean)}
     *
     * @return partition V-W
     */
    public Set<V> getSinkPartition()
    {
        Set<V> sinkPartition = new LinkedHashSet<>(network.vertexSet());
        sinkPartition.removeAll(sourcePartitionMinimumCut);
        return sinkPartition;
    }

    /**
     * Returns the set of edges which run from the source partition to the sink partition, in the
     * s-t cut obtained after the last invocation of {@link #calculateMinCut(Set, boolean)}
     *
     * @return set of edges which have one endpoint in the source partition and one endpoint in the
     *         sink partition.
     */
    public Set<E> getCutEdges()
    {
        Predicate<E> predicate = e -> sourcePartitionMinimumCut.contains(network.getEdgeSource(e))
            ^ sourcePartitionMinimumCut.contains(network.getEdgeTarget(e));
        return network.edgeSet().stream().filter(predicate).collect(
            Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Helper class which models a group of vertices
     */
    private class Cluster
    {
        /* Cluster head */
        private final V clusterHead;
        /* Vertices in this cluster */
        private Set<V> nodes;
        /* Subset of the vertices in this cluster which are labeled odd */
        private Set<V> oddNodeSubset;

        private Cluster(V clusterHead, Set<V> nodes)
        {
            this.clusterHead = clusterHead;
            this.nodes = nodes;
            oddNodeSubset =
                nodes.stream().filter(oddVertices::contains).collect(Collectors.toSet());
        }

        private void retainAll(Set<V> subset)
        {
            nodes.retainAll(subset);
            oddNodeSubset.retainAll(subset);
        }

        public String toString()
        {
            return clusterHead + ": {" + nodes + "} o(" + oddNodeSubset + ")";
        }
    }
}
