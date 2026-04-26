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
import de.njsm.stocks.client.business.entities.GroceryChain;
import de.njsm.stocks.client.business.entities.GroceryStore;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.IdImpl;

import java.time.Instant;

@AutoValue
public abstract class GroceryStoreEventFeedItem extends EventFeedItem<GroceryStore> {

    public abstract String name();

    public abstract IdImpl<GroceryChain> groceryChainId();

    public abstract String groceryChainName();

    public static GroceryStoreEventFeedItem create(int id, Instant validTimeEnd, Instant transactionTimeStart, String userName, String name, IdImpl<GroceryChain> groceryChainId, String groceryChainName) {
        return new AutoValue_GroceryStoreEventFeedItem(validTimeEnd, transactionTimeStart, userName, IdImpl.create(id), name, groceryChainId, groceryChainName);
    }

    public static GroceryStoreEventFeedItem create(Id<GroceryStore> id, Instant validTimeEnd, Instant transactionTimeStart, String userName, String name, IdImpl<GroceryChain> groceryChainId, String groceryChainName) {
        return new AutoValue_GroceryStoreEventFeedItem(validTimeEnd, transactionTimeStart, userName, id.toId(), name, groceryChainId, groceryChainName);
    }
}
