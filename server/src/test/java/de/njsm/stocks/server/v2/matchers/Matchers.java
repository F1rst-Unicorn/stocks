/* stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.njsm.stocks.server.v2.matchers;

import de.njsm.stocks.common.api.Entity;
import de.njsm.stocks.common.api.Insertable;
import de.njsm.stocks.common.api.Versionable;
import de.njsm.stocks.server.util.Principals;
import org.hamcrest.Matcher;

public class Matchers {

    public static <T extends Entity<T>> Matcher<Entity<T>> matchesInsertable(Insertable<T> contentData) {
        return new MatchesInsertable<>(contentData);
    }

    public static <T extends Entity<T>> Matcher<Entity<T>> matchesVersionableUpdated(Versionable<T> contentData) {
        return new MatchesVersionableUpdated<>(contentData);
    }

    public static <T extends Entity<T>> Matcher<Entity<T>> matchesVersionableExactly(Versionable<T> contentData) {
        return new MatchesVersionableExactly<>(contentData);
    }

    public static <T extends Entity<T>> Matcher<? super Entity<T>> isTerminatedForInfinity() {
        return new IsTerminatedForInfinity<>();
    }

    public static <T extends Entity<T>> Matcher<Entity<T>> wasInitiatedBy(Principals principals) {
        return new WasInitiatedBy<>(principals);
    }
}
