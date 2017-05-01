/*
 * (C) Copyright 2016-2017, by Dimitrios Michail and Contributors.
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

import junit.framework.TestCase;
import org.jgrapht.Graph;
import org.jgrapht.VertexFactory;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.matching.*;
import org.jgrapht.generate.GnpRandomGraphGenerator;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Pseudograph;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * A small benchmark comparing matching algorithms.
 * 
 * @author Dimitrios Michail
 */
public class MaxCardinalityMatchingPerformanceTest
    extends TestCase
{

    public static final int PERF_BENCHMARK_VERTICES_COUNT = 2000;
    public static final double PERF_BENCHMARK_EDGES_PROP = 0.7;

    @State(Scope.Benchmark)
    private static abstract class RandomGraphBenchmarkBase
    {
        public static final long SEED = 13l;

        private GraphGenerator<Integer, DefaultEdge, Integer> generator = null;
        private Graph<Integer, DefaultEdge> graph;

        abstract MatchingAlgorithm<Integer, DefaultEdge> createSolver(
            Graph<Integer, DefaultEdge> graph);

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
            long time=System.currentTimeMillis();
            MatchingAlgorithm.Matching m =createSolver(graph).getMatching();
            time=System.currentTimeMillis()-time;
            System.out.println("time: "+time+" obj :"+m.getEdges().size()+" vertices: "+graph.vertexSet().size()+" edges: "+graph.edgeSet().size());
        }
    }


    public static class EdmondsBlossomShrinkingBenchmark
        extends RandomGraphBenchmarkBase
    {
        @Override
        MatchingAlgorithm<Integer, DefaultEdge> createSolver(Graph<Integer, DefaultEdge> graph)
        {
            return new EdmondsBlossomShrinking<>(graph);
        }
    }

    public static class EdmondsMaxCardinalityMatchingBenchmark
            extends RandomGraphBenchmarkBase
    {
        @Override
        MatchingAlgorithm<Integer, DefaultEdge> createSolver(Graph<Integer, DefaultEdge> graph)
        {
            return new EdmondsMaxCardinalityMatching<>(graph);
        }
    }

    public static class EdmondsMaxCardinalityMatchingBaseLineComparisonBenchmark
            extends RandomGraphBenchmarkBase
    {
        @Override
        MatchingAlgorithm<Integer, DefaultEdge> createSolver(Graph<Integer, DefaultEdge> graph)
        {
            return new EdmondsMaxCardinalityMatchingBaseLineComparison<>(graph, new GreedyMaxCardinalityMatching<>(graph, false));
        }
    }

    public void testRandomGraphBenchmark()
        throws RunnerException
    {
        Options opt = new OptionsBuilder()
//                .include(
//                        ".*" + EdmondsBlossomShrinkingBenchmark.class.getSimpleName() + ".*")
            .include(
                ".*" + EdmondsMaxCardinalityMatchingBenchmark.class.getSimpleName() + ".*")
            .include(
                ".*" + EdmondsMaxCardinalityMatchingBaseLineComparisonBenchmark.class.getSimpleName() + ".*")
//            .include(
//                    ".*" + EdmondsBlossomShrinkingWarmstartBenchmark.class.getSimpleName() + ".*")
            .mode(Mode.SingleShotTime).timeUnit(TimeUnit.MILLISECONDS).warmupIterations(5)
            .measurementIterations(10).forks(1).shouldFailOnError(true).shouldDoGC(true).build();

        new Runner(opt).run();
    }
}
