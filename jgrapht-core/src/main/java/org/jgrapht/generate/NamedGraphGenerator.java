/*
 * (C) Copyright 2017-2017, by Joris Kinable and Contributors.
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
package org.jgrapht.generate;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.VertexFactory;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Collection of commonly used named graphs
 * @author Joris Kinable
 */
public class NamedGraphGenerator<V,E> {

    private VertexFactory<V> vertexFactory;
    private Map<Integer, V> vertexMap;

    public NamedGraphGenerator(VertexFactory<V> vertexFactory){
        this.vertexFactory=vertexFactory;
    }

    //-------------Doyle Graph-----------//
    /**
     * @see #generateDoyleGraph
     * @return Doyle Graph
     */
    public static Graph<Integer, DefaultEdge> doyleGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateDoyleGraph(g);
        return g;
    }
    /**
     * Generates a <a href="http://mathworld.wolfram.com/DoyleGraph.html">Doyle Graph</a>.
     * The Doyle graph, sometimes also known as the Holt graph (Marušič et al. 2005), is the quartic symmetric graph on 27 nodes
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateDoyleGraph(Graph<V,E> targetGraph){
        for(int i=0; i<9; i++)
            for(int j=0; j<3; j++) {
                this.addEdge(targetGraph, doyleHash(i, j), doyleHash((4 * i + 1) % 9, (j - 1) % 3));
                this.addEdge(targetGraph, doyleHash(i, j), doyleHash((4 * i - 1) % 9, (j - 1) % 3));
                this.addEdge(targetGraph, doyleHash(i, j), doyleHash((7 * i + 7) % 9, (j + 1) % 3));
                this.addEdge(targetGraph, doyleHash(i, j), doyleHash((7 * i - 7) % 9, (j + 1) % 3));
            }
    }
    private int doyleHash(int u, int v){
        return u*55+v;
    }

    //-------------Generalized Petersen Graph-----------//
    public static Graph<Integer, DefaultEdge> generalizedPetersenGraph(int n, int k){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateGeneralizedPetersenGraph(g, n, k);
        return g;
    }
    public void generateGeneralizedPetersenGraph(Graph<V,E> targetGraph, int n, int k){
        List<V> verticesU=new ArrayList<>(n);
        List<V> verticesV=new ArrayList<>(n);
        for(int i=0; i<n; i++){
            verticesU.add(vertexFactory.createVertex());
            verticesV.add(vertexFactory.createVertex());
        }
        Graphs.addAllVertices(targetGraph, verticesU);
        Graphs.addAllVertices(targetGraph, verticesV);

        for(int i=0; i<n; i++){
            targetGraph.addEdge(verticesU.get(i), verticesU.get((i+1)%n));
            targetGraph.addEdge(verticesU.get(i), verticesV.get(i));
            targetGraph.addEdge(verticesV.get(i), verticesV.get((i+k)%n));
        }
    }

    //-------------Petersen Graph-----------//
    /**
     * @see #generatePetersenGraph
     * @return Petersen Graph
     */
    public static Graph<Integer, DefaultEdge> petersenGraph(){
        return generalizedPetersenGraph(5,2);
    }
    /**
     * Generates a <a href="http://mathworld.wolfram.com/PetersenGraph.html">Petersen Graph</a>.
     * The Petersen Graph is a named graph that consists of 10 vertices and 15 edges, usually drawn as a five-point star embedded in a pentagon.
     * It is the generalized Petersen graph $GP(5,2)$
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generatePetersenGraph(Graph<V,E> targetGraph){
        generateGeneralizedPetersenGraph(targetGraph, 5, 2);
    }

    //-------------Dürer Graph-----------//
    /**
     * @see #generateDürerGraph
     * @return Dürer Graph
     */
    public static Graph<Integer, DefaultEdge> dürerGraph(){
        return generalizedPetersenGraph(6,2);
    }
    /**
     * Generates a <a href="http://mathworld.wolfram.com/DuererGraph.html">Dürer Graph</a>.
     * The Dürer graph is the skeleton of Dürer's solid, which is the generalized Petersen graph $GP(6,2)$.
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateDürerGraph(Graph<V,E> targetGraph){
        generateGeneralizedPetersenGraph(targetGraph, 6, 2);
    }

    //-------------Dodecahedron Graph-----------//
    /**
     * @see #generateDodecahedronGraph
     * @return Dodecahedron Graph
     */
    public static Graph<Integer, DefaultEdge> dodecahedronGraph(){
        return generalizedPetersenGraph(10,2);
    }
    /**
     * Generates a <a href="http://mathworld.wolfram.com/DodecahedralGraph.html">Dodecahedron Graph</a>.
     * The skeleton of the dodecahedron (the vertices and edges) form a graph. It is one of 5 Platonic graphs, each a skeleton of its Platonic solid.
     * It is the generalized Petersen graph $GP(10,2)$
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateDodecahedronGraph(Graph<V,E> targetGraph){
        generateGeneralizedPetersenGraph(targetGraph, 10, 2);
    }

    //-------------Desargues Graph-----------//
    /**
     * @see #generateDesarguesGraph
     * @return Desargues Graph
     */
    public static Graph<Integer, DefaultEdge> desarguesGraph(){
        return generalizedPetersenGraph(10,3);
    }
    /**
     * Generates a <a href="http://mathworld.wolfram.com/DesarguesGraph.html">Desargues Graph</a>.
     * The Desargues graph is a cubic symmetric graph distance-regular graph on 20 vertices and 30 edges.
     * It is the generalized Petersen graph $GP(10,3)$
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateDesarguesGraph(Graph<V,E> targetGraph){
        generateGeneralizedPetersenGraph(targetGraph, 10, 3);
    }

    //-------------Nauru Graph-----------//
    /**
     * @see #generateNauruGraph
     * @return Nauru Graph
     */
    public static Graph<Integer, DefaultEdge> nauruGraph(){
        return generalizedPetersenGraph(12,5);
    }
    /**
     * Generates a <a href="http://mathworld.wolfram.com/NauruGraph.html">Nauru Graph</a>.
     * The Nauru graph is a symmetric bipartite cubic graph with 24 vertices and 36 edges.
     * It is the generalized Petersen graph $GP(12,5)$
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateNauruGraph(Graph<V,E> targetGraph){
        generateGeneralizedPetersenGraph(targetGraph, 12, 5);
    }

    //-------------Möbius-Kantor Graph-----------//
    /**
     * @see #generateMöbiusKantorGraph
     * @return Möbius-Kantor Graph
     */
    public static Graph<Integer, DefaultEdge> möbiusKantorGraph(){
        return generalizedPetersenGraph(12,5);
    }
    /**
     * Generates a <a href="http://mathworld.wolfram.com/Moebius-KantorGraph.html">Möbius-Kantor Graph</a>.
     * The unique cubic symmetric graph on 16 nodes.
     * It is the generalized Petersen graph $GP(8,3)$
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateMöbiusKantorGraph(Graph<V,E> targetGraph){
        generateGeneralizedPetersenGraph(targetGraph, 8, 3);
    }

    //-------------Bull Graph-----------//
    /**
     * @see #generateBullGraph
     * @return Bull Graph
     */
    public static Graph<Integer, DefaultEdge> bullGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateBullGraph(g);
        return g;
    }
    /**
     * Generates a <a href="http://mathworld.wolfram.com/BullGraph.html">Bull Graph</a>.
     * The bull graph is a simple graph on 5 nodes and 5 edges whose name derives from its resemblance to a schematic illustration of a bull or ram
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateBullGraph(Graph<V,E> targetGraph){
        this.addEdge(targetGraph, 0, 1);
        this.addEdge(targetGraph, 1, 2);
        this.addEdge(targetGraph, 2, 3);
        this.addEdge(targetGraph, 1, 3);
        this.addEdge(targetGraph, 3, 4);
    }

    //-------------Friendship Graph-----------//
    public static Graph<Integer, DefaultEdge> friendshipGraph(int n){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateFriendshipGraph(g, n);
        return g;
    }
    public void generateFriendshipGraph(Graph<V,E> targetGraph, int n){
        for(int i=0; i<n; i++){
           this.addEdge(targetGraph, 0, 2*i+1);
           this.addEdge(targetGraph, 0, 2*i+2);
           this.addEdge(targetGraph, 2*i+1, 2*i+2);
        }
    }

    //-------------Butterfly Graph-----------//
    /**
     * @see #generateButterflyGraph
     * @return Butterfly Graph
     */
    public static Graph<Integer, DefaultEdge> butterflyGraph(){
        return friendshipGraph(2);
    }
    /**
     * Generates a <a href="http://mathworld.wolfram.com/ButterflyGraph.html">Butterfly Graph</a>.
     * This graph is also known as the "bowtie graph" (West 2000, p. 12).
     * It is isomorphic to the friendship graph $F_2$.
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateButterflyGraph(Graph<V,E> targetGraph){
        generateFriendshipGraph(targetGraph, 2);
    }

    //-------------Claw Graph-----------//
    /**
     * @see #generateClawGraph
     * @return Claw Graph
     */
    public static Graph<Integer, DefaultEdge> clawGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateClawGraph(g);
        return g;
    }
    /**
     * Generates a <a href="http://mathworld.wolfram.com/ClawGraph.html">Claw Graph</a>.
     * The complete bipartite graph $K_{1,3}$ is a tree known as the "claw."
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateClawGraph(Graph<V,E> targetGraph){
        new StarGraphGenerator<V, E>(4).generateGraph(targetGraph, this.vertexFactory, null);
    }

    //-------------Windmill Graph-----------//
    public static Graph<Integer, DefaultEdge> windmillGraph(int m, int n){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateWindmillGraph(g, m, n);
        return g;
    }
    public void generateWindmillGraph(Graph<V,E> targetGraph, int m, int n){
        if(m<2 || n < 2)
            throw new IllegalArgumentException("parameters m and n are supposed to be larger or equal to 2");

        V center=addVertex(targetGraph, 0);
        List<V> sub=new ArrayList<>(n);

        for(int i=0; i<m; i++){ //m copies of complete graph Kn
            sub.clear();
            sub.add(center);
            for(int j=1; j<n; j++)
                sub.add(addVertex(targetGraph, targetGraph.vertexSet().size()));

            for(int r=0; r<sub.size()-1; r++)
                for(int s=r+1; s<sub.size(); s++)
                    targetGraph.addEdge(sub.get(r), sub.get(s));
        }
    }

    //-------------Dutch Windmill Graph-----------//
    public static Graph<Integer, DefaultEdge> dutchWindmillGraph(int m, int n){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateWindmillGraph(g, m, n);
        return g;
    }
    public void generateDutchWindmillGraph(Graph<V,E> targetGraph, int m, int n){
        if(m<2 || n < 3)
            throw new IllegalArgumentException("Invalid parameters. Required: m>=2, and n>=3");

        V center=addVertex(targetGraph, 0);
        List<V> sub=new ArrayList<>(n);

        for(int i=0; i<m; i++){ //m copies of cycle graph Cn
            sub.clear();
            sub.add(center);
            for(int j=1; j<n; j++)
                sub.add(addVertex(targetGraph, targetGraph.vertexSet().size()));

            for(int r=0; r<sub.size(); r++)
                targetGraph.addEdge(sub.get(r), sub.get((r+1)%n));
        }
    }

    //-------------Bucky ball Graph-----------//
    /**
     * @see #generateBuckyBallGraph
     * @return Bucky ball Graph
     */
    public static Graph<Integer, DefaultEdge> buckyBallGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateBuckyBallGraph(g);
        return g;
    }
    /**
     * Generates a <a href="https://en.wikipedia.org/wiki/Fullerene">Bucky ball Graph</a>.
     * This graph is a 3-regular 60-vertex planar graph. Its vertices and edges correspond precisely to the carbon
     * atoms and bonds in buckminsterfullerene.  When embedded on a sphere, its 12 pentagon and 20 hexagon faces are
     * arranged exactly as the sections of a soccer ball.
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateBuckyBallGraph(Graph<V,E> targetGraph){
        int[][] edges={{0, 2}, {0, 48}, {0, 59}, {1, 3}, {1, 9}, {1, 58}, {2, 3}, {2, 36}, {3, 17}, {4, 6}, {4, 8}, {4, 12}, {5, 7}, {5, 9}, {5, 16}, {6, 7}, {6, 20}, {7, 21}, {8, 9}, {8, 56}, {10, 11}, {10, 12}, {10, 20}, {11, 27}, {11, 47}, {12, 13}, {13, 46}, {13, 54}, {14, 15}, {14, 16}, {14, 21}, {15, 25}, {15, 41}, {16, 17}, {17, 40}, {18, 19}, {18, 20}, {18, 26}, {19, 21}, {19, 24}, {22, 23}, {22, 31}, {22, 34}, {23, 25}, {23, 38}, {24, 25}, {24, 30}, {26, 27}, {26, 30}, {27, 29}, {28, 29}, {28, 31}, {28, 35}, {29, 44}, {30, 31}, {32, 34}, {32, 39}, {32, 50}, {33, 35}, {33, 45}, {33, 51}, {34, 35}, {36, 37}, {36, 40}, {37, 39}, {37, 52}, {38, 39}, {38, 41}, {40, 41}, {42, 43}, {42, 46}, {42, 55}, {43, 45}, {43, 53}, {44, 45}, {44, 47}, {46, 47}, {48, 49}, {48, 52}, {49, 53}, {49, 57}, {50, 51}, {50, 52}, {51, 53}, {54, 55}, {54, 56}, {55, 57}, {56, 58}, {57, 59}, {58, 59}};
        for(int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    //-------------Clebsch Graph-----------//
    /**
     * @see #generateClebschGraph
     * @return Clebsch Graph
     */
    public static Graph<Integer, DefaultEdge> clebschGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateClebschGraph(g);
        return g;
    }
    /**
     * Generates a <a href="http://mathworld.wolfram.com/ClebschGraph.html">Clebsch Graph</a>.
     * The Clebsch graph, also known as the Greenwood-Gleason graph (Read and Wilson, 1998, p. 284), is a strongly
     * regular quintic graph on 16 vertices and 40 edges.
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateClebschGraph(Graph<V,E> targetGraph){
        int x = 0;
        for(int i=0; i<8; i++) {
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

    //-------------Grötzsch Graph-----------//
    /**
     * @see #generateGrötzschGraph
     * @return Grötzsch Graph
     */
    public static Graph<Integer, DefaultEdge> grötzschGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateGrötzschGraph(g);
        return g;
    }
    /**
     * Generates a <a href="http://mathworld.wolfram.com/GroetzschGraph.html">Grötzsch Graph</a>.
     * The Grötzsch graph is smallest triangle-free graph with chromatic number four.
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateGrötzschGraph(Graph<V,E> targetGraph){
        for(int i=1; i<6; i++)
            addEdge(targetGraph, 0, i);
        addEdge(targetGraph, 10, 6);
        for(int i=6; i<10; i++){
            addEdge(targetGraph, i, i+1);
            addEdge(targetGraph, i, i-4);
        }
        addEdge(targetGraph, 10, 1);
        for(int i=7; i<11; i++)
            addEdge(targetGraph, i, i-6);
        addEdge(targetGraph, 6, 5);
    }
    ///////////////////////////////////////////////

    //-------------Hall-Janko Graph-----------//
    /* Graph has 1800 edges: too big to include as edge array. Need sparse6 dependency
    public static Graph<Integer, DefaultEdge> hallJankoGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateHallJankoGraph(g);
        return g;
    }
    public void generateHallJankoGraph(Graph<V,E> targetGraph){
        int[][] edges=;
        for(int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }*/

    //-------------Bidiakis cube Graph-----------//
    /**
     * @see #generateBidiakisCubeGraph
     * @return Bidiakis cube Graph
     */
    public static Graph<Integer, DefaultEdge> bidiakisCubeGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateBidiakisCubeGraph(g);
        return g;
    }

    /**
     * Generates a <a href="http://mathworld.wolfram.com/BidiakisCube.html">Bidiakis cube Graph</a>.
     * The 12-vertex graph consisting of a cube in which two opposite faces (say, top and bottom) have
     * edges drawn across them which connect the centers of opposite sides of the faces in such a way
     * that the orientation of the edges added on top and bottom are perpendicular to each other.
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateBidiakisCubeGraph(Graph<V,E> targetGraph){
        int[][] edges=;
        for(int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    //-------------First Blanusa Snark Graph-----------//
    /**
     * @see #generateBlanusaFirstSnarkGraph
     * @return First Blanusa Snark Graph
     */
    public static Graph<Integer, DefaultEdge> blanusaFirstSnarkGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateBlanusaFirstSnarkGraph(g);
        return g;
    }
    /**
     * Generates the <a href="http://mathworld.wolfram.com/BlanusaSnarks.html">First Blanusa Snark Graph</a>.
     * The Blanusa graphs are two snarks on 18 vertices and 27 edges.
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateBlanusaFirstSnarkGraph(Graph<V,E> targetGraph){
        int[][] edges=;
        for(int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    //-------------Second Blanusa Snark Graph-----------//
    /**
     * @see #generateBlanusaSecondSnarkGraph
     * @return Second Blanusa Snark Graph
     */
    public static Graph<Integer, DefaultEdge> blanusaSecondSnarkGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateBlanusaSecondSnarkGraph(g);
        return g;
    }
    /**
     * Generates the <a href="http://mathworld.wolfram.com/BlanusaSnarks.html">Second Blanusa Snark Graph</a>.
     * The Blanusa graphs are two snarks on 18 vertices and 27 edges.
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateBlanusaSecondSnarkGraph(Graph<V,E> targetGraph){
        int[][] edges=;
        for(int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    //-------------Double Star Snark Graph-----------//
    /**
     * @see #generateDoubleStarSnarkGraph
     * @return Double Star Snark Graph
     */
    public static Graph<Integer, DefaultEdge> doubleStarSnarkGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateDoubleStarSnarkGraph(g);
        return g;
    }
    /**
     * Generates the <a href="http://mathworld.wolfram.com/DoubleStarSnark.html">Double Star Snark Graph</a>.
     * A snark on 30 vertices with edge chromatic number 4.
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateDoubleStarSnarkGraph(Graph<V,E> targetGraph){
        int[][] edges=;
        for(int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    //-------------Brinkmann Graph-----------//
    /**
     * @see #generateBrinkmannGraph
     * @return Brinkmann Graph
     */
    public static Graph<Integer, DefaultEdge> brinkmannGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateBrinkmannGraph(g);
        return g;
    }
    /**
     * Generates the <a href="http://mathworld.wolfram.com/BrinkmannGraph.html">Brinkmann Graph</a>.
     * The Brinkmann graph is a weakly regular quartic graph on 21 vertices and 42 edges.
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateBrinkmannGraph(Graph<V,E> targetGraph){
        int[][] edges=;
        for(int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    //-------------Gosset Graph-----------//
    /**
     * @see #generateGossetGraph
     * @return Gosset Graph
     */
    public static Graph<Integer, DefaultEdge> gossetGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateGossetGraph(g);
        return g;
    }
    /**
     * Generates the <a href="http://mathworld.wolfram.com/GossetGraph.html">Gosset Graph</a>.
     * The Gosset graph is a 27-regular graph on 56 vertices which is the skeleton of the Gosset polytope $3_{21}$.
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateGossetGraph(Graph<V,E> targetGraph){
        int[][] edges=;
        for(int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    //-------------Chvatal Graph-----------//
    /**
     * @see #generateChvatalGraph
     * @return Chvatal Graph
     */
    public static Graph<Integer, DefaultEdge> chvatalGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateChvatalGraph(g);
        return g;
    }
    /**
     * Generates the <a href="http://mathworld.wolfram.com/ChvatalGraph.html">Chvatal Graph</a>.
     * The Chvátal graph is an undirected graph with 12 vertices and 24 edges, discovered by Václav Chvátal (1970)
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateChvatalGraph(Graph<V,E> targetGraph){
        int[][] edges=;
        for(int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    //-------------Kittell Graph-----------//
    /**
     * @see #generateKittellGraph
     * @return Kittell Graph
     */
    public static Graph<Integer, DefaultEdge> kittellGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateKittellGraph(g);
        return g;
    }
    /**
     * Generates the <a href="http://mathworld.wolfram.com/KittellGraph.html">Kittell Graph</a>.
     * The Kittell graph is a planar graph on 23 nodes and 63 edges that tangles the Kempe chains in Kempe's algorithm
     * and thus provides an example of how Kempe's supposed proof of the four-color theorem fails.
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateKittellGraph(Graph<V,E> targetGraph){
        int[][] edges=;
        for(int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    //-------------Coxeter Graph-----------//
    /**
     * @see #generateCoxeterGraph
     * @return Coxeter Graph
     */
    public static Graph<Integer, DefaultEdge> coxeterGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateCoxeterGraph(g);
        return g;
    }
    /**
     * Generates the <a href="http://mathworld.wolfram.com/CoxeterGraph.html">Coxeter Graph</a>.
     * The Coxeter graph is a nonhamiltonian cubic symmetric graph on 28 vertices and 42 edges.
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateCoxeterGraph(Graph<V,E> targetGraph){
        int[][] edges=;
        for(int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    //-------------Ellingham-Horton 78 Graph-----------//
    /**
     * @see #generateEllinghamHorton78Graph
     * @return Ellingham-Horton 78 Graph
     */
    public static Graph<Integer, DefaultEdge> ellinghamHorton78Graph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateEllinghamHorton78Graph(g);
        return g;
    }
    /**
     * Generates the <a href="http://mathworld.wolfram.com/Ellingham-HortonGraphs.html">Ellingham-Horton 78 Graph</a>.
     * The Ellingham–Horton graph is a 3-regular graph of 78 vertices
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateEllinghamHorton78Graph(Graph<V,E> targetGraph){
        int[][] edges=;
        for(int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    //-------------Errera Graph-----------//
    /**
     * @see #generateErreraGraph
     * @return Errera Graph
     */
    public static Graph<Integer, DefaultEdge> erreraGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateErreraGraph(g);
        return g;
    }
    /**
     * Generates the <a href="http://mathworld.wolfram.com/ErreraGraph.html">Errera Graph</a>.
     * The Errera graph is the 17-node planar graph
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateErreraGraph(Graph<V,E> targetGraph){
        int[][] edges=;
        for(int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    //-------------Franklin Graph-----------//
    /**
     * @see #generateFranklinGraph
     * @return Franklin Graph
     */
    public static Graph<Integer, DefaultEdge> franklinGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateFranklinGraph(g);
        return g;
    }
    /**
     * Generates the <a href="http://mathworld.wolfram.com/FranklinGraph.html">Franklin Graph</a>.
     * The Franklin graph is the 12-vertex cubic graph.
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateFranklinGraph(Graph<V,E> targetGraph){
        int[][] edges=;
        for(int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    //-------------Frucht Graph-----------//
    /**
     * @see #generateFruchtGraph
     * @return Frucht Graph
     */
    public static Graph<Integer, DefaultEdge> fruchtGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateFruchtGraph(g);
        return g;
    }
    /**
     * Generates the <a href="http://mathworld.wolfram.com/FruchtGraph.html">Frucht Graph</a>.
     * The Frucht graph is smallest cubic identity graph.
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateFruchtGraph(Graph<V,E> targetGraph){
        int[][] edges=;
        for(int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    //-------------Goldner-Harary Graph-----------//
    /**
     * @see #generateGoldnerHararyGraph
     * @return Goldner-Harary Graph
     */
    public static Graph<Integer, DefaultEdge> goldnerHararyGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateGoldnerHararyGraph(g);
        return g;
    }
    /**
     * Generates the <a href="http://mathworld.wolfram.com/Goldner-HararyGraph.html">Goldner-Harary Graph</a>.
     * The Goldner-Harary graph is a graph on 11 vertices and 27. It is a simplicial graph, meaning that it is
     * polyhedral and consists of only triangular faces.
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateGoldnerHararyGraph(Graph<V,E> targetGraph){
        int[][] edges=;
        for(int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    //-------------Heawood Graph-----------//
    /**
     * @see #generateHeawoodGraph
     * @return Heawood Graph
     */
    public static Graph<Integer, DefaultEdge> heawoodGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateHeawoodGraph(g);
        return g;
    }
    /**
     * Generates the <a href="http://mathworld.wolfram.com/HeawoodGraph.html">Heawood Graph</a>.
     * Heawood graph is an undirected graph with 14 vertices and 21 edges, named after Percy John Heawood.
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateHeawoodGraph(Graph<V,E> targetGraph){
        int[][] edges=;
        for(int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    //-------------Herschel Graph-----------//
    /**
     * @see #generateHerschelGraph
     * @return Herschel Graph
     */
    public static Graph<Integer, DefaultEdge> herschelGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateHerschelGraph(g);
        return g;
    }
    /**
     * Generates the <a href="http://mathworld.wolfram.com/HerschelGraph.html">Herschel Graph</a>.
     * The Herschel graph is the smallest nonhamiltonian polyhedral graph (Coxeter 1973, p. 8).
     * It is the unique such graph on 11 nodes and 18 edges.
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateHerschelGraph(Graph<V,E> targetGraph){
        int[][] edges=;
        for(int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    //-------------Hoffman Graph-----------//
    /**
     * @see #generateHoffmanGraph
     * @return Hoffman Graph
     */
    public static Graph<Integer, DefaultEdge> hoffmanGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateHoffmanGraph(g);
        return g;
    }
    /**
     * Generates the <a href="http://mathworld.wolfram.com/HoffmanGraph.html">Hoffman Graph</a>.
     * The Hoffman graph is the bipartite graph on 16 nodes and 32 edges.
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateHoffmanGraph(Graph<V,E> targetGraph){
        int[][] edges=;
        for(int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    //-------------Krackhardt kite Graph-----------//
    /**
     * @see #generateKrackhardtKiteGraph
     * @return Krackhardt kite Graph
     */
    public static Graph<Integer, DefaultEdge> krackhardtKiteGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateKrackhardtKiteGraph(g);
        return g;
    }
    /**
     * Generates the <a href="http://mathworld.wolfram.com/KrackhardtKite.html">Krackhardt kite Graph</a>.
     * The Krackhardt kite is the simple graph on 10 nodes and 18 edges. It arises in social network theory.
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateKrackhardtKiteGraph(Graph<V,E> targetGraph){
        int[][] edges=;
        for(int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    //-------------Klein 3-regular Graph-----------//
    /**
     * @see #generateKlein3RegularGraph
     * @return Klein 3-regular Graph
     */
    public static Graph<Integer, DefaultEdge> klein3RegularGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateKlein3RegularGraph(g);
        return g;
    }
    /**
     * Generates the <a href="https://en.wikipedia.org/wiki/Klein_graphs">Klein 3-regular Graph</a>.
     * This graph is a 3-regular graph with 56 vertices and 84 edges, named after Felix Klein.
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateKlein3RegularGraph(Graph<V,E> targetGraph){
        int[][] edges=;
        for(int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    //-------------Klein 7-regular Graph-----------//
    /**
     * @see #generateKlein7RegularGraph
     * @return Klein 7-regular Graph
     */
    public static Graph<Integer, DefaultEdge> klein7RegularGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateKlein7RegularGraph(g);
        return g;
    }
    /**
     * Generates the <a href="https://en.wikipedia.org/wiki/Klein_graphs">Klein 7-regular Graph</a>.
     * This graph is a 7-regular graph with 24 vertices and 84 edges, named after Felix Klein.
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateKlein7RegularGraph(Graph<V,E> targetGraph){
        int[][] edges=;
        for(int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    //-------------Moser spindle Graph-----------//
    /**
     * @see #generateMoserSpindleGraph
     * @return Moser spindle Graph
     */
    public static Graph<Integer, DefaultEdge> moserSpindleGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateMoserSpindleGraph(g);
        return g;
    }
    /**
     * Generates the <a href="http://mathworld.wolfram.com/MoserSpindle.html">Moser spindle Graph</a>.
     * The Moser spindle is the 7-node unit-distance graph.
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateMoserSpindleGraph(Graph<V,E> targetGraph){
        int[][] edges=;
        for(int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    //-------------Poussin Graph-----------//
    /**
     * @see #generatePoussinGraph
     * @return Poussin Graph
     */
    public static Graph<Integer, DefaultEdge> poussinGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generatePoussinGraph(g);
        return g;
    }
    /**
     * Generates the <a href="http://mathworld.wolfram.com/PoussinGraph.html">Poussin Graph</a>.
     * The Poussin graph is the 15-node planar graph.
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generatePoussinGraph(Graph<V,E> targetGraph){
        int[][] edges=;
        for(int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    //-------------Schläfli Graph-----------//
    /**
     * @see #generateSchläfliGraph
     * @return Schläfli Graph
     */
    public static Graph<Integer, DefaultEdge> schläfliGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateSchläfliGraph(g);
        return g;
    }
    /**
     * Generates the <a href="http://mathworld.wolfram.com/SchlaefliGraph.html">Schläfli Graph</a>.
     * The Schläfli graph is a strongly regular graph on 27 nodes
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateSchläfliGraph(Graph<V,E> targetGraph){
        int[][] edges=;
        for(int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    //-------------Thomsen Graph-----------//
    /**
     * @see #generateThomsenGraph
     * @return Thomsen Graph
     */
    public static Graph<Integer, DefaultEdge> thomsenGraph(){
        Graph<Integer, DefaultEdge> g=new SimpleGraph<>(DefaultEdge.class);
        new NamedGraphGenerator<Integer, DefaultEdge>(new IntegerVertexFactory()).generateThomsenGraph(g);
        return g;
    }
    /**
     * Generates the <a href="http://mathworld.wolfram.com/UtilityGraph.html">Thomsen Graph</a>.
     * The Thomsen Graph is complete bipartite graph consisting of 6 vertices (3 vertices in each bipartite
     * partition. It is also called the Utility graph.
     * @param targetGraph receives the generated edges and vertices; if this is non-empty on entry, the
     *        result will be a disconnected graph since generated elements will not be connected to
     *        existing elements
     */
    public void generateThomsenGraph(Graph<V,E> targetGraph){
        int[][] edges=;
        for(int[] edge : edges)
            addEdge(targetGraph, edge[0], edge[1]);
    }

    //--------------Helper methods-----------------/
    private V addVertex(Graph<V,E> targetGraph, int i){
        if(!vertexMap.containsKey(i)) {
            V v=vertexFactory.createVertex();
            vertexMap.put(i, v);
            targetGraph.addVertex(v);
        }
        return vertexMap.get(i);
    }
    private void addEdge(Graph<V,E> targetGraph, int i, int j){
        V u = addVertex(targetGraph, i);
        V v = addVertex(targetGraph, j);
        targetGraph.addEdge(u, v);
    }

    public static class IntegerVertexFactory implements VertexFactory<Integer>
    {
        private int counter = 0;

        @Override
        public Integer createVertex()
        {
            return counter++;
        }

    }

}
