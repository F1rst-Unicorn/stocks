package de.njsm.stocks.server.v2.matchers;

import de.njsm.stocks.server.v2.business.data.Entity;
import de.njsm.stocks.server.v2.business.data.Insertable;
import de.njsm.stocks.server.v2.business.data.Versionable;
import org.hamcrest.Matcher;
import org.jooq.Record;

public class Matchers {

    public static <T extends Entity<T>, R extends Record> Matcher<Entity<T>> matchesInsertable(Insertable<R, T> contentData) {
        return new MatchesInsertable<>(contentData);
    }

    public static <T extends Entity<T>> Matcher<Entity<T>> matchesVersionable(Versionable<T> contentData) {
        return new MatchesVersionable<>(contentData);
    }
}
