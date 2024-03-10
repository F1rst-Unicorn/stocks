/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.server.v2.matchers;

import de.njsm.stocks.common.api.Entity;
import de.njsm.stocks.common.api.Insertable;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class MatchesInsertable<T extends Entity<T>> extends BaseMatcher<Entity<T>> {

    private final Insertable<T> contentData;

    public MatchesInsertable(Insertable<T> contentData) {
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
