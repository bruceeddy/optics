package org.bruceeddy;


import org.hamcrest.collection.IsEmptyCollection;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static co.unruly.matchers.OptionalMatchers.contains;
import static co.unruly.matchers.OptionalMatchers.empty;
import static java.util.stream.Stream.concat;
import static org.bruceeddy.Lenses.curry;
import static org.bruceeddy.ShutterTest.Shutters.gen;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ShutterTest {

    private List<Integer> xs;
    private List<Integer> ys;
    private Shutter<List<Integer>, Integer> head;

    static class Shutters {
        static <V, R> Shutter<V, R> gen(Function<V, Optional<R>> getter, BiFunction<R, V, V> setter) {
            return new Shutter<V, R>() {

                @Override
                public Optional<R> getOptional(V v) {
                    return getter.apply(v);
                }

                @Override
                public Function<V, V> setOptional(R v) {
                    return curry(setter).apply(v);
                }

                @Override
                public boolean nonEmpty(V v) {
                    return getOptional(v).map(x -> false).orElse(true);
                }

                @Override
                public Function<V, V> modify(Function<R, R> f) {
                    return v -> modifyOptional(f).apply(v).orElse(v);
                }

                @Override
                public Function<V, Optional<V>> modifyOptional(Function<R, R> f) {
                    return v -> getOptional(v).map(f).map(r -> setOptional(r)).map(h -> h.apply(v));
                }
            };
        }
    }

    @Before
    public void createTargets() {
        xs = Arrays.asList(1, 2, 3);
        ys = Collections.emptyList();
    }

    @Before
    public void createSUT() {
        Function<List<Integer>, Optional<Integer>> getter = i -> i.stream().findFirst();
        BiFunction<Integer, List<Integer>, List<Integer>> setter =
                (j, i) -> concat(i.stream().limit(1).map(x -> j), i.stream().skip(1)).collect(Collectors.toList());

       /* Example of wrong setter - do laws catch this?
       (doesn't work for empty list)
       (and dates from when setter was a BiFunction<V,R,V> rather than <R,V,V>)
        BiFunction< List<Integer>,  Integer, List<Integer>> setter =
                (i,j ) -> concat(Stream.of(j), i.stream().skip(1)).collect(Collectors.toList());*/
        head = gen(getter, setter);
    }

    @Test
    public void shutterShouldGetPopulatedOptionalValue() {
        assertThat(head.getOptional(xs), contains(1));
    }

    @Test
    public void shutterShouldGetEmptyOptionalValue() {
        assertThat(head.getOptional(ys), empty());
    }

    @Test
    public void shutterShouldSetPopulatedOptionalValue() {
        List<Integer> set = head.setOptional(5).apply(xs);
        assertThat(set, IsIterableContainingInOrder.contains(5, 2, 3));
    }

    @Test
    public void shutterShouldSetEmptyOptionalValue() {
        List<Integer> set = head.setOptional(5).apply(ys);
        assertThat(set, IsEmptyCollection.empty());
    }

    @Test
    public void nonEmptyShouldReturnFalseWhereTargetIsPopulated() {
        assertThat(head.nonEmpty(xs), is(false));
    }

    @Test
    public void nonEmptyShouldReturnTrueWhereTargetIsEmpty() {
        assertThat(head.nonEmpty(ys), is(true));
    }

    @Test
    public void modifyShouldModifyPopulatedTarget() {
        List<Integer> set = head.modify(x -> x + 10).apply(xs);
        assertThat(set, IsIterableContainingInOrder.contains(11, 2, 3));
    }

    @Test
    public void modifyShouldNotModifyEmptyTarget() {
        List<Integer> set = head.modify(x -> x + 10).apply(ys);
        assertThat(set, IsEmptyCollection.empty());
    }

    @Test
    public void modifyOptionalShouldModifyPopulatedTarget() {
        Optional<List<Integer>> set = head.modifyOptional(x -> x + 10).apply(xs);
        assertThat(set, contains(IsIterableContainingInOrder.contains(11, 2, 3)));
    }

    @Test
    public void modifyOptionalShouldNotModifyEmptyTarget() {
        Optional<List<Integer>> set = head.modifyOptional(x -> x + 10).apply(ys);
        assertThat(set, empty());
    }
}


