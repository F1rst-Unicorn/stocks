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

package de.njsm.stocks.client.database;

import com.google.auto.value.AutoValue;
import de.njsm.stocks.client.business.entities.FoodItem;
import de.njsm.stocks.client.business.entities.FoodItemForListing;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.StoredAmount;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;

@AutoValue
public abstract class FoodItemForListingData implements Id<FoodItem> {

    public abstract BigDecimal amount();

    public abstract String abbreviation();

    public abstract String location();

    public abstract Instant eatBy();

    public abstract String buyer();

    public abstract String registerer();

    public static FoodItemForListingData create(int id, BigDecimal amount, String abbreviation, String location, Instant eatBy, String buyer, String registerer) {
        return new AutoValue_FoodItemForListingData(id, amount, abbreviation, location, eatBy, buyer, registerer);
    }

    public FoodItemForListing map() {
        return FoodItemForListing.create(id(), StoredAmount.create(amount(), abbreviation()), location(), eatBy().atZone(ZoneId.systemDefault()).toLocalDate(), buyer(), registerer());
    }
}
