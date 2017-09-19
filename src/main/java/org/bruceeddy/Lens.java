package org.bruceeddy;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface Lens<V, R> {

    R get(V v);

    Function<V, V> set(R i);

    Function<V, V> modify(Function<R, R> f);

    Function<V, List<V>> modifyFList(Function<R, List<R>> f);

    Function<V, Optional<V>> modifyFOptional(Function<R, Optional<R>> f);

    Function<V, CompletableFuture<V>> modifyFFuture(Function<R, CompletableFuture<R>> f);

    <T> Function<V, Functor<T,V>> modifyF(Function<R,Functor<T,R>> f);

    <U> Lens<U, R> compose(Lens<U, V> comp);
}
