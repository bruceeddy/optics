package org.bruceeddy;

import java.util.List;
import java.util.function.Function;

interface Bar<V,R> {
    <T> Function<V, Functor<T,V>> modifyF(Function<R,Functor<T,R>> f);
}

public class JavaDoesntWork  {

    static <V, R> void gen()  {
        class Foo implements Bar<V,R>  {

            Function<V, List<V>> here()  {
                Function<R, Functor<List, R>> h = null;
                //return modifyF(h);
                return null;
            }

            public <T> Function<V, Functor<T, V>> modifyF(Function<R, Functor<T, R>> f) {
                return null;
            }
        }
    }

}

class JavaWorks<R,V> {

    void here()  {
        Function<R, Functor<List, R>> h = null;
        modifyF(h);
    }

    <T> Function<V, Functor<T, V>> modifyF(Function<R, Functor<T, R>> f) {
        return null;
    }

}
