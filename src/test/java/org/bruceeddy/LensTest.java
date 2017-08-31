package org.bruceeddy;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LensTest {

    private Lens<Address, Integer> streetNumber;
    private Lens<Person, Address> address;
    private Address anAddress;
    private Person aPerson;
    private Lens<Person, Integer> personsStreetNumber;

    static class Address {

        final int streetNumber;
        final String streetName;

        public Address(int streetNumber, String streetName) {
            this.streetNumber = streetNumber;
            this.streetName = streetName;
        }
    }

    static class Person {
        final String name;
        final int age;
        final Address address;

        public Person(String name, int age, Address address) {
            this.name = name;
            this.age = age;
            this.address = address;
        }
    }

    public static <A, B, C> Function<A, Function<B, C>> curry(final BiFunction<A, B, C> f) {
        return (A a) -> (B b) -> f.apply(a, b);
    }

    static class Composed<U, R, V> implements Lens<U,R>  {

        private Lens<U, V> comp1;
        private Lens<V, R> comp2;

        public Composed(Lens<U, V> comp1, Lens<V,R> comp2) {
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

    public static <V,R> Lens<V, R> gen(Function<V, R> f, BiFunction<R, V, V> g) {
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

    interface Lens<V, R>  {

        public R get(V v);

        public Function<V,V> set(R i);

        public Function<V,V> modify(Function<R,R> f);

        public Function<V,List<V>> modifyF(Function<R,List<R>> f);

        public <U> Lens<U,R> compose(Lens<U, V> comp);
    }

    @Before
    public void createValues()  {
        anAddress = new Address(10, "The High Street");
        aPerson = new Person("Joyo", 99, anAddress);
    }

    @Before
    public void createLenses()  {
        streetNumber = gen(a -> a.streetNumber, (i, a) -> new Address(i,a.streetName));
        address = gen(p -> p.address, (a, p) -> new Person(p.name, p.age, a));
        personsStreetNumber = streetNumber.compose(address);
    }

    @Test
    public void lensShouldGetValue()  {
        assertThat(streetNumber.get(anAddress), is(10));
    }

    @Test
    public void lensShouldSetValue()  {
        Address updated = streetNumber.set(20).apply(anAddress);
        assertThat(updated.streetNumber, is(20));
    }

    @Test
    public void lensShouldModify()  {
        Address modified = streetNumber.modify(x -> x+1).apply(anAddress);
        assertThat(modified.streetNumber,is(11));
    }

    //use better matchers in the assertion
    @Test
    public void lensShouldModifyF_forListF()  {
        Function<Integer, List<Integer>> neigbours = n -> asList(n-1, n+1);
        List<Address> modifiedF = streetNumber.modifyF(neigbours).apply(anAddress);

        assertThat(modifiedF.get(0).streetNumber, is(9) );
        assertThat(modifiedF.get(1).streetNumber, is(11) );
    }

    @Test
    public void composedLensShouldGetValue()  {
        assertThat(personsStreetNumber.get(aPerson), is(10));
    }

    @Test
    public void composedLensShouldSetValue()  {
        Person updated = personsStreetNumber.set(20).apply(aPerson);
        assertThat(updated.address.streetNumber, is(20));
    }

    @Test
    public void composedLensShouldModify()  {
        Person modified = personsStreetNumber.modify(x -> x+1).apply(aPerson);
        assertThat(modified.address.streetNumber,is(11));
    }

    @Test
    public void composedLensShouldModifyF()  {
        Function<Integer, List<Integer>> neigbours = n -> asList(n-1, n+1);
        List<Person> modifiedF = personsStreetNumber.modifyF(neigbours).apply(aPerson);

        assertThat(modifiedF.get(0).address.streetNumber, is(9) );
        assertThat(modifiedF.get(1).address.streetNumber, is(11) );
    }

    @Test
    public void composedLensShouldCompose()  {
        class CoxedPair {
            final Person cox;
            final Person stroke;
            final Person bow;

            public CoxedPair(Person cox, Person stroke, Person bow) {
                this.cox = cox;
                this.stroke = stroke;
                this.bow = bow;
            }
        }

        Lens<CoxedPair,Person> bow = gen(c -> c.bow, (p, c) -> new CoxedPair(c.cox, c.stroke, p));
        Lens<CoxedPair, Integer> bowsStreetNumber = personsStreetNumber.compose(bow);

        assertThat(bowsStreetNumber.get(new CoxedPair(null,null,aPerson)), is(10));
    }
}

/**
 * Todo
 * finish tests for compose
 * own project
 * separate test and object
 * better matchers in modifyF test
 * better factor tests
 * PBTs (for laws, at least)
 * modifyF for other functors (Future?, Optional)
 * abstract over other functors
 *
 *
 * read about haskell lens - do the test examples in Haskell
 *
 * example in exchange code
 * boiler-plateyness of real examples?
 *
 *
 * Notes
 * -- the types wrote the code: e.g. compose.modify(F)
 */