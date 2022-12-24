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
import de.njsm.stocks.client.business.entities.FoodItem;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.UnitAmount;
import de.njsm.stocks.client.business.event.FoodItemEventFeedItem;

@AutoValue
public abstract class FoodItemDeletedEvent extends ActivityEvent {

    public abstract Id<FoodItem> id();

    public abstract String foodName();

    public abstract UnitAmount unit();

    public static FoodItemDeletedEvent create(FoodItemEventFeedItem feedItem, Localiser localiser) {
        return new AutoValue_FoodItemDeletedEvent(
                localiser.toLocalDateTime(feedItem.transactionTimeStart()),
                feedItem.userName(),
                feedItem.id(),
                feedItem.foodName(),
                UnitAmount.of(feedItem.unitScale(), feedItem.abbreviation()));
    }

    @Override
    public <I, O> O accept(Visitor<I, O> visitor, I input) {
        return visitor.foodItemDeleted(this, input);
    }
}
