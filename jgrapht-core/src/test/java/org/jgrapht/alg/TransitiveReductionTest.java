/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
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
/* ----------------------
 * TransitiveReductionTest.java
 * ----------------------
 *
 * Original Author:   Christophe Thiebaud
 * Contributor(s):
 *
 * Changes
 * -------
 * 13-August-2015: Initial revision (CT);
 *
 */
package org.jgrapht.alg;

import java.util.Arrays;
import java.util.BitSet;

import org.junit.Assert;
import org.junit.Test;

public class TransitiveReductionTest {
    @Test
    public void test() {

        // @formatter:off
        final int[][] matrix = new int[][] {
            {0, 1, 1, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 1, 1},
            {0, 0, 0, 0, 1},
            {0, 1, 0, 0, 0}
        };
        
        final int[][] expected_path_matrix = new int[][] {
            {0, 1, 1, 1, 1},
            {0, 0, 0, 0, 0},
            {0, 1, 0, 1, 1},
            {0, 1, 0, 0, 1},
            {0, 1, 0, 0, 0}
        };
        
        final int[][] expected_transitively_reduced_matrix = new int[][] {
            {0, 0, 1, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 1, 0},
            {0, 0, 0, 0, 1},
            {0, 1, 0, 0, 0}
        };
        // @formatter:on

        // System.out.println(Arrays.deepToString(matrix) + " original matrix");

        final int n = matrix.length;

        // calc path matrix
        int[][] path_matrix = new int[n][n];
        {
            {
                System.arraycopy(matrix, 0, path_matrix, 0, matrix.length);

                final BitSet[] pathMatrixAsBitSetArray = asBitSetArray(path_matrix);

                TransitiveReduction.transformToPathMatrix(pathMatrixAsBitSetArray);

                path_matrix = asIntArray(pathMatrixAsBitSetArray);
            }
            // System.out.println(Arrays.deepToString(path_matrix) + " path matrix");

            Assert.assertArrayEquals(expected_path_matrix, path_matrix);
        }

        // calc transitive reduction
        {
            int[][] transitively_reduced_matrix = new int[n][n];
            {
                System.arraycopy(path_matrix, 0, transitively_reduced_matrix, 0, matrix.length);

                final BitSet[] transitivelyReducedMatrixAsBitSetArray = asBitSetArray(transitively_reduced_matrix);

                TransitiveReduction.transitiveReduction(transitivelyReducedMatrixAsBitSetArray);

                transitively_reduced_matrix = asIntArray(transitivelyReducedMatrixAsBitSetArray);
            }

            // System.out.println(Arrays.deepToString(transitively_reduced_matrix) + " transitive reduction");

            Assert.assertArrayEquals(expected_transitively_reduced_matrix, transitively_reduced_matrix);
        }
    }

    static private BitSet[] asBitSetArray(final int[][] intArray) {
        final BitSet[] ret = new BitSet[intArray.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = new BitSet(intArray[i].length);
            for (int j = 0; j < intArray[i].length; j++) {
                ret[i].set(j, intArray[i][j] == 1);
            }
        }
        return ret;
    }

    static private int[][] asIntArray(final BitSet[] bitsetArray) {
        final int[][] ret = new int[bitsetArray.length][bitsetArray.length];
        for (int i = 0; i < ret.length; i++) {
            for (int j = 0; j < ret.length; j++) {
                ret[i][j] = bitsetArray[i].get(j) ? 1 : 0;
            }
        }
        return ret;

    }

}
