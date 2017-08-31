package org.bruceeddy;

import java.util.List;
import java.util.function.Function;

interface Lens<V, R> {

    R get(V v);

    Function<V, V> set(R i);

    Function<V, V> modify(Function<R, R> f);

    Function<V, List<V>> modifyF(Function<R, List<R>> f);

    <U> Lens<U, R> compose(Lens<U, V> comp);
}
