package org.bruceeddy;

import org.junit.Test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.bruceeddy.TraversalTest.Traversals.genList;
import static org.bruceeddy.TraversalTest.Traversals.genPair;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;

public class TraversalTest {

    interface Traversal<S,A> {

        Function<S,List<A>> getAll();

        Function<S,List<A>> setList(A i);

        Function<S,Pair<A>> setPair(A i);
    }

    static class Pair<V> {
        V a;
        V b;

        public Pair(V a, V b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pair<?> pair = (Pair<?>) o;

            if (a != null ? !a.equals(pair.a) : pair.a != null) return false;
            return b != null ? b.equals(pair.b) : pair.b == null;
        }

        @Override
        public int hashCode() {
            int result = a != null ? a.hashCode() : 0;
            result = 31 * result + (b != null ? b.hashCode() : 0);
            return result;
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

                @Override
                public Function<S, List<V>> setList(V i) {
                    return s -> {
                        List<V> all = new LinkedList<>();
                        f.apply(s).forEachRemaining(a -> all.add(i));
                        return all;
                    };
                }

                @Override
                public Function<S, Pair<V>> setPair(V i) {
                    return s -> new Pair<>(i, i);
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
        Traversal<List<Integer>,Integer> trav = genList();
        assertThat(trav.getAll().apply(xs), contains(1, 2, 3, 4, 5));
    }

    @Test
    public void getAllShouldGetAllTargetsOfPair()  {
        Pair<Integer> xs = new Pair(1,2);
        Traversal<Pair<Integer>,Integer> trav = genPair();
        assertThat(trav.getAll().apply(xs), contains(1, 2));
    }

    @Test
    public void setShouldSetAllTargetsOfList()  {
        List<Integer> xs = asList(1,2,3,4,5);
        Traversal<List<Integer>,Integer> trav = genList();
        assertThat(trav.setList(0).apply(xs), contains(0, 0, 0, 0, 0));
    }

    @Test
    public void setShouldSetAllTargetsOfPair()  {
        Pair<Integer> xs = new Pair(1,2);
        Traversal<Pair<Integer>,Integer> trav = genPair();
        assertThat(trav.setPair(0).apply(xs), is(new Pair(0,0)));
    }
}
