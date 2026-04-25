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

package de.njsm.stocks.client.database.migration;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import static de.njsm.stocks.client.database.CurrentTable.NOW_AS_BEST_KNOWN;

public class Migration52To53 extends Migration {

    public Migration52To53() {
        super(52, 53);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase db) {
        var ddlPrimitives = new DdlPrimitives(db);
        ddlPrimitives.createTable("grocery_chain", "id, version, transaction_time_start",
                "id INTEGER not null",
                "version INTEGER not null",
                "valid_time_start TEXT not null",
                "valid_time_end TEXT not null",
                "transaction_time_start TEXT not null",
                "transaction_time_end TEXT not null",
                "initiates INTEGER not null",
                "name TEXT not null");
        ddlPrimitives.createTable("grocery_store", "id, version, transaction_time_start",
                "id INTEGER not null",
                "version INTEGER not null",
                "valid_time_start TEXT not null",
                "valid_time_end TEXT not null",
                "transaction_time_start TEXT not null",
                "transaction_time_end TEXT not null",
                "initiates INTEGER not null",
                "name TEXT not null",
                "grocery_chain INTEGER not null");
        ddlPrimitives.createTable("price", "id, version, transaction_time_start",
                "id INTEGER not null",
                "version INTEGER not null",
                "valid_time_start TEXT not null",
                "valid_time_end TEXT not null",
                "transaction_time_start TEXT not null",
                "transaction_time_end TEXT not null",
                "initiates INTEGER not null",
                "price TEXT not null",
                "scale TEXT not null",
                "grocery_store INTEGER not null",
                "food INTEGER not null",
                "scaled_unit INTEGER not null");

        ddlPrimitives.createIndex("grocery_chain", "grocery_chain_current", "id", "valid_time_start", "valid_time_end");
        ddlPrimitives.createIndex("grocery_chain", "grocery_chain_pkey", "id");
        ddlPrimitives.createIndex("grocery_chain", "grocery_chain_transaction_time_start", "transaction_time_start");
        ddlPrimitives.createIndex("grocery_chain", "grocery_chain_transaction_time_end", "transaction_time_end");
        ddlPrimitives.createIndex("grocery_store", "grocery_store_current", "id", "valid_time_start", "valid_time_end");
        ddlPrimitives.createIndex("grocery_store", "grocery_store_pkey", "id");
        ddlPrimitives.createIndex("grocery_store", "grocery_store_transaction_time_start", "transaction_time_start");
        ddlPrimitives.createIndex("grocery_store", "grocery_store_transaction_time_end", "transaction_time_end");
        ddlPrimitives.createIndex("price", "price_current", "id", "valid_time_start", "valid_time_end");
        ddlPrimitives.createIndex("price", "price_pkey", "id");
        ddlPrimitives.createIndex("price", "price_transaction_time_start", "transaction_time_start");
        ddlPrimitives.createIndex("price", "price_transaction_time_end", "transaction_time_end");

        ddlPrimitives.createTable("grocery_chain_to_delete", "id",
                "id INTEGER not null",
                "version INTEGER not null",
                "grocery_chain_id INTEGER ",
                "grocery_chain_transaction_time TEXT");
        ddlPrimitives.createTable("grocery_store_to_delete", "id",
                "id INTEGER not null",
                "version INTEGER not null",
                "grocery_store_id INTEGER",
                "grocery_store_transaction_time TEXT");
        ddlPrimitives.createTable("price_to_delete", "id",
                "id INTEGER not null",
                "version INTEGER not null",
                "price_id INTEGER",
                "price_transaction_time TEXT");

        ddlPrimitives.createView("current_grocery_chain", "select * from grocery_chain " + NOW_AS_BEST_KNOWN);
        ddlPrimitives.createView("current_grocery_store", "select * from grocery_store " + NOW_AS_BEST_KNOWN);
        ddlPrimitives.createView("current_price", "select * from price " + NOW_AS_BEST_KNOWN);

        ddlPrimitives.createTable("grocery_chain_to_add", "id",
                "id INTEGER not null",
                "name TEXT not null");
        ddlPrimitives.createTable("grocery_chain_to_edit", "id",
                "id INTEGER not null",
                "version INTEGER not null",
                "name TEXT not null",
                "grocery_chain_id INTEGER not null",
                "grocery_chain_transaction_time TEXT not null",
                "execution_time TEXT not null");
        ddlPrimitives.createTable("grocery_store_to_add", "id",
                "id INTEGER not null",
                "name TEXT not null",
                "grocery_chain_id INTEGER",
                "grocery_chain_transaction_time TEXT");
        ddlPrimitives.createTable("grocery_chain_to_edit", "id",
                "id INTEGER not null",
                "version INTEGER not null",
                "name TEXT not null",
                "grocery_store_id INTEGER not null",
                "grocery_store_transaction_time TEXT not null",
                "grocery_chain_id INTEGER not null",
                "grocery_chain_transaction_time TEXT not null",
                "execution_time TEXT not null");
        ddlPrimitives.createTable("price_to_add", "id",
                "id INTEGER not null",
                "price TEXT not null",
                "scale TEXT not null",
                "valid_time TEXT not null",
                "grocery_store_id INTEGER",
                "grocery_store_transaction_time TEXT",
                "food_id INTEGER",
                "food_transaction_time TEXT",
                "scaled_unit_id INTEGER",
                "scaled_unit_transaction_time TEXT");
    }
}
