package org.bruceeddy;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LensTest {

    private LensTopLevel.Lens<Address, Integer> streetNumber;
    private LensTopLevel.Lens<Person, Address> address;
    private Address anAddress;
    private Person aPerson;
    private LensTopLevel.Lens<Person, Integer> personsStreetNumber;

    @Before
    public void createValues() {
        anAddress = new Address(10, "The High Street");
        aPerson = new Person("Joyo", 99, anAddress);
    }

    @Before
    public void createLenses() {
        streetNumber = LensTopLevel.gen(a -> a.streetNumber, (i, a) -> new Address(i, a.streetName));
        address = LensTopLevel.gen(p -> p.address, (a, p) -> new Person(p.name, p.age, a));
        personsStreetNumber = streetNumber.compose(address);
    }

    @Test
    public void lensShouldGetValue() {
        assertThat(streetNumber.get(anAddress), is(10));
    }

    @Test
    public void lensShouldSetValue() {
        Address updated = streetNumber.set(20).apply(anAddress);
        assertThat(updated.streetNumber, is(20));
    }

    @Test
    public void lensShouldModify() {
        Address modified = streetNumber.modify(x -> x + 1).apply(anAddress);
        assertThat(modified.streetNumber, is(11));
    }

    //use better matchers in the assertion
    @Test
    public void lensShouldModifyF_forListF() {
        Function<Integer, List<Integer>> neigbours = n -> asList(n - 1, n + 1);
        List<Address> modifiedF = streetNumber.modifyF(neigbours).apply(anAddress);

        assertThat(modifiedF.get(0).streetNumber, is(9));
        assertThat(modifiedF.get(1).streetNumber, is(11));
    }

    @Test
    public void composedLensShouldGetValue() {
        assertThat(personsStreetNumber.get(aPerson), is(10));
    }

    @Test
    public void composedLensShouldSetValue() {
        Person updated = personsStreetNumber.set(20).apply(aPerson);
        assertThat(updated.address.streetNumber, is(20));
    }

    @Test
    public void composedLensShouldModify() {
        Person modified = personsStreetNumber.modify(x -> x + 1).apply(aPerson);
        assertThat(modified.address.streetNumber, is(11));
    }

    @Test
    public void composedLensShouldModifyF() {
        Function<Integer, List<Integer>> neigbours = n -> asList(n - 1, n + 1);
        List<Person> modifiedF = personsStreetNumber.modifyF(neigbours).apply(aPerson);

        assertThat(modifiedF.get(0).address.streetNumber, is(9));
        assertThat(modifiedF.get(1).address.streetNumber, is(11));
    }

    @Test
    public void composedLensShouldCompose() {
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

        LensTopLevel.Lens<CoxedPair, Person> bow = LensTopLevel.gen(c -> c.bow, (p, c) -> new CoxedPair(c.cox, c.stroke, p));
        LensTopLevel.Lens<CoxedPair, Integer> bowsStreetNumber = personsStreetNumber.compose(bow);

        assertThat(bowsStreetNumber.get(new CoxedPair(null, null, aPerson)), is(10));
    }

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
}

