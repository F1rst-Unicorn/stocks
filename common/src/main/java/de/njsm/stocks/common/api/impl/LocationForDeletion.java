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

import de.njsm.stocks.common.api.Location;
import de.njsm.stocks.common.api.Versionable;

import java.util.Objects;

public class LocationForDeletion extends VersionedData implements Versionable<Location> {

    private final boolean cascade;

    public LocationForDeletion(int id, int version, boolean cascade) {
        super(id, version);
        this.cascade = cascade;
    }

    public LocationForDeletion(int id, int version) {
        super(id, version);
        this.cascade = false;
    }

    public boolean isCascade() {
        return cascade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocationForDeletion)) return false;
        if (!super.equals(o)) return false;
        LocationForDeletion that = (LocationForDeletion) o;
        return isCascade() == that.isCascade();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isCascade());
    }
}
