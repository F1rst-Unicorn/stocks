/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General License for more details.
 *
 * You should have received a copy of the GNU General License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.njsm.stocks.client.database.error;

import androidx.room.Dao;
import androidx.room.Query;
import de.njsm.stocks.client.database.*;

import java.time.Instant;

import static de.njsm.stocks.client.database.StocksDatabase.DATABASE_INFINITY_STRING_SQL;
import static de.njsm.stocks.client.database.StocksDatabase.NOW;

@Dao
public abstract class BitemporalSearchDao {

    LocationDbEntity getLocationByValidOrTransactionTime(PreservedId id) {
        LocationDbEntity location = getCurrentLocation(id.id());
        if (location == null) {
            location = getLatestLocationAsBestKnown(id.id());
        }
        if (location == null) {
            location = getCurrentLocationAsKnownAt(id.id(), id.transactionTime());
        }
        return location;
    }

    @Query("select * " +
            "from current_location " +
            "where id = :locationId")
    abstract LocationDbEntity getCurrentLocation(int locationId);

    @Query("select * " +
            "from location " +
            "where id = :id " +
            "and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from location " +
            "   where id = :id " +
            "   and valid_time_start <= " + NOW +
            "   and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            ")")
    abstract LocationDbEntity getLatestLocationAsBestKnown(int id);

    @Query("select * " +
            "from location " +
            "where id = :id " +
            "and transaction_time_start <= :transactionTime " +
            "and :transactionTime < transaction_time_end " +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from location " +
            "   where id = :id " +
            "   and valid_time_start <= " + NOW +
            "   and transaction_time_start <= :transactionTime " +
            "   and :transactionTime < transaction_time_end " +
            ")")
    abstract LocationDbEntity getCurrentLocationAsKnownAt(int id, Instant transactionTime);

    UnitDbEntity getUnitByValidOrTransactionTime(PreservedId id) {
        UnitDbEntity unit = getCurrentUnit(id.id());
        if (unit == null) {
            unit = getLatestUnitAsBestKnown(id.id());
        }
        if (unit == null) {
            unit = getCurrentUnitAsKnownAt(id.id(), id.transactionTime());
        }
        return unit;
    }

    @Query("select * " +
            "from current_unit " +
            "where id = :unitId")
    abstract UnitDbEntity getCurrentUnit(int unitId);

    @Query("select * " +
            "from unit " +
            "where id = :id " +
            "and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from unit " +
            "   where id = :id" +
            "   and valid_time_start <= " + NOW +
            "   and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            ")")
    abstract UnitDbEntity getLatestUnitAsBestKnown(int id);

    @Query("select * " +
            "from unit " +
            "where id = :id " +
            "and transaction_time_start <= :transactionTime " +
            "and :transactionTime < transaction_time_end " +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from unit " +
            "   where id = :id" +
            "   and valid_time_start <= " + NOW +
            "   and transaction_time_start <= :transactionTime " +
            "   and :transactionTime < transaction_time_end " +
            ")")
    abstract UnitDbEntity getCurrentUnitAsKnownAt(int id, Instant transactionTime);

    ScaledUnitDbEntity getScaledUnitByValidOrTransactionTime(PreservedId id) {
        ScaledUnitDbEntity unit = getCurrentScaledUnit(id.id());
        if (unit == null) {
            unit = getLatestScaledUnitAsBestKnown(id.id());
        }
        if (unit == null) {
            unit = getCurrentScaledUnitAsKnownAt(id.id(), id.transactionTime());
        }
        return unit;
    }

    @Query("select * " +
            "from current_scaled_unit " +
            "where id = :unitId")
    abstract ScaledUnitDbEntity getCurrentScaledUnit(int unitId);

    @Query("select * " +
            "from scaled_unit " +
            "where id = :id " +
            "and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from scaled_unit " +
            "   where id = :id" +
            "   and valid_time_start <= " + NOW +
            "   and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            ")")
    abstract ScaledUnitDbEntity getLatestScaledUnitAsBestKnown(int id);

    @Query("select * " +
            "from scaled_unit " +
            "where id = :id " +
            "and transaction_time_start <= :transactionTime " +
            "and :transactionTime < transaction_time_end " +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from scaled_unit " +
            "   where id = :id" +
            "   and valid_time_start <= " + NOW +
            "   and transaction_time_start <= :transactionTime " +
            "   and :transactionTime < transaction_time_end " +
            ")")
    abstract ScaledUnitDbEntity getCurrentScaledUnitAsKnownAt(int id, Instant transactionTime);

    FoodDbEntity getFoodByValidOrTransactionTime(PreservedId id) {
        FoodDbEntity food = getCurrentFood(id.id());
        if (food == null) {
            food = getLatestFoodAsBestKnown(id.id());
        }
        if (food == null) {
            food = getCurrentFoodAsKnownAt(id.id(), id.transactionTime());
        }
        return food;
    }

    @Query("select * " +
            "from current_food " +
            "where id = :id")
    abstract FoodDbEntity getCurrentFood(int id);

    @Query("select * " +
            "from food " +
            "where id = :id " +
            "and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from food " +
            "   where id = :id" +
            "   and valid_time_start <= " + NOW +
            "   and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            ")")
    abstract FoodDbEntity getLatestFoodAsBestKnown(int id);

    @Query("select * " +
            "from food " +
            "where id = :id " +
            "and transaction_time_start <= :transactionTime " +
            "and :transactionTime < transaction_time_end " +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from food " +
            "   where id = :id" +
            "   and valid_time_start <= " + NOW +
            "   and transaction_time_start <= :transactionTime " +
            "   and :transactionTime < transaction_time_end " +
            ")")
    abstract FoodDbEntity getCurrentFoodAsKnownAt(int id, Instant transactionTime);

    FoodItemDbEntity getFoodItemByValidOrTransactionTime(PreservedId id) {
        FoodItemDbEntity foodItem = getCurrentFoodItem(id.id());
        if (foodItem == null) {
            foodItem = getLatestFoodItemAsBestKnown(id.id());
        }
        if (foodItem == null) {
            foodItem = getCurrentFoodItemAsKnownAt(id.id(), id.transactionTime());
        }
        return foodItem;
    }

    @Query("select * " +
            "from current_food_item " +
            "where id = :id")
    abstract FoodItemDbEntity getCurrentFoodItem(int id);

    @Query("select * " +
            "from food_item " +
            "where id = :id " +
            "and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from food_item " +
            "   where id = :id" +
            "   and valid_time_start <= " + NOW +
            "   and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            ")")
    abstract FoodItemDbEntity getLatestFoodItemAsBestKnown(int id);

    @Query("select * " +
            "from food_item " +
            "where id = :id " +
            "and transaction_time_start <= :transactionTime " +
            "and :transactionTime < transaction_time_end " +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from food_item " +
            "   where id = :id" +
            "   and valid_time_start <= " + NOW +
            "   and transaction_time_start <= :transactionTime " +
            "   and :transactionTime < transaction_time_end " +
            ")")
    abstract FoodItemDbEntity getCurrentFoodItemAsKnownAt(int id, Instant transactionTime);

    EanNumberDbEntity getEanNumberByValidOrTransactionTime(PreservedId id) {
        EanNumberDbEntity eanNumber = getCurrentEanNumber(id.id());
        if (eanNumber == null) {
            eanNumber = getLatestEanNumberAsBestKnown(id.id());
        }
        if (eanNumber == null) {
            eanNumber = getCurrentEanNumberAsKnownAt(id.id(), id.transactionTime());
        }
        return eanNumber;
    }

    @Query("select * " +
            "from current_ean_number " +
            "where id = :id")
    abstract EanNumberDbEntity getCurrentEanNumber(int id);

    @Query("select * " +
            "from ean_number " +
            "where id = :id " +
            "and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from ean_number " +
            "   where id = :id" +
            "   and valid_time_start <= " + NOW +
            "   and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            ")")
    abstract EanNumberDbEntity getLatestEanNumberAsBestKnown(int id);

    @Query("select * " +
            "from ean_number " +
            "where id = :id " +
            "and transaction_time_start <= :transactionTime " +
            "and :transactionTime < transaction_time_end " +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from ean_number " +
            "   where id = :id" +
            "   and valid_time_start <= " + NOW +
            "   and transaction_time_start <= :transactionTime " +
            "   and :transactionTime < transaction_time_end " +
            ")")
    abstract EanNumberDbEntity getCurrentEanNumberAsKnownAt(int id, Instant transactionTime);

    UserDeviceDbEntity getUserDeviceByValidOrTransactionTime(PreservedId id) {
        UserDeviceDbEntity userDevice = getCurrentUserDevice(id.id());
        if (userDevice == null) {
            userDevice = getLatestUserDeviceAsBestKnown(id.id());
        }
        if (userDevice == null) {
            userDevice = getCurrentUserDeviceAsKnownAt(id.id(), id.transactionTime());
        }
        return userDevice;
    }

    @Query("select * " +
            "from current_user_device " +
            "where id = :id")
    abstract UserDeviceDbEntity getCurrentUserDevice(int id);

    @Query("select * " +
            "from user_device " +
            "where id = :id " +
            "and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from user_device " +
            "   where id = :id" +
            "   and valid_time_start <= " + NOW +
            "   and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            ")")
    abstract UserDeviceDbEntity getLatestUserDeviceAsBestKnown(int id);

    @Query("select * " +
            "from user_device " +
            "where id = :id " +
            "and transaction_time_start <= :transactionTime " +
            "and :transactionTime < transaction_time_end " +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from user_device " +
            "   where id = :id" +
            "   and valid_time_start <= " + NOW +
            "   and transaction_time_start <= :transactionTime " +
            "   and :transactionTime < transaction_time_end " +
            ")")
    abstract UserDeviceDbEntity getCurrentUserDeviceAsKnownAt(int id, Instant transactionTime);

    UserDbEntity getUserByValidOrTransactionTime(PreservedId id) {
        UserDbEntity user = getCurrentUser(id.id());
        if (user == null) {
            user = getLatestUserAsBestKnown(id.id());
        }
        if (user == null) {
            user = getCurrentUserAsKnownAt(id.id(), id.transactionTime());
        }
        return user;
    }

    @Query("select * " +
            "from current_user " +
            "where id = :id")
    abstract UserDbEntity getCurrentUser(int id);

    @Query("select * " +
            "from user " +
            "where id = :id " +
            "and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from user " +
            "   where id = :id" +
            "   and valid_time_start <= " + NOW +
            "   and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            ")")
    abstract UserDbEntity getLatestUserAsBestKnown(int id);

    @Query("select * " +
            "from user " +
            "where id = :id " +
            "and transaction_time_start <= :transactionTime " +
            "and :transactionTime < transaction_time_end " +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from user " +
            "   where id = :id" +
            "   and valid_time_start <= " + NOW +
            "   and transaction_time_start <= :transactionTime " +
            "   and :transactionTime < transaction_time_end " +
            ")")
    abstract UserDbEntity getCurrentUserAsKnownAt(int id, Instant transactionTime);

    public RecipeDbEntity getRecipeByValidOrTransactionTime(PreservedId id) {
        RecipeDbEntity recipe = getCurrentRecipe(id.id());
        if (recipe == null) {
            recipe = getLatestRecipeAsBestKnown(id.id());
        }
        if (recipe == null) {
            recipe = getCurrentRecipeAsKnownAt(id.id(), id.transactionTime());
        }
        return recipe;
    }

    @Query("select * " +
            "from current_recipe " +
            "where id = :id")
    abstract RecipeDbEntity getCurrentRecipe(int id);

    @Query("select * " +
            "from recipe " +
            "where id = :id " +
            "and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from recipe " +
            "   where id = :id" +
            "   and valid_time_start <= " + NOW +
            "   and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            ")")
    abstract RecipeDbEntity getLatestRecipeAsBestKnown(int id);

    @Query("select * " +
            "from recipe " +
            "where id = :id " +
            "and transaction_time_start <= :transactionTime " +
            "and :transactionTime < transaction_time_end " +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from recipe " +
            "   where id = :id" +
            "   and valid_time_start <= " + NOW +
            "   and transaction_time_start <= :transactionTime " +
            "   and :transactionTime < transaction_time_end " +
            ")")
    abstract RecipeDbEntity getCurrentRecipeAsKnownAt(int id, Instant transactionTime);

    GroceryChainDbEntity getGroceryChainByValidOrTransactionTime(PreservedId id) {
        GroceryChainDbEntity groceryChain = getCurrentGroceryChain(id.id());
        if (groceryChain == null) {
            groceryChain = getLatestGroceryChainAsBestKnown(id.id());
        }
        if (groceryChain == null) {
            groceryChain = getCurrentGroceryChainAsKnownAt(id.id(), id.transactionTime());
        }
        return groceryChain;
    }

    @Query("select * " +
            "from current_grocery_chain " +
            "where id = :id")
    abstract GroceryChainDbEntity getCurrentGroceryChain(int id);

    @Query("select * " +
            "from grocery_chain " +
            "where id = :id " +
            "and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from grocery_chain " +
            "   where id = :id" +
            "   and valid_time_start <= " + NOW +
            "   and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            ")")
    abstract GroceryChainDbEntity getLatestGroceryChainAsBestKnown(int id);

    @Query("select * " +
            "from grocery_chain " +
            "where id = :id " +
            "and transaction_time_start <= :transactionTime " +
            "and :transactionTime < transaction_time_end " +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from grocery_chain " +
            "   where id = :id" +
            "   and valid_time_start <= " + NOW +
            "   and transaction_time_start <= :transactionTime " +
            "   and :transactionTime < transaction_time_end " +
            ")")
    abstract GroceryChainDbEntity getCurrentGroceryChainAsKnownAt(int id, Instant transactionTime);

    GroceryStoreDbEntity getGroceryStoreByValidOrTransactionTime(PreservedId id) {
        GroceryStoreDbEntity groceryStore = getCurrentGroceryStore(id.id());
        if (groceryStore == null) {
            groceryStore = getLatestGroceryStoreAsBestKnown(id.id());
        }
        if (groceryStore == null) {
            groceryStore = getCurrentGroceryStoreAsKnownAt(id.id(), id.transactionTime());
        }
        return groceryStore;
    }

    @Query("select * " +
            "from current_grocery_store " +
            "where id = :id")
    abstract GroceryStoreDbEntity getCurrentGroceryStore(int id);

    @Query("select * " +
            "from grocery_store " +
            "where id = :id " +
            "and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from grocery_store " +
            "   where id = :id" +
            "   and valid_time_start <= " + NOW +
            "   and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            ")")
    abstract GroceryStoreDbEntity getLatestGroceryStoreAsBestKnown(int id);

    @Query("select * " +
            "from grocery_store " +
            "where id = :id " +
            "and transaction_time_start <= :transactionTime " +
            "and :transactionTime < transaction_time_end " +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from grocery_store " +
            "   where id = :id" +
            "   and valid_time_start <= " + NOW +
            "   and transaction_time_start <= :transactionTime " +
            "   and :transactionTime < transaction_time_end " +
            ")")
    abstract GroceryStoreDbEntity getCurrentGroceryStoreAsKnownAt(int id, Instant transactionTime);

    PriceDbEntity getPriceByValidOrTransactionTime(PreservedId id) {
        PriceDbEntity price = getCurrentPrice(id.id());
        if (price == null) {
            price = getLatestPriceAsBestKnown(id.id());
        }
        if (price == null) {
            price = getCurrentPriceAsKnownAt(id.id(), id.transactionTime());
        }
        return price;
    }

    @Query("select * " +
            "from current_price " +
            "where id = :id")
    abstract PriceDbEntity getCurrentPrice(int id);

    @Query("select * " +
            "from price " +
            "where id = :id " +
            "and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from price " +
            "   where id = :id" +
            "   and valid_time_start <= " + NOW +
            "   and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            ")")
    abstract PriceDbEntity getLatestPriceAsBestKnown(int id);

    @Query("select * " +
            "from price " +
            "where id = :id " +
            "and transaction_time_start <= :transactionTime " +
            "and :transactionTime < transaction_time_end " +
            "and valid_time_start = (" +
            "   select max(valid_time_start) " +
            "   from price " +
            "   where id = :id" +
            "   and valid_time_start <= " + NOW +
            "   and transaction_time_start <= :transactionTime " +
            "   and :transactionTime < transaction_time_end " +
            ")")
    abstract PriceDbEntity getCurrentPriceAsKnownAt(int id, Instant transactionTime);
}
