package org.bruceeddy;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class LensTopLevel {
    public static <A, B, C> Function<A, Function<B, C>> curry(final BiFunction<A, B, C> f) {
        return (A a) -> (B b) -> f.apply(a, b);
    }

    public static <V, R> Lens<V, R> gen(Function<V, R> f, BiFunction<R, V, V> g) {
        class Lensy implements Lens<V, R> {
            public R get(V v) {
                return f.apply(v);
            }

            public Function<V, V> set(R i) {
                return curry(g).apply(i);
            }

            public Function<V, V> modify(Function<R, R> f) {
                return v -> set(f.apply(get(v))).apply(v);
            }

            public Function<V, List<V>> modifyF(Function<R, List<R>> f) {
                return v -> f.apply(get(v)).stream().map(r -> set(r).apply(v)).collect(toList());
            }

            public <U> Lens<U, R> compose(Lens<U, V> comp) {

                return new Composed(comp, Lensy.this);
            }
        }


        return new Lensy();
    }

    interface Lens<V, R> {

        public R get(V v);

        public Function<V, V> set(R i);

        public Function<V, V> modify(Function<R, R> f);

        public Function<V, List<V>> modifyF(Function<R, List<R>> f);

        public <U> Lens<U, R> compose(Lens<U, V> comp);
    }

    static class Composed<U, R, V> implements Lens<U, R> {

        private Lens<U, V> comp1;
        private Lens<V, R> comp2;

        public Composed(Lens<U, V> comp1, Lens<V, R> comp2) {
            this.comp1 = comp1;
            this.comp2 = comp2;
        }

        @Override
        public R get(U u) {
            return comp2.get(comp1.get(u));
        }

        @Override
        public Function<U, U> set(R i) {
            return comp1.modify(comp2.set(i));
        }

        @Override
        public Function<U, U> modify(Function<R, R> f) {
            return comp1.modify(comp2.modify(f));
        }

        @Override
        public Function<U, List<U>> modifyF(Function<R, List<R>> f) {
            return comp1.modifyF(comp2.modifyF(f));
        }

        @Override
        public <U1> Lens<U1, R> compose(Lens<U1, U> comp1) {
            return new Composed(comp1, this);
        }
    }
}
