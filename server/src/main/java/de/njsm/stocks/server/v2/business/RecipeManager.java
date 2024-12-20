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

package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.common.api.*;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.db.RecipeHandler;
import de.njsm.stocks.server.v2.db.RecipeIngredientHandler;
import de.njsm.stocks.server.v2.db.RecipeProductHandler;
import de.njsm.stocks.server.v2.db.jooq.tables.records.RecipeRecord;
import fj.data.Validation;

public class RecipeManager extends BusinessObject<RecipeRecord, Recipe>
        implements BusinessGettable<RecipeRecord, Recipe>,
                   BusinessDeletable<FullRecipeForDeletion, Recipe> {

    private final RecipeHandler dbHandler;

    private final RecipeIngredientHandler recipeIngredientHandler;

    private final RecipeProductHandler recipeProductHandler;

    public RecipeManager(RecipeHandler dbHandler, RecipeIngredientHandler ingredientHandler, RecipeProductHandler recipeProductHandler) {
        super(dbHandler);
        this.dbHandler = dbHandler;
        recipeIngredientHandler = ingredientHandler;
        this.recipeProductHandler = recipeProductHandler;
    }

    public Validation<StatusCode, Integer> add(FullRecipeForInsertion fullRecipeForInsertion) {
        return runFunction(() -> {
                    Validation<StatusCode, Integer> id = dbHandler.addReturningId(fullRecipeForInsertion.recipe());
                    if (id.isFail())
                        return id;
                    StatusCode ingredientResult = fullRecipeForInsertion.ingredients().stream()
                                    .reduce(StatusCode.SUCCESS,
                                            (code, item) ->
                                                    code.bind(() -> recipeIngredientHandler.add(item.withRecipe(id.success()))),
                                            (x, y) -> x);
                    if (ingredientResult.isFail())
                        return Validation.fail(ingredientResult);
                    var productResult = fullRecipeForInsertion.products().stream()
                                            .reduce(StatusCode.SUCCESS,
                                                    (code, item) -> code.bind(() -> recipeProductHandler.add(item.withRecipe(id.success()))),
                                                    (x, y) -> x);
                    if (productResult.isFail())
                        return Validation.fail(productResult);
                    return id;
                }
        );
    }

    public StatusCode edit(FullRecipeForEditing input) {
        return runOperation(() ->
                recipeIngredientHandler.areEntitiesComplete(input.recipe(), input.existingIngredients())
                        .bind(() -> recipeProductHandler.areEntitiesComplete(input.recipe(), input.existingProducts()))
                        .bind(() -> input.ingredients().stream()
                                .reduce(StatusCode.SUCCESS,
                                        (code, item) ->
                                                code.bind(() -> recipeIngredientHandler.edit(item)),
                                        (x,y) -> x))
                        .bind(() -> input.ingredientsToDelete().stream()
                                .reduce(StatusCode.SUCCESS,
                                        (code, item) ->
                                                code.bind(() -> recipeIngredientHandler.delete(item)),
                                        (x,y) -> x))
                        .bind(() -> input.ingredientsToInsert().stream()
                                .reduce(StatusCode.SUCCESS,
                                        (code, item) ->
                                                code.bind(() -> recipeIngredientHandler.add(item.withRecipe(input.recipe().id()))),
                                        (x,y) -> x))
                        .bind(() -> input.products().stream()
                                .reduce(StatusCode.SUCCESS,
                                        (code, item) ->
                                                code.bind(() -> recipeProductHandler.edit(item)),
                                        (x,y) -> x))
                        .bind(() -> input.productsToDelete().stream()
                                .reduce(StatusCode.SUCCESS,
                                        (code, item) ->
                                                code.bind(() -> recipeProductHandler.delete(item)),
                                        (x,y) -> x))
                        .bind(() -> input.productsToInsert().stream()
                                .reduce(StatusCode.SUCCESS,
                                        (code, item) ->
                                                code.bind(() -> recipeProductHandler.add(item.withRecipe(input.recipe().id()))),
                                        (x,y) -> x))
                        .bind(() -> dbHandler.edit(input.recipe()))
        );
    }

    @Override
    public StatusCode delete(FullRecipeForDeletion recipe) {
        return runOperation(() ->
                recipeIngredientHandler.areEntitiesComplete(recipe.recipe(), recipe.ingredients())
                        .bind(() -> recipeProductHandler.areEntitiesComplete(recipe.recipe(), recipe.products()))
                        .bind(() -> recipeIngredientHandler.deleteAllOf(recipe.recipe()))
                        .bind(() -> recipeProductHandler.deleteAllOf(recipe.recipe()))
                        .bind(() -> dbHandler.delete(recipe.recipe()))
        );
    }

    @Override
    public void setPrincipals(Principals principals) {
        super.setPrincipals(principals);
        recipeIngredientHandler.setPrincipals(principals);
        recipeProductHandler.setPrincipals(principals);
    }
}
