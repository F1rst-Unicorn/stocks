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
import de.njsm.stocks.client.business.entities.EanNumber;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.event.EanNumberEventFeedItem;

@AutoValue
public abstract class EanNumberDeletedEvent extends ActivityEvent {

    public abstract Id<EanNumber> id();

    public abstract String foodName();

    public abstract String eanNumber();

    public abstract Id<Food> identifies();

    public static EanNumberDeletedEvent create(EanNumberEventFeedItem feedItem, Localiser localiser) {
        return new AutoValue_EanNumberDeletedEvent(
                localiser.toLocalDateTime(feedItem.transactionTimeStart()),
                feedItem.userName(),
                feedItem.id(),
                feedItem.foodName(),
                feedItem.eanNumber(),
                feedItem.identifies());
    }

    @Override
    public <I, O> O accept(Visitor<I, O> visitor, I input) {
        return visitor.eanNumberDeleted(this, input);
    }
}
