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
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.UnitAmount;
import de.njsm.stocks.client.business.event.FoodEventFeedItem;

import java.time.Period;
import java.util.List;
import java.util.Optional;

@AutoValue
public abstract class FoodEditedEvent extends ActivityEvent {

    public abstract Id<Food> id();

    public abstract EditedField<String> name();

    public abstract EditedField<Boolean> toBuy();

    public abstract EditedField<Period> expirationOffset();

    public abstract EditedField<UnitAmount> unit();

    public abstract EditedField<Optional<String>> locationName();

    public abstract EditedField<String> description();

    public static FoodEditedEvent create(List<FoodEventFeedItem> feedItems, Localiser localiser) {
        var former = feedItems.get(0);
        var current = feedItems.get(1);
        return new AutoValue_FoodEditedEvent(
                localiser.toLocalDateTime(former.transactionTimeStart()),
                current.userName(),
                former.id(),
                EditedField.create(former.name(), current.name()),
                EditedField.create(former.toBuy(), current.toBuy()),
                EditedField.create(former.expirationOffset(), current.expirationOffset()),
                EditedField.create(
                        UnitAmount.of(former.unitScale(), former.abbreviation()),
                        UnitAmount.of(current.unitScale(), current.abbreviation())),
                EditedField.createNullable(former.locationName(), current.locationName()),
                EditedField.create(former.description(), current.description()));
    }

    @Override
    public <I, O> O accept(Visitor<I, O> visitor, I input) {
        return visitor.foodEditedEvent(this, input);
    }
}
