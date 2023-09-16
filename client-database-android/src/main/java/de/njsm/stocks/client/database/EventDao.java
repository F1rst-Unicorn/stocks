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
import de.njsm.stocks.client.business.entities.EntityType;
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

    private static final String CROSS_JOIN_INITIATOR =
            "cross join user_device initiator_device on main_table.initiates = initiator_device.id " +
            "and initiator_device.valid_time_end = " + DATABASE_INFINITY_STRING_SQL + " " +
            "cross join user initiator_user on initiator_user.id = initiator_device.belongs_to " +
            "and initiator_user.valid_time_end = " + DATABASE_INFINITY_STRING_SQL + " ";

    private static final String WHERE_TIME_INTERVAL =
            "where :lower <= main_table.transaction_time_start " +
            "and main_table.transaction_time_start < :upper ";

    private static final String FILTER_INITIATES_USER =
            "and main_table.initiates in (" +
                "select d.id " +
                "from user_device d " +
                "where d.belongs_to = :initiatorUser " +
                "and d.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < d.valid_time_end " +
                "and d.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            ") ";

    public static final String SELECT_LOCATION =
            "select " +
            EVENT_COLUMNS +
            "main_table.name as name, " +
            "main_table.description as description " +
            "from location main_table ";

    @Query(SELECT_LOCATION +
            JOIN_INITIATOR +
            WHERE_TIME_INTERVAL +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<LocationEventFeedItem>> getLocationEvents(Instant lower, Instant upper);

    @Query(SELECT_LOCATION +
            JOIN_INITIATOR +
            WHERE_TIME_INTERVAL +
            "and main_table.id = :locationId " +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<LocationEventFeedItem>> getLocationEventsOf(int locationId, Instant lower, Instant upper);

    @Query(SELECT_LOCATION +
            JOIN_INITIATOR +
            WHERE_TIME_INTERVAL +
            "and main_table.initiates = :initiator " +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<LocationEventFeedItem>> getLocationEventsOfInitiator(int initiator, Instant lower, Instant upper);

    @Query(SELECT_LOCATION +
            JOIN_INITIATOR +
            WHERE_TIME_INTERVAL +
            FILTER_INITIATES_USER +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<LocationEventFeedItem>> getLocationEventsOfInitiatorUser(int initiatorUser, Instant lower, Instant upper);

    private static final String SELECT_UNIT = "select " +
            EVENT_COLUMNS +
            "main_table.name as name, " +
            "main_table.abbreviation as abbreviation " +
            "from unit main_table ";

    @Query(SELECT_UNIT +
            JOIN_INITIATOR +
            WHERE_TIME_INTERVAL +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<UnitEventFeedItem>> getUnitEvents(Instant lower, Instant upper);

    @Query(SELECT_UNIT +
            JOIN_INITIATOR +
            WHERE_TIME_INTERVAL +
            "and main_table.initiates = :initiator " +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<UnitEventFeedItem>> getUnitEventsOfInitiator(int initiator, Instant lower, Instant upper);

    @Query(SELECT_UNIT +
            JOIN_INITIATOR +
            WHERE_TIME_INTERVAL +
            "and main_table.initiates = :initiator " +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<UnitEventFeedItem>> getUnitEventsOfInitiatorUser(int initiator, Instant lower, Instant upper);

    private static final String SELECT_USER = "select " +
            EVENT_COLUMNS +
            "main_table.name as name " +
            "from user main_table ";

    @Query(SELECT_USER +
            JOIN_INITIATOR +
            WHERE_TIME_INTERVAL +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<UserEventFeedItem>> getUserEvents(Instant lower, Instant upper);

    @Query(SELECT_USER +
            JOIN_INITIATOR +
            WHERE_TIME_INTERVAL +
            "and main_table.initiates = :initiator " +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<UserEventFeedItem>> getUserEventsOfInitiator(int initiator, Instant lower, Instant upper);

    @Query(SELECT_USER +
            JOIN_INITIATOR +
            WHERE_TIME_INTERVAL +
            FILTER_INITIATES_USER +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<UserEventFeedItem>> getUserEventsOfInitiatorUser(int initiatorUser, Instant lower, Instant upper);

    private static final String SELECT_USER_DEVICE = "select " +
            EVENT_COLUMNS +
            "main_table.name as name, " +
            "owner.name as ownerName," +
            "owner.id as ownerId " +
            "from user_device main_table " +
            "join user owner on owner.id = main_table.belongs_to " +
                "and owner.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start <= owner.valid_time_end " +
                "and owner.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL;

    @Query(SELECT_USER_DEVICE +
            JOIN_INITIATOR +
            WHERE_TIME_INTERVAL +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<UserDeviceEventFeedItem>> getUserDeviceEvents(Instant lower, Instant upper);

    @Query(SELECT_USER_DEVICE +
            JOIN_INITIATOR +
            WHERE_TIME_INTERVAL +
            "and main_table.initiates = :initiator " +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<UserDeviceEventFeedItem>> getUserDeviceEventsOfInitiator(int initiator, Instant lower, Instant upper);

    @Query(SELECT_USER_DEVICE +
            JOIN_INITIATOR +
            WHERE_TIME_INTERVAL +
            FILTER_INITIATES_USER +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<UserDeviceEventFeedItem>> getUserDeviceEventsOfInitiatorUser(int initiatorUser, Instant lower, Instant upper);

    private static final String SELECT_SCALED_UNIT = "select " +
            EVENT_COLUMNS +
            "main_table.scale as scale, " +
            "unit.name as name, " +
            "unit.abbreviation as abbreviation " +
            "from scaled_unit main_table " +
            "join unit on unit.id = main_table.unit " +
                "and unit.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < unit.valid_time_end " +
                "and unit.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL;

    @Query(SELECT_SCALED_UNIT +
            JOIN_INITIATOR +
            WHERE_TIME_INTERVAL +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<ScaledUnitEventFeedItem>> getScaledUnitEvents(Instant lower, Instant upper);

    @Query(SELECT_SCALED_UNIT +
            JOIN_INITIATOR +
            WHERE_TIME_INTERVAL +
            "and main_table.initiates = :initiator " +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<ScaledUnitEventFeedItem>> getScaledUnitEventsOfInitiator(int initiator, Instant lower, Instant upper);

    @Query(SELECT_SCALED_UNIT +
            JOIN_INITIATOR +
            WHERE_TIME_INTERVAL +
            FILTER_INITIATES_USER +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<ScaledUnitEventFeedItem>> getScaledUnitEventsOfInitiatorUser(int initiatorUser, Instant lower, Instant upper);

    public static final String SELECT_FOOD_JOIN_TABLES =
            "select " +
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
                "and location.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL;

    @Query(SELECT_FOOD_JOIN_TABLES +
            JOIN_INITIATOR +
            WHERE_TIME_INTERVAL +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<FoodEventFeedItem>> getFoodEvents(Instant lower, Instant upper);

    @Query(SELECT_FOOD_JOIN_TABLES +
            JOIN_INITIATOR +
            WHERE_TIME_INTERVAL +
            "and main_table.id = :foodId " +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<FoodEventFeedItem>> getFoodEventsOf(int foodId, Instant lower, Instant upper);

    @Query(SELECT_FOOD_JOIN_TABLES +
            JOIN_INITIATOR +
            WHERE_TIME_INTERVAL +
            "and main_table.initiates = :initiator " +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<FoodEventFeedItem>> getFoodEventsOfInitiator(int initiator, Instant lower, Instant upper);

    @Query(SELECT_FOOD_JOIN_TABLES +
            JOIN_INITIATOR +
            WHERE_TIME_INTERVAL +
            FILTER_INITIATES_USER +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<FoodEventFeedItem>> getFoodEventsOfInitiatorUser(int initiatorUser, Instant lower, Instant upper);

    private static final String SELECT_FOOD_ITEM_JOIN_TABLES = "select " +
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
            "cross join food on food.id = main_table.of_type " +
                "and food.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < food.valid_time_end " +
                "and food.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "cross join scaled_unit on scaled_unit.id = main_table.unit " +
                "and scaled_unit.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < scaled_unit.valid_time_end " +
                "and scaled_unit.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "cross join unit on unit.id = scaled_unit.unit " +
                "and unit.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < unit.valid_time_end " +
                "and unit.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "cross join location on location.id = main_table.stored_in " +
                "and location.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < location.valid_time_end " +
                "and location.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "cross join user on user.id = main_table.buys " +
                "and user.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < user.valid_time_end " +
                "and user.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL +
            "cross join user_device on user_device.id = main_table.registers " +
                "and user_device.valid_time_start <= main_table.transaction_time_start " +
                "and main_table.transaction_time_start < user_device.valid_time_end " +
                "and user_device.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL;

    @Query(SELECT_FOOD_ITEM_JOIN_TABLES +
            CROSS_JOIN_INITIATOR +
            WHERE_TIME_INTERVAL +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<FoodItemEventFeedItem>> getFoodItemEvents(Instant lower, Instant upper);

    @Query(SELECT_FOOD_ITEM_JOIN_TABLES +
            CROSS_JOIN_INITIATOR +
            WHERE_TIME_INTERVAL +
            "and main_table.of_type = :foodId " +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<FoodItemEventFeedItem>> getFoodItemEventsOf(int foodId, Instant lower, Instant upper);

    @Query(SELECT_FOOD_ITEM_JOIN_TABLES +
            CROSS_JOIN_INITIATOR +
            WHERE_TIME_INTERVAL +
            "and :locationId in (" +
                "select item.stored_in " +
                "from food_item item " +
                "where item.transaction_time_start = main_table.transaction_time_start " +
                "and item.id = main_table.id" +
            ") " +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<FoodItemEventFeedItem>> getFoodItemEventsInvolving(int locationId, Instant lower, Instant upper);

    @Query(SELECT_FOOD_ITEM_JOIN_TABLES +
            CROSS_JOIN_INITIATOR +
            WHERE_TIME_INTERVAL +
            "and main_table.initiates = :initiator " +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<FoodItemEventFeedItem>> getFoodItemEventsOfInitiator(int initiator, Instant lower, Instant upper);

    @Query(SELECT_FOOD_ITEM_JOIN_TABLES +
            CROSS_JOIN_INITIATOR +
            WHERE_TIME_INTERVAL +
            FILTER_INITIATES_USER +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Flowable<List<FoodItemEventFeedItem>> getFoodItemEventsOfInitiatorUser(int initiatorUser, Instant lower, Instant upper);

    private static final String SELECT_EAN_NUMBER_JOIN_FOOD = "select " +
            EVENT_COLUMNS +
            "main_table.number as eanNumber, " +
            "food.name as foodName, " +
            "main_table.identifies as identifies " +
            "from ean_number main_table " +
            "join food on food.id = main_table.identifies " +
                    "and food.valid_time_start <= main_table.transaction_time_start " +
                    "and main_table.transaction_time_start < food.valid_time_end " +
                    "and food.transaction_time_end = " + DATABASE_INFINITY_STRING_SQL;

    @Query(SELECT_EAN_NUMBER_JOIN_FOOD +
            JOIN_INITIATOR +
            WHERE_TIME_INTERVAL +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Observable<List<EanNumberEventFeedItem>> getEanNumberEvents(Instant lower, Instant upper);

    @Query(SELECT_EAN_NUMBER_JOIN_FOOD +
            JOIN_INITIATOR +
            WHERE_TIME_INTERVAL +
            "and main_table.identifies = :foodId " +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Observable<List<EanNumberEventFeedItem>> getEanNumberEventsOfFood(int foodId, Instant lower, Instant upper);

    @Query(SELECT_EAN_NUMBER_JOIN_FOOD +
            JOIN_INITIATOR +
            WHERE_TIME_INTERVAL +
            "and main_table.initiates = :initiator " +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Observable<List<EanNumberEventFeedItem>> getEanNumberEventsOfInitiator(int initiator, Instant lower, Instant upper);

    @Query(SELECT_EAN_NUMBER_JOIN_FOOD +
            JOIN_INITIATOR +
            WHERE_TIME_INTERVAL +
            FILTER_INITIATES_USER +
            "order by main_table.transaction_time_start desc, main_table.valid_time_end")
    abstract Observable<List<EanNumberEventFeedItem>> getEanNumberEventsOfInitiatorUser(int initiatorUser, Instant lower, Instant upper);

    @Query("select max(last_update) x " +
            "from updates " +
            "where name in (:relevantEntities) " +
            "union " +
            "select '1970-01-01 00:00:00.000000' x " +
            "order by x desc " +
            "limit 1")
    abstract Observable<Instant> getLatestUpdateTimestamp(List<EntityType> relevantEntities);

    private static final String SELECT_REDUCER = "select " +
            "case :previous " +
                "when true then max(main_table.transaction_time_start) " +
                "else min(main_table.transaction_time_end) end ";

    @Query(SELECT_REDUCER +
            "from location main_table " +
            "where :previous != (main_table.transaction_time_start >= :day)")
    abstract Maybe<Instant> getNextDayContainingLocationEvents(Instant day, boolean previous);

    @Query(SELECT_REDUCER +
            "from location main_table " +
            "where :previous != (main_table.transaction_time_start >= :day) " +
            "and main_table.id = :location")
    abstract Maybe<Instant> getNextDayContainingLocationEvents(Instant day, int location, boolean previous);

    @Query(SELECT_REDUCER +
            "from location main_table " +
            "where :previous != (main_table.transaction_time_start >= :day) " +
            "and main_table.initiates = :initiator")
    abstract Maybe<Instant> getNextDayContainingLocationEventsOfInitiator(Instant day, boolean previous, int initiator);

    @Query(SELECT_REDUCER +
            "from location main_table " +
            "where :previous != (main_table.transaction_time_start >= :day) " +
            FILTER_INITIATES_USER)
    abstract Maybe<Instant> getNextDayContainingLocationEventsOfInitiatorUser(Instant day, boolean previous, int initiatorUser);

    @Query(SELECT_REDUCER +
            "from unit main_table " +
            "where :previous != (main_table.transaction_time_start >= :day)")
    abstract Maybe<Instant> getNextDayContainingUnitEvents(Instant day, boolean previous);

    @Query(SELECT_REDUCER +
            "from unit main_table " +
            "where :previous != (main_table.transaction_time_start >= :day) " +
            "and main_table.initiates = :initiator")
    abstract Maybe<Instant> getNextDayContainingUnitEventsOfInitiator(Instant day, boolean previous, int initiator);

    @Query(SELECT_REDUCER +
            "from unit main_table " +
            "where :previous != (main_table.transaction_time_start >= :day) " +
            FILTER_INITIATES_USER)
    abstract Maybe<Instant> getNextDayContainingUnitEventsOfInitiatorUser(Instant day, boolean previous, int initiatorUser);

    @Query(SELECT_REDUCER +
            "from scaled_unit main_table " +
            "where :previous != (main_table.transaction_time_start >= :day)")
    abstract Maybe<Instant> getNextDayContainingScaledUnitEvents(Instant day, boolean previous);

    @Query(SELECT_REDUCER +
            "from scaled_unit main_table " +
            "where :previous != (main_table.transaction_time_start >= :day) " +
            "and main_table.initiates = :initiator")
    abstract Maybe<Instant> getNextDayContainingScaledUnitEventsOfInitiator(Instant day, boolean previous, int initiator);

    @Query(SELECT_REDUCER +
            "from scaled_unit main_table " +
            "where :previous != (main_table.transaction_time_start >= :day) " +
            FILTER_INITIATES_USER)
    abstract Maybe<Instant> getNextDayContainingScaledUnitEventsOfInitiatorUser(Instant day, boolean previous, int initiatorUser);

    @Query(SELECT_REDUCER +
            "from food main_table " +
            "where :previous != (main_table.transaction_time_start >= :day)")
    abstract Maybe<Instant> getNextDayContainingFoodEvents(Instant day, boolean previous);

    @Query(SELECT_REDUCER +
            "from food main_table " +
            "where :previous != (main_table.transaction_time_start >= :day) " +
            "and main_table.id = :food")
    abstract Maybe<Instant> getNextDayContainingFoodEvents(Instant day, int food, boolean previous);

    @Query(SELECT_REDUCER +
            "from food main_table " +
            "where :previous != (main_table.transaction_time_start >= :day) " +
            "and main_table.initiates = :initiator")
    abstract Maybe<Instant> getNextDayContainingFoodEventsOfInitiator(Instant day, boolean previous, int initiator);

    @Query(SELECT_REDUCER +
            "from food main_table " +
            "where :previous != (main_table.transaction_time_start >= :day) " +
            FILTER_INITIATES_USER)
    abstract Maybe<Instant> getNextDayContainingFoodEventsOfInitiatorUser(Instant day, boolean previous, int initiatorUser);

    @Query(SELECT_REDUCER +
            "from food_item main_table " +
            "where :previous != (main_table.transaction_time_start >= :day)")
    abstract Maybe<Instant> getNextDayContainingFoodItemEvents(Instant day, boolean previous);

    @Query(SELECT_REDUCER +
            "from food_item main_table " +
            "where :previous != (main_table.transaction_time_start >= :day) " +
            "and main_table.stored_in = :location")
    abstract Maybe<Instant> getNextDayContainingFoodItemEventsOfLocation(Instant day, int location, boolean previous);

    @Query(SELECT_REDUCER +
            "from food_item main_table " +
            "where :previous != (main_table.transaction_time_start >= :day) " +
            "and main_table.of_type = :food")
    abstract Maybe<Instant> getNextDayContainingFoodItemEventsOfFood(Instant day, int food, boolean previous);

    @Query(SELECT_REDUCER +
            "from food_item main_table " +
            "where :previous != (main_table.transaction_time_start >= :day) " +
            "and main_table.initiates = :initiator")
    abstract Maybe<Instant> getNextDayContainingFoodItemEventsOfInitiator(Instant day, boolean previous, int initiator);

    @Query(SELECT_REDUCER +
            "from food_item main_table " +
            "where :previous != (main_table.transaction_time_start >= :day) " +
            FILTER_INITIATES_USER)
    abstract Maybe<Instant> getNextDayContainingFoodItemEventsOfInitiatorUser(Instant day, boolean previous, int initiatorUser);

    @Query(SELECT_REDUCER +
            "from ean_number main_table " +
            "where :previous != (main_table.transaction_time_start >= :day)")
    abstract Maybe<Instant> getNextDayContainingEanNumberEvents(Instant day, boolean previous);

    @Query(SELECT_REDUCER +
            "from ean_number main_table " +
            "where :previous != (main_table.transaction_time_start >= :day) " +
            "and main_table.identifies = :food")
    abstract Maybe<Instant> getNextDayContainingEanNumberEvents(Instant day, int food, boolean previous);

    @Query(SELECT_REDUCER +
            "from ean_number main_table " +
            "where :previous != (main_table.transaction_time_start >= :day) " +
            "and main_table.initiates = :initiator")
    abstract Maybe<Instant> getNextDayContainingEanNumberEvents(Instant day, boolean previous, int initiator);

    @Query(SELECT_REDUCER +
            "from ean_number main_table " +
            "where :previous != (main_table.transaction_time_start >= :day) " +
            FILTER_INITIATES_USER)
    abstract Maybe<Instant> getNextDayContainingEanNumberEventsUser(Instant day, boolean previous, int initiatorUser);

    @Query(SELECT_REDUCER +
            "from recipe main_table " +
            "where :previous != (main_table.transaction_time_start >= :day)")
    abstract Maybe<Instant> getNextDayContainingRecipeEvents(Instant day, boolean previous);

    @Query(SELECT_REDUCER +
            "from recipe_ingredient main_table " +
            "where :previous != (main_table.transaction_time_start >= :day)")
    abstract Maybe<Instant> getNextDayContainingRecipeIngredientEvents(Instant day, boolean previous);

    @Query(SELECT_REDUCER +
            "from recipe_product main_table " +
            "where :previous != (main_table.transaction_time_start >= :day)")
    abstract Maybe<Instant> getNextDayContainingRecipeProductEvents(Instant day, boolean previous);

    @Query(SELECT_REDUCER +
            "from user main_table " +
            "where :previous != (main_table.transaction_time_start >= :day)")
    abstract Maybe<Instant> getNextDayContainingUserEvents(Instant day, boolean previous);

    @Query(SELECT_REDUCER +
            "from user main_table " +
            "where :previous != (main_table.transaction_time_start >= :day) " +
            "and main_table.initiates = :initiator")
    abstract Maybe<Instant> getNextDayContainingUserEventsOfInitiator(Instant day, boolean previous, int initiator);

    @Query(SELECT_REDUCER +
            "from user main_table " +
            "where :previous != (main_table.transaction_time_start >= :day) " +
            FILTER_INITIATES_USER)
    abstract Maybe<Instant> getNextDayContainingUserEventsOfInitiatorUser(Instant day, boolean previous, int initiatorUser);

    @Query(SELECT_REDUCER +
            "from user_device main_table " +
            "where :previous != (main_table.transaction_time_start >= :day)")
    abstract Maybe<Instant> getNextDayContainingUserDeviceEvents(Instant day, boolean previous);

    @Query(SELECT_REDUCER +
            "from user_device main_table " +
            "where :previous != (main_table.transaction_time_start >= :day) " +
            "and main_table.initiates = :initiator")
    abstract Maybe<Instant> getNextDayContainingUserDeviceEventsOfInitiator(Instant day, boolean previous, int initiator);

    @Query(SELECT_REDUCER +
            "from user_device main_table " +
            "where :previous != (main_table.transaction_time_start >= :day) " +
            FILTER_INITIATES_USER)
    abstract Maybe<Instant> getNextDayContainingUserDeviceEventsOfInitiatorUser(Instant day, boolean previous, int initiatorUser);
}
