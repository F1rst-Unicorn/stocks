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

package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.common.api.GroceryStore;
import de.njsm.stocks.common.api.GroceryStoreForDeletion;
import de.njsm.stocks.common.api.GroceryStoreForEditing;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.server.v2.db.GroceryStoreHandler;
import de.njsm.stocks.server.v2.db.PriceHandler;
import de.njsm.stocks.server.v2.db.jooq.tables.records.GroceryStoreRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

@Service
@RequestScope
public class GroceryStoreManager extends BusinessObject<GroceryStoreRecord, GroceryStore> implements
        BusinessGettable<GroceryStoreRecord, GroceryStore>,
        BusinessAddable<GroceryStoreRecord, GroceryStore>,
        BusinessDeletable<GroceryStoreForDeletion, GroceryStore> {

    private final GroceryStoreHandler groceryStoreHandler;
    private final PriceHandler priceHandler;

    public GroceryStoreManager(GroceryStoreHandler dbHandler, GroceryStoreHandler groceryStoreHandler, PriceHandler priceHandler) {
        super(dbHandler);
        this.groceryStoreHandler = groceryStoreHandler;
        this.priceHandler = priceHandler;
    }

    public StatusCode edit(GroceryStoreForEditing item) {
        return runOperation(() -> groceryStoreHandler.edit(item));
    }

    @Override
    public StatusCode delete(GroceryStoreForDeletion v) {
        return runOperation(() -> priceHandler.deletePricesOfStore(v)
                .bind(() -> groceryStoreHandler.delete(v)));
    }
}
