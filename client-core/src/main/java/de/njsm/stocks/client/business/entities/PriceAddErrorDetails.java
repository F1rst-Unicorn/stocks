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

import java.math.BigDecimal;
import java.time.Instant;

@AutoValue
public abstract class PriceAddErrorDetails implements ErrorDetails {

    public abstract BigDecimal price();

    public abstract BigDecimal scale();

    public abstract Instant validTime();

    public abstract Id<GroceryStore> groceryStore();

    public abstract String groceryStoreName();

    public abstract Id<Food> food();

    public abstract String foodName();

    public abstract Id<ScaledUnit> scaledUnit();

    public abstract UnitForErrorDetails unit();

    public static PriceAddErrorDetails create(BigDecimal price, BigDecimal scale, Instant validTime, IdImpl<GroceryStore> groceryStore, String groceryStoreName, IdImpl<Food> food, String foodName, IdImpl<ScaledUnit> scaledUnit, UnitForErrorDetails unit) {
        return new AutoValue_PriceAddErrorDetails(price, scale, validTime, groceryStore, groceryStoreName, food, foodName, scaledUnit, unit);
    }

    @Override
    public <I, O> O accept(ErrorDetailsVisitor<I, O> visitor, I input) {
        return visitor.priceAddErrorDetails(this, input);
    }
}
