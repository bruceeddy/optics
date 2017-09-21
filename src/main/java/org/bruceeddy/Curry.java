package org.bruceeddy;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Curry {
    public static <A, B, C> Function<A, Function<B, C>> curry(final BiFunction<A, B, C> f) {
        return (A a) -> (B b) -> f.apply(a, b);
    }
}
