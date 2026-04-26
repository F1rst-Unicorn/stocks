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

package de.njsm.stocks.client.database;

import androidx.room.Dao;
import androidx.room.Query;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.PriceForDeletion;
import de.njsm.stocks.client.business.entities.PriceForTableListingData;
import io.reactivex.rxjava3.core.Observable;

import java.util.List;

import static de.njsm.stocks.client.database.StocksDatabase.DATABASE_INFINITY_STRING_SQL;
import static de.njsm.stocks.client.database.StocksDatabase.NOW;

@Dao
abstract class PriceDao {

    @Query("select * from current_price where id = :id")
    abstract PriceForDeletion getPrice(int id);

    @Query("select " +
                "price.id as id, " +
                "price.valid_time_start as date, " +
                "current_grocery_store.name as groceryStoreName, " +
                "current_grocery_chain.name as groceryChainName, " +
                "price.price as price, " +
                "current_scaled_unit.scale as quantity, " +
                "current_unit.abbreviation as abbreviation " +
            "from price " +
            "join current_grocery_store on current_grocery_store.id = price.grocery_store " +
            "join current_grocery_chain on current_grocery_chain.id = current_grocery_store.grocery_chain " +
            "join current_scaled_unit on current_scaled_unit.id = price.scaled_unit " +
            "join current_unit on current_unit.id = current_scaled_unit.unit " +
            "where price.food = :foodId " +
            "and price.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            " order by price.valid_time_start desc")
    abstract Observable<List<PriceRepositoryImpl.PriceListingData>> getPricesOfFood(int foodId);

    @Query("select " +
                "price.valid_time_start as date, " +
                "price.price as price, " +
                "price.scale as scale, " +
                "grocery_store.name as groceryStoreName, " +
                "grocery_chain.name as groceryChainName, " +
                "scaled_unit.scale as scaledUnitScale, " +
                "unit.abbreviation as abbreviation " +
            "from price " +
            "join grocery_store on grocery_store.id = price.grocery_store " +
                "and grocery_store.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
                " and grocery_store.valid_time_start = (" +
                    "select max(g.valid_time_start) " +
                    "from grocery_store g " +
                    "where g.id = grocery_store.id " +
                    "and g.valid_time_start <= " + NOW +
                    "and g.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
                ")" +
            "join grocery_chain on grocery_chain.id = grocery_store.grocery_chain " +
                "and grocery_chain.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
                " and grocery_chain.valid_time_start = (" +
                    "select max(g.valid_time_start) " +
                    "from grocery_chain g " +
                    "where g.id = grocery_chain.id " +
                    "and g.valid_time_start <= " + NOW +
                    "and g.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
                ")" +
            "join scaled_unit on scaled_unit.id = price.scaled_unit " +
                "and scaled_unit.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
                " and scaled_unit.valid_time_start = (" +
                    "select max(g.valid_time_start) " +
                    "from scaled_unit g " +
                    "where g.id = scaled_unit.id " +
                    "and g.valid_time_start <= " + NOW +
                    "and g.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
                ")" +
            "join unit on unit.id = scaled_unit.unit " +
                "and unit.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
                " and unit.valid_time_start = (" +
                    "select max(g.valid_time_start) " +
                    "from unit g " +
                    "where g.id = unit.id " +
                    "and g.valid_time_start <= " + NOW +
                    "and g.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
                ")" +
            "where price.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            " and price.food = :id " +
            "and price.id in (" +
                "select id " +
                "from price " +
                "where food = :id " +
                "and transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
                "group by grocery_store " +
                "having price.valid_time_start = max(price.valid_time_start)" +
                "" +
            ") " +
            "order by price.valid_time_start desc")
    abstract Observable<List<PriceForTableListingData>> getLatestPriceByGroceryStore(Id<Food> id);
}
