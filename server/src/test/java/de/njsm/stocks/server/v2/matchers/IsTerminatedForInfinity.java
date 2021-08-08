package de.njsm.stocks.server.v2.matchers;

import de.njsm.stocks.common.api.Bitemporal;
import de.njsm.stocks.common.api.Entity;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import static de.njsm.stocks.server.v2.db.CrudDatabaseHandler.INFINITY;

public class IsTerminatedForInfinity<T extends Entity<T>> extends BaseMatcher<Entity<T>> {

    @Override
    public boolean matches(Object item) {
        if (!(item instanceof Bitemporal)) {
            return false;
        }

        Bitemporal<T> entity;
        try {
            entity = (Bitemporal<T>) item;
        } catch (ClassCastException e) {
            return false;
        }

        return !entity.validTimeEnd().equals(INFINITY.toInstant()) &&
                entity.transactionTimeEnd().equals(INFINITY.toInstant());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("is not yet terminated");
    }
}
