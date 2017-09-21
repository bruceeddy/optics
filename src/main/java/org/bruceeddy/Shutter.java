package org.bruceeddy;


import java.util.Optional;
import java.util.function.Function;

public interface Shutter<V,R> {

    Optional<R> getOptional(V v);

    Function<V,V> setOptional(R xs);

    boolean nonEmpty(V xs);

    Function<V,V> modify(Function<R,R> f);

    Function<V,Optional<V>> modifyOptional(Function<R,R> f);
}
