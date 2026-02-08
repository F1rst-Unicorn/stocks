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

import de.njsm.stocks.common.api.GroceryChain;
import de.njsm.stocks.common.api.GroceryChainForDeletion;
import de.njsm.stocks.common.api.GroceryChainForEditing;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.server.v2.db.GroceryChainHandler;
import de.njsm.stocks.server.v2.db.GroceryStoreHandler;
import de.njsm.stocks.server.v2.db.PriceHandler;
import de.njsm.stocks.server.v2.db.jooq.tables.records.GroceryChainRecord;

public class GroceryChainManager extends BusinessObject<GroceryChainRecord, GroceryChain> implements
        BusinessGettable<GroceryChainRecord, GroceryChain>,
        BusinessAddable<GroceryChainRecord, GroceryChain>,
        BusinessDeletable<GroceryChainForDeletion, GroceryChain> {

    private final GroceryChainHandler groceryChainHandler;
    private final GroceryStoreHandler groceryStoreHandler;
    private final PriceHandler priceHandler;

    public GroceryChainManager(GroceryChainHandler dbHandler, GroceryChainHandler groceryChainHandler, GroceryStoreHandler groceryStoreHandler, PriceHandler priceHandler) {
        super(dbHandler);
        this.groceryChainHandler = groceryChainHandler;
        this.groceryStoreHandler = groceryStoreHandler;
        this.priceHandler = priceHandler;
    }

    public StatusCode edit(GroceryChainForEditing item) {
        return runOperation(() -> groceryChainHandler.edit(item));
    }

    @Override
    public StatusCode delete(GroceryChainForDeletion v) {
        return runOperation(() -> priceHandler.deletePricesOfChain(v)
                .bind(() -> groceryStoreHandler.deleteStoresOfChain(v))
                .bind(() -> groceryChainHandler.delete(v)));
    }
}
