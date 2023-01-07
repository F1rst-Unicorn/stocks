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

package de.njsm.stocks.client.business.event;

import com.google.auto.value.AutoValue;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.FoodItem;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.IdImpl;

import java.math.BigDecimal;
import java.time.Instant;

@AutoValue
public abstract class FoodItemEventFeedItem extends EventFeedItem<FoodItem> {

    public abstract String foodName();

    public abstract Id<Food> ofType();

    public abstract Instant eatBy();

    public abstract BigDecimal unitScale();

    public abstract String abbreviation();

    public abstract String locationName();

    public abstract String buyer();

    public abstract String registerer();

    public static FoodItemEventFeedItem create(int id, Instant validTimeEnd, Instant transactionTimeStart, String userName, String foodName, Instant eatBy, BigDecimal unitScale, String abbreviation, String locationName, String buyer, String registerer, int ofType) {
        return new AutoValue_FoodItemEventFeedItem(validTimeEnd, transactionTimeStart, userName, IdImpl.create(id), foodName, IdImpl.create(ofType), eatBy, unitScale, abbreviation, locationName, buyer, registerer);
    }

    public static FoodItemEventFeedItem create(Id<FoodItem> id, Instant validTimeEnd, Instant transactionTimeStart, String userName, String foodName, Instant eatBy, BigDecimal unitScale, String abbreviation, String locationName, String buyer, String registerer, Id<Food> ofType) {
        return new AutoValue_FoodItemEventFeedItem(validTimeEnd, transactionTimeStart, userName, id, foodName, ofType, eatBy, unitScale, abbreviation, locationName, buyer, registerer);
    }
}
