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

import de.njsm.stocks.common.api.BitemporalGroceryChain;
import de.njsm.stocks.common.api.GroceryChain;
import de.njsm.stocks.server.v2.db.jooq.tables.records.GroceryChainRecord;
import org.jooq.Field;
import org.jooq.RecordMapper;
import org.jooq.Table;
import org.jooq.TableField;

import java.util.Arrays;
import java.util.List;

import static de.njsm.stocks.server.v2.db.jooq.tables.GroceryChain.GROCERY_CHAIN;

public class GroceryChainHandler extends CrudDatabaseHandler<GroceryChainRecord, GroceryChain> {


    public GroceryChainHandler(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    @Override
    protected Table<GroceryChainRecord> getTable() {
        return GROCERY_CHAIN;
    }

    @Override
    protected RecordMapper<GroceryChainRecord, GroceryChain> getDtoMap() {
        return cursor -> BitemporalGroceryChain.builder()
                .id(cursor.getId())
                .version(cursor.getVersion())
                .validTimeStart(cursor.getValidTimeStart().toInstant())
                .validTimeEnd(cursor.getValidTimeEnd().toInstant())
                .transactionTimeStart(cursor.getTransactionTimeStart().toInstant())
                .transactionTimeEnd(cursor.getTransactionTimeEnd().toInstant())
                .initiates(cursor.getInitiates())
                .name(cursor.getName())
                .build();
    }

    @Override
    protected TableField<GroceryChainRecord, Integer> getIdField() {
        return GROCERY_CHAIN.ID;
    }

    @Override
    protected TableField<GroceryChainRecord, Integer> getVersionField() {
        return GROCERY_CHAIN.VERSION;
    }

    @Override
    protected List<Field<?>> getNontemporalFields() {
        return Arrays.asList(
                GROCERY_CHAIN.ID,
                GROCERY_CHAIN.VERSION,
                GROCERY_CHAIN.NAME
        );
    }
}
