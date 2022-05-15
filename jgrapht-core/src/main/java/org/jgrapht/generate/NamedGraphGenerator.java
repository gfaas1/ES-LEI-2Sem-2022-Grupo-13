/*
 * (C) Copyright 2017-2021, by Joris Kinable and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * See the CONTRIBUTORS.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the
 * GNU Lesser General Public License v2.1 or later
 * which is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1-standalone.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR LGPL-2.1-or-later
 */
package org.jgrapht.generate;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.graph.builder.*;
import org.jgrapht.util.*;

import java.util.*;

/**
 * Collection of commonly used named graphs
 *
 * @author Joris Kinable
 *
 * @param <V> graph vertex type
 * @param <E> graph edge type
 */
public class NamedGraphGenerator<V, E>
{
    private Map<Integer, V> vertexMap;

    /**
     * Constructs a new generator for named graphs
     */
    public NamedGraphGenerator()
    {
        vertexMap = new HashMap<>();
    }

    // -------------Doyle Graph-----------//
    /**
     * Generate the Doyle Graph
     * 
     * @see #generateDoyleGraph
     * @return Doyle Graph
     */
    public static Graph<Integer, DefaultEdge> doyleGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateDoyleGraph(g);
        return g;
    }

    /**
     * Generates a <a href="http://mathworld.wolfram.com/DoyleGraph.html">Doyle Graph</a>. The Doyle
     * graph, sometimes also known as the Holt graph (Marušic et al. 2005), is the quartic symmetric
     * graph on 27 nodes
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateDoyleGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 3; j++) {
                this
                    .addEdge(
                        targetGraph, doyleHash(i, j), doyleHash(mod(4 * i + 1, 9), mod(j - 1, 3)));
                this
                    .addEdge(
                        targetGraph, doyleHash(i, j), doyleHash(mod(4 * i - 1, 9), mod(j - 1, 3)));
                this
                    .addEdge(
                        targetGraph, doyleHash(i, j), doyleHash(mod(7 * i + 7, 9), mod(j + 1, 3)));
                this
                    .addEdge(
                        targetGraph, doyleHash(i, j), doyleHash(mod(7 * i - 7, 9), mod(j + 1, 3)));
            }
    }

    private int doyleHash(int u, int v)
    {
        return u * 19 + v;
    }

    private int mod(int u, int m)
    {
        int r = u % m;
        return r < 0 ? r + m : r;
    }

    // -------------Generalized Petersen Graph-----------//

    /**
     * @see GeneralizedPetersenGraphGenerator
     * @param n Generalized Petersen graphs $GP(n,k)$
     * @param k Generalized Petersen graphs $GP(n,k)$
     * @return Generalized Petersen Graph
     */
    public static Graph<Integer, DefaultEdge> generalizedPetersenGraph(int n, int k)
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateGeneralizedPetersenGraph(g, n, k);
        return g;
    }

    private void generateGeneralizedPetersenGraph(Graph<V, E> targetGraph, int n, int k)
    {
        GeneralizedPetersenGraphGenerator<V, E> gpgg =
            new GeneralizedPetersenGraphGenerator<>(n, k);
        gpgg.generateGraph(targetGraph);
    }

    // -------------Petersen Graph-----------//
    /**
     * @see #generatePetersenGraph
     * @return Petersen Graph
     */
    public static Graph<Integer, DefaultEdge> petersenGraph()
    {
        return generalizedPetersenGraph(5, 2);
    }

    /**
     * Generates a <a href="http://mathworld.wolfram.com/PetersenGraph.html">Petersen Graph</a>. The
     * Petersen Graph is a named graph that consists of 10 vertices and 15 edges, usually drawn as a
     * five-point star embedded in a pentagon. It is the generalized Petersen graph $GP(5,2)$
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generatePetersenGraph(Graph<V, E> targetGraph)
    {
        generateGeneralizedPetersenGraph(targetGraph, 5, 2);
    }

    // -------------Dürer Graph-----------//
    /**
     * Generates a <a href="http://mathworld.wolfram.com/DuererGraph.html">Dürer Graph</a>. The
     * Dürer graph is the skeleton of Dürer's solid, which is the generalized Petersen graph
     * $GP(6,2)$.
     * 
     * @return the Dürer Graph
     */
    public static Graph<Integer, DefaultEdge> durerGraph()
    {
        return generalizedPetersenGraph(6, 2);
    }

    /**
     * Generates a <a href="http://mathworld.wolfram.com/DuererGraph.html">Dürer Graph</a>. The
     * Dürer graph is the skeleton of Dürer's solid, which is the generalized Petersen graph
     * $GP(6,2)$.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateDurerGraph(Graph<V, E> targetGraph)
    {
        generateGeneralizedPetersenGraph(targetGraph, 6, 2);
    }

    // -------------Dodecahedron Graph-----------//
    /**
     * @see #generateDodecahedronGraph
     * @return Dodecahedron Graph
     */
    public static Graph<Integer, DefaultEdge> dodecahedronGraph()
    {
        return generalizedPetersenGraph(10, 2);
    }

    /**
     * Generates a <a href="http://mathworld.wolfram.com/DodecahedralGraph.html">Dodecahedron
     * Graph</a>. The skeleton of the dodecahedron (the vertices and edges) form a graph. It is one
     * of 5 Platonic graphs, each a skeleton of its Platonic solid. It is the generalized Petersen
     * graph $GP(10,2)$
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateDodecahedronGraph(Graph<V, E> targetGraph)
    {
        generateGeneralizedPetersenGraph(targetGraph, 10, 2);
    }

    // -------------Desargues Graph-----------//
    /**
     * @see #generateDesarguesGraph
     * @return Desargues Graph
     */
    public static Graph<Integer, DefaultEdge> desarguesGraph()
    {
        return generalizedPetersenGraph(10, 3);
    }

    /**
     * Generates a <a href="http://mathworld.wolfram.com/DesarguesGraph.html">Desargues Graph</a>.
     * The Desargues graph is a cubic symmetric graph distance-regular graph on 20 vertices and 30
     * edges. It is the generalized Petersen graph $GP(10,3)$
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateDesarguesGraph(Graph<V, E> targetGraph)
    {
        generateGeneralizedPetersenGraph(targetGraph, 10, 3);
    }

    // -------------Nauru Graph-----------//
    /**
     * @see #generateNauruGraph
     * @return Nauru Graph
     */
    public static Graph<Integer, DefaultEdge> nauruGraph()
    {
        return generalizedPetersenGraph(12, 5);
    }

    /**
     * Generates a <a href="http://mathworld.wolfram.com/NauruGraph.html">Nauru Graph</a>. The Nauru
     * graph is a symmetric bipartite cubic graph with 24 vertices and 36 edges. It is the
     * generalized Petersen graph $GP(12,5)$
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateNauruGraph(Graph<V, E> targetGraph)
    {
        generateGeneralizedPetersenGraph(targetGraph, 12, 5);
    }

    // -------------Möbius-Kantor Graph-----------//
    /**
     * Generates a <a href="http://mathworld.wolfram.com/Moebius-KantorGraph.html">Möbius-Kantor
     * Graph</a>. The unique cubic symmetric graph on 16 nodes. It is the generalized Petersen graph
     * $GP(8,3)$
     * 
     * @return the Möbius-Kantor Graph
     */
    public static Graph<Integer, DefaultEdge> mobiusKantorGraph()
    {
        return generalizedPetersenGraph(8, 3);
    }

    /**
     * Generates a <a href="http://mathworld.wolfram.com/Moebius-KantorGraph.html">Möbius-Kantor
     * Graph</a>. The unique cubic symmetric graph on 16 nodes. It is the generalized Petersen graph
     * $GP(8,3)$
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateMobiusKantorGraph(Graph<V, E> targetGraph)
    {
        generateGeneralizedPetersenGraph(targetGraph, 8, 3);
    }

    // -------------Bull Graph-----------//
    /**
     * @see #generateBullGraph
     * @return Bull Graph
     */
    public static Graph<Integer, DefaultEdge> bullGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateBullGraph(g);
        return g;
    }

    /**
     * Generates a <a href="http://mathworld.wolfram.com/BullGraph.html">Bull Graph</a>. The bull
     * graph is a simple graph on 5 nodes and 5 edges whose name derives from its resemblance to a
     * schematic illustration of a bull or ram
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateBullGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        this.addEdge(targetGraph, 0, 1);
        this.addEdge(targetGraph, 1, 2);
        this.addEdge(targetGraph, 2, 3);
        this.addEdge(targetGraph, 1, 3);
        this.addEdge(targetGraph, 3, 4);
    }

    // -------------Butterfly Graph-----------//
    /**
     * @see #generateButterflyGraph
     * @return Butterfly Graph
     */
    public static Graph<Integer, DefaultEdge> butterflyGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateButterflyGraph(g);
        return g;
    }

    /**
     * Generates a <a href="http://mathworld.wolfram.com/ButterflyGraph.html">Butterfly Graph</a>.
     * This graph is also known as the "bowtie graph" (West 2000, p. 12). It is isomorphic to the
     * friendship graph $F_2$.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateButterflyGraph(Graph<V, E> targetGraph)
    {
        new WindmillGraphsGenerator<V, E>(WindmillGraphsGenerator.Mode.DUTCHWINDMILL, 2, 3)
            .generateGraph(targetGraph);
    }

    // -------------Claw Graph-----------//
    /**
     * @see #generateClawGraph
     * @return Claw Graph
     */
    public static Graph<Integer, DefaultEdge> clawGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateClawGraph(g);
        return g;
    }

    /**
     * Generates a <a href="http://mathworld.wolfram.com/ClawGraph.html">Claw Graph</a>. The
     * complete bipartite graph $K_{1,3}$ is a tree known as the "claw."
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateClawGraph(Graph<V, E> targetGraph)
    {
        new StarGraphGenerator<V, E>(4).generateGraph(targetGraph);
    }

    // -------------Bucky ball Graph-----------//
    /**
     * @see #generateBuckyBallGraph
     * @return Bucky ball Graph
     */
    public static Graph<Integer, DefaultEdge> buckyBallGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateBuckyBallGraph(g);
        return g;
    }

    /**
     * Generates a <a href="https://en.wikipedia.org/wiki/Fullerene">Bucky ball Graph</a>. This
     * graph is a 3-regular 60-vertex planar graph. Its vertices and edges correspond precisely to
     * the carbon atoms and bonds in buckminsterfullerene. When embedded on a sphere, its 12
     * pentagon and 20 hexagon faces are arranged exactly as the sections of a soccer ball.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateBuckyBallGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int[][] edges = { { 0, 2 }, { 0, 48 }, { 0, 59 }, { 1, 3 }, { 1, 9 }, { 1, 58 }, { 2, 3 },
            { 2, 36 }, { 3, 17 }, { 4, 6 }, { 4, 8 }, { 4, 12 }, { 5, 7 }, { 5, 9 }, { 5, 16 },
            { 6, 7 }, { 6, 20 }, { 7, 21 }, { 8, 9 }, { 8, 56 }, { 10, 11 }, { 10, 12 }, { 10, 20 },
            { 11, 27 }, { 11, 47 }, { 12, 13 }, { 13, 46 }, { 13, 54 }, { 14, 15 }, { 14, 16 },
            { 14, 21 }, { 15, 25 }, { 15, 41 }, { 16, 17 }, { 17, 40 }, { 18, 19 }, { 18, 20 },
            { 18, 26 }, { 19, 21 }, { 19, 24 }, { 22, 23 }, { 22, 31 }, { 22, 34 }, { 23, 25 },
            { 23, 38 }, { 24, 25 }, { 24, 30 }, { 26, 27 }, { 26, 30 }, { 27, 29 }, { 28, 29 },
            { 28, 31 }, { 28, 35 }, { 29, 44 }, { 30, 31 }, { 32, 34 }, { 32, 39 }, { 32, 50 },
            { 33, 35 }, { 33, 45 }, { 33, 51 }, { 34, 35 }, { 36, 37 }, { 36, 40 }, { 37, 39 },
            { 37, 52 }, { 38, 39 }, { 38, 41 }, { 40, 41 }, { 42, 43 }, { 42, 46 }, { 42, 55 },
            { 43, 45 }, { 43, 53 }, { 44, 45 }, { 44, 47 }, { 46, 47 }, { 48, 49 }, { 48, 52 },
            { 49, 53 }, { 49, 57 }, { 50, 51 }, { 50, 52 }, { 51, 53 }, { 54, 55 }, { 54, 56 },
            { 55, 57 }, { 56, 58 }, { 57, 59 }, { 58, 59 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Clebsch Graph-----------//
    /**
     * @see #generateClebschGraph
     * @return Clebsch Graph
     */
    public static Graph<Integer, DefaultEdge> clebschGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateClebschGraph(g);
        return g;
    }

    /**
     * Generates a <a href="http://mathworld.wolfram.com/ClebschGraph.html">Clebsch Graph</a>. The
     * Clebsch graph, also known as the Greenwood-Gleason graph (Read and Wilson, 1998, p. 284), is
     * a strongly regular quintic graph on 16 vertices and 40 edges.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateClebschGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int x = 0;
        for (int i = 0; i < 8; i++) {
            addEdge(targetGraph, x % 16, (x + 1) % 16);
            addEdge(targetGraph, x % 16, (x + 6) % 16);
            addEdge(targetGraph, x % 16, (x + 8) % 16);
            x++;
            addEdge(targetGraph, x % 16, (x + 3) % 16);
            addEdge(targetGraph, x % 16, (x + 2) % 16);
            addEdge(targetGraph, x % 16, (x + 8) % 16);
            x++;
        }
    }

    // -------------Grötzsch Graph-----------//
    /**
     * Generates a <a href="http://mathworld.wolfram.com/GroetzschGraph.html">Grötzsch Graph</a>.
     * The Grötzsch graph is smallest triangle-free graph with chromatic number four.
     * 
     * @return the Grötzsch Graph
     */
    public static Graph<Integer, DefaultEdge> grotzschGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateGrotzschGraph(g);
        return g;
    }

    /**
     * Generates a <a href="http://mathworld.wolfram.com/GroetzschGraph.html">Grötzsch Graph</a>.
     * The Grötzsch graph is smallest triangle-free graph with chromatic number four.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateGrotzschGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        for (int i = 1; i < 6; i++)
            addEdge(targetGraph, 0, i);
        addEdge(targetGraph, 10, 6);
        for (int i = 6; i < 10; i++) {
            addEdge(targetGraph, i, i + 1);
            addEdge(targetGraph, i, i - 4);
        }
        addEdge(targetGraph, 10, 1);
        for (int i = 7; i < 11; i++)
            addEdge(targetGraph, i, i - 6);
        addEdge(targetGraph, 6, 5);
    }

    // -------------Bidiakis cube Graph-----------//
    /**
     * @see #generateBidiakisCubeGraph
     * @return Bidiakis cube Graph
     */
    public static Graph<Integer, DefaultEdge> bidiakisCubeGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateBidiakisCubeGraph(g);
        return g;
    }

    /**
     * Generates a <a href="http://mathworld.wolfram.com/BidiakisCube.html">Bidiakis cube Graph</a>.
     * The 12-vertex graph consisting of a cube in which two opposite faces (say, top and bottom)
     * have edges drawn across them which connect the centers of opposite sides of the faces in such
     * a way that the orientation of the edges added on top and bottom are perpendicular to each
     * other.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateBidiakisCubeGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int[][] edges = { { 0, 1 }, { 0, 6 }, { 0, 11 }, { 1, 2 }, { 1, 5 }, { 2, 3 }, { 2, 10 },
            { 3, 4 }, { 3, 9 }, { 4, 5 }, { 4, 8 }, { 5, 6 }, { 6, 7 }, { 7, 8 }, { 7, 11 },
            { 8, 9 }, { 9, 10 }, { 10, 11 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------First Blanusa Snark Graph-----------//
    /**
     * @see #generateBlanusaFirstSnarkGraph
     * @return First Blanusa Snark Graph
     */
    public static Graph<Integer, DefaultEdge> blanusaFirstSnarkGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateBlanusaFirstSnarkGraph(g);
        return g;
    }

    /**
     * Generates the <a href="http://mathworld.wolfram.com/BlanusaSnarks.html">First Blanusa Snark
     * Graph</a>. The Blanusa graphs are two snarks on 18 vertices and 27 edges.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateBlanusaFirstSnarkGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int[][] edges = { { 0, 1 }, { 0, 5 }, { 0, 16 }, { 1, 2 }, { 1, 17 }, { 2, 3 }, { 2, 14 },
            { 3, 4 }, { 3, 8 }, { 4, 5 }, { 4, 17 }, { 5, 6 }, { 6, 7 }, { 6, 11 }, { 7, 8 },
            { 7, 17 }, { 8, 9 }, { 9, 10 }, { 9, 13 }, { 10, 11 }, { 10, 15 }, { 11, 12 },
            { 12, 13 }, { 12, 16 }, { 13, 14 }, { 14, 15 }, { 15, 16 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Second Blanusa Snark Graph-----------//
    /**
     * @see #generateBlanusaSecondSnarkGraph
     * @return Second Blanusa Snark Graph
     */
    public static Graph<Integer, DefaultEdge> blanusaSecondSnarkGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateBlanusaSecondSnarkGraph(g);
        return g;
    }

    /**
     * Generates the <a href="http://mathworld.wolfram.com/BlanusaSnarks.html">Second Blanusa Snark
     * Graph</a>. The Blanusa graphs are two snarks on 18 vertices and 27 edges.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateBlanusaSecondSnarkGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int[][] edges = { { 0, 1 }, { 0, 2 }, { 0, 14 }, { 1, 5 }, { 1, 11 }, { 2, 3 }, { 2, 6 },
            { 3, 4 }, { 3, 9 }, { 4, 5 }, { 4, 7 }, { 5, 6 }, { 6, 8 }, { 7, 8 }, { 7, 17 },
            { 8, 9 }, { 9, 15 }, { 10, 11 }, { 10, 14 }, { 10, 16 }, { 11, 12 }, { 12, 13 },
            { 12, 17 }, { 13, 14 }, { 13, 15 }, { 15, 16 }, { 16, 17 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Double Star Snark Graph-----------//
    /**
     * @see #generateDoubleStarSnarkGraph
     * @return Double Star Snark Graph
     */
    public static Graph<Integer, DefaultEdge> doubleStarSnarkGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateDoubleStarSnarkGraph(g);
        return g;
    }

    /**
     * Generates the <a href="http://mathworld.wolfram.com/DoubleStarSnark.html">Double Star Snark
     * Graph</a>. A snark on 30 vertices with edge chromatic number 4.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateDoubleStarSnarkGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int[][] edges = { { 0, 1 }, { 0, 14 }, { 0, 15 }, { 1, 2 }, { 1, 11 }, { 2, 3 }, { 2, 7 },
            { 3, 4 }, { 3, 18 }, { 4, 5 }, { 4, 14 }, { 5, 6 }, { 5, 10 }, { 6, 7 }, { 6, 21 },
            { 7, 8 }, { 8, 9 }, { 8, 13 }, { 9, 10 }, { 9, 24 }, { 10, 11 }, { 11, 12 }, { 12, 13 },
            { 12, 27 }, { 13, 14 }, { 15, 16 }, { 15, 29 }, { 16, 20 }, { 16, 23 }, { 17, 18 },
            { 17, 25 }, { 17, 28 }, { 18, 19 }, { 19, 23 }, { 19, 26 }, { 20, 21 }, { 20, 28 },
            { 21, 22 }, { 22, 26 }, { 22, 29 }, { 23, 24 }, { 24, 25 }, { 25, 29 }, { 26, 27 },
            { 27, 28 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Brinkmann Graph-----------//
    /**
     * @see #generateBrinkmannGraph
     * @return Brinkmann Graph
     */
    public static Graph<Integer, DefaultEdge> brinkmannGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateBrinkmannGraph(g);
        return g;
    }

    /**
     * Generates the <a href="http://mathworld.wolfram.com/BrinkmannGraph.html">Brinkmann Graph</a>.
     * The Brinkmann graph is a weakly regular quartic graph on 21 vertices and 42 edges.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateBrinkmannGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int[][] edges = { { 0, 2 }, { 0, 5 }, { 0, 7 }, { 0, 13 }, { 1, 3 }, { 1, 6 }, { 1, 7 },
            { 1, 8 }, { 2, 4 }, { 2, 8 }, { 2, 9 }, { 3, 5 }, { 3, 9 }, { 3, 10 }, { 4, 6 },
            { 4, 10 }, { 4, 11 }, { 5, 11 }, { 5, 12 }, { 6, 12 }, { 6, 13 }, { 7, 15 }, { 7, 20 },
            { 8, 14 }, { 8, 16 }, { 9, 15 }, { 9, 17 }, { 10, 16 }, { 10, 18 }, { 11, 17 },
            { 11, 19 }, { 12, 18 }, { 12, 20 }, { 13, 14 }, { 13, 19 }, { 14, 17 }, { 14, 18 },
            { 15, 18 }, { 15, 19 }, { 16, 19 }, { 16, 20 }, { 17, 20 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Gosset Graph-----------//
    /**
     * @see #generateGossetGraph
     * @return Gosset Graph
     */
    public static Graph<Integer, DefaultEdge> gossetGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateGossetGraph(g);
        return g;
    }

    private static void addEdges(ArrayList<int[]> edges, int x, int yStart, int yEnd, List<Integer> toSkip){

        for(int y = yStart; y <= yEnd; y++){

            if(!toSkip.contains(y))  // Do not skip
                edges.add(new int[]{x, y});

        }
    }

    private static ArrayList<int[]> generateGossetEdges(){
        ArrayList<int[]> edges = new ArrayList<int[]>();

        addEdges(edges,0,1,33, Arrays.asList(22,23,24,25,26,27));
        addEdges(edges,1,2,38, Arrays.asList(17,18,19,20,21,27,29,30,31,32,33));
        addEdges(edges,2,3,42, Arrays.asList(13,14,15,16,21,26,28,30,31,32,33,35,36,37,38));
        addEdges(edges,3,4,45, Arrays.asList(10,11,12,16,20,25,28,29,31,32,33,34,36,37,38,40,41,42));
        addEdges(edges,4,5,47, Arrays.asList(8,9,12,15,19,24,28,29,30,32,33,34,35,37,38,39,41,42,44,45));
        addEdges(edges,5,6,48, Arrays.asList(9,11,14,18,23,28,29,30,31,33,34,35,36,38,39,40,42,43,45,47));
        addEdges(edges,6,9,48, Arrays.asList(13,17,22,28,29,30,31,32,34,35,36,37,39,40,41,43,44,46));
        addEdges(edges,7,8,50, Arrays.asList(12,15,16,19,20,21,24,25,26,27,32,33,37,38,41,42,44,45,46,47,48));
        addEdges(edges,8,9,51, Arrays.asList(11,14,16,18,20,21,23,25,26,27,31,33,36,38,40,42,43,45,46,47,48,50));
        addEdges(edges,9,11,51, Arrays.asList(13,16,17,20,21,22,25,26,27,31,32,36,37,40,41,43,44,46,47,48,49));
        addEdges(edges,10,11,52, Arrays.asList(14,15,18,19,21,23,24,26,27,30,33,35,38,39,42,43,44,45,47,48,50,51));
        addEdges(edges,11,12,52, Arrays.asList(15,17,19,21,22,24,26,27,30,32,35,37,39,41,43,44,45,46,48,49,51));
        addEdges(edges,12,15,52, Arrays.asList(17,18,21,22,23,26,27,30,31,35,36,39,40,43,44,45,46,47,49,50));
        addEdges(edges,13,14,53, Arrays.asList(18,19,20,23,24,25,27,29,33,34,38,39,40,41,42,45,47,48,50,51,52));
        addEdges(edges,14,15,53, Arrays.asList(17,19,20,22,24,25,27,29,32,34,37,39,40,41,42,44,46,48,49,51,52));
        addEdges(edges,15,16,53, Arrays.asList(20,22,23,25,27,29,31,34,36,39,40,41,42,43,46,47,49,50,52));
        addEdges(edges,16,20,53, Arrays.asList(22,23,24,27,29,30,34,35,39,40,41,42,43,44,45,49,50,51));
        addEdges(edges,17,18,54, Arrays.asList(23,24,25,26,28,33,34,35,36,37,38,42,45,47,48,50,51,52,53));
        addEdges(edges,18,19,54, Arrays.asList(22,24,25,26,28,32,34,35,36,37,38,41,44,46,48,49,51,52,53));
        addEdges(edges,19,20,54, Arrays.asList(22,23,25,26,28,31,34,35,36,37,38,40,43,46,47,49,50,52,53));
        addEdges(edges,20,21,54, Arrays.asList(26,28,30,34,35,36,37,38,39,43,44,45,49,50,51,53));
        addEdges(edges,21,26,54, Arrays.asList(28,29,34,35,36,37,38,39,40,41,42,49,50,51,52));
        addEdges(edges,22,23,55, Arrays.asList(28,29,30,31,32,33,38,42,45,47,48,50,51,52,53,54));
        addEdges(edges,23,24,55, Arrays.asList(28,29,30,31,32,33,37,41,44,46,48,49,51,52,53,54));
        addEdges(edges,24,25,55, Arrays.asList(28,29,30,31,32,33,36,40,43,46,47,49,50,52,53,54));
        addEdges(edges,25,26,55, Arrays.asList(28,29,30,31,32,33,35,39,43,44,45,49,50,51,53,54));
        addEdges(edges,26,27,55, Arrays.asList(39,40,41,42,49,50,51,52,54));
        addEdges(edges,27,39,55, Arrays.asList(49,50,51,52,53));
        addEdges(edges,28,29,53, Arrays.asList(39,40,41,42,43,44,45,46,47,48));
        addEdges(edges,29,30,54, Arrays.asList(35,36,37,38,43,44,45,46,47,48,53));
        addEdges(edges,30,31,54, Arrays.asList(34,36,37,38,40,41,42,46,47,48,52));
        addEdges(edges,31,32,54, Arrays.asList(34,35,37,38,39,41,42,44,45,48,51));
        addEdges(edges,32,33,54, Arrays.asList(38,39,40,42,43,45,47,50));
        addEdges(edges,33,38,54, Arrays.asList(43,44,46,49));
        addEdges(edges,34,35,55, Arrays.asList(43,44,45,46,47,48,53,54));
        addEdges(edges,35,36,55, Arrays.asList(40,41,42,46,47,48,52,54));
        addEdges(edges,36,37,55, Arrays.asList(39,41,42,44,45,48,51,54));
        addEdges(edges,37,38,55, Arrays.asList(42,43,45,47,50,54));
        addEdges(edges,38,42,55, Arrays.asList(46,49,54));
        addEdges(edges,39,40,55, Arrays.asList(46,47,48,52,53));
        addEdges(edges,40,41,55, Arrays.asList(44,45,48,51,53));
        addEdges(edges,41,42,55, Arrays.asList(45,47,50,53));
        addEdges(edges,42,45,55, Arrays.asList(49,53));
        addEdges(edges,43,44,55, Arrays.asList(48,51,52));
        addEdges(edges,44,45,55, Arrays.asList(47,50,52));
        addEdges(edges,45,47,55, Arrays.asList(49,52));
        addEdges(edges,46,47,55, Arrays.asList(50,51));
        addEdges(edges,47,48,55, Arrays.asList(51));
        addEdges(edges,48,51,55, Arrays.asList());
        addEdges(edges,49,50,55, Arrays.asList());
        addEdges(edges,50,51,55, Arrays.asList());
        addEdges(edges,51,52,55, Arrays.asList());
        addEdges(edges,52,53,55, Arrays.asList());
        addEdges(edges,53,54,55, Arrays.asList());

        return edges;
    }

    /**
     * Generates the <a href="http://mathworld.wolfram.com/GossetGraph.html">Gosset Graph</a>. The
     * Gosset graph is a 27-regular graph on 56 vertices which is the skeleton of the Gosset
     * polytope $3_{21}$.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateGossetGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();

        ArrayList<int[]> edges = generateGossetEdges();

        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Chvatal Graph-----------//
    /**
     * @see #generateChvatalGraph
     * @return Chvatal Graph
     */
    public static Graph<Integer, DefaultEdge> chvatalGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateChvatalGraph(g);
        return g;
    }

    /**
     * Generates the <a href="http://mathworld.wolfram.com/ChvatalGraph.html">Chvatal Graph</a>. The
     * Chvátal graph is an undirected graph with 12 vertices and 24 edges, discovered by Václav
     * Chvátal (1970)
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateChvatalGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int[][] edges = { { 0, 1 }, { 0, 4 }, { 0, 6 }, { 0, 9 }, { 1, 2 }, { 1, 5 }, { 1, 7 },
            { 2, 3 }, { 2, 6 }, { 2, 8 }, { 3, 4 }, { 3, 7 }, { 3, 9 }, { 4, 5 }, { 4, 8 },
            { 5, 10 }, { 5, 11 }, { 6, 10 }, { 6, 11 }, { 7, 8 }, { 7, 11 }, { 8, 10 }, { 9, 10 },
            { 9, 11 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Kittell Graph-----------//
    /**
     * @see #generateKittellGraph
     * @return Kittell Graph
     */
    public static Graph<Integer, DefaultEdge> kittellGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateKittellGraph(g);
        return g;
    }

    /**
     * Generates the <a href="http://mathworld.wolfram.com/KittellGraph.html">Kittell Graph</a>. The
     * Kittell graph is a planar graph on 23 nodes and 63 edges that tangles the Kempe chains in
     * Kempe's algorithm and thus provides an example of how Kempe's supposed proof of the
     * four-color theorem fails.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateKittellGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int[][] edges = { { 0, 1 }, { 0, 2 }, { 0, 4 }, { 0, 5 }, { 0, 6 }, { 0, 7 }, { 1, 2 },
            { 1, 7 }, { 1, 10 }, { 1, 11 }, { 1, 13 }, { 2, 4 }, { 2, 11 }, { 2, 14 }, { 3, 4 },
            { 3, 5 }, { 3, 12 }, { 3, 14 }, { 3, 16 }, { 4, 5 }, { 4, 14 }, { 5, 6 }, { 5, 16 },
            { 6, 7 }, { 6, 15 }, { 6, 16 }, { 6, 17 }, { 6, 18 }, { 7, 8 }, { 7, 13 }, { 7, 18 },
            { 8, 9 }, { 8, 13 }, { 8, 18 }, { 8, 19 }, { 9, 10 }, { 9, 13 }, { 9, 19 }, { 9, 20 },
            { 10, 11 }, { 10, 13 }, { 10, 20 }, { 10, 21 }, { 11, 12 }, { 11, 14 }, { 11, 15 },
            { 11, 21 }, { 12, 14 }, { 12, 15 }, { 12, 16 }, { 15, 16 }, { 15, 17 }, { 15, 21 },
            { 15, 22 }, { 17, 18 }, { 17, 19 }, { 17, 22 }, { 18, 19 }, { 19, 20 }, { 19, 22 },
            { 20, 21 }, { 20, 22 }, { 21, 22 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Coxeter Graph-----------//
    /**
     * @see #generateCoxeterGraph
     * @return Coxeter Graph
     */
    public static Graph<Integer, DefaultEdge> coxeterGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateCoxeterGraph(g);
        return g;
    }

    /**
     * Generates the <a href="http://mathworld.wolfram.com/CoxeterGraph.html">Coxeter Graph</a>. The
     * Coxeter graph is a nonhamiltonian cubic symmetric graph on 28 vertices and 42 edges.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateCoxeterGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int[][] edges = { { 0, 1 }, { 0, 23 }, { 0, 24 }, { 1, 2 }, { 1, 12 }, { 2, 3 }, { 2, 25 },
            { 3, 4 }, { 3, 21 }, { 4, 5 }, { 4, 17 }, { 5, 6 }, { 5, 11 }, { 6, 7 }, { 6, 27 },
            { 7, 8 }, { 7, 24 }, { 8, 9 }, { 8, 25 }, { 9, 10 }, { 9, 20 }, { 10, 11 }, { 10, 26 },
            { 11, 12 }, { 12, 13 }, { 13, 14 }, { 13, 19 }, { 14, 15 }, { 14, 27 }, { 15, 16 },
            { 15, 25 }, { 16, 17 }, { 16, 26 }, { 17, 18 }, { 18, 19 }, { 18, 24 }, { 19, 20 },
            { 20, 21 }, { 21, 22 }, { 22, 23 }, { 22, 27 }, { 23, 26 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Diamond Graph-----------//
    /**
     * @see #generateDiamondGraph
     * @return Diamond Graph
     */
    public static Graph<Integer, DefaultEdge> diamondGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateDiamondGraph(g);
        return g;
    }

    /**
     * Generates the <a href="https://en.wikipedia.org/wiki/Diamond_graph">Diamond Graph</a>. The
     * Diamond graph has 4 vertices and 5 edges.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateDiamondGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int[][] edges = { { 0, 1 }, { 0, 2 }, { 0, 3 }, { 1, 2 }, { 2, 3 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Ellingham-Horton 54 Graph-----------//
    /**
     * @see #generateEllinghamHorton54Graph
     * @return Ellingham-Horton 54 Graph
     */
    public static Graph<Integer, DefaultEdge> ellinghamHorton54Graph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateEllinghamHorton54Graph(g);
        return g;
    }

    /**
     * Generates the
     * <a href="http://mathworld.wolfram.com/Ellingham-HortonGraphs.html">Ellingham-Horton 54
     * Graph</a>. The Ellingham–Horton graph is a 3-regular bicubic graph of 54 vertices
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateEllinghamHorton54Graph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int[][] edges = { { 0, 1 }, { 0, 11 }, { 0, 15 }, { 1, 2 }, { 1, 47 }, { 2, 3 }, { 2, 13 },
            { 3, 4 }, { 3, 8 }, { 4, 5 }, { 4, 15 }, { 5, 6 }, { 5, 10 }, { 6, 7 }, { 6, 30 },
            { 7, 8 }, { 7, 12 }, { 8, 9 }, { 9, 10 }, { 9, 29 }, { 10, 11 }, { 11, 12 }, { 12, 13 },
            { 13, 14 }, { 14, 15 }, { 14, 48 }, { 16, 17 }, { 16, 21 }, { 16, 28 }, { 17, 24 },
            { 17, 29 }, { 18, 19 }, { 18, 23 }, { 18, 30 }, { 19, 20 }, { 19, 31 }, { 20, 21 },
            { 20, 32 }, { 21, 33 }, { 22, 23 }, { 22, 27 }, { 22, 28 }, { 23, 29 }, { 24, 25 },
            { 24, 30 }, { 25, 26 }, { 25, 31 }, { 26, 27 }, { 26, 32 }, { 27, 33 }, { 28, 31 },
            { 32, 52 }, { 33, 53 }, { 34, 35 }, { 34, 39 }, { 34, 46 }, { 35, 42 }, { 35, 47 },
            { 36, 37 }, { 36, 41 }, { 36, 48 }, { 37, 38 }, { 37, 49 }, { 38, 39 }, { 38, 50 },
            { 39, 51 }, { 40, 41 }, { 40, 45 }, { 40, 46 }, { 41, 47 }, { 42, 43 }, { 42, 48 },
            { 43, 44 }, { 43, 49 }, { 44, 45 }, { 44, 50 }, { 45, 51 }, { 46, 49 }, { 50, 52 },
            { 51, 53 }, { 52, 53 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Ellingham-Horton 78 Graph-----------//
    /**
     * @see #generateEllinghamHorton78Graph
     * @return Ellingham-Horton 78 Graph
     */
    public static Graph<Integer, DefaultEdge> ellinghamHorton78Graph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateEllinghamHorton78Graph(g);
        return g;
    }

    /**
     * Generates the
     * <a href="http://mathworld.wolfram.com/Ellingham-HortonGraphs.html">Ellingham-Horton 78
     * Graph</a>. The Ellingham–Horton graph is a 3-regular graph of 78 vertices
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateEllinghamHorton78Graph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int[][] edges = { { 0, 1 }, { 0, 5 }, { 0, 60 }, { 1, 2 }, { 1, 12 }, { 2, 3 }, { 2, 7 },
            { 3, 4 }, { 3, 14 }, { 4, 5 }, { 4, 9 }, { 5, 6 }, { 6, 7 }, { 6, 11 }, { 7, 15 },
            { 8, 9 }, { 8, 13 }, { 8, 22 }, { 9, 10 }, { 10, 11 }, { 10, 72 }, { 11, 12 },
            { 12, 13 }, { 13, 14 }, { 14, 72 }, { 15, 16 }, { 15, 20 }, { 16, 17 }, { 16, 27 },
            { 17, 18 }, { 17, 22 }, { 18, 19 }, { 18, 29 }, { 19, 20 }, { 19, 24 }, { 20, 21 },
            { 21, 22 }, { 21, 26 }, { 23, 24 }, { 23, 28 }, { 23, 72 }, { 24, 25 }, { 25, 26 },
            { 25, 71 }, { 26, 27 }, { 27, 28 }, { 28, 29 }, { 29, 69 }, { 30, 31 }, { 30, 35 },
            { 30, 52 }, { 31, 32 }, { 31, 42 }, { 32, 33 }, { 32, 37 }, { 33, 34 }, { 33, 43 },
            { 34, 35 }, { 34, 39 }, { 35, 36 }, { 36, 41 }, { 36, 63 }, { 37, 65 }, { 37, 66 },
            { 38, 39 }, { 38, 59 }, { 38, 74 }, { 39, 40 }, { 40, 41 }, { 40, 44 }, { 41, 42 },
            { 42, 74 }, { 43, 44 }, { 43, 74 }, { 44, 45 }, { 45, 46 }, { 45, 50 }, { 46, 47 },
            { 46, 57 }, { 47, 48 }, { 47, 52 }, { 48, 49 }, { 48, 75 }, { 49, 50 }, { 49, 54 },
            { 50, 51 }, { 51, 52 }, { 51, 56 }, { 53, 54 }, { 53, 58 }, { 53, 73 }, { 54, 55 },
            { 55, 56 }, { 55, 59 }, { 56, 57 }, { 57, 58 }, { 58, 75 }, { 59, 75 }, { 60, 61 },
            { 60, 64 }, { 61, 62 }, { 61, 71 }, { 62, 63 }, { 62, 77 }, { 63, 67 }, { 64, 65 },
            { 64, 69 }, { 65, 77 }, { 66, 70 }, { 66, 73 }, { 67, 68 }, { 67, 73 }, { 68, 69 },
            { 68, 76 }, { 70, 71 }, { 70, 76 }, { 76, 77 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Errera Graph-----------//
    /**
     * @see #generateErreraGraph
     * @return Errera Graph
     */
    public static Graph<Integer, DefaultEdge> erreraGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateErreraGraph(g);
        return g;
    }

    /**
     * Generates the <a href="http://mathworld.wolfram.com/ErreraGraph.html">Errera Graph</a>. The
     * Errera graph is the 17-node planar graph
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateErreraGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int[][] edges = { { 0, 1 }, { 0, 7 }, { 0, 14 }, { 0, 15 }, { 0, 16 }, { 1, 2 }, { 1, 9 },
            { 1, 14 }, { 1, 15 }, { 2, 3 }, { 2, 8 }, { 2, 9 }, { 2, 10 }, { 2, 14 }, { 3, 4 },
            { 3, 9 }, { 3, 10 }, { 3, 11 }, { 4, 5 }, { 4, 10 }, { 4, 11 }, { 4, 12 }, { 5, 6 },
            { 5, 11 }, { 5, 12 }, { 5, 13 }, { 6, 7 }, { 6, 8 }, { 6, 12 }, { 6, 13 }, { 6, 16 },
            { 7, 13 }, { 7, 15 }, { 7, 16 }, { 8, 10 }, { 8, 12 }, { 8, 14 }, { 8, 16 }, { 9, 11 },
            { 9, 13 }, { 9, 15 }, { 10, 12 }, { 11, 13 }, { 13, 15 }, { 14, 16 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Folkman Graph-----------//
    /**
     * @see #generateFolkmanGraph
     * @return Folkman Graph
     */
    public static Graph<Integer, DefaultEdge> folkmanGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateFolkmanGraph(g);
        return g;
    }

    /**
     * Generates the <a href="https://en.wikipedia.org/wiki/Folkman_graph">Folkman Graph</a>. The
     * Folkman graph is the 20-vertex 4-regular graph.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateFolkmanGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int[][] edges = { { 0, 1 }, { 0, 3 }, { 0, 13 }, { 0, 15 }, { 1, 2 }, { 1, 6 }, { 1, 8 },
            { 2, 3 }, { 2, 17 }, { 2, 19 }, { 3, 6 }, { 3, 8 }, { 4, 5 }, { 4, 7 }, { 4, 17 },
            { 4, 19 }, { 5, 6 }, { 5, 10 }, { 5, 12 }, { 6, 7 }, { 7, 10 }, { 7, 12 }, { 8, 9 },
            { 8, 11 }, { 9, 10 }, { 9, 14 }, { 9, 16 }, { 10, 11 }, { 11, 14 }, { 11, 16 },
            { 12, 13 }, { 12, 15 }, { 13, 14 }, { 13, 18 }, { 14, 15 }, { 15, 18 }, { 16, 17 },
            { 16, 19 }, { 17, 18 }, { 18, 19 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Franklin Graph-----------//
    /**
     * @see #generateFranklinGraph
     * @return Franklin Graph
     */
    public static Graph<Integer, DefaultEdge> franklinGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateFranklinGraph(g);
        return g;
    }

    /**
     * Generates the <a href="http://mathworld.wolfram.com/FranklinGraph.html">Franklin Graph</a>.
     * The Franklin graph is the 12-vertex cubic graph.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateFranklinGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int[][] edges = { { 0, 1 }, { 0, 5 }, { 0, 6 }, { 1, 2 }, { 1, 7 }, { 2, 3 }, { 2, 8 },
            { 3, 4 }, { 3, 9 }, { 4, 5 }, { 4, 10 }, { 5, 11 }, { 6, 7 }, { 6, 9 }, { 7, 10 },
            { 8, 9 }, { 8, 11 }, { 10, 11 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Frucht Graph-----------//
    /**
     * @see #generateFruchtGraph
     * @return Frucht Graph
     */
    public static Graph<Integer, DefaultEdge> fruchtGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateFruchtGraph(g);
        return g;
    }

    /**
     * Generates the <a href="http://mathworld.wolfram.com/FruchtGraph.html">Frucht Graph</a>. The
     * Frucht graph is smallest cubic identity graph.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateFruchtGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int[][] edges = { { 0, 1 }, { 0, 6 }, { 0, 7 }, { 1, 2 }, { 1, 7 }, { 2, 3 }, { 2, 8 },
            { 3, 4 }, { 3, 9 }, { 4, 5 }, { 4, 9 }, { 5, 6 }, { 5, 10 }, { 6, 10 }, { 7, 11 },
            { 8, 9 }, { 8, 11 }, { 10, 11 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Goldner-Harary Graph-----------//
    /**
     * @see #generateGoldnerHararyGraph
     * @return Goldner-Harary Graph
     */
    public static Graph<Integer, DefaultEdge> goldnerHararyGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateGoldnerHararyGraph(g);
        return g;
    }

    /**
     * Generates the <a href="http://mathworld.wolfram.com/Goldner-HararyGraph.html">Goldner-Harary
     * Graph</a>. The Goldner-Harary graph is a graph on 11 vertices and 27. It is a simplicial
     * graph, meaning that it is polyhedral and consists of only triangular faces.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateGoldnerHararyGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int[][] edges = { { 0, 1 }, { 0, 3 }, { 0, 4 }, { 1, 2 }, { 1, 3 }, { 1, 4 }, { 1, 5 },
            { 1, 6 }, { 1, 7 }, { 1, 10 }, { 2, 3 }, { 2, 7 }, { 3, 4 }, { 3, 7 }, { 3, 8 },
            { 3, 9 }, { 3, 10 }, { 4, 5 }, { 4, 9 }, { 4, 10 }, { 5, 10 }, { 6, 7 }, { 6, 10 },
            { 7, 8 }, { 7, 10 }, { 8, 10 }, { 9, 10 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Heawood Graph-----------//
    /**
     * @see #generateHeawoodGraph
     * @return Heawood Graph
     */
    public static Graph<Integer, DefaultEdge> heawoodGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateHeawoodGraph(g);
        return g;
    }

    /**
     * Generates the <a href="http://mathworld.wolfram.com/HeawoodGraph.html">Heawood Graph</a>.
     * Heawood graph is an undirected graph with 14 vertices and 21 edges, named after Percy John
     * Heawood.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateHeawoodGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int[][] edges = { { 0, 1 }, { 0, 5 }, { 0, 13 }, { 1, 2 }, { 1, 10 }, { 2, 3 }, { 2, 7 },
            { 3, 4 }, { 3, 12 }, { 4, 5 }, { 4, 9 }, { 5, 6 }, { 6, 7 }, { 6, 11 }, { 7, 8 },
            { 8, 9 }, { 8, 13 }, { 9, 10 }, { 10, 11 }, { 11, 12 }, { 12, 13 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Herschel Graph-----------//
    /**
     * @see #generateHerschelGraph
     * @return Herschel Graph
     */
    public static Graph<Integer, DefaultEdge> herschelGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateHerschelGraph(g);
        return g;
    }

    /**
     * Generates the <a href="http://mathworld.wolfram.com/HerschelGraph.html">Herschel Graph</a>.
     * The Herschel graph is the smallest nonhamiltonian polyhedral graph (Coxeter 1973, p. 8). It
     * is the unique such graph on 11 nodes and 18 edges.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateHerschelGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int[][] edges = { { 0, 1 }, { 0, 3 }, { 0, 4 }, { 1, 2 }, { 1, 5 }, { 1, 6 }, { 2, 3 },
            { 2, 7 }, { 3, 8 }, { 3, 9 }, { 4, 5 }, { 4, 9 }, { 5, 10 }, { 6, 7 }, { 6, 10 },
            { 7, 8 }, { 8, 10 }, { 9, 10 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Hoffman Graph-----------//
    /**
     * @see #generateHoffmanGraph
     * @return Hoffman Graph
     */
    public static Graph<Integer, DefaultEdge> hoffmanGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateHoffmanGraph(g);
        return g;
    }

    /**
     * Generates the <a href="http://mathworld.wolfram.com/HoffmanGraph.html">Hoffman Graph</a>. The
     * Hoffman graph is the bipartite graph on 16 nodes and 32 edges.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateHoffmanGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int[][] edges = { { 0, 1 }, { 0, 7 }, { 0, 8 }, { 0, 13 }, { 1, 2 }, { 1, 9 }, { 1, 14 },
            { 2, 3 }, { 2, 8 }, { 2, 10 }, { 3, 4 }, { 3, 9 }, { 3, 15 }, { 4, 5 }, { 4, 10 },
            { 4, 11 }, { 5, 6 }, { 5, 12 }, { 5, 14 }, { 6, 7 }, { 6, 11 }, { 6, 13 }, { 7, 12 },
            { 7, 15 }, { 8, 12 }, { 8, 14 }, { 9, 11 }, { 9, 13 }, { 10, 12 }, { 10, 15 },
            { 11, 14 }, { 13, 15 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Krackhardt kite Graph-----------//
    /**
     * @see #generateKrackhardtKiteGraph
     * @return Krackhardt kite Graph
     */
    public static Graph<Integer, DefaultEdge> krackhardtKiteGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateKrackhardtKiteGraph(g);
        return g;
    }

    /**
     * Generates the <a href="http://mathworld.wolfram.com/KrackhardtKite.html">Krackhardt kite
     * Graph</a>. The Krackhardt kite is the simple graph on 10 nodes and 18 edges. It arises in
     * social network theory.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateKrackhardtKiteGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int[][] edges = { { 0, 1 }, { 0, 2 }, { 0, 3 }, { 0, 5 }, { 1, 3 }, { 1, 4 }, { 1, 6 },
            { 2, 3 }, { 2, 5 }, { 3, 4 }, { 3, 5 }, { 3, 6 }, { 4, 6 }, { 5, 6 }, { 5, 7 },
            { 6, 7 }, { 7, 8 }, { 8, 9 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Klein 3-regular Graph-----------//
    /**
     * @see #generateKlein3RegularGraph
     * @return Klein 3-regular Graph
     */
    public static Graph<Integer, DefaultEdge> klein3RegularGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateKlein3RegularGraph(g);
        return g;
    }

    /**
     * Generates the <a href="https://en.wikipedia.org/wiki/Klein_graphs">Klein 3-regular Graph</a>.
     * This graph is a 3-regular graph with 56 vertices and 84 edges, named after Felix Klein.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateKlein3RegularGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int[][] edges = { { 0, 3 }, { 0, 53 }, { 0, 55 }, { 1, 4 }, { 1, 30 }, { 1, 42 }, { 2, 6 },
            { 2, 44 }, { 2, 55 }, { 3, 7 }, { 3, 10 }, { 4, 15 }, { 4, 22 }, { 5, 8 }, { 5, 13 },
            { 5, 50 }, { 6, 9 }, { 6, 14 }, { 7, 12 }, { 7, 18 }, { 8, 9 }, { 8, 33 }, { 9, 12 },
            { 10, 17 }, { 10, 29 }, { 11, 16 }, { 11, 25 }, { 11, 53 }, { 12, 19 }, { 13, 18 },
            { 13, 54 }, { 14, 21 }, { 14, 37 }, { 15, 16 }, { 15, 17 }, { 16, 23 }, { 17, 20 },
            { 18, 40 }, { 19, 20 }, { 19, 24 }, { 20, 27 }, { 21, 22 }, { 21, 24 }, { 22, 26 },
            { 23, 28 }, { 23, 47 }, { 24, 31 }, { 25, 26 }, { 25, 44 }, { 26, 32 }, { 27, 28 },
            { 27, 35 }, { 28, 33 }, { 29, 30 }, { 29, 46 }, { 30, 54 }, { 31, 34 }, { 31, 36 },
            { 32, 34 }, { 32, 51 }, { 33, 39 }, { 34, 40 }, { 35, 36 }, { 35, 38 }, { 36, 43 },
            { 37, 42 }, { 37, 48 }, { 38, 41 }, { 38, 46 }, { 39, 41 }, { 39, 44 }, { 40, 49 },
            { 41, 51 }, { 42, 50 }, { 43, 45 }, { 43, 48 }, { 45, 47 }, { 45, 49 }, { 46, 52 },
            { 47, 50 }, { 48, 52 }, { 49, 53 }, { 51, 54 }, { 52, 55 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Klein 7-regular Graph-----------//
    /**
     * @see #generateKlein7RegularGraph
     * @return Klein 7-regular Graph
     */
    public static Graph<Integer, DefaultEdge> klein7RegularGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateKlein7RegularGraph(g);
        return g;
    }

    /**
     * Generates the <a href="https://en.wikipedia.org/wiki/Klein_graphs">Klein 7-regular Graph</a>.
     * This graph is a 7-regular graph with 24 vertices and 84 edges, named after Felix Klein.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateKlein7RegularGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int arr[] = { 0, 1, 2, 3, 4, 5, 6 };
        addCycle(targetGraph, arr);
        int[][] edges = { { 0, 2 }, { 0, 6 }, { 0, 10 }, { 0, 11 }, { 0, 12 }, { 0, 18 }, { 1, 3 },
            { 1, 9 }, { 1, 11 }, { 1, 20 }, { 1, 22 }, { 2, 4 }, { 2, 10 }, { 2, 15 }, { 2, 19 },
            { 3, 5 }, { 3, 7 }, { 3, 14 }, { 3, 22 }, { 4, 6 }, { 4, 8 }, { 4, 19 }, { 4, 21 },
            { 5, 7 }, { 5, 11 }, { 5, 17 }, { 5, 23 }, { 6, 8 }, { 6, 11 }, { 6, 16 }, { 6, 18 },
            { 7, 9 }, { 7, 14 }, { 7, 15 }, { 7, 16 }, { 7, 17 }, { 8, 10 }, { 8, 13 }, { 8, 14 },
            { 8, 16 }, { 8, 21 }, { 9, 11 }, { 9, 13 }, { 9, 15 }, { 9, 16 }, { 9, 20 }, { 10, 12 },
            { 10, 13 }, { 10, 14 }, { 10, 15 }, { 11, 13 }, { 11, 23 }, { 12, 14 }, { 12, 17 },
            { 12, 18 }, { 12, 22 }, { 12, 23 }, { 13, 15 }, { 13, 21 }, { 13, 23 }, { 14, 16 },
            { 14, 22 }, { 15, 17 }, { 15, 19 }, { 16, 18 }, { 16, 20 }, { 17, 18 }, { 17, 19 },
            { 17, 23 }, { 18, 19 }, { 18, 20 }, { 19, 20 }, { 19, 21 }, { 20, 21 }, { 20, 22 },
            { 21, 22 }, { 21, 23 }, { 22, 23 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Moser spindle Graph-----------//
    /**
     * @see #generateMoserSpindleGraph
     * @return Moser spindle Graph
     */
    public static Graph<Integer, DefaultEdge> moserSpindleGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateMoserSpindleGraph(g);
        return g;
    }

    /**
     * Generates the <a href="http://mathworld.wolfram.com/MoserSpindle.html">Moser spindle
     * Graph</a>. The Moser spindle is the 7-node unit-distance graph.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateMoserSpindleGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int[][] edges = { { 0, 1 }, { 0, 4 }, { 0, 5 }, { 0, 6 }, { 1, 2 }, { 1, 5 }, { 2, 3 },
            { 2, 5 }, { 3, 4 }, { 3, 6 }, { 4, 6 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Pappus Graph-----------//
    /**
     * @see #generatePappusGraph
     * @return Pappus Graph
     */
    public static Graph<Integer, DefaultEdge> pappusGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generatePappusGraph(g);
        return g;
    }

    /**
     * Generates the <a href="https://en.wikipedia.org/wiki/Pappus_graph">Pappus Graph</a>. The
     * Pappus Graph is a bipartite 3-regular undirected graph with 18 vertices and 27 edges.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generatePappusGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int[][] edges = { { 0, 1 }, { 0, 5 }, { 0, 6 }, { 1, 2 }, { 1, 7 }, { 2, 3 }, { 2, 8 },
            { 3, 4 }, { 3, 9 }, { 4, 5 }, { 4, 10 }, { 5, 11 }, { 6, 13 }, { 6, 17 }, { 7, 12 },
            { 7, 14 }, { 8, 13 }, { 8, 15 }, { 9, 14 }, { 9, 16 }, { 10, 15 }, { 10, 17 },
            { 11, 12 }, { 11, 16 }, { 12, 15 }, { 13, 16 }, { 14, 17 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Poussin Graph-----------//
    /**
     * @see #generatePoussinGraph
     * @return Poussin Graph
     */
    public static Graph<Integer, DefaultEdge> poussinGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generatePoussinGraph(g);
        return g;
    }

    /**
     * Generates the <a href="http://mathworld.wolfram.com/PoussinGraph.html">Poussin Graph</a>. The
     * Poussin graph is the 15-node planar graph.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generatePoussinGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int arr[] = { 0, 1, 2, 3, 4, 5, 6 };
        addCycle(targetGraph, arr);
        int arr1[] = { 9, 10, 11, 12, 13, 14 };
        addCycle(targetGraph, arr1);
        int[][] edges = { { 0, 2 }, { 0, 4 }, { 0, 5 }, { 1, 6 }, { 1, 7 }, { 2, 4 }, { 2, 7 },
            { 2, 8 }, { 3, 5 }, { 3, 8 }, { 3, 9 }, { 3, 13 }, { 5, 9 }, { 5, 10 }, { 6, 7 },
            { 6, 10 }, { 6, 11 }, { 7, 8 }, { 7, 11 }, { 7, 12 }, { 8, 12 }, { 8, 13 }, { 9, 13 },
            { 10, 14 }, { 11, 14 }, { 12, 14 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Schläfli Graph-----------//
    /**
     * Generates the <a href="http://mathworld.wolfram.com/SchlaefliGraph.html">Schläfli Graph</a>.
     * The Schläfli graph is a strongly regular graph on 27 nodes
     * 
     * @return the Schläfli Graph
     */
    public static Graph<Integer, DefaultEdge> schläfliGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateSchläfliGraph(g);
        return g;
    }

    /**
     * Generates the <a href="http://mathworld.wolfram.com/SchlaefliGraph.html">Schläfli Graph</a>.
     * The Schläfli graph is a strongly regular graph on 27 nodes
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateSchläfliGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int[][] edges = { { 0, 11 }, { 0, 12 }, { 0, 13 }, { 0, 14 }, { 0, 15 }, { 0, 16 },
            { 0, 17 }, { 0, 18 }, { 0, 19 }, { 0, 20 }, { 0, 21 }, { 0, 22 }, { 0, 23 }, { 0, 24 },
            { 0, 25 }, { 0, 26 }, { 1, 3 }, { 1, 4 }, { 1, 5 }, { 1, 6 }, { 1, 7 }, { 1, 8 },
            { 1, 9 }, { 1, 10 }, { 1, 19 }, { 1, 20 }, { 1, 21 }, { 1, 22 }, { 1, 23 }, { 1, 24 },
            { 1, 25 }, { 1, 26 }, { 2, 3 }, { 2, 4 }, { 2, 5 }, { 2, 6 }, { 2, 7 }, { 2, 8 },
            { 2, 9 }, { 2, 10 }, { 2, 11 }, { 2, 12 }, { 2, 13 }, { 2, 14 }, { 2, 15 }, { 2, 16 },
            { 2, 17 }, { 2, 18 }, { 3, 5 }, { 3, 6 }, { 3, 7 }, { 3, 8 }, { 3, 9 }, { 3, 10 },
            { 3, 15 }, { 3, 16 }, { 3, 17 }, { 3, 18 }, { 3, 23 }, { 3, 24 }, { 3, 25 }, { 3, 26 },
            { 4, 5 }, { 4, 6 }, { 4, 7 }, { 4, 8 }, { 4, 9 }, { 4, 10 }, { 4, 11 }, { 4, 12 },
            { 4, 13 }, { 4, 14 }, { 4, 19 }, { 4, 20 }, { 4, 21 }, { 4, 22 }, { 5, 7 }, { 5, 8 },
            { 5, 9 }, { 5, 10 }, { 5, 13 }, { 5, 14 }, { 5, 17 }, { 5, 18 }, { 5, 21 }, { 5, 22 },
            { 5, 25 }, { 5, 26 }, { 6, 7 }, { 6, 8 }, { 6, 9 }, { 6, 10 }, { 6, 11 }, { 6, 12 },
            { 6, 15 }, { 6, 16 }, { 6, 19 }, { 6, 20 }, { 6, 23 }, { 6, 24 }, { 7, 9 }, { 7, 10 },
            { 7, 12 }, { 7, 14 }, { 7, 16 }, { 7, 18 }, { 7, 20 }, { 7, 22 }, { 7, 24 }, { 7, 26 },
            { 8, 9 }, { 8, 10 }, { 8, 11 }, { 8, 13 }, { 8, 15 }, { 8, 17 }, { 8, 19 }, { 8, 21 },
            { 8, 23 }, { 8, 25 }, { 9, 12 }, { 9, 13 }, { 9, 15 }, { 9, 18 }, { 9, 19 }, { 9, 22 },
            { 9, 24 }, { 9, 25 }, { 10, 11 }, { 10, 14 }, { 10, 16 }, { 10, 17 }, { 10, 20 },
            { 10, 21 }, { 10, 23 }, { 10, 26 }, { 11, 12 }, { 11, 13 }, { 11, 14 }, { 11, 15 },
            { 11, 16 }, { 11, 17 }, { 11, 19 }, { 11, 20 }, { 11, 21 }, { 11, 23 }, { 12, 13 },
            { 12, 14 }, { 12, 15 }, { 12, 16 }, { 12, 18 }, { 12, 19 }, { 12, 20 }, { 12, 22 },
            { 12, 24 }, { 13, 14 }, { 13, 15 }, { 13, 17 }, { 13, 18 }, { 13, 19 }, { 13, 21 },
            { 13, 22 }, { 13, 25 }, { 14, 16 }, { 14, 17 }, { 14, 18 }, { 14, 20 }, { 14, 21 },
            { 14, 22 }, { 14, 26 }, { 15, 16 }, { 15, 17 }, { 15, 18 }, { 15, 19 }, { 15, 23 },
            { 15, 24 }, { 15, 25 }, { 16, 17 }, { 16, 18 }, { 16, 20 }, { 16, 23 }, { 16, 24 },
            { 16, 26 }, { 17, 18 }, { 17, 21 }, { 17, 23 }, { 17, 25 }, { 17, 26 }, { 18, 22 },
            { 18, 24 }, { 18, 25 }, { 18, 26 }, { 19, 20 }, { 19, 21 }, { 19, 22 }, { 19, 23 },
            { 19, 24 }, { 19, 25 }, { 20, 21 }, { 20, 22 }, { 20, 23 }, { 20, 24 }, { 20, 26 },
            { 21, 22 }, { 21, 23 }, { 21, 25 }, { 21, 26 }, { 22, 24 }, { 22, 25 }, { 22, 26 },
            { 23, 24 }, { 23, 25 }, { 23, 26 }, { 24, 25 }, { 24, 26 }, { 25, 26 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Tietze Graph-----------//
    /**
     * @see #generateTietzeGraph
     * @return Tietze Graph
     */
    public static Graph<Integer, DefaultEdge> tietzeGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateTietzeGraph(g);
        return g;
    }

    /**
     * Generates the <a href="https://en.wikipedia.org/wiki/Tietze's_graph">Tietze Graph</a>. The
     * Tietze Graph is an undirected cubic graph with 12 vertices and 18 edges.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateTietzeGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int arr[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
        addCycle(targetGraph, arr);
        int[][] edges = { { 0, 9 }, { 1, 5 }, { 2, 7 }, { 3, 10 }, { 4, 8 }, { 6, 11 }, { 9, 10 },
            { 9, 11 }, { 10, 11 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Thomsen Graph-----------//
    /**
     * @see #generateThomsenGraph
     * @return Thomsen Graph
     */
    public static Graph<Integer, DefaultEdge> thomsenGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateThomsenGraph(g);
        return g;
    }

    /**
     * Generates the <a href="http://mathworld.wolfram.com/UtilityGraph.html">Thomsen Graph</a>. The
     * Thomsen Graph is complete bipartite graph consisting of 6 vertices (3 vertices in each
     * bipartite partition. It is also called the Utility graph.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateThomsenGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int[][] edges = { { 0, 3 }, { 0, 4 }, { 0, 5 }, { 1, 3 }, { 1, 4 }, { 1, 5 }, { 2, 3 },
            { 2, 4 }, { 2, 5 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Tutte Graph-----------//
    /**
     * @see #generateTutteGraph
     * @return Tutte Graph
     */
    public static Graph<Integer, DefaultEdge> tutteGraph()
    {
        Graph<Integer,
            DefaultEdge> g = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false)
                .vertexSupplier(SupplierUtil.createIntegerSupplier()).edgeClass(DefaultEdge.class)
                .buildGraph();
        new NamedGraphGenerator<Integer, DefaultEdge>().generateTutteGraph(g);
        return g;
    }

    /**
     * Generates the <a href="https://en.wikipedia.org/wiki/Tutte_graph">Tutte Graph</a>. The Tutte
     * Graph is a 3-regular graph with 46 vertices and 69 edges.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateTutteGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int[][] edges = { { 0, 1 }, { 0, 16 }, { 0, 31 }, { 1, 2 }, { 1, 4 }, { 2, 3 }, { 2, 5 },
            { 3, 4 }, { 3, 7 }, { 4, 9 }, { 5, 6 }, { 5, 10 }, { 6, 7 }, { 6, 11 }, { 7, 8 },
            { 8, 9 }, { 8, 12 }, { 9, 15 }, { 10, 11 }, { 10, 13 }, { 11, 12 }, { 12, 14 },
            { 13, 14 }, { 13, 30 }, { 14, 15 }, { 15, 43 }, { 16, 17 }, { 16, 19 }, { 17, 18 },
            { 17, 20 }, { 18, 19 }, { 18, 22 }, { 19, 24 }, { 20, 21 }, { 20, 25 }, { 21, 22 },
            { 21, 26 }, { 22, 23 }, { 23, 24 }, { 23, 27 }, { 24, 30 }, { 25, 26 }, { 25, 28 },
            { 26, 27 }, { 27, 29 }, { 28, 29 }, { 28, 45 }, { 29, 30 }, { 31, 32 }, { 31, 34 },
            { 32, 33 }, { 32, 35 }, { 33, 34 }, { 33, 37 }, { 34, 39 }, { 35, 36 }, { 35, 40 },
            { 36, 37 }, { 36, 41 }, { 37, 38 }, { 38, 39 }, { 38, 42 }, { 39, 45 }, { 40, 41 },
            { 40, 43 }, { 41, 42 }, { 42, 44 }, { 43, 44 }, { 44, 45 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // -------------Zachary's Karate Club Graph-----------//

    /**
     * Generates the <a href="https://en.wikipedia.org/wiki/Zachary%27s_karate_club">Zachary's
     * karate club Graph</a>.
     * 
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry,
     *        the result will be a disconnected graph since generated elements will not be connected
     *        to existing elements
     */
    public void generateZacharyKarateClubGraph(Graph<V, E> targetGraph)
    {
        vertexMap.clear();
        int[][] edges = { { 0, 1 }, { 0, 2 }, { 0, 3 }, { 0, 4 }, { 0, 5 }, { 0, 6 }, { 0, 7 },
            { 0, 8 }, { 0, 10 }, { 0, 11 }, { 0, 12 }, { 0, 13 }, { 0, 17 }, { 0, 19 }, { 0, 21 },
            { 0, 31 }, { 1, 2 }, { 1, 3 }, { 1, 7 }, { 1, 13 }, { 1, 17 }, { 1, 19 }, { 1, 21 },
            { 1, 30 }, { 2, 3 }, { 2, 7 }, { 2, 8 }, { 2, 9 }, { 2, 13 }, { 2, 27 }, { 2, 28 },
            { 2, 32 }, { 3, 7 }, { 3, 12 }, { 3, 13 }, { 4, 6 }, { 4, 10 }, { 5, 6 }, { 5, 10 },
            { 5, 16 }, { 6, 16 }, { 8, 30 }, { 8, 32 }, { 8, 33 }, { 9, 33 }, { 13, 33 },
            { 14, 32 }, { 14, 33 }, { 15, 32 }, { 15, 33 }, { 18, 32 }, { 18, 33 }, { 19, 33 },
            { 20, 32 }, { 20, 33 }, { 22, 32 }, { 22, 33 }, { 23, 25 }, { 23, 27 }, { 23, 29 },
            { 23, 32 }, { 23, 33 }, { 24, 25 }, { 24, 27 }, { 24, 31 }, { 25, 31 }, { 26, 29 },
            { 26, 33 }, { 27, 33 }, { 28, 31 }, { 28, 33 }, { 29, 32 }, { 29, 33 }, { 30, 32 },
            { 30, 33 }, { 31, 32 }, { 31, 33 }, { 32, 33 } };
        for (int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    // --------------Helper methods-----------------/
    private V addVertex(Graph<V, E> targetGraph, int i)
    {
        return vertexMap.computeIfAbsent(i, i1 -> targetGraph.addVertex());
    }

    private void addEdge(Graph<V, E> targetGraph, int i, int j)
    {
        V u = addVertex(targetGraph, i);
        V v = addVertex(targetGraph, j);
        targetGraph.addEdge(u, v);
    }

    private void addCycle(Graph<V, E> targetGraph, int array[])
    {
        for (int i = 0; i < array.length; i++)
            addEdge(targetGraph, array[i], array[(i + 1) % array.length]);
    }
}
