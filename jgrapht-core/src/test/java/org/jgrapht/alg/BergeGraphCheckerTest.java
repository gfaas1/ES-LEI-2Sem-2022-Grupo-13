/*
 * (C) Copyright 2016-2018, by Philipp S. Kaesgen and Contributors.
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
package org.jgrapht.alg;
import org.junit.Test;


import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.jgrapht.generate.GnmRandomBipartiteGraphGenerator;
import org.jgrapht.generate.WheelGraphGenerator;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.util.SupplierUtil;

public class BergeGraphCheckerTest extends BergeGraphChecker<Integer,Integer>{
    private SimpleGraph<Integer,Integer> stimulus;
    
    
    
    private void reset(){
        stimulus= new SimpleGraph<Integer,Integer>(SupplierUtil.createIntegerSupplier(),SupplierUtil.createIntegerSupplier(),false);
    }

    private int maximalNumberOfVertices = 17,
            minimalNumberOfVertices = 14;
    
    private int repititionsPerTestCase = 1;
    

    
    @Test
    public void checkPyramid(){
        reset();
                stimulus.addVertex(1);//b1
                stimulus.addVertex(2);//b2
                stimulus.addVertex(3);//b3
                
                stimulus.addEdge(1, 2);
                stimulus.addEdge(2, 3);
                stimulus.addEdge(3, 1);
                
                stimulus.addVertex(4);//s1
                stimulus.addVertex(5);//s2
                stimulus.addVertex(6);//s3
                stimulus.addVertex(7);//a
                
                stimulus.addEdge(4, 7);
                stimulus.addEdge(5, 7);
                stimulus.addEdge(6, 7);
                
                /*optional either
                stimulus.addEdge(7,1);iff 1 in {4,5,6}
                stimulus.addEdge(7,2);iff 2 in {4,5,6}
                stimulus.addEdge(7,3);iff 3 in {4,5,6}
                */
                
                
                stimulus.addVertex(8);//m1
                stimulus.addVertex(9);//m2
                stimulus.addVertex(10);//m3
                
                stimulus.addVertex(11);//S1
                stimulus.addVertex(12);//S2
                stimulus.addVertex(13);//S3
                stimulus.addVertex(14);//T1
                stimulus.addVertex(15);//T2
                stimulus.addVertex(16);//T3
                
                stimulus.addEdge(4, 11);
                stimulus.addEdge(11, 8);
                stimulus.addEdge(5, 12);
                stimulus.addEdge(12, 9);
                stimulus.addEdge(6, 13);
                stimulus.addEdge(13, 10);
                
                stimulus.addEdge(8, 14);
                stimulus.addEdge(14, 1);
                stimulus.addEdge(9, 15);
                stimulus.addEdge(15, 2);
                stimulus.addEdge(10, 16);
                stimulus.addEdge(16, 3);

                assertEquals(true,containsPyramid(stimulus));
                
                stimulus.addEdge(4, 2);
                assertEquals(false,containsPyramid(stimulus));
    }
    
    @Test
    public void checkJewel(){
        reset();
        
        stimulus.addVertex(1);
        stimulus.addVertex(2);
        stimulus.addVertex(3);
        stimulus.addVertex(4);
        stimulus.addVertex(5);
        
        stimulus.addEdge(1, 2);
        stimulus.addEdge(2, 3);
        stimulus.addEdge(3, 4);
        stimulus.addEdge(4, 5);
        stimulus.addEdge(5, 1);
        
        /*
            non-edges:
            v1v3
            v2v4
            v1v4
        */

        stimulus.addVertex(6);
        stimulus.addVertex(7);
        stimulus.addVertex(8);
        
        stimulus.addEdge(1, 6);
        stimulus.addEdge(6, 7);
        stimulus.addEdge(7, 8);
        stimulus.addEdge(8, 4);
        
        assertEquals(true,containsJewel(stimulus));
        
        stimulus.addEdge(1, 3);
        assertEquals(false,containsJewel(stimulus));
    }
    
    @Test
    public void checkIsYXComplete(){
        reset();
        
        stimulus.addVertex(1);
        stimulus.addVertex(2);
        stimulus.addVertex(3);
        stimulus.addVertex(4);
        
        stimulus.addEdge(1, 4);
        stimulus.addEdge(1, 2);
        stimulus.addEdge(1, 3);
        Set<Integer> X = new HashSet<Integer>();
        X.add(2);
        X.add(3);
        X.add(4);
        assertEquals(true,isYXComplete(stimulus,1,X));
        
        stimulus.removeEdge(1,4);
        assertEquals(false,isYXComplete(stimulus,1,X));
        stimulus.addEdge(1, 4);
        
        X.clear();
        X.add(2);
        X.add(1);
        assertEquals(false,isYXComplete(stimulus,3,X));
        
    }
    
    @Test
    public void checkConfigurationType2(){
        reset();
                
        stimulus.addVertex(1);
        stimulus.addVertex(2);
        stimulus.addVertex(3);
        stimulus.addVertex(4);
        stimulus.addVertex(5);//p1
        stimulus.addVertex(6);//x
        stimulus.addVertex(7);//p2=P*
        stimulus.addVertex(8);//p3
        
        stimulus.addEdge(1, 2);
        stimulus.addEdge(2, 3);
        stimulus.addEdge(3, 4);
        
        stimulus.addEdge(1, 6);
        stimulus.addEdge(2, 6);
        stimulus.addEdge(4, 6);
        
        stimulus.addEdge(1, 5);
        stimulus.addEdge(5, 7);
        stimulus.addEdge(7, 8);
        stimulus.addEdge(4, 8);
        
        assertEquals(true,hasConfigurationType2(stimulus));

        stimulus.addEdge(3, 6);
        assertEquals(true,hasConfigurationType2(stimulus));
        
        stimulus.addEdge(7, 6);
        
        assertEquals(false, hasConfigurationType2(stimulus));
        
        
        stimulus.removeEdge(3,6);
        stimulus.removeEdge(4,8);
        assertEquals(false, hasConfigurationType2(stimulus));
    }
    
    @Test
    public void checkConfigurationType3(){
        reset();
                
        stimulus.addVertex(1);
        stimulus.addVertex(2);
        stimulus.addVertex(3);
        stimulus.addVertex(4);
        stimulus.addVertex(5);
        stimulus.addVertex(6);
        
        stimulus.addEdge(1, 2);
        stimulus.addEdge(3, 4);
        stimulus.addEdge(1, 4);
        stimulus.addEdge(2, 3);
        stimulus.addEdge(3, 5);
        stimulus.addEdge(4, 6);
        
        /*
          Non-edges:
         stimulus.addEdge(1,3);
         stimulus.addEdge(2,4);
         stimulus.addEdge(1,5);
         stimulus.addEdge(2,5);
         stimulus.addEdge(1,6);
         stimulus.addEdge(2,6);
         stimulus.addEdge(4,5);
         
         Optional edges:
         stimulus.addEdge(3,5);
         stimulus.addEdge(3,6);
         
         stimulus.addEdge(5,6);
         implies non-edge
         stimulus.addEdge(6,7);
         */

        stimulus.addVertex(7);//x
        
        stimulus.addEdge(1, 7);
        stimulus.addEdge(2, 7);
        stimulus.addEdge(5, 7);
        
        /*
         Non-edges either:
         stimulus.addEdge(3,7);
         or
         stimulus.addEdge(4,7);
!!         Note: one is to choose, otherwise it is a 5-Cycle        !!
         
         Optional edges if non-edge stimulus.addEdge(5,6);
         stimulus.addEdge(6,7);
         */
        
        
        stimulus.addVertex(8);//p1
        stimulus.addVertex(9);//p2
        stimulus.addVertex(10);//p3
        
        stimulus.addEdge(5, 8);
        stimulus.addEdge(8, 9);
        stimulus.addEdge(9, 10);
        stimulus.addEdge(10, 6);
        
        /*
        Non-edges:
        stimulus.addEdge(1,9);
        stimulus.addEdge(2,9);
        stimulus.addEdge(7,9);
        
        Optional edges:
        stimulus.addEdge(1,8);
        stimulus.addEdge(2,8);
        stimulus.addEdge(3,8);
        stimulus.addEdge(4,8);
        stimulus.addEdge(6,8);
        stimulus.addEdge(7,8);
        stimulus.addEdge(8,10);
        stimulus.addEdge(3,9);
        stimulus.addEdge(4,9);
        stimulus.addEdge(5,9);
        stimulus.addEdge(6,9);
    
        */

        assertEquals(true,hasConfigurationType3(stimulus));

        stimulus.addEdge(4, 7);
        assertEquals(false,hasConfigurationType3(stimulus));
    }
    
    @Test
    public void checkCleanOddHole(){
        reset();
        stimulus.addVertex(1);
        stimulus.addVertex(2);
        stimulus.addVertex(3);
        stimulus.addVertex(4);
        stimulus.addVertex(5);
        stimulus.addVertex(6);
        stimulus.addVertex(7);
        
        stimulus.addEdge(1, 2);
        stimulus.addEdge(3, 2);
        stimulus.addEdge(4, 3);
        stimulus.addEdge(4, 5);
        stimulus.addEdge(6, 5);
        stimulus.addEdge(6, 7);
        stimulus.addEdge(7, 1);
        
        assertEquals(true,containsCleanShortestOddHole(stimulus));
        
        stimulus.addEdge(3, 7);
        stimulus.addEdge(4, 7);
        assertEquals(false,containsCleanShortestOddHole(stimulus));
    }
    
    @Test
    public void checkContainsShortestOddHole() {
        reset();
        stimulus.addVertex(1);
        stimulus.addVertex(2);
        stimulus.addVertex(3);
        stimulus.addVertex(4);
        stimulus.addVertex(5);
        stimulus.addVertex(6);
        stimulus.addVertex(7);
        
        stimulus.addEdge(1, 2);
        stimulus.addEdge(3, 2);
        stimulus.addEdge(4, 3);
        stimulus.addEdge(4, 5);
        stimulus.addEdge(6, 5);
        stimulus.addEdge(6, 7);
        stimulus.addEdge(7, 1);
        
        stimulus.addVertex(8);//Cleaner
        stimulus.addEdge(3, 8);
        stimulus.addEdge(8, 7);
        stimulus.addEdge(8, 5);
        assertEquals(true,containsCleanShortestOddHole(stimulus));
        
        stimulus.removeVertex(8);
        assertEquals(true,containsCleanShortestOddHole(stimulus));
        
        

        
    }
    
    @Test
    public void checkRoutine3(){
        reset();
        
        stimulus.addVertex(1);//u
        stimulus.addVertex(2);//v
        stimulus.addVertex(3);
        stimulus.addVertex(4);
        
        stimulus.addEdge(1, 2);
        stimulus.addEdge(2, 3);
        stimulus.addEdge(2, 4);
        stimulus.addEdge(1, 3);
        stimulus.addEdge(1, 4);
        
        Set<Set<Integer>> golden = new HashSet<Set<Integer>>();
        Set<Integer> golden1 = new HashSet<Integer>(), golden2 = new HashSet<Integer>();
        golden1.add(1);
        golden1.add(2);
        golden2.add(1);
        golden2.add(2);
        golden2.add(3);
        golden2.add(4);
        golden.add(golden1);
        golden.add(golden2);
        
        assertEquals(golden,routine3(stimulus));
    }
    
    
    
    @Test
    public void checkBipartiteGraphs(){
        int repititions = repititionsPerTestCase;
        reset();
        while(repititions-->0){
            int n1 = new Random().nextInt(maximalNumberOfVertices-minimalNumberOfVertices)/2+minimalNumberOfVertices/2,
                    n2 = maximalNumberOfVertices-n1;
            
            
            int maximalNumberOfEdges = n1*n2;        
            int numberOfEdges = new Random().nextInt(maximalNumberOfEdges);    
            
            reset();
            new GnmRandomBipartiteGraphGenerator<Integer,Integer>(n1,n2,numberOfEdges).generateGraph(stimulus);
            
            assertEquals(true,isBerge(stimulus));
        }
        
        
    }
    
    @Test
    public void checkWheelGraphs(){

        int repititions=repititionsPerTestCase;
        while (repititions-->0){

            int numberOfVertices = new Random().nextInt(maximalNumberOfVertices-minimalNumberOfVertices)+minimalNumberOfVertices;
            if (numberOfVertices%2==0) numberOfVertices+=1;
            assertEquals(true,maximalNumberOfVertices>minimalNumberOfVertices);
            
            
            reset();
            new WheelGraphGenerator<Integer,Integer>(numberOfVertices).generateGraph(stimulus);
            
            
            assertEquals(true,isBerge(stimulus));
        }
        
        repititions=repititionsPerTestCase;
        while (repititions-->0){

            int numberOfVertices = new Random().nextInt(maximalNumberOfVertices-minimalNumberOfVertices)+minimalNumberOfVertices;
            if (numberOfVertices%2==1) numberOfVertices+=1;
            assertEquals(true,maximalNumberOfVertices>minimalNumberOfVertices);
            
            
            reset();
            new WheelGraphGenerator<Integer,Integer>(numberOfVertices).generateGraph(stimulus);
            
            
            assertEquals(false,isBerge(stimulus));
        }
    }
    
}
