package de.njsm.stocks.server.v2.matchers;

import de.njsm.stocks.server.v2.business.data.Entity;
import de.njsm.stocks.server.v2.business.data.Insertable;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jooq.Record;

public class MatchesInsertable<T extends Entity<T>, R extends Record> extends BaseMatcher<Entity<T>> {

    private final Insertable<R, T> contentData;

    public MatchesInsertable(Insertable<R, T> contentData) {
        this.contentData = contentData;
    }

    @Override
    public boolean matches(Object item) {
        if (!(item instanceof Entity)) {
            return false;
        }

        try {
            return contentData.isContainedIn((T) item);
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(contentData);
    }
}
