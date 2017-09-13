package eu.mihosoft.vmf.tutorial01;

import eu.mihosoft.vmf.runtime.core.Change;
import eu.mihosoft.vmf.runtime.core.VIterator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

// ---------------------------------------------------------
// 1.) Basic features (setters, getter, readOnly, containments, undo/events, cloning and iteration strategies)
// ---------------------------------------------------------

        // create a new parent instance
        MyParent parent = MyParent.newInstance();

        // start change recorder for undo
        parent.vmf().changes().start();

        // register change listener
        parent.vmf().changes().addListener(
                (evt)-> {
                    System.out.println("evt: " + evt.propertyName());

                    if(evt.propertyChange().isPresent()) {
                        System.out.println("  -> oldValue: " + evt.propertyChange().get().oldValue());
                        System.out.println("  -> newValue: " + evt.propertyChange().get().newValue());
                    } else if (evt.listChange().isPresent()) {
                        System.out.println("  -> " + evt.listChange().get().toStringWithDetails());
                    }
                }
        );

        // cause a change by setting the name of parent
        parent.setName("Parent 1");

        System.out.println("--------");

        // create an new child
        MyChild child1 = MyChild.newInstance();

        // add the child to the parent
        parent.getChildren().add(child1);

        System.out.println("--------");

        // the child automatically knows its parent
        System.out.println("my parent: " + child1.getParent().getName());

        System.out.println("--------");

        // cause a change by setting the name of child 1
        child1.setName("Child 1");

        System.out.println("--------");

        // get a read-only instance of parent
        // use auto-completion to check that it has no setter methods
        // lists also contain read-only instances and are unmodifiable as well
        ReadOnlyMyParent parentRo = parent.asReadOnly();

        // create a deep clone of parent
        MyParent parentClone = parent.vmf().content().deepCopy();

        // ensure that parentClone and parent are equal ...
        System.out.println("parent eq clone: " + Objects.equals(parent,parentClone));

        // ... but not identical
        System.out.println("parent != clone: " + (parent!=parentClone));

        // use automatically generated toString() method
        System.out.println(" -> parent:      " + parent);
        System.out.println(" -> parentClone: " + parentClone);

        System.out.println("--------");

        // show number of changes
        System.out.println("#changes: " + parent.vmf().changes().all().size()+"\n");

        // invert change order ...
        List<Change> changesToRevert = new ArrayList<>(parent.vmf().changes().all());
        Collections.reverse(changesToRevert);

        // ... and undo all changes
        changesToRevert.stream().forEach(c->{
            System.out.println("-------- undo change: --------");c.undo();}
        );

        // after undo we compare the clone and the empty parent (they are not equal)
        // we expect the parent to be empty (all changes are reverted)
        System.out.println("--------");
        System.out.println("parent eq clone: " + Objects.equals(parent,parentClone));
        System.out.println(" -> parent:      " + parent);
        System.out.println(" -> parentClone: " + parentClone);


        // Iteration strategies:
        NamedElement element = NamedElement.newInstance();
        element.setName("element");

        // we add the element twice to study the effect on different
        // iteration strategies
        parentClone.getElements().add(element);
        parentClone.getElements().add(element);

        // each node is visited exactly once
        System.out.println("-------- Iteration: UNIQUE_NODE --------");
        parentClone.vmf().content().stream(VIterator.IterationStrategy.UNIQUE_NODE).forEach(e->{
            NamedElement namedElement = (NamedElement) e;

            System.out.println(namedElement.getName());
        });

        // each property of each node is visited exactly once
        System.out.println("-------- Iteration: UNIQUE_PROPERTY --------");
        parentClone.vmf().content().stream(VIterator.IterationStrategy.UNIQUE_PROPERTY).forEach(e->{
            NamedElement namedElement = (NamedElement) e;

            System.out.println(namedElement.getName());
        });

        // only the containment tree is visited (pure references are completely ignored)
        System.out.println("-------- Iteration: CONTAINMENT_TREE --------");
        parentClone.vmf().content().stream(VIterator.IterationStrategy.CONTAINMENT_TREE).forEach(e->{
            NamedElement namedElement = (NamedElement) e;

            System.out.println(namedElement.getName());
        });


// ---------------------------------------------------------
// 2. InterfaceOnly, Immutables and Delegation
// ---------------------------------------------------------


        // initialize immutables
        // verify via auto-completion that immutables only provide getters
        // also interesting:
        // calling asReadOnly() is a no-op, immutables do not have ReadOnly-Sibling
        // clone() is a no-op, cloning uses shared immutable references
        ImmutableElement e = ImmutableElement.newBuilder().withId(3).build();

        MyNode myNode = MyNode.newInstance();

        for(int i = 0; i < 10;i++) {
            myNode.getElements().add(
                    ImmutableElement.newBuilder().withId(i).build());
        }

        // test delegation (we only want to loop over elements with id less than 3)
        myNode.elementsWithIdLessThan(3).forEach(
                immutableElement -> System.out.println("e: " + immutableElement.getId()));

    }
}
