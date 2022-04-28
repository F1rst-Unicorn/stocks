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

import static de.njsm.stocks.client.database.CurrentTable.NOW_AS_BEST_KNOWN;

@DatabaseView(viewName = CurrentRecipeIngredientDbView.CURRENT_RECIPE_INGREDIENT_TABLE, value =
        "select * " +
        "from recipe_ingredient " +
        NOW_AS_BEST_KNOWN)
@AutoValue
abstract class CurrentRecipeIngredientDbView implements IdFields, BitemporalFields, RecipeIngredientFields {

    static final String CURRENT_RECIPE_INGREDIENT_TABLE = "current_recipe_ingredient";

    static CurrentRecipeIngredientDbView create(int id,
                                                int version,
                                                Instant validTimeStart,
                                                Instant validTimeEnd,
                                                Instant transactionTimeStart,
                                                Instant transactionTimeEnd,
                                                int initiates,
                                                int amount,
                                                int ingredient,
                                                int unit,
                                                int recipe) {
        return new AutoValue_CurrentRecipeIngredientDbView(
                id,
                version,
                validTimeStart,
                validTimeEnd,
                transactionTimeStart,
                transactionTimeEnd,
                initiates,
                amount,
                ingredient,
                unit,
                recipe
        );
    }
}
