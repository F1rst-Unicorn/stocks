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

import de.njsm.stocks.server.v2.db.jooq.Tables;
import de.njsm.stocks.server.v2.db.jooq.tables.records.*;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableRecord;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static de.njsm.stocks.server.v2.db.jooq.Tables.*;
import static de.njsm.stocks.server.v2.db.jooq.tables.EanNumber.EAN_NUMBER;
import static de.njsm.stocks.server.v2.db.jooq.tables.Food.FOOD;
import static de.njsm.stocks.server.v2.db.jooq.tables.FoodItem.FOOD_ITEM;
import static de.njsm.stocks.server.v2.db.jooq.tables.GroceryChain.GROCERY_CHAIN;
import static de.njsm.stocks.server.v2.db.jooq.tables.Location.LOCATION;
import static de.njsm.stocks.server.v2.db.jooq.tables.Price.PRICE;
import static de.njsm.stocks.server.v2.db.jooq.tables.Recipe.RECIPE;
import static de.njsm.stocks.server.v2.db.jooq.tables.ScaledUnit.SCALED_UNIT;
import static de.njsm.stocks.server.v2.db.jooq.tables.Unit.UNIT;
import static de.njsm.stocks.server.v2.db.jooq.tables.User.USER;
import static de.njsm.stocks.server.v2.db.jooq.tables.UserDevice.USER_DEVICE;

interface TableDescription<R extends TableRecord<R>> {

    Table<R> table();

    TableField<R, Integer> id();

    TableField<R, Integer> version();

    TableField<R, Integer> initiates();

    TableField<R, OffsetDateTime> validTimeStart();

    TableField<R, OffsetDateTime> validTimeEnd();

    TableField<R, OffsetDateTime> transactionTimeStart();

    TableField<R, OffsetDateTime> transactionTimeEnd();

    List<TableField<R, ?>> getNontemporalFields();

    default List<TableField<R, ?>> getAllFields() {
        List<TableField<R, ?>> fieldsWithTime = new ArrayList<>(getNontemporalFields());
        fieldsWithTime.add(id());
        fieldsWithTime.add(version());
        fieldsWithTime.add(validTimeStart());
        fieldsWithTime.add(validTimeEnd());
        fieldsWithTime.add(transactionTimeStart());
        fieldsWithTime.add(transactionTimeEnd());
        fieldsWithTime.add(initiates());
        return fieldsWithTime;
    }

    class Food implements TableDescription<FoodRecord> {

        @Override
        public Table<FoodRecord> table() {
            return FOOD;
        }

        @Override
        public TableField<FoodRecord, Integer> id() {
            return FOOD.ID;
        }

        @Override
        public TableField<FoodRecord, Integer> version() {
            return FOOD.VERSION;
        }

        @Override
        public TableField<FoodRecord, Integer> initiates() {
            return FOOD.INITIATES;
        }

        @Override
        public TableField<FoodRecord, OffsetDateTime> validTimeStart() {
            return FOOD.VALID_TIME_START;
        }

        @Override
        public TableField<FoodRecord, OffsetDateTime> validTimeEnd() {
            return FOOD.VALID_TIME_END;
        }

        @Override
        public TableField<FoodRecord, OffsetDateTime> transactionTimeStart() {
            return FOOD.TRANSACTION_TIME_START;
        }

        @Override
        public TableField<FoodRecord, OffsetDateTime> transactionTimeEnd() {
            return FOOD.TRANSACTION_TIME_END;
        }

        @Override
        public List<TableField<FoodRecord, ?>> getNontemporalFields() {
            return List.of(
                    Tables.FOOD.NAME,
                    Tables.FOOD.TO_BUY,
                    Tables.FOOD.EXPIRATION_OFFSET,
                    Tables.FOOD.LOCATION,
                    Tables.FOOD.DESCRIPTION,
                    Tables.FOOD.STORE_UNIT
            );
        }
    }

    class ScaledUnit implements TableDescription<ScaledUnitRecord> {

        @Override
        public Table<ScaledUnitRecord> table() {
            return SCALED_UNIT;
        }

        @Override
        public TableField<ScaledUnitRecord, Integer> id() {
            return SCALED_UNIT.ID;
        }

        @Override
        public TableField<ScaledUnitRecord, Integer> version() {
            return SCALED_UNIT.VERSION;
        }

        @Override
        public TableField<ScaledUnitRecord, Integer> initiates() {
            return SCALED_UNIT.INITIATES;
        }

        @Override
        public TableField<ScaledUnitRecord, OffsetDateTime> validTimeStart() {
            return SCALED_UNIT.VALID_TIME_START;
        }

        @Override
        public TableField<ScaledUnitRecord, OffsetDateTime> validTimeEnd() {
            return SCALED_UNIT.VALID_TIME_END;
        }

        @Override
        public TableField<ScaledUnitRecord, OffsetDateTime> transactionTimeStart() {
            return SCALED_UNIT.TRANSACTION_TIME_START;
        }

        @Override
        public TableField<ScaledUnitRecord, OffsetDateTime> transactionTimeEnd() {
            return SCALED_UNIT.TRANSACTION_TIME_END;
        }

        @Override
        public List<TableField<ScaledUnitRecord, ?>> getNontemporalFields() {
            return List.of(
                    Tables.SCALED_UNIT.SCALE,
                    Tables.SCALED_UNIT.UNIT
            );
        }
    }

    class Location implements TableDescription<LocationRecord> {

        @Override
        public Table<LocationRecord> table() {
            return LOCATION;
        }

        @Override
        public TableField<LocationRecord, Integer> id() {
            return LOCATION.ID;
        }

        @Override
        public TableField<LocationRecord, Integer> version() {
            return LOCATION.VERSION;
        }

        @Override
        public TableField<LocationRecord, Integer> initiates() {
            return LOCATION.INITIATES;
        }

        @Override
        public TableField<LocationRecord, OffsetDateTime> validTimeStart() {
            return LOCATION.VALID_TIME_START;
        }

        @Override
        public TableField<LocationRecord, OffsetDateTime> validTimeEnd() {
            return LOCATION.VALID_TIME_END;
        }

        @Override
        public TableField<LocationRecord, OffsetDateTime> transactionTimeStart() {
            return LOCATION.TRANSACTION_TIME_START;
        }

        @Override
        public TableField<LocationRecord, OffsetDateTime> transactionTimeEnd() {
            return LOCATION.TRANSACTION_TIME_END;
        }

        @Override
        public List<TableField<LocationRecord, ?>> getNontemporalFields() {
            return List.of(
                    LOCATION.NAME,
                    LOCATION.DESCRIPTION
            );
        }
    }

    class Unit implements TableDescription<UnitRecord> {

        @Override
        public Table<UnitRecord> table() {
            return UNIT;
        }

        @Override
        public TableField<UnitRecord, Integer> id() {
            return UNIT.ID;
        }

        @Override
        public TableField<UnitRecord, Integer> version() {
            return UNIT.VERSION;
        }

        @Override
        public TableField<UnitRecord, Integer> initiates() {
            return UNIT.INITIATES;
        }

        @Override
        public TableField<UnitRecord, OffsetDateTime> validTimeStart() {
            return UNIT.VALID_TIME_START;
        }

        @Override
        public TableField<UnitRecord, OffsetDateTime> validTimeEnd() {
            return UNIT.VALID_TIME_END;
        }

        @Override
        public TableField<UnitRecord, OffsetDateTime> transactionTimeStart() {
            return UNIT.TRANSACTION_TIME_START;
        }

        @Override
        public TableField<UnitRecord, OffsetDateTime> transactionTimeEnd() {
            return UNIT.TRANSACTION_TIME_END;
        }

        @Override
        public List<TableField<UnitRecord, ?>> getNontemporalFields() {
            return List.of(
                    UNIT.NAME,
                    UNIT.ABBREVIATION
            );
        }
    }

    class GroceryStore implements TableDescription<GroceryStoreRecord> {

        @Override
        public Table<GroceryStoreRecord> table() {
            return GROCERY_STORE;
        }

        @Override
        public TableField<GroceryStoreRecord, Integer> id() {
            return GROCERY_STORE.ID;
        }

        @Override
        public TableField<GroceryStoreRecord, Integer> version() {
            return GROCERY_STORE.VERSION;
        }

        @Override
        public TableField<GroceryStoreRecord, Integer> initiates() {
            return GROCERY_STORE.INITIATES;
        }

        @Override
        public TableField<GroceryStoreRecord, OffsetDateTime> validTimeStart() {
            return GROCERY_STORE.VALID_TIME_START;
        }

        @Override
        public TableField<GroceryStoreRecord, OffsetDateTime> validTimeEnd() {
            return GROCERY_STORE.VALID_TIME_END;
        }

        @Override
        public TableField<GroceryStoreRecord, OffsetDateTime> transactionTimeStart() {
            return GROCERY_STORE.TRANSACTION_TIME_START;
        }

        @Override
        public TableField<GroceryStoreRecord, OffsetDateTime> transactionTimeEnd() {
            return GROCERY_STORE.TRANSACTION_TIME_END;
        }

        @Override
        public List<TableField<GroceryStoreRecord, ?>> getNontemporalFields() {
            return List.of(
                    GROCERY_STORE.NAME,
                    GROCERY_STORE.GROCERY_CHAIN
            );
        }
    }

    class GroceryChain implements TableDescription<GroceryChainRecord> {

        @Override
        public Table<GroceryChainRecord> table() {
            return GROCERY_CHAIN;
        }

        @Override
        public TableField<GroceryChainRecord, Integer> id() {
            return GROCERY_CHAIN.ID;
        }

        @Override
        public TableField<GroceryChainRecord, Integer> version() {
            return GROCERY_CHAIN.VERSION;
        }

        @Override
        public TableField<GroceryChainRecord, Integer> initiates() {
            return GROCERY_CHAIN.INITIATES;
        }

        @Override
        public TableField<GroceryChainRecord, OffsetDateTime> validTimeStart() {
            return GROCERY_CHAIN.VALID_TIME_START;
        }

        @Override
        public TableField<GroceryChainRecord, OffsetDateTime> validTimeEnd() {
            return GROCERY_CHAIN.VALID_TIME_END;
        }

        @Override
        public TableField<GroceryChainRecord, OffsetDateTime> transactionTimeStart() {
            return GROCERY_CHAIN.TRANSACTION_TIME_START;
        }

        @Override
        public TableField<GroceryChainRecord, OffsetDateTime> transactionTimeEnd() {
            return GROCERY_CHAIN.TRANSACTION_TIME_END;
        }

        @Override
        public List<TableField<GroceryChainRecord, ?>> getNontemporalFields() {
            return List.of(
                    GROCERY_CHAIN.NAME
            );
        }
    }

    class FoodItem implements TableDescription<FoodItemRecord> {

        @Override
        public Table<FoodItemRecord> table() {
            return FOOD_ITEM;
        }

        @Override
        public TableField<FoodItemRecord, Integer> id() {
            return FOOD_ITEM.ID;
        }

        @Override
        public TableField<FoodItemRecord, Integer> version() {
            return FOOD_ITEM.VERSION;
        }

        @Override
        public TableField<FoodItemRecord, Integer> initiates() {
            return FOOD_ITEM.INITIATES;
        }

        @Override
        public TableField<FoodItemRecord, OffsetDateTime> validTimeStart() {
            return FOOD_ITEM.VALID_TIME_START;
        }

        @Override
        public TableField<FoodItemRecord, OffsetDateTime> validTimeEnd() {
            return FOOD_ITEM.VALID_TIME_END;
        }

        @Override
        public TableField<FoodItemRecord, OffsetDateTime> transactionTimeStart() {
            return FOOD_ITEM.TRANSACTION_TIME_START;
        }

        @Override
        public TableField<FoodItemRecord, OffsetDateTime> transactionTimeEnd() {
            return FOOD_ITEM.TRANSACTION_TIME_END;
        }

        @Override
        public List<TableField<FoodItemRecord, ?>> getNontemporalFields() {
            return List.of(
                    FOOD_ITEM.EAT_BY,
                    FOOD_ITEM.OF_TYPE,
                    FOOD_ITEM.STORED_IN,
                    FOOD_ITEM.REGISTERS,
                    FOOD_ITEM.BUYS,
                    FOOD_ITEM.UNIT
            );
        }
    }

    class Recipe implements TableDescription<RecipeRecord> {

        @Override
        public Table<RecipeRecord> table() {
            return RECIPE;
        }

        @Override
        public TableField<RecipeRecord, Integer> id() {
            return RECIPE.ID;
        }

        @Override
        public TableField<RecipeRecord, Integer> version() {
            return RECIPE.VERSION;
        }

        @Override
        public TableField<RecipeRecord, Integer> initiates() {
            return RECIPE.INITIATES;
        }

        @Override
        public TableField<RecipeRecord, OffsetDateTime> validTimeStart() {
            return RECIPE.VALID_TIME_START;
        }

        @Override
        public TableField<RecipeRecord, OffsetDateTime> validTimeEnd() {
            return RECIPE.VALID_TIME_END;
        }

        @Override
        public TableField<RecipeRecord, OffsetDateTime> transactionTimeStart() {
            return RECIPE.TRANSACTION_TIME_START;
        }

        @Override
        public TableField<RecipeRecord, OffsetDateTime> transactionTimeEnd() {
            return RECIPE.TRANSACTION_TIME_END;
        }

        @Override
        public List<TableField<RecipeRecord, ?>> getNontemporalFields() {
            return List.of(
                    RECIPE.NAME,
                    RECIPE.INSTRUCTIONS,
                    RECIPE.DURATION
            );
        }
    }

    class User implements TableDescription<UserRecord> {

        @Override
        public Table<UserRecord> table() {
            return USER;
        }

        @Override
        public TableField<UserRecord, Integer> id() {
            return USER.ID;
        }

        @Override
        public TableField<UserRecord, Integer> version() {
            return USER.VERSION;
        }

        @Override
        public TableField<UserRecord, Integer> initiates() {
            return USER.INITIATES;
        }

        @Override
        public TableField<UserRecord, OffsetDateTime> validTimeStart() {
            return USER.VALID_TIME_START;
        }

        @Override
        public TableField<UserRecord, OffsetDateTime> validTimeEnd() {
            return USER.VALID_TIME_END;
        }

        @Override
        public TableField<UserRecord, OffsetDateTime> transactionTimeStart() {
            return USER.TRANSACTION_TIME_START;
        }

        @Override
        public TableField<UserRecord, OffsetDateTime> transactionTimeEnd() {
            return USER.TRANSACTION_TIME_END;
        }

        @Override
        public List<TableField<UserRecord, ?>> getNontemporalFields() {
            return List.of(
                    USER.NAME
            );
        }
    }

    class UserDevice implements TableDescription<UserDeviceRecord> {

        @Override
        public Table<UserDeviceRecord> table() {
            return USER_DEVICE;
        }

        @Override
        public TableField<UserDeviceRecord, Integer> id() {
            return USER_DEVICE.ID;
        }

        @Override
        public TableField<UserDeviceRecord, Integer> version() {
            return USER_DEVICE.VERSION;
        }

        @Override
        public TableField<UserDeviceRecord, Integer> initiates() {
            return USER_DEVICE.INITIATES;
        }

        @Override
        public TableField<UserDeviceRecord, OffsetDateTime> validTimeStart() {
            return USER_DEVICE.VALID_TIME_START;
        }

        @Override
        public TableField<UserDeviceRecord, OffsetDateTime> validTimeEnd() {
            return USER_DEVICE.VALID_TIME_END;
        }

        @Override
        public TableField<UserDeviceRecord, OffsetDateTime> transactionTimeStart() {
            return USER_DEVICE.TRANSACTION_TIME_START;
        }

        @Override
        public TableField<UserDeviceRecord, OffsetDateTime> transactionTimeEnd() {
            return USER_DEVICE.TRANSACTION_TIME_END;
        }

        @Override
        public List<TableField<UserDeviceRecord, ?>> getNontemporalFields() {
            return List.of(
                    USER_DEVICE.NAME,
                    USER_DEVICE.BELONGS_TO
            );
        }
    }

    class EanNumber implements TableDescription<EanNumberRecord> {

        @Override
        public Table<EanNumberRecord> table() {
            return EAN_NUMBER;
        }

        @Override
        public TableField<EanNumberRecord, Integer> id() {
            return EAN_NUMBER.ID;
        }

        @Override
        public TableField<EanNumberRecord, Integer> version() {
            return EAN_NUMBER.VERSION;
        }

        @Override
        public TableField<EanNumberRecord, Integer> initiates() {
            return EAN_NUMBER.INITIATES;
        }

        @Override
        public TableField<EanNumberRecord, OffsetDateTime> validTimeStart() {
            return EAN_NUMBER.VALID_TIME_START;
        }

        @Override
        public TableField<EanNumberRecord, OffsetDateTime> validTimeEnd() {
            return EAN_NUMBER.VALID_TIME_END;
        }

        @Override
        public TableField<EanNumberRecord, OffsetDateTime> transactionTimeStart() {
            return EAN_NUMBER.TRANSACTION_TIME_START;
        }

        @Override
        public TableField<EanNumberRecord, OffsetDateTime> transactionTimeEnd() {
            return EAN_NUMBER.TRANSACTION_TIME_END;
        }

        @Override
        public List<TableField<EanNumberRecord, ?>> getNontemporalFields() {
            return List.of(
                    Tables.EAN_NUMBER.NUMBER,
                    Tables.EAN_NUMBER.IDENTIFIES
            );
        }
    }

    class Price implements TableDescription<PriceRecord> {

        @Override
        public Table<PriceRecord> table() {
            return PRICE;
        }

        @Override
        public TableField<PriceRecord, Integer> id() {
            return PRICE.ID;
        }

        @Override
        public TableField<PriceRecord, Integer> version() {
            return PRICE.VERSION;
        }

        @Override
        public TableField<PriceRecord, Integer> initiates() {
            return PRICE.INITIATES;
        }

        @Override
        public TableField<PriceRecord, OffsetDateTime> validTimeStart() {
            return PRICE.VALID_TIME_START;
        }

        @Override
        public TableField<PriceRecord, OffsetDateTime> validTimeEnd() {
            return PRICE.VALID_TIME_END;
        }

        @Override
        public TableField<PriceRecord, OffsetDateTime> transactionTimeStart() {
            return PRICE.TRANSACTION_TIME_START;
        }

        @Override
        public TableField<PriceRecord, OffsetDateTime> transactionTimeEnd() {
            return PRICE.TRANSACTION_TIME_END;
        }

        @Override
        public List<TableField<PriceRecord, ?>> getNontemporalFields() {
            return List.of(
                    PRICE.PRICE_,
                    PRICE.SCALE,
                    PRICE.GROCERY_STORE,
                    PRICE.FOOD,
                    PRICE.SCALED_UNIT
            );
        }
    }

    class RecipeIngredient implements TableDescription<RecipeIngredientRecord> {

        @Override
        public Table<RecipeIngredientRecord> table() {
            return RECIPE_INGREDIENT;
        }

        @Override
        public TableField<RecipeIngredientRecord, Integer> id() {
            return RECIPE_INGREDIENT.ID;
        }

        @Override
        public TableField<RecipeIngredientRecord, Integer> version() {
            return RECIPE_INGREDIENT.VERSION;
        }

        @Override
        public TableField<RecipeIngredientRecord, Integer> initiates() {
            return RECIPE_INGREDIENT.INITIATES;
        }

        @Override
        public TableField<RecipeIngredientRecord, OffsetDateTime> validTimeStart() {
            return RECIPE_INGREDIENT.VALID_TIME_START;
        }

        @Override
        public TableField<RecipeIngredientRecord, OffsetDateTime> validTimeEnd() {
            return RECIPE_INGREDIENT.VALID_TIME_END;
        }

        @Override
        public TableField<RecipeIngredientRecord, OffsetDateTime> transactionTimeStart() {
            return RECIPE_INGREDIENT.TRANSACTION_TIME_START;
        }

        @Override
        public TableField<RecipeIngredientRecord, OffsetDateTime> transactionTimeEnd() {
            return RECIPE_INGREDIENT.TRANSACTION_TIME_END;
        }

        @Override
        public List<TableField<RecipeIngredientRecord, ?>> getNontemporalFields() {
            return List.of(
                    RECIPE_INGREDIENT.AMOUNT,
                    RECIPE_INGREDIENT.INGREDIENT,
                    RECIPE_INGREDIENT.RECIPE,
                    RECIPE_INGREDIENT.UNIT
            );
        }
    }

    class RecipeProduct implements TableDescription<RecipeProductRecord> {

        @Override
        public Table<RecipeProductRecord> table() {
            return RECIPE_PRODUCT;
        }

        @Override
        public TableField<RecipeProductRecord, Integer> id() {
            return RECIPE_PRODUCT.ID;
        }

        @Override
        public TableField<RecipeProductRecord, Integer> version() {
            return RECIPE_PRODUCT.VERSION;
        }

        @Override
        public TableField<RecipeProductRecord, Integer> initiates() {
            return RECIPE_PRODUCT.INITIATES;
        }

        @Override
        public TableField<RecipeProductRecord, OffsetDateTime> validTimeStart() {
            return RECIPE_PRODUCT.VALID_TIME_START;
        }

        @Override
        public TableField<RecipeProductRecord, OffsetDateTime> validTimeEnd() {
            return RECIPE_PRODUCT.VALID_TIME_END;
        }

        @Override
        public TableField<RecipeProductRecord, OffsetDateTime> transactionTimeStart() {
            return RECIPE_PRODUCT.TRANSACTION_TIME_START;
        }

        @Override
        public TableField<RecipeProductRecord, OffsetDateTime> transactionTimeEnd() {
            return RECIPE_PRODUCT.TRANSACTION_TIME_END;
        }

        @Override
        public List<TableField<RecipeProductRecord, ?>> getNontemporalFields() {
            return List.of(
                    RECIPE_PRODUCT.AMOUNT,
                    RECIPE_PRODUCT.PRODUCT,
                    RECIPE_PRODUCT.RECIPE,
                    RECIPE_PRODUCT.UNIT
            );
        }
    }
}
