package eu.mihosoft.vmf.tutorial01.vmfmodel;

import eu.mihosoft.vmf.core.*;

import java.util.List;


// ---------------------------------------------------------
// 1.) Basic features (setters, getter, readOnly, contianments, undo/events, cloning and iteration strategies)
// ---------------------------------------------------------
interface NamedElement {
    String getName();
}

interface MyParent extends NamedElement{
    @Contains(opposite = "parent")
    MyChild[] getChildren();

    NamedElement[] getElements();
}

interface MyChild extends NamedElement{
    @Container(opposite="children")
    MyParent getParent();
}

// ---------------------------------------------------------
// 2. InterfaceOnly, Immutables and Delegation
// ---------------------------------------------------------

@InterfaceOnly
interface WithValue {
    @GetterOnly
    Object getValue();
}

// Model entities can redefine properties with subclass
// if the parent interface is a pure interface with
// getter only (see langmodel project for a real use case)
// TODO introduce proper error messages for incorrect redefinition
interface IntegerValue extends WithValue {
    Integer getValue();
}

// Immutable have a builder and no setter methods:
// ImmutableElement.newBuilder().withId(2).build();
@Immutable
interface ImmutableElement extends WithValue {
    int getId();
}

interface MyNode {
    ImmutableElement[] getElements();

    // Delegation methods must not start with 'get' or 'is'
    @DelegateTo(className = "eu.mihosoft.vmf.tutorial01.MyNodeDelegate")
    ImmutableElement[] elementsWithIdLessThan(int value);
}


