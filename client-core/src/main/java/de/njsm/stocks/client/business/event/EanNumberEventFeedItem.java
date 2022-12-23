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
import de.njsm.stocks.client.business.entities.EanNumber;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.IdImpl;

import java.time.Instant;

@AutoValue
public abstract class EanNumberEventFeedItem extends EventFeedItem<EanNumber> {

    public abstract String foodName();

    public abstract String eanNumber();

    public static EanNumberEventFeedItem create(int id, Instant validTimeEnd, Instant transactionTimeStart, String userName, String foodName, String eanNumber) {
        return new AutoValue_EanNumberEventFeedItem(validTimeEnd, transactionTimeStart, userName, IdImpl.create(id), foodName, eanNumber);
    }

    public static EanNumberEventFeedItem create(Id<EanNumber> id, Instant validTimeEnd, Instant transactionTimeStart, String userName, String foodName, String eanNumber) {
        return new AutoValue_EanNumberEventFeedItem(validTimeEnd, transactionTimeStart, userName, id, foodName, eanNumber);
    }
}
