/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2021  The stocks developers
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

package de.njsm.stocks.client.database;

import androidx.room.DatabaseView;
import com.google.auto.value.AutoValue;

import java.time.Instant;
import java.time.Period;

import static de.njsm.stocks.client.database.CurrentTable.NOW_AS_BEST_KNOWN;

@DatabaseView(viewName = CurrentFoodItemDbView.CURRENT_FOOD_ITEM_TABLE, value =
        "select * " +
        "from food_item " +
        NOW_AS_BEST_KNOWN)
@AutoValue
abstract class CurrentFoodItemDbView implements IdFields, BitemporalFields, FoodItemFields {

    static final String CURRENT_FOOD_ITEM_TABLE = "current_food_item";

    static CurrentFoodItemDbView create(int id,
                                        int version,
                                        Instant validTimeStart,
                                        Instant validTimeEnd,
                                        Instant transactionTimeStart,
                                        Instant transactionTimeEnd,
                                        int initiates,
                                        Instant eatBy,
                                        int ofType,
                                        int storedIn,
                                        int buys,
                                        int registers,
                                        int unit) {
        return new AutoValue_CurrentFoodItemDbView(
                id,
                version,
                validTimeStart,
                validTimeEnd,
                transactionTimeStart,
                transactionTimeEnd,
                initiates,
                eatBy,
                ofType,
                storedIn,
                buys,
                registers,
                unit
        );
    }
}
