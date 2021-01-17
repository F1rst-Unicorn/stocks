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

import java.time.Instant;
import java.util.Objects;

public class FoodItemForEditing extends VersionedData implements Versionable<FoodItem> {

    private final Instant eatBy;

    private final int storedIn;

    public FoodItemForEditing(int id, int version, Instant eatBy, int storedIn) {
        super(id, version);
        this.eatBy = eatBy;
        this.storedIn = storedIn;
    }

    public Instant getEatBy() {
        return eatBy;
    }

    public int getStoredIn() {
        return storedIn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FoodItemForEditing that = (FoodItemForEditing) o;
        return getStoredIn() == that.getStoredIn() && getEatBy().equals(that.getEatBy());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEatBy(), getStoredIn());
    }
}
