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

public class FoodForSetToBuy extends VersionedData implements Versionable<Food> {

    private final boolean toBuy;

    public FoodForSetToBuy(int id, int version, boolean toBuy) {
        super(id, version);
        this.toBuy = toBuy;
    }

    public boolean isToBuy() {
        return toBuy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FoodForSetToBuy that = (FoodForSetToBuy) o;
        return isToBuy() == that.isToBuy();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isToBuy());
    }

    @Override
    public boolean isContainedIn(Food item) {
        return Versionable.super.isContainedIn(item) &&
                toBuy == item.isToBuy();
    }
}
