package de.njsm.stocks.android.util.idling;

public interface IdlingResource {

    default void increment() {

    }

    default void decrement() {

    }

    androidx.test.espresso.IdlingResource getNestedResource();

}
