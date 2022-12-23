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
import de.njsm.stocks.client.business.entities.ScaledUnit;
import de.njsm.stocks.client.business.event.ScaledUnitEventFeedItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AutoValue
public abstract class ScaledUnitEditedEvent extends ActivityEvent {

    public abstract Id<ScaledUnit> id();

    public abstract EditedField<BigDecimal> scale();

    public abstract EditedField<String> name();

    public abstract EditedField<String> abbreviation();

    public static ScaledUnitEditedEvent create(Id<ScaledUnit> id, LocalDateTime timeOccurred, EditedField<BigDecimal> scale, EditedField<String> name, EditedField<String> abbreviation, String userName) {
        return new AutoValue_ScaledUnitEditedEvent(timeOccurred, userName, id, scale, name, abbreviation);
    }

    public static ScaledUnitEditedEvent create(List<ScaledUnitEventFeedItem> feedItems, Localiser localiser) {
        var former = feedItems.get(0);
        var current = feedItems.get(1);
        return new AutoValue_ScaledUnitEditedEvent(
                localiser.toLocalDateTime(former.transactionTimeStart()),
                current.userName(),
                former.id(),
                EditedField.create(former.scale(), current.scale()),
                EditedField.create(former.name(), current.name()),
                EditedField.create(former.abbreviation(), current.abbreviation()));
    }
}
