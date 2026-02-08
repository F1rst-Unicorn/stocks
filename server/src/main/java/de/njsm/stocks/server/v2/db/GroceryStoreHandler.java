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

package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.common.api.BitemporalGroceryStore;
import de.njsm.stocks.common.api.GroceryStore;
import de.njsm.stocks.common.api.User;
import de.njsm.stocks.server.v2.db.jooq.tables.records.GroceryStoreRecord;
import org.jooq.Field;
import org.jooq.RecordMapper;
import org.jooq.Table;
import org.jooq.TableField;

import java.util.Arrays;
import java.util.List;

import static de.njsm.stocks.server.v2.db.jooq.tables.GroceryStore.GROCERY_STORE;
import static de.njsm.stocks.server.v2.db.jooq.tables.User.USER;

public class GroceryStoreHandler extends CrudDatabaseHandler<GroceryStoreRecord, GroceryStore> {


    public GroceryStoreHandler(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    @Override
    protected Table<GroceryStoreRecord> getTable() {
        return GROCERY_STORE;
    }

    @Override
    protected RecordMapper<GroceryStoreRecord, GroceryStore> getDtoMap() {
        return cursor -> BitemporalGroceryStore.builder()
                .id(cursor.getId())
                .version(cursor.getVersion())
                .validTimeStart(cursor.getValidTimeStart().toInstant())
                .validTimeEnd(cursor.getValidTimeEnd().toInstant())
                .transactionTimeStart(cursor.getTransactionTimeStart().toInstant())
                .transactionTimeEnd(cursor.getTransactionTimeEnd().toInstant())
                .initiates(cursor.getInitiates())
                .name(cursor.getName())
                .groceryChain(cursor.getGroceryChain())
                .build();
    }

    @Override
    protected TableField<GroceryStoreRecord, Integer> getIdField() {
        return GROCERY_STORE.ID;
    }

    @Override
    protected TableField<GroceryStoreRecord, Integer> getVersionField() {
        return GROCERY_STORE.VERSION;
    }

    @Override
    protected List<Field<?>> getNontemporalFields() {
        return Arrays.asList(
                GROCERY_STORE.ID,
                GROCERY_STORE.VERSION,
                GROCERY_STORE.NAME,
                GROCERY_STORE.GROCERY_CHAIN
        );
    }
}
