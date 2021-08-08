package de.njsm.stocks.server.v2.matchers;

import de.njsm.stocks.common.api.Bitemporal;
import de.njsm.stocks.common.api.Entity;
import de.njsm.stocks.server.util.Principals;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class WasInitiatedBy<T extends Entity<T>> extends TypeSafeDiagnosingMatcher<Entity<T>> {

    private final Principals expectedPrincipals;

    public WasInitiatedBy(Principals expectedPrincipals) {
        this.expectedPrincipals = expectedPrincipals;
    }

    @Override
    protected boolean matchesSafely(Entity<T> item, Description description) {
        Bitemporal<T> entity;
        try {
            entity = (Bitemporal<T>) item;
        } catch (ClassCastException e) {
            description.appendText("was not bitemporal");
            return false;
        }

        boolean result = entity.initiates() == expectedPrincipals.getDid();
        if (!result)
            description
                    .appendText("was initiated by ")
                    .appendValue(entity.initiates());
        return result;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("was not intiated by " + expectedPrincipals.getDid());
    }
}
