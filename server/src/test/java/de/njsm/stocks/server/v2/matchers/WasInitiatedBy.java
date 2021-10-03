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
