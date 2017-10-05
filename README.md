Unruly Optics
======

Unruly Optics is a Lens, and other optics, library for Java 8.

What are optics?
---------------

A lens is a "composable getter and setter for immutable data structures".  Shutters, prisms, traversals and one or two
others are variations or generalisations of this basic idea.
 
Whose idea is this?
-------------------

Unruly Optics is heavily influenced by [Monocle](https://github.com/julien-truffaut/Monocle) (Julien Truffaut, Scala),
which in turn owes a lot to [Control.Lens](https://hackage.haskell.org/package/lens-4.15.4/docs/Control-Lens.html) (Edward Kmett, Haskell).  It's a 
widely used pattern for handling immutable data.

How do I include  it in my project?
----------------

It's not yet in Maven central.  Until it is, clone this repo, the run 
```mvn clean install```.  You can then include it as a maven dependency with a 
```$xslt
      <dependency>
            <groupId>org.bruceeddy</groupId>
            <artifactId>optics</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
```

How do I use it?
----------------

Start by looking at the API provided by Lens and Shutter.  Then, 
best place to start is the LensTest and ShutterTest, which have examples
of each method of Lens and Shutter being used.

Why is it Unruly?
----------------

I work at [Unruly](https://medium.com/unruly-engineering).
  I did some of the work for this during my 20% time there,
   and I'm testing the ideas in the library on my colleagues.

I see no mention of a *Shutter* in Monocle or Control.Lens
--------------------

It's called Optional in Monocle and Control.Lens.  I've named it Shutter
 in Unruly.Optics, because Optional is already taken in Java 8.
 
 



