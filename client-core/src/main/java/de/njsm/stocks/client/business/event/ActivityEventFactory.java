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

import de.njsm.stocks.client.business.Constants;
import de.njsm.stocks.client.business.Localiser;
import de.njsm.stocks.client.business.entities.event.*;

import javax.inject.Inject;
import java.util.List;
import java.util.function.BiFunction;

class ActivityEventFactory {

    private final Localiser localiser;

    @Inject
    ActivityEventFactory(Localiser localiser) {
        this.localiser = localiser;
    }

    ActivityEvent getLocationEventFrom(List<LocationEventFeedItem> feedItems) {
        return getEventFrom(feedItems,
                LocationCreatedEvent::create,
                LocationDeletedEvent::create,
                LocationEditedEvent::create);
    }

    public ActivityEvent getUnitEventFrom(List<UnitEventFeedItem> feedItems) {
        return getEventFrom(feedItems,
                UnitCreatedEvent::create,
                UnitDeletedEvent::create,
                UnitEditedEvent::create);
    }

    <T extends EventFeedItem> ActivityEvent getEventFrom(List<T> feedItems,
                                                         BiFunction<T, Localiser, ? extends ActivityEvent> createdFactory,
                                                         BiFunction<T, Localiser, ? extends ActivityEvent> deletedFactory,
                                                         BiFunction<List<T>, Localiser, ? extends ActivityEvent> updatedFactory) {
        if (feedItems.size() == 1) {
            T item = feedItems.get(0);
            if (item.validTimeEnd().equals(Constants.INFINITY)) {
                return createdFactory.apply(item, localiser);
            } else {
                return deletedFactory.apply(item, localiser);
            }
        } else {
            return updatedFactory.apply(feedItems, localiser);
        }
    }
}
