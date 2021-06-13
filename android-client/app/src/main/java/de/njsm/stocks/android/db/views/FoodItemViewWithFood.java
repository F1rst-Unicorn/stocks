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

package de.njsm.stocks.android.db.views;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import de.njsm.stocks.android.db.entities.*;
import org.threeten.bp.Instant;

import java.util.Objects;

public class FoodItemViewWithFood extends FoodItem {

    @Embedded(prefix = Sql.FOOD_PREFIX)
    Food food;

    @Embedded(prefix = Sql.LOCATION_PREFIX)
    Location location;

    @Embedded(prefix = Sql.SCALED_UNIT_PREFIX)
    ScaledUnit scaledUnit;

    @Embedded(prefix = Sql.UNIT_PREFIX)
    Unit unitEntity;

    public FoodItemViewWithFood(int id, @NonNull Instant validTimeStart, @NonNull Instant validTimeEnd, @NonNull Instant transactionTimeStart, @NonNull Instant transactionTimeEnd, int version, int initiates, Instant eatByDate, int ofType, int storedIn, int registers, int buys, int unit, Food food, Location location, ScaledUnit scaledUnit, Unit unitEntity) {
        super(id, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, version, initiates, eatByDate, ofType, storedIn, registers, buys, unit);
        this.food = food;
        this.location = location;
        this.scaledUnit = scaledUnit;
        this.unitEntity = unitEntity;
    }

    public Food getFood() {
        return food;
    }

    public Location getLocation() {
        return location;
    }

    public ScaledUnit getScaledUnit() {
        return scaledUnit;
    }

    public Unit getUnitEntity() {
        return unitEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FoodItemViewWithFood)) return false;
        if (!super.equals(o)) return false;
        FoodItemViewWithFood that = (FoodItemViewWithFood) o;
        return getFood().equals(that.getFood()) && getLocation().equals(that.getLocation()) && getScaledUnit().equals(that.getScaledUnit()) && getUnitEntity().equals(that.getUnitEntity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getFood(), getLocation(), getScaledUnit(), getUnit());
    }
}
