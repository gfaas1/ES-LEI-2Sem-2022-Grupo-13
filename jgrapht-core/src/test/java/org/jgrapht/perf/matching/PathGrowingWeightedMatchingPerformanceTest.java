/*
 * (C) Copyright 2016-2016, by Dimitrios Michail and Contributors.
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
package org.jgrapht.perf.matching;

import java.util.concurrent.TimeUnit;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.VertexFactory;
import org.jgrapht.alg.EdmondsBlossomShrinking;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.matching.GreedyWeightedMatching;
import org.jgrapht.alg.matching.PathGrowingWeightedMatching;
import org.jgrapht.generate.GnpRandomGraphGenerator;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Pseudograph;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import junit.framework.TestCase;

/**
 * A small benchmark comparing matching algorithms.
 * 
 * @author Dimitrios Michail
 */
public class PathGrowingWeightedMatchingPerformanceTest
    extends TestCase
{

    public static final int PERF_BENCHMARK_VERTICES_COUNT = 1000;
    public static final double PERF_BENCHMARK_EDGES_PROP = 0.8;

    @State(Scope.Benchmark)
    private static abstract class RandomGraphBenchmarkBase
    {
        public static final long SEED = 13l;

        private GraphGenerator<Integer, DefaultEdge, Integer> generator = null;
        private UndirectedGraph<Integer, DefaultEdge> graph;

        abstract MatchingAlgorithm<Integer, DefaultEdge> createSolver(
            UndirectedGraph<Integer, DefaultEdge> graph);

        @Setup(Level.Iteration)
        public void setup()
        {
            if (generator == null) {
                // lazily construct generator
                generator = new GnpRandomGraphGenerator<>(
                    PERF_BENCHMARK_VERTICES_COUNT, PERF_BENCHMARK_EDGES_PROP, SEED, false);
            }

            graph = new Pseudograph<>(DefaultEdge.class);

            generator.generateGraph(graph, new VertexFactory<Integer>()
            {
                int i;

                @Override
                public Integer createVertex()
                {
                    return ++i;
                }
            }, null);
        }

        @Benchmark
        public void run()
        {
            createSolver(graph).getMatching();
        }
    }

    public static class PathGrowingWeightedMatchingRandomGraphBenchmark
        extends RandomGraphBenchmarkBase
    {
        @Override
        MatchingAlgorithm<Integer, DefaultEdge> createSolver(
            UndirectedGraph<Integer, DefaultEdge> graph)
        {
            return new PathGrowingWeightedMatching<>(graph);
        }
    }

    public static class PathGrowingWeightedMatchingNoHeuristicsRandomGraphBenchmark
        extends RandomGraphBenchmarkBase
    {
        @Override
        MatchingAlgorithm<Integer, DefaultEdge> createSolver(
            UndirectedGraph<Integer, DefaultEdge> graph)
        {
            final boolean useHeuristics = false;
            return new PathGrowingWeightedMatching<>(graph, useHeuristics);
        }
    }

    public static class GreedyWeightedMatchingRandomGraphBenchmark
        extends RandomGraphBenchmarkBase
    {
        @Override
        MatchingAlgorithm<Integer, DefaultEdge> createSolver(
            UndirectedGraph<Integer, DefaultEdge> graph)
        {
            return new GreedyWeightedMatching<>(graph);
        }
    }

    public static class EdmondsBlossomShrinkingRandomGraphBenchmark
        extends RandomGraphBenchmarkBase
    {
        @Override
        MatchingAlgorithm<Integer, DefaultEdge> createSolver(
            UndirectedGraph<Integer, DefaultEdge> graph)
        {
            return new EdmondsBlossomShrinking<>(graph);
        }
    }

    public void testPathGrowingRandomGraphBenchmark()
        throws RunnerException
    {
        Options opt = new OptionsBuilder()
            .include(
                ".*" + PathGrowingWeightedMatchingRandomGraphBenchmark.class.getSimpleName() + ".*")
            .include(
                ".*" + PathGrowingWeightedMatchingNoHeuristicsRandomGraphBenchmark.class
                    .getSimpleName() + ".*")
            .include(".*" + GreedyWeightedMatchingRandomGraphBenchmark.class.getSimpleName() + ".*")
            .include(
                ".*" + EdmondsBlossomShrinkingRandomGraphBenchmark.class.getSimpleName() + ".*")
            .mode(Mode.SingleShotTime).timeUnit(TimeUnit.MILLISECONDS).warmupIterations(5)
            .measurementIterations(10).forks(1).shouldFailOnError(true).shouldDoGC(true).build();

        new Runner(opt).run();
    }
}
