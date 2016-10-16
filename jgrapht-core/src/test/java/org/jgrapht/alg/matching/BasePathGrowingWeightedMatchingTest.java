package org.jgrapht.alg.matching;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.interfaces.WeightedMatchingAlgorithm;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.generate.GnpRandomGraphGenerator;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.IntegerVertexFactory;
import org.jgrapht.graph.WeightedPseudograph;

public abstract class BasePathGrowingWeightedMatchingTest
    extends ApproximateWeightedMatchingTest
{

    public BasePathGrowingWeightedMatchingTest()
    {
        super();
    }

    public abstract WeightedMatchingAlgorithm<Integer,
        DefaultWeightedEdge> getApproximationAlgorithm(Graph<Integer, DefaultWeightedEdge> graph);

    public void testDynamicProgrammingOnPaths()
    {
        // test 0
        WeightedPseudograph<Integer, DefaultWeightedEdge> g =
            new WeightedPseudograph<>(DefaultWeightedEdge.class);

        PathGrowingWeightedMatching<Integer, DefaultWeightedEdge> pathGrowingAlgo =
            new PathGrowingWeightedMatching<>(g);

        Graphs.addAllVertices(g, Arrays.asList(0, 1, 2, 3, 4, 5, 6));
        LinkedList<DefaultWeightedEdge> path = new LinkedList<>();
        path.add(Graphs.addEdge(g, 0, 1, 5.0));
        path.add(Graphs.addEdge(g, 1, 2, 2.5));
        path.add(Graphs.addEdge(g, 2, 3, 5.0));
        path.add(Graphs.addEdge(g, 3, 4, 2.5));
        path.add(Graphs.addEdge(g, 4, 5, 2.5));
        path.add(Graphs.addEdge(g, 5, 6, 5.0));

        PathGrowingWeightedMatching<Integer,
            DefaultWeightedEdge>.DynamicProgrammingPathSolver pathSolver =
                pathGrowingAlgo.new DynamicProgrammingPathSolver();
        Pair<Double, Set<DefaultWeightedEdge>> result =
            pathSolver.getMaximumWeightMatching(g, path);
        double weight = result.getFirst();
        assertEquals(15.0, weight, WeightedMatchingAlgorithm.DEFAULT_EPSILON);
        Set<DefaultWeightedEdge> matching = result.getSecond();
        assertEquals(3, matching.size());
        assertTrue(matching.contains(g.getEdge(0, 1)));
        assertTrue(matching.contains(g.getEdge(2, 3)));
        assertTrue(matching.contains(g.getEdge(5, 6)));

        // test 1 (empty path)
        LinkedList<DefaultWeightedEdge> path1 = new LinkedList<>();
        Pair<Double, Set<DefaultWeightedEdge>> result1 =
            pathSolver.getMaximumWeightMatching(g, path1);
        double weight1 = result1.getFirst();
        assertEquals(0.0, weight1, WeightedMatchingAlgorithm.DEFAULT_EPSILON);
        Set<DefaultWeightedEdge> matching1 = result1.getSecond();
        assertEquals(0, matching1.size());

        // test 2 (single edge)
        Graphs.addAllVertices(g, Arrays.asList(7, 8));
        LinkedList<DefaultWeightedEdge> path2 = new LinkedList<>();
        path2.add(Graphs.addEdge(g, 7, 8, 100.0));
        Pair<Double, Set<DefaultWeightedEdge>> result2 =
            pathSolver.getMaximumWeightMatching(g, path2);
        double weight2 = result2.getFirst();
        assertEquals(100.0, weight2, WeightedMatchingAlgorithm.DEFAULT_EPSILON);
        Set<DefaultWeightedEdge> matching2 = result2.getSecond();
        assertEquals(1, matching2.size());
        assertTrue(matching2.contains(g.getEdge(7, 8)));

        // test 3 (two edges)
        Graphs.addAllVertices(g, Arrays.asList(9, 10, 11));
        LinkedList<DefaultWeightedEdge> path3 = new LinkedList<>();
        path3.add(Graphs.addEdge(g, 9, 10, 10.0));
        path3.add(Graphs.addEdge(g, 10, 11, 15.0));
        Pair<Double, Set<DefaultWeightedEdge>> result3 =
            pathSolver.getMaximumWeightMatching(g, path3);
        double weight3 = result3.getFirst();
        assertEquals(15.0, weight3, WeightedMatchingAlgorithm.DEFAULT_EPSILON);
        Set<DefaultWeightedEdge> matching3 = result3.getSecond();
        assertEquals(1, matching3.size());
        assertTrue(matching3.contains(g.getEdge(10, 11)));
    }

    public void testApproximationFactorOnRandomInstances()
    {
        final int seed = 33;
        final double edgeProbability = 0.7;
        final int numberVertices = 100;
        final int repeat = 10;

        GraphGenerator<Integer, DefaultWeightedEdge, Integer> gg =
            new GnpRandomGraphGenerator<Integer, DefaultWeightedEdge>(
                numberVertices, edgeProbability, seed, false);

        for (int i = 0; i < repeat; i++) {
            WeightedPseudograph<Integer, DefaultWeightedEdge> g =
                new WeightedPseudograph<>(DefaultWeightedEdge.class);
            gg.generateGraph(g, new IntegerVertexFactory(), null);

            MatchingAlgorithm<Integer, DefaultWeightedEdge> alg1 =
                new PathGrowingWeightedMatching<>(g);
            Set<DefaultWeightedEdge> m1 = alg1.getMatching();
            MatchingAlgorithm<Integer, DefaultWeightedEdge> alg2 =
                new PathGrowingWeightedMatching<>(g, false);
            Set<DefaultWeightedEdge> m2 = alg2.getMatching();
            MatchingAlgorithm<Integer, DefaultWeightedEdge> alg3 = new EdmondsBlossomShrinking<>(g);
            Set<DefaultWeightedEdge> m3 = alg3.getMatching();
            MatchingAlgorithm<Integer, DefaultWeightedEdge> alg4 = new GreedyWeightedMatching<>(g);
            Set<DefaultWeightedEdge> m4 = alg4.getMatching();

            assertTrue(isMatching(g, m1));
            assertTrue(isMatching(g, m2));
            assertTrue(isMatching(g, m3));
            assertTrue(isMatching(g, m4));
            assertTrue(m1.size() >= 0.5 * m3.size());
            assertTrue(m2.size() >= 0.5 * m3.size());
            assertTrue(m4.size() >= 0.5 * m3.size());
        }
    }

}
