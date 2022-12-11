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

public class Legacy40To43 extends Migration {

    public Legacy40To43() {
        super(40, 43);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        var ddlPrimitives = new DdlPrimitives(database);
        handleFailedOperationRecordingTables(ddlPrimitives);
        handleExceptionRecordingTables(ddlPrimitives);
        handleMainEntityTables(ddlPrimitives);
        handleSearchTables(ddlPrimitives);
    }

    private static void handleMainEntityTables(DdlPrimitives ddlPrimitives) {
        handleEanNumber(ddlPrimitives);
        handleUserDevice(ddlPrimitives);
        handleUser(ddlPrimitives);
        handleScaledUnit(ddlPrimitives);
        handleRecipeProduct(ddlPrimitives);
    }

    private static void handleFailedOperationRecordingTables(DdlPrimitives ddlPrimitives) {
        ddlPrimitives.createTable("food_item_to_add", "id",
                "id INTEGER not null",
                "of_type_id INTEGER",
                "of_type_transaction_time TEXT",
                "stored_in_id INTEGER",
                "stored_in_transaction_time TEXT",
                "unit_id INTEGER",
                "unit_transaction_time TEXT",
                "eat_by TEXT not null"
        );
        ddlPrimitives.createTable("unit_to_add", "id",
                "name TEXT not null",
                "id INTEGER not null",
                "abbreviation TEXT not null"
        );
        ddlPrimitives.createTable("unit_to_delete", "id",
                "id INTEGER not null",
                "version INTEGER not null",
                "unit_id INTEGER",
                "unit_transaction_time TEXT"
        );
        ddlPrimitives.createTable("user_device_to_delete", "id",
                "id INTEGER not null",
                "version INTEGER not null",
                "user_device_id INTEGER",
                "user_device_transaction_time TEXT"
        );
        ddlPrimitives.createTable("user_to_delete", "id",
                "id INTEGER not null",
                "version INTEGER not null",
                "user_id INTEGER",
                "user_transaction_time TEXT"
        );
        ddlPrimitives.createTable("food_item_to_edit", "id",
                "food_item_id INTEGER",
                "id INTEGER not null",
                "execution_time TEXT",
                "unit_transaction_time TEXT",
                "unit_id INTEGER",
                "version INTEGER not null",
                "stored_in_transaction_time TEXT",
                "eat_by TEXT not null",
                "food_item_transaction_time TEXT",
                "stored_in_id INTEGER"
        );
        ddlPrimitives.createTable("scaled_unit_to_edit", "id",
                "id INTEGER not null",
                "execution_time TEXT",
                "unit_transaction_time TEXT",
                "unit_id INTEGER",
                "version INTEGER not null",
                "scaled_unit_id INTEGER",
                "scaled_unit_transaction_time TEXT",
                "scale TEXT not null"
        );
        ddlPrimitives.createTable("food_to_buy", "id",
                "id INTEGER not null",
                "to_buy INTEGER not null",
                "food_id INTEGER",
                "food_transaction_time TEXT",
                "execution_time TEXT",
                "version INTEGER not null"
        );
    }

    private static void handleExceptionRecordingTables(DdlPrimitives ddlPrimitives) {
        ddlPrimitives.createTable("subsystem_error", "id",
                "message TEXT not null",
                "stacktrace TEXT not null",
                "id INTEGER not null");
        ddlPrimitives.createTable("status_code_error", "id",
                "message TEXT not null",
                "stacktrace TEXT not null",
                "id INTEGER not null",
                "status_code TEXT not null");
        ddlPrimitives.createTable("error", "id",
                "id INTEGER not null",
                "action TEXT",
                "data_id INTEGER not null",
                "exception_type TEXT",
                "exception_id INTEGER not null");
    }

    private static void handleEanNumber(DdlPrimitives ddlPrimitives) {
        ddlPrimitives.createTable("ean_number", "id, version, transaction_time_start",
                "id INTEGER not null",
                "version INTEGER not null",
                "valid_time_start TEXT not null",
                "valid_time_end TEXT not null",
                "transaction_time_start TEXT not null",
                "transaction_time_end TEXT not null",
                "initiates INTEGER not null",
                "number TEXT not null",
                "identifies INTEGER not null"
        );
        ddlPrimitives.copyTableContent("eannumber", "ean_number",
                "_id", "id",
                "version", "version",
                "valid_time_start", "valid_time_start",
                "valid_time_end", "valid_time_end",
                "transaction_time_start", "transaction_time_start",
                "transaction_time_end", "transaction_time_end",
                "initiates", "initiates",
                "number", "number",
                "identifies", "identifies"
        );
        ddlPrimitives.dropView("current_eannumber");
        ddlPrimitives.dropTable("eannumber");
        ddlPrimitives.createIndex("ean_number", "ean_number_current", "1 = 1", "id", "valid_time_start", "valid_time_end");
        ddlPrimitives.createIndex("ean_number", "ean_number_pkey", "1 = 1", "id");
        ddlPrimitives.createIndex("ean_number", "ean_number_transaction_time_start", "1 = 1", "transaction_time_start");
        ddlPrimitives.createIndex("ean_number", "ean_number_transaction_time_end", "1 = 1", "transaction_time_end");
    }

    private static void handleUserDevice(DdlPrimitives ddlPrimitives) {
        ddlPrimitives.createTable("user_device_new", "id, version, transaction_time_start",
                "id INTEGER not null",
                "version INTEGER not null",
                "valid_time_start TEXT not null",
                "valid_time_end TEXT not null",
                "transaction_time_start TEXT not null",
                "transaction_time_end TEXT not null",
                "initiates INTEGER not null",
                "name TEXT not null",
                "belongs_to INTEGER not null"
        );
        ddlPrimitives.copyTableContent("user_device", "user_device_new",
                "_id", "id",
                "version", "version",
                "valid_time_start", "valid_time_start",
                "valid_time_end", "valid_time_end",
                "transaction_time_start", "transaction_time_start",
                "transaction_time_end", "transaction_time_end",
                "initiates", "initiates",
                "name", "name",
                "belongs_to", "belongs_to"
        );
        ddlPrimitives.dropView("current_user_device");
        ddlPrimitives.dropTable("user_device");
        ddlPrimitives.renameTable("user_device_new", "user_device");
        ddlPrimitives.createIndex("user_device", "user_device_current", "1 = 1", "id", "valid_time_start", "valid_time_end");
        ddlPrimitives.createIndex("user_device", "user_device_pkey", "1 = 1", "id");
        ddlPrimitives.createIndex("user_device", "user_device_transaction_time_start", "1 = 1", "transaction_time_start");
        ddlPrimitives.createIndex("user_device", "user_device_transaction_time_end", "1 = 1", "transaction_time_end");
    }

    private static void handleUser(DdlPrimitives ddlPrimitives) {
        ddlPrimitives.createTable("user_new", "id, version, transaction_time_start",
                "id INTEGER not null",
                "version INTEGER not null",
                "valid_time_start TEXT not null",
                "valid_time_end TEXT not null",
                "transaction_time_start TEXT not null",
                "transaction_time_end TEXT not null",
                "initiates INTEGER not null",
                "name TEXT not null"
        );
        ddlPrimitives.copyTableContent("user", "user_new",
                "_id", "id",
                "version", "version",
                "valid_time_start", "valid_time_start",
                "valid_time_end", "valid_time_end",
                "transaction_time_start", "transaction_time_start",
                "transaction_time_end", "transaction_time_end",
                "initiates", "initiates",
                "name", "name"
        );
        ddlPrimitives.dropView("current_user");
        ddlPrimitives.dropTable("user");
        ddlPrimitives.renameTable("user_new", "user");
        ddlPrimitives.createIndex("user", "user_current", "1 = 1", "id", "valid_time_start", "valid_time_end");
        ddlPrimitives.createIndex("user", "user_pkey", "1 = 1", "id");
        ddlPrimitives.createIndex("user", "user_transaction_time_start", "1 = 1", "transaction_time_start");
        ddlPrimitives.createIndex("user", "user_transaction_time_end", "1 = 1", "transaction_time_end");
    }

    private static void handleScaledUnit(DdlPrimitives ddlPrimitives) {
        ddlPrimitives.createTable("scaled_unit_new", "id, version, transaction_time_start",
                "id INTEGER not null",
                "version INTEGER not null",
                "valid_time_start TEXT not null",
                "valid_time_end TEXT not null",
                "transaction_time_start TEXT not null",
                "transaction_time_end TEXT not null",
                "initiates INTEGER not null",
                "scale TEXT not null",
                "unit INTEGER not null"
        );
        ddlPrimitives.copyTableContent("scaled_unit", "scaled_unit_new",
                "_id", "id",
                "version", "version",
                "valid_time_start", "valid_time_start",
                "valid_time_end", "valid_time_end",
                "transaction_time_start", "transaction_time_start",
                "transaction_time_end", "transaction_time_end",
                "initiates", "initiates",
                "scale", "scale",
                "unit", "unit"
        );
        ddlPrimitives.dropView("current_scaled_unit");
        ddlPrimitives.dropView("current_scaled_amount");
        ddlPrimitives.dropView("current_scaled_unit_conversion");
        ddlPrimitives.dropView("current_scaled_ingredient_amount_and_stock");
        ddlPrimitives.dropView("recipe_stock_rating_base");
        ddlPrimitives.dropTable("scaled_unit");
        ddlPrimitives.renameTable("scaled_unit_new", "scaled_unit");
        ddlPrimitives.createIndex("scaled_unit", "scaled_unit_current", "1 = 1", "id", "valid_time_start", "valid_time_end");
        ddlPrimitives.createIndex("scaled_unit", "scaled_unit_pkey", "1 = 1", "id");
        ddlPrimitives.createIndex("scaled_unit", "scaled_unit_transaction_time_start", "1 = 1", "transaction_time_start");
        ddlPrimitives.createIndex("scaled_unit", "scaled_unit_transaction_time_end", "1 = 1", "transaction_time_end");
    }

    private static void handleRecipeProduct(DdlPrimitives ddlPrimitives) {
        ddlPrimitives.createTable("recipe_product_new", "id, version, transaction_time_start",
                "id INTEGER not null",
                "version INTEGER not null",
                "valid_time_start TEXT not null",
                "valid_time_end TEXT not null",
                "transaction_time_start TEXT not null",
                "transaction_time_end TEXT not null",
                "initiates INTEGER not null",
                "recipe INTEGER not null",
                "product INTEGER not null",
                "unit INTEGER not null",
                "amount INTEGER not null"
        );
        ddlPrimitives.copyTableContent("recipe_product", "recipe_product_new",
                "_id", "id",
                "version", "version",
                "valid_time_start", "valid_time_start",
                "valid_time_end", "valid_time_end",
                "transaction_time_start", "transaction_time_start",
                "transaction_time_end", "transaction_time_end",
                "initiates", "initiates",
                "recipe", "recipe",
                "product", "product",
                "unit", "unit",
                "amount", "amount"
        );
        ddlPrimitives.dropView("current_recipe_product");
        ddlPrimitives.dropTable("recipe_product");
        ddlPrimitives.renameTable("recipe_product_new", "recipe_product");
        ddlPrimitives.createIndex("recipe_product", "recipe_product_current", "1 = 1", "id", "valid_time_start", "valid_time_end");
        ddlPrimitives.createIndex("recipe_product", "recipe_product_pkey", "1 = 1", "id");
        ddlPrimitives.createIndex("recipe_product", "recipe_product_transaction_time_start", "1 = 1", "transaction_time_start");
        ddlPrimitives.createIndex("recipe_product", "recipe_product_transaction_time_end", "1 = 1", "transaction_time_end");
    }

    private static void handleSearchTables(DdlPrimitives ddlPrimitives) {
        ddlPrimitives.createTable("searched_food", "food",
                "last_queried TEXT not null",
                "food INTEGER not null"
        );
        ddlPrimitives.createTable("recent_search", "term",
                "last_queried TEXT not null",
                "term TEXT not null"
        );
    }
}
