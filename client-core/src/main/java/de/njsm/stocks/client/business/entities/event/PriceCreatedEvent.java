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

package de.njsm.stocks.client.business.entities.event;

import com.google.auto.value.AutoValue;
import de.njsm.stocks.client.business.Localiser;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.IdImpl;
import de.njsm.stocks.client.business.entities.StoredAmount;
import de.njsm.stocks.client.business.event.PriceEventFeedItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AutoValue
public abstract class PriceCreatedEvent extends ActivityEvent {

    public abstract IdImpl<Food> foodId();

    public abstract String foodName();

    public abstract BigDecimal price();

    public abstract StoredAmount quantity();

    public abstract LocalDateTime validTime();

    public abstract String groceryStoreName();

    public static PriceCreatedEvent create(PriceEventFeedItem feedItem, Localiser localiser) {
        return new AutoValue_PriceCreatedEvent(
                localiser.toLocalDateTime(feedItem.transactionTimeStart()),
                feedItem.userName(),
                feedItem.foodId(),
                feedItem.foodName(),
                feedItem.price(),
                feedItem.quantity(),
                feedItem.validTime(),
                feedItem.groceryStoreName());
    }

    @Override
    public <I, O> O accept(Visitor<I, O> visitor, I input) {
        return visitor.priceCreated(this, input);
    }
}
