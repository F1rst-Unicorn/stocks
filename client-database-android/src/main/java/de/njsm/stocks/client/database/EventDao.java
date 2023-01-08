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
import de.njsm.stocks.client.business.event.*;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;

import java.time.Instant;
import java.util.List;

import static de.njsm.stocks.client.database.StocksDatabase.DATABASE_INFINITY_STRING_SQL;

@Dao
abstract class EventDao {

    private static final String EVENT_COLUMNS =
            "main_table.id as id, " +
            "main_table.valid_time_end as validTimeEnd, " +
            "main_table.transaction_time_start as transactionTimeStart, " +
            "initiator_user.name as userName, ";

    private static final String JOIN_INITIATOR =
            "join user_device initiator_device on main_table.initiates = initiator_device.id " +
            "and initiator_device.valid_time_end = " + DATABASE_INFINITY_STRING_SQL + " " +
            "join user initiator_user on initiator_user.id = initiator_device.belongs_to " +
            "and initiator_user.valid_time_end = " + DATABASE_INFINITY_STRING_SQL + " ";

    @Query("select " +
            EVENT_COLUMNS +
            "main_table.name as name, " +
            "main_table.description as description " +
            "from location main_table " +
            JOIN_INITIATOR +
            "where :lower <= main_table.transaction_time_start " +
            "and main_table.transaction_time_start < :upper " +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<LocationEventFeedItem>> getLocationEvents(Instant lower, Instant upper);

    @Query("select " +
            EVENT_COLUMNS +
            "main_table.name as name, " +
            "main_table.description as description " +
            "from location main_table " +
            JOIN_INITIATOR +
            "where :lower <= main_table.transaction_time_start " +
            "and main_table.transaction_time_start < :upper " +
            "and main_table.id = :locationId " +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<LocationEventFeedItem>> getLocationEventsOf(int locationId, Instant lower, Instant upper);

    @Query("select " +
            EVENT_COLUMNS +
            "main_table.name as name, " +
            "main_table.abbreviation as abbreviation " +
            "from unit main_table " +
            JOIN_INITIATOR +
            "where :lower <= main_table.transaction_time_start " +
            "and main_table.transaction_time_start < :upper " +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<UnitEventFeedItem>> getUnitEvents(Instant lower, Instant upper);

    @Query("select " +
            EVENT_COLUMNS +
            "main_table.name as name " +
            "from user main_table " +
            JOIN_INITIATOR +
            "where :lower <= main_table.transaction_time_start " +
            "and main_table.transaction_time_start < :upper " +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<UserEventFeedItem>> getUserEvents(Instant lower, Instant upper);

    @Query("select " +
            EVENT_COLUMNS +
            "main_table.name as name, " +
            "owner.name as ownerName," +
            "owner.id as ownerId " +
            "from user_device main_table " +
            "join user owner on owner.id = main_table.belongs_to " +
                "and owner.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start <= owner.valid_time_end " +
                "and owner.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            JOIN_INITIATOR +
            "where :lower <= main_table.transaction_time_start " +
            "and main_table.transaction_time_start <= :upper " +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<UserDeviceEventFeedItem>> getUserDeviceEvents(Instant lower, Instant upper);

    @Query("select " +
            EVENT_COLUMNS +
            "main_table.scale as scale, " +
            "unit.name as name, " +
            "unit.abbreviation as abbreviation " +
            "from scaled_unit main_table " +
            "join unit on unit.id = main_table.unit " +
                "and unit.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < unit.valid_time_end " +
                "and unit.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            JOIN_INITIATOR +
            "where :lower <= main_table.transaction_time_start " +
            "and main_table.transaction_time_start < :upper " +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<ScaledUnitEventFeedItem>> getScaledUnitEvents(Instant lower, Instant upper);

    @Query("select " +
            EVENT_COLUMNS +
            "main_table.name as name," +
            "main_table.to_buy as toBuy," +
            "main_table.expiration_offset as expirationOffset, " +
            "scaled_unit.scale as unitScale, " +
            "unit.abbreviation as abbreviation, " +
            "location.name as locationName, " +
            "main_table.description as description " +
            "from food main_table " +
            "join scaled_unit on scaled_unit.id = main_table.store_unit " +
                "and scaled_unit.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < scaled_unit.valid_time_end " +
                "and scaled_unit.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "join unit on unit.id = scaled_unit.unit " +
                "and unit.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < unit.valid_time_end " +
                "and unit.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "left outer join location on location.id = main_table.location " +
                "and location.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < location.valid_time_end " +
                "and location.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            JOIN_INITIATOR +
            "where :lower <= main_table.transaction_time_start " +
            "and main_table.transaction_time_start < :upper " +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<FoodEventFeedItem>> getFoodEvents(Instant lower, Instant upper);

    @Query("select " +
            EVENT_COLUMNS +
            "main_table.name as name," +
            "main_table.to_buy as toBuy," +
            "main_table.expiration_offset as expirationOffset, " +
            "scaled_unit.scale as unitScale, " +
            "unit.abbreviation as abbreviation, " +
            "location.name as locationName, " +
            "main_table.description as description " +
            "from food main_table " +
            "join scaled_unit on scaled_unit.id = main_table.store_unit " +
                "and scaled_unit.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < scaled_unit.valid_time_end " +
                "and scaled_unit.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "join unit on unit.id = scaled_unit.unit " +
                "and unit.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < unit.valid_time_end " +
                "and unit.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "left outer join location on location.id = main_table.location " +
                "and location.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < location.valid_time_end " +
                "and location.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            JOIN_INITIATOR +
            "where :lower <= main_table.transaction_time_start " +
            "and main_table.transaction_time_start < :upper " +
            "and main_table.id = :foodId " +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<FoodEventFeedItem>> getFoodEventsOf(int foodId, Instant lower, Instant upper);

    @Query("select " +
            EVENT_COLUMNS +
            "food.name as foodName, " +
            "food.id as ofType, " +
            "main_table.eat_by as eatBy, " +
            "scaled_unit.scale as unitScale, " +
            "unit.abbreviation as abbreviation, " +
            "user.name as buyer, " +
            "user_device.name as registerer, " +
            "location.name as locationName " +
            "from food_item main_table " +
            "join food on food.id = main_table.of_type " +
                "and food.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < food.valid_time_end " +
                "and food.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "join scaled_unit on scaled_unit.id = main_table.unit " +
                "and scaled_unit.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < scaled_unit.valid_time_end " +
                "and scaled_unit.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "join unit on unit.id = scaled_unit.unit " +
                "and unit.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < unit.valid_time_end " +
                "and unit.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "join location on location.id = main_table.stored_in " +
                "and location.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < location.valid_time_end " +
                "and location.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "join user on user.id = main_table.buys " +
                "and user.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < user.valid_time_end " +
                "and user.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "join user_device on user_device.id = main_table.registers " +
                "and user_device.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < user_device.valid_time_end " +
                "and user_device.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            JOIN_INITIATOR +
            "where :lower <= main_table.transaction_time_start " +
            "and main_table.transaction_time_start < :upper " +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<FoodItemEventFeedItem>> getFoodItemEvents(Instant lower, Instant upper);

    @Query("select " +
            EVENT_COLUMNS +
            "food.name as foodName, " +
            "food.id as ofType, " +
            "main_table.eat_by as eatBy, " +
            "scaled_unit.scale as unitScale, " +
            "unit.abbreviation as abbreviation, " +
            "user.name as buyer, " +
            "user_device.name as registerer, " +
            "location.name as locationName " +
            "from food_item main_table " +
            "join food on food.id = main_table.of_type " +
                "and food.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < food.valid_time_end " +
                "and food.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "join scaled_unit on scaled_unit.id = main_table.unit " +
                "and scaled_unit.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < scaled_unit.valid_time_end " +
                "and scaled_unit.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "join unit on unit.id = scaled_unit.unit " +
                "and unit.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < unit.valid_time_end " +
                "and unit.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "join location on location.id = main_table.stored_in " +
                "and location.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < location.valid_time_end " +
                "and location.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "join user on user.id = main_table.buys " +
                "and user.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < user.valid_time_end " +
                "and user.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "join user_device on user_device.id = main_table.registers " +
                "and user_device.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < user_device.valid_time_end " +
                "and user_device.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            JOIN_INITIATOR +
            "where :lower <= main_table.transaction_time_start " +
            "and main_table.transaction_time_start < :upper " +
            "and main_table.of_type = :foodId " +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<FoodItemEventFeedItem>> getFoodItemEventsOf(int foodId, Instant lower, Instant upper);

    @Query("select " +
            EVENT_COLUMNS +
            "food.name as foodName, " +
            "food.id as ofType, " +
            "main_table.eat_by as eatBy, " +
            "scaled_unit.scale as unitScale, " +
            "unit.abbreviation as abbreviation, " +
            "user.name as buyer, " +
            "user_device.name as registerer, " +
            "location.name as locationName " +
            "from food_item main_table " +
            "join food on food.id = main_table.of_type " +
                "and food.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < food.valid_time_end " +
                "and food.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "join scaled_unit on scaled_unit.id = main_table.unit " +
                "and scaled_unit.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < scaled_unit.valid_time_end " +
                "and scaled_unit.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "join unit on unit.id = scaled_unit.unit " +
                "and unit.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < unit.valid_time_end " +
                "and unit.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "join location on location.id = main_table.stored_in " +
                "and location.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < location.valid_time_end " +
                "and location.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "join user on user.id = main_table.buys " +
                "and user.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < user.valid_time_end " +
                "and user.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "join user_device on user_device.id = main_table.registers " +
                "and user_device.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < user_device.valid_time_end " +
                "and user_device.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            JOIN_INITIATOR +
            "where :lower <= main_table.transaction_time_start " +
            "and main_table.transaction_time_start < :upper " +
            "and :locationId in (" +
                "select stored_in " +
                "from food_item " +
                "where transaction_time_start = main_table.transaction_time_start" +
            ")" +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<FoodItemEventFeedItem>> getFoodItemEventsInvolving(int locationId, Instant lower, Instant upper);

    @Query("select " +
            EVENT_COLUMNS +
            "main_table.number as eanNumber, " +
            "food.name as foodName," +
            "main_table.identifies as identifies " +
            "from ean_number main_table " +
            "join food on food.id = main_table.identifies " +
                "and food.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < food.valid_time_end " +
                "and food.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            JOIN_INITIATOR +
            "where :lower <= main_table.transaction_time_start " +
            "and main_table.transaction_time_start < :upper " +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Observable<List<EanNumberEventFeedItem>> getEanNumberEvents(Instant lower, Instant upper);

    @Query("select " +
            EVENT_COLUMNS +
            "main_table.number as eanNumber, " +
            "food.name as foodName, " +
            "main_table.identifies as identifies " +
            "from ean_number main_table " +
            "join food on food.id = main_table.identifies " +
                "and food.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < food.valid_time_end " +
                "and food.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            JOIN_INITIATOR +
            "where :lower <= main_table.transaction_time_start " +
            "and main_table.transaction_time_start < :upper " +
            "and main_table.identifies = :foodId " +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Observable<List<EanNumberEventFeedItem>> getEanNumberEventsOf(int foodId, Instant lower, Instant upper);

    @Query("select max(last_update) " +
            "from updates")
    abstract Observable<Instant> getLatestUpdateTimestamp();

    @Query("select min(x) from (" +
            "select * from (" +
                "select min(transaction_time_start) x " +
                "from location " +
                "where transaction_time_start >= :day" +
            ") union select * from (" +
                "select min(transaction_time_start) x " +
                "from unit " +
              "where transaction_time_start >= :day" +
            ") union select * from (" +
                "select min(transaction_time_start) x " +
                "from scaled_unit " +
                "where transaction_time_start >= :day" +
            ") union select * from (" +
                "select min(transaction_time_start) x " +
                "from food " +
                "where transaction_time_start >= :day" +
            ") union select * from (" +
                "select min(transaction_time_start) x " +
                "from food_item " +
                "where transaction_time_start >= :day" +
            ") union select * from (" +
                "select min(transaction_time_start) x " +
                "from ean_number " +
                "where transaction_time_start >= :day" +
            ") union select * from (" +
                "select min(transaction_time_start) x " +
                "from recipe " +
                "where transaction_time_start >= :day" +
            ") union select * from (" +
                "select min(transaction_time_start) x " +
                "from recipe_ingredient " +
                "where transaction_time_start >= :day" +
            ") union select * from (" +
                "select min(transaction_time_start) x " +
                "from recipe_product " +
                "where transaction_time_start >= :day" +
            ") union select * from (" +
                "select min(transaction_time_start) x " +
                "from user " +
                "where transaction_time_start >= :day" +
            ") union select * from (" +
                "select min(transaction_time_start) x " +
                "from user_device " +
                "where transaction_time_start >= :day" +
            ")" +
            ")")
    abstract Maybe<Instant> getNextDayContainingEvents(Instant day);

    @Query("select max(x) from (" +
            "select * from (" +
                "select max(transaction_time_start) x " +
                "from location " +
                "where transaction_time_start < :day" +
            ") union select * from (" +
                "select max(transaction_time_start) x " +
                "from unit " +
              "where transaction_time_start < :day" +
            ") union select * from (" +
                "select max(transaction_time_start) x " +
                "from scaled_unit " +
                "where transaction_time_start < :day" +
            ") union select * from (" +
                "select max(transaction_time_start) x " +
                "from food " +
                "where transaction_time_start < :day" +
            ") union select * from (" +
                "select max(transaction_time_start) x " +
                "from food_item " +
                "where transaction_time_start < :day" +
            ") union select * from (" +
                "select max(transaction_time_start) x " +
                "from ean_number " +
                "where transaction_time_start < :day" +
            ") union select * from (" +
                "select max(transaction_time_start) x " +
                "from recipe " +
                "where transaction_time_start < :day" +
            ") union select * from (" +
                "select max(transaction_time_start) x " +
                "from recipe_ingredient " +
                "where transaction_time_start < :day" +
            ") union select * from (" +
                "select max(transaction_time_start) x " +
                "from recipe_product " +
                "where transaction_time_start < :day" +
            ") union select * from (" +
                "select max(transaction_time_start) x " +
                "from user " +
                "where transaction_time_start < :day" +
            ") union select * from (" +
                "select max(transaction_time_start) x " +
                "from user_device " +
                "where transaction_time_start < :day" +
            ")" +
            ")")
    abstract Maybe<Instant> getPreviousDayContainingEvents(Instant day);
}
