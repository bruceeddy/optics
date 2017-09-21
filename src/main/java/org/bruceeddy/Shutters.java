package org.bruceeddy;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.bruceeddy.Lenses.curry;

public class Shutters {
    public static <V, R> Shutter<V, R> gen(Function<V, Optional<R>> getter, BiFunction<R, V, V> setter) {
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
