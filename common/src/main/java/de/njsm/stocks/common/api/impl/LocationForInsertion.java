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

package de.njsm.stocks.common.api.impl;

import de.njsm.stocks.common.api.Insertable;
import de.njsm.stocks.common.api.Location;
import de.njsm.stocks.common.api.visitor.InsertableVisitor;

import java.util.Objects;

public class LocationForInsertion implements Insertable<Location> {

    private final String name;

    public LocationForInsertion(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationForInsertion that = (LocationForInsertion) o;
        return getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public boolean isContainedIn(Location entity) {
        return name.equals(entity.getName());
    }

    @Override
    public <I, O> O accept(InsertableVisitor<I, O> visitor, I argument) {
        return visitor.locationForInsertion(this, argument);
    }
}
