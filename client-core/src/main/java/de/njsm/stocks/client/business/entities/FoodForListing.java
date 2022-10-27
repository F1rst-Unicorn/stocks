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

package de.njsm.stocks.client.business.entities;

import com.google.auto.value.AutoValue;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@AutoValue
public abstract class FoodForListing implements Id<Food> {

    public abstract String name();

    public abstract boolean toBuy();

    public abstract LocalDate nextEatByDate();

    public abstract List<UnitAmount> storedAmounts();

    public static FoodForListing create(int id, String name, boolean toBuy, LocalDate nextEatByDate, List<UnitAmount> storedAmounts) {
        return new AutoValue_FoodForListing(id, name, toBuy, nextEatByDate, storedAmounts);
    }

    public static FoodForListing create(FoodForListingBaseData food, List<UnitAmount> storedAmounts) {
        return new AutoValue_FoodForListing(food.id(), food.name(), food.toBuy(), food.nextEatByDate().atZone(ZoneId.systemDefault()).toLocalDate(), storedAmounts);
    }
}
