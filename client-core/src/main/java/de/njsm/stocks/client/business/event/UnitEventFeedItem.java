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
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.IdImpl;
import de.njsm.stocks.client.business.entities.Unit;

import java.time.Instant;

@AutoValue
public abstract class UnitEventFeedItem extends EventFeedItem {

    public abstract Id<Unit> id();

    public abstract String name();

    public abstract String abbreviation();

    public static UnitEventFeedItem create(int id, Instant validTimeEnd, Instant transactionTimeStart, String userName, String name, String abbreviation) {
        return new AutoValue_UnitEventFeedItem(validTimeEnd, transactionTimeStart, userName, IdImpl.create(id), name, abbreviation);
    }

    public static UnitEventFeedItem create(Id<Unit> id, Instant validTimeEnd, Instant transactionTimeStart, String userName, String name, String abbreviation) {
        return new AutoValue_UnitEventFeedItem(validTimeEnd, transactionTimeStart, userName, id, name, abbreviation);
    }
}
