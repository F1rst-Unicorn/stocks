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

import static de.njsm.stocks.android.db.dbview.RecipeIngredientAmountAndStock.QUERY;

@DatabaseView(viewName = RecipeIngredientAmountAndStock.RECIPE_INGREDIENT_AMOUNT_AND_STOCK_TABLE, value = QUERY)
public class RecipeIngredientAmountAndStock {

    public static final String RECIPE_INGREDIENT_AMOUNT_AND_STOCK_TABLE = "current_scaled_ingredient_amount_and_stock";

    public static final String RECIPE_INGREDIENT_AMOUNT_AND_STOCK_PREFIX = RECIPE_INGREDIENT_AMOUNT_AND_STOCK_TABLE + "_";

    public static final String QUERY ="select " +
            "recipe._id as recipe_id, " +
            "recipe_ingredient.ingredient as ingredient, " +
            "recipe_ingredient.amount as required_amount, " +
            "recipe_ingredient.unit as required_unit, " +
            "current_scaled_amount.amount as present_amount, " +
            "current_scaled_amount.scaled_unit__id as present_unit, " +
            "cast(current_scaled_amount.amount as numeric) * coalesce((" +
                    "select factor " +
                    "from current_scaled_unit_conversion " +
                    "where source_id = current_scaled_amount.scaled_unit__id " +
                    "and target_id = recipe_ingredient.unit)," +
            " 0) as present_amount_scaled " +
            "from current_recipe recipe " +
            "join current_recipe_ingredient recipe_ingredient on recipe_ingredient.recipe = recipe._id " +
            "join current_scaled_amount current_scaled_amount on current_scaled_amount.of_type = recipe_ingredient.ingredient";

    public static final String SCALED_AMOUNT_FIELDS_QUALIFIED =
            RECIPE_INGREDIENT_AMOUNT_AND_STOCK_TABLE + ".recipe_id as " + RECIPE_INGREDIENT_AMOUNT_AND_STOCK_PREFIX + "recipe_id, " +
            RECIPE_INGREDIENT_AMOUNT_AND_STOCK_TABLE + ".ingredient as " + RECIPE_INGREDIENT_AMOUNT_AND_STOCK_PREFIX + "ingredient, " +
            RECIPE_INGREDIENT_AMOUNT_AND_STOCK_TABLE + ".required_amount as " + RECIPE_INGREDIENT_AMOUNT_AND_STOCK_PREFIX + "required_amount, " +
            RECIPE_INGREDIENT_AMOUNT_AND_STOCK_TABLE + ".required_unit as " + RECIPE_INGREDIENT_AMOUNT_AND_STOCK_PREFIX + "required_unit, " +
            RECIPE_INGREDIENT_AMOUNT_AND_STOCK_TABLE + ".present_amount as " + RECIPE_INGREDIENT_AMOUNT_AND_STOCK_PREFIX + "present_amount, " +
            RECIPE_INGREDIENT_AMOUNT_AND_STOCK_TABLE + ".present_unit as " + RECIPE_INGREDIENT_AMOUNT_AND_STOCK_PREFIX + "present_unit, " +
            RECIPE_INGREDIENT_AMOUNT_AND_STOCK_TABLE + ".present_amount_scaled as " + RECIPE_INGREDIENT_AMOUNT_AND_STOCK_PREFIX + "present_amount_scaled, ";

    @ColumnInfo(name = "recipe_id")
    private final int recipeId;

    @ColumnInfo(name = "ingredient")
    private final int ingredient;

    @ColumnInfo(name = "required_amount")
    private final int requiredAmount;

    @ColumnInfo(name = "required_unit")
    private final int requiredUnit;

    @ColumnInfo(name = "present_amount")
    private final int presentAmount;

    @ColumnInfo(name = "present_unit")
    private final int presentUnit;

    @ColumnInfo(name = "present_amount_scaled")
    private final int presentAmountScaled;

    public RecipeIngredientAmountAndStock(int recipeId, int ingredient, int requiredAmount, int requiredUnit, int presentAmount, int presentUnit, int presentAmountScaled) {
        this.recipeId = recipeId;
        this.ingredient = ingredient;
        this.requiredAmount = requiredAmount;
        this.requiredUnit = requiredUnit;
        this.presentAmount = presentAmount;
        this.presentUnit = presentUnit;
        this.presentAmountScaled = presentAmountScaled;
    }
}
