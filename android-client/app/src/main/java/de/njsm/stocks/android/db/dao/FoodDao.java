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

import androidx.lifecycle.LiveData;
import androidx.room.*;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.db.entities.Sql;
import de.njsm.stocks.android.db.views.FoodSummaryView;
import de.njsm.stocks.android.db.views.FoodSummaryWithExpirationView;
import org.threeten.bp.Instant;

import java.util.List;

import static de.njsm.stocks.android.db.StocksDatabase.NOW;
import static de.njsm.stocks.android.db.entities.Sql.*;
import static de.njsm.stocks.android.util.Config.DATABASE_INFINITY_STRING;

@Dao
public abstract class FoodDao implements Inserter<Food> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Food[] food);

    @Transaction
    public void synchronise(Food[] food) {
        delete();
        insert(food);
    }

    @Query("select * " +
            "from Food " +
            "where _id = :id " +
            "and valid_time_start <= " + NOW +
            "and " + NOW + " < valid_time_end " +
            "and transaction_time_end = '" + DATABASE_INFINITY_STRING + "'")
    public abstract LiveData<Food> getFood(int id);

    @Query("select * " +
            "from Food " +
            "where valid_time_start <= " + NOW +
            "and " + NOW + " < valid_time_end " +
            "and transaction_time_end = '" + DATABASE_INFINITY_STRING + "' " +
            "order by name")
    public abstract LiveData<List<Food>> getAll();

    @Query("select " +
            FOOD_FIELDS +
            UNIT_FIELDS_QUALIFIED +
            SCALED_UNIT_FIELDS_QUALIFIED +
            "0 as amount " +
            "from food food " +
            SCALED_UNIT_JOIN_FOOD +
            UNIT_JOIN_SCALED_UNIT +
            "where food.valid_time_start <= " + NOW +
            "and " + NOW + " < food.valid_time_end " +
            "and food.transaction_time_end = '" + DATABASE_INFINITY_STRING + "' " +
            "and food._id not in (" +
                "select distinct of_type " +
                "from fooditem fooditem " +
                "where fooditem.valid_time_start <= " + NOW +
                "and " + NOW + " < fooditem.valid_time_end " +
                "and fooditem.transaction_time_end = '" + DATABASE_INFINITY_STRING + "') " +
            "order by food._id")
    public abstract LiveData<List<FoodSummaryView.SingleFoodSummaryView>> getEmptyFood();

    @Query("with least_eat_by_date as (" +
                "select i.of_type as of_type, i.eat_by as eatBy " +
                "from FoodItem i " +
                "where i.valid_time_start <= " + NOW +
                "and " + NOW + " < i.valid_time_end " +
                "and i.transaction_time_end = '" + DATABASE_INFINITY_STRING + "' " +
                "group by i.of_type " +
                "having i.eat_by = MIN(i.eat_by)), " +
            "scaled_amount as (" +
                "select " +
                Sql.UNIT_FIELDS_QUALIFIED +
                Sql.SCALED_UNIT_FIELDS_QUALIFIED +
                "fooditem.of_type as of_type, count(*) as amount " +
                "from fooditem fooditem " +
                Sql.SCALED_UNIT_JOIN_FOODITEM +
                Sql.UNIT_JOIN_SCALED_UNIT +
                "where fooditem.valid_time_start <= " + NOW +
                "and " + NOW + " < fooditem.valid_time_end " +
                "and fooditem.transaction_time_end = '" + DATABASE_INFINITY_STRING + "' " +
                "group by fooditem.of_type, fooditem.unit" +
            ") " +
            "select " +
            FOOD_FIELDS +
            "least_eat_by_date.eatBy as eatBy, scaled_amount.* " +
            "from food food " +
            "inner join least_eat_by_date on least_eat_by_date.of_type = food._id " +
            "inner join scaled_amount on scaled_amount.of_type = food._id " +
            "where food.valid_time_start <= " + NOW +
            "and " + NOW + " < food.valid_time_end " +
            "and food.transaction_time_end = '" + DATABASE_INFINITY_STRING + "' " +
            "order by eatBy, food._id")
    public abstract LiveData<List<FoodSummaryWithExpirationView.SingleFoodSummaryView>> getFoodToEatSummary();

    @Query("with least_eat_by_date as (" +
                "select fooditem.of_type as of_type, fooditem.eat_by as eatBy " +
                "from fooditem fooditem " +
                "where fooditem.valid_time_start <= " + NOW +
                "and fooditem.stored_in = :location " +
                "and " + NOW + " < fooditem.valid_time_end " +
                "and fooditem.transaction_time_end = '" + DATABASE_INFINITY_STRING + "' " +
                "group by fooditem.of_type " +
                "having fooditem.eat_by = MIN(fooditem.eat_by)), " +
            "scaled_amount as (" +
                "select " +
                Sql.UNIT_FIELDS_QUALIFIED +
                Sql.SCALED_UNIT_FIELDS_QUALIFIED +
                "fooditem.of_type as of_type, count(*) as amount " +
                "from fooditem fooditem " +
                Sql.SCALED_UNIT_JOIN_FOODITEM +
                Sql.UNIT_JOIN_SCALED_UNIT +
                "where fooditem.valid_time_start <= " + NOW +
                "and " + NOW + " < fooditem.valid_time_end " +
                "and fooditem.transaction_time_end = '" + DATABASE_INFINITY_STRING + "' " +
                "and fooditem.stored_in = :location " +
                "group by fooditem.of_type, fooditem.unit" +
            ") " +
            "select " +
            FOOD_FIELDS +
            "least_eat_by_date.eatBy as eatBy, scaled_amount.* " +
            "from food food " +
            "inner join least_eat_by_date on least_eat_by_date.of_type = food._id " +
            "inner join scaled_amount on scaled_amount.of_type = food._id " +
            "where food.valid_time_start <= " + NOW +
            "and " + NOW + " < food.valid_time_end " +
            "and food.transaction_time_end = '" + DATABASE_INFINITY_STRING + "' " +
            "order by eatBy, food._id")
    public abstract LiveData<List<FoodSummaryWithExpirationView.SingleFoodSummaryView>> getFoodByLocationSummary(int location);

    @Query("select f._id, f.version, f.initiates, f.name, f.to_buy, f.expiration_offset, f.location as location, f.description as description, f.valid_time_start, f.valid_time_end, f.transaction_time_start, f.transaction_time_end, f.store_unit " +
            "from Food f " +
            "inner join EanNumber n on n.identifies = f._id " +
            "where n.number = :s " +
            "and f.valid_time_start <= " + NOW +
            "and " + NOW + " < f.valid_time_end " +
            "and f.transaction_time_end = '" + DATABASE_INFINITY_STRING + "' " +
            "and n.valid_time_start <= " + NOW +
            "and " + NOW + " < n.valid_time_end " +
            "and n.transaction_time_end = '" + DATABASE_INFINITY_STRING + "' " +
            "limit 1")
    public abstract LiveData<Food> getFoodByEanNumber(String s);

    @Query(
        "with scaled_amount as (" +
            "select " +
            Sql.UNIT_FIELDS_QUALIFIED +
            Sql.SCALED_UNIT_FIELDS_QUALIFIED +
            "fooditem.of_type as of_type, count(*) as amount " +
            "from fooditem fooditem " +
            Sql.SCALED_UNIT_JOIN_FOODITEM +
            Sql.UNIT_JOIN_SCALED_UNIT +
            "where fooditem.valid_time_start <= " + NOW +
            "and " + NOW + " < fooditem.valid_time_end " +
            "and fooditem.transaction_time_end = '" + DATABASE_INFINITY_STRING + "' " +
            "group by fooditem.of_type, fooditem.unit" +
        ") " +
            "select " +
            FOOD_FIELDS +
            "scaled_amount.* " +
            "from food food " +
            "inner join scaled_amount on scaled_amount.of_type = food._id " +
            "where food.to_buy " +
            "and food.valid_time_start <= " + NOW +
            "and " + NOW + " < food.valid_time_end " +
            "and food.transaction_time_end = '" + DATABASE_INFINITY_STRING + "' " +
        "union all " +
            "select " +
            FOOD_FIELDS +
            UNIT_FIELDS_QUALIFIED +
            SCALED_UNIT_FIELDS_QUALIFIED +
            "food._id as of_type, 0 as amount " +
            "from food food " +
            SCALED_UNIT_JOIN_FOOD +
            UNIT_JOIN_SCALED_UNIT +
            "where food.to_buy " +
            "and food.valid_time_start <= " + NOW +
            "and " + NOW + " < food.valid_time_end " +
            "and food.transaction_time_end = '" + DATABASE_INFINITY_STRING + "' " +
            "and food._id not in (" +
                "select distinct of_type " +
                "from fooditem fooditem " +
                "where fooditem.valid_time_start <= " + NOW +
                "and " + NOW + " < fooditem.valid_time_end " +
                "and fooditem.transaction_time_end = '" + DATABASE_INFINITY_STRING + "') " +
        "order by name"
    )
    public abstract LiveData<List<FoodSummaryView.SingleFoodSummaryView>> getFoodToBuy();

    @Query(
        "with scaled_amount as (" +
            "select " +
            Sql.UNIT_FIELDS_QUALIFIED +
            Sql.SCALED_UNIT_FIELDS_QUALIFIED +
            "fooditem.of_type as of_type, count(*) as amount " +
            "from fooditem fooditem " +
            Sql.SCALED_UNIT_JOIN_FOODITEM +
            Sql.UNIT_JOIN_SCALED_UNIT +
            "where fooditem.valid_time_start <= " + NOW +
            "and " + NOW + " < fooditem.valid_time_end " +
            "and fooditem.transaction_time_end = '" + DATABASE_INFINITY_STRING + "' " +
            "group by fooditem.of_type, fooditem.unit" +
        ") " +
            "select " +
            FOOD_FIELDS +
            "scaled_amount.* " +
            "from food food " +
            "inner join scaled_amount on scaled_amount.of_type = food._id " +
            "where food.name like :searchTerm " +
            "and food.valid_time_start <= " + NOW +
            "and " + NOW + " < food.valid_time_end " +
            "and food.transaction_time_end = '" + DATABASE_INFINITY_STRING + "' " +
        "union all " +
            "select " +
            FOOD_FIELDS +
            UNIT_FIELDS_QUALIFIED +
            SCALED_UNIT_FIELDS_QUALIFIED +
            "food._id as of_type, 0 as amount " +
            "from food food " +
            SCALED_UNIT_JOIN_FOOD +
            UNIT_JOIN_SCALED_UNIT +
            "where food.name like :searchTerm " +
            "and food.valid_time_start <= " + NOW +
            "and " + NOW + " < food.valid_time_end " +
            "and food.transaction_time_end = '" + DATABASE_INFINITY_STRING + "' " +
            "and food._id not in (" +
                "select distinct of_type " +
                "from fooditem fooditem " +
                "where fooditem.valid_time_start <= " + NOW +
                "and " + NOW + " < fooditem.valid_time_end " +
                "and fooditem.transaction_time_end = '" + DATABASE_INFINITY_STRING + "') " +
        "order by name"
    )
    public abstract LiveData<List<FoodSummaryView.SingleFoodSummaryView>> getFoodBySubString(String searchTerm);

    @Query("delete from Food")
    abstract void delete();

    @Query("select * " +
            "from food " +
            "where _id = :id " +
            "and valid_time_start <= " + NOW +
            "and " + NOW + " < valid_time_end " +
            "and transaction_time_start <= :transactionTime " +
            "and :transactionTime < transaction_time_end")
    public abstract LiveData<Food> getFoodNowAsKnownBy(int id, Instant transactionTime);
}
