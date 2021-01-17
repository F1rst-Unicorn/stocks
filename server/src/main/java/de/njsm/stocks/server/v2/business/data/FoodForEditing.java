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

package de.njsm.stocks.server.v2.business.data;

import java.time.Period;
import java.util.Objects;

public class FoodForEditing extends VersionedData implements Versionable<Food> {

    private final String newName;

    private final Period expirationOffset;

    private final int location;

    public FoodForEditing(int id, int version, String newName, Period expirationOffset, int location) {
        super(id, version);
        this.newName = newName;
        this.expirationOffset = expirationOffset;
        this.location = location;
    }

    public String getNewName() {
        return newName;
    }

    public Period getExpirationOffset() {
        return expirationOffset;
    }

    public int getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FoodForEditing that = (FoodForEditing) o;
        return getExpirationOffset() == that.getExpirationOffset() && getLocation() == that.getLocation() && getNewName().equals(that.getNewName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getNewName(), getExpirationOffset(), getLocation());
    }
}
