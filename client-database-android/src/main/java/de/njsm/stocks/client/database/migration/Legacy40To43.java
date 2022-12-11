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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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
        ddlPrimitives.dropView("current_scaled_amount");
        ddlPrimitives.dropView("current_scaled_unit_conversion");
        ddlPrimitives.dropView("current_scaled_ingredient_amount_and_stock");
        ddlPrimitives.dropView("recipe_stock_rating_base");
        handleLocation(ddlPrimitives);
        handleUnit(ddlPrimitives);
        handleScaledUnit(ddlPrimitives);
        handleFood(ddlPrimitives);
        handleFoodItem(ddlPrimitives);
        handleEanNumber(ddlPrimitives);
        handleUser(ddlPrimitives);
        handleUserDevice(ddlPrimitives);
        handleRecipe(ddlPrimitives);
        handleRecipeProduct(ddlPrimitives);
        handleRecipeIngredient(ddlPrimitives);

        ddlPrimitives.copyTable("updates", "updates", "id", List.of(
                        "id INTEGER not null",
                        "name TEXT not null",
                        "last_update TEXT not null"
                ), List.of(
                        "_id", "id",
                        "name", "name",
                        "last_update", "last_update"
        ));
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
        ddlPrimitives.createTable("food_to_add", "id",
                "id INTEGER not null",
                "name TEXT not null",
                "to_buy INTEGER not null",
                "expiration_offset INTEGER",
                "location_id INTEGER",
                "location_transaction_time TEXT",
                "store_unit_id INTEGER",
                "store_unit_transaction_time TEXT",
                "description TEXT not null"
        );
        ddlPrimitives.createTable("unit_to_add", "id",
                "id INTEGER not null",
                "name TEXT not null",
                "abbreviation TEXT not null"
        );
        ddlPrimitives.createTable("ean_number_to_add", "id",
                "id INTEGER not null",
                "identifies_id INTEGER not null",
                "identifies_transaction_time TEXT not null",
                "ean_number TEXT not null"
        );
        ddlPrimitives.createTable("location_to_add", "id",
                "id INTEGER not null",
                "name TEXT not null",
                "description TEXT not null"
        );
        ddlPrimitives.createTable("scaled_unit_to_add", "id",
                "id INTEGER not null",
                "scale TEXT not null",
                "unit_id INTEGER",
                "unit_transaction_time TEXT"
        );
        ddlPrimitives.createTable("recipe_ingredient_to_add", "id",
                "id INTEGER not null",
                "amount INTEGER not null",
                "unit_id INTEGER",
                "unit_transaction_time TEXT",
                "ingredient_id INTEGER",
                "ingredient_transaction_time TEXT",
                "recipe_to_add INTEGER not null"
        );
        ddlPrimitives.createTable("recipe_product_to_add", "id",
                "id INTEGER not null",
                "amount INTEGER not null",
                "unit_id INTEGER",
                "unit_transaction_time TEXT",
                "product_id INTEGER",
                "product_transaction_time TEXT",
                "recipe_to_add INTEGER not null"
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
        ddlPrimitives.createTable("location_to_delete", "id",
                "id INTEGER not null",
                "version INTEGER not null",
                "location_id INTEGER",
                "location_transaction_time TEXT"
        );
        ddlPrimitives.createTable("food_to_delete", "id",
                "id INTEGER not null",
                "version INTEGER not null",
                "food_id INTEGER",
                "food_transaction_time TEXT"
        );
        ddlPrimitives.createTable("food_item_to_delete", "id",
                "id INTEGER not null",
                "version INTEGER not null",
                "food_item_id INTEGER",
                "food_item_transaction_time TEXT"
        );
        ddlPrimitives.createTable("ean_number_to_delete", "id",
                "id INTEGER not null",
                "version INTEGER not null",
                "ean_number_id INTEGER",
                "ean_number_transaction_time TEXT"
        );
        ddlPrimitives.createTable("scaled_unit_to_delete", "id",
                "id INTEGER not null",
                "version INTEGER not null",
                "scaled_unit_id INTEGER",
                "scaled_unit_transaction_time TEXT"
        );

        ddlPrimitives.createTable("food_to_edit", "id",
                "id INTEGER not null",
                "execution_time TEXT",
                "food_id INTEGER",
                "food_transaction_time TEXT",
                "version INTEGER not null",
                "name TEXT not null",
                "to_buy INTEGER not null",
                "expiration_offset INTEGER not null",
                "location_id INTEGER",
                "location_transaction_time TEXT not null",
                "store_unit_id INTEGER not null",
                "store_unit_transaction_time TEXT not null",
                "description TEXT not null"
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
        ddlPrimitives.createTable("unit_to_edit", "id",
                "id INTEGER not null",
                "execution_time TEXT",
                "unit_transaction_time TEXT",
                "unit_id INTEGER",
                "version INTEGER not null",
                "name TEXT not null",
                "abbreviation TEXT not null"
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
        ddlPrimitives.createTable("location_to_edit", "id",
                "id INTEGER not null",
                "execution_time TEXT",
                "version INTEGER not null",
                "location_id INTEGER",
                "location_transaction_time TEXT",
                "name TEXT not null",
                "description TEXT not null"
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
        copyEntityTable(ddlPrimitives, "eannumber", "ean_number",
                List.of(
                        "number TEXT not null",
                        "identifies INTEGER not null"
                ), List.of(
                        "number",
                        "identifies"
                ));
    }

    private static void handleUserDevice(DdlPrimitives ddlPrimitives) {
        copyEntityTable(ddlPrimitives, "user_device", "user_device",
                List.of(
                        "name TEXT not null",
                        "belongs_to INTEGER not null"
                ), List.of(
                        "name",
                        "belongs_to"
                ));
    }

    private static void handleUser(DdlPrimitives ddlPrimitives) {
        copyEntityTable(ddlPrimitives, "user", "user",
                List.of(
                        "name TEXT not null"
                ), List.of(
                        "name"
                ));
    }

    private static void handleUnit(DdlPrimitives ddlPrimitives) {
        copyEntityTable(ddlPrimitives, "unit", "unit",
                List.of(
                        "name TEXT not null",
                        "abbreviation TEXT not null"
                ), List.of(
                        "name",
                        "abbreviation"
                ));
    }

    private static void handleLocation(DdlPrimitives ddlPrimitives) {
        copyEntityTable(ddlPrimitives, "location", "location",
                List.of(
                        "name TEXT not null",
                        "description TEXT not null"
                ), List.of(
                        "name",
                        "description"
                ));
    }

    private static void handleScaledUnit(DdlPrimitives ddlPrimitives) {
        copyEntityTable(ddlPrimitives, "scaled_unit", "scaled_unit",
                List.of(
                        "scale TEXT not null",
                        "unit INTEGER not null"
                ), List.of(
                        "scale",
                        "unit"
                ));
    }

    private static void handleRecipeProduct(DdlPrimitives ddlPrimitives) {
        copyEntityTable(ddlPrimitives, "recipe_product", "recipe_product",
                List.of(
                        "recipe INTEGER not null",
                        "product INTEGER not null",
                        "unit INTEGER not null",
                        "amount INTEGER not null"
                ), List.of(
                        "recipe",
                        "product",
                        "unit",
                        "amount"
                ));
    }

    private static void handleRecipeIngredient(DdlPrimitives ddlPrimitives) {
        copyEntityTable(ddlPrimitives, "recipe_ingredient", "recipe_ingredient",
                List.of(
                        "recipe INTEGER not null",
                        "ingredient INTEGER not null",
                        "unit INTEGER not null",
                        "amount INTEGER not null"
                ), List.of(
                        "recipe",
                        "ingredient",
                        "unit",
                        "amount"
                ));
    }

    private static void handleRecipe(DdlPrimitives ddlPrimitives) {
        copyEntityTable(ddlPrimitives, "recipe", "recipe",
                List.of(
                        "name TEXT not null",
                        "duration TEXT not null",
                        "instructions TEXT not null"
                ), List.of(
                        "name",
                        "duration",
                        "instructions"
                ));
    }

    private static void handleFoodItem(DdlPrimitives ddlPrimitives) {
        copyEntityTable(ddlPrimitives, "fooditem", "food_item",
                List.of(
                        "of_type INTEGER not null",
                        "stored_in INTEGER not null",
                        "buys INTEGER not null",
                        "registers INTEGER not null",
                        "unit INTEGER not null",
                        "eat_by TEXT not null"
                ), List.of(
                        "of_type",
                        "stored_in",
                        "buys",
                        "registers",
                        "unit",
                        "eat_by"
                ));
    }

    private static void handleFood(DdlPrimitives ddlPrimitives) {
        copyEntityTable(ddlPrimitives, "food", "food",
                List.of(
                        "name TEXT not null",
                        "to_buy INTEGER not null",
                        "expiration_offset INTEGER not null",
                        "location INTEGER",
                        "store_unit INTEGER not null",
                        "description TEXT not null"
                ), List.of(
                        "name",
                        "to_buy",
                        "expiration_offset",
                        "location",
                        "store_unit",
                        "description"
                ));
    }

    private static void copyEntityTable(DdlPrimitives ddlPrimitives, String oldName, String newName, List<String> columnsDdl, List<String> columns) {
        String[] allColumnDdls = Stream.concat(Arrays.stream(new String[] {
                "id INTEGER not null",
                "version INTEGER not null",
                "valid_time_start TEXT not null",
                "valid_time_end TEXT not null",
                "transaction_time_start TEXT not null",
                "transaction_time_end TEXT not null",
                "initiates INTEGER not null",

        }), columnsDdl.stream()).toArray(String[]::new);
        List<String> allCustomColumsDuplicate = new ArrayList<>();
        columns.forEach(v -> {
            allCustomColumsDuplicate.add(v);
            allCustomColumsDuplicate.add(v);
        });
        String[] allColumns = Stream.concat(Arrays.stream(new String[] {
                "_id", "id",
                "version", "version",
                "valid_time_start", "valid_time_start",
                "valid_time_end", "valid_time_end",
                "transaction_time_start", "transaction_time_start",
                "transaction_time_end", "transaction_time_end",
                "initiates", "initiates",

        }), allCustomColumsDuplicate.stream()).toArray(String[]::new);

        ddlPrimitives.dropView("current_" + oldName);
        ddlPrimitives.copyTable(oldName, newName, "id, version, transaction_time_start", allColumnDdls, allColumns);
        ddlPrimitives.createIndex(newName, newName + "_current", "1 = 1", "id", "valid_time_start", "valid_time_end");
        ddlPrimitives.createIndex(newName, newName + "_pkey", "1 = 1", "id");
        ddlPrimitives.createIndex(newName, newName + "_transaction_time_start", "1 = 1", "transaction_time_start");
        ddlPrimitives.createIndex(newName, newName + "_transaction_time_end", "1 = 1", "transaction_time_end");
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
