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
import static org.bruceeddy.Shutters.gen;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ShutterTest {

    private List<Integer> xs;
    private List<Integer> ys;
    private Shutter<List<Integer>, Integer> head;

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


