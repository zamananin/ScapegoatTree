package scapeGoat;

import org.junit.jupiter.api.Test;


import java.util.Calendar;
import java.util.LinkedList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ScapegoatTest {
    static private final Random r = new Random(Calendar.getInstance().getTimeInMillis());

    @Test
    public void simpleTest(){
        ScapegoatTree<Integer> tree = new ScapegoatTree<>();
        Integer v = 5;
        assertTrue(tree.isEmpty());
        assertFalse(tree.contains(v));
        assertTrue(tree.add(v));
        assertTrue(tree.contains(v));
        assertEquals(1, tree.size());
        assertFalse(tree.contains("Meow"));
        assertTrue(tree.remove(1));
        assertEquals(0, tree.size());
        assertFalse(tree.contains(v));
    }

    @Test
    public void fewValuesTest(){
        ScapegoatTree<Integer> tree = new ScapegoatTree<>();
        LinkedList<Integer> list = new LinkedList<>();
        tree.setAlpha(0.5);
        assertTrue(tree.add(5));
        assertTrue(tree.contains(5));
        list.add(5);
        assertTrue(tree.add(6));
        assertTrue(tree.contains(6));
        list.add(6);
        assertTrue(tree.add(3));
        assertTrue(tree.contains(3));
        list.add(3);
        assertTrue(tree.add(0));
        assertTrue(tree.contains(0));
        list.add(0);
        assertTrue(tree.add(7));
        assertTrue(tree.contains(7));
        list.add(7);
        assertTrue(tree.add(8));
        list.add(8);
        assertEquals(6, tree.size());
        assertTrue(tree.contains(8));
        assertTrue(tree.containsAll(list));

        //       5
        //      / \
        //     3   7
        //    /   / \
        //   0   6   8

        assertTrue(tree.add(-4));
        list.add(-4);
        assertTrue(tree.contains(-4));
        assertTrue(tree.add(-10));
        list.add(-10);
        assertTrue(tree.contains(-10));
        assertTrue(tree.add(-9));
        list.add(-9);
        assertTrue(tree.contains(-9));
        assertFalse(tree.add(5));
    }

    @Test
    public void randomValuesTest() {
        ScapegoatTree<Integer> tree = new ScapegoatTree<>();
        tree.setAlpha(0.9);
        int m = 100;
        int size = 0;
        for (int n = 0; n < m; n++) {

            Integer rv = r.nextInt() % 10000;
            System.out.println(n + " " + rv);
            if (!tree.contains(rv)) {
                assertTrue(tree.add(rv));
                assertTrue(tree.contains(rv));
                size++;
                assertEquals(size, tree.size());
            }
        }
    }
}
