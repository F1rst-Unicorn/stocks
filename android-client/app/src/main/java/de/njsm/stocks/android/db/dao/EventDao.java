/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.android.db.dao;

import androidx.paging.DataSource;
import androidx.paging.PositionalDataSource;
import androidx.room.Dao;
import androidx.room.Query;

import org.threeten.bp.Instant;

import de.njsm.stocks.android.business.data.activity.EntityEvent;
import de.njsm.stocks.android.db.views.AbstractHistoryView;
import de.njsm.stocks.android.db.views.EanNumberHistoryView;
import de.njsm.stocks.android.db.views.FoodHistoryView;
import de.njsm.stocks.android.db.views.FoodItemHistoryView;
import de.njsm.stocks.android.db.views.LocationHistoryView;
import de.njsm.stocks.android.db.views.UserDeviceHistoryView;
import de.njsm.stocks.android.db.views.UserHistoryView;

import static de.njsm.stocks.android.util.Config.DATABASE_INFINITY;

@Dao
public abstract class EventDao {

    private static final String TIME_COLUMNS =
            "l1._id as version1__id, l1.version as version1_version, l1.initiates as version1_initiates, l1.valid_time_start as version1_valid_time_start, l1.valid_time_end as version1_valid_time_end, l1.transaction_time_start as version1_transaction_time_start, l1.transaction_time_end as version1_transaction_time_end, " +
            "l2._id as version2__id, l2.version as version2_version, l2.initiates as version2_initiates, l2.valid_time_start as version2_valid_time_start, l2.valid_time_end as version2_valid_time_end, l2.transaction_time_start as version2_transaction_time_start, l2.transaction_time_end as version2_transaction_time_end, ";

    private static final String TIME_COLUMNS_FOOD =
            TIME_COLUMNS +
            "l1.transaction_time_start = (select min(transaction_time_start) from food x where x._id = l1._id) as is_first ";

    private static final String TIME_COLUMNS_FOOD_ITEM =
            TIME_COLUMNS +
            "l1.transaction_time_start = (select min(transaction_time_start) from fooditem x where x._id = l1._id) as is_first ";

    private static final String TIME_COLUMNS_LOCATION =
            TIME_COLUMNS +
            "l1.transaction_time_start = (select min(transaction_time_start) from location x where x._id = l1._id) as is_first ";

    private static final String TIME_COLUMNS_USER =
            TIME_COLUMNS +
            "l1.transaction_time_start = (select min(transaction_time_start) from user x where x._id = l1._id) as is_first ";

    private static final String TIME_COLUMNS_USER_DEVICE =
            TIME_COLUMNS +
            "l1.transaction_time_start = (select min(transaction_time_start) from user_device x where x._id = l1._id) as is_first ";

    private static final String TIME_COLUMNS_EAN_NUMBER =
            TIME_COLUMNS +
            "l1.transaction_time_start = (select min(transaction_time_start) from eannumber x where x._id = l1._id) as is_first ";

    private static final String ON_CHRONOLOGY =
            "on l1.transaction_time_start = l2.transaction_time_start and l1.version + 1 = l2.version and l1._id = l2._id ";

    private static final String WHERE_VALID_FOOD =
            "where not (l1.version != (select min(version) from food x where x._id = l1._id) and l2._id is null and l1.transaction_time_end != :infinity) " +
            "and (not (l1.valid_time_end = :infinity and l1.transaction_time_end = l1.valid_time_end) or l1.version = (select min(version) from food x where x._id = l1._id))";

    private static final String WHERE_VALID_LOCATION =
            "where not (l1.version != (select min(version) from location x where x._id = l1._id) and l2._id is null and l1.transaction_time_end != :infinity) " +
            "and (not (l1.valid_time_end = :infinity and l1.transaction_time_end = l1.valid_time_end) or l1.version = (select min(version) from location x where x._id = l1._id))";

    private static final String WHERE_VALID_FOOD_ITEM =
            "where not (l1.version != (select min(version) from fooditem x where x._id = l1._id) and l2._id is null and l1.transaction_time_end != :infinity) " +
            "and (not (l1.valid_time_end = :infinity and l1.transaction_time_end = l1.valid_time_end) or l1.version = (select min(version) from fooditem x where x._id = l1._id))";

    private static final String WHERE_VALID_USER =
            "where not (l1.version != (select min(version) from user x where x._id = l1._id) and l2._id is null and l1.transaction_time_end != :infinity) " +
            "and (not (l1.valid_time_end = :infinity and l1.transaction_time_end = l1.valid_time_end) or l1.version = (select min(version) from user x where x._id = l1._id))";

    private static final String WHERE_VALID_USER_DEVICE =
            "where not (l1.version != (select min(version) from user_device x where x._id = l1._id) and l2._id is null and l1.transaction_time_end != :infinity) " +
            "and (not (l1.valid_time_end = :infinity and l1.transaction_time_end = l1.valid_time_end) or l1.version = (select min(version) from user_device x where x._id = l1._id))";

    private static final String WHERE_VALID_EAN_NUMBER =
            "where not (l1.version != (select min(version) from eannumber x where x._id = l1._id) and l2._id is null and l1.transaction_time_end != :infinity) " +
            "and (not (l1.valid_time_end = :infinity and l1.transaction_time_end = l1.valid_time_end) or l1.version = (select min(version) from eannumber x where x._id = l1._id))";

    public DataSource.Factory<Integer, EntityEvent<?>> getLocationHistory() {
        return getLocationHistory(DATABASE_INFINITY)
                .map(AbstractHistoryView::mapToEvent);
    }

    public DataSource.Factory<Integer, EntityEvent<?>> getEanHistory() {
        return getEanHistory(DATABASE_INFINITY)
                .map(AbstractHistoryView::mapToEvent);
    }

    public DataSource.Factory<Integer, EntityEvent<?>> getFoodHistory() {
        return getFoodHistory(DATABASE_INFINITY)
                .map(AbstractHistoryView::mapToEvent);
    }

    public DataSource.Factory<Integer, EntityEvent<?>> getUserHistory() {
        return getUserHistory(DATABASE_INFINITY)
                .map(AbstractHistoryView::mapToEvent);
    }

    public DataSource.Factory<Integer, EntityEvent<?>> getUserDeviceHistory() {
        return getUserDeviceHistory(DATABASE_INFINITY)
                .map(AbstractHistoryView::mapToEvent);
    }

    public DataSource.Factory<Integer, EntityEvent<?>> getFoodItemHistory() {
        return getFoodItemHistory(DATABASE_INFINITY)
                .map(AbstractHistoryView::mapToEvent);
    }

    public DataSource.Factory<Integer, EntityEvent<?>> getFoodHistoryOfSingleFood(int id) {
        return getFoodHistoryOfSingleFood(id, DATABASE_INFINITY)
                .map(AbstractHistoryView::mapToEvent);
    }

    public DataSource.Factory<Integer, EntityEvent<?>> getFoodItemHistoryOfSingleFood(int id) {
        return getFoodItemHistoryOfSingleFood(id, DATABASE_INFINITY)
                .map(AbstractHistoryView::mapToEvent);
    }

    public DataSource.Factory<Integer, EntityEvent<?>> getEanNumberHistoryOfSingleFood(int id) {
        return getEanNumberHistoryOfSingleFood(id, DATABASE_INFINITY)
                .map(AbstractHistoryView::mapToEvent);
    }

    public DataSource.Factory<Integer, EntityEvent<?>> getLocationHistoryOfSingleLocation(int id) {
        return getLocationHistoryOfSingleLocation(id, DATABASE_INFINITY)
                .map(AbstractHistoryView::mapToEvent);
    }

    public DataSource.Factory<Integer, EntityEvent<?>> getFoodItemHistoryOfSingleLocation(int id) {
        return getFoodItemHistoryOfSingleLocation(id, DATABASE_INFINITY)
                .map(AbstractHistoryView::mapToEvent);
    }

    @Query("select " +
            "l1.name as version1_name, " +
            "l2.name as version2_name, " +
            TIME_COLUMNS_LOCATION +
            "from location l1 " +
            "left outer join location l2 " + ON_CHRONOLOGY +
            WHERE_VALID_LOCATION +
            "order by l1.transaction_time_start desc")
    abstract PositionalDataSource.Factory<Integer, LocationHistoryView> getLocationHistory(Instant infinity);

    @Query("select " +
            "f1.name as version1_identified_food_name, l1.number as version1_number, l1.identifies as version1_identifies," +
            "f2.name as version2_identified_food_name, l2.number as version2_number, l2.identifies as version2_identifies," +
            TIME_COLUMNS_EAN_NUMBER +
            "from eannumber l1 " +
            "left outer join eannumber l2 " + ON_CHRONOLOGY +
            "join food f1 on l1.identifies = f1._id and f1.valid_time_start <= l1.transaction_time_start and l1.transaction_time_start < f1.valid_time_end and f1.transaction_time_end = :infinity " +
            "left outer join food f2 on l2.identifies = f2._id and f2.valid_time_start <= l2.transaction_time_start and l2.transaction_time_start < f2.valid_time_end and f2.transaction_time_end = :infinity " +
            WHERE_VALID_EAN_NUMBER +
            "order by l1.transaction_time_start desc")
    abstract PositionalDataSource.Factory<Integer, EanNumberHistoryView> getEanHistory(Instant infinity);

    @Query("select " +
            "l1.name as version1_name, l1.location as version1_location, l1.to_buy as version1_to_buy, l1.expiration_offset as version1_expiration_offset, l1.location as version1_location, f1.name as version1_location_name, " +
            "l2.name as version2_name, l2.location as version2_location, l2.to_buy as version2_to_buy, l2.expiration_offset as version2_expiration_offset, l2.location as version2_location, f2.name as version2_location_name, " +
            TIME_COLUMNS_FOOD +
            "from food l1 " +
            "left outer join food l2 " + ON_CHRONOLOGY +
            "left outer join location f1 on l1.location = f1._id and f1.valid_time_start <= l1.transaction_time_start and l1.transaction_time_start < f1.valid_time_end and f1.transaction_time_end = :infinity " +
            "left outer join location f2 on l2.location = f2._id and f2.valid_time_start <= l2.transaction_time_start and l2.transaction_time_start < f2.valid_time_end and f2.transaction_time_end = :infinity " +
            WHERE_VALID_FOOD +
            "order by l1.transaction_time_start desc")
    abstract PositionalDataSource.Factory<Integer, FoodHistoryView> getFoodHistory(Instant infinity);

    @Query("select " +
            "l1.name as version1_name, " +
            "l2.name as version2_name, " +
            TIME_COLUMNS_USER +
            "from user l1 " +
            "left outer join user l2 " + ON_CHRONOLOGY +
            WHERE_VALID_USER +
            "order by l1.transaction_time_start desc")
    abstract PositionalDataSource.Factory<Integer, UserHistoryView> getUserHistory(Instant infinity);

    @Query("select " +
            "l1.name as version1_name, l1.belongs_to as version1_belongs_to, f1.name as version1_username, " +
            "l2.name as version2_name, l2.belongs_to as version2_belongs_to, f2.name as version2_username, " +
            TIME_COLUMNS_USER_DEVICE +
            "from user_device l1 " +
            "left outer join user_device l2 " + ON_CHRONOLOGY +
            "join user f1 on l1.belongs_to = f1._id and f1.valid_time_start <= l1.transaction_time_start and l1.transaction_time_start < f1.valid_time_end and f1.transaction_time_end = :infinity " +
            "left outer join user f2 on l2.belongs_to = f2._id and f2.valid_time_start <= l2.transaction_time_start and l2.transaction_time_start < f2.valid_time_end and f2.transaction_time_end = :infinity " +
            WHERE_VALID_USER_DEVICE +
            "order by l1.transaction_time_start desc")
    abstract PositionalDataSource.Factory<Integer, UserDeviceHistoryView> getUserDeviceHistory(Instant infinity);

    @Query("select " +
            "d1.name as version1_deviceName, u1.name as version1_userName, i1.name as version1_location, f1.name as version1_food_name, l1.of_type as version1_ofType, l1.stored_in as version1_storedIn, l1.eat_by as version1_eatByDate, " +
            "d2.name as version2_deviceName, u2.name as version2_userName, i2.name as version2_location, f2.name as version2_food_name, l2.of_type as version2_ofType, l2.stored_in as version2_storedIn, l2.eat_by as version2_eatByDate, " +
            TIME_COLUMNS_FOOD_ITEM +
            "from fooditem l1 " +
            "left outer join fooditem l2 " + ON_CHRONOLOGY +
            "join food f1 on l1.of_type = f1._id and f1.valid_time_start <= l1.transaction_time_start and l1.transaction_time_start < f1.valid_time_end and f1.transaction_time_end = :infinity " +
            "left outer join food f2 on l2.of_type = f2._id and f2.valid_time_start <= l2.transaction_time_start and l2.transaction_time_start < f2.valid_time_end and f2.transaction_time_end = :infinity " +
            "join location i1 on l1.stored_in = i1._id and i1.valid_time_start <= l1.transaction_time_start and l1.transaction_time_start < i1.valid_time_end and i1.transaction_time_end = :infinity " +
            "left outer join location i2 on l2.stored_in = i2._id and i2.valid_time_start <= l2.transaction_time_start and l2.transaction_time_start < i2.valid_time_end and i2.transaction_time_end = :infinity " +
            "join user u1 on l1.buys = u1._id and u1.valid_time_start <= l1.transaction_time_start and l1.transaction_time_start < u1.valid_time_end and u1.transaction_time_end = :infinity " +
            "left outer join user u2 on l2.buys = u2._id and u2.valid_time_start <= l2.transaction_time_start and l2.transaction_time_start < u2.valid_time_end and u2.transaction_time_end = :infinity " +
            "join user_device d1 on l1.registers = d1._id and d1.valid_time_start <= l1.transaction_time_start and l1.transaction_time_start < d1.valid_time_end and d1.transaction_time_end = :infinity " +
            "left outer join user_device d2 on l2.registers = d2._id and d2.valid_time_start <= l2.transaction_time_start and l2.transaction_time_start < d2.valid_time_end and d2.transaction_time_end = :infinity " +
            WHERE_VALID_FOOD_ITEM +
            "order by l1.transaction_time_start desc")
    abstract PositionalDataSource.Factory<Integer, FoodItemHistoryView> getFoodItemHistory(Instant infinity);

    @Query("select " +
            "l1.name as version1_name, l1.location as version1_location, l1.to_buy as version1_to_buy, l1.expiration_offset as version1_expiration_offset, l1.location as version1_location, f1.name as version1_location_name, " +
            "l2.name as version2_name, l2.location as version2_location, l2.to_buy as version2_to_buy, l2.expiration_offset as version2_expiration_offset, l2.location as version2_location, f2.name as version2_location_name, " +
            TIME_COLUMNS_FOOD +
            "from food l1 " +
            "left outer join food l2 " + ON_CHRONOLOGY +
            "left outer join location f1 on l1.location = f1._id and f1.valid_time_start <= l1.transaction_time_start and l1.transaction_time_start < f1.valid_time_end and f1.transaction_time_end = :infinity " +
            "left outer join location f2 on l2.location = f2._id and f2.valid_time_start <= l2.transaction_time_start and l2.transaction_time_start < f2.valid_time_end and f2.transaction_time_end = :infinity " +
            WHERE_VALID_FOOD +
            "and l1._id = :id " +
            "order by l1.transaction_time_start desc")
    abstract PositionalDataSource.Factory<Integer, FoodHistoryView> getFoodHistoryOfSingleFood(int id, Instant infinity);

    @Query("select " +
            "d1.name as version1_deviceName, u1.name as version1_userName, i1.name as version1_location, f1.name as version1_food_name, l1.of_type as version1_ofType, l1.stored_in as version1_storedIn, l1.eat_by as version1_eatByDate, " +
            "d2.name as version2_deviceName, u2.name as version2_userName, i2.name as version2_location, f2.name as version2_food_name, l2.of_type as version2_ofType, l2.stored_in as version2_storedIn, l2.eat_by as version2_eatByDate, " +
            TIME_COLUMNS_FOOD_ITEM +
            "from fooditem l1 " +
            "left outer join fooditem l2 " + ON_CHRONOLOGY +
            "join food f1 on l1.of_type = f1._id and f1.valid_time_start <= l1.transaction_time_start and l1.transaction_time_start < f1.valid_time_end and f1.transaction_time_end = :infinity " +
            "left outer join food f2 on l2.of_type = f2._id and f2.valid_time_start <= l2.transaction_time_start and l2.transaction_time_start < f2.valid_time_end and f2.transaction_time_end = :infinity " +
            "join location i1 on l1.stored_in = i1._id and i1.valid_time_start <= l1.transaction_time_start and l1.transaction_time_start < i1.valid_time_end and i1.transaction_time_end = :infinity " +
            "left outer join location i2 on l2.stored_in = i2._id and i2.valid_time_start <= l2.transaction_time_start and l2.transaction_time_start < i2.valid_time_end and i2.transaction_time_end = :infinity " +
            "join user u1 on l1.buys = u1._id and u1.valid_time_start <= l1.transaction_time_start and l1.transaction_time_start < u1.valid_time_end and u1.transaction_time_end = :infinity " +
            "left outer join user u2 on l2.buys = u2._id and u2.valid_time_start <= l2.transaction_time_start and l2.transaction_time_start < u2.valid_time_end and u2.transaction_time_end = :infinity " +
            "join user_device d1 on l1.registers = d1._id and d1.valid_time_start <= l1.transaction_time_start and l1.transaction_time_start < d1.valid_time_end and d1.transaction_time_end = :infinity " +
            "left outer join user_device d2 on l2.registers = d2._id and d2.valid_time_start <= l2.transaction_time_start and l2.transaction_time_start < d2.valid_time_end and d2.transaction_time_end = :infinity " +
            WHERE_VALID_FOOD_ITEM +
            "and l1.of_type = :id " +
            "order by l1.transaction_time_start desc")
    abstract PositionalDataSource.Factory<Integer, FoodItemHistoryView> getFoodItemHistoryOfSingleFood(int id, Instant infinity);

    @Query("select " +
            "f1.name as version1_identified_food_name, l1.number as version1_number, l1.identifies as version1_identifies," +
            "f2.name as version2_identified_food_name, l2.number as version2_number, l2.identifies as version2_identifies," +
            TIME_COLUMNS_EAN_NUMBER +
            "from eannumber l1 " +
            "left outer join eannumber l2 " + ON_CHRONOLOGY +
            "join food f1 on l1.identifies = f1._id and f1.valid_time_start <= l1.transaction_time_start and l1.transaction_time_start < f1.valid_time_end and f1.transaction_time_end = :infinity " +
            "left outer join food f2 on l2.identifies = f2._id and f2.valid_time_start <= l2.transaction_time_start and l2.transaction_time_start < f2.valid_time_end and f2.transaction_time_end = :infinity " +
            WHERE_VALID_EAN_NUMBER +
            "and l1.identifies = :id " +
            "order by l1.transaction_time_start desc")
    abstract PositionalDataSource.Factory<Integer, EanNumberHistoryView> getEanNumberHistoryOfSingleFood(int id, Instant infinity);

    @Query("select " +
            "d1.name as version1_deviceName, u1.name as version1_userName, i1.name as version1_location, f1.name as version1_food_name, l1.of_type as version1_ofType, l1.stored_in as version1_storedIn, l1.eat_by as version1_eatByDate, " +
            "d2.name as version2_deviceName, u2.name as version2_userName, i2.name as version2_location, f2.name as version2_food_name, l2.of_type as version2_ofType, l2.stored_in as version2_storedIn, l2.eat_by as version2_eatByDate, " +
            TIME_COLUMNS_FOOD_ITEM +
            "from fooditem l1 " +
            "left outer join fooditem l2 " + ON_CHRONOLOGY +
            "join food f1 on l1.of_type = f1._id and f1.valid_time_start <= l1.transaction_time_start and l1.transaction_time_start < f1.valid_time_end and f1.transaction_time_end = :infinity " +
            "left outer join food f2 on l2.of_type = f2._id and f2.valid_time_start <= l2.transaction_time_start and l2.transaction_time_start < f2.valid_time_end and f2.transaction_time_end = :infinity " +
            "join location i1 on l1.stored_in = i1._id and i1.valid_time_start <= l1.transaction_time_start and l1.transaction_time_start < i1.valid_time_end and i1.transaction_time_end = :infinity " +
            "left outer join location i2 on l2.stored_in = i2._id and i2.valid_time_start <= l2.transaction_time_start and l2.transaction_time_start < i2.valid_time_end and i2.transaction_time_end = :infinity " +
            "join user u1 on l1.buys = u1._id and u1.valid_time_start <= l1.transaction_time_start and l1.transaction_time_start < u1.valid_time_end and u1.transaction_time_end = :infinity " +
            "left outer join user u2 on l2.buys = u2._id and u2.valid_time_start <= l2.transaction_time_start and l2.transaction_time_start < u2.valid_time_end and u2.transaction_time_end = :infinity " +
            "join user_device d1 on l1.registers = d1._id and d1.valid_time_start <= l1.transaction_time_start and l1.transaction_time_start < d1.valid_time_end and d1.transaction_time_end = :infinity " +
            "left outer join user_device d2 on l2.registers = d2._id and d2.valid_time_start <= l2.transaction_time_start and l2.transaction_time_start < d2.valid_time_end and d2.transaction_time_end = :infinity " +
            WHERE_VALID_FOOD_ITEM +
            "and l1.stored_in = :id or l2.stored_in = :id " +
            "order by l1.transaction_time_start desc")
    abstract PositionalDataSource.Factory<Integer, FoodItemHistoryView> getFoodItemHistoryOfSingleLocation(int id, Instant infinity);

    @Query("select " +
            "l1.name as version1_name, " +
            "l2.name as version2_name, " +
            TIME_COLUMNS_LOCATION +
            "from location l1 " +
            "left outer join location l2 " + ON_CHRONOLOGY +
            WHERE_VALID_LOCATION +
            "and l1._id = :id " +
            "order by l1.transaction_time_start desc")
    abstract PositionalDataSource.Factory<Integer, LocationHistoryView> getLocationHistoryOfSingleLocation(int id, Instant infinity);
}
