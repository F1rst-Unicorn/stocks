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

package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.common.api.Recipe;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.impl.FullRecipeForDeletion;
import de.njsm.stocks.common.api.impl.FullRecipeForInsertion;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.db.RecipeHandler;
import de.njsm.stocks.server.v2.db.RecipeIngredientHandler;
import de.njsm.stocks.server.v2.db.RecipeProductHandler;
import de.njsm.stocks.server.v2.db.jooq.tables.records.RecipeRecord;

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

    @Override
    public StatusCode delete(FullRecipeForDeletion recipe) {
        return runOperation(
                () -> recipeIngredientHandler.areEntitiesComplete(recipe.recipe(), recipe.ingredients())
                        .bind(() -> recipeProductHandler.areEntitiesComplete(recipe.recipe(), recipe.products()))
                        .bind(() -> recipeIngredientHandler.deleteAllOf(recipe.recipe()))
                        .bind(() -> recipeProductHandler.deleteAllOf(recipe.recipe()))
                        .bind(() -> dbHandler.delete(recipe.recipe())));
    }

    public StatusCode add(FullRecipeForInsertion fullRecipeForInsertion) {
        return runOperation(() -> dbHandler.addReturningId(fullRecipeForInsertion.recipe())
                .map(recipeId -> fullRecipeForInsertion.ingredients().stream()
                        .reduce(StatusCode.SUCCESS,
                                (code, item) ->
                                        code.bind(() -> recipeIngredientHandler.add(item.withRecipe(recipeId))),
                                (x, y) -> x)
                        .bind(() -> fullRecipeForInsertion.products().stream()
                                .reduce(StatusCode.SUCCESS,
                                        (code, item) -> code.bind(() -> recipeProductHandler.add(item.withRecipe(recipeId))),
                                        (x,y) -> x))).toEither().right().orValue(() -> StatusCode.SUCCESS)
        );
    }

    @Override
    public void setPrincipals(Principals principals) {
        super.setPrincipals(principals);
        recipeIngredientHandler.setPrincipals(principals);
        recipeProductHandler.setPrincipals(principals);
    }
}
