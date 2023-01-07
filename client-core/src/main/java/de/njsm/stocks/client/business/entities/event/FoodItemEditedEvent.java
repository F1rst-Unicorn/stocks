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
import de.njsm.stocks.client.business.entities.FoodItem;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.UnitAmount;
import de.njsm.stocks.client.business.event.FoodItemEventFeedItem;

import java.time.LocalDateTime;
import java.util.List;

@AutoValue
public abstract class FoodItemEditedEvent extends ActivityEvent {

    public abstract Id<FoodItem> id();

    public abstract String foodName();

    public abstract Id<Food> ofType();

    public abstract EditedField<LocalDateTime> eatBy();

    public abstract EditedField<UnitAmount> unit();

    public abstract EditedField<String> locationName();

    public abstract EditedField<String> buyerName();

    public abstract EditedField<String> registererName();

    public static FoodItemEditedEvent create(List<FoodItemEventFeedItem> feedItems, Localiser localiser) {
        var former = feedItems.get(0);
        var current = feedItems.get(1);
        return new AutoValue_FoodItemEditedEvent(
                localiser.toLocalDateTime(former.transactionTimeStart()),
                current.userName(),
                former.id(),
                former.foodName(),
                former.ofType(),
                EditedField.create(
                        localiser.toLocalDateTime(former.eatBy()),
                        localiser.toLocalDateTime(current.eatBy())
                ),
                EditedField.create(
                        UnitAmount.of(former.unitScale(), former.abbreviation()),
                        UnitAmount.of(current.unitScale(), current.abbreviation())),
                EditedField.create(former.locationName(), current.locationName()),
                EditedField.create(former.buyer(), current.buyer()),
                EditedField.create(former.registerer(), current.registerer())
        );
    }

    @Override
    public <I, O> O accept(Visitor<I, O> visitor, I input) {
        return visitor.foodItemEdited(this, input);
    }
}
