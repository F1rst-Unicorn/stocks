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
import de.njsm.stocks.client.business.entities.Location;
import de.njsm.stocks.client.business.event.LocationEventFeedItem;

import java.time.LocalDateTime;
import java.util.List;

@AutoValue
public abstract class LocationEditedEvent extends ActivityEvent {

    public abstract Id<Location> id();

    public abstract EditedField<String> name();

    public abstract EditedField<String> description();

    public static LocationEditedEvent create(Id<Location> id, LocalDateTime timeOccurred, String userName, EditedField<String> name, EditedField<String> description) {
        return new AutoValue_LocationEditedEvent(timeOccurred, userName, id, name, description);
    }

    public static LocationEditedEvent create(List<LocationEventFeedItem> feedItems, Localiser localiser) {
        LocationEventFeedItem former = feedItems.get(0);
        LocationEventFeedItem current = feedItems.get(1);
        return new AutoValue_LocationEditedEvent(
                localiser.toLocalDateTime(former.transactionTimeStart()),
                current.userName(),
                former.id(),
                EditedField.create(former.name(), current.name()),
                EditedField.create(former.description(), current.description())
        );
    }
}
