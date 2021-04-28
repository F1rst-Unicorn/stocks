package de.njsm.stocks.android.util.idling;

import javax.inject.Inject;

public class NullIdlingResource implements IdlingResource {

    @Inject
    public NullIdlingResource() {
    }

    @Override
    public androidx.test.espresso.IdlingResource getNestedResource() {
        // Never called
        return null;
    }
}
