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

    static class Shutters  {
        static <V,R> Shutter<V,R> gen(Function<V,Optional<R>> getter, BiFunction<V,R,V> setter)  {
            return new Shutter<V,R>() {
                @Override public Optional<R> getOptional(V v)  {
                        return getter.apply(v);
                    }

                @Override
                public Function<R,V> setOptional(V v) {
                    return curry(setter).apply(v);
                }

                @Override
                public boolean nonEmpty(V v) {
                    return getOptional(v).map(x -> false).orElse(true);
                }
            };
        }
    }

    @Before
    public void createTargets()  {
        xs = Arrays.asList(1,2,3);
        ys = Collections.emptyList();
    }

    @Before
    public void createSUT()  {
        Function<List<Integer>,Optional<Integer>> getter = i -> i.stream().findFirst();
        BiFunction< List<Integer>,  Integer, List<Integer>> setter =
                (i,j ) -> concat(i.stream().limit(1).map(x -> j), i.stream().skip(1)).collect(Collectors.toList());

       /* Example of wrong setter - do laws catch this?
       (doesn't work for empty list)
        BiFunction< List<Integer>,  Integer, List<Integer>> setter =
                (i,j ) -> concat(Stream.of(j), i.stream().skip(1)).collect(Collectors.toList());*/
        head = gen(getter, setter);
    }

    @Test
    public void shutterShouldGetPopulatedOptionalValue()  {
        assertThat(head.getOptional(xs), contains(1));
    }

    @Test
    public void shutterShouldGetEmptyOptionalValue()  {
        assertThat(head.getOptional(ys), empty());
    }

    @Test
    public void shutterShouldSetPopulatedOptionalValue()  {
        List<Integer> set = head.setOptional(xs).apply(5);
        assertThat(set, IsIterableContainingInOrder.contains(5,2,3));
    }

    @Test
    public void shutterShouldSetEmptyOptionalValue()  {
        List<Integer> set = head.setOptional(ys).apply(5);
        assertThat(set, IsEmptyCollection.empty());
    }

    @Test
    public void nonEmptyShouldReturnFalseWhereTargetIsPopulated()  {
        assertThat(head.nonEmpty(xs), is(false));
    }

    @Test
    public void nonEmptyShouldReturnTrueWhereTargetIsEmpty()  {
        assertThat(head.nonEmpty(ys), is(true));
    }
}