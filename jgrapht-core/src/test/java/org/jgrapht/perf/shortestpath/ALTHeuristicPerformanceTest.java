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
package org.jgrapht.perf.shortestpath;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.ALTAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.generate.GnpRandomGraphGenerator;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.graph.IntegerVertexFactory;
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
public class ALTHeuristicPerformanceTest
    extends TestCase
{

    public static final int PERF_BENCHMARK_VERTICES_COUNT = 100;
    public static final double PERF_BENCHMARK_EDGES_PROP = 0.3;

    @State(Scope.Benchmark)
    private static abstract class RandomGraphBenchmarkBase
    {
        protected static final long SEED = 13l;
        protected Random rng = new Random(SEED);

        private GraphGenerator<Integer, DefaultEdge, Integer> generator = null;
        private DirectedGraph<Integer, DefaultEdge> graph;

        abstract ShortestPathAlgorithm<Integer, DefaultEdge> createSolver(
            Graph<Integer, DefaultEdge> graph);

        @Setup(Level.Iteration)
        public void setup()
        {
            if (generator == null) {
                // lazily construct generator
                generator = new GnpRandomGraphGenerator<>(
                    PERF_BENCHMARK_VERTICES_COUNT, PERF_BENCHMARK_EDGES_PROP, rng, false);
            }

            graph = new DirectedPseudograph<>(DefaultEdge.class);
            generator.generateGraph(graph, new IntegerVertexFactory(), null);
        }

        @Benchmark
        public void run()
        {
            ShortestPathAlgorithm<Integer, DefaultEdge> sp = createSolver(graph);
            for (Integer v : graph.vertexSet()) {
                for (Integer u : graph.vertexSet()) {
                    sp.getPath(v, u);
                }
            }
        }
    }

    public static class DijkstraBenchmark
        extends RandomGraphBenchmarkBase
    {
        @Override
        ShortestPathAlgorithm<Integer, DefaultEdge> createSolver(Graph<Integer, DefaultEdge> graph)
        {
            return new DijkstraShortestPath<>(graph);
        }
    }

    public static class AStarNoHeuristicBenchmark
        extends RandomGraphBenchmarkBase
    {
        @Override
        ShortestPathAlgorithm<Integer, DefaultEdge> createSolver(Graph<Integer, DefaultEdge> graph)
        {
            return new AStarShortestPath<>(graph, (u, t) -> 0d);

        }
    }

    public static class ALTBenchmark
        extends RandomGraphBenchmarkBase
    {
        @Override
        ShortestPathAlgorithm<Integer, DefaultEdge> createSolver(Graph<Integer, DefaultEdge> graph)
        {
            Integer[] vertices = graph.vertexSet().toArray(new Integer[0]);
            Set<Integer> landmarks = new HashSet<>();
            while (landmarks.size() < 3) {
                landmarks.add(vertices[rng.nextInt(graph.vertexSet().size())]);
            }
            return new AStarShortestPath<>(graph, new ALTAdmissibleHeuristic<>(graph, landmarks));

        }
    }

    public void testPathGrowingRandomGraphBenchmark()
        throws RunnerException
    {
        Options opt = new OptionsBuilder()
            .include(".*" + DijkstraBenchmark.class.getSimpleName() + ".*")
            .mode(Mode.SingleShotTime).include(".*" + ALTBenchmark.class.getSimpleName() + ".*")
            .mode(Mode.SingleShotTime)
            .include(".*" + AStarNoHeuristicBenchmark.class.getSimpleName() + ".*")
            .mode(Mode.SingleShotTime).timeUnit(TimeUnit.MILLISECONDS).warmupIterations(5)
            .measurementIterations(10).forks(1).shouldFailOnError(true).shouldDoGC(true).build();

        new Runner(opt).run();
    }
}
