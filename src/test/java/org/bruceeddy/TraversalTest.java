package org.bruceeddy;

import org.junit.Test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;

public class TraversalTest {

    interface Traversal<S,A> {

        Function<S,List<A>> getAll();
    }
    class Pair<V> {
        V a;
        V b;

        public Pair(V a, V b) {
            this.a = a;
            this.b = b;
        }
    }

    static class Traversals {

        public static <S,V> Traversal<S,V> genList(Function<S,Iterator<V>> f)  {
            return new Traversal<S, V>() {
                @Override
                public Function<S, List<V>> getAll() {
                    return s -> {
                        List<V> all = new LinkedList<>();
                        f.apply(s).forEachRemaining(all::add);
                        return all;
                    };
                }
            };
        }

        public static <A> Traversal<List<A>,A> genList()  {
            return genList(l -> l.iterator());
        }

        public static <A> Traversal<Pair<A>,A> genPair()  {
            return genList(p -> asList(p.a,p.b).iterator());
        }
    }

    @Test
    public void getAllShouldGetAllTargetsOfList()  {
        List<Integer> xs = asList(1,2,3,4,5);
        Traversal<List<Integer>,Integer> trav = Traversals.genList();
        assertThat(trav.getAll().apply(xs), contains(1, 2, 3, 4, 5));
    }

    @Test
    public void getAllShouldGetAllTargetsOfPair()  {
        Pair<Integer> xs = new Pair(1,2);
        Traversal<Pair<Integer>,Integer> trav = Traversals.genPair();
        assertThat(trav.getAll().apply(xs), contains(1, 2));
    }


}
