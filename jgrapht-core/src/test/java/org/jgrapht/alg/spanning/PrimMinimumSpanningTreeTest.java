package org.jgrapht.alg.spanning;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.util.IntegerVertexFactory;
import org.jgrapht.generate.GnpRandomGraphGenerator;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.WeightedPseudograph;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static org.jgrapht.alg.spanning.MinimumSpanningTreeTest.*;
import static org.junit.Assert.assertEquals;

public class PrimMinimumSpanningTreeTest {

    @Test
    public void testRandomInstances()
    {
        final Random rng = new Random(33);
        final double edgeProbability = 0.5;
        final int numberVertices = 100;
        final int repeat = 200;

        GraphGenerator<Integer, DefaultWeightedEdge, Integer> gg =
                new GnpRandomGraphGenerator<>(
                        numberVertices, edgeProbability, rng, false);

        for (int i = 0; i < repeat; i++) {
            WeightedPseudograph<Integer, DefaultWeightedEdge> g =
                    new WeightedPseudograph<>(DefaultWeightedEdge.class);
            gg.generateGraph(g, new IntegerVertexFactory(), null);

            for (DefaultWeightedEdge e : g.edgeSet()) {
                g.setEdgeWeight(e, rng.nextDouble());
            }

            PrimMinimumSpanningTree<Integer, DefaultWeightedEdge> alg = new PrimMinimumSpanningTree<>(g);

            SpanningTreeAlgorithm.SpanningTree<DefaultWeightedEdge> tree1 = alg.getSpanningTreeDense();
            SpanningTreeAlgorithm.SpanningTree<DefaultWeightedEdge> tree2 = new KruskalMinimumSpanningTree<>(g).getSpanningTree();
            SpanningTreeAlgorithm.SpanningTree<DefaultWeightedEdge> tree3 = alg.getSpanningTree();

            assertEquals(tree1.getWeight(), tree2.getWeight(), 1e-9);
            assertEquals(tree2.getWeight(), tree3.getWeight(), 1e-9);
        }
    }

    @Test
    public void testPrim()
    {
        testMinimumSpanningTreeBuilding(
                new PrimMinimumSpanningTree<>(
                        MinimumSpanningTreeTest.createSimpleConnectedWeightedGraph()).getSpanningTree(),
                Arrays.asList(AB, AC, BD, DE), 15.0);

        testMinimumSpanningTreeBuilding(
                new PrimMinimumSpanningTree<>(
                        MinimumSpanningTreeTest.createSimpleConnectedWeightedGraph()).getSpanningTreeDense(),
                Arrays.asList(AB, AC, BD, DE), 15.0);

        testMinimumSpanningTreeBuilding(
                new PrimMinimumSpanningTree<>(
                        createSimpleDisconnectedWeightedGraph()).getSpanningTree(),
                Arrays.asList(AB, AC, BD, EG, GH, FH), 60.0);

        testMinimumSpanningTreeBuilding(
                new PrimMinimumSpanningTree<>(
                        createSimpleDisconnectedWeightedGraph()).getSpanningTreeDense(),
                Arrays.asList(AB, AC, BD, EG, GH, FH), 60.0);
    }

    /*
        The above code is to test time diff between getSpanningTree and getSpanningTreeDense
     */

    private static Graph<Integer, DefaultWeightedEdge> bigGraph;

    @BeforeClass
    public static void generateBigGraph(){
        Random random = new Random(991199);
        bigGraph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        final int N = 2_000;

        for (int i = 0; i < N; i++) {
            bigGraph.addVertex(i);
        }

        int P = 70;

        for (int i = 0; i < N; i++) {
            for (int j = i + 1; j < N; j++) {
                if (1 + random.nextInt(100) <= P){
                    bigGraph.setEdgeWeight(bigGraph.addEdge(i, j), 1 + random.nextInt(100));
                }
            }
        }
    }

    @Test
    public void testDenseGraphNormal(){
        assertEquals(new PrimMinimumSpanningTree<>(bigGraph).getSpanningTree().getWeight(), 1999, 1e-9);
    }

    @Test
    public void testDenseGraphSpecial(){
        assertEquals(new PrimMinimumSpanningTree<>(bigGraph).getSpanningTreeDense().getWeight(), 1999, 1e-9);
    }
}