package org.jgrapht.util;

import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Test class AsSynchronizedIterateOnCopySet.
 *
 * @author CHEN Kui
 */
public class AsSynchronizedIterateOnCopySetTest
{
    @Test
    public void testAdd()
    {
        AsSynchronizedIterateOnCopySet<Integer> set = new AsSynchronizedIterateOnCopySet<>(new HashSet<>());
        class Add
            implements Runnable
        {
            private int start, end;
            private Add(int _start, int _end)
            {
                start = _start;
                end = _end;
            }
            @Override
            public void run()
            {
                for (int i = start; i < end; i++) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    set.add(i);
                }
            }
        }

        for (int i = 0; i < 20; i++) {
            new Thread(new Add(20*i, 20*i + 20)).start();
        }
        while (Thread.activeCount() > 2);
        assertEquals(400, set.size());
    }

    @Test
    public void testRemove()
    {
        AsSynchronizedIterateOnCopySet<Integer> set = new AsSynchronizedIterateOnCopySet<>(new HashSet<>());
        Vector<Integer> v = new Vector<>();

        for (int i = 0; i < 1000; i++) {
            set.add(i);
            v.add(i);

        }
        class Remove
            implements Runnable
        {
            private int num;
            private Remove (int _num)
            {
                num = _num;
            }

            @Override
            public void run()
            {
                for (int i = 0; i < num; i++) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Integer o = v.remove(0);
                    set.remove(o);
                }
            }
        }
        for (int i = 0; i < 20; i++) {
            new Thread(new Remove(20)).start();
        }
        while (Thread.activeCount() > 2);
        assertEquals(600, set.size());
    }

    @Test
    public void testIterator() {
        AsSynchronizedIterateOnCopySet<Integer> set = new AsSynchronizedIterateOnCopySet<>(new HashSet<>());
        Collection<Integer> c = new ArrayList<>();
        for (int i = 0; i < 1000; i++)
            set.add(i);

        for (int i = 1000; i < 1100; i++)
            c.add(i);

        assertEquals(1000, set.size());

        set.remove(0);
        Iterator it = set.iterator();
        assertEquals(1, it.next());
        assertFalse(set.contains(0));

        set.addAll(c);
        it = set.iterator();
        int cnt = 0;
        while (it.hasNext()) {
            cnt++;
            it.next();
        }
        assertEquals(1099, cnt);

        set.removeAll(c);
        it = set.iterator();
        cnt = 0;
        while (it.hasNext()) {
            cnt++;
            it.next();
        }
        assertEquals(999, cnt);

        set.retainAll(c);
        it = set.iterator();
        cnt = 0;
        while (it.hasNext()) {
            cnt++;
            it.next();
        }
        assertEquals(0, cnt);

        set.add(0);
        it = set.iterator();
        cnt = 0;
        while (it.hasNext()) {
            cnt++;
            it.next();
        }
        assertEquals(1, cnt);
    }
}
