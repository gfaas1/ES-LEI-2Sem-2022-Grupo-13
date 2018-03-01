package org.jgrapht.alg.cycle;

import org.jgrapht.alg.cycle.BucketList;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BucketListTest {

    /**
     * Tests proper creation of bucket list and removal of vertices from it
     */
    @Test
    public void testBucketList() {
        BucketList<String> bucketList = new BucketList<>(Arrays.asList("a", "b", "c", "d", "e"));
        assertTrue(bucketList.containsBucketWith("a"));
        assertTrue(bucketList.containsBucketWith("b"));
        assertTrue(bucketList.containsBucketWith("c"));
        assertTrue(bucketList.containsBucketWith("d"));
        assertTrue(bucketList.containsBucketWith("e"));
        String vertex1 = bucketList.poll();
        assertFalse(bucketList.containsBucketWith(vertex1));
        String vertex2 = bucketList.poll();
        assertFalse(bucketList.containsBucketWith(vertex2));
        String vertex3 = bucketList.poll();
        assertFalse(bucketList.containsBucketWith(vertex3));
        String vertex4 = bucketList.poll();
        assertFalse(bucketList.containsBucketWith(vertex4));
        String vertex5 = bucketList.poll();
        assertFalse(bucketList.containsBucketWith(vertex5));
    }

    /**
     * Tests proper updating of vertices positions in {@code BucketList}
     */
    @Test
    public void testBucketList2() {
        List<Integer> vertices = Arrays.asList(0, 1, 2, 3, 4);
        BucketList<Integer> bucketList = new BucketList<>(vertices);
        int vertex1 = bucketList.poll();
        assertFalse(bucketList.containsBucketWith(vertex1));
        int vertex2 = (vertex1 + 1) % 5;
        int vertex3 = (vertex1 + 2) % 5;
        assertTrue(bucketList.containsBucketWith(vertex2));
        assertTrue(bucketList.containsBucketWith(vertex3));
        bucketList.updateBuckets(new HashSet<>(Arrays.asList(vertex2, vertex3)));
        assertTrue(bucketList.containsBucketWith(vertex2));
        assertTrue(bucketList.containsBucketWith(vertex3));
        int vertex4 = bucketList.poll();
        int vertex5 = bucketList.poll();
        assertTrue(vertex2 == vertex4 || vertex2 == vertex5);
        assertTrue(vertex3 == vertex4 || vertex3 == vertex5);
        assertTrue(vertex4 != vertex5);
        assertFalse(bucketList.containsBucketWith(vertex4));
        assertFalse(bucketList.containsBucketWith(vertex5));
    }
}
