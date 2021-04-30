package de.njsm.stocks.server.v2.matchers;

import de.njsm.stocks.server.v2.business.data.Entity;
import de.njsm.stocks.server.v2.business.data.Versionable;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class MatchesVersionable<T extends Entity<T>> extends BaseMatcher<Entity<T>> {

    private final Versionable<T> contentData;

    public MatchesVersionable(Versionable<T> contentData) {
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
