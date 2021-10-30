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

package de.njsm.stocks.android.db.dbview;

import androidx.room.ColumnInfo;
import androidx.room.DatabaseView;

import static de.njsm.stocks.android.db.dbview.RecipeStockRatingBase.QUERY;

@DatabaseView(viewName = RecipeStockRatingBase.RECIPE_STOCK_RATING_BASE_TABLE, value = QUERY)
public class RecipeStockRatingBase {

    public static final String RECIPE_STOCK_RATING_BASE_TABLE = "recipe_stock_rating_base";

    public static final String RECIPE_STOCK_RATING_BASE_PREFIX = RECIPE_STOCK_RATING_BASE_TABLE + "_";

    public static final String QUERY = "select " +
            "recipe_id, " +
            "ingredient, " +
            "required_amount, " +
            "min(cast(1 as numeric), max(present_amount)) as present_for_necessary, " +
            "case when max(present_amount_scaled) >= required_amount then 1 else 0 end present_for_sufficient " +
            "from current_scaled_ingredient_amount_and_stock " +
            "where required_amount > 0 " +
            "group by recipe_id, ingredient, required_amount, required_unit";

    public static final String SCALED_AMOUNT_FIELDS_QUALIFIED =
            RECIPE_STOCK_RATING_BASE_TABLE + ".recipe_id as " + RECIPE_STOCK_RATING_BASE_PREFIX + "recipe_id, " +
                    RECIPE_STOCK_RATING_BASE_TABLE + ".ingredient as " + RECIPE_STOCK_RATING_BASE_PREFIX + "ingredient, " +
                    RECIPE_STOCK_RATING_BASE_TABLE + ".required_amount as " + RECIPE_STOCK_RATING_BASE_PREFIX + "required_amount, " +
                    RECIPE_STOCK_RATING_BASE_TABLE + ".present_for_necessary as " + RECIPE_STOCK_RATING_BASE_PREFIX + "present_for_necessary, " +
                    RECIPE_STOCK_RATING_BASE_TABLE + ".present_for_sufficient as " + RECIPE_STOCK_RATING_BASE_PREFIX + "present_for_sufficient, ";

    @ColumnInfo(name = "recipe_id")
    private final int recipeId;

    @ColumnInfo(name = "ingredient")
    private final int ingredient;

    @ColumnInfo(name = "required_amount")
    private final int requiredAmount;

    @ColumnInfo(name = "present_for_necessary")
    private final int presentForNecessary;

    @ColumnInfo(name = "present_for_sufficient")
    private final int presentForSufficient;

    public RecipeStockRatingBase(int recipeId, int ingredient, int requiredAmount, int presentForNecessary, int presentForSufficient) {
        this.recipeId = recipeId;
        this.ingredient = ingredient;
        this.requiredAmount = requiredAmount;
        this.presentForNecessary = presentForNecessary;
        this.presentForSufficient = presentForSufficient;
    }
}
