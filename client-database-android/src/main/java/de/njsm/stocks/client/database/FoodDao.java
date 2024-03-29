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
import androidx.room.Embedded;
import androidx.room.Query;
import de.njsm.stocks.client.business.entities.*;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;

import java.math.BigDecimal;
import java.util.List;

@Dao
abstract class FoodDao {

    @Query("select * " +
            "from current_food")
    abstract List<FoodDbEntity> getAll();

    @Query("select f.id, f.name, f.to_buy as toBuy, u.abbreviation as unitAbbreviation " +
            "from current_food f " +
            "join current_scaled_unit s on f.store_unit = s.id " +
            "join current_unit u on s.unit = u.id " +
            "where f.id not in (" +
            "   select of_type " +
            "   from current_food_item" +
            ") " +
            "order by f.name")
    abstract Observable<List<EmptyFoodRecord>> getCurrentEmptyFood();

    @Query("select * " +
            "from current_food " +
            "where id = :id")
    abstract FoodForDeletion getForDeletion(int id);

    @Query("select * " +
            "from current_food " +
            "where id = :id")
    abstract Observable<FoodDbEntity> getToEdit(int id);

    @Query("select * " +
            "from current_food " +
            "where id = :id")
    abstract Maybe<FoodDbEntity> getFoodForItemAdding(int id);

    class FoodWithLocation {
        @Embedded FoodDbEntity food;
        String locationName;
        int scaledUnitId;
        BigDecimal scale;
        String abbreviation;
    }

    @Query("select f.*, l.name as locationName, s.id as scaledUnitId, s.scale, u.abbreviation " +
            "from current_food f " +
            "join current_scaled_unit s on s.id = f.store_unit " +
            "join current_unit u on u.id = s.unit " +
            "left outer join current_location l on l.id = f.location " +
            "where f.id = :id")
    abstract Observable<FoodWithLocation> getDetails(int id);

    @Query("select * " +
            "from current_food " +
            "where id = :id")
    abstract FoodDbEntity getForEditing(int id);

    @Query("with least_eat_by_date as (" +
                "select of_type as of_type, min(eat_by) as eat_by " +
                "from current_food_item " +
                "where stored_in = :location " +
                "group by of_type" +
            ") " +
            "select f.id, f.name, f.to_buy as toBuy, d.eat_by as nextEatByDate " +
            "from current_food f " +
            "join least_eat_by_date d on f.id = d.of_type " +
            "where f.id in (select i.of_type " +
                "from current_food_item i " +
                "where i.stored_in = :location" +
            ")")
    public abstract Observable<List<FoodForListingBaseData>> getCurrentFoodBy(int location);

    @Query("select i.of_type as foodId, s.id as scaledUnitId, u.id as unitId, " +
            "count(1) as numberOfFoodItemsWithSameScaledUnit, s.scale as scale, u.abbreviation as abbreviation " +
            "from current_food_item i " +
            "join current_scaled_unit s on i.unit = s.id " +
            "join current_unit u on s.unit = u.id " +
            "where i.stored_in = :location " +
            "group by i.of_type, s.id, u.id, s.scale, u.abbreviation")
    public abstract Observable<List<StoredFoodAmount>> getAmountsStoredIn(int location);

    @Query("with least_eat_by_date as (" +
                "select of_type as of_type, min(eat_by) as eat_by " +
                "from current_food_item " +
                "group by of_type" +
            ") " +
            "select f.id, f.name, f.to_buy as toBuy, d.eat_by as nextEatByDate " +
            "from current_food f " +
            "join least_eat_by_date d on f.id = d.of_type " +
            "where f.id in (select i.of_type " +
                "from current_food_item i" +
            ")")
    public abstract Observable<List<FoodForListingBaseData>> getCurrentFood();

    @Query("select i.of_type as foodId, s.id as scaledUnitId, u.id as unitId, " +
            "count(1) as numberOfFoodItemsWithSameScaledUnit, s.scale as scale, u.abbreviation as abbreviation " +
            "from current_food_item i " +
            "join current_scaled_unit s on i.unit = s.id " +
            "join current_unit u on s.unit = u.id " +
            "group by i.of_type, s.id, u.id, s.scale, u.abbreviation")
    abstract Observable<List<StoredFoodAmount>> getAmounts();

    @Query("select i.of_type as foodId, s.id as scaledUnitId, u.id as unitId, " +
            "count(1) as numberOfFoodItemsWithSameScaledUnit, s.scale as scale, u.abbreviation as abbreviation " +
            "from current_food_item i " +
            "join current_scaled_unit s on i.unit = s.id " +
            "join current_unit u on s.unit = u.id " +
            "where i.of_type = :foodId " +
            "group by i.of_type, s.id, u.id, s.scale, u.abbreviation")
    abstract Observable<List<StoredFoodAmount>> getAmountsOf(int foodId);

    @Query("select i.id, s.scale as amount, unit.abbreviation, l.name as location, i.eat_by as eatBy, u.name as buyer, d.name as registerer " +
            "from current_food_item i " +
            "join current_user_device d on i.registers = d.id " +
            "join current_user u on d.belongs_to = u.id " +
            "join current_location l on i.stored_in = l.id " +
            "join current_scaled_unit s on i.unit = s.id " +
            "join current_unit unit on s.unit = unit.id " +
            "where of_type = :id " +
            "order by i.eat_by, i.id")
    abstract Observable<List<FoodItemForListingData>> get(int id);

    @Query("select id, name " +
            "from current_food " +
            "order by name")
    abstract Observable<List<FoodForSelection>> getForSelection();

    @Query("select id, version, to_buy as toBuy " +
            "from current_food " +
            "where id = :id")
    abstract FoodForBuying getCurrentShoppingState(int id);

    @Query("select id, name " +
            "from current_food " +
            "where to_buy " +
            "order by name, id")
    abstract Observable<List<FoodWithAmountForListingBaseData>> getCurrentFoodToBuy();

    @Query("select i.of_type as foodId, s.id as scaledUnitId, u.id as unitId, " +
            "count(1) as numberOfFoodItemsWithSameScaledUnit, s.scale as scale, u.abbreviation as abbreviation " +
            "from current_food_item i " +
            "join current_food f on i.of_type = f.id " +
            "join current_scaled_unit s on i.unit = s.id " +
            "join current_unit u on s.unit = u.id " +
            "where f.to_buy " +
            "group by i.of_type, s.id, u.id, s.scale, u.abbreviation")
    abstract Observable<List<StoredFoodAmount>> getFoodAmountsToBuy();

    @Query("select f.id as foodId, s.id as scaledUnitId, u.id as unitId, " +
            "0 as numberOfFoodItemsWithSameScaledUnit, s.scale as scale, u.abbreviation as abbreviation " +
            "from current_food f " +
            "join current_scaled_unit s on f.store_unit = s.id " +
            "join current_unit u on s.unit = u.id " +
            "where f.to_buy " +
            "and f.id not in (" +
                "select of_type " +
                "from current_food_item" +
            ")")
    abstract Observable<List<StoredFoodAmount>> getFoodAmountsOfAbsentFoodToBuy();

    @Query("select id, name " +
            "from current_food " +
            "order by name")
    abstract Observable<List<FoodForEanNumberAssignment>> getForEanNumberAssignment();
}
