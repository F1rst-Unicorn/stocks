package de.njsm.stocks.android.util.idling;

import androidx.test.espresso.idling.CountingIdlingResource;

import javax.inject.Inject;

public class CounterIdlingResource implements IdlingResource {

    private final CountingIdlingResource resource;

    @Inject
    public CounterIdlingResource() {
        this.resource = new CountingIdlingResource("stocks");
    }

    public void increment() {
        resource.increment();
    }

    public void decrement() {
        resource.decrement();
    }

    @Override
    public androidx.test.espresso.IdlingResource getNestedResource() {
        return resource;
    }
}
