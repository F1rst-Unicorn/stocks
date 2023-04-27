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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@AutoValue
public abstract class FoodDetails implements Id<Food> {

    public abstract String name();

    public abstract Period expirationOffset();

    public abstract Optional<String> locationName();

    public abstract ScaledUnitForSelection storeUnit();

    public abstract String description();

    public List<UnitAmount> displayedAmount() {
        if (storedFoodAmounts().isEmpty()) {
            return List.of(NoStoredAmount.create(storeUnit().abbreviation()));
        } else {
            return storedFoodAmounts();
        }
    }

    public abstract List<UnitAmount> storedFoodAmounts();

    public abstract List<PlotByUnit<LocalDateTime>> amountOverTime();

    public abstract List<PlotPoint<Integer>> eatenAmountByExpiration();

    public static FoodDetails create(Id<Food> id, String name, Period expirationOffset, Optional<String> locationName, ScaledUnitForSelection storeUnit, String description, List<UnitAmount> storedAmounts, List<PlotByUnit<LocalDateTime>> amountsOverTime, List<PlotPoint<Integer>> expirationHistogram) {
        return new AutoValue_FoodDetails(id.id(), name, expirationOffset, locationName, storeUnit, description, storedAmounts, amountsOverTime, expirationHistogram);
    }
}
