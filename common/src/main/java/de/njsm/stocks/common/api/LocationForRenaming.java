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

package de.njsm.stocks.common.api;

import java.util.Objects;

public class LocationForRenaming extends VersionedData implements Versionable<Location> {

    private final String newName;

    public LocationForRenaming(int id, int version, String newName) {
        super(id, version);
        this.newName = newName;
    }

    public String getNewName() {
        return newName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LocationForRenaming that = (LocationForRenaming) o;
        return getNewName().equals(that.getNewName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getNewName());
    }

    @Override
    public boolean isContainedIn(Location item) {
        return Versionable.super.isContainedIn(item) &&
                newName.equals(item.name());
    }
}
