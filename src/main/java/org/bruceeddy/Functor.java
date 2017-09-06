package org.bruceeddy;


import java.util.function.Function;

public interface Functor<T, V> {
    <R> Functor<T, R> fmap(Function<V, R> f);
}
