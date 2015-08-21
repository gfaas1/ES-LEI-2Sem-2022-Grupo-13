package org.jgrapht;

import junit.framework.TestCase;
import org.jgrapht.alg.AStarShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.WeightedMultigraph;

/**
 * .Test class for AStarShortestPath implementation
 *
 * @author Joris Kinable
 * @since Aug 21, 2015
 */
public class AStarShortestPathTest extends TestCase{
    private final String[] labyrinth1={
                ". . . . . . . . . . . . . . . . . . . . . ####. . . . . . .",
                ". . . . . . . . . . . . . . . . . . . . . ####. . . . . . .",
                ". . . . . . . . . . . . . . . . . . . . . ####. . . . . . .",
                ". . . ####. . . . . . . . . . . . . . . . ####. . . . . . .",
                ". . . ####. . . . . . . . ####. . . . . . ####T . . . . . .",
                ". . . ####. . . . . . . . ####. . . . . . ##########. . . .",
                ". . . ####. . . . . . . . ####. . . . . . ##########. . . .",
                ". . . ####. . . . . . . . ####. . . . . . . . . . . . . . .",
                ". . . ####. . . . . . . . ####. . . . . . . . . . . . . . .",
                ". . . ####. . . . . . . . ####. . . . . . . . . . . . . . .",
                ". . . ####. . . . . . . . ####. . . . . . . . . . . . . . .",
                ". . . ####. . . . . . . . ####. . . . . . . . . . . . . . .",
                ". . . . . . . . . . . . . ####. . . . . . . . . . . . . . .",
                ". . . . . . . . . . . . . ####. . . . . . . . . . . . . . .",
                "S . . . . . . . . . . . . ####. . . . . . . . . . . . . . ."
    };

    private final String[] labyrinth2={ //Target node is unreachable
            ". . . . . . . . . . . . . . . . . . . . . ####. . . . . . .",
            ". . . . . . . . . . . . . . . . . . . . . ####. . . . . . .",
            ". . . . . . . . . . . . . . . . . . . . . ####. . . . . . .",
            ". . . ####. . . . . . . . . . . . . . . . ####### . . . . .",
            ". . . ####. . . . . . . . ####. . . . . . ####T## . . . . .",
            ". . . ####. . . . . . . . ####. . . . . . ##########. . . .",
            ". . . ####. . . . . . . . ####. . . . . . ##########. . . .",
            ". . . ####. . . . . . . . ####. . . . . . . . . . . . . . .",
            ". . . ####. . . . . . . . ####. . . . . . . . . . . . . . .",
            ". . . ####. . . . . . . . ####. . . . . . . . . . . . . . .",
            ". . . ####. . . . . . . . ####. . . . . . . . . . . . . . .",
            ". . . ####. . . . . . . . ####. . . . . . . . . . . . . . .",
            ". . . . . . . . . . . . . ####. . . . . . . . . . . . . . .",
            ". . . . . . . . . . . . . ####. . . . . . . . . . . . . . .",
            "S . . . . . . . . . . . . ####. . . . . . . . . . . . . . ."
    };

    private WeightedGraph<Node, DefaultWeightedEdge> graph;
    private Node sourceNode;
    private Node targetNode;

    private void readLabyrinth(String[] labyrinth){
        graph=new SimpleWeightedGraph<Node, DefaultWeightedEdge>(DefaultWeightedEdge.class);

        //Create the nodes
        Node[][] nodes=new Node[labyrinth.length][labyrinth[0].length()];
        for(int i=0; i<labyrinth.length; i++){
            for(int j=0; j<labyrinth[0].length(); j++){
                if(labyrinth[i].charAt(j)=='#' || labyrinth[i].charAt(j)==' ')
                    continue;
                nodes[i][j]=new Node(i,j);
                graph.addVertex(nodes[i][j]);
                if(labyrinth[i].charAt(j)=='S')
                    sourceNode=nodes[i][j];
                else if(labyrinth[i].charAt(j)=='T')
                    targetNode=nodes[i][j];
            }
        }
        //Create the edges
        //a. Horizontal edges
        for(int i=0; i<labyrinth.length; i++) {
            for (int j = 0; j < labyrinth[0].length()-2; j++) {
                if(nodes[i][j] == null || nodes[i][j+2]==null)
                    continue;
                Graphs.addEdge(graph, nodes[i][j], nodes[i][j+2], 1);
            }
        }
        //b. Vertical edges
        for(int i=0; i<labyrinth.length-1; i++) {
            for (int j = 0; j < labyrinth[0].length(); j++) {
                if(nodes[i][j] == null || nodes[i+1][j]==null)
                    continue;
                Graphs.addEdge(graph, nodes[i][j], nodes[i+1][j], 1);
            }
        }
    }

    public void testLabyrinth1(){
        this.readLabyrinth(labyrinth1);
        AStarShortestPath<Node, DefaultWeightedEdge> aStarShortestPath=new AStarShortestPath<>(graph);
        GraphPath<Node, DefaultWeightedEdge> path=aStarShortestPath.getShortestPath(sourceNode, targetNode, new ManhattanDistance());
        assertNotNull(path);
        assertEquals((int)path.getWeight(), 47);
        assertEquals(path.getEdgeList().size(), 47);
        assertEquals(Graphs.getPathVertexList(path).size(), 48);

        path=aStarShortestPath.getShortestPath(sourceNode, targetNode, new EuclideanDistance());
        assertNotNull(path);
        assertEquals((int)path.getWeight(), 47);
        assertEquals(path.getEdgeList().size(), 47);
    }

    public void testLabyrinth2(){
        this.readLabyrinth(labyrinth2);
        AStarShortestPath<Node, DefaultWeightedEdge> aStarShortestPath=new AStarShortestPath<>(graph);
        GraphPath<Node, DefaultWeightedEdge> path=aStarShortestPath.getShortestPath(sourceNode, targetNode, new ManhattanDistance());
        assertNull(path);
    }

    public void testMultiGraph(){
        WeightedMultigraph<Node, DefaultWeightedEdge> multigraph=new WeightedMultigraph<Node, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        Node n1=new Node(0,0);
        multigraph.addVertex(n1);
        Node n2=new Node(0,0);
        multigraph.addVertex(n2);
        Node n3=new Node(0,0);
        multigraph.addVertex(n3);
        Graphs.addEdge(multigraph,n1,n2, 5.0);
        Graphs.addEdge(multigraph,n1,n2, 4.0);
        Graphs.addEdge(multigraph,n1,n2, 8.0);
        Graphs.addEdge(multigraph,n2,n3, 7.0);
        Graphs.addEdge(multigraph,n2,n3, 9);
        Graphs.addEdge(multigraph,n2,n3, 2);
        AStarShortestPath<Node, DefaultWeightedEdge> aStarShortestPath=new AStarShortestPath<>(multigraph);
        GraphPath<Node, DefaultWeightedEdge> path=aStarShortestPath.getShortestPath(n1, n3, new ManhattanDistance());
        assertNotNull(path);
        assertEquals((int)path.getWeight(), 6);
        assertEquals(path.getEdgeList().size(), 2);
    }

    private class ManhattanDistance implements AStarShortestPath.AStarAdmissibleHeuristic<Node> {
        @Override
        public double getCostEstimate(Node start, Node goal) {
            return Math.abs(start.x-goal.x)+Math.abs(start.y-goal.y);
        }
    }

    private class EuclideanDistance implements AStarShortestPath.AStarAdmissibleHeuristic<Node> {
        @Override
        public double getCostEstimate(Node start, Node goal) {
            return Math.sqrt(Math.pow(start.x-goal.x,2)+Math.pow(start.y-goal.y,2));
        }
    }

    private class Node{
        public final int x;
        public final int y;

        private Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public String toString(){
            return "("+x+","+y+")";
        }
    }
}
