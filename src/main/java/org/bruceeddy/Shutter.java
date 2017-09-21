package org.bruceeddy;


import java.util.Optional;
import java.util.function.Function;

public interface Shutter<V,R> {

    Optional<R> getOptional(V v);

    Function<R,V> setOptional(V xs);

    boolean nonEmpty(V xs);
}
