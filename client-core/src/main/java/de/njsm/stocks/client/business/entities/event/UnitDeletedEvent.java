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
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.Unit;
import de.njsm.stocks.client.business.event.UnitEventFeedItem;

@AutoValue
public abstract class UnitDeletedEvent extends ActivityEvent {

    public abstract Id<Unit> id();

    public abstract String name();

    public abstract String abbreviation();

    public static UnitDeletedEvent create(UnitEventFeedItem feedItem, Localiser localiser) {
        return new AutoValue_UnitDeletedEvent(
                localiser.toLocalDateTime(feedItem.transactionTimeStart()),
                feedItem.userName(),
                feedItem.id(),
                feedItem.name(),
                feedItem.abbreviation());
    }

    @Override
    public <I, O> O accept(Visitor<I, O> visitor, I input) {
        return visitor.unitDeleted(this, input);
    }
}
