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
import java.time.LocalDate;

@AutoValue
public abstract class FoodItemEditErrorDetails implements Id<FoodItem>, ErrorDetails {

    public abstract String foodName();

    public abstract LocalDate eatBy();

    public abstract int storedIn();

    public abstract int unit();

    public static FoodItemEditErrorDetails create(int id, String foodName, LocalDate eatBy, int storedIn, int unit) {
        return new AutoValue_FoodItemEditErrorDetails(id, foodName, eatBy, storedIn, unit);
    }

    @Override
    public <I, O> O accept(ErrorDetailsVisitor<I, O> visitor, I input) {
        return visitor.foodItemEditErrorDetails(this, input);
    }

    public FoodItemToEdit into() {
        return FoodItemToEdit.create(id(), eatBy(), storedIn(), unit());
    }
}
