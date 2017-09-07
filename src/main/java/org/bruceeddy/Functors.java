package org.bruceeddy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static java.util.concurrent.CompletableFuture.completedFuture;

public class Functors {
    static <T> Functor<List,T> listFunctor(List<T> l) {
        return new Functor<List, T>()  {
            @Override
            public <R> Functor<List, R> fmap(Function<T, R> f) {
                return listFunctor(l.stream().map(f).collect(Collectors.toList()));
            }
        };
    }

    static <V> List<V> functorList(Functor<List, V> functor)  {
        List<V> l = new ArrayList<>();
        functor.fmap(e -> l.add(e));
        return l;
    }

    static <T> Functor<Optional,T> optionalFunctor(Optional<T> l) {
        return new Functor<Optional, T>() {

            @Override
            public <R> Functor<Optional, R> fmap(Function<T, R> f) {
                return optionalFunctor(l.map(f));
            }
        };
    }

    static <V> Optional<V> functorOptional(Functor<Optional, V> functor)  {
        class Holder<T>  {
            T held;
        }
        Holder<V> l = new Holder<>();
        functor.fmap(e -> l.held = e);
        return ofNullable(l.held);
    }

    static <T> Functor<CompletableFuture,T> futureFunctor(CompletableFuture<T> l) {
        return new Functor<CompletableFuture, T>()  {
            @Override
            public <R> Functor<CompletableFuture, R> fmap(Function<T, R> f) {
                return futureFunctor(l.thenApply(f));
            }
        };
    }

    static <V> CompletableFuture<V> functorFuture(Functor<CompletableFuture, V> functor)  {
        class Holder<T>  {
            T held;
        }
        Holder<V> l = new Holder<>();
        functor.fmap(e -> l.held = e);
        return completedFuture(l.held);
    }

}
