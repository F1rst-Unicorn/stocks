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
import de.njsm.stocks.client.business.entities.*;

import java.math.BigDecimal;
import java.time.Instant;

@AutoValue
public abstract class PriceEventFeedItem extends EventFeedItem<Price> {

    public abstract IdImpl<Food> foodId();

    public abstract String foodName();

    public abstract PricePerQuantity price();

    public abstract Instant validTime();

    public abstract String groceryStoreName();

    public static PriceEventFeedItem create(Id<Price> id, Instant validTimeEnd, Instant transactionTimeStart, String userName, IdImpl<Food> foodId, String foodName, BigDecimal price, BigDecimal scale, BigDecimal scaledUnitScale, String abbreviation, Instant validTime, String groceryStoreName) {
        return new AutoValue_PriceEventFeedItem(validTimeEnd, transactionTimeStart, userName, id.toId(), foodId, foodName, PricePerQuantity.create(price, scale, StoredAmount.create(scaledUnitScale, abbreviation)), validTime, groceryStoreName);
    }
}
